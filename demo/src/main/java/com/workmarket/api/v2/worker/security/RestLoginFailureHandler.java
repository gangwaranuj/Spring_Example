package com.workmarket.api.v2.worker.security;

import com.google.common.collect.ImmutableList;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.workmarket.api.ApiJSONPayloadMap;
import com.workmarket.api.v2.ApiV2Response;
import com.workmarket.api.v2.model.ApiEmailNotConfirmedDTO;
import com.workmarket.service.exception.authentication.EmailNotConfirmedException;

import org.apache.tools.ant.util.CollectionUtils;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * Handler for failed login drops the MVC forwarding mechanisms typically set up by spring security, and just
 * send back a 401 Unauthorized response with error code and message in the JSON response metadata section
 */
public class RestLoginFailureHandler implements AuthenticationFailureHandler {

	@Override
	public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException
		exception) throws IOException, ServletException {

		response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
		response.setContentType("application/json;charset=UTF-8");
		response.setHeader("Cache-Control", "no-cache");

		final ApiV2Response responsePayload = createResponse(exception);
		final ObjectMapper jsonMapper = new ObjectMapper();
		final PrintWriter writer = response.getWriter();

		writer.write(jsonMapper.writeValueAsString(responsePayload));
		writer.flush();
		writer.close();

		new LoginTracker().logDetailsOfSigninFailure(request, "API");
	}

	private ApiV2Response createResponse(final AuthenticationException exception) throws IOException, ServletException {
		final ApiJSONPayloadMap metadataBuilder = new ApiJSONPayloadMap();

		metadataBuilder.setStatusCode(HttpServletResponse.SC_UNAUTHORIZED);
		metadataBuilder.put("message", exception.getMessage());

		if (!(exception instanceof EmailNotConfirmedException)) {
			return new ApiV2Response(metadataBuilder, CollectionUtils.EMPTY_LIST, null);
		}
 
		final EmailNotConfirmedException emailNotConfirmedException = (EmailNotConfirmedException) exception;
		final ApiEmailNotConfirmedDTO emailNotConfirmedDTO = new ApiEmailNotConfirmedDTO.Builder()
			.setUserNumber(emailNotConfirmedException.getUserNumber())
			.build();

		return new ApiV2Response(metadataBuilder, ImmutableList.of(emailNotConfirmedDTO), null);
	}
}
