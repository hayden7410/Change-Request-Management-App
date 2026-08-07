package com.hayden.changerequest.dto.Auth;

import java.util.List;

public record CurrentUserResponse(
        String email,
        String firstName,
        String lastName,
        List<String> roles,
        List<String> permissions
) {
}