package com.workmarket.api.v1;

import org.apache.commons.codec.CharEncoding;
import org.apache.commons.collections.CollectionUtils;
import org.apache.velocity.app.VelocityEngine;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.MediaType;
import org.springframework.http.converter.AbstractHttpMessageConverter;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.HttpMessageNotWritableException;
import org.springframework.ui.velocity.VelocityEngineUtils;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.HashMap;
import java.util.Map;

public class ApiV1ResponseToHtmlHttpMessageConverter extends AbstractHttpMessageConverter<ApiV1Response> {

	@Autowired private VelocityEngine velocityEngine;

	public ApiV1ResponseToHtmlHttpMessageConverter() {}

	public ApiV1ResponseToHtmlHttpMessageConverter(MediaType supportedMediaType) {
		super(supportedMediaType);
	}

	public ApiV1ResponseToHtmlHttpMessageConverter(MediaType... supportedMediaTypes) {
		super(supportedMediaTypes);
	}

	@Override
	protected boolean supports(Class<?> clazz) {
		return ApiV1Response.class.equals(clazz);
	}

	@Override
	protected ApiV1Response readInternal(Class<? extends ApiV1Response> clazz, HttpInputMessage inputMessage) throws IOException, HttpMessageNotReadableException {
		return null;
	}

	@Override
	protected void writeInternal(ApiV1Response apiResponse, HttpOutputMessage outputMessage) throws IOException, HttpMessageNotWritableException {
		Map<String, Object> model = new HashMap<>();
		model.put("apiResponse", apiResponse);
		model.put("hasErrors", CollectionUtils.isNotEmpty(apiResponse.getMeta().getErrors()));

		String htmlTemplate = apiResponse.getMeta().getHtmlTemplate();

		String finalHtml = VelocityEngineUtils.mergeTemplateIntoString(velocityEngine, htmlTemplate, CharEncoding.UTF_8, model);
		outputMessage.getHeaders().entrySet();
		OutputStreamWriter writer = new OutputStreamWriter(outputMessage.getBody());
		writer.write(finalHtml);
		writer.flush();
		writer.close();
	}
}