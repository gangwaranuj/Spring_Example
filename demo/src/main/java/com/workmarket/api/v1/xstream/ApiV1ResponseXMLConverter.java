package com.workmarket.api.v1.xstream;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ser.FilterProvider;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;
import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.ConverterMatcher;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import com.thoughtworks.xstream.io.xml.AbstractPullReader;
import com.workmarket.api.ApiBaseHttpMessageConverter;
import com.workmarket.api.v1.ApiV1Response;
import com.workmarket.api.v1.ApiV1ResponseMeta;
import org.springframework.http.converter.HttpMessageNotWritableException;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class ApiV1ResponseXMLConverter implements Converter, ConverterMatcher {
	private static final String ELEMENT_META = "meta";
	private static final String ELEMENT_RESPONSE = "response";

	@Override
	public void marshal(Object source, HierarchicalStreamWriter writer, MarshallingContext context) {
		ApiV1Response apiResponse = (ApiV1Response)source;

		writer.startNode(ELEMENT_META);
		if (apiResponse.getMeta() != null) {
			context.convertAnother(apiResponse.getMeta());
		}
		writer.endNode();

		writer.startNode(ELEMENT_RESPONSE);
		Object apiResponsePayload = apiResponse.getResponse();
		if (apiResponsePayload != null) {
			ObjectMapper mapper = new ObjectMapper();
			try {

				final FilterProvider filters = new SimpleFilterProvider().addFilter(
					ApiBaseHttpMessageConverter.FILTER_API_PROJECTIONS,
					new ApiBaseHttpMessageConverter.APIProjectionsFilter(Collections.<String>emptySet())
				);

				String json = mapper.writer(filters).writeValueAsString(apiResponsePayload);
				apiResponsePayload = mapper.readValue(json, Map.class);
			} catch (JsonProcessingException e) {
				throw new HttpMessageNotWritableException("Could not write content: " + source, e);
			} catch (IOException e) {
				throw new HttpMessageNotWritableException("Could not write content: " + source, e);
			}

			context.convertAnother(apiResponsePayload);
		}
		writer.endNode();
	}

	@Override
	@SuppressWarnings("unchecked")
	public Object unmarshal(HierarchicalStreamReader reader, UnmarshallingContext context) {
		ApiV1Response apiResponse = new ApiV1Response();

		if (reader.hasMoreChildren()) {
			do {
				reader.moveDown();
				String nodeName = reader.getNodeName();

				if (ELEMENT_META.equals(nodeName)) {
					ApiV1ResponseMeta meta = (ApiV1ResponseMeta)context.convertAnother(apiResponse, ApiV1ResponseMeta.class);
					apiResponse.setMeta(meta);
				}
				else if (ELEMENT_RESPONSE.equals(nodeName)) {
					Object response = unmarshalResponse(reader, context);
					apiResponse.setResponse(response);
				}

				reader.moveUp();
			} while (reader.hasMoreChildren());
		}

		return apiResponse;
	}


	protected Object unmarshalResponse(HierarchicalStreamReader streamReader, UnmarshallingContext context) {
		AbstractPullReader reader = (AbstractPullReader)streamReader;

		if (reader.hasMoreChildren()) {
			reader.mark();
			reader.moveDown();

			String nodeName = reader.getNodeName();
			reader.reset();

			if (ListConverter.ELEMENT_ITEM.equals(nodeName)) {
				return context.convertAnother(context.currentObject(), List.class);
			}
			else {
				return context.convertAnother(reader, Map.class);
			}
		}

		return null;
	}

	@Override
	public boolean canConvert(Class type) {
		return ApiV1Response.class.equals(type);
	}
}
