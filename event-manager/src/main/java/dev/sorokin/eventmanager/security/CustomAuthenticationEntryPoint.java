package dev.sorokin.eventmanager.security;

import dev.sorokin.eventmanager.errors.ErrorMessageResponse;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDateTime;

@Component
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private static final Logger log = LoggerFactory.getLogger(CustomAuthenticationEntryPoint.class);
    private final SecurityResponseSender securityResponseSender;

    public CustomAuthenticationEntryPoint(SecurityResponseSender securityResponseSender) {
        this.securityResponseSender = securityResponseSender;
    }

    @Override
    public void commence(
            HttpServletRequest request,
            HttpServletResponse response,
            AuthenticationException authException)
            throws IOException, ServletException {
        log.error("Handling authentication exception: ", authException);

        var messageResponse = new ErrorMessageResponse(
                "Необходима аутентификация",
                authException.getMessage(),
                LocalDateTime.now()
        );

        securityResponseSender.sendError(response, HttpStatus.UNAUTHORIZED, messageResponse);
    }
}