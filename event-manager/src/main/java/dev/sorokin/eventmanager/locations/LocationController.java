package dev.sorokin.eventmanager.locations;

import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/locations")
public class LocationController {

    private final Logger log = LoggerFactory.getLogger(LocationController.class);
    private final LocationService locationService;
    private final LocationDtoConverter dtoConverter;

    public LocationController(
            LocationService locationService,
            LocationDtoConverter dtoConverter
    ) {
        this.locationService = locationService;
        this.dtoConverter = dtoConverter;
    }

    @GetMapping
    public ResponseEntity<List<LocationDto>> getLocations() {
        log.info("Getting locations");
        var allLocations = locationService.getLocations();
        return ResponseEntity.ok(
                allLocations.stream()
                        .map(dtoConverter::toDto)
                        .toList()
        );
    }

    @PostMapping
    public ResponseEntity<LocationDto> createLocation(
            @RequestBody @Valid LocationDto locationToCreate
    ) {
        log.info("Creating location: {}", locationToCreate);
        Location createdLocation = locationService.createLocation(
                dtoConverter.toDomain(locationToCreate)
        );
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(dtoConverter.toDto(createdLocation));
    }

    @GetMapping("/{locationId}")
    public ResponseEntity<LocationDto> getLocation(
            @PathVariable("locationId") Long locationId
    ) {
        log.info("Getting location by id: {}", locationId);
        var foundLocation = locationService.findById(locationId);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(dtoConverter.toDto(foundLocation));
    }
}