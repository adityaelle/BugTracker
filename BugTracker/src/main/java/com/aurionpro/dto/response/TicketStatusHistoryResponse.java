package com.aurionpro.dto.response;

import com.aurionpro.entity.enums.TicketStatus;
import lombok.*;
import java.time.LocalDateTime;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class TicketStatusHistoryResponse {
    private Long id;
    private TicketStatus oldStatus;
    private TicketStatus newStatus;
    private String remarks;
    private UserResponse changedBy;
    private LocalDateTime changedAt;
}