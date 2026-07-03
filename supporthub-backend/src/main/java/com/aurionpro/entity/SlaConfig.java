package com.aurionpro.entity;

import com.aurionpro.entity.enums.TicketPriority;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "sla_config")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class SlaConfig {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, unique = true)
    private TicketPriority priority;

    @Column(name = "resolution_hours", nullable = false)
    private int resolutionHours;
}