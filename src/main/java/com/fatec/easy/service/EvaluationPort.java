package com.fatec.easy.service;

import java.util.List;

public interface EvaluationPort {
    EvaluationResult evaluate(String question, String answer, List<String> context);
}
