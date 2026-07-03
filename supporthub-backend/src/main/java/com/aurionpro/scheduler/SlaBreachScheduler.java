package com.aurionpro.scheduler;

import com.aurionpro.entity.Ticket;
import com.aurionpro.repository.TicketRepository;
import com.aurionpro.service.EmailNotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class SlaBreachScheduler {

    private final TicketRepository ticketRepository;
    private final EmailNotificationService emailNotificationService;

    // Runs every hour
    @Scheduled(fixedRate = 3600000)
    @Transactional
    public void checkSlaBreaches() {
        log.info("SLA breach check running at {}", LocalDateTime.now());

        List<Ticket> breachedTickets =
                ticketRepository.findSlaBreachedTickets(LocalDateTime.now());

        for (Ticket ticket : breachedTickets) {
            ticket.setSlaBreached(true);
            ticketRepository.save(ticket);
            emailNotificationService.sendSlaBreachEmail(ticket);
            log.warn("SLA breached for ticket: {}", ticket.getTicketNumber());
        }

        log.info("SLA check complete. {} ticket(s) breached.", breachedTickets.size());
    }
}