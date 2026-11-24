# Diagrama de sequência do processo de avaliação RAG

```mermaid
sequenceDiagram
    participant Usuario
    participant Controller as RetrievalController
    participant Triad as DefaultRagTriadService
    participant Adapter as LangChainRagAdapter
    participant Evaluation as RagEvaluationService
    participant Tutor as RagAssistant
    participant Judge as RagJudgeAssistant

    Usuario->>Controller: POST /api/retrieval/consultar
    Controller->>Triad: answer(question)

    Triad->>Adapter: recupera contexto e gera resposta
    Adapter->>Tutor: chat(question)
    Tutor-->>Adapter: resposta
    Adapter-->>Triad: context + answer

    Triad->>Evaluation: evaluate(question, answer, context)
    Evaluation->>Judge: evaluate(prompt de avaliação)
    Judge-->>Evaluation: avaliação em JSON

    Evaluation-->>Triad: EvaluationResult
    Triad-->>Controller: RagAnswerResponse
    Controller-->>Usuario: resposta + contexto + avaliação
```
