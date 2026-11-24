package com.fatec.easy.service;

/**
 * Interface que representa o assistente avaliador (LLM-as-a-Judge).
 * Isolado do assistente principal para não sofrer contaminação de persona,
 * histórico de conversa (chat memory) ou recuperação vetorial automática indevida.
 */
public interface IRagJudgeAssistant {

    String evaluate(String prompt);
}
