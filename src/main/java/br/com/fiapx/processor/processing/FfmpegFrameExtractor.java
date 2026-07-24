package br.com.fiapx.processor.processing;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class FfmpegFrameExtractor {

    private final String ffmpegCommand;

    public FfmpegFrameExtractor(@Value("${app.processing.ffmpeg-command:ffmpeg}") String ffmpegCommand) {
        this.ffmpegCommand = ffmpegCommand;
    }

    public List<Path> extractFrames(Path videoPath, Path framesDir, double fps) throws IOException, InterruptedException {
        Files.createDirectories(framesDir);
        Path outputPattern = framesDir.resolve("frame_%04d.jpg");
        ProcessBuilder processBuilder = new ProcessBuilder(
            ffmpegCommand,
            "-y",
            "-i", videoPath.toAbsolutePath().toString(),
            "-vf", "fps=" + fps,
            outputPattern.toAbsolutePath().toString()
        );
        processBuilder.redirectErrorStream(true);
        Process process = processBuilder.start();
        String output = new String(process.getInputStream().readAllBytes());
        int exitCode = process.waitFor();
        if (exitCode != 0) {
            throw new IOException("FFmpeg falhou com código " + exitCode + ": " + output);
        }
        try (var stream = Files.list(framesDir)) {
            List<Path> frames = new ArrayList<>(stream.filter(path -> path.toString().endsWith(".jpg")).sorted().toList());
            if (frames.isEmpty()) {
                throw new IOException("Nenhum frame extraído");
            }
            return frames;
        }
    }
}
