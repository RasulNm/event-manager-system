package dev.sorokin.eventmanager.events;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class StatusScheduler {

    private static final Logger log = LoggerFactory.getLogger(StatusScheduler.class);

    private final EventService eventService;

    public StatusScheduler(EventService eventService) {
        this.eventService = eventService;
    }

    @Scheduled(fixedDelayString = "${task.fixed-delay-millis}")
    public void moveWaitStartToStarted() {
        log.info("Starting {} -> {} processing", EventStatus.WAIT_START, EventStatus.STARTED);
        eventService.changeStatus(EventStatus.WAIT_START, EventStatus.STARTED);
        log.info("{} -> {} processing completed", EventStatus.WAIT_START, EventStatus.STARTED);
    }

    @Scheduled(fixedDelayString = "${task.fixed-delay-millis}")
    public void moveStartedToFinished() {
        log.info("Starting {} -> {} processing", EventStatus.STARTED, EventStatus.FINISHED);
        eventService.changeStatus(EventStatus.STARTED, EventStatus.FINISHED);
        log.info("{} -> {} processing completed", EventStatus.STARTED, EventStatus.FINISHED);
    }
}
