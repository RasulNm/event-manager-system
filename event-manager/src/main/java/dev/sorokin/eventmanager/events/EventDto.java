package dev.sorokin.eventmanager.events;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.*;

import java.time.LocalDateTime;

public record EventDto(
        @Null
        Long id,
        @NotBlank
        String name,
        @Null
        Long ownerId,
        @NotNull
        @Positive(message = "Duration must be greater than 0")
        Integer maxPlaces,
        @Null
        Integer occupiedPlaces,
        @NotNull
        @Future(message = "Event date must be in the future")
        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
        LocalDateTime date,
        @NotNull
        @Min(value = 1, message = "Cost must be at least 1")
        Integer cost,
        @NotNull
        @Positive(message = "Duration must be greater than 0")
        Integer duration,
        @NotNull
        Long locationId,
        @Null
        EventStatus status
) {
}