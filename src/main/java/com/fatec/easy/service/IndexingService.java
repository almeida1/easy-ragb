package com.fatec.easy.service;

import java.io.InputStream;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;

import dev.langchain4j.data.document.Document;
import dev.langchain4j.data.document.parser.apache.tika.ApacheTikaDocumentParser;
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

    /**
     * Método responsável por processar e indexar os documentos para que eles possam
     * ser posteriormente consultados pelo modulo de recuperação de informações.
     * O ApacheTikaDocumentParser identifica se é pdf, docx. É responsável por
     * extrair o texto do documento.
     * O EmbeddingStoreIngestor é responsável por indexar o documento.
     * Faz parte do Ciclo RAG - Etapa de INDEXAÇÃO.
     * 
     * @param dataStream
     */
    public void ingestDocument(InputStream dataStream) {
        logger.info(">>>>>> Indexação (Indexing) - Iniciando...");
        Document document = new ApacheTikaDocumentParser().parse(dataStream);
        ingestor.ingest(document);
        logger.info(">>>>>> Indexação concluída. Caracteres ingeridos: " + document.text().length());
    }
}
