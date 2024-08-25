package com.bigobrains.ai.databind;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.TreeNode;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

import java.io.IOException;

class SchemaSerializer extends JsonDeserializer<String> {

    @Override
    public String deserialize(JsonParser jParser, DeserializationContext ctxt) throws IOException {
        TreeNode tree = jParser.getCodec().readTree(jParser);
        return tree.toString();
    }
}
