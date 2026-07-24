package br.com.fiapx.processor.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties({RabbitMqProperties.class, StorageProperties.class})
public class RabbitMqConfig {

    @Bean
    TopicExchange fiapxEventsExchange(RabbitMqProperties properties) {
        return new TopicExchange(properties.exchange(), true, false);
    }

    @Bean
    Queue videoProcessingQueue(RabbitMqProperties properties) {
        return QueueBuilder.durable(properties.queue()).build();
    }

    @Bean
    Binding videoProcessingBinding(Queue videoProcessingQueue, TopicExchange fiapxEventsExchange, RabbitMqProperties properties) {
        return BindingBuilder
            .bind(videoProcessingQueue)
            .to(fiapxEventsExchange)
            .with(properties.routingKeyVideoRequested());
    }
}
