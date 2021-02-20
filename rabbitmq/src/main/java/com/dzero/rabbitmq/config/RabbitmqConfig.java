package com.dzero.rabbitmq.config;

import com.dzero.rabbitmq.consumer.Receiver;
import org.springframework.amqp.core.Exchange;
import org.springframework.amqp.core.ExchangeBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.amqp.rabbit.listener.adapter.MessageListenerAdapter;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author dzd
 * @date 2020/10/29 9:32
 */
@Configuration
public class RabbitmqConfig {

    public static final String QUEUE_INFORM = "ha_queue_inform";
    public static final String EXCHANGE_TOPICS_INFORM = "ha_exchange_topics_inform";
    public static final String ROUTING_KEY_MESSAGE = "inform.#";

    @Bean(EXCHANGE_TOPICS_INFORM)
    public Exchange exchange() {
        // durable(true) 可持久化的,表示重启之后交换机还在
        return ExchangeBuilder.topicExchange(EXCHANGE_TOPICS_INFORM).durable(true).build();

    }

    @Bean(QUEUE_INFORM)
    public Queue queue() {
        return new Queue(QUEUE_INFORM);
    }

    @Bean(ROUTING_KEY_MESSAGE)
    public Binding binding(@Qualifier(QUEUE_INFORM) Queue queue, @Qualifier(EXCHANGE_TOPICS_INFORM) Exchange exchange) {
        return BindingBuilder.bind(queue).to(exchange).with(ROUTING_KEY_MESSAGE).noargs();
    }

    @Bean
    SimpleMessageListenerContainer container(ConnectionFactory connectionFactory, MessageListenerAdapter listenerAdapter) {
        SimpleMessageListenerContainer container = new SimpleMessageListenerContainer();
        container.setConnectionFactory(connectionFactory);
        container.setQueueNames(QUEUE_INFORM);
        container.setMessageListener(listenerAdapter);
        return container;
    }

    @Bean
    MessageListenerAdapter listenerAdapter(Receiver receiver) {
        return new MessageListenerAdapter(receiver, "receiveMessage");
    }
}
