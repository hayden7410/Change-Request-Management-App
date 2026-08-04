package com.hayden.changerequest.controller;

import java.util.Map;

import org.springframework.security.access.prepost.PreAuthorize;
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
    @PreAuthorize("hasAuthority('CREATE_CHANGE_REQUEST')")
    @GetMapping("/test/create")
    public String testCreatePermission(){
        return "You have the CREATE_CHANGE_REQUEST permission!";
    }
    @PreAuthorize("hasAuthority('Approve_Request')")
    @GetMapping("/test/approve")
    public String testApprovePermission(){
        return "You have the Approve_Request permission!";
    }
}