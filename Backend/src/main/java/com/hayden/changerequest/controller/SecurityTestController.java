package com.hayden.changerequest.controller;

import java.util.Map;

import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/change-requests")
public class SecurityTestController {

    @GetMapping("/test")
    public Map<String, Object> test(Authentication authentication) {
        return Map.of(
                "message", "Protected endpoint reached",
                "user", authentication.getName(),
                "authorities", authentication.getAuthorities()
        );
    }
}