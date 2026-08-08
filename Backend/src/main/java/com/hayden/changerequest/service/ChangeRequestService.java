package com.hayden.changerequest.service;

import java.time.Instant;
import java.util.List;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.security.access.AccessDeniedException;

import com.hayden.changerequest.dto.Assignment.AssignDeveloperRequest;
import com.hayden.changerequest.dto.ChangeRequest.CRResponse;
import com.hayden.changerequest.dto.ChangeRequest.CRStatusHistoryResponse;
import com.hayden.changerequest.dto.ChangeRequest.CreateCRRequest;
import com.hayden.changerequest.dto.ChangeRequest.UpdateCRRequest;
import com.hayden.changerequest.dto.ChangeRequest.UpdatePriorityRequest;

import com.hayden.changerequest.entity.ChangeRequest;
import com.hayden.changerequest.entity.Department;
import com.hayden.changerequest.entity.User;
import com.hayden.changerequest.entity.ChangeRequestStatusHistory;

import com.hayden.changerequest.repository.ChangeRequestRepository;
import com.hayden.changerequest.repository.DepartmentRepository;
import com.hayden.changerequest.repository.UserRepository;
import com.hayden.changerequest.repository.ChangeRequestStatusHistoryRepository;
import com.hayden.changerequest.common.exception.ResourceNotFoundException;
import com.hayden.changerequest.common.enums.ChangeRequestStatus;
import com.hayden.changerequest.common.enums.CreateCRAction;

import com.hayden.changerequest.common.exception.InvalidRequestStateException;


import org.springframework.security.core.Authentication;



@Service
public class ChangeRequestService {

    private final ChangeRequestRepository changeRequestRepository;
    private final DepartmentRepository departmentRepository;
    private final UserRepository userRepository;
    private final ChangeRequestStatusHistoryRepository statusHistoryRepository;

    public ChangeRequestService(
            ChangeRequestRepository changeRequestRepository,
            DepartmentRepository departmentRepository,
            UserRepository userRepository,
            ChangeRequestStatusHistoryRepository statusHistoryRepository) {

        this.changeRequestRepository = changeRequestRepository;
        this.departmentRepository = departmentRepository;
        this.userRepository = userRepository;
        this.statusHistoryRepository = statusHistoryRepository;
    }

    @Transactional
    @PreAuthorize("hasAuthority('CREATE_CHANGE_REQUEST')")
    public CRResponse create(
            CreateCRRequest request,
            String submittedByEmail) {

        User submittedBy = userRepository
                .findByEmail(submittedByEmail)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Authenticated user was not found"
                        )
                );

        Department department = departmentRepository
                .findById(request.assignedDepartmentId())
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Assigned department was not found"
                        )
                );

        ChangeRequest changeRequest = new ChangeRequest();

        changeRequest.setTitle(request.title());
        changeRequest.setDescription(request.description());
        changeRequest.setBusinessJustification(
                request.businessJustification()
        );
        changeRequest.setUrgency(request.urgency());
        changeRequest.setSubmittedBy(submittedBy);
        changeRequest.setAssignedDepartment(department);
        if (request.action() == CreateCRAction.SUBMIT) {
        changeRequest.setStatus(ChangeRequestStatus.SUBMITTED);
}

        ChangeRequest saved =
                changeRequestRepository.save(changeRequest);

        return toResponse(saved);
    }
    //View all change requests submitted by the authenticated user. Optionally filtering by status.
   @Transactional(readOnly = true)
   @PreAuthorize("hasAuthority('VIEW_SUBMITTED_REQUESTS')")
   public List<CRResponse> getMyRequests(
        String submittedByEmail,
        ChangeRequestStatus status) {

    List<ChangeRequest> requests;

    if (status == null) {
        requests =
                changeRequestRepository
                        .findBySubmittedBy_EmailOrderByCreatedAtDesc(
                                submittedByEmail
                        );
    } else {
        requests =
                changeRequestRepository
                        .findBySubmittedBy_EmailAndStatusOrderByCreatedAtDesc(
                                submittedByEmail,
                                status
                        );
    }

    return requests.stream()
            .map(this::toResponse)
            .toList();
    }
    //Retrieve a change request by its ID, ensuring that the user has the appropriate permissions to view it. If the request is in draft status, only the owner can view it. If the user is not the owner and does not have the 'VIEW_ALL_REQUESTS' authority, access is denied.
    @Transactional(readOnly = true)
    @PreAuthorize(
        "hasAnyAuthority('VIEW_SUBMITTED_REQUESTS', 'VIEW_ALL_REQUESTS','VIEW_ASSIGNED_REQUESTS')"
)
    public CRResponse getById(
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
    boolean isDraft =
            changeRequest.getStatus() == ChangeRequestStatus.DRAFT;

    if (isDraft && !isOwner) {
        throw new AccessDeniedException(
                "Draft requests are visible only to their owner"
        );
    }

    if (!isOwner && !canViewAllRequests && !isAssignedDeveloper) {
        throw new AccessDeniedException(
                "You do not have access to this change request"
        );
    }

    return toResponse(changeRequest);
}
   //Update a draft change request. Only the owner of the request can update it, and only if the request is still in draft status. If the request is submitted, it cannot be edited.
    @Transactional
    @PreAuthorize("hasAuthority('EDIT_CHANGE_REQUEST')")
        public CRResponse updateDraft(
        Long requestId,
        UpdateCRRequest request,
        String currentUserEmail) {

    ChangeRequest changeRequest = changeRequestRepository
            .findById(requestId)
            .orElseThrow(() ->
                    new ResourceNotFoundException(
                            "Change request was not found"
                    )
            );

    boolean isOwner = changeRequest
            .getSubmittedBy()
            .getEmail()
            .equals(currentUserEmail);

    if (!isOwner) {
        throw new AccessDeniedException(
                "You cannot edit another user's change request"
        );
    }

    if (changeRequest.getStatus() != ChangeRequestStatus.DRAFT) {
        throw new InvalidRequestStateException(
                "Only draft change requests can be edited"
        );
    }

    Department department = departmentRepository
            .findById(request.assignedDepartmentId())
            .orElseThrow(() ->
                    new ResourceNotFoundException(
                            "Assigned department was not found"
                    )
            );

    changeRequest.setTitle(request.title());
    changeRequest.setDescription(request.description());
    changeRequest.setBusinessJustification(
            request.businessJustification()
    );
    changeRequest.setUrgency(request.urgency());
    changeRequest.setAssignedDepartment(department);

    if (request.action() == CreateCRAction.SUBMIT) {
        changeRequest.setStatus(ChangeRequestStatus.SUBMITTED);
    }

    ChangeRequest saved =
            changeRequestRepository.save(changeRequest);

    return toResponse(saved);
}
//View all change requests that are not in draft status, regardless of user ownership. Restricted to users with "View all requests" authority.
        @Transactional(readOnly = true)
        @PreAuthorize("hasAuthority('VIEW_ALL_REQUESTS')")
        public List<CRResponse> getReviewQueue() {

        return changeRequestRepository
                .findByStatusNotOrderByCreatedAtDesc(
                        ChangeRequestStatus.DRAFT
                )
                .stream()
                .map(this::toResponse)
                .toList();
        }
