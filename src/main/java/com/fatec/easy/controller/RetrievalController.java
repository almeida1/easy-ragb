package com.fatec.easy.controller;

import com.fatec.easy.model.ChatRequest;
import com.fatec.easy.service.RagAssistant;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Controller responsável pela etapa de RECUPERAÇÃO e GERAÇÃO
 * (Retrieval-Augmented Generation) no ciclo RAG.
 */
@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/retrieval")
public class RetrievalController {

    private final RagAssistant ragAssistant;

    public RetrievalController(RagAssistant ragAssistant) {
        this.ragAssistant = ragAssistant;
    }

    @PostMapping("/consultar")
    public ResponseEntity<String> consultar(@RequestBody ChatRequest request) {
        String answer = ragAssistant.chat(request.getQuestion());
        return ResponseEntity.ok(answer);
    }
}
