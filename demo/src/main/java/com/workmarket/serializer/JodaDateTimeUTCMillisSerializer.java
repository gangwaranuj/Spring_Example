package com.workmarket.serializer;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import org.joda.time.DateTime;

import java.io.IOException;

public class JodaDateTimeUTCMillisSerializer extends JsonSerializer<DateTime> {
  @Override
  public void serialize(
      final DateTime value,
      final JsonGenerator gen,
      final SerializerProvider serializerProvider) throws IOException, JsonProcessingException {
    if (value != null) {
      gen.writeNumber(value.getMillis());
    } else {
      gen.writeNull();
    }
  }
}
