package com.example.inovasiyanotebook.service.viewservices.order;


import com.example.inovasiyanotebook.service.viewservices.order.worddocument.RawPositionData;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Converter
public class OrderPositionConverter implements AttributeConverter<List<RawPositionData>, String> {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public String convertToDatabaseColumn(List<RawPositionData> positions) {
        try {
            return objectMapper.writeValueAsString(positions);
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException("Error converting list of positions to JSON", e);
        }
    }

    @Override
    public List<RawPositionData> convertToEntityAttribute(String dbData) {
        try {
            if (dbData == null || dbData.isEmpty()) {
                return new ArrayList<>();
            }
            return objectMapper.readValue(dbData, objectMapper.getTypeFactory().constructCollectionType(List.class, RawPositionData.class));
        } catch (IOException e) {
            throw new IllegalArgumentException("Error converting JSON to list of positions", e);
        }
    }
}

