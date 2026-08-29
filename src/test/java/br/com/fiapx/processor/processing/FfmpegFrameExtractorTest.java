package br.com.fiapx.processor.processing;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import br.com.fiapx.processor.config.StorageProperties;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class FfmpegFrameExtractorTest {

    @TempDir
    Path tempDir;

    private FfmpegFrameExtractor ffmpegFrameExtractor;

    @BeforeEach
    void setUp() {
        Path script = Path.of("src/test/resources/scripts/fake-ffmpeg.sh").toAbsolutePath().normalize();
        ffmpegFrameExtractor = new FfmpegFrameExtractor(script.toString());
    }

    @Test
    void shouldExtractFramesUsingFakeFfmpeg() throws Exception {
        Path video = tempDir.resolve("video.mp4");
        Files.writeString(video, "conteudo");
        Path framesDir = tempDir.resolve("frames");

        List<Path> frames = ffmpegFrameExtractor.extractFrames(video, framesDir, 1.0);

        assertThat(frames).hasSize(1);
        assertThat(Files.exists(frames.getFirst())).isTrue();
    }

    @Test
    void shouldFailWhenFfmpegReturnsError() {
        FfmpegFrameExtractor failingExtractor = new FfmpegFrameExtractor("false");
        Path video = tempDir.resolve("video.mp4");

        assertThatThrownBy(() -> failingExtractor.extractFrames(video, tempDir.resolve("frames"), 1.0))
            .isInstanceOf(Exception.class);
    }
}
