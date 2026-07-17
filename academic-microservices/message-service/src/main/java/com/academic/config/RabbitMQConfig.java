package com.academic.config;

import com.academic.common.mq.RabbitMQConstants;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(jsonMessageConverter());
        rabbitTemplate.setMandatory(true);
        return rabbitTemplate;
    }

    @Bean
    public SimpleRabbitListenerContainerFactory rabbitListenerContainerFactory(ConnectionFactory connectionFactory) {
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);
        factory.setMessageConverter(jsonMessageConverter());
        factory.setAcknowledgeMode(AcknowledgeMode.MANUAL);
        factory.setPrefetchCount(20);
        factory.setConcurrentConsumers(5);
        factory.setMaxConcurrentConsumers(15);
        return factory;
    }

    @Bean
    public FanoutExchange notificationExchange() {
        return new FanoutExchange(RabbitMQConstants.EXCHANGE_NOTIFICATION, true, false);
    }

    @Bean
    public DirectExchange messageExchange() {
        return new DirectExchange(RabbitMQConstants.EXCHANGE_MESSAGE, true, false);
    }

    @Bean
    public Queue messageSendQueue() {
        return QueueBuilder.durable(RabbitMQConstants.QUEUE_MESSAGE_SEND)
                .withArgument("x-dead-letter-exchange", "message.dlx")
                .build();
    }

    @Bean
    public Queue messageNotificationQueue() {
        return QueueBuilder.durable("message.notification.queue").build();
    }

    @Bean
    public Binding messageSendBinding() {
        return BindingBuilder.bind(messageSendQueue())
                .to(messageExchange())
                .with(RabbitMQConstants.ROUTING_KEY_MESSAGE_SEND);
    }

    @Bean
    public Binding messageNotificationBinding() {
        return BindingBuilder.bind(messageNotificationQueue())
                .to(notificationExchange());
    }
}