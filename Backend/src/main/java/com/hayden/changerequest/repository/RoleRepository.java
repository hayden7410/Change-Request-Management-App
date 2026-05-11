package com.hayden.changerequest.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.hayden.changerequest.entity.Role;

public interface RoleRepository extends JpaRepository<Role, Long> {
}
