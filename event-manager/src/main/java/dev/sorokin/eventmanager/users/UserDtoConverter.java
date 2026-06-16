package dev.sorokin.eventmanager.users;

import org.springframework.stereotype.Component;

@Component
public class UserDtoConverter {

    public User toDomain(UserDto user) {
        return new User(
                user.id(),
                user.login(),
                user.age(),
                user.role()
        );
    }

    public UserDto toDto(User user) {
        return new UserDto(
                user.id(),
                user.login(),
                user.age(),
                user.role()
        );
    }
}