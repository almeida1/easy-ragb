package com.fatec.easy.service;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import org.junit.jupiter.api.Test;

class RagTriadServiceTest {

    @Test
    void shouldComposeAnswerAndEvaluationUsingPorts() {
        RetrievalPort retrievalPort = question -> List.of("Contexto A", "Contexto B");
        AnswerGenerationPort generationPort = (question, context) -> "Resposta gerada para " + question;
        EvaluationPort evaluationPort = (question, answer, context) -> new EvaluationResult(
                true,
                "Boa resposta",
                0.91,
                "O conteúdo recuperado foi suficiente para sustentar a resposta");

        RagTriadService service = new DefaultRagTriadService(retrievalPort, generationPort, evaluationPort);

        RagAnswerResponse response = service.answer("Qual é a pergunta?");

        assertThat(response.answer()).isEqualTo("Resposta gerada para Qual é a pergunta?");
        assertThat(response.context()).containsExactly("Contexto A", "Contexto B");
        assertThat(response.evaluation().isAccurate()).isTrue();
        assertThat(response.evaluation().score()).isEqualTo(0.91);
    }
}
