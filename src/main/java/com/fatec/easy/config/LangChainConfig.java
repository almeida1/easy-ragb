package com.fatec.easy.config;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fatec.easy.service.IRagAssistante;
import com.fatec.easy.service.IRagJudgeAssistant;

import dev.langchain4j.data.document.splitter.DocumentSplitters;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.model.embedding.onnx.allminilml6v2.AllMiniLmL6V2EmbeddingModel;
import dev.langchain4j.model.openai.OpenAiChatModel;
import dev.langchain4j.rag.content.retriever.ContentRetriever;
import dev.langchain4j.rag.content.retriever.EmbeddingStoreContentRetriever;
import dev.langchain4j.service.AiServices;
import dev.langchain4j.store.embedding.EmbeddingStore;
import dev.langchain4j.store.embedding.EmbeddingStoreIngestor;
import dev.langchain4j.store.embedding.inmemory.InMemoryEmbeddingStore;
import java.time.Duration;

@Configuration
public class LangChainConfig {

    private final Logger logger = LogManager.getLogger(this.getClass());

    @Value("${app.ai.chat.base-url}")
    private String baseUrl;

    @Value("${app.ai.chat.api-key}")
    private String apiKey;

    @Value("${app.ai.chat.model-name}")
    private String chatModelName;

    @Value("${app.ai.chat.temperature:0.0}")
    private Double temperature;

    @Value("${app.ai.chat.timeout-seconds:180}")
    private Long timeoutSeconds;

    @Value("${app.ai.rag.max-results:3}")
    private Integer maxResults;

    @Value("${app.ai.rag.min-score:0.5}")
    private Double minScore;

    @Value("${app.ai.rag.chunk-size:500}")
    private Integer chunkSize;

    @Value("${app.ai.rag.chunk-overlap:50}")
    private Integer chunkOverlap;

    @Bean
    public ChatLanguageModel chatLanguageModel() {
        logger.info(">>>>>> Configurando o modelo de chat: {}", chatModelName);
        return OpenAiChatModel.builder()
                .baseUrl(baseUrl)
                .apiKey(apiKey)
                .modelName(chatModelName)
                .temperature(temperature)
                .timeout(Duration.ofSeconds(timeoutSeconds))
                .maxRetries(1)
                .logRequests(true)
                .logResponses(true)
                .build();
    }

    @Bean
    public EmbeddingModel embeddingModel() {
        logger.info(">>>>>> Configurando modelo de embedding local: AllMiniLmL6V2");
        return new AllMiniLmL6V2EmbeddingModel();
    }

    @Bean
    public EmbeddingStore<TextSegment> embeddingStore() {
        return new InMemoryEmbeddingStore<>();
    }

    @Bean
    public EmbeddingStoreIngestor embeddingStoreIngestor(EmbeddingModel embeddingModel,
            EmbeddingStore<TextSegment> embeddingStore) {
        return EmbeddingStoreIngestor.builder()
                .documentSplitter(DocumentSplitters.recursive(chunkSize, chunkOverlap))
                .embeddingModel(embeddingModel)
                .embeddingStore(embeddingStore)
                .build();
    }

    @Bean
    public ContentRetriever contentRetriever(EmbeddingStore<TextSegment> embeddingStore,
            EmbeddingModel embeddingModel) {
        return EmbeddingStoreContentRetriever.builder()
                .embeddingStore(embeddingStore)
                .embeddingModel(embeddingModel)
                .maxResults(maxResults)
                .minScore(minScore)
                .build();
    }

    @Bean
    public IRagAssistante ragAssistant(ChatLanguageModel chatLanguageModel, ContentRetriever contentRetriever) {
        return AiServices.builder(IRagAssistante.class)
                .chatLanguageModel(chatLanguageModel)
                .contentRetriever(contentRetriever)
                .chatMemory(MessageWindowChatMemory.withMaxMessages(10))
                .build();
    }

    @Bean
    public IRagJudgeAssistant ragJudgeAssistant(ChatLanguageModel chatLanguageModel) {
        return AiServices.builder(IRagJudgeAssistant.class)
                .chatLanguageModel(chatLanguageModel)
                .build();
    }

    @Bean
    public ObjectMapper objectMapper() {
        return new ObjectMapper();
    }
}