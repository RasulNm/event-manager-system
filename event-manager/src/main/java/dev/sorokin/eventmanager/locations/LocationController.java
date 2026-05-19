package dev.sorokin.eventmanager.locations;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
}