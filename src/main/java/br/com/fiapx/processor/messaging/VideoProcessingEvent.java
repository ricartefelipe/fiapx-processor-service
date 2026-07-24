package br.com.fiapx.processor.messaging;

import java.util.UUID;

public record VideoProcessingEvent(UUID jobId) {
}
