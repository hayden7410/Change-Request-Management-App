package com.hayden.changerequest.config;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;    
import org.springframework.security.config.http.SessionCreationPolicy;
//Adding authentication Manager -> CustomUserDetailsService
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
//Adding JWT authentication filter
import com.hayden.changerequest.security.JWTAuthenticationFilter;
//Adding method level authorization
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import jakarta.servlet.http.HttpServletResponse;
//Adding error-dispatch rule
import jakarta.servlet.DispatcherType;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
@Configuration
@EnableMethodSecurity
public class SecurityConfig {
    private final JWTAuthenticationFilter jwtAuthenticationFilter;

public SecurityConfig(
        JWTAuthenticationFilter jwtAuthenticationFilter) {

    this.jwtAuthenticationFilter = jwtAuthenticationFilter;
}
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
        .csrf(csrf -> csrf.disable())
        .sessionManagement(session ->session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
        .formLogin(form -> form.disable())
        .httpBasic(httpBasic -> httpBasic.disable())
        .authorizeHttpRequests(auth -> auth
            .dispatcherTypeMatchers(DispatcherType.ERROR).permitAll()
            .requestMatchers("/api/health","/api/auth/login").permitAll()
            .requestMatchers("/api/admin/**").hasRole("SYSTEM_ADMIN")
            .requestMatchers("/api/change-requests/**").authenticated()
            .anyRequest().denyAll()
            )
            .exceptionHandling(exception -> exception

        .authenticationEntryPoint((request, response, authException) -> {
            response.setStatus(
                HttpServletResponse.SC_UNAUTHORIZED
            );

            response.setContentType("application/json");

            response.getWriter().write(
                "{\"error\":\"Unauthorized\",\"message\":\"Authentication is required\"}"
            );
        })

        .accessDeniedHandler((request, response, accessDeniedException) -> {
            response.setStatus(
                HttpServletResponse.SC_FORBIDDEN
            );

            response.setContentType("application/json");

            response.getWriter().write(
                "{\"error\":\"Forbidden\",\"message\":\"You do not have permission to access this resource\"}"
            );
        })
    )
        .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
            
    
}
 @Bean
    public AuthenticationManager authenticationManager(
        AuthenticationConfiguration configuration) throws Exception{
            return configuration.getAuthenticationManager();
        }
   
    
}