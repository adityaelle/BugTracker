package com.aurionpro.controller;

import com.aurionpro.dto.request.RegisterRequest;
import com.aurionpro.dto.response.ApiResponse;
import com.aurionpro.dto.response.UserResponse;
import com.aurionpro.entity.User;
import com.aurionpro.entity.enums.UserRole;
import com.aurionpro.exception.ResourceNotFoundException;
import com.aurionpro.mapper.TicketMapper;
import com.aurionpro.repository.UserRepository;
import com.aurionpro.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {

    private final UserRepository userRepository;
    private final AuthService authService;
    private final TicketMapper ticketMapper;

    @GetMapping("/users")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<ApiResponse<List<UserResponse>>> getAllUsers() {
        List<UserResponse> users = userRepository.findAll()
                .stream()
                .map(ticketMapper::toUserResponse)
                .toList();
        return ResponseEntity.ok(ApiResponse.success("Users fetched", users));
    }

    @PostMapping("/users")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<ApiResponse<UserResponse>> createUser(
            @Valid @RequestBody RegisterRequest request) {
        authService.register(request);
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        return ResponseEntity.ok(
                ApiResponse.success("User created", ticketMapper.toUserResponse(user)));
    }

    @PatchMapping("/users/{userId}/toggle-active")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<ApiResponse<UserResponse>> toggleUserActive(
            @PathVariable Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        user.setActive(!user.isActive());
        userRepository.save(user);
        return ResponseEntity.ok(
                ApiResponse.success("User status updated", ticketMapper.toUserResponse(user)));
    }

    @GetMapping("/users/role/{role}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<ApiResponse<List<UserResponse>>> getUsersByRole(
            @PathVariable UserRole role) {
        List<UserResponse> users = userRepository.findByRole(role)
                .stream()
                .map(ticketMapper::toUserResponse)
                .toList();
        return ResponseEntity.ok(ApiResponse.success("Users fetched", users));
    }

    @GetMapping("/assignable-users")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_SUPPORT_DESK', 'ROLE_TEAM_LEAD')")
    public ResponseEntity<ApiResponse<List<UserResponse>>> getAssignableUsers() {
        List<UserRole> assignableRoles = List.of(
                UserRole.DEVELOPER, UserRole.QA_TESTER, UserRole.TEAM_LEAD);
        List<UserResponse> users = userRepository.findByRoleInAndIsActiveTrue(assignableRoles)
                .stream()
                .map(ticketMapper::toUserResponse)
                .toList();
        return ResponseEntity.ok(ApiResponse.success("Assignable users fetched", users));
    }
}