package com.logtriage;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class LogConsumer {

    private final LogTriageService triageService;

    @KafkaListener(topics = "logs.raw", groupId = "log-triage")
    public void onMessage(LogEvent event) {
        if (event == null) {
            log.warn("Received null event, skipping");
            return;
        }
        TriageResult result = triage(event);
        log.info("Triaged [{}] severity={} rootCause={} -> {}",
                event.eventId(), result.severity(), result.rootCause(), result.suggestedAction());
    }

    /**
     * Circuit breaker opens after repeated LLM failures and short-circuits straight to the
     * fallback instead of hammering a struggling provider; Retry handles brief blips first.
     */
    @CircuitBreaker(name = "logTriage", fallbackMethod = "fallback")
    @Retry(name = "logTriage")
    public TriageResult triage(LogEvent event) {
        return triageService.triage(
                event.service(),
                event.level(),
                event.message(),
                event.stackTrace() == null ? "" : event.stackTrace());
    }

    @SuppressWarnings("unused")
    private TriageResult fallback(LogEvent event, Throwable throwable) {
        log.warn("LLM triage unavailable for {}: {}", event.eventId(), throwable.toString());
        return TriageResult.fallback(throwable.getClass().getSimpleName());
    }
}
