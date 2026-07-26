package com.fatec.easy.service;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

import com.fatec.easy.model.EvaluationResult;
import com.fatec.easy.model.RagAnswerResponse;

class RagTriadServiceTest {

    @Test
void shouldComposeAnswerAndEvaluationUsingEasyRagFlow() {
    IRagAssistante assistant = message -> "Resposta gerada para " + message;
    IEvaluationPort evaluationPort = (question, answer, context) -> new EvaluationResult(
            true,
            "Boa resposta",
            0.91,
            "O conteúdo recuperado foi suficiente para sustentar a resposta"
    );

    // Instancia o adaptador que provê IRetrievalPort e IAnswerGenerationPort
    var adapter = new LangChainRagAdapter(assistant);

    // Passa as três portas necessárias para o serviço
    IRagTriadService service = new DefaultRagTriadService(
            evaluationPort,
            adapter,  // IRetrievalPort
            adapter   // IAnswerGenerationPort
    );

    RagAnswerResponse response = service.answer("Qual é a pergunta?");

    assertThat(response.answer()).isEqualTo("Resposta gerada para Qual é a pergunta?");
    assertThat(response.context()).containsExactly("Contexto recuperado automaticamente pela abordagem Easy RAG para: Qual é a pergunta?");
    assertThat(response.evaluation().isAccurate()).isTrue();
    assertThat(response.evaluation().score()).isEqualTo(0.91);
}

    @Test
    void deveRetornarScoreBaixoQuandoContextoInvalido() {
        IRagAssistante assistant = message -> "Resposta improvisada sem relação com o contexto";
        IEvaluationPort evaluationPort = (question, answer, context) -> new EvaluationResult(
                false,
                "A resposta não está alinhada com o contexto recuperado",
                0.12,
                "O conteúdo recuperado não sustenta a resposta fornecida");

        // Instancia o adaptador que provê IRetrievalPort e IAnswerGenerationPort
        var adapter = new LangChainRagAdapter(assistant);

        // Passa as três portas necessárias para o serviço
        IRagTriadService service = new DefaultRagTriadService(
                evaluationPort,
                adapter,  // IRetrievalPort
                adapter   // IAnswerGenerationPort
        );

        RagAnswerResponse response = service.answer("Qual é a política de desconto?");

        assertThat(response.answer()).isEqualTo("Resposta improvisada sem relação com o contexto");
        assertThat(response.evaluation().isAccurate()).isFalse();
        assertThat(response.evaluation().score()).isEqualTo(0.12);
        assertThat(response.evaluation().rationale()).contains("não está alinhada");
    }
}
