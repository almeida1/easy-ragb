package com.fatec.easy.service;

import java.util.List;
import org.springframework.stereotype.Service;
/**
 * Implementacao default do servico RAG triad.
 * Avalia a resposta gerada com base na pergunta e no contexto recuperado.
 */
@Service
public class DefaultRagTriadService implements IRagTriadService {

    private final IRetrievalPort retrievalPort;
    private final IAnswerGenerationPort generationPort;
    private final EvaluationPort evaluationPort;

    public DefaultRagTriadService(IRetrievalPort retrievalPort,
            IAnswerGenerationPort generationPort,
            EvaluationPort evaluationPort) {
        this.retrievalPort = retrievalPort;
        this.generationPort = generationPort;
        this.evaluationPort = evaluationPort;
    }

    @Override
    public RagAnswerResponse answer(String question) {
        List<String> context = retrievalPort.retrieve(question);
        String answer = generationPort.generate(question, context);
        EvaluationResult evaluation = evaluationPort.evaluate(question, answer, context);
        return new RagAnswerResponse(answer, context, evaluation);
    }
}
