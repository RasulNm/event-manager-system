package dev.sorokin.eventmanager.locations;

import org.springframework.stereotype.Component;

@Component
public class LocationEntityConverter {

    public Location toDomain(LocationEntity location) {
        return new Location(
                location.getId(),
                location.getName(),
                location.getAddress(),
                location.getCapacity(),
                location.getDescription()
        );
    }

    public LocationEntity toEntity(Location location) {
        return new LocationEntity(
                location.id(),
                location.name(),
                location.address(),
                location.capacity(),
                location.description()
        );
    }
}