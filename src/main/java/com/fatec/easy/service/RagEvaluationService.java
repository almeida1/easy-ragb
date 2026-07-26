package com.fatec.easy.service;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.springframework.stereotype.Service;

import com.fatec.easy.model.EvaluationResult;
/**
 * Cria um prompt para um "judge" LLM. O score é obtido do texto retornado pelo LLM com regex.
 * Este serviço utiliza um assistente RAG para avaliar a precisão, utilidade e suporte da resposta fornecida em relação à 
 * pergunta e ao contexto recuperado. A aplicação nao calcula o score de avaliação, mas sim delega a tarefa de avaliação 
 * ao assistente LLM configurado configurado.
 */
@Service
public class RagEvaluationService implements IEvaluationPort {

    private static final Pattern JSON_BOOLEAN = Pattern.compile("\\\"isAccurate\\\"\\s*:\\s*(true|false)");
    private static final Pattern JSON_SCORE = Pattern.compile("\\\"score\\\"\\s*:\\s*([0-9]+(?:\\.[0-9]+)?)");
    private static final Pattern JSON_RATIONALE = Pattern.compile("\\\"rationale\\\"\\s*:\\s*\\\"([^\\\"]*)\\\"");
    private static final Pattern JSON_EVIDENCE = Pattern.compile("\\\"evidence\\\"\\s*:\\s*\\\"([^\\\"]*)\\\"");

    private final IRagAssistante ragAssistant;

    public RagEvaluationService(IRagAssistante ragAssistant) {
        this.ragAssistant = ragAssistant;
    }

    @Override
    public EvaluationResult evaluate(String question, String answer, List<String> context) {
        String prompt = buildJudgePrompt(question, answer, context);
        String raw = ragAssistant.chat(prompt);

        boolean accurate = false;
        double score = 0.0;
        String rationale = "Avaliação automática não pôde ser inferida com precisão.";
        String evidence = "Sem evidência extraída do juiz.";

        if (raw != null && !raw.isBlank()) {
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
                rationale = rationaleMatcher.group(1);
            }

            Matcher evidenceMatcher = JSON_EVIDENCE.matcher(raw);
            if (evidenceMatcher.find()) {
                evidence = evidenceMatcher.group(1);
            }
        }

        return new EvaluationResult(accurate, rationale, score, evidence);
    }

    private String buildJudgePrompt(String question, String answer, List<String> context) {
        return String.format("""
                Você é um juiz de qualidade para um sistema RAG.
                Avalie se a resposta abaixo é precisa, útil e apoiada pelo contexto recuperado.
                Responda em JSON estrito com as chaves: "isAccurate", "rationale", "score", "evidence".
                Pergunta: %s
                Resposta: %s
                Contexto: %s
                """, question, answer, context == null ? "[]" : String.join(" | ", context));
    }
}
