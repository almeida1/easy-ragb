package com.fatec.easy.service;

import java.util.List;
import org.springframework.stereotype.Service;

import com.fatec.easy.model.EvaluationResult;
import com.fatec.easy.model.RagAnswerResponse;

/**
 * Implementacao default do servico RAG triad.
 * Para a abordagem Easy RAG, a resposta e o contexto sao obtidos diretamente
 * pelo assistente configurado com um content retriever do LangChain4j.
 */
@Service
public class DefaultRagTriadService implements IRagTriadService {

    private final IEvaluationPort evaluationPort;
    private final IRetrievalPort retrievalPort;
    private final IAnswerGenerationPort answerGenerationPort;

    public DefaultRagTriadService(
            IEvaluationPort evaluationPort,
            IRetrievalPort retrievalPort,
            IAnswerGenerationPort answerGenerationPort) {

        this.evaluationPort = evaluationPort;
        this.retrievalPort = retrievalPort;
        this.answerGenerationPort = answerGenerationPort;
    }

    @Override
    public RagAnswerResponse answer(String question) {
        List<String> context = retrievalPort.retrieve(question);
        String answer = answerGenerationPort.generate(question, context);
        EvaluationResult evaluation = evaluationPort.evaluate(question, answer, context);
        return new RagAnswerResponse(answer, context, evaluation);
    }
}
