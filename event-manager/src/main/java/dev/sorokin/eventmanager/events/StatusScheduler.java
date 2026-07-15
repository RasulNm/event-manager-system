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
        log.info("Starting WAIT_START -> STARTED processing");
        eventService.moveWaitStartToStarted();
        log.info("WAIT_START -> STARTED processing completed");
    }

    @Scheduled(fixedDelayString = "${task.fixed-delay-millis}")
    public void moveStartedToFinished() {
        log.info("Starting STARTED -> FINISHED processing");
        eventService.moveStartedToFinished();
        log.info("STARTED -> FINISHED processing completed");
    }
}
