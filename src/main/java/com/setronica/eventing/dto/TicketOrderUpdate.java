package com.setronica.eventing.dto;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class TicketOrderUpdate {
    private String firstname;
    private String lastname;
    private String email;
    private Integer amount;
}