package com.dzero.rabbitmq.producer;

import com.dzero.rabbitmq.config.RabbitmqConfig;
import com.dzero.rabbitmq.consumer.Receiver;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

/**
 * @author dzd
 * @date 2020/10/29 10:19
 */
@Component
public class Runner implements CommandLineRunner {

    @Autowired
    private  RabbitTemplate rabbitTemplate;

    @Autowired
    private Receiver receiver;

    @Override
    public void run(String... args) throws InterruptedException {
        System.out.println("【producer】Sending message...");
        rabbitTemplate.convertAndSend(RabbitmqConfig.EXCHANGE_TOPICS_INFORM, "inform.hello", "Hello from RabbitMQ!");
        System.out.println("【Latch】"+receiver.getLatch().getCount());
        receiver.getLatch().await(10000, TimeUnit.MILLISECONDS);
        System.out.println("【Latch】"+receiver.getLatch().getCount());
    }
}
