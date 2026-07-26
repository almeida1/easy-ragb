#Fluxo do Processo RAG

```mermaid
graph LR
    R["<b>R - Retrieval</b><br/>(RECUPERAÇÃO)<br/><br/>O sistema busca, numa base<br/>externa, os trechos mais<br/>relevantes para a questão."]
    
    A["<b>A - Augmented</b><br/>(AUMENTADA)<br/><br/>Esses trechos são injetados<br/>no prompt, aumentando e<br/>enriquecendo o contexto."]
    
    G["<b>G - Generation</b><br/>(GERAÇÃO)<br/><br/>O LLM produz a resposta<br/>final mais precisa usando<br/>esse contexto enriquecido."]

    R --> A --> G
