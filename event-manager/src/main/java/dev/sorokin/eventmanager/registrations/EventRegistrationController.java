package dev.sorokin.eventmanager.registrations;

import dev.sorokin.eventmanager.events.EventDto;
import dev.sorokin.eventmanager.events.EventDtoConverter;
import dev.sorokin.eventmanager.users.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/events/registrations")
public class EventRegistrationController {

    private static final Logger log = LoggerFactory.getLogger(EventRegistrationController.class);

    private final EventRegistrationService eventRegistrationService;
    private final EventDtoConverter dtoConverter;

    public EventRegistrationController(
            EventRegistrationService eventRegistrationService,
            EventDtoConverter dtoConverter
    ) {
        this.eventRegistrationService = eventRegistrationService;
        this.dtoConverter = dtoConverter;
    }


    @PostMapping("/{eventId}")
    public ResponseEntity<Void> registerToEvent(
            @PathVariable("eventId") Long eventId,
            @AuthenticationPrincipal User user
    ) {
        log.info("User ID={} is registering to event ID={}", user.id(), eventId);
        eventRegistrationService.registerToEvent(eventId, user.id());
        return ResponseEntity
                .status(HttpStatus.OK)
                .build();
    }

    @DeleteMapping("/cancel/{eventId}")
    public ResponseEntity<Void> cancelRegistration(
            @PathVariable("eventId") Long eventId,
            @AuthenticationPrincipal User user
    ) {
        log.info("User ID={} is canceling event ID={}", user.id(), eventId);
        eventRegistrationService.cancelRegistration(eventId, user.id());
        return ResponseEntity
                .status(HttpStatus.NO_CONTENT)
                .build();
    }

    @GetMapping("/my")
    public ResponseEntity<List<EventDto>> getMyEvents(
            @AuthenticationPrincipal User user
    ) {
        log.info("User ID={} is getting events", user.id());
        List<EventDto> foundEvents = eventRegistrationService.getEvents(user.id()).stream()
                .map(dtoConverter::toDto)
                .toList();
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(foundEvents);
    }
}
