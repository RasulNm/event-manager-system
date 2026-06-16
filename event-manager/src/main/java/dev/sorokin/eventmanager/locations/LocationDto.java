package dev.sorokin.eventmanager.locations;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Null;

public record LocationDto(
        @Null
        Long id,
        @NotBlank
        String name,
        @NotBlank
        String address,
        @NotNull
        @Min(5)
        Integer capacity,
        String description
) {
}