package dev.sorokin.eventmanager.users;

public record User(
        Long id,
        String login,
        Integer age,
        UserRole role
) {
}