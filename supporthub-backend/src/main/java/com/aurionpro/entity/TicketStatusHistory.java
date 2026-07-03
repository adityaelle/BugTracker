package com.aurionpro.entity;

import com.aurionpro.entity.enums.TicketStatus;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "ticket_status_history")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class TicketStatusHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ticket_id", nullable = false)
    private Ticket ticket;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "changed_by_id", nullable = false)
    private User changedBy;

    @Enumerated(EnumType.STRING)
    @Column(name = "old_status")
    private TicketStatus oldStatus;

    @Enumerated(EnumType.STRING)
    @Column(name = "new_status", nullable = false)
    private TicketStatus newStatus;

    private String remarks;

    @Column(name = "changed_at", updatable = false)
    private LocalDateTime changedAt;

    @PrePersist
    protected void onCreate() { changedAt = LocalDateTime.now(); }
}