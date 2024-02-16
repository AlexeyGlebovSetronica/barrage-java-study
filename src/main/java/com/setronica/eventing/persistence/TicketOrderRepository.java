package com.setronica.eventing.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

public interface TicketOrderRepository extends JpaRepository<TicketOrder, Integer> {
}