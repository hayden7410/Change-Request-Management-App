package com.hayden.changerequest.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.hayden.changerequest.entity.User;

public interface UserRepository extends JpaRepository<User, Long> {
}
