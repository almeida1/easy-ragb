package com.fatec.easy.controller;

import com.fatec.easy.model.ChatRequest;
import com.fatec.easy.model.RagAnswerResponse;
import com.fatec.easy.service.IRagTriadService;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * O RAG Triad é um framework de avaliação que mede a qualidade do sistema
 * usando três métricas principais: a relevância do contexto recuperado, a
 * fidelidade
 * da resposta gerada ao contexto e a relevância da resposta em relação à
 * pergunta
 * original. As Três Métricas da Triad
 * (1) Relevância do Contexto (Context Relevance): Mede se os trechos de texto
 * recuperados contêm apenas a informação
 * necessária para responder à pergunta. Penaliza documentos que trazem dados
 * inúteis ou poluição informacional.
 * (2) Fidelidade (Faithfulness / Groundedness): Verifica
 * se a resposta gerada pelo modelo de linguagem baseia-se estritamente no
 * contexto recuperado.Detecta alucinações ou invenções que não estavam nos
 * documentos originais.
 * (3) Relevância da Resposta (Answer Relevance):Avalia se a resposta
 * final borda de fato o que o usuário perguntou.Identifica desvios de foco,
 * mesmo que o texto gerado seja correto ou fiel ao contexto.
 * (i) Entrada - A pergunta do usuário, o contexto recuperado e a resposta
 * gerada.
 * (ii) Avaliação - Cada eixo recebe uma nota (geralmente de 0 a 1 ou de 0 a 5).
 * (iii) Uso - Automatiza o diagnóstico de falhas no pipeline de RAG (se o erro
 * está na busca ou na geração).
 * (iv) Ferramentas - Aplicada por frameworks de testes e métricas como o
 * TruLens ou Ragas para monitorar IAs.
 */

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/rag")
public class RagTriadController {
    private final Logger logger = LogManager.getLogger(this.getClass());
    private final IRagTriadService ragTriadService;

    public RagTriadController(IRagTriadService ragTriadService) {
        this.ragTriadService = ragTriadService;
    }

    @PostMapping("/evaluate")
    public ResponseEntity<RagAnswerResponse> evaluate(@RequestBody ChatRequest request) {
        logger.info(">>>>>> Evaluating request: {}", request.getQuestion());
        RagAnswerResponse response = ragTriadService.answer(request.getQuestion());
        return ResponseEntity.ok(response);
    }
}
