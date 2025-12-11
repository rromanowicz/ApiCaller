package io.github.rromanowicz.apicaller.common;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import io.github.rromanowicz.apicaller.common.Model.ApiHeader;
import java.io.IOException;

public class ApiHeaderSerializer extends JsonSerializer<ApiHeader> {

  @Override
  public void serialize(ApiHeader apiHeader, JsonGenerator jsonGenerator,
      SerializerProvider serializerProvider) throws IOException {
    jsonGenerator.writeStartObject();
    jsonGenerator.writeStringField(apiHeader.name(), apiHeader.value());
    jsonGenerator.writeEndObject();
  }
}
