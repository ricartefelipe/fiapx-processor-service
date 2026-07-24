package br.com.fiapx.processor.processing;

import br.com.fiapx.processor.config.StorageProperties;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class VideoProcessingService {

    private final StorageProperties storageProperties;
    private final FfmpegFrameExtractor ffmpegFrameExtractor;
    private final ZipArchiveService zipArchiveService;
    private final double fps;

    public VideoProcessingService(
        StorageProperties storageProperties,
        FfmpegFrameExtractor ffmpegFrameExtractor,
        ZipArchiveService zipArchiveService,
        @Value("${app.processing.fps:1}") double fps
    ) throws IOException {
        this.storageProperties = storageProperties;
        this.ffmpegFrameExtractor = ffmpegFrameExtractor;
        this.zipArchiveService = zipArchiveService;
        this.fps = fps;
        Files.createDirectories(Path.of(storageProperties.outputDir()));
    }

    public String process(UUID jobId, String storagePath) throws IOException, InterruptedException {
        Path videoPath = Path.of(storagePath);
        if (!Files.exists(videoPath)) {
            throw new IOException("Vídeo não encontrado: " + storagePath);
        }
        Path workDir = Path.of(storageProperties.outputDir()).resolve(jobId.toString());
        Path framesDir = workDir.resolve("frames");
        try {
            List<Path> frames = ffmpegFrameExtractor.extractFrames(videoPath, framesDir, fps);
            Path zipPath = workDir.resolve(jobId + ".zip");
            zipArchiveService.createZip(zipPath, frames);
            return zipPath.toAbsolutePath().toString();
        } finally {
            cleanupDirectory(framesDir);
        }
    }

    private void cleanupDirectory(Path directory) throws IOException {
        if (!Files.exists(directory)) {
            return;
        }
        try (var paths = Files.walk(directory)) {
            paths.sorted(Comparator.reverseOrder()).forEach(path -> {
                try {
                    Files.deleteIfExists(path);
                } catch (IOException ignored) {
                }
            });
        }
    }
}
