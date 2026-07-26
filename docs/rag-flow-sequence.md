#Diagrama de sequencia do fluxo do processo RAG

```mermaid
sequenceDiagram
    autonumber
    actor Usuario as Usuário
    participant Controller as RetrievalController
    participant Service as DefaultRagTriadService
    participant Assistant as RagAssistant
    participant Retriever as ContentRetriever
    participant Store as EmbeddingStore
    participant LLM as ChatLanguageModel

    Usuario->>Controller: POST /api/retrieval/consultar
    Controller->>Service: answer(pergunta)

    Service->>Assistant: chat(pergunta)
    Assistant->>Retriever: buscar contexto relevante
    Retriever->>Store: consulta por similaridade
    Store-->>Retriever: trechos/documentos relevantes
    Retriever-->>Assistant: contexto recuperado
    Assistant->>LLM: prompt + contexto
    LLM-->>Assistant: resposta gerada
    Assistant-->>Service: resposta final

    Service->>Service: montar resposta + contexto + avaliação
    Service-->>Controller: RagAnswerResponse
    Controller-->>Usuario: resposta JSON
