package dev.sorokin.eventmanager.events;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

@Repository
public interface EventRepository extends JpaRepository<EventEntity, Long> {

    @Modifying
    @Query("""
            UPDATE EventEntity e
            SET e.name = :name,
            e.maxPlaces = :max_places,
            e.date = :date,
            e.cost = :cost,
            e.duration = :duration,
            e.locationId = :location_id
            WHERE e.id = :id
            """)
    void updateEvent(
            @Param("id") Long eventId,
            @Param("name") String name,
            @Param("max_places") Integer maxPlaces,
            @Param("date") LocalDateTime date,
            @Param("cost") Integer cost,
            @Param("duration") Integer duration,
            @Param("location_id") Long locationId
    );

    @Query(value = """
            SELECT * FROM events e
            WHERE (CAST(:name AS varchar) IS NULL OR e.name = :name)
              AND (CAST(:placesMin AS integer) IS NULL OR e.max_places >= :placesMin)
              AND (CAST(:placesMax AS integer) IS NULL OR e.max_places <= :placesMax)
              AND (CAST(:dateStartAfter AS timestamp) IS NULL OR e.date >= :dateStartAfter)
              AND (CAST(:dateStartBefore AS timestamp) IS NULL OR e.date <= :dateStartBefore)
              AND (CAST(:costMin AS integer) IS NULL OR e.cost >= :costMin)
              AND (CAST(:costMax AS integer) IS NULL OR e.cost <= :costMax)
              AND (CAST(:durationMin AS integer) IS NULL OR e.duration >= :durationMin)
              AND (CAST(:durationMax AS integer) IS NULL OR e.duration <= :durationMax)
              AND (CAST(:locationId AS bigint) IS NULL OR e.location_id = :locationId)
              AND (CAST(:eventStatus AS varchar) IS NULL OR e.status = :eventStatus)
            """, nativeQuery = true)
    List<EventEntity> searchEvents(
            @Param("name") String name,
            @Param("placesMin") Integer placesMin,
            @Param("placesMax") Integer placesMax,
            @Param("dateStartAfter") LocalDateTime dateStartAfter,
            @Param("dateStartBefore") LocalDateTime dateStartBefore,
            @Param("costMin") Integer costMin,
            @Param("costMax") Integer costMax,
            @Param("durationMin") Integer durationMin,
            @Param("durationMax") Integer durationMax,
            @Param("locationId") Long locationId,
            @Param("eventStatus") String eventStatus
    );

    List<EventEntity> findAllByOwnerId(Long ownerId);

    @Modifying
    @Query(value = """
            UPDATE events
            SET occupied_places = occupied_places + 1
            WHERE id = :eventId
            AND status = 'WAIT_START'
            AND max_places > occupied_places
            """, nativeQuery = true)
    int incrementOccupiedPlaces(
            @Param("eventId") Long eventId
    );

    @Modifying
    @Query(value = """
            UPDATE events
            SET occupied_places = occupied_places - 1
            WHERE id = :eventId
            AND status = 'WAIT_START'
            AND occupied_places > 0
            """, nativeQuery = true)
    int decrementOccupiedPlaces(
            @Param("eventId") Long eventId
    );

    @Query(value = """
            SELECT e.*
            FROM events e 
            JOIN registrations r ON(e.id = r.event_id)
            WHERE r.user_id = :ownerId
            """, nativeQuery = true)
    List<EventEntity> findAllByParticipantOwnerId(
            @Param("ownerId") Long userId
    );

    @Query(value = """
            SELECT MAX(e.max_places)
            FROM events e
            WHERE e.location_id = :locationId
            AND status != 'CANCELLED'
            """, nativeQuery = true)
    int findMaxRequiredPlacesByLocationId(
            @Param("locationId") Long id
    );

    @Query(value = """
            SELECT COUNT(*)
            FROM events e
            WHERE e.location_id = :locationId
            """, nativeQuery = true)
    int countByLocationId(
            @Param("locationId") Long id
    );

    List<EventEntity> findByStatus(String name);
}
