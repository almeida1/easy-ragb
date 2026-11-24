package com.fatec.easy.service;

import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;

import dev.langchain4j.rag.content.Content;
import dev.langchain4j.rag.content.retriever.ContentRetriever;
import dev.langchain4j.rag.query.Query;

/**
 * Adaptador para o fluxo RAG do LangChain4j.
 * Integra a recuperação de contexto através do ContentRetriever e a geração
 * da resposta através do assistente IRagAssistante.
 */
@Service
public class LangChainRagAdapter implements IRetrievalPort, IAnswerGenerationPort {
    private final Logger logger = LogManager.getLogger(this.getClass());
    private final IRagAssistante ragAssistante;
    private final ContentRetriever contentRetriever;

    public LangChainRagAdapter(IRagAssistante ragAssistante, ContentRetriever contentRetriever) {
        this.ragAssistante = ragAssistante;
        this.contentRetriever = contentRetriever;
    }

    /**
     * Recupera trechos de contexto relevantes utilizando o ContentRetriever.
     * Caso o ContentRetriever não esteja presente (em testes unitários isolados),
     * retorna um contexto padrão simulado.
     */
    @Override
    public List<String> retrieve(String question) {
        logger.info(">>>>>> Recuperando contexto para a pergunta: {}", question);
        if (contentRetriever != null) {
            try {
                List<Content> contents = contentRetriever.retrieve(Query.from(question));
                if (contents != null && !contents.isEmpty()) {
                    return contents.stream()
                            .map(c -> c.textSegment().text())
                            .toList();
                }
                return List.of("Nenhum contexto relevante foi encontrado no repositório para a pergunta: " + question);
            } catch (Exception e) {
                logger.warn("Falha ao recuperar contexto do ContentRetriever: {}", e.getMessage());
            }
        }
        return List.of("Contexto recuperado automaticamente pela abordagem Easy RAG para: " + question);
    }

    @Override
    public String generate(String question, List<String> context) {
        logger.info(">>>>>> Geração de Resposta RAG: question: {}, context: {}", question, context);
        if (context == null || context.isEmpty()) {
            return "Não há contexto suficiente para responder com precisão.";
        }
        return ragAssistante.chat(question);
    }
}
