package com.workmarket.api.v1;

import com.fasterxml.jackson.core.JsonEncoding;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.converter.HttpMessageNotWritableException;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;

import java.io.IOException;

public class MappingJacksonJsonpHttpMessageConverter extends MappingJackson2HttpMessageConverter {

	@Override
	protected void writeInternal(Object object, HttpOutputMessage outputMessage)
			throws IOException, HttpMessageNotWritableException {

		JsonEncoding encoding = getJsonEncoding(outputMessage.getHeaders().getContentType());
		JsonGenerator jsonGenerator = this.getObjectMapper()
				.getFactory().createGenerator(outputMessage.getBody(), encoding);

		try {
			String jsonPadding = "callback";

			if (object instanceof ApiV1Response) {
				jsonPadding = (((ApiV1Response) object).getMeta().getJsonPCallback() != null) ?
						((ApiV1Response) object).getMeta().getJsonPCallback() :
						jsonPadding;
			}

			jsonGenerator.writeRaw(jsonPadding);
			jsonGenerator.writeRaw("(");
			this.getObjectMapper().writeValue(jsonGenerator, object);
			jsonGenerator.writeRaw(")");
			jsonGenerator.flush();
		} catch (JsonProcessingException ex) {
			throw new HttpMessageNotWritableException("Could not write JSON: + ex.getMessage(), ex");
		}
	}
}
