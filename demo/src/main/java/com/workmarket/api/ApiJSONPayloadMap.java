package com.workmarket.api;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.common.collect.ForwardingMap;
import com.workmarket.api.v3.response.ApiV3ResponseMeta;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * This class is used to create JSON payloads without any null keys among the first level properties.
 * Currently we do it by decorating a ForwardingMap<String, Object></String,> to ignore nulls and empty collections.
 * <p>
 * TODO API - should we disallow empty collections ?
 * TODO API - should ensure the the values stored here pass some sort of schema validation ?
 */
public class ApiJSONPayloadMap extends ForwardingMap<String, Object> implements ApiV3ResponseMeta {

	public static final String META_V3_STATUS_CODE = "statusCode";
	public static final String META_V2_STATUS_CODE = "code";
	public static final String META_V1_STATUS_CODE = "status_code";
	public static final String META_V3_RESPONSE_TIME = "responseTime";
	public static final String META_V2_RESPONSE_TIME = "executionTime";
	public static final String META_V1_RESPONSE_TIME = "execution_time";
	public static final String META_TIMESTAMP = "timestamp";
	public static final String META_REQUEST_ID = "requestId";
	public static final String META_CLIENT_REQUEST_ID = "clientRequestId";


	private Map<String, Object> delegate = new HashMap<>();

	public ApiJSONPayloadMap() {
	}

	public ApiJSONPayloadMap(Map<String, Object> delegate) {
		this.delegate = delegate;
	}

	public Map<String, Object> delegate() {

		return this.delegate;
	}

	@Override
	public Object get(@Nullable String key) {
		return super.get(key);
	}

	/**
	 * Only puts non-null non-empty values into the underlying HashMap;
	 */
	@Override
	public Object put(final String key, final Object value) {
		if (value instanceof String) {
			if (StringUtils.isNotBlank((String) value)) {
				return delegate.put(key, value);
			}
		}
		if (value instanceof Collection) {
			if (CollectionUtils.isNotEmpty((Collection) value)) {
				return delegate.put(key, value);
			}
		}
		if (value instanceof Map) {
			if (value != null && ((Map) value).size() > 0) {
				return delegate.put(key, value);
			}
		}
		if (value != null) {
			return delegate.put(key, value);
		}
		return null;
	}

	@Override
	@JsonIgnore
	public Integer getStatusCode() {
		Object o = this.get(META_V3_STATUS_CODE);
		return o == null ? null : Integer.valueOf(String.valueOf(o));
	}

	@Override
	public void setStatusCode(Integer statusCode) {
		this.put(META_V1_STATUS_CODE, statusCode);
		this.put(META_V2_STATUS_CODE, statusCode);
		this.put(META_V3_STATUS_CODE, statusCode);
	}

	@Override
	@JsonIgnore
	public Double getResponseTime() {
		Object o = this.get(META_V3_RESPONSE_TIME);
		return o == null ? null : Double.valueOf(String.valueOf(o));
	}

	@Override
	public void setResponseTime(Double responseTime) {
		this.put(META_V3_RESPONSE_TIME, responseTime);
		this.put(META_V2_RESPONSE_TIME, responseTime);
		this.put(META_V1_RESPONSE_TIME, responseTime);
	}

	@Override
	@JsonIgnore
	public Long getTimestamp() {
		Object o = this.get(META_TIMESTAMP);
		return o == null ? null : Long.valueOf(String.valueOf(o));
	}

	@Override
	public void setTimestamp(Long timestamp) {
		this.put(META_TIMESTAMP, timestamp);
	}

	@Override
	@JsonIgnore
	public String getRequestId() {
		Object o = this.get(META_REQUEST_ID);
		return o == null ? null : String.valueOf(o);
	}

	@Override
	public void setRequestId(String requestId) {
		this.put(META_REQUEST_ID, requestId);
	}

	@Override
	@JsonIgnore
	public String getClientRequestId() {
		Object o = this.get(META_CLIENT_REQUEST_ID);
		return o == null ? null : String.valueOf(o);
	}

	@Override
	public void setClientRequestId(String clientRequestId) {
		this.put(META_CLIENT_REQUEST_ID, clientRequestId);
	}
}
