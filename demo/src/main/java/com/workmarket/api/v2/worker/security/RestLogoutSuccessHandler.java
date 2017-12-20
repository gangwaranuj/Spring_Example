package com.workmarket.api.v2.worker.security;


import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.workmarket.api.ApiJSONPayloadMap;
import com.workmarket.api.v2.ApiV2Response;
import org.apache.tools.ant.util.CollectionUtils;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class RestLogoutSuccessHandler implements LogoutSuccessHandler {

	@Override
	public void onLogoutSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication)
		throws IOException, ServletException {

		if (authentication != null && authentication.getDetails() != null) {
				request.getSession().invalidate();
		}
		response.setStatus(HttpServletResponse.SC_OK);
		response.setContentType("application/json;charset=UTF-8");
		response.setHeader("Cache-Control", "no-cache");

		ApiJSONPayloadMap metadataBuilder = new ApiJSONPayloadMap();
		metadataBuilder.put("code", HttpServletResponse.SC_OK);

		ApiV2Response responsePayload = new ApiV2Response(metadataBuilder, CollectionUtils.EMPTY_LIST, null);
		ObjectMapper jsonMapper = new ObjectMapper();
		jsonMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
		response.getWriter().write(jsonMapper.writeValueAsString(responsePayload));
	}
}
