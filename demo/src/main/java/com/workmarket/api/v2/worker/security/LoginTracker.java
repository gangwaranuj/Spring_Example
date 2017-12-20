package com.workmarket.api.v2.worker.security;

import com.google.common.collect.ImmutableSet;
import edu.emory.mathcs.backport.java.util.Collections;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Set;

/**
 * Created by joshlevine on 2/9/17.
 */
public class LoginTracker {

	private static final Log logger = LogFactory.getLog(LoginTracker.class);
	private static final String[] REDACT_SUCCESSFUL_PAYLOAD_NAMES = {"password"};
	private static final Set<String> REDACT_SUCCESSFUL_PAYLOAD = ImmutableSet.copyOf(REDACT_SUCCESSFUL_PAYLOAD_NAMES);

	public void logDetailsOfSigninSuccess(HttpServletRequest request, String context) {

		String requestDetails = buildDump(request, REDACT_SUCCESSFUL_PAYLOAD);

		logger.warn("Successful login[" + context + "]  - dumping deets:" + requestDetails);

	}

	public void logDetailsOfSigninFailure(HttpServletRequest request, String context) {

		String requestDetails = buildDump(request, Collections.emptySet());

		logger.warn("Failed login[" + context + "] - dumping deets:" + requestDetails);

	}

	private String buildDump(HttpServletRequest request, Set<String> redactedFields) {
		StringBuilder requestDetails = new StringBuilder();
		requestDetails.append("\n");

		String payload = getPayload(request, redactedFields);
		String headers = getHeaders(request);


		requestDetails
			.append("Remote host: \"")
			.append(request.getRemoteHost()).append("\" ")
			.append("URI: \"")
			.append(request.getRequestURI())
			.append("\"\n");
		requestDetails.append(headers);
		requestDetails.append(payload.toString());

		return requestDetails.toString();
	}

	private String getPayload(HttpServletRequest request, Set<String> redactedParams) {
		StringBuilder payload = new StringBuilder();
		payload.append("parameters:{");

		if (!request.getParameterMap().isEmpty()) {
			for (Enumeration e = request.getParameterNames(); e.hasMoreElements(); ) {
				String name = (String) e.nextElement();
				if(redactedParams.contains(name)) {
					continue;
				}
				String[] values = request.getParameterValues(name);
				payload.append("\n").append(name).append("=");

				if ((values == null) || (values.length == 0)) {
					payload.append("null");
				} else {
					for (Iterator<String> i = Arrays.asList(values).iterator(); i.hasNext(); ) {
						payload.append(i.next());

						if (i.hasNext()) {
							payload.append(",");
						}
					}
				}
			}
			payload.append("\n");
		}
		payload.append("}\n");
		return payload.toString();
	}

	private String getHeaders(HttpServletRequest request) {
		StringBuilder headers = new StringBuilder();
		headers.append("headers: {");

		for (Enumeration e = request.getHeaderNames(); e.hasMoreElements(); ) {
			String name = (String) e.nextElement();
			String value = request.getHeader(name);
			headers.append("\n").append(name).append("=").append(value);
		}
		headers.append("\n}\n");
		return headers.toString();
	}
}