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
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestParam;
import com.hayden.changerequest.dto.ChangeRequest.CRResponse;
import com.hayden.changerequest.dto.ChangeRequest.CreateCRRequest;
import com.hayden.changerequest.service.ChangeRequestService;
import com.hayden.changerequest.common.enums.ChangeRequestStatus;

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
}