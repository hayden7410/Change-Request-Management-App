package com.hayden.changerequest.repository;

import java.lang.foreign.Linker.Option;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

import com.hayden.changerequest.entity.User;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
}
