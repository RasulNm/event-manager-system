package dev.sorokin.eventmanager.users;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class AdminInitializer {

    private final static Logger log = LoggerFactory.getLogger(AdminInitializer.class);
    private final UserService userService;
    private final String adminLogin;
    private final String adminPassword;


    public AdminInitializer(
            UserService userService,
            @Value("${admin.username}") String adminLogin,
            @Value("${admin.password}") String adminPassword
    ) {
        this.userService = userService;
        this.adminLogin = adminLogin;
        this.adminPassword = adminPassword;
    }

    @EventListener(ApplicationReadyEvent.class)
    public void createAdmin() {
        log.info("Creating Admin");
        userService.createAdmin(adminLogin, adminPassword);
        log.info("Admin created");
    }
}
