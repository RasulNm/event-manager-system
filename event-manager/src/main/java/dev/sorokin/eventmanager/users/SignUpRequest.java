package dev.sorokin.eventmanager.users;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record SignUpRequest(
        @NotBlank(message = "Login cannot be blank")
        String login,
        @NotBlank(message = "Password cannot be blank")
        String password,
        @NotNull(message = "Age cannot be null")
        @Min(value = 18, message = "Age must be at least 18")
        Integer age
) {
}