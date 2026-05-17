package com.hayden.changerequest.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.Collection;
import java.util.List;
import com.hayden.changerequest.entity.RolePermission;

public interface RolePermissionRepository extends JpaRepository<RolePermission, Long> {
    boolean existsByRoleNameAndPermissionName(String roleName, String permissionName);
    @Query("""
    SELECT rp
    FROM RolePermission rp
    JOIN FETCH rp.role r
    JOIN FETCH rp.permission p
    WHERE r.name IN :roleNames
      AND p.name IN :permissionNames
""")
List<RolePermission> findExistingPairs(
    @Param("roleNames") Collection<String> roleNames,
    @Param("permissionNames") Collection<String> permissionNames
);
}
