package com.fatec.easy.controller;

import com.fatec.easy.model.ChatRequest;
import com.fatec.easy.service.RagAnswerResponse;
import com.fatec.easy.service.IRagTriadService;
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

    private final IRagTriadService ragTriadService;

    public RetrievalController(IRagTriadService ragTriadService) {
        this.ragTriadService = ragTriadService;
    }

    @PostMapping("/consultar")
    public ResponseEntity<RagAnswerResponse> consultar(@RequestBody ChatRequest request) {
        RagAnswerResponse response = ragTriadService.answer(request.getQuestion());
        return ResponseEntity.ok(response);
    }
}
