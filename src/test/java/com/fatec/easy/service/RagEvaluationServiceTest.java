package com.fatec.easy.service;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fatec.easy.model.EvaluationResult;

class RagEvaluationServiceTest {

    @Test
    @DisplayName("Deve avaliar abstenção legítima com score alto e acurácia verdadeira (evitou alucinação)")
    void shouldEvaluateLegitimateAbstentionAsAccurateAndHighScoring() {
        String mockedJudgeResponse = """
                {
                  "isAccurate": true,
                  "score": 10.0,
                  "rationale": "O modelo informou corretamente que o contexto não possui a resposta para a pergunta, evitando alucinações.",
                  "evidence": "O contexto recuperado não continha os papéis solicitados e o modelo absteve-se adequadamente."
                }
                """;

        IRagJudgeAssistant judge = prompt -> mockedJudgeResponse;
        RagEvaluationService evaluationService = new RagEvaluationService(judge);

        EvaluationResult result = evaluationService.evaluate(
                "Qual os dois papeis pricipais descritos no programa de nivel fundamental do ISTQB?",
                "Não possui essa informação. O texto fornecido não menciona os dois papéis principais descritos no programa de nível fundamental do ISTQB.",
                List.of("Nenhum contexto relevante foi encontrado no repositório.")
        );

        assertThat(result.isAccurate()).isTrue();
        assertThat(result.score()).isEqualTo(10.0);
        assertThat(result.rationale()).contains("evitando alucinações");
        assertThat(result.evidence()).contains("absteve-se adequadamente");
    }

    @Test
    @DisplayName("Deve fazer parse correto mesmo quando o LLM responde com bloco de código markdown ```json")
    void shouldParseEvaluationEnclosedInMarkdownCodeBlock() {
        String mockedJudgeResponse = """
                ```json
                {
                  "isAccurate": true,
                  "score": 9.5,
                  "rationale": "Resposta fundamentada no contexto.",
                  "evidence": "Os papéis são testador e líder de testes."
                }
                ```
                """;

        IRagJudgeAssistant judge = prompt -> mockedJudgeResponse;
        RagEvaluationService evaluationService = new RagEvaluationService(judge);

        EvaluationResult result = evaluationService.evaluate(
                "Quais os papéis?",
                "Os papéis são testador e líder.",
                List.of("O programa aborda papéis de testador e líder de testes.")
        );

        assertThat(result.isAccurate()).isTrue();
        assertThat(result.score()).isEqualTo(9.5);
        assertThat(result.rationale()).isEqualTo("Resposta fundamentada no contexto.");
        assertThat(result.evidence()).isEqualTo("Os papéis são testador e líder de testes.");
    }

    @Test
    @DisplayName("Deve avaliar alucinação como não acurada e com score baixo")
    void shouldEvaluateHallucinationAsInaccurateAndLowScoring() {
        String mockedJudgeResponse = """
                {
                  "isAccurate": false,
                  "score": 1.0,
                  "rationale": "A resposta inventou detalhes que não existem no contexto.",
                  "evidence": "Nenhuma das afirmações é corroborada pelo documento."
                }
                """;

        IRagJudgeAssistant judge = prompt -> mockedJudgeResponse;
        RagEvaluationService evaluationService = new RagEvaluationService(judge);

        EvaluationResult result = evaluationService.evaluate(
                "Qual a cor da capa?",
                "A capa é azul metálico com detalhes dourados.",
                List.of("Manual de teste sem menção a cores de capa.")
        );

        assertThat(result.isAccurate()).isFalse();
        assertThat(result.score()).isEqualTo(1.0);
        assertThat(result.rationale()).contains("inventou detalhes");
    }

    @Test
    @DisplayName("Deve reprovar resposta que tenta inferir ou extrapolar conceitos ausentes no contexto")
    void shouldRejectExtrapolationAndUnauthorizedInferenceAsInaccurate() {
        String mockedJudgeResponse = """
                {
                  "isAccurate": false,
                  "score": 1.5,
                  "rationale": "A resposta extrapolou e tentou inferir conceitos que não constam no contexto recuperado.",
                  "evidence": "O texto fala sobre shift-left testing e não sobre teste baseado em risco de Felderer."
                }
                """;

        IRagJudgeAssistant judge = prompt -> mockedJudgeResponse;
        RagEvaluationService evaluationService = new RagEvaluationService(judge);

        EvaluationResult result = evaluationService.evaluate(
                "O que é teste baseado em risco segundo Felderer?",
                "De acordo com o contexto fornecido, não há uma definição explícita... No entanto, é possível inferir que o autor está se referindo a uma abordagem de teste...",
                List.of("Outro aspecto que impede a adoção mais sistemática do processo de teste...")
        );

        assertThat(result.isAccurate()).isFalse();
        assertThat(result.score()).isLessThanOrEqualTo(2.0);
        assertThat(result.rationale()).contains("extrapolou");
    }
}
