package com.workmarket.api.v2.model;

import com.fasterxml.jackson.annotation.JsonFilter;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.workmarket.api.ApiBaseHttpMessageConverter;
import com.workmarket.domains.work.model.project.Project;
import com.workmarket.utility.DateUtilities;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel
@JsonFilter(ApiBaseHttpMessageConverter.FILTER_API_PROJECTIONS)
@JsonDeserialize(builder = ProjectApiDTO.Builder.class)
public final class ProjectApiDTO {

	private final Long projectId;
	private final Long ownerId;
	private final Long clientCompanyId;
	private final String name;
	private final String description;
	private final String dueDate;
	private final boolean reservedFundsEnabled;

	public ProjectApiDTO(final Builder builder) {
		this.projectId = builder.projectId;
		this.ownerId = builder.ownerId;
		this.clientCompanyId = builder.clientCompanyId;
		this.name = builder.name;
		this.description = builder.description;
		this.dueDate = builder.dueDate;
		this.reservedFundsEnabled = builder.reservedFundsEnabled;
	}

	@ApiModelProperty(
			value = "Project ID. This is a monolith understood identifier; use UUID if you can.",
			example = "123455")
	@JsonProperty("projectId")
	public Long getProjectId() {
		return projectId;
	}

	@ApiModelProperty(
			value = "Owner ID. This is a monolith understood identifier; use UUID if you can.",
			example = "123455")
	@JsonProperty("ownerId")
	public Long getOwnerId() {
		return ownerId;
	}

	@ApiModelProperty(
			value = "Client Company ID. This is a monolith understood identifier; use UUID if you can.",
			example = "123455")
	@JsonProperty("clientCompanyId")
	public Long getClientCompanyId() {
		return clientCompanyId;
	}

	@ApiModelProperty(
			value = "Project Name.",
			example = "Sample Project")
	@JsonProperty("name")
	public String getName() {
		return name;
	}

	@ApiModelProperty(
			value = "Project Description.",
			example = "This project is just an example.")
	@JsonProperty("description")
	public String getDescription() {
		return description;
	}

	@ApiModelProperty(
			value = "Due date. When is this project due or when it is to be completed.",
			example = "2017-01-01T00:00:00Z")
	@JsonProperty("dueDate")
	public String getDueDate() {
		return dueDate;
	}

	@ApiModelProperty(
			value = "Are reserved funds enabled for this project?",
			example = "true")
	@JsonProperty("reservedFundsEnabled")
	public boolean isReservedFundsEnabled() {
		return reservedFundsEnabled;
	}

	public static final class Builder {

		private Long projectId;
		private Long ownerId;
		private Long clientCompanyId;
		private String name;
		private String description;
		private String dueDate;
		private boolean reservedFundsEnabled;

		public Builder() { }

		public Builder(final ProjectApiDTO projectApiDTO) {
			this.projectId = projectApiDTO.projectId;
			this.ownerId = projectApiDTO.ownerId;
			this.clientCompanyId = projectApiDTO.clientCompanyId;
			this.name = projectApiDTO.name;
			this.description = projectApiDTO.description;
			this.dueDate = projectApiDTO.dueDate;
			this.reservedFundsEnabled = projectApiDTO.reservedFundsEnabled;
		}

		public Builder(final Project project) {
			this.projectId = project.getId();
			this.ownerId = project.getOwner().getId();
			this.clientCompanyId = project.getClientCompany().getId();
			this.name = project.getName();
			this.description = project.getDescription();
			this.dueDate = DateUtilities.getISO8601(project.getDueDate());
			this.reservedFundsEnabled = project.isReservedFundsEnabled();
		}

		@JsonProperty("projectId")
		public Builder projectId(Long projectId) {
			this.projectId = projectId;
			return this;
		}

		@JsonProperty("ownerId")
		public Builder ownerId(Long ownerId) {
			this.ownerId = ownerId;
			return this;
		}

		@JsonProperty("clientCompanyId")
		public Builder clientCompanyId(Long clientCompanyId) {
			this.clientCompanyId = clientCompanyId;
			return this;
		}

		@JsonProperty("name")
		public Builder name(String name) {
			this.name = name;
			return this;
		}

		@JsonProperty("description")
		public Builder description(String description) {
			this.description = description;
			return this;
		}

		@JsonProperty("dueDate")
		public Builder dueDate(String dueDate) {
			this.dueDate = dueDate;
			return this;
		}

		@JsonProperty("reservedFundsEnabled")
		public Builder reservedFundsEnabled(boolean reservedFundsEnabled) {
			this.reservedFundsEnabled = reservedFundsEnabled;
			return this;
		}

		public ProjectApiDTO build() {
			return new ProjectApiDTO(this);
		}

	}
}
