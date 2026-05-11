package com.hayden.changerequest.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.hayden.changerequest.entity.Department;

public interface DepartmentRepository extends JpaRepository<Department, Long> {
}
