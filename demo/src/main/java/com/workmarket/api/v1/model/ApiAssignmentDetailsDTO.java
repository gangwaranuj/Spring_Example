package com.workmarket.api.v1.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.util.List;

@ApiModel(value = "AssignmentDetails")
@JsonDeserialize(builder = ApiAssignmentDetailsDTO.Builder.class)
public class ApiAssignmentDetailsDTO {

	private final String id;
	private final String title;
	private final String description;
	private final String instructions;
	private final String desiredSkills;
	private final String shortUrl;
	private final String status;
	private final List<ApiAssignmentDetailsLabelDTO> substatuses;
	private final List<ApiAssignmentDetailsLabelDTO> labels;
	private final Long project;
	private final Long client;
	private final String internalOwner;
	private final ApiInternalOwnerDetailsDTO internalOwnerDetails;
	private final Integer sentWorkerCount;
	private final Long assignmentWindowStart;
	private final String assignmentWindowStartDate;
	private final Long assignmentWindowEnd;
	private final String assignmentWindowEndDate;
	private final Long scheduledTime;
	private final String scheduledTimeDate;
	private final Long scheduledStart;
	private final Long scheduledEnd;
	private final ApiRescheduleRequestDTO rescheduleRequest;
	private final ApiBudgetIncreaseRequestDTO budgetIncreaseRequest;
	private final ApiExpenseReimbursementRequestDTO expenseReimbursementRequest;
	private final ApiBonusRequestDTO bonusRequest;
	private final String resolution;
	private final String industry;
	private final String timeZone;
	private final Integer requiredAttachments;
	private final ApiLocationDTO location;
	private final Boolean locationOffsite;
	private final ApiLocationContactDTO locationContact;
	private final ApiSupportContactDTO supportContact;
	private final ApiActiveResourceDTO activeResource;
	private final ApiPricingDTO pricing;
	private final String invoiceNumber;
	private final ApiPaymentDTO payment;
	private final List<ApiAssignmentDetailsAttachmentDTO> attachments;
	private final Long createdOn;
	private final Long lastModifiedOn;
	private final List<ApiHistoryDTO> history;
	private final List<ApiNoteDTO> notes;
	private final List<ApiCustomFieldGroupDTO> customFields;
	private final ApiPartsDTO parts;
	private final List<ApiApplicationDTO> pendingOffers;
	private final List<ApiDeclinedResourceDTO> declinedResources;
	private final List<ApiQuestionAnswerPairDTO> questions;

	protected ApiAssignmentDetailsDTO(Builder builder) {
		id = builder.id;
		title = builder.title;
		description = builder.description;
		instructions = builder.instructions;
		desiredSkills = builder.desiredSkills;
		shortUrl = builder.shortUrl;
		status = builder.status;
		substatuses = builder.substatuses;
		labels = builder.labels;
		project = builder.project;
		client = builder.client;
		internalOwner = builder.internalOwner;
		internalOwnerDetails = builder.internalOwnerDetails;
		sentWorkerCount = builder.sentWorkerCount;
		assignmentWindowStart = builder.assignmentWindowStart;
		assignmentWindowStartDate = builder.assignmentWindowStartDate;
		assignmentWindowEnd = builder.assignmentWindowEnd;
		assignmentWindowEndDate = builder.assignmentWindowEndDate;
		scheduledTime = builder.scheduledTime;
		scheduledTimeDate = builder.scheduledTimeDate;
		scheduledStart = builder.scheduledStart;
		scheduledEnd = builder.scheduledEnd;
		rescheduleRequest = builder.rescheduleRequest;
		budgetIncreaseRequest = builder.budgetIncreaseRequest;
		expenseReimbursementRequest = builder.expenseReimbursementRequest;
		bonusRequest = builder.bonusRequest;
		resolution = builder.resolution;
		industry = builder.industry;
		timeZone = builder.timeZone;
		requiredAttachments = builder.requiredAttachments;
		location = builder.location;
		locationOffsite = builder.locationOffsite;
		locationContact = builder.locationContact;
		supportContact = builder.supportContact;
		activeResource = builder.activeResource;
		pricing = builder.pricing;
		invoiceNumber = builder.invoiceNumber;
		payment = builder.payment;
		attachments = builder.attachments;
		createdOn = builder.createdOn;
		lastModifiedOn = builder.lastModifiedOn;
		history = builder.history;
		notes = builder.notes;
		customFields = builder.customFields;
		parts = builder.parts;
		pendingOffers = builder.pendingOffers;
		declinedResources = builder.declinedResources;
		questions = builder.questions;
	}

