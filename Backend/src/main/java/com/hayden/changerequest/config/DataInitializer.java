package com.hayden.changerequest.config;
import java.util.List;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import com.hayden.changerequest.repository.DepartmentRepository;
import com.hayden.changerequest.entity.Department;
import com.hayden.changerequest.repository.RoleRepository;
import com.hayden.changerequest.entity.Role;
import com.hayden.changerequest.repository.PermissionRepository;
import com.hayden.changerequest.entity.Permission;
import java.util.Set;
import java.util.stream.Collectors;
@Configuration
public class DataInitializer {
    private record RoleSeed(String name, String description) {}
    private record PermissionSeed(String name, String description) {}
@Bean

    public CommandLineRunner seedData(DepartmentRepository departmentRepository, RoleRepository roleRepository, PermissionRepository permissionRepository){
        return arg -> {
            if(!departmentRepository.existsByName("IT")){
                Department itDepartment = new Department();
                itDepartment.setName("IT");
                itDepartment.setDescription("Information Technology Department");
                departmentRepository.save(itDepartment);
            }
            if(!departmentRepository.existsByName("HR")){
                Department hrDepartment = new Department();
                hrDepartment.setName("HR");
                hrDepartment.setDescription("Human Resources Department");
                departmentRepository.save(hrDepartment);
            }
            List<RoleSeed> rolesSeed = List.of(
                new RoleSeed("BUSINESS_ANALYST", "Business Analyst with permissions to manage change requests and view reports"),
                new RoleSeed("SYSTEM_ADMIN", "System Administrator with full permissions to manage the application"),
                new RoleSeed("DEVELOPER", "Developer with permissions to manage change requests and view reports"),
                new RoleSeed("PROJECT_MANAGER", "Project Manager with permissions to manage change requests and view reports"),
                new RoleSeed("SOLUTION_ARCHITECT", "Solution Architect with permissions to design and approve change requests")
            );
            List<String> roleNames = rolesSeed.stream().map(RoleSeed::name).toList();
            Set<String> existingRoleNames = roleRepository.findByNameIn(roleNames).stream().map(Role::getName).collect(Collectors.toSet());
            List<Role> rolesToCreate = rolesSeed.stream()
                .filter(seed -> !existingRoleNames.contains(seed.name))
                .map(seed -> {
                    Role role = new Role();
                    role.setName(seed.name);
                    role.setDescription(seed.description);
                    return role;
                })
                .toList();
            if(!rolesToCreate.isEmpty()) {
                roleRepository.saveAll(rolesToCreate);
            }

            List<PermissionSeed> permissionsSeed = List.of(
                new PermissionSeed("CREATE_CHANGE_REQUEST", "Permission to create change requests"),
                new PermissionSeed("EDIT_CHANGE_REQUEST", "Permission to update change requests"),
                new PermissionSeed("DELETE_OWN_REQUEST", "Permission to delete owner's change requests"),
                new PermissionSeed("COMMENT_ON_REQUEST", "Permission to comment on change requests"),
                new PermissionSeed("VIEW_OWN_REQUEST", "Permission to view owner's change requests"),
                new PermissionSeed("VIEW_ALL_REQUESTS", "Permission to view all change requests"),
                new PermissionSeed("UPLOAD_ATTACHMENT", "Permission to upload attachments to change requests"),
                new PermissionSeed("UPDATE_REQUEST_STATUS", "Permission to update the status of change requests"),
                new PermissionSeed("APPROVE_REQUEST", "Permission to approve change requests"),
                new PermissionSeed("REJECT_REQUEST", "Permission to reject change requests"),
                new PermissionSeed("ASSIGN_REQUEST", "Permission to assign change requests to users"),
                new PermissionSeed("GENERATE_AI_SUMMARY", "Permission to generate AI summaries for change requests"),
                new PermissionSeed("APPROVE_AI_SUMMARY", "Permission to approve AI summaries for change requests"),
                new PermissionSeed("MANAGE_USERS", "Permission to manage user accounts and roles"),
                new PermissionSeed("MANAGE_ROLES", "Permission to manage roles and their permissions"),
                new PermissionSeed("MANAGE_PERMISSIONS", "Permission to manage permissions")
            );
            List<String> permissionNames = permissionsSeed.stream().map(PermissionSeed::name).toList();
            Set<String> existingPermissionNames = permissionRepository.findByNameIn(permissionNames).stream().map(Permission::getName).collect(Collectors.toSet());
            List<Permission> permissionsToCreate = permissionsSeed.stream()
                .filter(seed -> !existingPermissionNames.contains(seed.name))
                .map(seed -> {
                    Permission permission = new Permission();
                    permission.setName(seed.name);
                    permission.setDescription(seed.description);
                    return permission;
                })
                .toList();
            if(!permissionsToCreate.isEmpty()) {
                permissionRepository.saveAll(permissionsToCreate);
            }

        };
    }
}
