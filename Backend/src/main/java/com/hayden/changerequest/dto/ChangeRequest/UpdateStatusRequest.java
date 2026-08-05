package com.hayden.changerequest.dto.ChangeRequest;

import com.hayden.changerequest.common.enums.ChangeRequestStatus;
import jakarta.validation.constraints.NotNull;

public record UpdateStatusRequest(

        @NotNull(message = "Status is required")
        ChangeRequestStatus status

) {
}