package com.hayden.changerequest.dto.comment;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateCommentRequest(

        @NotBlank(message = "Comment cannot be blank")
        @Size(max = 2000, message = "Comment cannot exceed 2000 characters")
        String content

) {
}