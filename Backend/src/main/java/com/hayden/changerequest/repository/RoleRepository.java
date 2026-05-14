package com.hayden.changerequest.repository;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.hayden.changerequest.entity.Role;


public interface RoleRepository extends JpaRepository<Role, Long> {
    @Query("""
        SELECT DISTINCT r
        FROM Role r
        LEFT JOIN FETCH r.rolePermissions rp
        LEFT JOIN FETCH rp.permission
        WHERE r.id = :id
    """)
    Optional<Role> findByIdWithPermissions(@Param("id") Long id);
}
