package com.fatec.easy.service;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fatec.easy.model.EvaluationResult;

/**
 * Serviço responsável por avaliar a qualidade da resposta do sistema RAG usando a técnica LLM-as-a-Judge.
 * Utiliza o assistente dedicado IRagJudgeAssistant (desacoplado do assistente tutor).
 */
@Service
public class RagEvaluationService implements IEvaluationPort {

    private final Logger logger = LogManager.getLogger(this.getClass());

    private static final Pattern JSON_BOOLEAN = Pattern.compile("\"isAccurate\"\\s*:\\s*(true|false)", Pattern.CASE_INSENSITIVE);
    private static final Pattern JSON_SCORE = Pattern.compile("\"score\"\\s*:\\s*([0-9]+(?:\\.[0-9]+)?)");
    private static final Pattern JSON_RATIONALE = Pattern.compile("\"rationale\"\\s*:\\s*\"(.*?)\"(?:\\s*,|\\s*\\})", Pattern.DOTALL);
    private static final Pattern JSON_EVIDENCE = Pattern.compile("\"evidence\"\\s*:\\s*\"(.*?)\"(?:\\s*,|\\s*\\})", Pattern.DOTALL);

    private final IRagJudgeAssistant ragJudgeAssistant;
    private final ObjectMapper objectMapper;

    public RagEvaluationService(IRagJudgeAssistant ragJudgeAssistant) {
        this.ragJudgeAssistant = ragJudgeAssistant;
        this.objectMapper = new ObjectMapper();
    }

    @Override
    public EvaluationResult evaluate(String question, String answer, List<String> context) {
        String prompt = buildJudgePrompt(question, answer, context);
        String raw = ragJudgeAssistant.evaluate(prompt);
        logger.info(">>>>>> Resposta bruta do Juiz LLM: {}", raw);

        boolean accurate = false;
        double score = 0.0;
        String rationale = "Avaliação automática não pôde ser inferida com precisão.";
        String evidence = "Sem evidência extraída do juiz.";

        if (raw != null && !raw.isBlank()) {
            boolean parsed = false;
            try {
                String cleaned = extractJson(raw);
                JsonNode node = objectMapper.readTree(cleaned);
                if (node.hasNonNull("isAccurate")) {
                    accurate = node.get("isAccurate").asBoolean();
                }
                if (node.hasNonNull("score")) {
                    score = node.get("score").asDouble();
                }
                if (node.hasNonNull("rationale")) {
                    rationale = node.get("rationale").asText();
                } else if (node.hasNonNull("reason")) {
                    rationale = node.get("reason").asText();
                } else if (node.hasNonNull("justification")) {
                    rationale = node.get("justification").asText();
                } else if (node.hasNonNull("justificativa")) {
                    rationale = node.get("justificativa").asText();
                }
                if (node.hasNonNull("evidence")) {
                    evidence = node.get("evidence").asText();
                }
                if ("Avaliação automática não pôde ser inferida com precisão.".equals(rationale) && !evidence.isBlank() && !"Sem evidência extraída do juiz.".equals(evidence)) {
                    rationale = evidence;
                }
                parsed = true;
            } catch (Exception e) {
                logger.warn("Falha no parsing Jackson do JSON do juiz, tentando extração via regex: {}", e.getMessage());
            }

            if (!parsed) {
                Matcher booleanMatcher = JSON_BOOLEAN.matcher(raw);
                if (booleanMatcher.find()) {
                    accurate = Boolean.parseBoolean(booleanMatcher.group(1));
                }

                Matcher scoreMatcher = JSON_SCORE.matcher(raw);
                if (scoreMatcher.find()) {
                    score = Double.parseDouble(scoreMatcher.group(1));
                }

                Matcher rationaleMatcher = JSON_RATIONALE.matcher(raw);
                if (rationaleMatcher.find()) {
                    rationale = rationaleMatcher.group(1).replace("\\n", " ").trim();
                }

                Matcher evidenceMatcher = JSON_EVIDENCE.matcher(raw);
                if (evidenceMatcher.find()) {
                    evidence = evidenceMatcher.group(1).replace("\\n", " ").trim();
                }
            }
        }

        return new EvaluationResult(accurate, rationale, score, evidence);
    }

