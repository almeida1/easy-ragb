package com.fatec.easy.service;

public record EvaluationResult(boolean isAccurate, String rationale, double score, String evidence) {
}
