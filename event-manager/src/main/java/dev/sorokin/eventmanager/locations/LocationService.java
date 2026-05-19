package dev.sorokin.eventmanager.locations;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class LocationService {

    private final LocationEntityConverter entityConverter;
    private final LocationRepository locationRepository;

    public LocationService(
            LocationEntityConverter entityConverter,
            LocationRepository locationRepository
    ) {
        this.entityConverter = entityConverter;
        this.locationRepository = locationRepository;
    }

    public List<Location> getLocations() {
        var foundLocations = locationRepository.findAll();

        List<Location> domainLocations = new ArrayList<>();
        for (var location : foundLocations) {
            domainLocations.add(entityConverter.toDomain(location));
        }
        return domainLocations;
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
                .orElseThrow(() -> new EntityNotFoundException(
                        "Not found location with id=%s".formatted(id)
                ));
        return entityConverter.toDomain(foundLocation);
    }

    @Transactional
    public Location updateLocation(
            Long id,
            Location locationToUpdate
    ) {
        int updatedCount = locationRepository.updateLocation(
                id,
                locationToUpdate.name(),
                locationToUpdate.address(),
                locationToUpdate.capacity(),
                locationToUpdate.description()
        );

        if (updatedCount == 0) {
            throw new EntityNotFoundException("Not found location with id=%s"
                    .formatted(id)
            );
        }

        return entityConverter.toDomain(
                locationRepository.findById(id).orElseThrow()
        );
    }
}