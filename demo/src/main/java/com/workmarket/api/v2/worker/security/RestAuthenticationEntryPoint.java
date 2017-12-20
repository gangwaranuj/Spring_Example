package com.workmarket.api.v2.worker.security;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.workmarket.api.ApiJSONPayloadMap;
import com.workmarket.api.v2.ApiV2Response;
import org.apache.tools.ant.util.CollectionUtils;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;


/**
 * This is the entry point for Rest API. Rather than forward to a configured login page like a typical MVC entry point
 * this one just returns an Unauthorized response.
 */
@Component
public class RestAuthenticationEntryPoint implements AuthenticationEntryPoint {
	@Override
	public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException error)
		throws IOException, ServletException {

		response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
		response.setContentType("application/json;charset=UTF-8");
		response.setHeader("Cache-Control", "no-cache");

		ApiJSONPayloadMap metadataBuilder = new ApiJSONPayloadMap();
		metadataBuilder.setStatusCode(HttpServletResponse.SC_UNAUTHORIZED);
		metadataBuilder.put("message", error.getMessage());

		ApiV2Response responsePayload = new ApiV2Response(metadataBuilder, CollectionUtils.EMPTY_LIST, null);
		ObjectMapper jsonMapper = new ObjectMapper();
		jsonMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
		response.getWriter().write(jsonMapper.writeValueAsString(responsePayload));

		new LoginTracker().logDetailsOfSigninFailure(request, "API");
	}
}
