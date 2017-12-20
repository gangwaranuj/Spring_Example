package com.workmarket.api.v2.employer.assignments.models;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel(value = "Schedule")
@JsonDeserialize(builder = ScheduleDTO.Builder.class)
public class ScheduleDTO {
	private final boolean range;
	private final String from;
	private final String through;
	private final boolean confirmationRequired;
	private final double confirmationLeadTime;
	private final boolean checkinRequired;
	private final boolean checkinCallRequired;
	private final String checkinContactName;
	private final String checkinContactPhone;
	private final boolean checkoutNoteDisplayed;
	private final String checkoutNote;

	private ScheduleDTO(Builder builder) {
		this.range = builder.range;
		this.from = builder.from;
		this.through = builder.through;
		this.confirmationRequired = builder.confirmationRequired;
		this.confirmationLeadTime = builder.confirmationLeadTime;
		this.checkinRequired = builder.checkinRequired;
		this.checkinCallRequired = builder.checkinCallRequired;
		this.checkinContactName = builder.checkinContactName;
		this.checkinContactPhone = builder.checkinContactPhone;
		this.checkoutNoteDisplayed = builder.checkoutNoteDisplayed;
		this.checkoutNote = builder.checkoutNote;
	}

	@ApiModelProperty(name = "range")
	@JsonProperty("range")
	public boolean isRange() {
		return range;
	}

	@ApiModelProperty(name = "from")
	@JsonProperty("from")
	public String getFrom() {
		return from;
	}

	@ApiModelProperty(name = "through")
	@JsonProperty("through")
	public String getThrough() {
		return through;
	}

	@ApiModelProperty(name = "confirmationRequired")
	@JsonProperty("confirmationRequired")
	public boolean isConfirmationRequired() {
		return confirmationRequired;
	}

	@ApiModelProperty(name = "confirmationLeadTime")
	@JsonProperty("confirmationLeadTime")
	public double getConfirmationLeadTime() {
		return confirmationLeadTime;
	}

	@ApiModelProperty(name = "checkinRequired")
	@JsonProperty("checkinRequired")
	public boolean isCheckinRequired() {
		return checkinRequired;
	}

	@ApiModelProperty(name = "checkinCallRequired")
	@JsonProperty("checkinCallRequired")
	public boolean isCheckinCallRequired() {
		return checkinCallRequired;
	}

	@ApiModelProperty(name = "checkinContactName")
	@JsonProperty("checkinContactName")
	public String getCheckinContactName() {
		return checkinContactName;
	}

	@ApiModelProperty(name = "checkinContactPhone")
	@JsonProperty("checkinContactPhone")
	public String getCheckinContactPhone() {
		return checkinContactPhone;
	}

	@ApiModelProperty(name = "checkoutNoteDisplayed")
	@JsonProperty("checkoutNoteDisplayed")
	public boolean isCheckoutNoteDisplayed() {
		return checkoutNoteDisplayed;
	}

	@ApiModelProperty(name = "checkoutNote")
	@JsonProperty("checkoutNote")
	public String getCheckoutNote() {
		return checkoutNote;
	}

	public static class Builder implements AbstractBuilder<ScheduleDTO> {
		private boolean range = false;
		private String from;
		private String through;
		private boolean confirmationRequired = false;
		private double confirmationLeadTime;
		private boolean checkinRequired = false;
		private boolean checkinCallRequired = false;
		private String checkinContactName;
		private String checkinContactPhone;
		private boolean checkoutNoteDisplayed = false;
		private String checkoutNote;

		public Builder() {}

		public Builder(ScheduleDTO scheduleDTO) {
			this.range = scheduleDTO.range;
			this.from = scheduleDTO.from;
			this.through = scheduleDTO.through;
			this.confirmationRequired = scheduleDTO.confirmationRequired;
			this.confirmationLeadTime = scheduleDTO.confirmationLeadTime;
			this.checkinRequired = scheduleDTO.checkinRequired;
			this.checkinCallRequired = scheduleDTO.checkinCallRequired;
			this.checkinContactName = scheduleDTO.checkinContactName;
			this.checkinContactPhone = scheduleDTO.checkinContactPhone;
			this.checkoutNoteDisplayed = scheduleDTO.checkoutNoteDisplayed;
			this.checkoutNote = scheduleDTO.checkoutNote;
		}

		@JsonProperty("range") public Builder setRange(boolean range) {
			this.range = range;
			return this;
		}

		@JsonProperty("from") public Builder setFrom(String from) {
			this.from = from;
			return this;
		}

		@JsonProperty("through") public Builder setThrough(String through) {
			this.through = through;
			return this;
		}

		@JsonProperty("confirmationRequired") public Builder setConfirmationRequired(boolean confirmationRequired) {
			this.confirmationRequired = confirmationRequired;
			return this;
		}

		@JsonProperty("confirmationLeadTime") public Builder setConfirmationLeadTime(double confirmationLeadTime) {
			this.confirmationLeadTime = confirmationLeadTime;
			return this;
		}

		@JsonProperty("checkinRequired") public Builder setCheckinRequired(boolean checkinRequired) {
			this.checkinRequired = checkinRequired;
			return this;
		}

		@JsonProperty("checkinCallRequired") public Builder setCheckinCallRequired(boolean checkinCallRequired) {
			this.checkinCallRequired = checkinCallRequired;
			return this;
		}

		@JsonProperty("checkinContactName") public Builder setCheckinContactName(String checkinContactName) {
			this.checkinContactName = checkinContactName;
			return this;
		}

		@JsonProperty("checkinContactPhone") public Builder setCheckinContactPhone(String checkinContactPhone) {
			this.checkinContactPhone = checkinContactPhone;
			return this;
		}

		@JsonProperty("checkoutNoteDisplayed") public Builder setCheckoutNoteDisplayed(boolean checkoutNoteDisplayed) {
			this.checkoutNoteDisplayed = checkoutNoteDisplayed;
			return this;
		}

		@JsonProperty("checkoutNote") public Builder setCheckoutNote(String checkoutNote) {
			this.checkoutNote = checkoutNote;
			return this;
		}

		public ScheduleDTO build() {
			return new ScheduleDTO(this);
		}
	}
}
