package com.logtriage;

import java.time.Instant;

/** A single log line as it arrives on the Kafka topic. */
public record LogEvent(
        String eventId,
        String service,
        String level,
        String message,
        String stackTrace,
        Instant timestamp
) {
    public LogEvent {
        if (timestamp == null) {
            timestamp = Instant.now();
        }
    }
}
