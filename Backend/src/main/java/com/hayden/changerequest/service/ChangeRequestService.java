package com.hayden.changerequest.service;

import java.util.List;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.security.access.AccessDeniedException;

import com.hayden.changerequest.dto.ChangeRequest.CRResponse;
import com.hayden.changerequest.dto.ChangeRequest.CreateCRRequest;
import com.hayden.changerequest.entity.ChangeRequest;
import com.hayden.changerequest.entity.Department;
import com.hayden.changerequest.entity.User;
import com.hayden.changerequest.repository.ChangeRequestRepository;
import com.hayden.changerequest.repository.DepartmentRepository;
import com.hayden.changerequest.repository.UserRepository;
import com.hayden.changerequest.common.exception.ResourceNotFoundException;
import com.hayden.changerequest.common.enums.ChangeRequestStatus;
import com.hayden.changerequest.common.enums.CreateCRAction;
import com.hayden.changerequest.dto.ChangeRequest.UpdateCRRequest;
import com.hayden.changerequest.common.exception.InvalidRequestStateException;

import org.springframework.security.core.Authentication;



@Service
public class ChangeRequestService {

    private final ChangeRequestRepository changeRequestRepository;
    private final DepartmentRepository departmentRepository;
    private final UserRepository userRepository;

    public ChangeRequestService(
            ChangeRequestRepository changeRequestRepository,
            DepartmentRepository departmentRepository,
            UserRepository userRepository) {

        this.changeRequestRepository = changeRequestRepository;
        this.departmentRepository = departmentRepository;
        this.userRepository = userRepository;
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
    @Transactional(readOnly = true)
    @PreAuthorize(
        "hasAnyAuthority('VIEW_SUBMITTED_REQUESTS', 'VIEW_ALL_REQUESTS')"
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

    boolean isDraft =
            changeRequest.getStatus() == ChangeRequestStatus.DRAFT;

    if (isDraft && !isOwner) {
        throw new AccessDeniedException(
                "Draft requests are visible only to their owner"
        );
    }

    if (!isOwner && !canViewAllRequests) {
        throw new AccessDeniedException(
                "You do not have access to this change request"
        );
    }

    return toResponse(changeRequest);
}
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
            changeRequest.getCreatedAt()
        );
    }
    
}