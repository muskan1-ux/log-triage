package com.logtriage;

import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;
import dev.langchain4j.service.V;

/** LangChain4j generates the implementation of this interface at runtime (see AppConfig). */
public interface LogTriageService {

    @SystemMessage("""
            You are an expert Site Reliability Engineer triaging application error logs.
            Be concise, technical, and decisive. Never invent details not in the log.
            """)
    @UserMessage("""
            Service: {{service}}
            Level: {{level}}
            Message: {{message}}
            Stack trace: {{stackTrace}}
            """)
    TriageResult triage(
            @V("service") String service,
            @V("level") String level,
            @V("message") String message,
            @V("stackTrace") String stackTrace
    );
}
