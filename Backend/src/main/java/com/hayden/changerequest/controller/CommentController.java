package com.hayden.changerequest.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import com.hayden.changerequest.dto.comment.CommentCreationResponse;
import com.hayden.changerequest.dto.comment.CreateCommentRequest;
import com.hayden.changerequest.service.CommentService;

import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/change-requests/{requestId}/comments")
public class CommentController {

    private final CommentService commentService;

    public CommentController(CommentService commentService) {
        this.commentService = commentService;
    }

    @PostMapping
    public ResponseEntity<CommentCreationResponse> addComment(
            @PathVariable Long requestId,
            @Valid @RequestBody CreateCommentRequest request,
            Authentication authentication) {

        CommentCreationResponse response =
                commentService.addComment(
                        requestId,
                        request,
                        authentication
                );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }
    @GetMapping
public ResponseEntity<List<CommentCreationResponse>> getComments(
        @PathVariable Long requestId,
        Authentication authentication) {

    List<CommentCreationResponse> responses =
            commentService.getComments(
                    requestId,
                    authentication
            );

    return ResponseEntity.ok(responses);
}
}