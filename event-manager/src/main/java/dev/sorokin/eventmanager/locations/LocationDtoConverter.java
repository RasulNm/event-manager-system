package dev.sorokin.eventmanager.locations;

import org.springframework.stereotype.Component;

@Component
public class LocationDtoConverter {

    public Location toDomain(LocationDto location) {
        return new Location(
                location.id(),
                location.name(),
                location.address(),
                location.capacity(),
                location.description()
        );
    }

    public LocationDto toDto(Location location) {
        return new LocationDto(
                location.id(),
                location.name(),
                location.address(),
                location.capacity(),
                location.description()
        );
    }
}