//Update priority level of the request. Only applicable to authorized roles with "Update request priority" authority. The request must not be in draft.
        @Transactional
        @PreAuthorize("hasAuthority('UPDATE_REQUEST_PRIORITY')")
        public CRResponse updatePriority(Long requestId, UpdatePriorityRequest request) {
                ChangeRequest changeRequest = changeRequestRepository.findById(requestId)
                .orElseThrow(() -> new ResourceNotFoundException("Change request was not found"));
                if(changeRequest.getStatus() == ChangeRequestStatus.DRAFT) {
                    throw new InvalidRequestStateException("Cannot update priority of a draft change request");
                }
                changeRequest.setPriority(request.priority());
                ChangeRequest saved = changeRequestRepository.save(changeRequest);
                return toResponse(saved);
        }
//Update the status of a change request. Only applicable to authorized roles with "Update request status" authority.
        @Transactional
        @PreAuthorize("hasAuthority('UPDATE_REQUEST_STATUS')")
        public CRResponse updateStatus(
                Long requestId,
                ChangeRequestStatus newStatus,
                String changedByEmail) {

        ChangeRequest changeRequest = changeRequestRepository
                .findById(requestId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Change request was not found"
                        )
                );

        User changedBy = userRepository
                .findByEmail(changedByEmail)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Authenticated user was not found"
                        )
                );

        ChangeRequestStatus previousStatus =
                changeRequest.getStatus();

        if (previousStatus == ChangeRequestStatus.DRAFT) {
                throw new InvalidRequestStateException(
                        "A draft request must be submitted before its status can be updated"
                );
        }

        if (!isValidStatusTransition(previousStatus, newStatus)) {
                throw new InvalidRequestStateException(
                        "Cannot change status from "
                                + previousStatus
                                + " to "
                                + newStatus
                );
        }

        changeRequest.setStatus(newStatus);

        ChangeRequest saved =
                changeRequestRepository.save(changeRequest);

        recordStatusChange(
                saved,
                previousStatus,
                newStatus,
                changedBy
        );

        return toResponse(saved);
        }

