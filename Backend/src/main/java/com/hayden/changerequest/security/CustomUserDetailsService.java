package com.hayden.changerequest.security;

import java.util.HashSet;

import java.util.Set;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hayden.changerequest.entity.RoleAssignment;
import com.hayden.changerequest.entity.User;
import com.hayden.changerequest.repository.UserRepository;
import com.hayden.changerequest.entity.Role;


@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String email)
            throws UsernameNotFoundException {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new UsernameNotFoundException(
                                "User not found with email: " + email
                        )
                );

        Set<GrantedAuthority> authorities = new HashSet<>();

        for (RoleAssignment roleAssignment : user.getRoleAssignments()) {

            if (!roleAssignment.isActive()) {
                continue;
            }

            Role role = roleAssignment.getRole();

            // Add the role.
            authorities.add(
                    new SimpleGrantedAuthority(
                            "ROLE_" + role.getName()
                    )
            );

            // Add every permission belonging to the role.
            role.getRolePermissions()
                    .forEach(rolePermission -> {

                        String permissionName =
                                rolePermission
                                        .getPermission()
                                        .getName();

                        authorities.add(
                                new SimpleGrantedAuthority(
                                        permissionName
                                )
                        );
                    });
        }

        return org.springframework.security.core.userdetails.User
                .withUsername(user.getEmail())
                .password(user.getPasswordHash())
                .authorities(authorities)
                .disabled(!user.isActive())
                .build();
    }
}