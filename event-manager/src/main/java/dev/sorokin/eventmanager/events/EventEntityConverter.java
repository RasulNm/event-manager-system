package dev.sorokin.eventmanager.events;

import org.springframework.stereotype.Component;

@Component
public class EventEntityConverter {

    public Event toDomain(EventEntity entity) {
        return new Event(
                entity.getId(),
                entity.getName(),
                entity.getOwnerId(),
                entity.getMaxPlaces(),
                entity.getOccupiedPlaces(),
                entity.getDate(),
                entity.getCost(),
                entity.getDuration(),
                entity.getLocationId(),
                EventStatus.valueOf(entity.getStatus())
        );
    }

    public EventEntity toEntity(Event event) {
        return new EventEntity(
                event.id(),
                event.name(),
                event.ownerId(),
                event.maxPlaces(),
                event.occupiedPlaces(),
                event.date(),
                event.cost(),
                event.duration(),
                event.locationId(),
                event.status() != null ? event.status().name() : EventStatus.WAIT_START.name()
        );
    }
}