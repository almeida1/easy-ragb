package com.fatec.easy.service;

import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class LangChainRagAdapter implements RetrievalPort, AnswerGenerationPort {

    private final RagAssistant ragAssistant;

    public LangChainRagAdapter(RagAssistant ragAssistant) {
        this.ragAssistant = ragAssistant;
    }

    @Override
    public List<String> retrieve(String question) {
        return List.of("Contexto recuperado automaticamente para: " + question);
    }

    @Override
    public String generate(String question, List<String> context) {
        if (context == null || context.isEmpty()) {
            return "Não há contexto suficiente para responder com precisão.";
        }
        return ragAssistant.chat(question);
    }

}
