package com.aurionpro.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.aurionpro.dto.request.AddCommentRequest;
import com.aurionpro.dto.request.AssignTicketRequest;
import com.aurionpro.dto.request.CreateTicketRequest;
import com.aurionpro.dto.request.UpdateTicketStatusRequest;
import com.aurionpro.dto.response.AttachmentResponse;
import com.aurionpro.dto.response.DashboardResponse;
import com.aurionpro.dto.response.TicketCommentResponse;
import com.aurionpro.dto.response.TicketDetailResponse;
import com.aurionpro.dto.response.TicketResponse;
import com.aurionpro.dto.response.TicketStatusHistoryResponse;
import com.aurionpro.entity.SlaConfig;
import com.aurionpro.entity.Ticket;
import com.aurionpro.entity.TicketComment;
import com.aurionpro.entity.TicketStatusHistory;
import com.aurionpro.entity.User;
import com.aurionpro.entity.enums.TicketCategory;
import com.aurionpro.entity.enums.TicketPriority;
import com.aurionpro.entity.enums.TicketSeverity;
import com.aurionpro.entity.enums.TicketStatus;
import com.aurionpro.entity.enums.UserRole;
import com.aurionpro.exception.InvalidStatusTransitionException;
import com.aurionpro.exception.ResourceNotFoundException;
import com.aurionpro.exception.UnauthorizedActionException;
import com.aurionpro.mapper.TicketMapper;
import com.aurionpro.repository.SlaConfigRepository;
import com.aurionpro.repository.TicketAttachmentRepository;
import com.aurionpro.repository.TicketCommentRepository;
import com.aurionpro.repository.TicketRepository;
import com.aurionpro.repository.TicketStatusHistoryRepository;
import com.aurionpro.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class TicketService {

    private final TicketRepository ticketRepository;
    private final UserRepository userRepository;
    private final TicketCommentRepository commentRepository;
    private final TicketStatusHistoryRepository statusHistoryRepository;
    private final TicketAttachmentRepository attachmentRepository;
    private final SlaConfigRepository slaConfigRepository;
    private final EmailNotificationService emailNotificationService;
    private final TicketMapper ticketMapper;

    // ─── Ticket Creation ────────────────────────────────────────────────────

    public TicketResponse createTicket(CreateTicketRequest request, String clientEmail) {
        User client = getUserByEmail(clientEmail);

        String ticketNumber = generateTicketNumber(request.getCategory());

        User supportDesk = userRepository.findByRoleAndIsActiveTrue(UserRole.SUPPORT_DESK)
                .stream()
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException(
                        "No active Support Desk account found"));

        Ticket ticket = Ticket.builder()
                .ticketNumber(ticketNumber)
                .title(request.getTitle())
                .description(request.getDescription())
                .category(request.getCategory())
                .priority(request.getPriority())
                .severity(TicketSeverity.MEDIUM)
                .status(TicketStatus.NEW)
                .applicationModule(request.getApplicationModule())
                .environment(request.getEnvironment())
                .version(request.getVersion())
                .browser(request.getBrowser())
                .stepsToReproduce(request.getStepsToReproduce())
                .expectedResult(request.getExpectedResult())
                .actualResult(request.getActualResult())
                .raisedBy(client)
                .assignedTo(supportDesk)
                .build();

        ticket = ticketRepository.save(ticket);

        recordStatusHistory(ticket, null, TicketStatus.NEW, client, "Ticket created");

        emailNotificationService.sendTicketCreatedEmail(ticket);
        emailNotificationService.sendNewTicketToSupportDesk(ticket);

        return ticketMapper.toTicketResponse(ticket);
    }

    // ─── Support Desk: Assign Ticket ────────────────────────────────────────

    public TicketResponse assignTicket(Long ticketId,
                                       AssignTicketRequest request,
                                       String supportDeskEmail) {
        User supportDesk = getUserByEmail(supportDeskEmail);
        Ticket ticket = getTicketById(ticketId);

        User assignee = userRepository.findById(request.getAssigneeId())
                .orElseThrow(() -> new ResourceNotFoundException("Assignee not found"));

        validateAssigneeRole(assignee);

        TicketStatus oldStatus = ticket.getStatus();

        ticket.setAssignedTo(assignee);
        ticket.setPriority(request.getPriority());
        ticket.setSeverity(request.getSeverity());
        ticket.setStatus(TicketStatus.ASSIGNED);
        ticket.setSlaDeadline(calculateSlaDeadline(request.getPriority()));

        ticketRepository.save(ticket);

        recordStatusHistory(ticket, oldStatus, TicketStatus.ASSIGNED,
                supportDesk, request.getRemarks());

        emailNotificationService.sendTicketAssignedToDeveloper(ticket);

        return ticketMapper.toTicketResponse(ticket);
    }

    // ─── Developer: Update Status ────────────────────────────────────────────

    public TicketResponse updateTicketStatus(Long ticketId,
                                              UpdateTicketStatusRequest request,
                                              String userEmail) {
        User currentUser = getUserByEmail(userEmail);
        Ticket ticket = getTicketById(ticketId);

        validateStatusTransition(ticket.getStatus(), request.getStatus(), currentUser.getRole());

        TicketStatus oldStatus = ticket.getStatus();

        if (request.getStatus() == TicketStatus.RESOLVED) {
            ticket.setStatus(TicketStatus.UNDER_QA);
            ticketRepository.save(ticket);
            recordStatusHistory(ticket, oldStatus, TicketStatus.RESOLVED,
                    currentUser, request.getRemarks());
            recordStatusHistory(ticket, TicketStatus.RESOLVED, TicketStatus.UNDER_QA,
                    currentUser, "Auto-moved to QA after resolution");
            emailNotificationService.sendTicketReadyForQa(ticket);
        } else {
            ticket.setStatus(request.getStatus());
            ticketRepository.save(ticket);
            recordStatusHistory(ticket, oldStatus, request.getStatus(),
                    currentUser, request.getRemarks());
            sendStatusChangeEmail(ticket, request.getStatus());
        }

        return ticketMapper.toTicketResponse(ticket);
    }

    // ─── QA: Pass or Fail ────────────────────────────────────────────────────

    public TicketResponse qaVerdict(Long ticketId,
                                    UpdateTicketStatusRequest request,
                                    String qaEmail) {
        User qa = getUserByEmail(qaEmail);
        Ticket ticket = getTicketById(ticketId);

        if (ticket.getStatus() != TicketStatus.UNDER_QA) {
            throw new InvalidStatusTransitionException("Ticket is not under QA review");
        }

        if (request.getStatus() == TicketStatus.VERIFIED) {
            ticket.setStatus(TicketStatus.VERIFIED);
            ticketRepository.save(ticket);
            recordStatusHistory(ticket, TicketStatus.UNDER_QA, TicketStatus.VERIFIED,
                    qa, request.getRemarks());
            emailNotificationService.sendTicketVerifiedToClient(ticket);

        } else if (request.getStatus() == TicketStatus.IN_PROGRESS) {
            ticket.setStatus(TicketStatus.IN_PROGRESS);
            ticketRepository.save(ticket);
            recordStatusHistory(ticket, TicketStatus.UNDER_QA, TicketStatus.IN_PROGRESS,
                    qa, "QA failed: " + request.getRemarks());
            emailNotificationService.sendQaFailedToDeveloper(ticket);

        } else {
            throw new InvalidStatusTransitionException(
                    "QA can only mark as VERIFIED or return IN_PROGRESS");
        }

        return ticketMapper.toTicketResponse(ticket);
    }

    // ─── Client: Close or Reopen ─────────────────────────────────────────────

    public TicketResponse closeTicket(Long ticketId, String clientEmail) {
        User client = getUserByEmail(clientEmail);
        Ticket ticket = getTicketById(ticketId);

        if (ticket.getStatus() != TicketStatus.VERIFIED) {
            throw new InvalidStatusTransitionException(
                    "Ticket can only be closed after QA verification");
        }

        validateTicketOwner(ticket, client);

        ticket.setStatus(TicketStatus.CLOSED);
        ticketRepository.save(ticket);
        recordStatusHistory(ticket, TicketStatus.VERIFIED, TicketStatus.CLOSED,
                client, "Client confirmed resolution");
        emailNotificationService.sendTicketClosedEmail(ticket);

        return ticketMapper.toTicketResponse(ticket);
    }

    public TicketResponse reopenTicket(Long ticketId,
                                       AddCommentRequest request,
                                       String clientEmail) {
        User client = getUserByEmail(clientEmail);
        Ticket ticket = getTicketById(ticketId);

        if (ticket.getStatus() != TicketStatus.CLOSED
                && ticket.getStatus() != TicketStatus.VERIFIED) {
            throw new InvalidStatusTransitionException(
                    "Only closed or verified tickets can be reopened");
        }

        validateTicketOwner(ticket, client);

        User supportDesk = userRepository.findByRoleAndIsActiveTrue(UserRole.SUPPORT_DESK)
                .stream()
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("No active Support Desk found"));

        TicketStatus oldStatus = ticket.getStatus();
        ticket.setStatus(TicketStatus.REOPENED);
        ticket.setAssignedTo(supportDesk);
        ticketRepository.save(ticket);

        TicketComment comment = TicketComment.builder()
                .ticket(ticket)
                .commentedBy(client)
                .content("Reopened: " + request.getContent())
                .build();
        commentRepository.save(comment);

        recordStatusHistory(ticket, oldStatus, TicketStatus.REOPENED,
                client, "Client reopened: " + request.getContent());

        emailNotificationService.sendTicketReopenedEmail(ticket);

        return ticketMapper.toTicketResponse(ticket);
    }

    // ─── Comments ────────────────────────────────────────────────────────────

    public TicketCommentResponse addComment(Long ticketId,
                                            AddCommentRequest request,
                                            String userEmail) {
        User user = getUserByEmail(userEmail);
        Ticket ticket = getTicketById(ticketId);

        TicketComment comment = TicketComment.builder()
                .ticket(ticket)
                .commentedBy(user)
                .content(request.getContent())
                .build();

        comment = commentRepository.save(comment);
        emailNotificationService.sendCommentAddedEmail(ticket, user);

        return ticketMapper.toCommentResponse(comment);
    }

    // ─── Fetch Queries ───────────────────────────────────────────────────────

    @Transactional(readOnly = true)
    public TicketDetailResponse getTicketDetail(Long ticketId) {
        Ticket ticket = getTicketById(ticketId);

        List<TicketCommentResponse> comments = commentRepository
                .findByTicketOrderByCreatedAtAsc(ticket)
                .stream()
                .map(ticketMapper::toCommentResponse)
                .toList();

        List<TicketStatusHistoryResponse> history = statusHistoryRepository
                .findByTicketOrderByChangedAtAsc(ticket)
                .stream()
                .map(ticketMapper::toStatusHistoryResponse)
                .toList();

        List<AttachmentResponse> attachments = attachmentRepository
                .findByTicket(ticket)
                .stream()
                .map(ticketMapper::toAttachmentResponse)
                .toList();

        return TicketDetailResponse.builder()
                .ticket(ticketMapper.toTicketResponse(ticket))
                .comments(comments)
                .statusHistory(history)
                .attachments(attachments)
                .build();
    }

    @Transactional(readOnly = true)
    public List<TicketResponse> getTicketsForClient(String clientEmail) {
        User client = getUserByEmail(clientEmail);
        return ticketRepository.findByRaisedBy(client)
                .stream()
                .map(ticketMapper::toTicketResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<TicketResponse> getTicketsForDeveloper(String developerEmail) {
        User developer = getUserByEmail(developerEmail);
        return ticketRepository.findByAssignedTo(developer)
                .stream()
                .map(ticketMapper::toTicketResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<TicketResponse> getAllTickets() {
        return ticketRepository.findAll()
                .stream()
                .map(ticketMapper::toTicketResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public DashboardResponse getDashboard(String userEmail) {
        User user = getUserByEmail(userEmail);

        if (user.getRole() == UserRole.CLIENT) {
            return DashboardResponse.builder()
                    .totalTickets(ticketRepository.findByRaisedBy(user).size())
                    .newTickets(ticketRepository.countByRaisedByAndStatus(user, TicketStatus.NEW))
                    .openTickets(ticketRepository.countByRaisedByAndStatus(user, TicketStatus.ASSIGNED))
                    .inProgressTickets(ticketRepository.countByRaisedByAndStatus(user, TicketStatus.IN_PROGRESS))
                    .resolvedTickets(ticketRepository.countByRaisedByAndStatus(user, TicketStatus.RESOLVED))
                    .closedTickets(ticketRepository.countByRaisedByAndStatus(user, TicketStatus.CLOSED))
                    .build();
        }

        return DashboardResponse.builder()
                .totalTickets(ticketRepository.count())
                .newTickets(ticketRepository.countByStatus(TicketStatus.NEW))
                .openTickets(ticketRepository.countByStatus(TicketStatus.ASSIGNED))
                .inProgressTickets(ticketRepository.countByStatus(TicketStatus.IN_PROGRESS))
                .resolvedTickets(ticketRepository.countByStatus(TicketStatus.RESOLVED))
                .closedTickets(ticketRepository.countByStatus(TicketStatus.CLOSED))
                .reopenedTickets(ticketRepository.countByStatus(TicketStatus.REOPENED))
                .slaBreachedTickets(ticketRepository
                        .findSlaBreachedTickets(LocalDateTime.now()).size())
                .build();
    }

    // ─── Private Helpers ─────────────────────────────────────────────────────

    private String generateTicketNumber(TicketCategory category) {
        String prefix = switch (category) {
            case BUG -> "BUG";
            case ENHANCEMENT -> "ENH";
            case FEATURE_REQUEST -> "FEAT";
        };
        String year = String.valueOf(LocalDateTime.now().getYear());
        long count = ticketRepository.count() + 1;
        return String.format("%s-%s-%06d", prefix, year, count);
    }

    private LocalDateTime calculateSlaDeadline(TicketPriority priority) {
        int hours = slaConfigRepository.findByPriority(priority)
                .map(SlaConfig::getResolutionHours)
                .orElse(24);
        return LocalDateTime.now().plusHours(hours);
    }

    private void recordStatusHistory(Ticket ticket, TicketStatus oldStatus,
                                     TicketStatus newStatus, User changedBy,
                                     String remarks) {
        TicketStatusHistory history = TicketStatusHistory.builder()
                .ticket(ticket)
                .oldStatus(oldStatus)
                .newStatus(newStatus)
                .changedBy(changedBy)
                .remarks(remarks)
                .build();
        statusHistoryRepository.save(history);
    }

    private void validateStatusTransition(TicketStatus current,
                                           TicketStatus requested,
                                           UserRole role) {
        boolean valid = switch (role) {
            case DEVELOPER -> switch (current) {
                case ASSIGNED -> requested == TicketStatus.IN_PROGRESS;
                case IN_PROGRESS -> requested == TicketStatus.RESOLVED
                        || requested == TicketStatus.WAITING_FOR_CLIENT;
                case WAITING_FOR_CLIENT -> requested == TicketStatus.IN_PROGRESS;
                default -> false;
            };
            case SUPPORT_DESK -> true;
            case TEAM_LEAD -> true;
            default -> false;
        };

        if (!valid) {
            throw new InvalidStatusTransitionException(
                    "Invalid status transition from " + current + " to " + requested
                    + " for role " + role);
        }
    }

    private void validateAssigneeRole(User assignee) {
        List<UserRole> validRoles = List.of(
                UserRole.DEVELOPER, UserRole.QA_TESTER, UserRole.TEAM_LEAD);
        if (!validRoles.contains(assignee.getRole())) {
            throw new UnauthorizedActionException(
                    "Tickets can only be assigned to Developer, QA Tester, or Team Lead");
        }
    }

    private void validateTicketOwner(Ticket ticket, User client) {
        if (!ticket.getRaisedBy().getId().equals(client.getId())) {
            throw new UnauthorizedActionException(
                    "You are not the owner of this ticket");
        }
    }

    private void sendStatusChangeEmail(Ticket ticket, TicketStatus status) {
        switch (status) {
            case WAITING_FOR_CLIENT ->
                    emailNotificationService.sendWaitingForClientEmail(ticket);
            case CLOSED ->
                    emailNotificationService.sendTicketClosedEmail(ticket);
            default -> {}
        }
    }

    private User getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "User not found: " + email));
    }

    private Ticket getTicketById(Long id) {
        return ticketRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Ticket not found with id: " + id));
    }
    @Transactional(readOnly = true)
    public List<TicketResponse> getTicketsByStatus(TicketStatus status) {
        return ticketRepository.findByStatus(status)
                .stream()
                .map(ticketMapper::toTicketResponse)
                .toList();
    }
}