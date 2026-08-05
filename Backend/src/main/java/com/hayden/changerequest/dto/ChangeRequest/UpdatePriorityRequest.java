package com.hayden.changerequest.dto.ChangeRequest;

import com.hayden.changerequest.common.enums.PriorityLevel;

import jakarta.validation.constraints.NotNull;

public record UpdatePriorityRequest(

        @NotNull(message = "Priority is required")
        PriorityLevel priority

) {
}