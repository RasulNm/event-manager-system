package dev.sorokin.eventmanager.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.sorokin.eventmanager.errors.ErrorMessageResponse;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class SecurityResponseSender {

    private final ObjectMapper objectMapper;

    public SecurityResponseSender(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public void sendError(
            HttpServletResponse response,
            HttpStatus status,
            ErrorMessageResponse body
    ) throws IOException {
        response.setStatus(status.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");

        objectMapper.writeValue(response.getWriter(), body);
    }
}