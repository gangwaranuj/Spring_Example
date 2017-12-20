package com.workmarket.api.v2.employer.assignments.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.google.api.client.util.Lists;
import com.google.common.collect.Sets;
import com.workmarket.api.v2.model.CustomFieldGroupDTO;
import com.workmarket.api.v2.model.LocationDTO;
import com.workmarket.api.v2.model.SurveyDTO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.util.List;
import java.util.Set;

@ApiModel(value = "Assignment")
@JsonDeserialize(builder = AssignmentDTO.Builder.class)
public class AssignmentDTO {
	private final String id;
	private final String title;
	private final String description;
	private final String instructions;
	private final boolean instructionsPrivate;
	private final String skills;
	private final Long industryId;
	private final Long projectId;
	private final String ownerId;
	private final String supportContactId;
	private final String uniqueExternalId;
	private final List<Long> requirementSetIds;
	private final List<String> followerIds;
	private final LocationDTO location;
	private final ScheduleDTO schedule;
	private final PricingDTO pricing;
	private final RoutingDTO routing;
	//changed to LinkedHashSet to avoid random ordering of the CustomFieldGroupDTO objects
	private final Set<CustomFieldGroupDTO> customFieldGroups = Sets.newLinkedHashSet();
	private final ShipmentGroupDTO shipmentGroup;
	private final Set<SurveyDTO> surveys = Sets.newHashSet();
	private final Set<DocumentDTO> documents = Sets.newHashSet();
	private final DeliverablesGroupDTO deliverablesGroup;
	private final ConfigurationDTO configuration;
	private final RecurrenceDTO recurrence;

