package com.workmarket.api.v1.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.util.List;

@ApiModel(value = "AssignmentListItem")
@JsonDeserialize(builder = ApiAssignmentListItemDTO.Builder.class)
public class ApiAssignmentListItemDTO {
	private final String id;
	private final Long projectId;
	private final String projectName;
	private final String title;
	private final Long scheduledStart;
	private final Long scheduledEnd;
	private final String city;
	private final String state;
	private final String postalCode;
	private final String locationId;
	private final String spendLimit;
	private final String modifiedStatus;
	private final String status;
	private final List<ApiLabelDTO> substatuses;
	private final List<ApiLabelDTO> labels;
	private final String internalOwner;
	private final String client;
	private final Long clientId;
	private final String invoiceNumber;
	private final Long paidDate;
	private final String totalCost;
	private final String resourceCompanyName;
	private final String resourceUserNumber;
	private final String resourceFullName;
	private final Long lastModifiedOn;
	private final String modifierFirstName;
	private final String modifierLastName;

	private ApiAssignmentListItemDTO(Builder builder) {
		id = builder.id;
		projectId = builder.projectId;
		projectName = builder.projectName;
		title = builder.title;
		scheduledStart = builder.scheduledStart;
		scheduledEnd = builder.scheduledEnd;
		city = builder.city;
		state = builder.state;
		postalCode = builder.postalCode;
		locationId = builder.locationId;
		spendLimit = builder.spendLimit;
		modifiedStatus = builder.modifiedStatus;
		status = builder.status;
		substatuses = builder.substatuses;
		labels = builder.labels;
		internalOwner = builder.internalOwner;
		client = builder.client;
		clientId = builder.clientId;
		invoiceNumber = builder.invoiceNumber;
		paidDate = builder.paidDate;
		totalCost = builder.totalCost;
		resourceCompanyName = builder.resourceCompanyName;
		resourceUserNumber = builder.resourceUserNumber;
		resourceFullName = builder.resourceFullName;
		lastModifiedOn = builder.lastModifiedOn;
		modifierFirstName = builder.modifierFirstName;
		modifierLastName = builder.modifierLastName;
	}

	@ApiModelProperty(name = "id")
	@JsonProperty("id")
	public String getId() {
		return id;
	}

	@ApiModelProperty(name = "project_id")
	@JsonProperty("project_id")
	public Long getProjectId() {
		return projectId;
	}

	@ApiModelProperty(name = "project_name")
	@JsonProperty("project_name")
	public String getProjectName() {
		return projectName;
	}

	@ApiModelProperty(name = "title")
	@JsonProperty("title")
	public String getTitle() {
		return title;
	}

	@ApiModelProperty(name = "scheduled_start")
	@JsonProperty("scheduled_start")
	public Long getScheduledStart() {
		return scheduledStart;
	}

	@ApiModelProperty(name = "scheduled_end")
	@JsonProperty("scheduled_end")
	public Long getScheduledEnd() {
		return scheduledEnd;
	}

	@ApiModelProperty(name = "city")
	@JsonProperty("city")
	public String getCity() {
		return city;
	}

	@ApiModelProperty(name = "state")
	@JsonProperty("state")
	public String getState() {
		return state;
	}

	@ApiModelProperty(name = "postal_code")
	@JsonProperty("postal_code")
	public String getPostalCode() {
		return postalCode;
	}

	@ApiModelProperty(name = "location_id")
	@JsonProperty("location_id")
	public String getLocationId() {
		return locationId;
	}

	@ApiModelProperty(name = "spend_limit")
	@JsonProperty("spend_limit")
	public String getSpendLimit() {
		return spendLimit;
	}

	@ApiModelProperty(name = "modified_status")
	@JsonProperty("modified_status")
	public String getModifiedStatus() {
		return modifiedStatus;
	}

	@ApiModelProperty(name = "status")
	@JsonProperty("status")
	public String getStatus() {
		return status;
	}

	@ApiModelProperty(name = "substatuses")
	@JsonProperty("substatuses")
	public List<ApiLabelDTO> getSubstatuses() {
		return substatuses;
	}