	@ApiModelProperty(name = "id")
	@JsonProperty("id")
	public String getId() {
		return id;
	}

	@ApiModelProperty(name = "title")
	@JsonProperty("title")
	public String getTitle() {
		return title;
	}

	@ApiModelProperty(name = "description")
	@JsonProperty("description")
	public String getDescription() {
		return description;
	}

	@ApiModelProperty(name = "instructions")
	@JsonProperty("instructions")
	public String getInstructions() {
		return instructions;
	}

	@ApiModelProperty(name = "desired_skills")
	@JsonProperty("desired_skills")
	public String getDesiredSkills() {
		return desiredSkills;
	}

	@ApiModelProperty(name = "short_url")
	@JsonProperty("short_url")
	public String getShortUrl() {
		return shortUrl;
	}

	@ApiModelProperty(name = "status")
	@JsonProperty("status")
	public String getStatus() {
		return status;
	}

	@ApiModelProperty(name = "substatuses")
	@JsonProperty("substatuses")
	public List<ApiAssignmentDetailsLabelDTO> getSubstatuses() {
		return substatuses;
	}

	@ApiModelProperty(name = "labels")
	@JsonProperty("labels")
	public List<ApiAssignmentDetailsLabelDTO> getLabels() {
		return labels;
	}

	@ApiModelProperty(name = "project")
	@JsonProperty("project")
	public Long getProject() {
		return project;
	}

	@ApiModelProperty(name = "client")
	@JsonProperty("client")
	public Long getClient() {
		return client;
	}

	@ApiModelProperty(name = "internal_owner")
	@JsonProperty("internal_owner")
	public String getInternalOwner() {
		return internalOwner;
	}

	@ApiModelProperty(name = "internal_owner_details")
	@JsonProperty("internal_owner_details")
	public ApiInternalOwnerDetailsDTO getInternalOwnerDetails() {
		return internalOwnerDetails;
	}

	@ApiModelProperty(name = "sent_worker_count")
	@JsonProperty("sent_worker_count")
	public Integer getSentWorkerCount() {
		return sentWorkerCount;
	}

	@ApiModelProperty(name = "assignment_window_start")
	@JsonProperty("assignment_window_start")
	public Long getAssignmentWindowStart() {
		return assignmentWindowStart;
	}

	@ApiModelProperty(name = "assignment_window_start_date")
	@JsonProperty("assignment_window_start_date")
	public String getAssignmentWindowStartDate() {
		return assignmentWindowStartDate;
	}

	@ApiModelProperty(name = "assignment_window_end")
	@JsonProperty("assignment_window_end")
	public Long getAssignmentWindowEnd() {
		return assignmentWindowEnd;
	}

	@ApiModelProperty(name = "assignment_window_end_date")
	@JsonProperty("assignment_window_end_date")
	public String getAssignmentWindowEndDate() {
		return assignmentWindowEndDate;
	}

	@ApiModelProperty(name = "scheduled_time")
	@JsonProperty("scheduled_time")
	public Long getScheduledTime() {
		return scheduledTime;
	}

