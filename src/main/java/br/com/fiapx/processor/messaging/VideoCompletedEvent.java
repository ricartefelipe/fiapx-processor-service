package br.com.fiapx.processor.messaging;

import java.util.UUID;

public record VideoCompletedEvent(UUID jobId, String outputPath) {
}
