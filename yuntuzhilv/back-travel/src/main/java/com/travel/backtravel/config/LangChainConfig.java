package com.travel.backtravel.config;

import dev.langchain4j.data.document.Document;
import dev.langchain4j.data.document.splitter.DocumentSplitters;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.openai.OpenAiChatModel;
import dev.langchain4j.store.embedding.EmbeddingStore;
import dev.langchain4j.store.embedding.chroma.ChromaEmbeddingStore;
import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.net.URI;
import java.util.List;

@Configuration
@Data
public class LangChainConfig {

    @Value("${langchain.qwen.api-key}")
    private String qwenApiKey;

    @Value("${langchain.qwen.base-url}")
    private String qwenBaseUrl;

    @Value("${langchain.chroma.host}")
    private String chromaHost;

    @Value("${langchain.chroma.port}")
    private Integer chromaPort;

    @Value("${langchain.chroma.collection-name}")
    private String collectionName;

    @Bean
    public ChatLanguageModel chatLanguageModel() {
        return OpenAiChatModel.builder()
                .apiKey(qwenApiKey)
                .baseUrl(qwenBaseUrl)
                .modelName("qwen-plus")
                .temperature(0.7)
                .build();
    }

    @Bean
    public EmbeddingStore<Document> embeddingStore() {
        return ChromaEmbeddingStore.builder()
                .baseUrl(URI.create("http://" + chromaHost + ":" + chromaPort))
                .collectionName(collectionName)
                .build();
    }
}
