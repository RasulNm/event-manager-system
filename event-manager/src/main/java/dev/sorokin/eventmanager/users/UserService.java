package dev.sorokin.eventmanager.users;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder
    ) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public void createAdmin(String login, String password) {
        if (userRepository.existsByLogin(login)) {
            return;
        }
        var admin = new UserEntity(
                null,
                login,
                passwordEncoder.encode(password),
                20,
                UserRole.ADMIN.name()
        );
        userRepository.save(admin);
    }
}