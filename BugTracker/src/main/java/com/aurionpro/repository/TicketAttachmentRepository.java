package com.aurionpro.repository;

import com.aurionpro.entity.Ticket;
import com.aurionpro.entity.TicketAttachment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TicketAttachmentRepository extends JpaRepository<TicketAttachment, Long> {

    List<TicketAttachment> findByTicket(Ticket ticket);
}