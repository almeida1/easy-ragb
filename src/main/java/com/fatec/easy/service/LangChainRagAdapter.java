package com.fatec.easy.service;

import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;

/**
 * Adaptador simples para o fluxo Easy RAG do LangChain4j.
 * A recuperação e a geração passam a ser tratadas pelo assistente com retriever
 * configurado, sem depender de um contexto manual placeholder.
 */
@Service
public class LangChainRagAdapter implements IRetrievalPort, IAnswerGenerationPort {
    private final Logger logger = LogManager.getLogger(this.getClass());
    private final IRagAssistante ragAssistante;

    public LangChainRagAdapter(IRagAssistante ragAssistante) {
        this.ragAssistante = ragAssistante;
    }

    @Override
    public List<String> retrieve(String question) {
        logger.info(">>>>>> Recuperando contexto para a pergunta: {}", question);
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
