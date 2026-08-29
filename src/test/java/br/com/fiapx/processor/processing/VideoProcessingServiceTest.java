package br.com.fiapx.processor.processing;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import br.com.fiapx.processor.config.StorageProperties;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class VideoProcessingServiceTest {

    @TempDir
    Path tempDir;

    private VideoProcessingService videoProcessingService;

    @BeforeEach
    void setUp() throws Exception {
        Path script = Path.of("src/test/resources/scripts/fake-ffmpeg.sh").toAbsolutePath().normalize();
        StorageProperties storageProperties = new StorageProperties(tempDir.resolve("output").toString());
        FfmpegFrameExtractor ffmpegFrameExtractor = new FfmpegFrameExtractor(script.toString());
        ZipArchiveService zipArchiveService = new ZipArchiveService();
        videoProcessingService = new VideoProcessingService(
            storageProperties,
            ffmpegFrameExtractor,
            zipArchiveService,
            1.0
        );
    }

    @Test
    void shouldGenerateZipFromVideo() throws Exception {
        UUID jobId = UUID.randomUUID();
        Path video = tempDir.resolve("input.mp4");
        Files.writeString(video, "video");

        String outputPath = videoProcessingService.process(jobId, video.toAbsolutePath().toString());

        assertThat(Files.exists(Path.of(outputPath))).isTrue();
        assertThat(Files.size(Path.of(outputPath))).isGreaterThan(0);
    }

    @Test
    void shouldRejectMissingVideo() {
        UUID jobId = UUID.randomUUID();

        assertThatThrownBy(() -> videoProcessingService.process(jobId, tempDir.resolve("missing.mp4").toString()))
            .isInstanceOf(IOException.class);
    }
}
