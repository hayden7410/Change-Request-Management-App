package com.hayden.changerequest.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.hayden.changerequest.entity.RolePermission;

public interface RolePermissionRepository extends JpaRepository<RolePermission, Long> {
}
