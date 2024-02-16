package com.setronica.eventing.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Setter
@Getter
public class EventScheduleUpdate {
    private Integer availableSeats;
    private BigDecimal price;
}