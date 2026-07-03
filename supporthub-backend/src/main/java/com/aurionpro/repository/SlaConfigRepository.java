package com.aurionpro.repository;

import com.aurionpro.entity.SlaConfig;
import com.aurionpro.entity.enums.TicketPriority;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SlaConfigRepository extends JpaRepository<SlaConfig, Long> {

    Optional<SlaConfig> findByPriority(TicketPriority priority);
}