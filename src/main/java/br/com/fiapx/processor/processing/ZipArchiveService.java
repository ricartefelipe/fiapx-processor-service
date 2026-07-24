package br.com.fiapx.processor.processing;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import org.springframework.stereotype.Service;

@Service
public class ZipArchiveService {

    public Path createZip(Path zipPath, List<Path> files) throws IOException {
        Files.createDirectories(zipPath.getParent());
        try (OutputStream outputStream = Files.newOutputStream(zipPath);
             ZipOutputStream zipOutputStream = new ZipOutputStream(outputStream)) {
            for (Path file : files) {
                ZipEntry entry = new ZipEntry(file.getFileName().toString());
                zipOutputStream.putNextEntry(entry);
                Files.copy(file, zipOutputStream);
                zipOutputStream.closeEntry();
            }
        }
        return zipPath;
    }
}
