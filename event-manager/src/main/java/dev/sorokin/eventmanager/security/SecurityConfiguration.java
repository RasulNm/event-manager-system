package dev.sorokin.eventmanager.security;

import dev.sorokin.eventmanager.security.jwt.JwtTokenFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AnonymousAuthenticationFilter;

@Configuration
@EnableMethodSecurity
public class SecurityConfiguration {

    private final CustomUserDetailsService userDetailsService;
    private final CustomAuthenticationEntryPoint customAuthenticationEntryPoint;
    private final CustomAccessDeniedHandler customAccessDeniedHandler;
    private final JwtTokenFilter jwtTokenFilter;

    public SecurityConfiguration(
            CustomUserDetailsService userDetailsService,
            CustomAuthenticationEntryPoint customAuthenticationEntryPoint,
            CustomAccessDeniedHandler customAccessDeniedHandler,
            JwtTokenFilter jwtTokenFilter
    ) {
        this.userDetailsService = userDetailsService;
        this.customAuthenticationEntryPoint = customAuthenticationEntryPoint;
        this.customAccessDeniedHandler = customAccessDeniedHandler;
        this.jwtTokenFilter = jwtTokenFilter;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .formLogin(AbstractHttpConfigurer::disable)
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(session -> {
                    session.sessionCreationPolicy(SessionCreationPolicy.STATELESS);
                })
                .authorizeHttpRequests(authorizeHttpRequests -> {
                    authorizeHttpRequests
                            .requestMatchers(HttpMethod.POST, "/locations")
                            .hasAnyAuthority("ADMIN")
                            .requestMatchers(HttpMethod.GET, "/locations/**")
                            .hasAnyAuthority("ADMIN", "USER")
                            .requestMatchers(HttpMethod.DELETE, "/locations/**")
                            .hasAnyAuthority("ADMIN")
                            .requestMatchers(HttpMethod.PUT, "/locations/**")
                            .hasAnyAuthority("ADMIN")

                            .requestMatchers(HttpMethod.POST, "/users")
                            .permitAll()
                            .requestMatchers(HttpMethod.POST, "/users/auth")
                            .permitAll()
                            .requestMatchers(HttpMethod.GET, "/users/**")
                            .hasAnyAuthority("ADMIN")

                            .requestMatchers(HttpMethod.POST, "/events")
                            .hasAnyAuthority("USER")
                            .requestMatchers(HttpMethod.DELETE, "/events/{eventId}")
                            .authenticated()
                            .requestMatchers(HttpMethod.GET, "/events/my")
                            .hasAnyAuthority("USER")
                            .requestMatchers(HttpMethod.GET, "/events/{eventId}")
                            .hasAnyAuthority("ADMIN", "USER")
                            .requestMatchers(HttpMethod.PUT, "/events/**")
                            .authenticated()
                            .requestMatchers(HttpMethod.POST, "/events/search")
                            .hasAnyAuthority("ADMIN", "USER")

                            .requestMatchers(HttpMethod.POST, "/events/registrations/{eventId}")
                            .hasAnyAuthority("USER")
                            .requestMatchers(HttpMethod.DELETE, "/events/registrations/cancel/{eventId}")
                            .hasAnyAuthority("USER")
                            .requestMatchers(HttpMethod.GET, "/events/registrations/my")
                            .hasAnyAuthority("USER")

                            .requestMatchers("/swagger-ui/**", "/v3/api-docs/**", "/openapi.yaml")
                            .permitAll()
                            .anyRequest()
                            .authenticated();
                })
                .exceptionHandling(exception ->
                        exception.authenticationEntryPoint(customAuthenticationEntryPoint)
                                .accessDeniedHandler(customAccessDeniedHandler)
                )
                .addFilterBefore(jwtTokenFilter, AnonymousAuthenticationFilter.class)
                .build();
    }

    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration configuration
    ) throws Exception {
        return configuration.getAuthenticationManager();
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        var authProvider = new DaoAuthenticationProvider();

        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());

        return authProvider;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}