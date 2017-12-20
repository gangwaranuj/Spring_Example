package com.workmarket.api.v2.employer.uploads.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.google.api.client.util.Lists;
import com.workmarket.api.ApiBaseError;
import com.workmarket.api.v2.employer.assignments.models.AbstractBuilder;
import io.swagger.annotations.ApiModelProperty;

import java.util.List;

@JsonDeserialize(builder = ErrorsDTO.Builder.class)
public class ErrorsDTO {
	private final String uuid;
	private final long count;
	private final List<List<ApiBaseError>> errors;

	public ErrorsDTO(@JsonProperty("uuid") String uuid, @JsonProperty("count") long count, @JsonProperty("errors") List<List<ApiBaseError>> errors) {
		this.uuid = uuid;
		this.count = count;
		this.errors = errors;
	}

	private ErrorsDTO(Builder builder) {
		this.uuid = builder.uuid;
		this.count = builder.count;
		this.errors = builder.errors;
	}

	@ApiModelProperty(name = "uuid")
	@JsonProperty("uuid")
	public String getUuid() {
		return uuid;
	}

	@ApiModelProperty(name = "count")
	@JsonProperty("count")
	public long getCount() {
		return count;
	}

	@ApiModelProperty(name = "errors")
	@JsonProperty("errors")
	public List<List<ApiBaseError>> getErrors() {
		return errors;
	}

	public static class Builder implements AbstractBuilder<ErrorsDTO> {
		private String uuid;
		private long count = 0;
		private List<List<ApiBaseError>> errors = Lists.newArrayList();

		public Builder(ErrorsDTO errorsDTO) {
			this.uuid = errorsDTO.uuid;
			this.count = errorsDTO.count;
			this.errors = errorsDTO.errors;
		}

		public Builder() {}

		@JsonProperty("uuid") public Builder setUuid(String uuid) {
			this.uuid = uuid;
			return this;
		}

		@JsonProperty("count") public Builder setCount(long count) {
			this.count = count;
			return this;
		}

		@JsonProperty("errors") public Builder setErrors(List<List<ApiBaseError>> errors) {
			this.errors = errors;
			return this;
		}

		public Builder addErrors(List<ApiBaseError> error) {
			this.errors.add(error);
			return this;
		}

		@Override
		public ErrorsDTO build() {
			return new ErrorsDTO(this);
		}
	}
}
