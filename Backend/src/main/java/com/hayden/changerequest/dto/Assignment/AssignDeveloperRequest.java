package com.hayden.changerequest.dto.Assignment;

import jakarta.validation.constraints.NotNull;

public record AssignDeveloperRequest(

        @NotNull(message = "Developer ID is required")
        Long developerId

) {
}