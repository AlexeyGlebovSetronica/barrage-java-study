package com.setronica.eventing.app;

import com.setronica.eventing.app.payment.dto.PaymentResultDto;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class QueueListener {

    @RabbitListener(queues = {"spring-boot"})
    public void readPaymentEvent(PaymentResultDto message) {
        // do something
    }
}
