package com.workmarket.service.external.vo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class GoogleRecaptchaResponse {

	@JsonProperty(value = "success", required = true)
	private boolean success;

	@JsonProperty(value = "timestamp", required = true)
	private String timestamp;

	@JsonProperty(value = "hostname", required = true)
	private String hostName;

	@JsonProperty(value = "error-codes", required = true)
	private String[] errorCodes;

	public boolean isSuccess() {
		return success;
	}

	public void setSuccess(final boolean success) {
		this.success = success;
	}

	public String getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(final String timestamp) {
		this.timestamp = timestamp;
	}

	public String getHostName() {
		return hostName;
	}

	public void setHostName(final String hostName) {
		this.hostName = hostName;
	}

	public String[] getErrorCodes() {
		return errorCodes;
	}

	public void setErrorCodes(final String[] errorCodes) {
		this.errorCodes = errorCodes;
	}
}
