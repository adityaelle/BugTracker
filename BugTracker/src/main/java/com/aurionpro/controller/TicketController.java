package com.aurionpro.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.aurionpro.dto.request.AddCommentRequest;
import com.aurionpro.dto.request.AssignTicketRequest;
import com.aurionpro.dto.request.CreateTicketRequest;
import com.aurionpro.dto.request.UpdateTicketStatusRequest;
import com.aurionpro.dto.response.ApiResponse;
import com.aurionpro.dto.response.DashboardResponse;
import com.aurionpro.dto.response.TicketCommentResponse;
import com.aurionpro.dto.response.TicketDetailResponse;
import com.aurionpro.dto.response.TicketResponse;
import com.aurionpro.dto.response.UserResponse;
import com.aurionpro.entity.enums.TicketStatus;
import com.aurionpro.entity.enums.UserRole;
import com.aurionpro.mapper.TicketMapper;
import com.aurionpro.repository.UserRepository;
import com.aurionpro.service.TicketService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/tickets")
@RequiredArgsConstructor
public class TicketController {

    private final TicketService ticketService;
    private final UserRepository userRepository;
    private final TicketMapper ticketMapper;

    // ─── Client ──────────────────────────────────────────────────────────────

    @PostMapping
    @PreAuthorize("hasRole('CLIENT')")
    public ResponseEntity<ApiResponse<TicketResponse>> createTicket(
            @Valid @RequestBody CreateTicketRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        TicketResponse response = ticketService.createTicket(request, userDetails.getUsername());
        return ResponseEntity.ok(ApiResponse.success("Ticket created successfully", response));
    }

    @GetMapping("/my")
    @PreAuthorize("hasRole('CLIENT')")
    public ResponseEntity<ApiResponse<List<TicketResponse>>> getMyTickets(
            @AuthenticationPrincipal UserDetails userDetails) {
        List<TicketResponse> tickets = ticketService.getTicketsForClient(userDetails.getUsername());
        return ResponseEntity.ok(ApiResponse.success("Tickets fetched", tickets));
    }

    @PatchMapping("/{ticketId}/close")
    @PreAuthorize("hasRole('CLIENT')")
    public ResponseEntity<ApiResponse<TicketResponse>> closeTicket(
            @PathVariable Long ticketId,
            @AuthenticationPrincipal UserDetails userDetails) {
        TicketResponse response = ticketService.closeTicket(ticketId, userDetails.getUsername());
        return ResponseEntity.ok(ApiResponse.success("Ticket closed", response));
    }

    @PatchMapping("/{ticketId}/reopen")
    @PreAuthorize("hasRole('CLIENT')")
    public ResponseEntity<ApiResponse<TicketResponse>> reopenTicket(
            @PathVariable Long ticketId,
            @Valid @RequestBody AddCommentRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        TicketResponse response = ticketService.reopenTicket(
                ticketId, request, userDetails.getUsername());
        return ResponseEntity.ok(ApiResponse.success("Ticket reopened", response));
    }

    // ─── Support Desk ─────────────────────────────────────────────────────────

    @PatchMapping("/{ticketId}/assign")
    @PreAuthorize("hasRole('SUPPORT_DESK')")
    public ResponseEntity<ApiResponse<TicketResponse>> assignTicket(
            @PathVariable Long ticketId,
            @Valid @RequestBody AssignTicketRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        TicketResponse response = ticketService.assignTicket(
                ticketId, request, userDetails.getUsername());
        return ResponseEntity.ok(ApiResponse.success("Ticket assigned", response));
    }

    // ─── Developer ────────────────────────────────────────────────────────────

    @GetMapping("/assigned")
    @PreAuthorize("hasAnyRole('DEVELOPER','QA_TESTER','TEAM_LEAD')")
    public ResponseEntity<ApiResponse<List<TicketResponse>>> getAssignedTickets(
            @AuthenticationPrincipal UserDetails userDetails) {
        List<TicketResponse> tickets =
                ticketService.getTicketsForDeveloper(userDetails.getUsername());
        return ResponseEntity.ok(ApiResponse.success("Assigned tickets fetched", tickets));
    }

    @PatchMapping("/{ticketId}/status")
    @PreAuthorize("hasAnyRole('DEVELOPER','TEAM_LEAD','SUPPORT_DESK')")
    public ResponseEntity<ApiResponse<TicketResponse>> updateStatus(
            @PathVariable Long ticketId,
            @Valid @RequestBody UpdateTicketStatusRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        TicketResponse response = ticketService.updateTicketStatus(
                ticketId, request, userDetails.getUsername());
        return ResponseEntity.ok(ApiResponse.success("Status updated", response));
    }

    // ─── QA ───────────────────────────────────────────────────────────────────

    @PatchMapping("/{ticketId}/qa-verdict")
    @PreAuthorize("hasRole('QA_TESTER')")
    public ResponseEntity<ApiResponse<TicketResponse>> qaVerdict(
            @PathVariable Long ticketId,
            @Valid @RequestBody UpdateTicketStatusRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        TicketResponse response = ticketService.qaVerdict(
                ticketId, request, userDetails.getUsername());
        return ResponseEntity.ok(ApiResponse.success("QA verdict recorded", response));
    }

    // ─── Comments ─────────────────────────────────────────────────────────────

    @PostMapping("/{ticketId}/comments")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<TicketCommentResponse>> addComment(
            @PathVariable Long ticketId,
            @Valid @RequestBody AddCommentRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        TicketCommentResponse response = ticketService.addComment(
                ticketId, request, userDetails.getUsername());
        return ResponseEntity.ok(ApiResponse.success("Comment added", response));
    }

    // ─── Shared ───────────────────────────────────────────────────────────────

    @GetMapping("/{ticketId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<TicketDetailResponse>> getTicketDetail(
            @PathVariable Long ticketId) {
        TicketDetailResponse response = ticketService.getTicketDetail(ticketId);
        return ResponseEntity.ok(ApiResponse.success("Ticket detail fetched", response));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('SUPPORT_DESK','ADMIN','TEAM_LEAD')")
    public ResponseEntity<ApiResponse<List<TicketResponse>>> getAllTickets() {
        List<TicketResponse> tickets = ticketService.getAllTickets();
        return ResponseEntity.ok(ApiResponse.success("All tickets fetched", tickets));
    }

    @GetMapping("/dashboard")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<DashboardResponse>> getDashboard(
            @AuthenticationPrincipal UserDetails userDetails) {
        DashboardResponse response = ticketService.getDashboard(userDetails.getUsername());
        return ResponseEntity.ok(ApiResponse.success("Dashboard data fetched", response));
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
    @GetMapping("/under-qa")
    @PreAuthorize("hasAuthority('ROLE_QA_TESTER')")
    public ResponseEntity<ApiResponse<List<TicketResponse>>> getTicketsUnderQa() {
        List<TicketResponse> tickets = ticketService.getTicketsByStatus(TicketStatus.UNDER_QA);
        return ResponseEntity.ok(ApiResponse.success("QA tickets fetched", tickets));
    }
}