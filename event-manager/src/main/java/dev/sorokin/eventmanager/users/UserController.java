package dev.sorokin.eventmanager.users;

import dev.sorokin.eventmanager.security.jwt.JwtAuthenticationService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
public class UserController {

    private static final Logger log = LoggerFactory.getLogger(UserController.class);
    private final UserService userService;
    private final JwtAuthenticationService jwtAuthenticationService;

    public UserController(
            UserService userService,
            JwtAuthenticationService jwtAuthenticationService
    ) {
        this.userService = userService;
        this.jwtAuthenticationService = jwtAuthenticationService;
    }


    @PostMapping
    public ResponseEntity<UserDto> registerUser(
            @RequestBody @Valid SignUpRequest signUpRequest
    ) {
        log.info("Get request for sign-up: login={}", signUpRequest.login());
        var user = userService.registerUser(signUpRequest);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(new UserDto(user.id(), user.login(), user.age(), user.role()));
    }

    @PostMapping("/auth")
    public ResponseEntity<JwtTokenResponse> authenticate(
            @RequestBody @Valid SignInRequest signInRequest
    ) {
        log.info("Get request for sign-in: login={}", signInRequest.login());
        var token = jwtAuthenticationService.authenticationUser(signInRequest);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(new JwtTokenResponse(token));
    }

    @GetMapping("/{userId}")
    public ResponseEntity<UserDto> getUser(
            @PathVariable("userId") Long userId
    ) {
        log.info("Get request for user: userId={}", userId);
        var foundUser = userService.findById(userId);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(new UserDto(foundUser.id(), foundUser.login(), foundUser.age(), foundUser.role()));
    }
}