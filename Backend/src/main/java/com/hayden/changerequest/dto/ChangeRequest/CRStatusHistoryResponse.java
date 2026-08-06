package com.hayden.changerequest.dto.ChangeRequest;

import java.time.Instant;

import com.hayden.changerequest.common.enums.ChangeRequestStatus;

public record CRStatusHistoryResponse(
        Long id,
        ChangeRequestStatus previousStatus,
        ChangeRequestStatus newStatus,
        String changedByEmail,
        Instant changedAt
) {
}