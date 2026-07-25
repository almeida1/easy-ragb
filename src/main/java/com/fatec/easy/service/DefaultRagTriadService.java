package com.fatec.easy.service;

import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class DefaultRagTriadService implements RagTriadService {

    private final RetrievalPort retrievalPort;
    private final AnswerGenerationPort generationPort;
    private final EvaluationPort evaluationPort;

    public DefaultRagTriadService(RetrievalPort retrievalPort,
            AnswerGenerationPort generationPort,
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
