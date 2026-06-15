package dev.sorokin.eventmanager.users;

import jakarta.persistence.EntityNotFoundException;
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

    public User registerUser(SignUpRequest signUpRequest) {
        if (userRepository.existsByLogin(signUpRequest.login())) {
            throw new IllegalArgumentException("Username '" + signUpRequest.login() + "' already taken");
        }

        var hashedPassword = passwordEncoder.encode(signUpRequest.password());

        var userToSave = new UserEntity(
                null,
                signUpRequest.login(),
                hashedPassword,
                signUpRequest.age(),
                UserRole.USER.name()
        );

        var saved = userRepository.save(userToSave);

        return mapToDomain(saved);
    }

    public User findByLogin(String loginFromToken) {
        var user = userRepository.findByLogin(loginFromToken)
                .orElseThrow(() -> new EntityNotFoundException("User " + loginFromToken + " not found"));
        return mapToDomain(user);
    }

    public User findById(Long userId) {
        var user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User with id:" + userId + " not found"));
        return mapToDomain(user);
    }

    private static User mapToDomain(UserEntity entity) {
        return new User(
                entity.getId(),
                entity.getLogin(),
                entity.getAge(),
                UserRole.valueOf(entity.getRole())
        );
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