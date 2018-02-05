package com.rental.carshowroom.deserializer;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

import java.io.IOException;

public class BooleanDeserializer extends JsonDeserializer {
    @Override
    public Boolean deserialize(JsonParser parser, DeserializationContext context) throws IOException {
        return parser.getBooleanValue();
    }
}
