package com.bigobrains.ai.databind;

import com.fasterxml.jackson.databind.ObjectMapper;

public interface ObjectMapperFactory {

    ObjectMapper serializerMapper();
    ObjectMapper deSerializerMapper();
}
