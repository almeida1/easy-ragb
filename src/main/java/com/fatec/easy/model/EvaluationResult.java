package com.fatec.easy.model;

public record EvaluationResult(boolean isAccurate, String rationale, double score, String evidence) {
}
