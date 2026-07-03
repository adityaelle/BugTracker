package com.aurionpro.repository;

import com.aurionpro.entity.Ticket;
import com.aurionpro.entity.TicketStatusHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TicketStatusHistoryRepository extends JpaRepository<TicketStatusHistory, Long> {

    List<TicketStatusHistory> findByTicketOrderByChangedAtAsc(Ticket ticket);
}