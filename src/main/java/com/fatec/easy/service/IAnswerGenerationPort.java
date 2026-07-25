package com.fatec.easy.service;

import java.util.List;

public interface IAnswerGenerationPort {
    String generate(String question, List<String> context);
}
