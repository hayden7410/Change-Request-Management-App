package com.hayden.changerequest.service;

import java.util.List;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hayden.changerequest.dto.ChangeRequest.CRResponse;
import com.hayden.changerequest.dto.ChangeRequest.CreateCRRequest;
import com.hayden.changerequest.entity.ChangeRequest;
import com.hayden.changerequest.entity.Department;
import com.hayden.changerequest.entity.User;
import com.hayden.changerequest.repository.ChangeRequestRepository;
import com.hayden.changerequest.repository.DepartmentRepository;
import com.hayden.changerequest.repository.UserRepository;

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

        ChangeRequest saved =
                changeRequestRepository.save(changeRequest);

        return toResponse(saved);
    }
    @Transactional(readOnly = true)
    @PreAuthorize("hasAuthority('VIEW_SUBMITTED_REQUESTS')")
    public List<CRResponse> getSubmittedRequests(String submittedByEmail){
        return changeRequestRepository.findBySubmittedBy_EmailOrderByCreatedAtDesc(submittedByEmail)
        .stream()
        .map(this::toResponse)
        .toList();
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