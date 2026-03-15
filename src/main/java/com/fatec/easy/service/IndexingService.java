package com.fatec.easy.service;

import java.io.InputStream;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;

import dev.langchain4j.data.document.Document;
import dev.langchain4j.data.document.parser.TextDocumentParser;
import dev.langchain4j.store.embedding.EmbeddingStoreIngestor;

/**
 * Responsavel por processar e indexar os documentos para que eles possam ser
 * posteriormente consultados pela IA.
 * Faz parte do Ciclo RAG - Etapa de INDEXAÇÃO.
 */
@Service
public class IndexingService {

    private final EmbeddingStoreIngestor ingestor;
    private final Logger logger = LogManager.getLogger(this.getClass());

    public IndexingService(EmbeddingStoreIngestor ingestor) {
        this.ingestor = ingestor;
    }

    public void ingestDocument(InputStream dataStream) {
        logger.info(">>>>>> Indexação (Indexing) - Iniciando...");
        Document document = new TextDocumentParser().parse(dataStream);
        ingestor.ingest(document);
        logger.info(">>>>>> Indexação concluída. Caracteres ingeridos: " + document.text().length());
    }
}
