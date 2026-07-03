package com.aurionpro.entity;

import com.aurionpro.entity.enums.*;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "tickets")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Ticket {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "ticket_number", nullable = false, unique = true)
    private String ticketNumber;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TicketCategory category;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TicketPriority priority;

    @Enumerated(EnumType.STRING)
    private TicketSeverity severity;

    @Enumerated(EnumType.STRING)
    private TicketStatus status;

    @Column(name = "application_module")
    private String applicationModule;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Environment environment;

    private String version;
    private String browser;

    @Column(name = "steps_to_reproduce", columnDefinition = "TEXT")
    private String stepsToReproduce;

    @Column(name = "expected_result", columnDefinition = "TEXT")
    private String expectedResult;

    @Column(name = "actual_result", columnDefinition = "TEXT")
    private String actualResult;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "raised_by_id", nullable = false)
    private User raisedBy;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assigned_to_id")
    private User assignedTo;

    @Column(name = "sla_deadline")
    private LocalDateTime slaDeadline;

    @Column(name = "sla_breached")
    private boolean slaBreached = false;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        if (status == null) status = TicketStatus.NEW;
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}