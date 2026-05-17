package com.hayden.changerequest.config;
import java.util.List;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.hayden.changerequest.repository.DepartmentRepository;
import com.hayden.changerequest.entity.Department;
import com.hayden.changerequest.repository.RoleRepository;
import com.hayden.changerequest.entity.Role;
import com.hayden.changerequest.entity.RoleAssignment;
import com.hayden.changerequest.entity.RolePermission;
import com.hayden.changerequest.repository.PermissionRepository;
import com.hayden.changerequest.entity.Permission;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.Map;
import com.hayden.changerequest.repository.RolePermissionRepository;
import com.hayden.changerequest.repository.RoleAssignmentRepository;
import com.hayden.changerequest.repository.UserRepository;
import com.hayden.changerequest.entity.User;
@Configuration
public class DataInitializer {
    private record RoleSeed(String name, String description) {}
    private record PermissionSeed(String name, String description) {}
    private record RolePermissionSeed(String roleName, String permissionName) {}
    private record UserSeed(String email, String firstName, String lastName, String password, String departmentName, String roleName) {}
@Bean

    public CommandLineRunner seedData(DepartmentRepository departmentRepository, RoleRepository roleRepository, PermissionRepository permissionRepository, RolePermissionRepository rolePermissionRepository, RoleAssignmentRepository roleAssignmentRepository, PasswordEncoder passwordEncoder, UserRepository userRepository) {
        return arg -> {
            //Seed departments data
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
            //Seed roles data
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
           
            //Seed permissions data
            List<PermissionSeed> permissionsSeed = List.of(
                new PermissionSeed("CREATE_CHANGE_REQUEST", "Permission to create change requests"),
                new PermissionSeed("EDIT_CHANGE_REQUEST", "Permission to update change requests"),
                new PermissionSeed("DELETE_SUBMITTED_REQUEST", "Permission to delete submitted change requests"),
                new PermissionSeed("COMMENT_ON_REQUEST", "Permission to comment on change requests"),
                new PermissionSeed("VIEW_SUBMITTED_REQUESTS", "Permission to view submitted change requests"),
                new PermissionSeed("VIEW_ASSIGNED_REQUESTS", "Permission to view assigned change requests"),
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
            //Seed role_permissions data
            List<RolePermissionSeed> rolePermissionsSeed = List.of(
                new RolePermissionSeed("BUSINESS_ANALYST", "CREATE_CHANGE_REQUEST"),
                new RolePermissionSeed("BUSINESS_ANALYST", "EDIT_CHANGE_REQUEST"),
                new RolePermissionSeed("BUSINESS_ANALYST", "DELETE_SUBMITTED_REQUEST"),
                new RolePermissionSeed("BUSINESS_ANALYST", "COMMENT_ON_REQUEST"),
                new RolePermissionSeed("BUSINESS_ANALYST", "VIEW_SUBMITTED_REQUESTS"),
                new RolePermissionSeed("BUSINESS_ANALYST", "UPLOAD_ATTACHMENT"),
                new RolePermissionSeed("BUSINESS_ANALYST", "GENERATE_AI_SUMMARY"),
                new RolePermissionSeed("BUSINESS_ANALYST", "APPROVE_AI_SUMMARY"),
                new RolePermissionSeed("SYSTEM_ADMIN", "VIEW_ALL_REQUESTS"),
                new RolePermissionSeed("SYSTEM_ADMIN", "MANAGE_USERS"),
                new RolePermissionSeed("SYSTEM_ADMIN", "MANAGE_ROLES"),
                new RolePermissionSeed("SYSTEM_ADMIN", "MANAGE_PERMISSIONS"),
                new RolePermissionSeed("DEVELOPER", "UPDATE_REQUEST_STATUS"),
                new RolePermissionSeed("DEVELOPER", "VIEW_ASSIGNED_REQUESTS"),
                new RolePermissionSeed("DEVELOPER", "UPLOAD_ATTACHMENT"),
                new RolePermissionSeed("DEVELOPER", "COMMENT_ON_REQUEST"),
                new RolePermissionSeed("PROJECT_MANAGER", "VIEW_ALL_REQUESTS"),
                new RolePermissionSeed("PROJECT_MANAGER", "COMMENT_ON_REQUEST"),
                new RolePermissionSeed("PROJECT_MANAGER", "UPDATE_REQUEST_STATUS"),
                new RolePermissionSeed("PROJECT_MANAGER", "ASSIGN_REQUEST"),
                new RolePermissionSeed("PROJECT_MANAGER", "APPROVE_REQUEST"),  
                new RolePermissionSeed("PROJECT_MANAGER", "REJECT_REQUEST"),                             
                new RolePermissionSeed("SOLUTION_ARCHITECT", "VIEW_ALL_REQUESTS"),
                new RolePermissionSeed("SOLUTION_ARCHITECT", "COMMENT_ON_REQUEST"),
                new RolePermissionSeed("SOLUTION_ARCHITECT", "APPROVE_REQUEST"),
                new RolePermissionSeed("SOLUTION_ARCHITECT", "REJECT_REQUEST"),
                new RolePermissionSeed("SOLUTION_ARCHITECT", "GENERATE_AI_SUMMARY"),
                new RolePermissionSeed("SOLUTION_ARCHITECT", "APPROVE_AI_SUMMARY"),
                new RolePermissionSeed("SOLUTION_ARCHITECT", "UPDATE_REQUEST_STATUS"));

                Set<String> roleRPNames = rolePermissionsSeed.stream()
                                .map(RolePermissionSeed::roleName)
                                .collect(Collectors.toSet());

                Set<String> permissionRPNames = rolePermissionsSeed.stream()
                                .map(RolePermissionSeed::permissionName)
                                .collect(Collectors.toSet());
                Map<String, Role> rolesByName = roleRepository.findByNameIn(roleNames)
                    .stream()
                    .collect(Collectors.toMap(Role::getName, role -> role));

                Map<String, Permission> permissionsByName = permissionRepository.findByNameIn(permissionNames)
                    .stream()
                    .collect(Collectors.toMap(Permission::getName, permission -> permission));
                Set<RolePermissionSeed> existingPairs = rolePermissionRepository
                    .findExistingPairs(roleRPNames, permissionRPNames)
                    .stream()
                    .map(rp -> new RolePermissionSeed(
                        rp.getRole().getName(),
                        rp.getPermission().getName()
                    ))
                    .collect(Collectors.toSet());       
                List<RolePermission> rolePermissionsToCreate = rolePermissionsSeed.stream()
                    .filter(seed -> !existingPairs.contains(seed))
                    .map(seed -> {
                        Role role = rolesByName.get(seed.roleName());
                        Permission permission = permissionsByName.get(seed.permissionName());

                        if (role == null) {
                            throw new RuntimeException("Role not found: " + seed.roleName());
                        }

                        if (permission == null) {
                            throw new RuntimeException("Permission not found: " + seed.permissionName());
                        }

                        RolePermission rolePermission = new RolePermission();
                        rolePermission.setRole(role);
                        rolePermission.setPermission(permission);

                        return rolePermission;
                    })
                    .toList();

                if (!rolePermissionsToCreate.isEmpty()) {
                    rolePermissionRepository.saveAll(rolePermissionsToCreate);
                }                   
                List<UserSeed> userSeeds = List.of(
                    new UserSeed(
                        "ba@example.com",
                        "Business",
                        "Analyst",
                        "password123",
                        "IT",
                        "BUSINESS_ANALYST"
                    ),
                    new UserSeed(
                        "developer@example.com",
                        "Dev",
                        "User",
                        "password123",
                        "IT",
                        "DEVELOPER"
                    ),
                    new UserSeed(
                        "pm@example.com",
                        "Project",
                        "Manager",
                        "password123",
                        "HR",
                        "PROJECT_MANAGER"
                    ),
                    new UserSeed(
                        "architect@example.com",
                        "Solution",
                        "Architect",
                        "password123",
                        "IT",
                        "SOLUTION_ARCHITECT"
                    ),
                    new UserSeed(
                        "admin@example.com",
                        "System",
                        "Admin",
                        "password123",
                        "IT",
                        "SYSTEM_ADMIN"
                    )
                ); 
            for (UserSeed seed : userSeeds) {

                User user = userRepository.findByEmail(seed.email())
                    .orElseGet(() -> {
                        Department department = departmentRepository.findByName(seed.departmentName())
                            .orElseThrow(() -> new RuntimeException("Department not found: " + seed.departmentName()));

                        User newUser = new User();
                        newUser.setEmail(seed.email());
                        newUser.setPasswordHash(passwordEncoder.encode(seed.password()));
                        newUser.setFirstName(seed.firstName());
                        newUser.setLastName(seed.lastName());
                        newUser.setDepartment(department);
                        newUser.setActive(true);

                        return userRepository.save(newUser);
                    });

                Role role = roleRepository.findByName(seed.roleName())
                    .orElseThrow(() -> new RuntimeException("Role not found: " + seed.roleName()));

                boolean assignmentExists = roleAssignmentRepository.existsByUserIdAndRoleId(
                    user.getId(),
                    role.getId()
                );

                if (!assignmentExists) {
                    RoleAssignment roleAssignment = new RoleAssignment();
                    roleAssignment.setUser(user);
                    roleAssignment.setRole(role);
                    roleAssignment.setActive(true);

                    roleAssignmentRepository.save(roleAssignment);
                }
            }               
                
            };
    }
}
