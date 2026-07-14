package dev.sorokin.eventmanager.registrations;

import dev.sorokin.eventmanager.events.*;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class EventRegistrationService {

    private final EventRepository eventRepository;
    private final EventRegistrationRepository eventRegistrationRepository;
    private final EventEntityConverter entityConverter;

    public EventRegistrationService(
            EventRepository eventRepository,
            EventRegistrationRepository eventRegistrationRepository,
            EventEntityConverter entityConverter
    ) {
        this.eventRepository = eventRepository;
        this.eventRegistrationRepository = eventRegistrationRepository;
        this.entityConverter = entityConverter;
    }

    @Transactional
    public void registerToEvent(Long eventId, Long userId) {

        EventEntity event = getValidEventOrThrow(eventId);

        // Создадтель не может записаться на свое мероприятие
        if (event.getOwnerId().equals(userId)) {
            throw new IllegalArgumentException("Creator cannot register for their own event");
        }

        // Проверяем, зарегистрирован ли уже userId на текущий eventId
        if (eventRegistrationRepository.existsByEventIdAndUserId(eventId, userId)) {
            throw new IllegalArgumentException("User is already registered for this event");
        }

        // БД проверит WAIT_START и max_places > occupied_places
        int updatedRows = eventRepository.incrementOccupiedPlaces(eventId);
        if (updatedRows == 0) {
            throw new IllegalArgumentException("No free places left for this event: eventId=%s".formatted(eventId));
        }

        eventRegistrationRepository.save(new EventRegistrationEntity(event, userId));
    }

    @Transactional
    public void cancelRegistration(Long eventId, Long userId) {
        EventRegistrationEntity registration = eventRegistrationRepository.findByEventIdAndUserId(eventId, userId)
                .orElseThrow(() -> new EntityNotFoundException("Registration with eventId=%s not found".formatted(eventId)));

        EventEntity event = getValidEventOrThrow(eventId);

        int updatedRows = eventRepository.decrementOccupiedPlaces(eventId);
        if (updatedRows == 0) {
            throw new IllegalArgumentException("Cancellation failed: occupied_places=%s  status=%s"
                    .formatted(event.getOccupiedPlaces(), event.getStatus()));
        }

        eventRegistrationRepository.delete(registration);
    }

    public List<Event> getEvents(Long userId) {
        return eventRepository.findAllByParticipantOwnerId(userId).stream()
                .map(entityConverter::toDomain)
                .toList();
    }

    private EventEntity getValidEventOrThrow(Long eventId) {
        EventEntity event = eventRepository.findById(eventId)
                .orElseThrow(() -> new EntityNotFoundException("Event with id=%s not found".formatted(eventId)));

        // Проверка статуса
        String currentStatus = event.getStatus();
        if (!EventStatus.WAIT_START.name().equals(currentStatus)) {
            throw new IllegalArgumentException("Registration is only allowed in WAIT_START status. Current status: %s".formatted(currentStatus));
        }

        // Проверка даты начала мероприятия
        if (event.getDate().isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("Date cannot be before current date");
        }

        return event;
    }
}
