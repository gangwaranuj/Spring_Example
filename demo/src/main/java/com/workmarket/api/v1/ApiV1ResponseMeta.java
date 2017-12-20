package com.workmarket.api.v1;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.workmarket.utility.CollectionUtilities;
import io.swagger.annotations.ApiModel;
import org.apache.commons.httpclient.HttpStatus;

import java.io.Serializable;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

@ApiModel(value = "v1Meta")
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonPropertyOrder(value = { "errors", "statusCode", "version", "requestId", "execution_time", "timestamp" })
public class ApiV1ResponseMeta implements Serializable {
	private static final long serialVersionUID = 1L;
	private static final String DEFAULT_HTML_TEMPLATE = "/template/html/ApiResponseTemplate.vm";

	private List errors;
	private int statusCode;
	private int version = 1;
	private double executionTime;
	private String requestId;
	private long timestamp;
	private String htmlTemplate;
	private String jsonPCallback;

	public ApiV1ResponseMeta() {
    this(Collections.emptyList());
	}

	public ApiV1ResponseMeta(List errors) {
		this.errors = errors == null ? Collections.emptyList() : errors;
		this.statusCode = this.errors.size() > 0 ? HttpStatus.SC_UNPROCESSABLE_ENTITY : HttpStatus.SC_OK;
		this.htmlTemplate = DEFAULT_HTML_TEMPLATE;
	}

	public ApiV1ResponseMeta(List errors, int statusCode) {
		this.errors       = errors == null ? Collections.emptyList() : errors;
		this.htmlTemplate = DEFAULT_HTML_TEMPLATE;
		this.statusCode   = statusCode;
	}

	@JsonProperty("errors")
	public List getErrors() {
		return errors;
	}

	public void setErrors(List errors) {
		this.errors = errors;
	}

	public void setErrorMessages(List<String> errors) {
		List<Map<String,Object>> errorMessages = new LinkedList<Map<String,Object>>();

		for (String message : errors) {
			errorMessages.add(
				CollectionUtilities.newObjectMap(
						"message", message,
						"code", "1"
				)
			);
		}

		setErrors(errorMessages);
	}

	@JsonProperty("status_code")
	public int getStatusCode() {
		return statusCode;
	}

	public void setStatusCode(int statusCode) {
		this.statusCode = statusCode;
	}

	@JsonProperty("version")
	public int getVersion() {
		return version;
	}

	public void setVersion(int version) {
		this.version = version;
	}

	@JsonProperty("execution_time")
	public double getExecutionTime() {
		return executionTime;
	}

	public void setExecutionTime(double executionTime) {
		this.executionTime = executionTime;
	}

	@JsonProperty("requestId")
	public String getRequestId() {
		return requestId;
	}

	public void setRequestId(String requestId) {
		this.requestId = requestId;
	}

	@JsonProperty("timestamp")
	public long getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(long timestamp) {
		this.timestamp = timestamp;
	}

	/**
	 * Used to tell the ApiV1ResponseToHtmlHttpMessageConverter which template to use.
	 * This property will not be included in the response.
	 * @return
	 */
	@JsonIgnore
	public String getHtmlTemplate() {
		return htmlTemplate;
	}

	public void setHtmlTemplate(String htmlTemplate) {
		this.htmlTemplate = htmlTemplate;
	}

	/**
	 * Used to tell the ApiV1ResponseToHtmlHttpMessageConverter which jsonp callback to use.
	 * This property will not be included in the response.
	 * @return
	 */
	@JsonIgnore
	public String getJsonPCallback() {
		return this.jsonPCallback;
	}
	public void setJsonPCallback(String jsonPCallback) {
		this.jsonPCallback = jsonPCallback;
	}
}