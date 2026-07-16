package dev.sorokin.eventmanager.errors;

import jakarta.persistence.EntityNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorMessageResponse> handleValidationException(
            Exception exception
    ) {
        log.error("Unhandled server error: ", exception);
        var errorMessage = new ErrorMessageResponse(
                "Внутренняя ошибка сервера",
                exception.getMessage(),
                LocalDateTime.now()
        );

        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(errorMessage);
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ErrorMessageResponse> handleNotFoundException(
            EntityNotFoundException exception
    ) {
        log.warn("Entity not found: ", exception);
        var errorMessage = new ErrorMessageResponse(
                "Сущность не найдена",
                exception.getMessage(),
                LocalDateTime.now()
        );

        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(errorMessage);
    }

    @ExceptionHandler(AuthorizationDeniedException.class)
    public ResponseEntity<ErrorMessageResponse> handleAuthorizationDeniedException(
            AuthorizationDeniedException exception
    ) {
        log.warn("Authorization denied: ", exception);
        var errorMessage = new ErrorMessageResponse(
                "Недостаточно прав для выполнения операции",
                exception.getMessage(),
                LocalDateTime.now()
        );

        return ResponseEntity
                .status(HttpStatus.FORBIDDEN)
                .body(errorMessage);
    }

    @ExceptionHandler({
            IllegalArgumentException.class,
            MethodArgumentNotValidException.class
    })
    public ResponseEntity<ErrorMessageResponse> handleIllegalArgumentException(
            Exception exception
    ) {
        log.warn("Illegal argument: ", exception);

        String detailedMessage = exception instanceof MethodArgumentNotValidException
                ? constructMethodArgumentNotValidException((MethodArgumentNotValidException) exception)
                : exception.getMessage();

        var errorMessage = new ErrorMessageResponse(
                "Некорректный запрос",
                detailedMessage,
                LocalDateTime.now()
        );

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(errorMessage);
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ErrorMessageResponse> handleBadCredentialsException(
            BadCredentialsException exception
    ) {
        log.warn("Bad credentials: ", exception);
        var errorMessage = new ErrorMessageResponse(
                "Необходима аутентификация",
                exception.getMessage(),
                LocalDateTime.now()
        );

        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(errorMessage);
    }

    private String constructMethodArgumentNotValidException(
            MethodArgumentNotValidException exception
    ) {
        return exception.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .collect(Collectors.joining(", "));
    }
}