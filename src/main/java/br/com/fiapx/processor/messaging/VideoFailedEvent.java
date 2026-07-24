package br.com.fiapx.processor.messaging;

import java.util.UUID;

public record VideoFailedEvent(UUID jobId, String errorMessage) {
}
