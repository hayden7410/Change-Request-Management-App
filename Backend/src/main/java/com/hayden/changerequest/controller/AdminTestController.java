package com.hayden.changerequest.controller;

import java.util.Map;

import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin")
public class AdminTestController {

    @GetMapping("/test")
    public Map<String, String> test(Authentication authentication) {
        return Map.of(
                "message", "Admin endpoint reached",
                "user", authentication.getName()
        );
    }
}