package com.workmarket.api.v1.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel(value = "CheckInOut")
@JsonDeserialize(builder = ApiCheckInOutDTO.Builder.class)
public class ApiCheckInOutDTO {

	private final Long id;
	private final Long checkedInOn;
	private final Long checkedOutOn;
	private final Long createdOn;
	private final Long modifiedOn;
	private final String note;

	private ApiCheckInOutDTO(Builder builder) {
		id = builder.id;
		checkedInOn = builder.checkedInOn;
		checkedOutOn = builder.checkoutOutOn;
		createdOn = builder.createdOn;
		modifiedOn = builder.modifiedOn;
		note = builder.note;
	}

	@ApiModelProperty(name = "check_in_out_id")
	@JsonProperty("check_in_out_id")
	public Long getId() {
		return id;
	}

	@ApiModelProperty(name = "checked_in_on")
	@JsonProperty("checked_in_on")
	public Long getCheckedInOn() {
		return checkedInOn;
	}

	@ApiModelProperty(name = "checked_out_on")
	@JsonProperty("checked_out_on")
	public Long getCheckedOutOn() {
		return checkedOutOn;
	}

	@ApiModelProperty(name = "created_on")
	@JsonProperty("created_on")
	public Long getCreatedOn() {
		return createdOn;
	}

	@ApiModelProperty(name = "modified_on")
	@JsonProperty("modified_on")
	public Long getModifiedOn() {
		return modifiedOn;
	}

	@ApiModelProperty(name = "note")
	@JsonProperty("note")
	public String getNote() {
		return note;
	}

	public static final class Builder {
		private Long id;
		private Long checkedInOn;
		private Long checkoutOutOn;
		private Long createdOn;
		private Long modifiedOn;
		private String note;

		public Builder() {
		}

		public Builder(ApiCheckInOutDTO copy) {
			this.id = copy.id;
			this.checkedInOn = copy.checkedInOn;
			this.checkoutOutOn = copy.checkedOutOn;
			this.createdOn = copy.createdOn;
			this.modifiedOn = copy.modifiedOn;
			this.note = copy.note;
		}

		@JsonProperty("id")
		public Builder withId(Long id) {
			this.id = id;
			return this;
		}

		@JsonProperty("checked_in_on")
		public Builder withCheckedInOn(Long checkedInOn) {
			this.checkedInOn = checkedInOn;
			return this;
		}

		@JsonProperty("checkout_out_on")
		public Builder withCheckoutOutOn(Long checkoutOutOn) {
			this.checkoutOutOn = checkoutOutOn;
			return this;
		}

		@JsonProperty("created_on")
		public Builder withCreatedOn(Long createdOn) {
			this.createdOn = createdOn;
			return this;
		}

		@JsonProperty("modified_on")
		public Builder withModifiedOn(Long modifiedOn) {
			this.modifiedOn = modifiedOn;
			return this;
		}

		@JsonProperty("note")
		public Builder withNote(String note) {
			this.note = note;
			return this;
		}

		public ApiCheckInOutDTO build() {
			return new ApiCheckInOutDTO(this);
		}
	}
}
