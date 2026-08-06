package com.hayden.changerequest.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestParam;

import com.hayden.changerequest.dto.Assignment.AssignDeveloperRequest;
import com.hayden.changerequest.dto.ChangeRequest.CRResponse;
import com.hayden.changerequest.dto.ChangeRequest.CRStatusHistoryResponse;
import com.hayden.changerequest.dto.ChangeRequest.CreateCRRequest;
import com.hayden.changerequest.service.ChangeRequestService;
import com.hayden.changerequest.common.enums.ChangeRequestStatus;
import com.hayden.changerequest.dto.ChangeRequest.UpdateCRRequest;
import com.hayden.changerequest.dto.ChangeRequest.UpdatePriorityRequest;
import com.hayden.changerequest.dto.ChangeRequest.UpdateStatusRequest;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/change-requests")
public class ChangeRequestController {

    private final ChangeRequestService changeRequestService;

    public ChangeRequestController(
            ChangeRequestService changeRequestService) {

        this.changeRequestService = changeRequestService;
    }

    @PostMapping
    public ResponseEntity<CRResponse> createChangeRequest(
            @Valid @RequestBody CreateCRRequest request,
            Authentication authentication) {

        String submittedByEmail = authentication.getName();

        CRResponse response = changeRequestService.create(
                request,
                submittedByEmail
        );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

   @GetMapping("/mine")
public ResponseEntity<List<CRResponse>> getMySubmittedRequests(
        @RequestParam(required = false) ChangeRequestStatus status,
        Authentication authentication) {

    String submittedByEmail = authentication.getName();

    List<CRResponse> responses =
            changeRequestService.getMyRequests(
                    submittedByEmail
                        , status
            );

    return ResponseEntity.ok(responses);
}
 @GetMapping("/{id}")
 public ResponseEntity<CRResponse> getChangeRequestById(
        @PathVariable Long id,
        Authentication authentication) {


    CRResponse response = changeRequestService.getById(
            id,
           authentication);

    return ResponseEntity.ok(response);
}
@PatchMapping("/{id}")
public ResponseEntity<CRResponse> updateDraft(
        @PathVariable Long id,
        @Valid @RequestBody UpdateCRRequest request,
        Authentication authentication) {

    String currentUserEmail = authentication.getName();

    CRResponse response = changeRequestService.updateDraft(
            id,
            request,
            currentUserEmail
    );

    return ResponseEntity.ok(response);
}
@GetMapping("/review")
public ResponseEntity<List<CRResponse>> viewReviewQueue() {

    List<CRResponse> responses =
            changeRequestService.getReviewQueue();

    return ResponseEntity.ok(responses);
}
@PatchMapping("/{id}/priority")
public ResponseEntity<CRResponse> updatePriority(
        @PathVariable Long id,
        @Valid @RequestBody UpdatePriorityRequest request) {

    CRResponse response = changeRequestService.updatePriority(id, request);

    return ResponseEntity.ok(response);}
@PatchMapping("/{id}/status")
public ResponseEntity<CRResponse> updateStatus(
        @PathVariable Long id,
        @Valid @RequestBody UpdateStatusRequest request,
        Authentication authentication) {

    CRResponse response = changeRequestService.updateStatus(id, request.status(), authentication.getName());

    return ResponseEntity.ok(response);
}
@GetMapping("/{id}/history")
public ResponseEntity<List<CRStatusHistoryResponse>> getStatusHistory(
        @PathVariable("id") Long id,
        Authentication authentication) {

    List<CRStatusHistoryResponse> response =
            changeRequestService.getStatusHistory(
                    id,
                    authentication
            );

    return ResponseEntity.ok(response);
}
@GetMapping("/assigned-to-me")
public ResponseEntity<List<CRResponse>> getMyAssignedRequests(
        Authentication authentication) {

    List<CRResponse> responses =
            changeRequestService.getMyAssignedRequests(
                    authentication.getName()
            );

    return ResponseEntity.ok(responses);
}
@PatchMapping("/{id}/assign-developer")
public ResponseEntity<CRResponse> assignDeveloper(
        @PathVariable("id") Long requestId,
        @Valid @RequestBody AssignDeveloperRequest request,
        Authentication authentication) {

    CRResponse response =
            changeRequestService.assignDeveloper(
                    requestId,
                    request,
                    authentication
            );

    return ResponseEntity.ok(response);
}
}