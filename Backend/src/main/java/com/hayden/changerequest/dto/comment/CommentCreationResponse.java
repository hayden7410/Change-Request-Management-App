package com.hayden.changerequest.dto.comment;

import java.time.Instant;

public record CommentCreationResponse(
        Long id,
        String content,
        String authorEmail,
        Instant createdAt
) {
}