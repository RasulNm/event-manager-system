package dev.sorokin.eventmanager.locations;

import dev.sorokin.eventmanager.events.EventRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class LocationService {

    private final LocationEntityConverter entityConverter;
    private final LocationRepository locationRepository;
    private final EventRepository eventRepository;

    public LocationService(
            LocationEntityConverter entityConverter,
            LocationRepository locationRepository,
            EventRepository eventRepository
    ) {
        this.entityConverter = entityConverter;
        this.locationRepository = locationRepository;
        this.eventRepository = eventRepository;
    }

    public List<Location> getLocations() {
        return locationRepository.findAll()
                .stream()
                .map(entityConverter::toDomain)
                .toList();
    }

    public Location createLocation(Location location) {
        if (locationRepository.existsByName(location.name())) {
            throw new IllegalArgumentException("Location with name " +
                                               location.name() + " already exists");
        }
        var entityLocation = entityConverter.toEntity(location);
        return entityConverter.toDomain(
                locationRepository.save(entityLocation)
        );
    }

    public Location findById(Long id) {
        var foundLocation = locationRepository.findById(id)
                .orElseThrow(() -> {
                    throwEntityNotFoundException(id);
                    return null;
                });
        return entityConverter.toDomain(foundLocation);
    }

    @Transactional
    public Location updateLocation(
            Long id,
            Location locationToUpdate
    ) {
        int maxRequiredPlaces = eventRepository.findMaxRequiredPlacesByLocationId(id);

        if(locationToUpdate.capacity() < maxRequiredPlaces) {
            throw new IllegalArgumentException("Нельзя уменьшить вместимость локации до %s, так как существующие мероприятия требуют до %s мест"
                    .formatted(locationToUpdate.capacity(), maxRequiredPlaces));
        }

        int updatedCount = locationRepository.updateLocation(
                id,
                locationToUpdate.name(),
                locationToUpdate.address(),
                locationToUpdate.capacity(),
                locationToUpdate.description()
        );

        if (updatedCount == 0) {
            throwEntityNotFoundException(id);
        }

        return entityConverter.toDomain(
                locationRepository.findById(id).orElseThrow()
        );
    }

    public void deleteLocation(Long id) {
        if (!locationRepository.existsById(id)) {
            throwEntityNotFoundException(id);
        }

        int eventsCount = eventRepository.countByLocationId(id);
        if(eventsCount > 0) {
            throw new IllegalArgumentException("Нельзя удалить локацию с id=%s, так как к ней привязаны мероприятия (eventsCount=%s)"
                    .formatted(id, eventsCount));
        }
        locationRepository.deleteById(id);
    }

    private void throwEntityNotFoundException(Long id) {
        throw new EntityNotFoundException("Not found location with id=%s"
                .formatted(id));
    }
}