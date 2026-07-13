package dev.sorokin.eventmanager.events;

import java.time.LocalDateTime;

public record Event(
        Long id,
        String name,
        Long ownerId, // id пользователя-создателя мероприятия
        Integer maxPlaces,
        Integer occupiedPlaces, // Кол-во уже занятых мест (создатель не учитывается при подсчете)
        LocalDateTime date,
        Integer cost,
        Integer duration, // Длительность в минутах
        Long locationId, // Идентификатор локации, где проходит мероприятие
        EventStatus status
) {
}
