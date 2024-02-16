package com.setronica.eventing.persistence;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Setter
@Getter
@Entity
@Table(name = "event_schedules")
public class EventSchedule {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "event_id")
    private int eventId;

    @Column(nullable = false)
    private LocalDate eventDate;

    @Column(nullable = false)
    private Integer availableSeats;

    @Column(nullable = false)
    private BigDecimal price;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "event_schedule_id")
    private List<TicketOrder> ticketOrders = new ArrayList<>();
}