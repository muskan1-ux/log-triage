package com.logtriage;

import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.openai.OpenAiChatModel;
import dev.langchain4j.service.AiServices;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AppConfig {

    @Bean
    public ChatModel chatModel(
            @Value("${openai.api-key}") String apiKey,
            @Value("${openai.model:gpt-4o-mini}") String model) {

        return OpenAiChatModel.builder()
                .apiKey(apiKey)
                .modelName(model)
                .temperature(0.1)
                .maxRetries(0) // Resilience4j (in LogConsumer) owns the retry policy instead
                .build();
    }

    @Bean
    public LogTriageService logTriageService(ChatModel chatModel) {
        return AiServices.builder(LogTriageService.class)
                .chatModel(chatModel)
                .build();
    }
}
