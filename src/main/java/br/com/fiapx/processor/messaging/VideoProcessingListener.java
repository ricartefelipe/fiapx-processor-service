package br.com.fiapx.processor.messaging;

import br.com.fiapx.processor.config.RabbitMqProperties;
import br.com.fiapx.processor.processing.VideoProcessingService;
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
    private final VideoProcessingService videoProcessingService;

    public VideoProcessingListener(
        RabbitTemplate rabbitTemplate,
        RabbitMqProperties properties,
        VideoProcessingService videoProcessingService
    ) {
        this.rabbitTemplate = rabbitTemplate;
        this.properties = properties;
        this.videoProcessingService = videoProcessingService;
    }

    @RabbitListener(queues = "${app.rabbitmq.queue}")
    public void handleVideoRequested(VideoRequestedEvent event) {
        log.info("Processamento solicitado para job {}", event.jobId());
        rabbitTemplate.convertAndSend(
            properties.exchange(),
            properties.routingKeyVideoProcessing(),
            new VideoProcessingEvent(event.jobId())
        );
        try {
            String outputPath = videoProcessingService.process(event.jobId(), event.storagePath());
            rabbitTemplate.convertAndSend(
                properties.exchange(),
                properties.routingKeyVideoCompleted(),
                new VideoCompletedEvent(event.jobId(), outputPath)
            );
        } catch (Exception exception) {
            log.error("Falha ao processar job {}", event.jobId(), exception);
            rabbitTemplate.convertAndSend(
                properties.exchange(),
                properties.routingKeyVideoFailed(),
                new VideoFailedEvent(event.jobId(), exception.getMessage())
            );
        }
    }
}
