package com.fatec.easy.service;

import java.util.List;

import com.fatec.easy.model.EvaluationResult;

public interface IEvaluationPort {
    EvaluationResult evaluate(String question, String answer, List<String> context);
}
