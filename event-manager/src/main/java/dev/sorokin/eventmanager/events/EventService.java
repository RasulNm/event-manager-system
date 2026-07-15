package dev.sorokin.eventmanager.events;

import dev.sorokin.eventmanager.locations.LocationEntity;
import dev.sorokin.eventmanager.locations.LocationRepository;
import dev.sorokin.eventmanager.users.User;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class EventService {

    private final EventRepository eventRepository;
    private final EventEntityConverter entityConverter;
    private final LocationRepository locationRepository;

    public EventService(
            EventRepository eventRepository,
            EventEntityConverter entityConverter,
            LocationRepository locationRepository
    ) {
        this.eventRepository = eventRepository;
        this.entityConverter = entityConverter;
        this.locationRepository = locationRepository;
    }

    @Transactional
    public Event createEvent(Event eventToCreate) {
        if (eventToCreate.id() != null) {
            throw new IllegalArgumentException("Event ID must be null for creation");
        }

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        Long currentUserId = null;
        if (auth != null && auth.getPrincipal() instanceof User user) {
            currentUserId = user.id(); // Получаем id создателя из JWT-токена
        }

        validateLocationCapacity(eventToCreate);

        Event eventWithOwner = new Event(
                null,
                eventToCreate.name(),
                currentUserId,
                eventToCreate.maxPlaces(),
                0,
                eventToCreate.date(),
                eventToCreate.cost(),
                eventToCreate.duration(),
                eventToCreate.locationId(),
                eventToCreate.status()
        );

        var eventEntity = entityConverter.toEntity(eventWithOwner);
        return entityConverter.toDomain(eventRepository.save(eventEntity));
    }

    @Transactional
    public void deleteEvent(Long eventId) {
        EventEntity event = eventRepository.findById(eventId)
                .orElseThrow(() -> throwEventNotFoundException(eventId));

        if (event.getDate().isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("Cannot cancel an event that has already started");
        }

        if (!event.getStatus().equals(EventStatus.CANCELLED.name())) {
            event.setStatus(EventStatus.CANCELLED.name());
            eventRepository.save(event);
        } else {
            throw new IllegalArgumentException("Cannot cancel the event because its current status is: %s"
                    .formatted(event.getStatus()));
        }
    }

    public Event findById(Long eventId) {
        EventEntity eventEntity = eventRepository.findById(eventId)
                .orElseThrow(() -> throwEventNotFoundException(eventId));
        return entityConverter.toDomain(eventEntity);
    }

    @Transactional
    public Event updateEvent(
            Long eventId,
            Event eventToUpdate
    ) {
        eventRepository.updateEvent(
                eventId,
                eventToUpdate.name(),
                eventToUpdate.maxPlaces(),
                eventToUpdate.date(),
                eventToUpdate.cost(),
                eventToUpdate.duration(),
                eventToUpdate.locationId()
        );
        EventEntity eventEntity = eventRepository.findById(eventId)
                .orElseThrow(() -> throwEventNotFoundException(eventId));

        Event event = entityConverter.toDomain(eventEntity);
        validateLocationCapacity(event);
        return event;
    }

    public List<Event> searchEvents(EventSearchRequestDto filter) {
        if (filter == null) {
            return eventRepository.findAll().stream()
                    .map(entityConverter::toDomain)
                    .toList();
        }

        return eventRepository.searchEvents(
                        filter.name(),
                        filter.placesMin(),
                        filter.placesMax(),
                        filter.dateStartAfter(),
                        filter.dateStartBefore(),
                        filter.costMin(),
                        filter.costMax(),
                        filter.durationMin(),
                        filter.durationMax(),
                        filter.locationId(),
                        filter.eventStatus() != null ? filter.eventStatus().name() : null
                ).stream()
                .map(entityConverter::toDomain)
                .toList();
    }

    public List<Event> getMyEvents(Long ownerId) {
        return eventRepository.findAllByOwnerId(ownerId).stream()
                .map(entityConverter::toDomain)
                .toList();
    }

    private EntityNotFoundException throwEventNotFoundException(Long id) {
        throw new EntityNotFoundException("Not found event with id=%s"
                .formatted(id));
    }

    // Проверяет существование локации и её вместимость
    private void validateLocationCapacity(Event event) {
        // Проверяем, существует ли локация
        LocationEntity locationEntity = locationRepository.findById(event.locationId())
                .orElseThrow(() -> new EntityNotFoundException("Location with id=%s not found"
                        .formatted(event.locationId())));

        // Проверяем вместимость локации
        if (event.maxPlaces() > locationEntity.getCapacity()) {
            throw new IllegalArgumentException("Event seats (%d) exceed location capacity (%d)"
                    .formatted(event.maxPlaces(), locationEntity.getCapacity()));
        }
    }

    @Transactional
    public void moveWaitStartToStarted() {
        List<EventEntity> events = eventRepository.findByStatus(EventStatus.WAIT_START.name());

        for (EventEntity event : events) {
            if(event.getDate().isBefore(LocalDateTime.now())) {
                event.setStatus(EventStatus.STARTED.name());
            }
        }
    }

    @Transactional
    public void moveStartedToFinished() {
        List<EventEntity> events = eventRepository.findByStatus(EventStatus.STARTED.name());

        for (EventEntity event : events) {
            if(event.getDate().plusMinutes(1).isBefore(LocalDateTime.now())) {
                event.setStatus(EventStatus.FINISHED.name());
            }
        }
    }
}
