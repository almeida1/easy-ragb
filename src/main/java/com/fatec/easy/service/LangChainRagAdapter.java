package com.fatec.easy.service;

import java.util.List;
import org.springframework.stereotype.Service;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
/**
 * Recupera o contexto relevante para uma pergunta e gera uma resposta utilizando o LangChain4j.
 * Atuar como um adaptador entre o fluxo de aplicação RAG Triad e o 
 * componente de geração/resposta do LangChain4j.
 */
@Service
public class LangChainRagAdapter implements IRetrievalPort, IAnswerGenerationPort {
    private final Logger logger = LogManager.getLogger(this.getClass());
    private final RagAssistant ragAssistant;

    public LangChainRagAdapter(RagAssistant ragAssistant) {
        this.ragAssistant = ragAssistant;
    }

    @Override
    public List<String> retrieve(String question) {
        logger.info(">>>>>> Recuperando contexto para a pergunta: {}", question);   
        return List.of("Contexto recuperado automaticamente para: " + question);
    }
    /**
     * Gera uma resposta para a pergunta fornecida, utilizando o contexto recuperado.
     * Se o contexto estiver vazio, retorna uma mensagem indicando que não há contexto suficiente.
     */
    @Override
    public String generate(String question, List<String> context) {
        logger.info(">>>>>> Geração de Resposta RAG: question: {}, context: {}", question, context);
        if (context == null || context.isEmpty()) {
            return "Não há contexto suficiente para responder com precisão.";
        }
        return ragAssistant.chat(question);
    }

}
