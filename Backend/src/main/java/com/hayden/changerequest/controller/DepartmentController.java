package com.hayden.changerequest.controller;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.hayden.changerequest.dto.DepartmentResponse;
import com.hayden.changerequest.repository.DepartmentRepository;

@RestController
@RequestMapping("/api/departments")
public class DepartmentController {
    private final DepartmentRepository departmentRepository;

    public DepartmentController(
            DepartmentRepository departmentRepository) {

        this.departmentRepository = departmentRepository;
    }

    @GetMapping
    public ResponseEntity<List<DepartmentResponse>> getDepartments() {

        List<DepartmentResponse> departments =
                departmentRepository.findAll()
                        .stream()
                        .map(department ->
                                new DepartmentResponse(
                                        department.getId(),
                                        department.getName()
                                )
                        )
                        .toList();

        return ResponseEntity.ok(departments);
    }
}
