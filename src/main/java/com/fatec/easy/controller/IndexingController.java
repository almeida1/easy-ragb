package com.fatec.easy.controller;

import com.fatec.easy.service.IndexingService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

/**
 * Controller responsável pela etapa de INDEXAÇÃO no ciclo RAG.
 */
@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/indexing")
public class IndexingController {

    private final IndexingService indexingService;

    public IndexingController(IndexingService indexingService) {
        this.indexingService = indexingService;
    }

    @PostMapping("/upload")
    public ResponseEntity<String> uploadDocument(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body("Por favor, selecione um arquivo.");
        }

        try {
            indexingService.ingestDocument(file.getInputStream());
            return ResponseEntity.ok("Documento indexado com sucesso: " + file.getOriginalFilename());
        } catch (IOException e) {
            return ResponseEntity.internalServerError()
                    .body("Erro ao processar o arquivo para indexação: " + e.getMessage());
        }
    }
}
