package com.aurionpro.dto.request;

import com.aurionpro.entity.enums.TicketPriority;
import com.aurionpro.entity.enums.TicketSeverity;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class AssignTicketRequest {

    @NotNull(message = "Assignee ID is required")
    private Long assigneeId;

    @NotNull(message = "Priority is required")
    private TicketPriority priority;

    @NotNull(message = "Severity is required")
    private TicketSeverity severity;

    private String remarks;
}