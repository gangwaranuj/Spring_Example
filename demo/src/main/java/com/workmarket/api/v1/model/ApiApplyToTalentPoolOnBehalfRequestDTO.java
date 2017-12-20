package com.workmarket.api.v1.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.util.List;

@ApiModel(value = "ApplyToTalentPoolOnBehalfRequest")
@JsonDeserialize(builder = ApiApplyToTalentPoolOnBehalfRequestDTO.Builder.class)
public class ApiApplyToTalentPoolOnBehalfRequestDTO {

	private final List<String> userIds;
	private final boolean override;
	private final boolean suppressNotification;

	public ApiApplyToTalentPoolOnBehalfRequestDTO(List<String> userIds, boolean override, boolean suppressNotification) {
		this.userIds = userIds;
		this.override = override;
		this.suppressNotification = suppressNotification;
	}

	private ApiApplyToTalentPoolOnBehalfRequestDTO(Builder builder) {
		userIds = builder.userIds;
		override = builder.override;
		suppressNotification = builder.suppressNotification;
	}

	public static final class Builder {
		private List<String> userIds;
		private boolean override;
		private boolean suppressNotification;

		public Builder() {
		}

		@ApiModelProperty(name = "userIds")
		@JsonProperty("userIds")
		public Builder withUserIds(List<String> userIds) {
			this.userIds = userIds;
			return this;
		}

		@ApiModelProperty(name = "override")
		@JsonProperty("override")
		public Builder withOverride(boolean override) {
			this.override = override;
			return this;
		}

		@ApiModelProperty(name = "suppressNotification")
		@JsonProperty("suppressNotification")
		public Builder withSuppressNotification(boolean suppressNotification) {
			this.suppressNotification = suppressNotification;
			return this;
		}

		public ApiApplyToTalentPoolOnBehalfRequestDTO build() {
			return new ApiApplyToTalentPoolOnBehalfRequestDTO(this);
		}
	}

	@JsonProperty("userIds")
	public List<String> getUserIds() {
		return userIds;
	}

	@JsonProperty("override")
	public boolean isOverride() {
		return override;
	}

	@JsonProperty("suppressNotification")
	public boolean isSuppressNotification() {
		return suppressNotification;
	}
}
