package com.hayden.changerequest.dto.ChangeRequest;

import com.hayden.changerequest.common.enums.UrgencyLevel;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CreateCRRequest(

        @NotBlank(message = "Title is required")
        @Size(
            max = 200,
            message = "Title cannot exceed 200 characters"
        )
        String title,

        @NotBlank(message = "Description is required")
        String description,

        String businessJustification,

        @NotNull(message = "Urgency is required")
        UrgencyLevel urgency,

        @NotNull(message = "Assigned department is required")
        Long assignedDepartmentId

) {
}