	@ApiModelProperty(name = "labels")
	@JsonProperty("labels")
	public List<ApiLabelDTO> getLabels() {
		return labels;
	}

	@ApiModelProperty(name = "internal_owner")
	@JsonProperty("internal_owner")
	public String getInternalOwner() {
		return internalOwner;
	}

	@ApiModelProperty(name = "client")
	@JsonProperty("client")
	public String getClient() {
		return client;
	}

	@ApiModelProperty(name = "client_id")
	@JsonProperty("client_id")
	public Long getClientId() {
		return clientId;
	}

	@ApiModelProperty(name = "invoice_number")
	@JsonProperty("invoice_number")
	public String getInvoiceNumber() {
		return invoiceNumber;
	}

	@ApiModelProperty(name = "paid_date")
	@JsonProperty("paid_date")
	public Long getPaidDate() {
		return paidDate;
	}

	@ApiModelProperty(name = "total_cost")
	@JsonProperty("total_cost")
	public String getTotalCost() {
		return totalCost;
	}

	@ApiModelProperty(name = "resource_company_name")
	@JsonProperty("resource_company_name")
	public String getResourceCompanyName() {
		return resourceCompanyName;
	}

	@ApiModelProperty(name = "resource_user_number")
	@JsonProperty("resource_user_number")
	public String getResourceUserNumber() {
		return resourceUserNumber;
	}

	@ApiModelProperty(name = "resource_full_name")
	@JsonProperty("resource_full_name")
	public String getResourceFullName() {
		return resourceFullName;
	}

	@ApiModelProperty(name = "last_modified_on")
	@JsonProperty("last_modified_on")
	public Long getLastModifiedOn() {
		return lastModifiedOn;
	}

	@ApiModelProperty(name = "modifier_first_name")
	@JsonProperty("modifier_first_name")
	public String getModifierFirstName() {
		return modifierFirstName;
	}

	@ApiModelProperty(name = "modifier_last_name")
	@JsonProperty("modifier_last_name")
	public String getModifierLastName() {
		return modifierLastName;
	}

	public static final class Builder {
		private String id;
		private Long projectId;
		private String projectName;
		private String title;
		private Long scheduledStart;
		private Long scheduledEnd;
		private String city;
		private String state;
		private String postalCode;
		private String locationId;
		private String spendLimit;
		private String modifiedStatus;
		private String status;
		private List<ApiLabelDTO> substatuses;
		private List<ApiLabelDTO> labels;
		private String internalOwner;
		private String client;
		private Long clientId;
		private String invoiceNumber;
		private Long paidDate;
		private String totalCost;
		private String resourceCompanyName;
		private String resourceUserNumber;
		private String resourceFullName;
		private Long lastModifiedOn;
		private String modifierFirstName;
		private String modifierLastName;

		public Builder() {
		}

		public Builder(ApiAssignmentListItemDTO copy) {
			this.id = copy.id;
			this.projectId = copy.projectId;
			this.projectName = copy.projectName;
			this.title = copy.title;
			this.scheduledStart = copy.scheduledStart;
			this.scheduledEnd = copy.scheduledEnd;
			this.city = copy.city;
			this.state = copy.state;
			this.postalCode = copy.postalCode;
			this.locationId = copy.locationId;
			this.spendLimit = copy.spendLimit;
			this.modifiedStatus = copy.modifiedStatus;
			this.status = copy.status;
			this.substatuses = copy.substatuses;
			this.labels = copy.labels;
			this.internalOwner = copy.internalOwner;
			this.client = copy.client;
			this.clientId = copy.clientId;
			this.invoiceNumber = copy.invoiceNumber;
			this.paidDate = copy.paidDate;
			this.totalCost = copy.totalCost;
			this.resourceCompanyName = copy.resourceCompanyName;
			this.resourceUserNumber = copy.resourceUserNumber;
			this.resourceFullName = copy.resourceFullName;
			this.lastModifiedOn = copy.lastModifiedOn;
			this.modifierFirstName = copy.modifierFirstName;
			this.modifierLastName = copy.modifierLastName;
		}

