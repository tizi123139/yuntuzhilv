package com.travel.backtravel.config;

import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.openai.OpenAiChatModel;
import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

@Configuration
@Data
public class LangChainConfig {

    @Value("${ai.pai.api-key:}")
    private String paiApiKey;

    @Value("${ai.pai.base-url:https://dashscope.aliyuncs.com/compatible-mode/v1}")
    private String paiBaseUrl;

    @Value("${ai.pai.model:qwen-plus}")
    private String paiModel;

    @Value("${ai.pai.temperature:0.7}")
    private Double paiTemperature;

    @Value("${ai.pai.timeout:120000}")
    private Long paiTimeout;

    @Value("${ai.pai.max-tokens:2048}")
    private Integer paiMaxTokens;

    @Bean
    public ChatLanguageModel chatLanguageModel() {
        return OpenAiChatModel.builder()
                .apiKey(paiApiKey)
                .baseUrl(paiBaseUrl)
                .modelName(paiModel)
                .temperature(paiTemperature)
                .timeout(Duration.ofMillis(paiTimeout))
                .maxTokens(paiMaxTokens)
                .build();
    }
}
