package com.aurionpro.dto.response;

import com.aurionpro.entity.enums.*;
import lombok.*;
import java.time.LocalDateTime;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class TicketResponse {
    private Long id;
    private String ticketNumber;
    private String title;
    private String description;
    private TicketCategory category;
    private TicketPriority priority;
    private TicketSeverity severity;
    private TicketStatus status;
    private String applicationModule;
    private Environment environment;
    private String version;
    private String browser;
    private String stepsToReproduce;
    private String expectedResult;
    private String actualResult;
    private UserResponse raisedBy;
    private UserResponse assignedTo;
    private LocalDateTime slaDeadline;
    private boolean slaBreached;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}