		@JsonProperty("id")
		public Builder withId(String id) {
			this.id = id;
			return this;
		}

		@JsonProperty("project_id")
		public Builder withProjectId(Long projectId) {
			this.projectId = projectId;
			return this;
		}

		@JsonProperty("project_name")
		public Builder withProjectName(String projectName) {
			this.projectName = projectName;
			return this;
		}

		@JsonProperty("title")
		public Builder withTitle(String title) {
			this.title = title;
			return this;
		}

		@JsonProperty("scheduled_start")
		public Builder withScheduledStart(Long scheduledStart) {
			this.scheduledStart = scheduledStart;
			return this;
		}

		@JsonProperty("scheduled_end")
		public Builder withScheduledEnd(Long scheduledEnd) {
			this.scheduledEnd = scheduledEnd;
			return this;
		}

		@JsonProperty("city")
		public Builder withCity(String city) {
			this.city = city;
			return this;
		}

		@JsonProperty("state")
		public Builder withState(String state) {
			this.state = state;
			return this;
		}

		@JsonProperty("postal_code")
		public Builder withPostalCode(String postalCode) {
			this.postalCode = postalCode;
			return this;
		}

		@JsonProperty("location_id")
		public Builder withLocationId(String locationId) {
			this.locationId = locationId;
			return this;
		}

		@JsonProperty("spend_limit")
		public Builder withSpendLimit(String spendLimit) {
			this.spendLimit = spendLimit;
			return this;
		}

		@JsonProperty("modified_status")
		public Builder withModifiedStatus(String modifiedStatus) {
			this.modifiedStatus = modifiedStatus;
			return this;
		}

		@JsonProperty("status")
		public Builder withStatus(String status) {
			this.status = status;
			return this;
		}

		@JsonProperty("substatuses")
		public Builder withSubstatuses(List<ApiLabelDTO> substatuses) {
			this.substatuses = substatuses;
			return this;
		}

		@JsonProperty("labels")
		public Builder withLabels(List<ApiLabelDTO> labels) {
			this.labels = labels;
			return this;
		}

		@JsonProperty("internal_owner")
		public Builder withInternalOwner(String internalOwner) {
			this.internalOwner = internalOwner;
			return this;
		}

		@JsonProperty("client")
		public Builder withClient(String client) {
			this.client = client;
			return this;
		}

		@JsonProperty("client_id")
		public Builder withClientId(Long clientId) {
			this.clientId = clientId;
			return this;
		}

		@JsonProperty("invoice_number")
		public Builder withInvoiceNumber(String invoiceNumber) {
			this.invoiceNumber = invoiceNumber;
			return this;
		}

		@JsonProperty("paid_date")
		public Builder withPaidDate(Long paidDate) {
			this.paidDate = paidDate;
			return this;
		}

		@JsonProperty("total_cost")
		public Builder withTotalCost(String totalCost) {
			this.totalCost = totalCost;
			return this;
		}

		@JsonProperty("resource_company_name")
		public Builder withResourceCompanyName(String resourceCompanyName) {
			this.resourceCompanyName = resourceCompanyName;
			return this;
		}

		@JsonProperty("resource_user_number")
		public Builder withResourceUserNumber(String resourceUserNumber) {
			this.resourceUserNumber = resourceUserNumber;
			return this;
		}

		@JsonProperty("resource_full_name")
		public Builder withResourceFullName(String resourceFullName) {
			this.resourceFullName = resourceFullName;
			return this;
		}

		@JsonProperty("last_modified_on")
		public Builder withLastModifiedOn(Long lastModifiedOn) {
			this.lastModifiedOn = lastModifiedOn;
			return this;
		}

		@JsonProperty("modifier_first_name")
		public Builder withModifierFirstName(String modifierFirstName) {
			this.modifierFirstName = modifierFirstName;
			return this;
		}

		@JsonProperty("modifier_last_name")
		public Builder withModifierLastName(String modifierLastName) {
			this.modifierLastName = modifierLastName;
			return this;
		}

		public ApiAssignmentListItemDTO build() {
			return new ApiAssignmentListItemDTO(this);
		}
	}
}
