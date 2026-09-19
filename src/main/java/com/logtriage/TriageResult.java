package com.logtriage;

import dev.langchain4j.model.output.structured.Description;

/** Structured triage output produced by the LLM. */
public record TriageResult(

        @Description("Severity: LOW, MEDIUM, HIGH, or CRITICAL")
        String severity,

        @Description("Likely root cause category, e.g. DATABASE, NETWORK, NULL_POINTER, CONFIG, UNKNOWN")
        String rootCause,

        @Description("One sentence plain-language summary of the issue")
        String summary,

        @Description("A single concrete next step for the on-call engineer")
        String suggestedAction
) {
    /** Used when the LLM call fails and the circuit breaker falls back. */
    public static TriageResult fallback(String reason) {
        return new TriageResult("MEDIUM", "UNKNOWN",
                "Automated triage unavailable: " + reason,
                "Route to on-call engineer for manual review");
    }
}
