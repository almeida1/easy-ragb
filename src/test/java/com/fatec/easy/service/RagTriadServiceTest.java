package com.fatec.easy.service;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import org.junit.jupiter.api.Test;

class RagTriadServiceTest {

    @Test
    void shouldComposeAnswerAndEvaluationUsingPorts() {
        IRetrievalPort retrievalPort = question -> List.of("Contexto A", "Contexto B");
        IAnswerGenerationPort generationPort = (question, context) -> "Resposta gerada para " + question;
        EvaluationPort evaluationPort = (question, answer, context) -> new EvaluationResult(
                true,
                "Boa resposta",
                0.91,
                "O conteúdo recuperado foi suficiente para sustentar a resposta");

        IRagTriadService service = new DefaultRagTriadService(retrievalPort, generationPort, evaluationPort);

        RagAnswerResponse response = service.answer("Qual é a pergunta?");

        assertThat(response.answer()).isEqualTo("Resposta gerada para Qual é a pergunta?");
        assertThat(response.context()).containsExactly("Contexto A", "Contexto B");
        assertThat(response.evaluation().isAccurate()).isTrue();
        assertThat(response.evaluation().score()).isEqualTo(0.91);
    }

    @Test
    void shouldReturnLowScoreWhenAnswerDoesNotMatchContext() {
        IRetrievalPort retrievalPort = question -> List.of("Contexto sobre políticas de desconto");
        IAnswerGenerationPort generationPort = (question, context) -> "Resposta improvisada sem relação com o contexto";
        EvaluationPort evaluationPort = (question, answer, context) -> new EvaluationResult(
                false,
                "A resposta não está alinhada com o contexto recuperado",
                0.12,
                "O conteúdo recuperado não sustenta a resposta fornecida");

        IRagTriadService service = new DefaultRagTriadService(retrievalPort, generationPort, evaluationPort);

        RagAnswerResponse response = service.answer("Qual é a política de desconto?");

        assertThat(response.answer()).isEqualTo("Resposta improvisada sem relação com o contexto");
        assertThat(response.evaluation().isAccurate()).isFalse();
        assertThat(response.evaluation().score()).isEqualTo(0.12);
        assertThat(response.evaluation().rationale()).contains("não está alinhada");
    }
}
