package com.hayden.changerequest.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.hayden.changerequest.entity.Permission;

public interface PermissionRepository extends JpaRepository<Permission, Long> {
}
