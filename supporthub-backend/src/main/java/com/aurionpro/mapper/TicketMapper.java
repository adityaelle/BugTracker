package com.aurionpro.mapper;

import org.springframework.stereotype.Component;

import com.aurionpro.dto.response.AttachmentResponse;
import com.aurionpro.dto.response.TicketCommentResponse;
import com.aurionpro.dto.response.TicketResponse;
import com.aurionpro.dto.response.TicketStatusHistoryResponse;
import com.aurionpro.dto.response.UserResponse;
import com.aurionpro.entity.Ticket;
import com.aurionpro.entity.TicketAttachment;
import com.aurionpro.entity.TicketComment;
import com.aurionpro.entity.TicketStatusHistory;
import com.aurionpro.entity.User;

@Component
public class TicketMapper {

    public UserResponse toUserResponse(User user) {
        if (user == null) return null;
        return UserResponse.builder()
                .id(user.getId())
                .fullName(user.getFullName())
                .email(user.getEmail())
                .role(user.getRole())
                .department(user.getDepartment())
                .isActive(user.isActive())
                .build();
    }

    public TicketResponse toTicketResponse(Ticket ticket) {
        return TicketResponse.builder()
                .id(ticket.getId())
                .ticketNumber(ticket.getTicketNumber())
                .title(ticket.getTitle())
                .description(ticket.getDescription())
                .category(ticket.getCategory())
                .priority(ticket.getPriority())
                .severity(ticket.getSeverity())
                .status(ticket.getStatus())
                .applicationModule(ticket.getApplicationModule())
                .environment(ticket.getEnvironment())
                .version(ticket.getVersion())
                .browser(ticket.getBrowser())
                .stepsToReproduce(ticket.getStepsToReproduce())
                .expectedResult(ticket.getExpectedResult())
                .actualResult(ticket.getActualResult())
                .raisedBy(toUserResponse(ticket.getRaisedBy()))
                .assignedTo(toUserResponse(ticket.getAssignedTo()))
                .slaDeadline(ticket.getSlaDeadline())
                .slaBreached(ticket.isSlaBreached())
                .createdAt(ticket.getCreatedAt())
                .updatedAt(ticket.getUpdatedAt())
                .build();
    }

    public TicketCommentResponse toCommentResponse(TicketComment comment) {
        return TicketCommentResponse.builder()
                .id(comment.getId())
                .content(comment.getContent())
                .commentedBy(toUserResponse(comment.getCommentedBy()))
                .createdAt(comment.getCreatedAt())
                .build();
    }

    public TicketStatusHistoryResponse toStatusHistoryResponse(TicketStatusHistory history) {
        return TicketStatusHistoryResponse.builder()
                .id(history.getId())
                .oldStatus(history.getOldStatus())
                .newStatus(history.getNewStatus())
                .remarks(history.getRemarks())
                .changedBy(toUserResponse(history.getChangedBy()))
                .changedAt(history.getChangedAt())
                .build();
    }

    public AttachmentResponse toAttachmentResponse(TicketAttachment attachment) {
        return AttachmentResponse.builder()
                .id(attachment.getId())
                .fileName(attachment.getFileName())
                .fileType(attachment.getFileType())
                .fileSize(attachment.getFileSize())
                .uploadedBy(toUserResponse(attachment.getUploadedBy()))
                .createdAt(attachment.getCreatedAt())
                .build();
    }
}