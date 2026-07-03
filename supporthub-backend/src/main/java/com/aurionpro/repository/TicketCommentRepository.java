package com.aurionpro.repository;

import com.aurionpro.entity.Ticket;
import com.aurionpro.entity.TicketComment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TicketCommentRepository extends JpaRepository<TicketComment, Long> {

    List<TicketComment> findByTicketOrderByCreatedAtAsc(Ticket ticket);
}