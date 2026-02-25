package internet.management.service;

import internet.management.entity.SessionEntity;
import internet.management.entity.UserBalanceTransactionEntity;
import internet.management.entity.UserEntity;
import internet.management.model.SessionResult;
import internet.management.model.SessionStatus;
import internet.management.model.TransactionType;
import internet.management.repository.SessionRepository;
import internet.management.repository.UserBalanceTransactionRepository;
import internet.management.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class SessionService {

    private final SessionRepository sessionRepository;
    private final UserRepository userRepository;
    private final UserBalanceTransactionRepository transactionRepository;

    public SessionService(SessionRepository sessionRepository,
                          UserRepository userRepository,
                          UserBalanceTransactionRepository transactionRepository) {
        this.sessionRepository = sessionRepository;
        this.userRepository = userRepository;
        this.transactionRepository = transactionRepository;
    }

    @Transactional
    public SessionResult startSession(Integer userId) {
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found: " + userId));

        sessionRepository.findByUserIdAndStatus(userId, SessionStatus.ACTIVE.getValue())
                .ifPresent(s -> {
                    throw new RuntimeException("User already has an active session (id=" + s.getId() + ")");
                });

        if (user.getBalance() <= 0) {
            throw new RuntimeException("Insufficient balance. Please deposit first. Current: "
                    + user.getBalance() + " VND");
        }

        SessionEntity session = new SessionEntity();
        session.setUserId(userId);
        session.setCategory(user.getCategory());
        session.setStartTime(LocalDateTime.now());
        session.setPricePerHour(user.getCategory().getPricePerHour());
        session.setStatus(SessionStatus.ACTIVE.getValue());
        session = sessionRepository.save(session);

        return toResult(session, user.getUsername());
    }

    @Transactional
    public SessionResult endSession(Integer userId) {
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found: " + userId));

        SessionEntity session = sessionRepository.findByUserIdAndStatus(userId, SessionStatus.ACTIVE.getValue())
                .orElseThrow(() -> new RuntimeException("No active session for user: " + userId));

        session.setEndTime(LocalDateTime.now());
        session.setStatus(SessionStatus.EXPIRED.getValue());

        long usedMinutes = Duration.between(session.getStartTime(), session.getEndTime()).toMinutes();
        if (usedMinutes < 1) usedMinutes = 1;
        int cost = (int) ((usedMinutes * session.getPricePerHour()) / 60);

        user.setBalance(Math.max(0, user.getBalance() - cost));
        userRepository.save(user);

        UserBalanceTransactionEntity tx = new UserBalanceTransactionEntity();
        tx.setUserId(userId);
        tx.setAmount(-cost);
        tx.setType(TransactionType.SESSION_PAYMENT.getValue());
        tx.setDescription("Session #" + session.getId() + " - " + usedMinutes + " min @ "
                + session.getPricePerHour() + " VND/h (" + session.getCategory().getName() + ")");
        tx.setCreatedAt(LocalDateTime.now());
        transactionRepository.save(tx);

        session = sessionRepository.save(session);
        return toResult(session, user.getUsername());
    }

    public SessionResult.TimeRemainingResult getTimeRemaining(Integer userId) {
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found: " + userId));

        SessionResult.TimeRemainingResult result = new SessionResult.TimeRemainingResult();
        result.setUserId(user.getId());
        result.setUsername(user.getUsername());
        result.setCategory(user.getCategory().getName());
        result.setBalance(user.getBalance());
        result.setPricePerHour(user.getCategory().getPricePerHour());

        int pricePerHour = user.getCategory().getPricePerHour();
        int effectiveBalance = user.getBalance();

        Optional<SessionEntity> activeSession = sessionRepository
                .findByUserIdAndStatus(userId, SessionStatus.ACTIVE.getValue());

        if (activeSession.isPresent()) {
            SessionEntity session = activeSession.get();
            result.setHasActiveSession(true);
            result.setSessionStartTime(session.getStartTime());
            long usedMinutes = Duration.between(session.getStartTime(), LocalDateTime.now()).toMinutes();
            result.setSessionUsedMinutes(usedMinutes);
            int elapsedCost = (int) ((usedMinutes * session.getPricePerHour()) / 60);
            effectiveBalance = Math.max(0, effectiveBalance - elapsedCost);
        } else {
            result.setHasActiveSession(false);
        }

        if (pricePerHour > 0 && effectiveBalance > 0) {
            int totalMinutes = (effectiveBalance * 60) / pricePerHour;
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

    public List<SessionResult> getSessionHistory(Integer userId) {
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found: " + userId));
        return sessionRepository.findByUserIdOrderByStartTimeDesc(userId).stream()
                .map(s -> toResult(s, user.getUsername()))
                .collect(Collectors.toList());
    }

    private SessionResult toResult(SessionEntity session, String username) {
        SessionResult r = new SessionResult();
        r.setId(session.getId());
        r.setUserId(session.getUserId());
        r.setUsername(username);
        r.setCategory(session.getCategory().getName());
        r.setPricePerHour(session.getPricePerHour());
        r.setStartTime(session.getStartTime());
        r.setEndTime(session.getEndTime());
        r.setStatus(SessionStatus.nameOf(session.getStatus()));

        if (session.getEndTime() != null) {
            long mins = Duration.between(session.getStartTime(), session.getEndTime()).toMinutes();
            r.setUsedMinutes(mins);
            r.setCost((int) ((mins * session.getPricePerHour()) / 60));
        } else if (session.getStatus() == SessionStatus.ACTIVE.getValue()) {
            long mins = Duration.between(session.getStartTime(), LocalDateTime.now()).toMinutes();
            r.setUsedMinutes(mins);
            r.setCost((int) ((mins * session.getPricePerHour()) / 60));
        }

        return r;
    }
}

