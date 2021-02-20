package com.dzero.rabbitmq.producer;

import com.dzero.rabbitmq.config.RabbitmqConfig;
import org.junit.jupiter.api.Test;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.jupiter.api.Assertions.*;

class RunnerTest {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Test
    public void testSendEmail() {
        System.out.println("【producer】Sending message...");
        rabbitTemplate.convertAndSend(RabbitmqConfig.EXCHANGE_TOPICS_INFORM, "inform.hello", "Hello from RabbitMQ!");
    }

}