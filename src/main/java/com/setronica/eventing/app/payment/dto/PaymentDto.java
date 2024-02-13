package com.setronica.eventing.app.payment.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class PaymentDto {

    private Integer orderId;

    private BigDecimal amount;

    private String description;
}
