package com.dzero.rabbitmq.consumer;

import com.dzero.rabbitmq.config.RabbitmqConfig;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.util.concurrent.CountDownLatch;

/**
 * @author dzd
 * @date 2020/10/29 10:15
 */
@Component
public class Receiver {
    private CountDownLatch latch = new CountDownLatch(1);

//    @RabbitListener(queues = {RabbitmqConfig.QUEUE_INFORM})
    public void receiveMessage(String msg) {
        System.out.println("【consumer】Received <" + msg + ">");
        latch.countDown();
    }

    public CountDownLatch getLatch() {
        return latch;
    }
}
