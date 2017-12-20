package com.workmarket.api;

import com.fasterxml.jackson.core.JsonEncoding;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.JsonStreamContext;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.FilterProvider;
import com.fasterxml.jackson.databind.ser.PropertyWriter;
import com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;
import com.google.api.client.util.Sets;
import com.google.common.collect.ImmutableSet;
import com.workmarket.api.v1.ApiV1Response;
import com.workmarket.api.v2.ApiV2Response;
import com.workmarket.api.v2.ApiV2xResponse;
import com.workmarket.api.v3.response.ApiV3Response;
import com.workmarket.service.web.WebRequestContextProvider;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.converter.HttpMessageNotWritableException;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.http.server.ServletServerHttpResponse;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.Collections;
import java.util.Set;

/**
 * Created by joshlevine on 12/27/16.
 */
@Configuration
public class ApiBaseHttpMessageConverter extends MappingJackson2HttpMessageConverter {

	public static final String FILTER_API_PROJECTIONS = "API Projections";
	public static final String HEADER_X_WM_FIELD_PROJECTION = "X-WM-FIELD-PROJECTION";

	@Autowired private WebRequestContextProvider webRequestContextProvider;

	@Override
	protected boolean supports(Class<?> clazz) {
		return isAnApiResponse(clazz);
	}

	private static boolean isAnApiResponse(Class<?> clazz) {
		return ApiV2Response.class.equals(clazz)
					 || ApiV1Response.class.equals(clazz)
					 || ApiV2xResponse.class.equals(clazz)
					 || ApiV3Response.class.equals(clazz)
					 || ApiRedirectResponse.class.equals(clazz);
	}

	@Override
	protected void writeInternal(Object apiResponse, Type type, HttpOutputMessage outputMessage) throws
																																										IOException,
																																										HttpMessageNotWritableException {
		final long startTime = webRequestContextProvider.getWebRequestContext().getRequestStartTime();

		long endTime = System.currentTimeMillis();

		if (apiResponse instanceof ApiV3Response) {
			// TODO API - remove this - used for breakpoints...
			int x = 0;
		} else if (apiResponse instanceof ApiV2Response) {
			final ApiV2Response v2Response = (ApiV2Response) apiResponse;
			v2Response.getMeta().setTimestamp(startTime / 1000L);
			v2Response.getMeta().setResponseTime((endTime - startTime) / 1000D);
			v2Response.getMeta().setRequestId(webRequestContextProvider.getWebRequestContext().getRequestId());
		} else if (apiResponse instanceof ApiV2xResponse) {
			final ApiV2xResponse v2Response = (ApiV2xResponse) apiResponse;
			v2Response.getMeta().setTimestamp(startTime / 1000L);
			v2Response.getMeta().setResponseTime((endTime - startTime) / 1000D);
			v2Response.getMeta().setRequestId(webRequestContextProvider.getWebRequestContext().getRequestId());
		} else if (apiResponse instanceof ApiV1Response) {
			final ApiV1Response v1Response = (ApiV1Response) apiResponse;
			v1Response.getMeta().setTimestamp(startTime / 1000L);
			v1Response.getMeta().setExecutionTime((endTime - startTime) / 1000D);
			v1Response.getMeta().setRequestId(webRequestContextProvider.getWebRequestContext().getRequestId());
		}
		else if (apiResponse != null) {
			logger.debug("Emitting non-standard return type: " + apiResponse.getClass());
		}

		writeApiResponse(apiResponse, type, outputMessage);
	}

	private void writeApiResponse(Object apiResponse, Type type, HttpOutputMessage outputMessage) throws IOException {
		// Ganked from the super class but made to do our bidding with the filters.
		final JsonEncoding encoding = this.getJsonEncoding(outputMessage.getHeaders().getContentType());
		final JsonGenerator generator = this.objectMapper.getFactory().createGenerator(outputMessage.getBody(), encoding);

		final Collection<String> fieldsToInclude = ((ServletServerHttpResponse)outputMessage).getServletResponse().getHeaders(HEADER_X_WM_FIELD_PROJECTION);

		final FilterProvider filters = new SimpleFilterProvider().addFilter(
			ApiBaseHttpMessageConverter.FILTER_API_PROJECTIONS,
			new APIProjectionsFilter(ImmutableSet.copyOf(fieldsToInclude))
		);

		final JavaType javaType = this.getJavaType(type, null);

		try {
			this.writePrefix(generator, apiResponse);
			final ObjectWriter objectWriter = this.objectMapper.writer(filters).forType(javaType);
			objectWriter.writeValue(generator, apiResponse);
			this.writeSuffix(generator, apiResponse);
			generator.flush();
		} catch (JsonProcessingException var11) {
			throw new HttpMessageNotWritableException("Could not write content: " + var11.getMessage(), var11);
		}
	}

	public static class APIProjectionsFilter extends SimpleBeanPropertyFilter implements java.io.Serializable {
		private static final long serialVersionUID = 1L;

		/**
		 * Set of property names to filter out.
		 */
		protected final Set<String> fieldsToInclude;
		protected final Set<String> enumeratedFieldsToInclude;

		APIProjectionsFilter() {
			fieldsToInclude = Collections.emptySet();
			enumeratedFieldsToInclude = Collections.emptySet();
		}

		public APIProjectionsFilter(Set<String> properties) {
			fieldsToInclude = properties;
			enumeratedFieldsToInclude = preProcessProperties(properties);
		}

		private Set<String> preProcessProperties(Set<String> properties) {
			Set<String>  fieldsToInclude = Sets.newHashSet();
			// find parent keys and include them
			for(String property : properties) {
				final String[] keys = StringUtils.split(property, '.');
				// a.b.c -> a, a.b, a.b.c
				// a.b.d -> a, a.b, a.b.d
				// a.c.d -> a, a.c, a.c.d
				final StringBuffer baseProperty = new StringBuffer();
				for(String key : keys) {
					baseProperty.append(key);
					fieldsToInclude.add(baseProperty.toString());
					baseProperty.append('.');
				}
			}

			return fieldsToInclude;
		}

		@Override
		public void serializeAsField(Object pojo,
																 JsonGenerator jgen,
																 SerializerProvider provider,
																 PropertyWriter writer) throws Exception {
			final StringBuffer propertyPathBuffer = new StringBuffer(writer.getName());
			JsonStreamContext outputContext = jgen.getOutputContext().getParent();
			while(outputContext != null) {
				if(outputContext.getParent() != null &&
					outputContext.getParent().getCurrentValue() != null &&
					ApiBaseHttpMessageConverter.isAnApiResponse(outputContext.getParent().getCurrentValue().getClass())) {
					break;
				}
				if(outputContext.getCurrentName() != null) {
					propertyPathBuffer.insert(0, '.');
					propertyPathBuffer.insert(0, outputContext.getCurrentName());
				}
				outputContext = outputContext.getParent();
			}
			final String propertyPath = propertyPathBuffer.toString();
			if(fieldsToInclude.isEmpty() || enumeratedFieldsToInclude.contains(propertyPath) || fieldIsPrefixedBy(propertyPath, fieldsToInclude)) {
				writer.serializeAsField(pojo, jgen, provider);
			}
		}

		private boolean fieldIsPrefixedBy(String propertyPath, Set<String> fieldsToInclude) {
			for(String fieldToInclude : fieldsToInclude) {
				if(propertyPath.startsWith(fieldToInclude)) {
					return true;
				}
			}
			return false;
		}
	}
}
