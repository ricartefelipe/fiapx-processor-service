package br.com.fiapx.processor.processing;

import static org.assertj.core.api.Assertions.assertThat;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class ZipArchiveServiceTest {

    private final ZipArchiveService zipArchiveService = new ZipArchiveService();

    @Test
    void shouldCreateZipWithFrames(@TempDir Path tempDir) throws Exception {
        Path frame = tempDir.resolve("frame_0001.jpg");
        Files.writeString(frame, "frame");
        Path zipPath = tempDir.resolve("output.zip");

        Path result = zipArchiveService.createZip(zipPath, List.of(frame));

        assertThat(Files.exists(result)).isTrue();
        assertThat(Files.size(result)).isGreaterThan(0);
    }
}
