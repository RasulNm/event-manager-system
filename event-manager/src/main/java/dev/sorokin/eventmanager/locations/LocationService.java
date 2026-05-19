package dev.sorokin.eventmanager.locations;

import org.springframework.stereotype.Service;

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
}