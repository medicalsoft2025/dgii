package com.medical.onepay.config.rabbitmq;

import org.springframework.amqp.core.*;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    public static final String INVOICE_EXCHANGE = "invoice.exchange";
    public static final String INVOICE_QUEUE = "invoice.queue";
    public static final String INVOICE_ROUTING_KEY = "invoice.routingkey";

    public static final String DEAD_LETTER_EXCHANGE = "deadletter.exchange";
    public static final String DEAD_LETTER_QUEUE = "deadletter.queue";
    public static final String DEAD_LETTER_ROUTING_KEY = "deadletter.routingkey";

    @Bean
    DirectExchange invoiceExchange() {
        return new DirectExchange(INVOICE_EXCHANGE);
    }

    @Bean
    DirectExchange deadLetterExchange() {
        return new DirectExchange(DEAD_LETTER_EXCHANGE);
    }

    @Bean
    Queue invoiceQueue() {
        return QueueBuilder.durable(INVOICE_QUEUE)
                .withArgument("x-dead-letter-exchange", DEAD_LETTER_EXCHANGE)
                .withArgument("x-dead-letter-routing-key", DEAD_LETTER_ROUTING_KEY)
                .build();
    }

    @Bean
    Queue deadLetterQueue() {
        return QueueBuilder.durable(DEAD_LETTER_QUEUE).build();
    }

    @Bean
    Binding invoiceBinding(Queue invoiceQueue, DirectExchange invoiceExchange) {
        return BindingBuilder.bind(invoiceQueue).to(invoiceExchange).with(INVOICE_ROUTING_KEY);
    }

    @Bean
    Binding deadLetterBinding(Queue deadLetterQueue, DirectExchange deadLetterExchange) {
        return BindingBuilder.bind(deadLetterQueue).to(deadLetterExchange).with(DEAD_LETTER_ROUTING_KEY);
    }

    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }
}
