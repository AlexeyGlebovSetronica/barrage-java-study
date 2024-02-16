package com.setronica.eventing.app;

import com.setronica.eventing.dto.TicketOrderUpdate;
import com.setronica.eventing.exceptions.NotFoundException;
import com.setronica.eventing.persistence.TicketOrder;
import com.setronica.eventing.persistence.TicketOrderRepository;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TicketOrderService {

    private final TicketOrderRepository ticketOrderRepository;

    public TicketOrderService(TicketOrderRepository ticketOrderRepository) {
        this.ticketOrderRepository = ticketOrderRepository;
    }

    public List<TicketOrder> getAll() {
        return ticketOrderRepository.findAll();
    }

    public TicketOrder getById(Integer id) {
        return ticketOrderRepository.findById(id).orElseThrow(() -> new NotFoundException("Ticket order not found with id=" + id));
    }

    public TicketOrder save(TicketOrder ticketOrder, int id) {
        ticketOrder.setEventScheduleId(id);
        try {
            return ticketOrderRepository.save(ticketOrder);
        } catch (DataIntegrityViolationException e) {
            throw new RuntimeException("Failed to save ticket order due to data integrity violation. Reason: " + e.getMessage(), e);
        }
    }

    public TicketOrder update(TicketOrder existingTicketOrder, TicketOrderUpdate ticketOrderUpdate) {
        existingTicketOrder.setFirstname(ticketOrderUpdate.getFirstname());
        existingTicketOrder.setLastname(ticketOrderUpdate.getLastname());
        existingTicketOrder.setEmail(ticketOrderUpdate.getEmail());
        existingTicketOrder.setAmount(ticketOrderUpdate.getAmount());
        return ticketOrderRepository.save(existingTicketOrder);
    }

    public void delete(Integer id) {
        ticketOrderRepository.deleteById(id);
    }

}