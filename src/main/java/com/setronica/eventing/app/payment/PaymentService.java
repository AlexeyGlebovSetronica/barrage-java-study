package com.setronica.eventing.app.payment;

import com.setronica.eventing.app.payment.dto.PaymentDto;
import com.setronica.eventing.app.payment.dto.PaymentResultDto;

public interface PaymentService {

    PaymentResultDto createPayment(PaymentDto dto);
}
