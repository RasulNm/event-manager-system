package dev.sorokin.eventmanager.registrations;

import jdk.jfr.Registered;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

@Registered
public interface EventRegistrationRepository extends JpaRepository<EventRegistrationEntity, Long> {
    boolean existsByEventIdAndUserId(Long eventId, Long userId);

    Optional<EventRegistrationEntity> findByEventIdAndUserId(Long eventId, Long userId);

    Optional<List<EventRegistrationEntity>> findByUserId(Long userId);
}
