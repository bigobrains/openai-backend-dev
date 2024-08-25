package com.bigobrains.ai.messaging.cases.management;

import org.springframework.ai.document.Document;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.vectorstore.SimpleVectorStore;

import java.util.ArrayList;
import java.util.List;

public class StaticVectorStore extends SimpleVectorStore {

    private final List<Document> documents;

    public StaticVectorStore(EmbeddingModel embeddingModel) {
        super(embeddingModel);
        this.documents = new ArrayList<>();
    }

    @Override
    public void add(List<Document> documents) {
        this.documents.addAll(documents);
        super.add(documents);
    }

    public List<Document> similaritySearchAll() {
        return documents;
    }
}
