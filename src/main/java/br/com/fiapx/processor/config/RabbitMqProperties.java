package br.com.fiapx.processor.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app.rabbitmq")
public record RabbitMqProperties(
    String exchange,
    String queue,
    String routingKeyVideoRequested,
    String routingKeyVideoCompleted,
    String routingKeyVideoFailed
) {
}
