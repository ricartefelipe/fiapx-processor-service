package br.com.fiapx.processor.support;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
@Profile("integration-test")
public class IntegrationTestRabbitConfig {

    public static final String COMPLETED_SPY_QUEUE = "test.video.completed.spy";
    public static final String FAILED_SPY_QUEUE = "test.video.failed.spy";
    public static final String PROCESSING_SPY_QUEUE = "test.video.processing.spy";

    @Bean
    Queue completedSpyQueue() {
        return QueueBuilder.durable(COMPLETED_SPY_QUEUE).build();
    }

    @Bean
    Queue failedSpyQueue() {
        return QueueBuilder.durable(FAILED_SPY_QUEUE).build();
    }

    @Bean
    Queue processingSpyQueue() {
        return QueueBuilder.durable(PROCESSING_SPY_QUEUE).build();
    }

    @Bean
    Binding completedSpyBinding(Queue completedSpyQueue, TopicExchange fiapxEventsExchange) {
        return BindingBuilder.bind(completedSpyQueue).to(fiapxEventsExchange).with("video.completed");
    }

    @Bean
    Binding failedSpyBinding(Queue failedSpyQueue, TopicExchange fiapxEventsExchange) {
        return BindingBuilder.bind(failedSpyQueue).to(fiapxEventsExchange).with("video.failed");
    }

    @Bean
    Binding processingSpyBinding(Queue processingSpyQueue, TopicExchange fiapxEventsExchange) {
        return BindingBuilder.bind(processingSpyQueue).to(fiapxEventsExchange).with("video.processing");
    }
}
