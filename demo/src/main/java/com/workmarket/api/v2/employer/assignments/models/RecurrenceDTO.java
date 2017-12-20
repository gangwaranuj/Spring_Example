package com.workmarket.api.v2.employer.assignments.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;

import java.util.List;

public class RecurrenceDTO {

	private final String uuid;
	private final String type;
	private final int frequencyModifier;
	private final List<Boolean> weekdays;
	private final int repetitions;
	private final String endDate;
	private final String description;
	private final Long recurringAssignmentId;
	private final Long labelId;

	public RecurrenceDTO(Builder builder) {
		this.uuid = builder.uuid;
		this.type = builder.type;
		this.frequencyModifier = builder.frequencyModifier;
		this.weekdays = builder.weekdays;
		this.repetitions = builder.repetitions;
		this.endDate = builder.endDate;
		this.description = builder.description;
		this.recurringAssignmentId = builder.recurringAssignmentId;
		this.labelId = builder.labelId;
	}

	@ApiModelProperty(name = "uuid")
	@JsonProperty("uuid")
	public String getUuid() {
		return uuid;
	}

	@ApiModelProperty(name = "endDate")
	@JsonProperty("endDate")
	public String getEndDate() {
		return endDate;
	}

	@ApiModelProperty(name = "type")
	@JsonProperty("type")
	public String getType() {
		return type;
	}

	@ApiModelProperty(name = "repetitions")
	@JsonProperty("repetitions")
	public int getRepetitions() {
		return repetitions;
	}

	@ApiModelProperty(name = "weekdays")
	@JsonProperty("weekdays")
	public List<Boolean> getWeekdays() {
		return weekdays;
	}

	@ApiModelProperty(name = "frequencyModifier")
	@JsonProperty("frequencyModifier")
	public int getFrequencyModifier() {
		return frequencyModifier;
	}

	@ApiModelProperty(name = "recurringAssignmentId")
	@JsonProperty("recurringAssignmentId")
	public Long getRecurringAssignmentId() {
		return recurringAssignmentId;
	}

	@ApiModelProperty(name = "description")
	@JsonProperty("description")
	public String getDescription() {
		return description;
	}

	@ApiModelProperty(name = "labelId")
	@JsonProperty("labelId")
	public Long getLabelId() {
		return labelId;
	}

	public static class Builder {

		private String uuid;
		private String type;
		private int repetitions;
		private List<Boolean> weekdays;
		private int frequencyModifier;
		private String endDate;
		private String description;
		private Long recurringAssignmentId;
		private Long labelId;

		public Builder() {

		}

		public Builder(RecurrenceDTO dto) {
			this.uuid = dto.uuid;
			this.type = dto.type;
			this.repetitions = dto.repetitions;
			this.weekdays = dto.weekdays;
			this.frequencyModifier = dto.frequencyModifier;
			this.endDate = dto.endDate;
			this.description = dto.description;
			this.recurringAssignmentId = dto.recurringAssignmentId;
			this.labelId = dto.labelId;
		}

		@JsonProperty("endDate")
		public Builder setEndDate(String endDate) {
			this.endDate = endDate;
			return this;
		}

		@JsonProperty("type")
		public Builder setType(String type) {
			this.type = type;
			return this;
		}

		@JsonProperty("repetitions")
		public Builder setRepetitions(int repetitions) {
			this.repetitions = repetitions;
			return this;
		}

		@JsonProperty("weekdays")
		public Builder setWeekdays(List<Boolean> weekdays) {
			this.weekdays = weekdays;
			return this;
		}

		@JsonProperty("frequencyModifier")
		public Builder setFrequencyModifier(int frequencyModifier) {
			this.frequencyModifier = frequencyModifier;
			return this;
		}

		@JsonProperty("recurringAssignmentId")
		public Builder setRecurringAssignmentId(Long recurringAssignmentId) {
			this.recurringAssignmentId = recurringAssignmentId;
			return this;
		}

		@JsonProperty("uuid")
		public Builder setUuid(String uuid) {
			this.uuid = uuid;
			return this;
		}

		@JsonProperty("description")
		public Builder setDescription(String description) {
			this.description = description;
			return this;
		}

		@JsonProperty("labelId")
		public Builder setLabelId(Long labelId) {
			this.labelId = labelId;
			return this;
		}

		public RecurrenceDTO build() {
			return new RecurrenceDTO(this);
		}
	}

}
