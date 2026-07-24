package br.com.fiapx.processor.messaging;

import java.util.UUID;

public record VideoRequestedEvent(
    UUID jobId,
    UUID userId,
    String originalFilename,
    String storagePath
) {
}
