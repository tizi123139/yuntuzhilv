package com.travel.backtravel.rag;

import dev.langchain4j.data.document.Document;
import dev.langchain4j.data.document.Metadata;
import dev.langchain4j.data.document.splitter.DocumentSplitters;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.model.openai.OpenAiEmbeddingModel;
import dev.langchain4j.rag.content.retriever.EmbeddingStoreContentRetriever;
import dev.langchain4j.store.embedding.EmbeddingStore;
import dev.langchain4j.store.embedding.inmemory.InMemoryEmbeddingStore;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Slf4j
@Component
public class VectorRetrieval {

    @Value("${ai.pai.api-key:}")
    private String apiKey;

    @Value("${ai.pai.base-url:https://dashscope.aliyuncs.com/compatible-mode/v1}")
    private String baseUrl;

    @Value("${ai.pai.embedding-model:text-embedding-v2}")
    private String embeddingModelName;

    // 内存向量库
    final EmbeddingStore<TextSegment> attractionStore = new InMemoryEmbeddingStore<>();
    final EmbeddingStore<TextSegment> hotelStore = new InMemoryEmbeddingStore<>();

    // 缓存单例，避免每次搜索都重新创建
    private EmbeddingModel cachedEmbeddingModel;

    @PostConstruct
    public void init() {
        cachedEmbeddingModel = OpenAiEmbeddingModel.builder()
                .apiKey(apiKey)
                .baseUrl(baseUrl)
                .modelName(embeddingModelName)
                .build();
        log.info("EmbeddingModel 初始化完成: {}", embeddingModelName);
    }

    private EmbeddingModel getEmbeddingModel() {
        return cachedEmbeddingModel;
    }


}
