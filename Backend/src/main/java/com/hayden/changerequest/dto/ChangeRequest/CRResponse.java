package com.hayden.changerequest.dto.ChangeRequest;

import java.time.Instant;

import com.hayden.changerequest.common.enums.ChangeRequestStatus;
import com.hayden.changerequest.common.enums.PriorityLevel;
import com.hayden.changerequest.common.enums.UrgencyLevel;

public record CRResponse(

        Long id,

        String title,

        String description,

        String businessJustification,

        ChangeRequestStatus status,

        UrgencyLevel urgency,

        PriorityLevel priority,

        String submittedByEmail,

        Long assignedDepartmentId,

        String assignedDepartmentName,

        Instant createdAt

) {
}