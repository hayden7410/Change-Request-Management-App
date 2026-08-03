package com.hayden.changerequest.service;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import com.hayden.changerequest.dto.Auth.LoginRequest;
import com.hayden.changerequest.dto.Auth.LoginResponse;
import com.hayden.changerequest.security.JWTService;

@Service
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final JWTService jwtService;

    public AuthService(
            AuthenticationManager authenticationManager,
            JWTService jwtService) {

        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
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
}