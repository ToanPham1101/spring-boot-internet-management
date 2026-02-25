package item.service;

import item.entity.CategoryEntity;
import item.entity.UserBalanceTransactionEntity;
import item.entity.UserEntity;
import item.model.CreateUserCommand;
import item.model.DepositCommand;
import item.model.TransactionType;
import item.model.UserResult;
import item.repository.CategoryRepository;
import item.repository.UserBalanceTransactionRepository;
import item.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final UserBalanceTransactionRepository transactionRepository;

    public UserService(UserRepository userRepository,
                       CategoryRepository categoryRepository,
                       UserBalanceTransactionRepository transactionRepository) {
        this.userRepository = userRepository;
        this.categoryRepository = categoryRepository;
        this.transactionRepository = transactionRepository;
    }

    @Transactional
    public UserResult createUser(CreateUserCommand command) {
        if (userRepository.existsByUsername(command.getUsername())) {
            throw new RuntimeException("Username '" + command.getUsername() + "' already exists");
        }

        CategoryEntity category = categoryRepository.findByName(command.getCategory().toUpperCase())
                .orElseThrow(() -> new RuntimeException("Category not found: " + command.getCategory()
                        + ". Valid: NORMAL, VIP, VVIP"));

        UserEntity user = new UserEntity();
        user.setUsername(command.getUsername());
        user.setFullName(command.getFullName());
        user.setPassword(command.getPassword());
        user.setBalance(0);
        user.setCategory(category);
        user.setCreatedAt(LocalDateTime.now());
        user = userRepository.save(user);

        return toResult(user);
    }

    @Transactional
    public UserResult deposit(DepositCommand command) {
        UserEntity user = userRepository.findById(command.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found: " + command.getUserId()));

        if (command.getAmount() == null || command.getAmount() <= 0) {
            throw new RuntimeException("Deposit amount must be greater than 0");
        }

        user.setBalance(user.getBalance() + command.getAmount());
        userRepository.save(user);

        // Record transaction
        UserBalanceTransactionEntity tx = new UserBalanceTransactionEntity();
        tx.setUserId(user.getId());
        tx.setAmount(command.getAmount());
        tx.setType(TransactionType.DEPOSIT.getValue());
        tx.setDescription(command.getDescription() != null ? command.getDescription() : "Deposit");
        tx.setCreatedAt(LocalDateTime.now());
        transactionRepository.save(tx);

        return toResult(user);
    }

    public UserResult getUser(Integer userId) {
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found: " + userId));
        return toResult(user);
    }

    public UserResult getUserByUsername(String username) {
        UserEntity user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found: " + username));
        return toResult(user);
    }

    public List<UserResult> getAllUsers() {
        return userRepository.findAll().stream()
                .map(this::toResult)
                .collect(Collectors.toList());
    }

    @Transactional
    public UserResult changeCategory(Integer userId, String categoryName) {
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found: " + userId));

        CategoryEntity category = categoryRepository.findByName(categoryName.toUpperCase())
                .orElseThrow(() -> new RuntimeException("Category not found: " + categoryName
                        + ". Valid: NORMAL, VIP, VVIP"));

        user.setCategory(category);
        userRepository.save(user);

        return toResult(user);
    }

    public List<UserResult.TransactionResult> getTransactions(Integer userId) {
        if (!userRepository.existsById(userId)) {
            throw new RuntimeException("User not found: " + userId);
        }
        return transactionRepository.findByUserIdOrderByCreatedAtDesc(userId).stream()
                .map(tx -> {
                    UserResult.TransactionResult r = new UserResult.TransactionResult();
                    r.setId(tx.getId());
                    r.setAmount(tx.getAmount());
                    r.setType(TransactionType.nameOf(tx.getType()));
                    r.setDescription(tx.getDescription());
                    r.setCreatedAt(tx.getCreatedAt());
                    return r;
                })
                .collect(Collectors.toList());
    }

    private UserResult toResult(UserEntity user) {
        UserResult result = new UserResult();
        result.setId(user.getId());
        result.setUsername(user.getUsername());
        result.setFullName(user.getFullName());
        result.setBalance(user.getBalance());
        result.setCategory(user.getCategory().getName());
        result.setPricePerHour(user.getCategory().getPricePerHour());
        result.setCreatedAt(user.getCreatedAt());

        // Calculate remaining time
        int pricePerHour = user.getCategory().getPricePerHour();
        int balance = user.getBalance();
        if (pricePerHour > 0 && balance > 0) {
            int totalMinutes = (balance * 60) / pricePerHour;
            result.setRemainingHours(totalMinutes / 60);
            result.setRemainingMinutes(totalMinutes % 60);
            result.setRemainingTimeFormatted(
                    String.format("%dh %dm", totalMinutes / 60, totalMinutes % 60));
        } else {
            result.setRemainingHours(0);
            result.setRemainingMinutes(0);
            result.setRemainingTimeFormatted("0h 0m");
        }

        return result;
    }
}

