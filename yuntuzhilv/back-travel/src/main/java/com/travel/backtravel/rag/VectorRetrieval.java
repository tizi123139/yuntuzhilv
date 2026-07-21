package com.travel.backtravel.rag;

import dev.langchain4j.data.document.Document;
import dev.langchain4j.data.document.Metadata;
import dev.langchain4j.data.document.parser.TextDocumentParser;
import dev.langchain4j.data.document.splitter.DocumentSplitters;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.model.openai.OpenAiEmbeddingModel;
import dev.langchain4j.retriever.EmbeddingStoreRetriever;
import dev.langchain4j.retriever.Retriever;
import dev.langchain4j.store.embedding.EmbeddingStore;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class VectorRetrieval {

    private final EmbeddingStore<Document> embeddingStore;

    @Value("${langchain.qwen.api-key}")
    private String apiKey;

    @Value("${langchain.qwen.base-url}")
    private String baseUrl;

    public void addDocument(String content, String metadataKey, String metadataValue) {
        try {
            Document document = Document.from(
                    content,
                    new TextDocumentParser(),
                    Metadata.from(metadataKey, metadataValue)
            );
            
            List<Document> chunks = DocumentSplitters.recursive(500, 50).split(document);
            
            EmbeddingModel embeddingModel = OpenAiEmbeddingModel.builder()
                    .apiKey(apiKey)
                    .baseUrl(baseUrl)
                    .modelName("text-embedding-ada-002")
                    .build();
            
            embeddingStore.add(chunks, embeddingModel);
            log.info("Document added to vector store: {}", metadataValue);
        } catch (Exception e) {
            log.error("Failed to add document to vector store", e);
        }
    }

    public List<String> search(String query, int maxResults) {
        try {
            EmbeddingModel embeddingModel = OpenAiEmbeddingModel.builder()
                    .apiKey(apiKey)
                    .baseUrl(baseUrl)
                    .modelName("text-embedding-ada-002")
                    .build();
            
            Retriever<Document> retriever = EmbeddingStoreRetriever.builder()
                    .embeddingStore(embeddingStore)
                    .embeddingModel(embeddingModel)
                    .maxResults(maxResults)
                    .build();
            
            List<Document> results = retriever.retrieve(query);
            return results.stream()
                    .map(Document::text)
                    .toList();
        } catch (Exception e) {
            log.error("Failed to search vector store", e);
            return List.of();
        }
    }
}
