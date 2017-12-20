package com.workmarket.api.v3;

import com.workmarket.api.v3.response.ApiV3ResponseResult;
import com.workmarket.api.v3.response.ApiV3ResponseResultPagination;
import com.workmarket.api.v3.response.result.ApiV3Error;

import java.io.Serializable;
import java.util.List;

/**
 * Created by joshlevine on 12/22/16.
 */
public class ApiV3ResponseResultImpl<T> implements Serializable,
																									 ApiV3ResponseResult<T> {

	private List<ApiV3Error> warnings;
	private List<ApiV3Error> errors;
	private ApiV3ResponseResultPagination pagination;
	private List<T> payload;

	public ApiV3ResponseResultImpl() {
	}

	public ApiV3ResponseResultImpl(List<ApiV3Error> warnings,
																 List<ApiV3Error> errors,
																 ApiV3ResponseResultPagination pagination,
																 List<T> payload) {
		this.warnings = warnings;
		this.errors = errors;
		this.pagination = pagination;
		this.payload = payload;
	}

	@Override
	public List<ApiV3Error> getWarnings() {
		return warnings;
	}

	public void setWarnings(List<ApiV3Error> warnings) {
		this.warnings = warnings;
	}

	@Override
	public List<ApiV3Error> getErrors() {
		return errors;
	}

	public void setErrors(List<ApiV3Error> errors) {
		this.errors = errors;
	}

	public ApiV3ResponseResultPagination getPagination() {
		return pagination;
	}

	public void setPagination(ApiV3ResponseResultPagination pagination) {
		this.pagination = pagination;
	}

	public List<T> getPayload() {
		return payload;
	}

	public void setPayload(List<T> payload) {
		this.payload = payload;
	}
}
