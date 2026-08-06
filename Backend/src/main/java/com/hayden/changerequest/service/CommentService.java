package com.hayden.changerequest.service;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import com.hayden.changerequest.common.enums.ChangeRequestStatus;
import com.hayden.changerequest.common.exception.InvalidRequestStateException;
import com.hayden.changerequest.common.exception.ResourceNotFoundException;
import com.hayden.changerequest.dto.comment.CommentCreationResponse;
import com.hayden.changerequest.dto.comment.CreateCommentRequest;
import com.hayden.changerequest.entity.ChangeRequest;
import com.hayden.changerequest.entity.ChangeRequestComment;
import com.hayden.changerequest.repository.ChangeRequestCommentRepository;
import com.hayden.changerequest.repository.ChangeRequestRepository;
import com.hayden.changerequest.repository.UserRepository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import com.hayden.changerequest.entity.User;
import java.util.List;

@Service
public class CommentService {

    private final ChangeRequestCommentRepository commentRepository;
    private final ChangeRequestRepository changeRequestRepository;
    private final UserRepository userRepository;

    public CommentService(
            ChangeRequestCommentRepository commentRepository,
            ChangeRequestRepository changeRequestRepository,
            UserRepository userRepository) {

        this.commentRepository = commentRepository;
        this.changeRequestRepository = changeRequestRepository;
        this.userRepository = userRepository;
    }
    @Transactional
    @PreAuthorize("hasAuthority('COMMENT_ON_REQUEST')")
public CommentCreationResponse addComment(
        Long requestId,
        CreateCommentRequest request,
        Authentication authentication) {

    ChangeRequest changeRequest = changeRequestRepository
            .findById(requestId)
            .orElseThrow(() ->
                    new ResourceNotFoundException(
                            "Change request was not found"
                    )
            );

    if (changeRequest.getStatus() == ChangeRequestStatus.DRAFT) {
        throw new InvalidRequestStateException(
                "Comments cannot be added to a draft change request"
        );
    }

    String currentUserEmail = authentication.getName();

    boolean isOwner = changeRequest
            .getSubmittedBy()
            .getEmail()
            .equals(currentUserEmail);

    boolean canViewAllRequests = authentication
            .getAuthorities()
            .stream()
            .anyMatch(authority ->
                    authority.getAuthority()
                            .equals("VIEW_ALL_REQUESTS")
            );

    boolean isAssignedDeveloper =
            changeRequest.getAssignedDeveloper() != null
            && changeRequest
                    .getAssignedDeveloper()
                    .getEmail()
                    .equals(currentUserEmail);

    if (!isOwner
            && !canViewAllRequests
            && !isAssignedDeveloper) {

        throw new AccessDeniedException(
                "You are not authorized to comment on this change request"
        );
    }

    User author = userRepository
            .findByEmail(currentUserEmail)
            .orElseThrow(() ->
                    new ResourceNotFoundException(
                            "Authenticated user was not found"
                    )
            );

    ChangeRequestComment comment =
            new ChangeRequestComment();

    comment.setChangeRequest(changeRequest);
    comment.setAuthor(author);
    comment.setContent(request.content());

    ChangeRequestComment savedComment =
            commentRepository.save(comment);

    return toCreationResponse(savedComment);
}
@Transactional(readOnly = true)
@PreAuthorize("""
    hasAnyAuthority(
        'VIEW_SUBMITTED_REQUESTS',
        'VIEW_ALL_REQUESTS',
        'VIEW_ASSIGNED_REQUESTS'
    )
""")
public List<CommentCreationResponse> getComments(
        Long requestId,
        Authentication authentication) {

    ChangeRequest changeRequest = changeRequestRepository
            .findById(requestId)
            .orElseThrow(() ->
                    new ResourceNotFoundException(
                            "Change request was not found"
                    )
            );

    String currentUserEmail = authentication.getName();

    boolean isOwner = changeRequest
            .getSubmittedBy()
            .getEmail()
            .equals(currentUserEmail);

    boolean canViewAllRequests = authentication
            .getAuthorities()
            .stream()
            .anyMatch(authority ->
                    authority.getAuthority()
                            .equals("VIEW_ALL_REQUESTS")
            );

    boolean isAssignedDeveloper =
            changeRequest.getAssignedDeveloper() != null
            && changeRequest
                    .getAssignedDeveloper()
                    .getEmail()
                    .equals(currentUserEmail);

    if (!isOwner
            && !canViewAllRequests
            && !isAssignedDeveloper) {

        throw new AccessDeniedException(
                "You are not authorized to view comments on this change request"
        );
    }

    return commentRepository
            .findByChangeRequest_IdOrderByCreatedAtAsc(requestId)
            .stream()
            .map(this::toCreationResponse)
            .toList();
}
    
private CommentCreationResponse toCreationResponse(
        ChangeRequestComment comment) {

    return new CommentCreationResponse(
            comment.getId(),
            comment.getContent(),
            comment.getAuthor().getEmail(),
            comment.getCreatedAt()
    );
}
}