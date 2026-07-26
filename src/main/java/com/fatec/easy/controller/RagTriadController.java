package com.fatec.easy.controller;

import com.fatec.easy.model.ChatRequest;
import com.fatec.easy.model.RagAnswerResponse;
import com.fatec.easy.service.IRagTriadService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/rag")
public class RagTriadController {

    private final IRagTriadService ragTriadService;

    public RagTriadController(IRagTriadService ragTriadService) {
        this.ragTriadService = ragTriadService;
    }

    @PostMapping("/evaluate")
    public ResponseEntity<RagAnswerResponse> evaluate(@RequestBody ChatRequest request) {
        RagAnswerResponse response = ragTriadService.answer(request.getQuestion());
        return ResponseEntity.ok(response);
    }
}
