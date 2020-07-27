package com.sysco.rps.converter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Map;
import javax.persistence.AttributeConverter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ObjectConverter implements AttributeConverter<Map<String, Object>, String> {
  private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
  private static final Logger logger = LoggerFactory.getLogger(ObjectConverter.class);

  @Override
  public String convertToDatabaseColumn(Map<String, Object> attribute) {
    String attributeJson = null;
    try {
      attributeJson = OBJECT_MAPPER.writeValueAsString(attribute);
    } catch (JsonProcessingException e) {
      logger.error("an error occurred while converting object to json string", e);
    }
    return attributeJson;
  }

  @Override
  public Map<String, Object> convertToEntityAttribute(String dbData) {
    Map<String, Object> attribute = null;
    try {
      attribute = OBJECT_MAPPER.readValue(dbData, Map.class);
    } catch (JsonProcessingException e) {
      logger.error("an error occurred while converting json data to object", e);
    }
    return attribute;
  }
}
