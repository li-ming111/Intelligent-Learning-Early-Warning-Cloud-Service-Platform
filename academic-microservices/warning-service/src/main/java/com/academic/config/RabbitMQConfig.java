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
        factory.setPrefetchCount(10);
        factory.setConcurrentConsumers(3);
        factory.setMaxConcurrentConsumers(8);
        return factory;
    }

    @Bean
    public FanoutExchange notificationExchange() {
        return new FanoutExchange(RabbitMQConstants.EXCHANGE_NOTIFICATION, true, false);
    }

    @Bean
    public DirectExchange warningExchange() {
        return new DirectExchange(RabbitMQConstants.EXCHANGE_WARNING, true, false);
    }

    @Bean
    public Queue warningAnalysisQueue() {
        return QueueBuilder.durable(RabbitMQConstants.QUEUE_WARNING_ANALYSIS)
                .withArgument("x-dead-letter-exchange", "warning.dlx")
                .build();
    }

    @Bean
    public Queue warningNotificationQueue() {
        return QueueBuilder.durable("warning.notification.queue").build();
    }

    @Bean
    public Binding warningAnalysisBinding() {
        return BindingBuilder.bind(warningAnalysisQueue())
                .to(warningExchange())
                .with(RabbitMQConstants.ROUTING_KEY_WARNING_ANALYSIS);
    }

    @Bean
    public Binding warningNotificationBinding() {
        return BindingBuilder.bind(warningNotificationQueue())
                .to(notificationExchange());
    }
}