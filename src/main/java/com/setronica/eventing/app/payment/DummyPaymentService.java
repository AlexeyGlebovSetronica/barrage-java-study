package com.setronica.eventing.app.payment;

import com.setronica.eventing.app.payment.dto.PaymentDto;
import com.setronica.eventing.app.payment.dto.PaymentResultDto;
import org.springframework.stereotype.Service;

@Service
public class DummyPaymentService implements PaymentService {
    @Override
    public PaymentResultDto createPayment(PaymentDto dto) {
        return new PaymentResultDto(false, null, null, null);
    }
}
