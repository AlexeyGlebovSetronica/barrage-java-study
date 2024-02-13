package com.setronica.eventing.app.payment.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class PaymentResultDto {
    private boolean isSucceed;

    private String paymentId;

    private String state;

    /**
     * The URL to payment gateway page
     */
    private String paymentUrl;
}
