package br.com.fiapx.processor.messaging;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import br.com.fiapx.processor.config.RabbitMqProperties;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

@ExtendWith(MockitoExtension.class)
class VideoProcessingListenerTest {

    @Mock
    private RabbitTemplate rabbitTemplate;

    @Mock
    private br.com.fiapx.processor.processing.VideoProcessingService videoProcessingService;

    private VideoProcessingListener listener;

    @BeforeEach
    void setUp() {
        RabbitMqProperties properties = new RabbitMqProperties(
            "fiapx.events",
            "video.processing",
            "video.requested",
            "video.completed",
            "video.failed"
        );
        listener = new VideoProcessingListener(rabbitTemplate, properties, videoProcessingService);
    }

    @Test
    void shouldPublishCompletedEvent() throws Exception {
        UUID jobId = UUID.randomUUID();
        VideoRequestedEvent event = new VideoRequestedEvent(jobId, UUID.randomUUID(), "clip.mp4", "/tmp/clip.mp4");
        when(videoProcessingService.process(jobId, event.storagePath())).thenReturn("/tmp/output/" + jobId + ".zip");

        listener.handleVideoRequested(event);

        verify(rabbitTemplate).convertAndSend(
            eq("fiapx.events"),
            eq("video.completed"),
            eq(new VideoCompletedEvent(jobId, "/tmp/output/" + jobId + ".zip"))
        );
    }

    @Test
    void shouldPublishFailedEventWhenProcessingFails() throws Exception {
        UUID jobId = UUID.randomUUID();
        VideoRequestedEvent event = new VideoRequestedEvent(jobId, UUID.randomUUID(), "clip.mp4", "/tmp/clip.mp4");
        when(videoProcessingService.process(jobId, event.storagePath())).thenThrow(new RuntimeException("ffmpeg indisponível"));

        listener.handleVideoRequested(event);

        verify(rabbitTemplate).convertAndSend(
            eq("fiapx.events"),
            eq("video.failed"),
            eq(new VideoFailedEvent(jobId, "ffmpeg indisponível"))
        );
    }
}
