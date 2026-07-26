package com.fatec.easy.model;

import java.util.List;

public record RagAnswerResponse(String answer, List<String> context, EvaluationResult evaluation) {
}
