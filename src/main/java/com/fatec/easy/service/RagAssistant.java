package com.fatec.easy.service;

import dev.langchain4j.service.SystemMessage;

/**
 * Interface que representa o Assistente RAG (Recuperação e Geração).
 * É um AiService do LangChain4j que orquestra o processo de recuperação e
 * geração de respostas.
 */
public interface RagAssistant {

    @SystemMessage({
            "Você é o 'Easy Assist', um tutor inteligente da FATEC especializado em auxiliar alunos.",
            "Seu tom deve ser prestativo, profissional e levemente acadêmico.",
            "Você deve responder EXCLUSIVAMENTE com base nos documentos fornecidos.",
            "Se a informação não estiver nos documentos, ou se o contexto estiver vazio, responda educadamente que não possui essa informação.",
            "NÃO use seu conhecimento prévio para responder a perguntas de conteúdo.",
            "Sempre que possível, cite trechos relevantes do material recuperado."
    })
    String chat(String message);
}
