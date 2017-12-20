package com.workmarket.utility;

import com.google.common.base.Optional;
import org.apache.commons.codec.CharEncoding;
import org.apache.commons.lang.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Map;

public abstract class WebUtilities {

	/**
	 * Determines if the requestURI of the given request is prefixed by any of the given strings
	 *
	 * @param request
	 * @param excludedPaths Path prefixes to check against
	 *
	 * @return true if the request is prefixed by any of the given strings, false otherwise
	 */
	public static boolean isRequestURIPrefixedByAny(HttpServletRequest request, String...excludedPaths) {
		if ((excludedPaths == null) || (excludedPaths.length == 0)) {
			return false;
		}

		String requestPath = request.getRequestURI().substring(request.getContextPath().length());

		for (String excludedPath : excludedPaths) {
			if (StringUtils.startsWith(requestPath, excludedPath)) {
				return true;
			}
		}

		return false;
	}

	/**
	 * Given a map, this method will form encode each entry into a string of the format
	 * key=value&key2=value2...
	 * @param parameters
	 * @return A string containing the form encoded map or Optional.absent() if encoding fails.
	 */
	public static Optional<String> formEncodeMap(Map<?, ?> parameters) {
		String body = "";

		if (parameters.isEmpty())
			return Optional.fromNullable(body);

		try {
			for (Map.Entry<?, ?> entry : parameters.entrySet()) {
				body += URLEncoder.encode(String.valueOf(entry.getKey()), CharEncoding.UTF_8) + "=" + URLEncoder.encode(String.valueOf(entry.getValue()), CharEncoding.UTF_8) + "&";
			}
		} catch (UnsupportedEncodingException e) {
			return Optional.absent();
		}

		return Optional.fromNullable(body.substring(0, body.length() - 1));
	}

	public static boolean isAjax(HttpServletRequest request) {
		return "XMLHttpRequest".equalsIgnoreCase(request.getHeader("X-Requested-With"));
	}

	// returns true if the request is for the main HTML page and not an ajax or jsp import
	public static boolean isPageRequest(HttpServletRequest request) {
		return !(isAjax(request) || request.getRequestURI().endsWith(".jsp"));
	}

	public static boolean isPageRequestedAsHtml(HttpServletRequest request){
		return request.getRequestURI().endsWith(".html");
	}
}
