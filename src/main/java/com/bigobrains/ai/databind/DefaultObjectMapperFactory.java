package com.bigobrains.ai.databind;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;

@Service
public class DefaultObjectMapperFactory implements ObjectMapperFactory {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public ObjectMapper serializerMapper() {
        return objectMapper;
    }

    @Override
    public ObjectMapper deSerializerMapper() {
        return objectMapper;
    }
}