//Retrieve the status change history of a specific change request. Only the owner of the request or users with "View all requests" authority can access this information.
        @Transactional(readOnly = true)
        @PreAuthorize(
                "hasAnyAuthority('VIEW_SUBMITTED_REQUESTS', 'VIEW_ALL_REQUESTS','VIEW_ASSIGNED_REQUESTS')"
        )
        public List<CRStatusHistoryResponse> getStatusHistory(
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

        boolean canViewAll = authentication
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

        if (!isOwner && !canViewAll && !isAssignedDeveloper) {
                throw new AccessDeniedException(
                        "You are not authorized to view this request's history"
                );
        }

        return statusHistoryRepository
                .findByChangeRequest_IdOrderByChangedAtDesc(requestId)
                .stream()
                .map(history ->
                        new CRStatusHistoryResponse(
                                history.getId(),
                                history.getPreviousStatus(),
                                history.getNewStatus(),
                                history.getChangedBy().getEmail(),
                                history.getChangedAt()
                        )
                )
                .toList();
        }
        @Transactional
        @PreAuthorize("hasAuthority('ASSIGN_DEVELOPER_TO_REQUEST')")
        public CRResponse assignDeveloper(
                Long requestId,
                AssignDeveloperRequest request,
                Authentication authentication) {

        ChangeRequest changeRequest = changeRequestRepository
                .findById(requestId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Change request was not found"
                        )
                );

        ChangeRequestStatus currentStatus =
                changeRequest.getStatus();

        boolean assignmentAllowed =
                currentStatus == ChangeRequestStatus.APPROVED
                || currentStatus
                        == ChangeRequestStatus.IMPLEMENTATION_PENDING;

        if (!assignmentAllowed) {
                throw new InvalidRequestStateException(
                        "A developer can only be assigned to an approved "
                                + "or implementation-pending request"
                );
        }

        User developer = userRepository
                .findById(request.developerId())
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Selected developer was not found"
                        )
                );

        boolean hasDeveloperRole = developer
                .getRoleAssignments()
                .stream()
                .anyMatch(roleAssignment ->
                        roleAssignment
                                .getRole()
                                .getName()
                                .equals("DEVELOPER")
                );

        if (!hasDeveloperRole) {
                throw new InvalidRequestStateException(
                        "The selected user does not have the DEVELOPER role"
                );
        }

        User assignedBy = userRepository
                .findByEmail(authentication.getName())
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Authenticated user was not found"
                        )
                );

        changeRequest.setAssignedDeveloper(developer);
        changeRequest.setAssignedBy(assignedBy);
        changeRequest.setAssignedAt(Instant.now());

        ChangeRequest saved =
                changeRequestRepository.save(changeRequest);

        return toResponse(saved);
        }
//Retrieve all change requests assigned to the authenticated developer. Restricted to users with "View assigned requests" authority.
        @Transactional(readOnly = true)
        @PreAuthorize("hasAuthority('VIEW_ASSIGNED_REQUESTS')")
        public List<CRResponse> getMyAssignedRequests(
                String developerEmail) {

        return changeRequestRepository
                .findByAssignedDeveloper_EmailOrderByUpdatedAtDesc(
                        developerEmail
                )
                .stream()
                .map(this::toResponse)
                .toList();
        }

        private boolean isValidStatusTransition(
                ChangeRequestStatus currentStatus,
                ChangeRequestStatus newStatus) {

        return switch (currentStatus) {

                case SUBMITTED ->
                        newStatus == ChangeRequestStatus.UNDER_REVIEW;

                case UNDER_REVIEW ->
                        newStatus == ChangeRequestStatus.APPROVED
                                || newStatus == ChangeRequestStatus.REJECTED;

                case APPROVED ->
                        newStatus == ChangeRequestStatus.IMPLEMENTATION_PENDING;

                case IMPLEMENTATION_PENDING ->
                        newStatus == ChangeRequestStatus.IMPLEMENTED;

                case IMPLEMENTED ->
                        newStatus == ChangeRequestStatus.CLOSED;

                case DRAFT, REJECTED, CLOSED -> false;
        };
        }
        private void recordStatusChange(
                ChangeRequest changeRequest,
                ChangeRequestStatus previousStatus,
                ChangeRequestStatus newStatus,
                User changedBy) {

        ChangeRequestStatusHistory history =
                new ChangeRequestStatusHistory();

        history.setChangeRequest(changeRequest);
        history.setPreviousStatus(previousStatus);
        history.setNewStatus(newStatus);
        history.setChangedBy(changedBy);

        statusHistoryRepository.save(history);
        }
    private CRResponse toResponse(ChangeRequest changeRequest){
        return new CRResponse(
            changeRequest.getId(),
            changeRequest.getTitle(),
            changeRequest.getDescription(),
            changeRequest.getBusinessJustification(),
            changeRequest.getStatus(),
            changeRequest.getUrgency(),
            changeRequest.getPriority(),
            changeRequest.getSubmittedBy().getEmail(),
            changeRequest.getAssignedDepartment().getId(),
            changeRequest.getAssignedDepartment().getName(),
            changeRequest.getCreatedAt(),
            changeRequest.getAssignedDeveloper() != null
                    ? changeRequest.getAssignedDeveloper().getId()
                    : null,

            changeRequest.getAssignedDeveloper() != null
                    ? changeRequest.getAssignedDeveloper().getEmail()
                    : null,

            changeRequest.getAssignedBy() != null
                    ? changeRequest.getAssignedBy().getEmail()
                    : null,
            changeRequest.getAssignedAt()
        );
    }
    
}