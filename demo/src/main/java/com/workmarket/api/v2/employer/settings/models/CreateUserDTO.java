package com.workmarket.api.v2.employer.settings.models;

import com.fasterxml.jackson.annotation.JsonFilter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.workmarket.api.ApiBaseHttpMessageConverter;
import io.swagger.annotations.ApiModel;

@ApiModel("Create User")
@JsonFilter(ApiBaseHttpMessageConverter.FILTER_API_PROJECTIONS)
@JsonDeserialize(builder = CreateUserDTO.Builder.class)
public class CreateUserDTO extends UserDTO {

	final private OnboardingNotificationStrategy onboardingNotificationStrategy;

	private CreateUserDTO(Builder builder) {
		super(builder);
		onboardingNotificationStrategy = builder.onboardingNotificationStrategy;
	}

	@ApiModel(description = "DEFAULT (or blank) will trigger an email to the new employee.  SUPPRESS will suppress email notifications to the new employee.")
	public enum OnboardingNotificationStrategy {
		DEFAULT, SUPPRESS
	}
	
	@JsonIgnore
	public CreateUserDTO.OnboardingNotificationStrategy getOnboardingNotificationStrategy() {
		return onboardingNotificationStrategy;
	}

	public static final class Builder extends UserDTO.Builder {
		private OnboardingNotificationStrategy onboardingNotificationStrategy;

		public Builder() {
		}

		public Builder(CreateUserDTO copy) {
			this.onboardingNotificationStrategy = copy.onboardingNotificationStrategy;
		}

		public Builder(UserDTO userDTO) {
			super(userDTO);
		}

		@JsonProperty("onboardingNotificationStrategy")
		public Builder withOnboardingNotificationStrategy(OnboardingNotificationStrategy onboardingNotificationStrategy) {
			this.onboardingNotificationStrategy = onboardingNotificationStrategy;
			return this;
		}

		public CreateUserDTO build() {
			return new CreateUserDTO(this);
		}
	}
}
