package com.hayden.changerequest.security;

import java.util.List;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hayden.changerequest.entity.RoleAssignment;
import com.hayden.changerequest.entity.User;
import com.hayden.changerequest.repository.UserRepository;

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

        List<SimpleGrantedAuthority> authorities =
                user.getRoleAssignments()
                        .stream()
                        .filter(RoleAssignment::isActive)
                        .map(roleAssignment ->
                                roleAssignment.getRole().getName()
                        )
                        .map(roleName ->
                                new SimpleGrantedAuthority(
                                        "ROLE_" + roleName
                                )
                        )
                        .distinct()
                        .toList();

        return org.springframework.security.core.userdetails.User
                .withUsername(user.getEmail())
                .password(user.getPasswordHash())
                .authorities(authorities)
                .disabled(!user.isActive())
                .build();
    }
}