	@ApiModelProperty(name = "scheduled_time_date")
	@JsonProperty("scheduled_time_date")
	public String getScheduledTimeDate() {
		return scheduledTimeDate;
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

	@ApiModelProperty(name = "reschedule_request")
	@JsonProperty("reschedule_request")
	public ApiRescheduleRequestDTO getRescheduleRequest() {
		return rescheduleRequest;
	}

	@ApiModelProperty(name = "budget_increase_request")
	@JsonProperty("budget_increase_request")
	public ApiBudgetIncreaseRequestDTO getBudgetIncreaseRequest() {
		return budgetIncreaseRequest;
	}

	@ApiModelProperty(name = "expense_reimbursement_request")
	@JsonProperty("expense_reimbursement_request")
	public ApiExpenseReimbursementRequestDTO getExpenseReimbursementRequest() {
		return expenseReimbursementRequest;
	}

	@ApiModelProperty(name = "bonus_request")
	@JsonProperty("bonus_request")
	public ApiBonusRequestDTO getBonusRequest() {
		return bonusRequest;
	}

	@ApiModelProperty(name = "resolution")
	@JsonProperty("resolution")
	public String getResolution() {
		return resolution;
	}

	@ApiModelProperty(name = "industry")
	@JsonProperty("industry")
	public String getIndustry() {
		return industry;
	}

	@ApiModelProperty(name = "time_zone")
	@JsonProperty("time_zone")
	public String getTimeZone() {
		return timeZone;
	}

	@ApiModelProperty(name = "required_attachments")
	@JsonProperty("required_attachments")
	public Integer getRequiredAttachments() {
		return requiredAttachments;
	}

	@ApiModelProperty(name = "location")
	@JsonProperty("location")
	public ApiLocationDTO getLocation() {
		return location;
	}

	@ApiModelProperty(name = "location_offsite")
	@JsonProperty("location_offsite")
	public Boolean getLocationOffsite() {
		return locationOffsite;
	}

	@ApiModelProperty(name = "location_contact")
	@JsonProperty("location_contact")
	public ApiLocationContactDTO getLocationContact() {
		return locationContact;
	}

	@ApiModelProperty(name = "support_contact")
	@JsonProperty("support_contact")
	public ApiSupportContactDTO getSupportContact() {
		return supportContact;
	}

	@ApiModelProperty(name = "active_resource")
	@JsonProperty("active_resource")
	public ApiActiveResourceDTO getActiveResource() {
		return activeResource;
	}

	@ApiModelProperty(name = "pricing")
	@JsonProperty("pricing")
	public ApiPricingDTO getPricing() {
		return pricing;
	}

	@ApiModelProperty(name = "invoice_number")
	@JsonProperty("invoice_number")
	public String getInvoiceNumber() {
		return invoiceNumber;
	}

	@ApiModelProperty(name = "payment")
	@JsonProperty("payment")
	public ApiPaymentDTO getPayment() {
		return payment;
	}

	@ApiModelProperty(name = "attachments")
	@JsonProperty("attachments")
	public List<ApiAssignmentDetailsAttachmentDTO> getAttachments() {
		return attachments;
	}

	@ApiModelProperty(name = "created_on")
	@JsonProperty("created_on")
	public Long getCreatedOn() {
		return createdOn;
	}

	@ApiModelProperty(name = "last_modified_on")
	@JsonProperty("last_modified_on")
	public Long getLastModifiedOn() {
		return lastModifiedOn;
	}

	@ApiModelProperty(name = "history")
	@JsonProperty("history")
	public List<ApiHistoryDTO> getHistory() {
		return history;
	}

	@ApiModelProperty(name = "notes")
	@JsonProperty("notes")
	public List<ApiNoteDTO> getNotes() {
		return notes;
	}

	@ApiModelProperty(name = "custom_fields")
	@JsonProperty("custom_fields")
	public List<ApiCustomFieldGroupDTO> getCustomFields() {
		return customFields;
	}

	@ApiModelProperty(name = "parts")
	@JsonProperty("parts")
	public ApiPartsDTO getParts() {
		return parts;
	}

	@ApiModelProperty(name = "pending_offers")
	@JsonProperty("pending_offers")
	public List<ApiApplicationDTO> getPendingOffers() {
		return pendingOffers;
	}

	@ApiModelProperty(name = "declined_resources")
	@JsonProperty("declined_resources")
	public List<ApiDeclinedResourceDTO> getDeclinedResources() {
		return declinedResources;
	}

	@ApiModelProperty(name = "questions")
	@JsonProperty("questions")
	public List<ApiQuestionAnswerPairDTO> getQuestions() {
		return questions;
	}

	public static class Builder {
		private String id;
		private String title;
		private String description;
		private String instructions;
		private String desiredSkills;
		private String shortUrl;
		private String status;
		private List<ApiAssignmentDetailsLabelDTO> substatuses;
		private List<ApiAssignmentDetailsLabelDTO> labels;
		private Long project;
		private Long client;
		private String internalOwner;
		private ApiInternalOwnerDetailsDTO internalOwnerDetails;
		private Integer sentWorkerCount;
		private Long assignmentWindowStart;
		private String assignmentWindowStartDate;
		private Long assignmentWindowEnd;
		private String assignmentWindowEndDate;
		private Long scheduledTime;
		private String scheduledTimeDate;
		private Long scheduledStart;
		private Long scheduledEnd;
		private ApiRescheduleRequestDTO rescheduleRequest;
		private ApiBudgetIncreaseRequestDTO budgetIncreaseRequest;
		private ApiExpenseReimbursementRequestDTO expenseReimbursementRequest;
		private ApiBonusRequestDTO bonusRequest;
		private String resolution;
		private String industry;
		private String timeZone;
		private Integer requiredAttachments;
		private ApiLocationDTO location;
		private Boolean locationOffsite;
		private ApiLocationContactDTO locationContact;
		private ApiSupportContactDTO supportContact;
		private ApiActiveResourceDTO activeResource;
		private ApiPricingDTO pricing;
		private String invoiceNumber;
		private ApiPaymentDTO payment;
		private List<ApiAssignmentDetailsAttachmentDTO> attachments;
		private Long createdOn;
		private Long lastModifiedOn;
		private List<ApiHistoryDTO> history;
		private List<ApiNoteDTO> notes;
		private List<ApiCustomFieldGroupDTO> customFields;
		private ApiPartsDTO parts;
		private List<ApiApplicationDTO> pendingOffers;
		private List<ApiDeclinedResourceDTO> declinedResources;
		private List<ApiQuestionAnswerPairDTO> questions;

		public Builder() {
		}

		public Builder(ApiAssignmentDetailsDTO copy) {
			this.id = copy.id;
			this.title = copy.title;
			this.description = copy.description;
			this.instructions = copy.instructions;
			this.desiredSkills = copy.desiredSkills;
			this.shortUrl = copy.shortUrl;
			this.status = copy.status;
			this.substatuses = copy.substatuses;
			this.labels = copy.labels;
			this.project = copy.project;
			this.client = copy.client;
			this.internalOwner = copy.internalOwner;
			this.internalOwnerDetails = copy.internalOwnerDetails;
			this.sentWorkerCount = copy.sentWorkerCount;
			this.assignmentWindowStart = copy.assignmentWindowStart;
			this.assignmentWindowStartDate = copy.assignmentWindowStartDate;
			this.assignmentWindowEnd = copy.assignmentWindowEnd;
			this.assignmentWindowEndDate = copy.assignmentWindowEndDate;
			this.scheduledTime = copy.scheduledTime;
			this.scheduledTimeDate = copy.scheduledTimeDate;
			this.scheduledStart = copy.scheduledStart;
			this.scheduledEnd = copy.scheduledEnd;
			this.rescheduleRequest = copy.rescheduleRequest;
			this.budgetIncreaseRequest = copy.budgetIncreaseRequest;
			this.expenseReimbursementRequest = copy.expenseReimbursementRequest;
			this.bonusRequest = copy.bonusRequest;
			this.resolution = copy.resolution;
			this.industry = copy.industry;
			this.timeZone = copy.timeZone;
			this.requiredAttachments = copy.requiredAttachments;
			this.location = copy.location;
			this.locationOffsite = copy.locationOffsite;
			this.locationContact = copy.locationContact;
			this.supportContact = copy.supportContact;
			this.activeResource = copy.activeResource;
			this.pricing = copy.pricing;
			this.invoiceNumber = copy.invoiceNumber;
			this.payment = copy.payment;
			this.attachments = copy.attachments;
			this.createdOn = copy.createdOn;
			this.lastModifiedOn = copy.lastModifiedOn;
			this.history = copy.history;
			this.notes = copy.notes;
			this.customFields = copy.customFields;
			this.parts = copy.parts;
			this.pendingOffers = copy.pendingOffers;
			this.declinedResources = copy.declinedResources;
			this.questions = copy.questions;
		}

		@JsonProperty("id")
		public Builder withId(String id) {
			this.id = id;
			return this;
		}

		@JsonProperty("title")
		public Builder withTitle(String title) {
			this.title = title;
			return this;
		}

		@JsonProperty("description")
		public Builder withDescription(String description) {
			this.description = description;
			return this;
		}

		@JsonProperty("instructions")
		public Builder withInstructions(String instructions) {
			this.instructions = instructions;
			return this;
		}

		@JsonProperty("desired_skills")
		public Builder withDesiredSkills(String desiredSkills) {
			this.desiredSkills = desiredSkills;
			return this;
		}

		@JsonProperty("short_url")
		public Builder withShortUrl(String shortUrl) {
			this.shortUrl = shortUrl;
			return this;
		}

		@JsonProperty("status")
		public Builder withStatus(String status) {
			this.status = status;
			return this;
		}

		@JsonProperty("substatuses")
		public Builder withSubstatuses(List<ApiAssignmentDetailsLabelDTO> substatuses) {
			this.substatuses = substatuses;
			return this;
		}

		@JsonProperty("labels")
		public Builder withLabels(List<ApiAssignmentDetailsLabelDTO> labels) {
			this.labels = labels;
			return this;
		}

		@JsonProperty("project")
		public Builder withProject(Long project) {
			this.project = project;
			return this;
		}

		@JsonProperty("client")
		public Builder withClient(Long client) {
			this.client = client;
			return this;
		}

		@JsonProperty("internal_owner")
		public Builder withInternalOwner(String internalOwner) {
			this.internalOwner = internalOwner;
			return this;
		}

		@JsonProperty("internal_owner_details")
		public Builder withInternalOwnerDetails(ApiInternalOwnerDetailsDTO internalOwnerDetails) {
			this.internalOwnerDetails = internalOwnerDetails;
			return this;
		}

		@JsonProperty("sent_worker_count")
		public Builder withSentWorkerCount(Integer sentWorkerCount) {
			this.sentWorkerCount = sentWorkerCount;
			return this;
		}

		@JsonProperty("assignment_window_start")
		public Builder withAssignmentWindowStart(Long assignmentWindowStart) {
			this.assignmentWindowStart = assignmentWindowStart;
			return this;
		}

		@JsonProperty("assignment_window_start_date")
		public Builder withAssignmentWindowStartDate(String assignmentWindowStartDate) {
			this.assignmentWindowStartDate = assignmentWindowStartDate;
			return this;
		}

		@JsonProperty("assignment_window_end")
		public Builder withAssignmentWindowEnd(Long assignmentWindowEnd) {
			this.assignmentWindowEnd = assignmentWindowEnd;
			return this;
		}

		@JsonProperty("assignment_window_end_date")
		public Builder withAssignmentWindowEndDate(String assignmentWindowEndDate) {
			this.assignmentWindowEndDate = assignmentWindowEndDate;
			return this;
		}

		@JsonProperty("scheduled_time")
		public Builder withScheduledTime(Long scheduledTime) {
			this.scheduledTime = scheduledTime;
			return this;
		}

		@JsonProperty("scheduled_time_date")
		public Builder withScheduledTimeDate(String scheduledTimeDate) {
			this.scheduledTimeDate = scheduledTimeDate;
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

		@JsonProperty("reschedule_request")
		public Builder withRescheduleRequest(ApiRescheduleRequestDTO rescheduleRequest) {
			this.rescheduleRequest = rescheduleRequest;
			return this;
		}

		@JsonProperty("budget_increase_request")
		public Builder withBudgetIncreaseRequest(ApiBudgetIncreaseRequestDTO budgetIncreaseRequest) {
			this.budgetIncreaseRequest = budgetIncreaseRequest;
			return this;
		}

		@JsonProperty("expense_reimbursement_request")
		public Builder withExpenseReimbursementRequest(ApiExpenseReimbursementRequestDTO expenseReimbursementRequest) {
			this.expenseReimbursementRequest = expenseReimbursementRequest;
			return this;
		}

		@JsonProperty("bonus_request")
		public Builder withBonusRequest(ApiBonusRequestDTO bonusRequest) {
			this.bonusRequest = bonusRequest;
			return this;
		}

		@JsonProperty("resolution")
		public Builder withResolution(String resolution) {
			this.resolution = resolution;
			return this;
		}

		@JsonProperty("industry")
		public Builder withIndustry(String industry) {
			this.industry = industry;
			return this;
		}

		@JsonProperty("time_zone")
		public Builder withTimeZone(String timeZone) {
			this.timeZone = timeZone;
			return this;
		}

		@JsonProperty("required_attachments")
		public Builder withRequiredAttachments(Integer requiredAttachments) {
			this.requiredAttachments = requiredAttachments;
			return this;
		}

		@JsonProperty("location")
		public Builder withLocation(ApiLocationDTO location) {
			this.location = location;
			return this;
		}

		@JsonProperty("location_offsite")
		public Builder withLocationOffsite(Boolean locationOffsite) {
			this.locationOffsite = locationOffsite;
			return this;
		}

		@JsonProperty("location_contact")
		public Builder withLocationContact(ApiLocationContactDTO locationContact) {
			this.locationContact = locationContact;
			return this;
		}

		@JsonProperty("support_contact")
		public Builder withSupportContact(ApiSupportContactDTO supportContact) {
			this.supportContact = supportContact;
			return this;
		}

		@JsonProperty("active_resource")
		public Builder withActiveResource(ApiActiveResourceDTO activeResource) {
			this.activeResource = activeResource;
			return this;
		}

		@JsonProperty("pricing")
		public Builder withPricing(ApiPricingDTO pricing) {
			this.pricing = pricing;
			return this;
		}

		@JsonProperty("invoice_number")
		public Builder withInvoiceNumber(String invoiceNumber) {
			this.invoiceNumber = invoiceNumber;
			return this;
		}

		@JsonProperty("payment")
		public Builder withPayment(ApiPaymentDTO payment) {
			this.payment = payment;
			return this;
		}

		@JsonProperty("attachments")
		public Builder withAttachments(List<ApiAssignmentDetailsAttachmentDTO> attachments) {
			this.attachments = attachments;
			return this;
		}

		@JsonProperty("created_on")
		public Builder withCreatedOn(Long createdOn) {
			this.createdOn = createdOn;
			return this;
		}

		@JsonProperty("last_modified_on")
		public Builder withLastModifiedOn(Long lastModifiedOn) {
			this.lastModifiedOn = lastModifiedOn;
			return this;
		}

		@JsonProperty("history")
		public Builder withHistory(List<ApiHistoryDTO> history) {
			this.history = history;
			return this;
		}

		@JsonProperty("notes")
		public Builder withNotes(List<ApiNoteDTO> notes) {
			this.notes = notes;
			return this;
		}

		@JsonProperty("custom_fields")
		public Builder withCustomFields(List<ApiCustomFieldGroupDTO> customFields) {
			this.customFields = customFields;
			return this;
		}

		@JsonProperty("parts")
		public Builder withParts(ApiPartsDTO parts) {
			this.parts = parts;
			return this;
		}

		@JsonProperty("pending_offers")
		public Builder withPendingOffers(List<ApiApplicationDTO> pendingOffers) {
			this.pendingOffers = pendingOffers;
			return this;
		}

		@JsonProperty("declined_resources")
		public Builder withDeclinedResources(List<ApiDeclinedResourceDTO> declinedResources) {
			this.declinedResources = declinedResources;
			return this;
		}

		@JsonProperty("questions")
		public Builder withQuestions(List<ApiQuestionAnswerPairDTO> questions) {
			this.questions = questions;
			return this;
		}

		public ApiAssignmentDetailsDTO build() {
			return new ApiAssignmentDetailsDTO(this);
		}
	}
}