	private AssignmentDTO(Builder builder) {
		this.id = builder.id;
		this.title = builder.title;
		this.description = builder.description;
		this.instructions = builder.instructions;
		this.instructionsPrivate = builder.instructionsPrivate;
		this.skills = builder.skills;
		this.industryId = builder.industryId;
		this.projectId = builder.projectId;
		this.ownerId = builder.ownerId;
		this.supportContactId = builder.supportContactId;
		this.uniqueExternalId = builder.uniqueExternalId;
		this.requirementSetIds = builder.requirementSetIds;
		this.followerIds = builder.followerIds;
		this.location = builder.location.build();
		this.schedule = builder.schedule.build();
		this.pricing = builder.pricing.build();
		this.routing = builder.routing.build();
		this.shipmentGroup = builder.shipmentGroup.build();

		for (CustomFieldGroupDTO.Builder customFieldGroup : builder.customFieldGroups) {
			this.customFieldGroups.add(customFieldGroup.build());
		}

		for (SurveyDTO.Builder survey : builder.surveys) {
			this.surveys.add(survey.build());
		}

		for (DocumentDTO.Builder document : builder.documents) {
			this.documents.add(document.build());
		}

		this.deliverablesGroup = builder.deliverablesGroup.build();
		this.configuration = builder.configuration.build();
		this.recurrence = builder.recurrence.build();
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

	@ApiModelProperty(name = "instructionsPrivate")
	@JsonProperty("instructionsPrivate")
	public boolean isInstructionsPrivate() {
		return instructionsPrivate;
	}

	@ApiModelProperty(name = "skills")
	@JsonProperty("skills")
	public String getSkills() {
		return skills;
	}

	@ApiModelProperty(name = "industryId")
	@JsonProperty("industryId")
	public Long getIndustryId() {
		return industryId;
	}

	@ApiModelProperty(name = "projectId")
	@JsonProperty("projectId")
	public Long getProjectId() {
		return projectId;
	}

	@ApiModelProperty(name = "ownerId")
	@JsonProperty("ownerId")
	public String getOwnerId() {
		return ownerId;
	}

	@ApiModelProperty(name = "supportContactId")
	@JsonProperty("supportContactId")
	public String getSupportContactId() {
		return supportContactId;
	}

	@ApiModelProperty(name = "uniqueExternalId")
	@JsonProperty("uniqueExternalId")
	public String getUniqueExternalId() {
		return uniqueExternalId;
	}

	@ApiModelProperty(name = "requirementSetIds")
	@JsonProperty("requirementSetIds")
	public List<Long> getRequirementSetIds() {
		return requirementSetIds;
	}

	@ApiModelProperty(name = "followerIds")
	@JsonProperty("followerIds")
	public List<String> getFollowerIds() {
		return followerIds;
	}

	@ApiModelProperty(name = "location")
	@JsonProperty("location")
	public LocationDTO getLocation() {
		return location;
	}

	@ApiModelProperty(name = "schedule")
	@JsonProperty("schedule")
	public ScheduleDTO getSchedule() {
		return schedule;
	}

	@ApiModelProperty(name = "pricing")
	@JsonProperty("pricing")
	public PricingDTO getPricing() {
		return pricing;
	}

	@ApiModelProperty(name = "routing")
	@JsonProperty("routing")
	public RoutingDTO getRouting() {
		return routing;
	}

	@ApiModelProperty(name = "customFieldGroups")
	@JsonProperty("customFieldGroups")
	public Set<CustomFieldGroupDTO> getCustomFieldGroups() {
		return customFieldGroups;
	}

	@ApiModelProperty(name = "shipmentGroup")
	@JsonProperty("shipmentGroup")
	public ShipmentGroupDTO getShipmentGroup() {
		return shipmentGroup;
	}

	@ApiModelProperty(name = "surveys")
	@JsonProperty("surveys")
	public Set<SurveyDTO> getSurveys() {
		return surveys;
	}

	@ApiModelProperty(name = "documents")
	@JsonProperty("documents")
	public Set<DocumentDTO> getDocuments() {
		return documents;
	}

	@ApiModelProperty(name = "deliverablesGroup")
	@JsonProperty("deliverablesGroup")
	public DeliverablesGroupDTO getDeliverablesGroup() {
		return deliverablesGroup;
	}

	@ApiModelProperty(name = "configuration")
	@JsonProperty("configuration")
	public ConfigurationDTO getConfiguration() {
		return configuration;
	}

	@ApiModelProperty(name = "recurrence")
	@JsonProperty("recurrence")
	public RecurrenceDTO getRecurrence() {
		return recurrence;
	}

	public static class Builder implements AbstractBuilder<AssignmentDTO> {
		private String id;
		private String title;
		private String description;
		private String instructions;
		private boolean instructionsPrivate = false;
		private String skills;
		private Long industryId;
		private Long projectId;
		private String ownerId;
		private String supportContactId;
		private String uniqueExternalId;
		private List<Long> requirementSetIds = Lists.newArrayList();
		private List<String> followerIds = Lists.newArrayList();
		private LocationDTO.Builder location = new LocationDTO.Builder();
		private ScheduleDTO.Builder schedule = new ScheduleDTO.Builder();
		private PricingDTO.Builder pricing = new PricingDTO.Builder();
		private RoutingDTO.Builder routing = new RoutingDTO.Builder();
		private Set<CustomFieldGroupDTO.Builder> customFieldGroups = Sets.newLinkedHashSet();
		private Set<SurveyDTO.Builder> surveys = Sets.newHashSet();
		private Set<DocumentDTO.Builder> documents = Sets.newHashSet();
		private ShipmentGroupDTO.Builder shipmentGroup = new ShipmentGroupDTO.Builder();
		private DeliverablesGroupDTO.Builder deliverablesGroup = new DeliverablesGroupDTO.Builder();
		private ConfigurationDTO.Builder configuration = new ConfigurationDTO.Builder();
		private RecurrenceDTO.Builder recurrence = new RecurrenceDTO.Builder();

		public Builder() {}

		public Builder(AssignmentDTO assignmentDTO) {
			this.id = assignmentDTO.id;
			this.title = assignmentDTO.title;
			this.description = assignmentDTO.description;
			this.instructions = assignmentDTO.instructions;
			this.instructionsPrivate = assignmentDTO.instructionsPrivate;
			this.skills = assignmentDTO.skills;
			this.industryId = assignmentDTO.industryId;
			this.projectId = assignmentDTO.projectId;
			this.ownerId = assignmentDTO.ownerId;
			this.supportContactId = assignmentDTO.supportContactId;
			this.uniqueExternalId = assignmentDTO.uniqueExternalId;
			this.requirementSetIds = assignmentDTO.requirementSetIds;
			this.followerIds = assignmentDTO.followerIds;
			this.shipmentGroup = new ShipmentGroupDTO.Builder(assignmentDTO.shipmentGroup);
			this.location = new LocationDTO.Builder(assignmentDTO.location);
			this.schedule = new ScheduleDTO.Builder(assignmentDTO.schedule);
			this.pricing = new PricingDTO.Builder(assignmentDTO.pricing);
			this.routing = new RoutingDTO.Builder(assignmentDTO.routing);

			for (CustomFieldGroupDTO customFieldGroupDTO : assignmentDTO.customFieldGroups) {
				this.customFieldGroups.add(new CustomFieldGroupDTO.Builder(customFieldGroupDTO));
			}

			for (SurveyDTO surveyDTO : assignmentDTO.surveys) {
				this.surveys.add(new SurveyDTO.Builder(surveyDTO));
			}

			for (DocumentDTO documentDTO : assignmentDTO.documents) {
				this.documents.add(new DocumentDTO.Builder(documentDTO));
			}

			this.deliverablesGroup = new DeliverablesGroupDTO.Builder(assignmentDTO.deliverablesGroup);
			this.configuration = new ConfigurationDTO.Builder(assignmentDTO.configuration);
			this.recurrence = new RecurrenceDTO.Builder(assignmentDTO.getRecurrence());
		}

		@JsonProperty("id") public Builder setId(String id) {
			this.id = id;
			return this;
		}

		@JsonProperty("title") public Builder setTitle(String title) {
			this.title = title;
			return this;
		}

		@JsonProperty("description") public Builder setDescription(String description) {
			this.description = description;
			return this;
		}

		@JsonProperty("instructions") public Builder setInstructions(String instructions) {
			this.instructions = instructions;
			return this;
		}

		@JsonProperty("instructionsPrivate") public Builder setInstructionsPrivate(boolean instructionsPrivate) {
			this.instructionsPrivate = instructionsPrivate;
			return this;
		}

		@JsonProperty("skills") public Builder setSkills(String skills) {
			this.skills = skills;
			return this;
		}

		@JsonProperty("industryId") public Builder setIndustryId(Long industryId) {
			this.industryId = industryId;
			return this;
		}

		@JsonProperty("projectId") public Builder setProjectId(Long projectId) {
			this.projectId = projectId;
			return this;
		}

		@JsonProperty("ownerId") public Builder setOwnerId(String ownerId) {
			this.ownerId = ownerId;
			return this;
		}

		@JsonProperty("supportContactId") public Builder setSupportContactId(String supportContactId) {
			this.supportContactId = supportContactId;
			return this;
		}

		@JsonProperty("uniqueExternalId") public Builder setUniqueExternalId(String uniqueExternalId) {
			this.uniqueExternalId = uniqueExternalId;
			return this;
		}

		@JsonProperty("requirementSetIds") public Builder setRequirementSetIds(List<Long> requirementSetIds) {
			this.requirementSetIds = requirementSetIds;
			return this;
		}

		public Builder addRequirementSetId(Long requirementSetId) {
			this.requirementSetIds.add(requirementSetId);
			return this;
		}

		@JsonProperty("followerIds") public Builder setFollowerIds(List<String> followerIds) {
			this.followerIds = followerIds;
			return this;
		}

		public Builder addFollowerId(String followerId) {
			this.followerIds.add(followerId);
			return this;
		}

		@JsonProperty("location") public Builder setLocation(LocationDTO.Builder location) {
			this.location = location;
			return this;
		}

		@JsonProperty("schedule") public Builder setSchedule(ScheduleDTO.Builder schedule) {
			this.schedule = schedule;
			return this;
		}

		@JsonProperty("pricing") public Builder setPricing(PricingDTO.Builder pricing) {
			this.pricing = pricing;
			return this;
		}

		@JsonProperty("routing") public Builder setRouting(RoutingDTO.Builder routing) {
			this.routing = routing;
			return this;
		}

		@JsonProperty("customFieldGroups") public Builder setCustomFieldGroups(Set<CustomFieldGroupDTO.Builder> customFieldGroups) {
			this.customFieldGroups = customFieldGroups;
			return this;
		}

		public Builder addCustomFieldGroup(CustomFieldGroupDTO.Builder customFieldGroup) {
			this.customFieldGroups.add(customFieldGroup);
			return this;
		}

		@JsonProperty("shipmentGroup") public Builder setShipmentGroup(ShipmentGroupDTO.Builder shipmentGroup) {
			this.shipmentGroup = shipmentGroup;
			return this;
		}

		@JsonProperty("surveys") public Builder setSurveys(Set<SurveyDTO.Builder> surveys) {
			this.surveys = surveys;
			return this;
		}

		public Builder addSurvey(SurveyDTO.Builder survey) {
			this.surveys.add(survey);
			return this;
		}

		@JsonProperty("documents") public Builder setDocuments(Set<DocumentDTO.Builder> documents) {
			this.documents = documents;
			return this;
		}

		public Builder addDocument(DocumentDTO.Builder document) {
			this.documents.add(document);
			return this;
		}

		@JsonProperty("configuration") public Builder setConfiguration(ConfigurationDTO.Builder configuration) {
			this.configuration = configuration;
			return this;
		}

		@JsonProperty("deliverablesGroup") public Builder setDeliverablesGroup(DeliverablesGroupDTO.Builder deliverablesGroup) {
			this.deliverablesGroup = deliverablesGroup;
			return this;
		}

		@JsonProperty("recurrence")
		public Builder setRecurrence(RecurrenceDTO.Builder recurrence) {
			this.recurrence = recurrence;
			return this;
		}

		public AssignmentDTO build() {
			return new AssignmentDTO(this);
		}
	}
}
