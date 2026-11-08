package dev.sorokin.eventmanager.events;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDateTime;

public record EventSearchRequestDto(
        String name,
        Integer placesMin,
        Integer placesMax,
        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
        LocalDateTime dateStartAfter,
        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
        LocalDateTime dateStartBefore,
        Integer costMin,
        Integer costMax,
        Integer durationMin,
        Integer durationMax,
        Long locationId,
        EventStatus eventStatus
) {
}
