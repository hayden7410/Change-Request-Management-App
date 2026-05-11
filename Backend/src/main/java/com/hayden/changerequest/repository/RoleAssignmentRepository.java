package com.hayden.changerequest.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.hayden.changerequest.entity.RoleAssignment;

public interface RoleAssignmentRepository extends JpaRepository<RoleAssignment, Long> {
}
