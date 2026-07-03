package com.aurionpro.repository;

import com.aurionpro.entity.Ticket;
import com.aurionpro.entity.User;
import com.aurionpro.entity.enums.TicketPriority;
import com.aurionpro.entity.enums.TicketStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface TicketRepository extends JpaRepository<Ticket, Long> {

    Optional<Ticket> findByTicketNumber(String ticketNumber);

    List<Ticket> findByRaisedBy(User raisedBy);

    List<Ticket> findByAssignedTo(User assignedTo);

    List<Ticket> findByStatus(TicketStatus status);

    List<Ticket> findByAssignedToAndStatus(User assignedTo, TicketStatus status);

    List<Ticket> findByRaisedByAndStatus(User raisedBy, TicketStatus status);

    List<Ticket> findByPriorityAndSlaBreachedFalseAndSlaDeadlineBefore(
            TicketPriority priority, LocalDateTime now);

    @Query("SELECT t FROM Ticket t WHERE t.status = 'NEW' OR t.status = 'UNDER_REVIEW'")
    List<Ticket> findUnassignedTickets();

    @Query("SELECT t FROM Ticket t WHERE t.slaBreached = false " +
           "AND t.slaDeadline IS NOT NULL AND t.slaDeadline < :now " +
           "AND t.status NOT IN ('CLOSED', 'VERIFIED')")
    List<Ticket> findSlaBreachedTickets(@Param("now") LocalDateTime now);

    long countByStatus(TicketStatus status);

    long countByRaisedByAndStatus(User raisedBy, TicketStatus status);

    long countByAssignedToAndStatus(User assignedTo, TicketStatus status);

    @Query("SELECT MONTH(t.createdAt), COUNT(t) FROM Ticket t " +
           "WHERE YEAR(t.createdAt) = :year GROUP BY MONTH(t.createdAt)")
    List<Object[]> countTicketsByMonth(@Param("year") int year);
}