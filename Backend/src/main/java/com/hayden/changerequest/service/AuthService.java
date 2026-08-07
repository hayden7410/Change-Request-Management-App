package com.hayden.changerequest.service;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hayden.changerequest.common.exception.ResourceNotFoundException;
import com.hayden.changerequest.dto.Auth.CurrentUserResponse;
import com.hayden.changerequest.dto.Auth.LoginRequest;
import com.hayden.changerequest.dto.Auth.LoginResponse;
import com.hayden.changerequest.security.JWTService;
import com.hayden.changerequest.entity.User;
import com.hayden.changerequest.repository.UserRepository;
import java.util.List;

@Service
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final JWTService jwtService;
    private final UserRepository userRepository;

    public AuthService(
            AuthenticationManager authenticationManager,
            JWTService jwtService,
            UserRepository userRepository) {

        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
        this.userRepository = userRepository;
    }

    public LoginResponse login(LoginRequest request) {

        Authentication authenticationRequest =
                UsernamePasswordAuthenticationToken.unauthenticated(
                        request.email(),
                        request.password()
                );

        Authentication authenticatedUser =
                authenticationManager.authenticate(authenticationRequest);

        String token = jwtService.generateToken(
                authenticatedUser.getName()
        );

        return new LoginResponse(token);
    }
        @Transactional(readOnly = true)
        public CurrentUserResponse getCurrentUser(
                Authentication authentication) {

        String email = authentication.getName();

        User user = userRepository
                .findByEmail(email)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Authenticated user was not found"
                        )
                );

        List<String> roles = authentication
                .getAuthorities()
                .stream()
                .map(authority -> authority.getAuthority())
                .filter(authority -> authority.startsWith("ROLE_"))
                .map(authority -> authority.substring(5))
                .toList();

        List<String> permissions = authentication
                .getAuthorities()
                .stream()
                .map(authority -> authority.getAuthority())
                .filter(authority -> !authority.startsWith("ROLE_"))
                .toList();

        return new CurrentUserResponse(
                user.getEmail(),
                user.getFirstName(),
                user.getLastName(),
                roles,
                permissions
        );
        }
}