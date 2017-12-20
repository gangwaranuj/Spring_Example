package com.workmarket.api.v2.worker.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.workmarket.domains.model.WorkStatusType;

import org.springframework.web.bind.annotation.RequestParam;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import javax.validation.constraints.Max;

@ApiModel(value = "AssignmentRequest")
@JsonDeserialize(builder = AssignmentsRequestDTO.Builder.class)
public class AssignmentsRequestDTO {

//	@RequestParam(value = "status", defaultValue = "available") WorkStatusType statusFilter,
//	@RequestParam(value = "fields", defaultValue = "") String fields,
//	@RequestParam(value = "page", defaultValue = "1") Integer page,
//	@RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize,

	private final WorkStatusType status;
	private final String fields;
	private final Integer page;
	@Max(value = 30)
	private final Integer pageSize;
	private final String sort;

	private AssignmentsRequestDTO(Builder builder) {
		status = builder.status;
		fields = builder.fields;
		page = builder.page;
		pageSize = builder.pageSize;
		sort = builder.sort;
	}

	@ApiModelProperty(name = "status")
	@JsonProperty("status")
	public WorkStatusType getStatus() {
		return status;
	}

	@ApiModelProperty(name = "fields")
	@JsonProperty("fields")
	public String getFields() {
		return fields;
	}

	@ApiModelProperty(name = "page")
	@JsonProperty("page")
	public Integer getPage() {
		return page;
	}

	@ApiModelProperty(name = "pageSize")
	@JsonProperty("pageSize")
	public Integer getPageSize() {
		return pageSize;
	}

	@ApiModelProperty(name = "sort")
	@JsonProperty("sort")
	public String getSort() {
		return sort;
	}

	public static final class Builder {
		private WorkStatusType status = new WorkStatusType(WorkStatusType.AVAILABLE);
		private String fields = "";
		private Integer page = 1;
		@Max(value = 30)
		private Integer pageSize = 25;
		private String sort;

		public Builder() {
		}

		public Builder(AssignmentsRequestDTO copy) {
			this.status = copy.status;
			this.fields = copy.fields;
			this.page = copy.page;
			this.pageSize = copy.pageSize;
			this.sort = copy.sort;
		}

		@JsonProperty("status")
		public Builder withStatus(WorkStatusType status) {
			this.status = status;
			return this;
		}

		@JsonProperty("fields")
		public Builder withFields(String fields) {
			this.fields = fields;
			return this;
		}

		@JsonProperty("page")
		public Builder withPage(Integer page) {
			this.page = page;
			return this;
		}

		@JsonProperty("pageSize")
		public Builder withPageSize(Integer pageSize) {
			this.pageSize = pageSize;
			return this;
		}

		@JsonProperty("sort")
		public Builder withSort(String sort) {
			this.sort = sort;
			return this;
		}

		public AssignmentsRequestDTO build() {
			return new AssignmentsRequestDTO(this);
		}
	}
}
