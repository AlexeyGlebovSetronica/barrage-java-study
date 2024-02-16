package com.setronica.eventing.web;

import com.setronica.eventing.app.EventScheduleService;
import com.setronica.eventing.app.TicketOrderService;
import com.setronica.eventing.persistence.EventSchedule;
import com.setronica.eventing.persistence.TicketOrder;
import jakarta.validation.Valid;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("event/api/v1/events/event_schedules")
public class TicketOrderController {

    private final TicketOrderService ticketOrderService;
    private final EventScheduleService eventScheduleService;

    public TicketOrderController(TicketOrderService ticketOrderService, EventScheduleService eventScheduleService) {
        this.ticketOrderService = ticketOrderService;
        this.eventScheduleService = eventScheduleService;
    }

    @GetMapping("ticket_orders")
    public List<TicketOrder> getAll() {
        return ticketOrderService.getAll();
    }

    @GetMapping("ticket_orders/{id}")
    public TicketOrder getById(
            @PathVariable Integer id
    ) {
        return ticketOrderService.getById(id);
    }

    @PostMapping("{id}/ticket_orders")
    public ResponseEntity<?> create(@PathVariable Integer id, @Valid @RequestBody TicketOrder ticketOrder) {
        EventSchedule existingEventSchedule = eventScheduleService.getById(id);
        try {
            TicketOrder savedTicketOrder = ticketOrderService.save(ticketOrder, existingEventSchedule.getId());
            return ResponseEntity.ok(savedTicketOrder);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to save ticket order. Reason: " + extractExceptionMessage(e));
        }
    }

    private String extractExceptionMessage(Exception e) {
        String errorMessage = e.getMessage();
        int startIndex = errorMessage.indexOf("Reason:");
        if (startIndex != -1) {
            return errorMessage.substring(startIndex);
        } else {
            return errorMessage;
        }
    }



    @DeleteMapping("ticket_orders/{id}")
    public void delete(@PathVariable Integer id) {
        TicketOrder existingTicketOrder = ticketOrderService.getById(id);
        ticketOrderService.delete(existingTicketOrder.getId());
    }
}