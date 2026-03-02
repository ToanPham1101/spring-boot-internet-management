package internet.management.security;

import internet.management.entity.UserEntity;
import internet.management.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
public class PasswordMigrationTask {

    private static final Logger log = LoggerFactory.getLogger(PasswordMigrationTask.class);

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public PasswordMigrationTask(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @EventListener(ApplicationReadyEvent.class)
    @Transactional
    public void hashPlainTextPasswords() {
        List<UserEntity> users = userRepository.findAll();
        int migrated = 0;

        for (UserEntity user : users) {
            if (user.getPassword() != null && !user.getPassword().startsWith("$2")) {
                user.setPassword(passwordEncoder.encode(user.getPassword()));
                userRepository.save(user);
                migrated++;
            }
        }

        if (migrated > 0) {
            log.info("Migrated user password(s) from plain-text to BCrypt", migrated);
        } else {
            log.info("All user passwords are already hashed");
        }
    }
}