    private String extractJson(String text) {
        String trimmed = text.trim();
        if (trimmed.contains("```json")) {
            int start = trimmed.indexOf("```json") + 7;
            int end = trimmed.indexOf("```", start);
            if (end != -1) {
                return trimmed.substring(start, end).trim();
            }
            return trimmed.substring(start).trim();
        } else if (trimmed.contains("```")) {
            int start = trimmed.indexOf("```") + 3;
            int end = trimmed.indexOf("```", start);
            if (end != -1) {
                return trimmed.substring(start, end).trim();
            }
            return trimmed.substring(start).trim();
        }
        int firstBrace = trimmed.indexOf('{');
        int lastBrace = trimmed.lastIndexOf('}');
        if (firstBrace != -1 && lastBrace > firstBrace) {
            return trimmed.substring(firstBrace, lastBrace + 1).trim();
        }
        return trimmed;
    }

    private String buildJudgePrompt(String question, String answer, List<String> context) {
        String contextStr = (context == null || context.isEmpty()) ? "[]" : String.join(" | ", context);
        return String.format("""
                Você é um juiz de qualidade rigoroso para um sistema RAG (Retrieval-Augmented Generation).
                Sua única responsabilidade é verificar a FIDELIDADE ESTRITA (Groundedness / Faithfulness) da resposta em relação ao contexto recuperado.

                CRITÉRIOS RIGOROSOS DE JULGAMENTO:
                1. TOLERÂNCIA ZERO PARA INFERÊNCIA OU EXTRAPOLAÇÃO (ALUCINAÇÃO):
                   - Se o conceito, termo, autor ou tema perguntado NÃO existir explicitamente no contexto recuperado e a resposta tentar "inferir", "deduzir", "interpretar" ou associar a outros tópicos do texto (ex: o usuário pergunta sobre 'teste baseado em risco' e a resposta tenta correlacionar com 'shift-left testing' ou 'melhoria de processo'), você DEVE reprovar a resposta.
                   - Respostas contendo expressões como 'é possível inferir', 'embora o texto não mencione explicitamente', 'podemos interpretar que' quando a informação não existe no contexto são ALUCINAÇÕES POR EXTRAPOLAÇÃO.
                   - Nesses casos, marque OBRIGATORIAMENTE:
                     "isAccurate": false,
                     "score": 0.0 a 2.0,
                     "rationale": "A resposta extrapolou e tentou inferir conceitos que não constam no contexto recuperado."

                2. ABSTENÇÃO PURA E CORRETA:
                   - Se o contexto NÃO contiver as informações necessárias e a resposta afirmar claramente e unicamente que NÃO possui a informação ou que o texto não a menciona (SEM tentar deduzir ou inventar hipóteses em seguida), considere a resposta PERFEITA:
                     "isAccurate": true,
                     "score": 10.0,
                     "rationale": "O modelo evitou alucinação ao afirmar honestamente a ausência da informação no contexto."

                3. RESPOSTA TOTALMENTE FUNDAMENTADA:
                   - Se a resposta responder à pergunta baseando-se estritamente e exclusivamente em fatos explícitos do contexto:
                     "isAccurate": true,
                     "score": 8.0 a 10.0

                Responda OBRIGATORIAMENTE em JSON estrito (sem texto antes ou depois):
                {
                  "isAccurate": true/false,
                  "score": <número de 0.0 a 10.0>,
                  "rationale": "<justificativa sucinta da avaliação>",
                  "evidence": "<trecho do contexto ou explicação do motivo da reprovação/aprovação>"
                }

                Pergunta: %s
                Resposta: %s
                Contexto: %s
                """, question, answer, contextStr);
    }
}
