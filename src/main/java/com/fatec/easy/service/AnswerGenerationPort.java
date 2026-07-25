package com.fatec.easy.service;

import java.util.List;

public interface AnswerGenerationPort {
    String generate(String question, List<String> context);
}
