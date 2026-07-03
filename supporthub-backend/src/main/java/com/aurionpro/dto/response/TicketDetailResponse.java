package com.aurionpro.dto.response;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class TicketDetailResponse {
    private TicketResponse ticket;
    private List<TicketCommentResponse> comments;
    private List<TicketStatusHistoryResponse> statusHistory;
    private List<AttachmentResponse> attachments;
}