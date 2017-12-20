package com.workmarket.api;

import com.google.common.collect.ImmutableMap;
import com.workmarket.api.v1.ApiServletRequestWrapper;
import com.workmarket.api.v1.ApiTraceServletResponseWrapper;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component(value = "apiBaseFilter")
public class ApiBaseFilter implements Filter {
	private static final Logger logger = LoggerFactory.getLogger(ApiBaseFilter.class);

	private Map<String, String> mediaTypes = ImmutableMap.of("json",
		"application/json",
		"jsonp",
		"application/javascript",
		"xml",
		"application/xml");
	private String parameterName = "output_format";

	@Override
	public void init(FilterConfig config) throws ServletException {
		initMediaTypes(config);
		initParameterName(config);
	}

	@Override
	public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain chain) throws IOException, ServletException {
		HttpServletRequest request = (HttpServletRequest)servletRequest;
		HttpServletResponse response = (HttpServletResponse)servletResponse;

		String contentType = getContentType(request);
		HttpServletRequest requestWrapper = new ApiServletRequestWrapper(request, response, contentType);

		if (logger.isDebugEnabled()) {
			debugRequestHeaders(request);
		}
		response.addHeader("Access-Control-Allow-Origin", StringUtils.defaultIfEmpty(request.getHeader("Origin"), "localhost"));
		response.addHeader("Access-Control-Allow-Credentials", "true");
		response.addHeader("Access-Control-Allow-Methods", "*");
		response.addHeader("Access-Control-Allow-Headers", "Content-type");
		response.addHeader("Access-Control-Allow-Headers", "Authorization");
		response.addHeader("Access-Control-Max-Age", "1800");

		if("OPTIONS".equals(request.getMethod())) {
			return;
		}
		if (logger.isTraceEnabled()) {
			traceRequestParameters(request);

			ApiTraceServletResponseWrapper traceResponseWrapper = new ApiTraceServletResponseWrapper(response);
			chain.doFilter(requestWrapper, traceResponseWrapper);

			logger.trace("returning response to \"{}\" for \"{}\"", request.getRemoteHost(), request.getRequestURI());
		}
		else {
			chain.doFilter(requestWrapper, response);
		}
	}

	@Override
	public void destroy() {}

	protected void initMediaTypes(FilterConfig config) throws ServletException {
		String configMediaTypes = config.getInitParameter("mediaTypes");


		logger.debug("configuring provided mediaTypes={}", configMediaTypes);

		if (StringUtils.isBlank(configMediaTypes)) {
			mediaTypes = Collections.emptyMap();
		}
		else {
			Pattern pattern = Pattern.compile("(.*)=(.*)(:?\\r?\\n)?");
			Matcher matcher = pattern.matcher(configMediaTypes);
			mediaTypes = new HashMap<>();

			while (matcher.find()) {
				String outputFormat = matcher.group(1);
				String contentType = matcher.group(2);

				if (StringUtils.isNotBlank(outputFormat) && StringUtils.isNotBlank(contentType)) {
					outputFormat = outputFormat.trim().toLowerCase();
					contentType = contentType.trim().toLowerCase();
					mediaTypes.put(outputFormat, contentType);
					logger.debug("mapped mediaType {} to {}", outputFormat, contentType);
				}
			}
		}
	}

	protected void initParameterName(FilterConfig config) throws ServletException {
		String configParameterName = config.getInitParameter("parameterName");

		if (StringUtils.isBlank(configParameterName)) {
			parameterName = "output_format";
		}
		else {
			parameterName = configParameterName.trim();
		}
	}

	protected String getContentType(HttpServletRequest request) {
		String contentType = null;
		String outputFormat = request.getParameter(parameterName);

		if (StringUtils.isNotBlank(outputFormat)) {
			outputFormat = outputFormat.toLowerCase();
			contentType = mediaTypes.get(outputFormat);
		}

		if (contentType == null) {
			contentType = "application/json";
		}

		logger.debug("parameter {}={} evaluated to contentType={}", parameterName, outputFormat, contentType);

		return contentType;
	}

	protected void traceRequestParameters(HttpServletRequest request) {
		StringBuilder log = new StringBuilder(512);
		log.append("received request from: \"").append(request.getRemoteHost()).append("\" ")
			.append("for \"").append(request.getRequestURI()).append("\"\n")
			.append("parameters:{");

		if (!request.getParameterMap().isEmpty()) {
			for (Enumeration e = request.getParameterNames(); e.hasMoreElements();) {
				String name = (String)e.nextElement();
				String[] values = request.getParameterValues(name);
				log.append("\n").append(name).append("=");

				if ((values == null) || (values.length == 0)) {
					log.append("null");
				}
				else {
					for (Iterator<String> i = Arrays.asList(values).iterator(); i.hasNext();) {
						log.append(i.next());

						if (i.hasNext()) {
							log.append(",");
						}
					}
				}
			}
			log.append("\n");
		}
		log.append("}\n");

		logger.trace(log.toString());
	}

	protected void debugRequestHeaders(HttpServletRequest request) {
		StringBuilder log = new StringBuilder(512);
		log.append("received request from: \"").append(request.getRemoteHost()).append("\" ")
			.append("for \"").append(request.getRequestURI()).append("\"\n")
			.append("headers: {");

		for (Enumeration e = request.getHeaderNames(); e.hasMoreElements();) {
			String name = (String)e.nextElement();
			String value = request.getHeader(name);
			log.append("\n").append(name).append("=").append(value);
		}
		log.append("\n}");

		logger.debug(log.toString());
	}
}
