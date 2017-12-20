package com.workmarket.api.v2.worker.fulfillment;

import com.workmarket.api.v2.ApiV2Pagination;

import java.util.LinkedList;
import java.util.List;

/**
 * Generic response for fulfillment processors, will hold an API results list of API objects, along with
 * related processing data like pagination information, meta messages etc. that web controller may need for
 * final API response generation
 */
public class FulfillmentPayloadDTO/*<T>*/ {
	// TODO API - what do we do with this

	private List/*<T>*/ payload;
	private ApiV2Pagination pagination;
	private String message;
	private Boolean successful;

	public FulfillmentPayloadDTO() {
		this.payload = new LinkedList<>();
	}

	public List/*<T>*/ getPayload() {
		return payload;
	}

	public void setPayload(final List/*<T>*/ payload) {
		this.payload = payload;
	}

	public ApiV2Pagination getPagination() {
		return pagination;
	}

	public void setPagination(final ApiV2Pagination pagination) {
		this.pagination = pagination;
	}

	public void addResponseResult(final Object resultModel) {
		payload.add(resultModel);
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(final String message) {
		this.message = message;
	}

	public Boolean isSuccessful() {
		return successful;
	}

	public void setSuccessful(final Boolean successful) {
		this.successful = successful;
	}
}
