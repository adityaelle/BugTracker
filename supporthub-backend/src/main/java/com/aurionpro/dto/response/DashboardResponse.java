package com.aurionpro.dto.response;

import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class DashboardResponse {
    private long totalTickets;
    private long newTickets;
    private long openTickets;
    private long inProgressTickets;
    private long resolvedTickets;
    private long closedTickets;
    private long reopenedTickets;
    private long slaBreachedTickets;
    private long bugCount;
    private long enhancementCount;
    private long featureRequestCount;
}