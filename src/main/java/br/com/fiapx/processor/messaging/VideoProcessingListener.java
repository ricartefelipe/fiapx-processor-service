package br.com.fiapx.processor.messaging;

import br.com.fiapx.processor.config.RabbitMqProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Component
public class VideoProcessingListener {

    private static final Logger log = LoggerFactory.getLogger(VideoProcessingListener.class);

    private final RabbitTemplate rabbitTemplate;
    private final RabbitMqProperties properties;

    public VideoProcessingListener(RabbitTemplate rabbitTemplate, RabbitMqProperties properties) {
        this.rabbitTemplate = rabbitTemplate;
        this.properties = properties;
    }

    @RabbitListener(queues = "${app.rabbitmq.queue}")
    public void handleVideoRequested(VideoRequestedEvent event) {
        log.info("Processamento solicitado para job {}", event.jobId());
        rabbitTemplate.convertAndSend(
            properties.exchange(),
            properties.routingKeyVideoCompleted(),
            new VideoCompletedEvent(event.jobId(), event.storagePath() + ".zip")
        );
    }

    public record VideoCompletedEvent(java.util.UUID jobId, String outputPath) {
    }
}
