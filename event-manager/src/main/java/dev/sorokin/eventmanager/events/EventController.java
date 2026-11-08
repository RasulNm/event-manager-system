package dev.sorokin.eventmanager.events;

import dev.sorokin.eventmanager.users.User;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.graphql.GraphQlProperties;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/events")
public class EventController {

    private static final Logger log = LoggerFactory.getLogger(EventController.class);

    private final EventService eventService;
    private final EventDtoConverter dtoConverter;

    public EventController(
            EventService eventService,
            EventDtoConverter dtoConverter
    ) {
        this.eventService = eventService;
        this.dtoConverter = dtoConverter;
    }

    @PostMapping
    public ResponseEntity<EventDto> createEvent(
            @RequestBody @Valid EventDto eventDto
    ) {
        log.info("Creating event: eventDto={}", eventDto);
        var cretedEvent = eventService.createEvent(dtoConverter.toDomain(eventDto));
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(dtoConverter.toDto(cretedEvent));
    }

    @DeleteMapping("/{eventId}")
    @PreAuthorize("hasAuthority('ADMIN') or @eventPermissionService.canManageEvent(#eventId)")
    public ResponseEntity<Void> deleteEvent(
            @PathVariable("eventId") Long eventId
    ) {
        log.info("Deleting event: eventId={}", eventId);
        eventService.deleteEvent(eventId);
        return ResponseEntity
                .status(HttpStatus.NO_CONTENT)
                .build();
    }

    @GetMapping("/{eventId}")
    public ResponseEntity<EventDto> getEvent(
            @PathVariable("eventId") Long eventId
    ) {
        log.info("Getting event: eventId={}", eventId);
        var foundEvent = eventService.findById(eventId);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(dtoConverter.toDto(foundEvent));
    }

    @PutMapping("/{eventId}")
    @PreAuthorize("hasAuthority('ADMIN') or @eventPermissionService.canManageEvent(#eventId)")
    public ResponseEntity<EventDto> updateEvent(
            @RequestBody @Valid EventDto eventDto,
            @PathVariable("eventId") Long eventId
    ) {
        log.info("Updating event with ID = eventId={}: eventDto={}", eventId, eventDto);
        var updatedEvent = eventService.updateEvent(
                eventId,
                dtoConverter.toDomain(eventDto));
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(dtoConverter.toDto(updatedEvent));
    }

    @PostMapping("/search")
    public ResponseEntity<List<EventDto>> searchEvents(
            @RequestBody(required = false) EventSearchRequestDto eventSearchRequestDto
    ) {
        log.info("Searching event: eventSearchRequestDto={}", eventSearchRequestDto);
        var foundEvents = eventService.searchEvents(eventSearchRequestDto);
        List<EventDto> eventDtoList = foundEvents.stream()
                .map(dtoConverter::toDto)
                .toList();
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(eventDtoList);
    }

    @GetMapping("/my")
    public ResponseEntity<List<EventDto>> getMyEvents(
            @AuthenticationPrincipal User user
    ) {
        log.info("Getting all events for user ID={}", user.id());
        var myEvents = eventService.getMyEvents(user.id());
        List<EventDto> eventDtoList = myEvents.stream()
                .map(dtoConverter::toDto)
                .toList();
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(eventDtoList);
    }
}
