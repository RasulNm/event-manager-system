package dev.sorokin.eventmanager.events;

import dev.sorokin.eventmanager.users.User;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service("eventPermissionService")
public class EventPermissionService {

    private final EventRepository eventRepository;

    public EventPermissionService(EventRepository eventRepository) {
        this.eventRepository = eventRepository;
    }

    public boolean canManageEvent(Long eventId) {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth == null || !auth.isAuthenticated()) {
            return false;
        }

        boolean isAdmin = auth.getAuthorities().stream()
                .anyMatch(g -> g.getAuthority().equals("ADMIN"));
        if (isAdmin) {
            return true;
        }

        if (auth.getPrincipal() instanceof User user) {
            Long currentUserId = user.id();

            EventEntity event = eventRepository.findById(eventId)
                    .orElseThrow(() -> new EntityNotFoundException("Not found event with id=%s".formatted(eventId)));

            return event.getOwnerId().equals(currentUserId);
        }
        return false;
    }
}
