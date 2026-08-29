package br.com.fiapx.processor.messaging;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;

import br.com.fiapx.processor.support.AbstractIntegrationTest;
import br.com.fiapx.processor.support.IntegrationTestRabbitConfig;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;

class VideoProcessingListenerIntegrationTest extends AbstractIntegrationTest {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Test
    void shouldProcessVideoAndPublishCompleted(@TempDir Path tempDir) throws Exception {
        Path video = tempDir.resolve("sample.mp4");
        Files.writeString(video, "fake-video");
        UUID jobId = UUID.randomUUID();
        VideoRequestedEvent event = new VideoRequestedEvent(
            jobId,
            UUID.randomUUID(),
            "sample.mp4",
            video.toAbsolutePath().toString()
        );

        rabbitTemplate.convertAndSend("fiapx.events", "video.requested", event);

        await().atMost(Duration.ofSeconds(20)).untilAsserted(() -> {
            Message processing = rabbitTemplate.receive(IntegrationTestRabbitConfig.PROCESSING_SPY_QUEUE, 200);
            assertThat(processing).isNotNull();

            Message completed = rabbitTemplate.receive(IntegrationTestRabbitConfig.COMPLETED_SPY_QUEUE, 200);
            assertThat(completed).isNotNull();
            Object payload = rabbitTemplate.getMessageConverter().fromMessage(completed);
            assertThat(payload).isInstanceOf(VideoCompletedEvent.class);
            VideoCompletedEvent completedEvent = (VideoCompletedEvent) payload;
            assertThat(completedEvent.jobId()).isEqualTo(jobId);
            assertThat(Files.exists(Path.of(completedEvent.outputPath()))).isTrue();
        });
    }

    @Test
    void shouldPublishFailedWhenVideoMissing() {
        UUID jobId = UUID.randomUUID();
        VideoRequestedEvent event = new VideoRequestedEvent(
            jobId,
            UUID.randomUUID(),
            "missing.mp4",
            "/caminho/inexistente/missing.mp4"
        );

        rabbitTemplate.convertAndSend("fiapx.events", "video.requested", event);

        await().atMost(Duration.ofSeconds(20)).untilAsserted(() -> {
            Message failed = rabbitTemplate.receive(IntegrationTestRabbitConfig.FAILED_SPY_QUEUE, 200);
            assertThat(failed).isNotNull();
            Object payload = rabbitTemplate.getMessageConverter().fromMessage(failed);
            assertThat(payload).isInstanceOf(VideoFailedEvent.class);
            VideoFailedEvent failedEvent = (VideoFailedEvent) payload;
            assertThat(failedEvent.jobId()).isEqualTo(jobId);
            assertThat(failedEvent.errorMessage()).isNotBlank();
        });
    }
}
