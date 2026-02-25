package item.model;

import lombok.Data;

@Data
public class CreateUserCommand {
    private String username;
    private String fullName;
    private String password;
    private String category; // NORMAL, VIP, VVIP
}

