package com.workmarket.vault.web.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.workmarket.api.v2.ApiV2Response;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

// TODO API
public class VaultControllerAccessDeniedHandler implements AccessDeniedHandler {

	@Override
	public void handle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, AccessDeniedException e) throws IOException, ServletException {
		httpServletResponse.setContentType("application/json");
		httpServletResponse.setStatus(HttpStatus.FORBIDDEN.value());
		httpServletResponse.getWriter().write(new ObjectMapper().writeValueAsString(ApiV2Response.valueWithMessage("Forbidden", HttpStatus.FORBIDDEN)));
	}
}
