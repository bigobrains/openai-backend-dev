package com.bigobrains.ai.messaging.cases.management;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.ai.document.Document;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.reader.JsonReader;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;

import java.io.*;
import java.util.List;

@Configuration
public class OpenAIConfigurer {

    private final Resource document;

    public OpenAIConfigurer(ResourceLoader resourceLoader) {
        this.document = resourceLoader.getResource("classpath:playbook.json");
    }

    @Bean
    public VectorStore vectorStore(EmbeddingModel embeddingModel) throws IOException, ClassNotFoundException {
        StaticVectorStore staticVectorStore = new StaticVectorStore(embeddingModel);
        JsonReader jsonReader = new JsonReader(document);
        List<Document> documents = jsonReader.read();
        TokenTextSplitter tokenTextSplitter = new TokenTextSplitter();
        List<Document> splitDocuments = tokenTextSplitter.split(documents);
        staticVectorStore.add(splitDocuments);
        return staticVectorStore;
    }

    @Bean
    public ChatClient simpleChatClient(ChatClient.Builder chatClientBuilder, VectorStore vectorStore) {
        return chatClientBuilder.defaultAdvisors(new SimpleLoggerAdvisor()).build();
    }
}
