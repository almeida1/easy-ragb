package com.fatec.easy.service;

import java.util.List;

public record RagAnswerResponse(String answer, List<String> context, EvaluationResult evaluation) {
}
