package com.fatec.easy.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;

import com.fatec.easy.model.EvaluationResult;
import com.fatec.easy.model.RagAnswerResponse;

class DefaultRagTriadServiceTest {

    @Test
    void answerUsesTheAssistantAndReturnsAnEasyRagResponse() {
        IRagAssistante assistant = message -> "Resposta baseada no contexto recuperado";
        IEvaluationPort evaluationPort = (question, answer, context) -> new EvaluationResult(
                true,
                "Resposta alinhada ao contexto",
                0.95,
                "contexto recuperado"
        );

        // Adapta a interface do assistente para as portas de Retrieval e Generation
        var adapter = new LangChainRagAdapter(assistant);

        // Passa as três portas exigidas pelo novo construtor
        DefaultRagTriadService service = new DefaultRagTriadService(
                evaluationPort,
                adapter,  // IRetrievalPort
                adapter   // IAnswerGenerationPort
        );

        RagAnswerResponse response = service.answer("Explique o conceito de RAG");

        assertEquals("Resposta baseada no contexto recuperado", response.answer());
        assertNotNull(response.context());
        assertFalse(response.context().isEmpty());
        assertEquals(true, response.evaluation().isAccurate());
    }
}