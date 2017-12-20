package com.workmarket.common.template.email;

import com.workmarket.utility.DateUtilities;
import com.workmarket.web.exceptions.AuthenticatedException;
import org.apache.commons.lang.exception.ExceptionUtils;

import java.util.Calendar;
import java.util.Map;

public class ErrorTemplate extends EmailTemplate {
	private static final long serialVersionUID = -1888662986749764156L;
	private String hostName;
	private Calendar timestamp;
	private Throwable t;
	private Map<String, String> requestProperties;

	private static final int MAX_EXCEPTION_LENGTH = 50;

	public static final String REQUEST_PROPERTY_BASE_URL = "base_url";
	public static final String REQUEST_PROPERTY_REQUEST_URL = "request_url";
	public static final String REQUEST_PROPERTY_REFERRER = "referrer";
	public static final String REQUEST_PROPERTY_IP = "ip";
	public static final String REQUEST_PROPERTY_USER_AGENT = "user_agent";
	public static final String REQUEST_PROPERTY_REQUEST_TRACE = "request_trace";

	public static final String WM_FRAME = "(com\\.workmarket(?:.+))";

	public ErrorTemplate(Long fromUserId, String recipient, String hostName, Throwable t, Map<String, String> requestProperties) {
		super(fromUserId, recipient);

		this.hostName = hostName;
		this.timestamp = DateUtilities.getCalendarNowUtc();
		this.t = t;
		this.requestProperties = requestProperties;
	}

	private Throwable getRootCause() {
		Throwable error;

		if (t instanceof AuthenticatedException) {
			error = ((AuthenticatedException) t).getE();
		} else {
			error = t;
		}

		return (ExceptionUtils.getRootCause(error) == null) ? error : ExceptionUtils.getRootCause(error);
	}

	public boolean getAuthenticated() {
		return t instanceof AuthenticatedException;
	}

	public String getFormattedSubject() {
		Throwable error = getRootCause();

		if (error != null && ExceptionUtils.getMessage(error) != null) {
			if (ExceptionUtils.getMessage(error).length() > MAX_EXCEPTION_LENGTH) {
				return ExceptionUtils.getMessage(error).substring(0, MAX_EXCEPTION_LENGTH);
			} else {
				return ExceptionUtils.getMessage(error);
			}
		}

		return "Unknown exception";
	}

	public String getHostName() {
		return hostName;
	}

	public String getTimestamp() {
	 	return DateUtilities.formatCalendarForSQL(timestamp);
	}

	public String getUserNumber() {
		if (t instanceof AuthenticatedException) {
			return ((AuthenticatedException) t).getUserNumber();
		}

		return "";
	}

	public String getUserEmail() {
		if (t instanceof AuthenticatedException) {
			return ((AuthenticatedException) t).getUserEmail();
		}

		return "";
	}

	public String getCompanyName() {
		if (t instanceof AuthenticatedException) {
			return ((AuthenticatedException) t).getCompanyName();
		}

		return "";
	}

	public String getCompanyId() {
		if (t instanceof AuthenticatedException) {
			return ((AuthenticatedException) t).getCompanyId();
		}

		return "";
	}

	public String getStackTrace() {
		Throwable error = getRootCause();

		String fullStackTrack = ExceptionUtils.getFullStackTrace(error);

		if (fullStackTrack != null) {
			return fullStackTrack.replaceAll(WM_FRAME, "<strong>$1</strong>");
		}

		return "No stack trace";
	}

	public String getMessage() {
		Throwable error = getRootCause();

		if (error != null && ExceptionUtils.getMessage(error) != null) {
			return ExceptionUtils.getMessage(error);
		}

		return "Unknown exception";
	}

	public String getUserIP() {
		return requestProperties.get(REQUEST_PROPERTY_IP);
	}

	public String getRequestUrl() {
		return requestProperties.get(REQUEST_PROPERTY_REQUEST_URL);
	}

	public String getReferrer() {
		return requestProperties.get(REQUEST_PROPERTY_REFERRER);
	}

	public String getRequestTrace() {
		return requestProperties.get(REQUEST_PROPERTY_REQUEST_TRACE);
	}

	public String getUserAgent() {
		return requestProperties.get(REQUEST_PROPERTY_USER_AGENT);
	}

	public String getBaseUrl() {
		return requestProperties.get(REQUEST_PROPERTY_BASE_URL);
	}
}
