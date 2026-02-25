package item.controller;

import item.model.CreateUserCommand;
import item.model.DepositCommand;
import item.model.UserResult;
import item.service.UserService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/user")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping
    public UserResult createUser(@RequestBody CreateUserCommand command) {
        return userService.createUser(command);
    }

    @GetMapping
    public List<UserResult> getAllUsers() {
        return userService.getAllUsers();
    }

    @GetMapping("/{id}")
    public UserResult getUser(@PathVariable Integer id) {
        return userService.getUser(id);
    }

    @GetMapping("/username/{username}")
    public UserResult getUserByUsername(@PathVariable String username) {
        return userService.getUserByUsername(username);
    }

    @PostMapping("/deposit")
    public UserResult deposit(@RequestBody DepositCommand command) {
        return userService.deposit(command);
    }

    @PutMapping("/{id}/category")
    public UserResult changeCategory(@PathVariable Integer id,
                                     @RequestParam String category) {
        return userService.changeCategory(id, category);
    }

    @GetMapping("/{id}/transactions")
    public List<UserResult.TransactionResult> getTransactions(@PathVariable Integer id) {
        return userService.getTransactions(id);
    }
}

