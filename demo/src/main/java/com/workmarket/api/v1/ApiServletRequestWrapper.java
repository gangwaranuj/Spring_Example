package com.workmarket.api.v1;

import com.workmarket.api.ApiBaseFilter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpServletResponse;
import java.util.Enumeration;

/**
 * Overrides "Accept" and "Content-Type" header values based on configured mediaTypes
 * @see ApiBaseFilter#initMediaTypes(javax.servlet.FilterConfig)
 * This override forces Spring MVC to use appropriate HttpMessageConverter to output results in requested format
 * such as XML, JSON or JSONP
 */
public class ApiServletRequestWrapper extends HttpServletRequestWrapper {
	private String acceptHeader;
	private HttpServletResponse response;

	public ApiServletRequestWrapper(HttpServletRequest request, HttpServletResponse response, String acceptHeader) {
		super(request);
		this.acceptHeader = acceptHeader;
		this.response = response;
	}

	public HttpServletResponse getResponse() {
		return response;
	}

	@Override
	public String getHeader(String name) {
		if ("Accept".equalsIgnoreCase(name) || "Content-Type".equalsIgnoreCase(name)) {
			return acceptHeader;
		}
		else {
			return super.getHeader(name);
		}
	}

	@Override
	public Enumeration getHeaders(String name) {
		if ("Accept".equalsIgnoreCase(name) || "Content-Type".equalsIgnoreCase(name)) {
			return new Enumeration() {
				transient boolean hasMoreElements = true;
				@Override
				public boolean hasMoreElements() {
					return hasMoreElements;
				}

				@Override
				public Object nextElement() {
					if (hasMoreElements) {
						hasMoreElements = false;
						return acceptHeader;
					}
					else {
						return null;
					}
				}
			};
		}
		else {
			return super.getHeaders(name);
		}
	}
}
