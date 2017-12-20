package com.workmarket.api.v2.worker.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.workmarket.service.business.dto.CreateNewWorkerRequest;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.math.BigDecimal;

@ApiModel("CreateWorkerRequest")
@JsonDeserialize(builder = ApiCreateWorkerRequestDTO.Builder.class)
public class ApiCreateWorkerRequestDTO {
	// required user fields
	private final String firstName;
	private final String lastName;
	private final String email;
	private final String secondaryEmail;
	private final String password;
	private final String locale;

	// optional
	private final String jobTitle;

	private final Long recruitingCampaignId;
	private final String companyName;

	// Address fields
	private final String address1;
	private final String address2;
	private final String country;
	private final String city;
	private final String postalCode;
	private final String state;
	private final BigDecimal longitude;
	private final BigDecimal latitude;

	// industry of worker
	private final String industryId;

	private final OnboardingNotificationStrategy onboardingNotificationStrategy;

	private ApiCreateWorkerRequestDTO(Builder builder) {
		firstName = builder.firstName;
		lastName = builder.lastName;
		email = builder.email;
		secondaryEmail = builder.secondaryEmail;
		password = builder.password;
		locale = builder.locale;
		jobTitle = builder.jobTitle;
		recruitingCampaignId = builder.recruitingCampaignId;
		companyName = builder.companyName;
		address1 = builder.address1;
		address2 = builder.address2;
		country = builder.country;
		city = builder.city;
		postalCode = builder.postalCode;
		state = builder.state;
		longitude = builder.longitude;
		latitude = builder.latitude;
		industryId = builder.industryId;
		onboardingNotificationStrategy = builder.onboardingNotificationStrategy;
	}


	public enum OnboardingNotificationStrategy {
		DEFAULT, SUPPRESS, PASSWORD_RESET
	}
	
	public static final class Builder {
		private String firstName;
		private String lastName;
		private String email;
		private String secondaryEmail;
		private String password;
		private String locale;
		private String jobTitle;
		private Long recruitingCampaignId;
		private String companyName;
		private String address1;
		private String address2;
		private String country;
		private String city;
		private String postalCode;
		private String state;
		private BigDecimal longitude;
		private BigDecimal latitude;
		private String industryId;
		private OnboardingNotificationStrategy onboardingNotificationStrategy = OnboardingNotificationStrategy.DEFAULT;

		public Builder() {
		}

		public Builder(ApiCreateWorkerRequestDTO copy) {
			this.firstName = copy.firstName;
			this.lastName = copy.lastName;
			this.email = copy.email;
			this.secondaryEmail = copy.secondaryEmail;
			this.password = copy.password;
			this.locale = copy.locale;
			this.jobTitle = copy.jobTitle;
			this.recruitingCampaignId = copy.recruitingCampaignId;
			this.companyName = copy.companyName;
			this.address1 = copy.address1;
			this.address2 = copy.address2;
			this.country = copy.country;
			this.city = copy.city;
			this.postalCode = copy.postalCode;
			this.state = copy.state;
			this.longitude = copy.longitude;
			this.latitude = copy.latitude;
			this.industryId = copy.industryId;
			this.onboardingNotificationStrategy = copy.onboardingNotificationStrategy;
		}

		@JsonProperty("firstName")
		public Builder withFirstName(String firstName) {
			this.firstName = firstName;
			return this;
		}

		@JsonProperty("lastName")
		public Builder withLastName(String lastName) {
			this.lastName = lastName;
			return this;
		}

		@JsonProperty("email")
		public Builder withEmail(String email) {
			this.email = email;
			return this;
		}

		@JsonProperty("secondaryEmail")
		public Builder withSecondaryEmail(String secondaryEmail) {
			this.secondaryEmail = secondaryEmail;
			return this;
		}

		@JsonProperty("password")
		public Builder withPassword(String password) {
			this.password = password;
			return this;
		}

		@JsonProperty("locale")
		public Builder withLocale(String locale) {
			this.locale = locale;
			return this;
		}

		@JsonProperty("jobTitle")
		public Builder withJobTitle(String jobTitle) {
			this.jobTitle = jobTitle;
			return this;
		}

		@JsonProperty("recruitingCampaignId")
		public Builder withRecruitingCampaignId(Long recruitingCampaignId) {
			this.recruitingCampaignId = recruitingCampaignId;
			return this;
		}

		@JsonProperty("companyName")
		public Builder withCompanyName(String companyName) {
			this.companyName = companyName;
			return this;
		}

		@JsonProperty("address1")
		public Builder withAddress1(String address1) {
			this.address1 = address1;
			return this;
		}

		@JsonProperty("address2")
		public Builder withAddress2(String address2) {
			this.address2 = address2;
			return this;
		}

		@JsonProperty("country")
		public Builder withCountry(String country) {
			this.country = country;
			return this;
		}

		@JsonProperty("city")
		public Builder withCity(String city) {
			this.city = city;
			return this;
		}

		@JsonProperty("postalCode")
		public Builder withPostalCode(String postalCode) {
			this.postalCode = postalCode;
			return this;
		}

		@JsonProperty("state")
		public Builder withState(String state) {
			this.state = state;
			return this;
		}

		@JsonProperty("longitude")
		public Builder withLongitude(BigDecimal longitude) {
			this.longitude = longitude;
			return this;
		}

		@JsonProperty("latitude")
		public Builder withLatitude(BigDecimal latitude) {
			this.latitude = latitude;
			return this;
		}

		@JsonProperty("industryId")
		public Builder withIndustryId(String industryId) {
			this.industryId = industryId;
			return this;
		}

		@JsonProperty("onboardingNotificationStrategy")
		public Builder withOnboardingNotificationStrategy(OnboardingNotificationStrategy onboardingNotificationStrategy) {
			this.onboardingNotificationStrategy = onboardingNotificationStrategy;
			return this;
		}

		public ApiCreateWorkerRequestDTO build() {
			return new ApiCreateWorkerRequestDTO(this);
		}
	}

	@ApiModelProperty(name = "firstName")
	@JsonProperty("firstName")
	public String getFirstName() {
		return firstName;
	}

	@ApiModelProperty(name = "lastName")
	@JsonProperty("lastName")
	public String getLastName() {
		return lastName;
	}

	@ApiModelProperty(name = "email")
	@JsonProperty("email")
	public String getEmail() {
		return email;
	}

	@ApiModelProperty(name = "secondaryEmail")
	@JsonProperty("secondaryEmail")
	public String getSecondaryEmail() {
		return secondaryEmail;
	}

	@ApiModelProperty(name = "password")
	@JsonProperty("password")
	public String getPassword() {
		return password;
	}

	@ApiModelProperty(name = "locale")
	@JsonProperty("locale")
	public String getLocale() {
		return locale;
	}

	@ApiModelProperty(name = "jobTitle")
	@JsonProperty("jobTitle")
	public String getJobTitle() {
		return jobTitle;
	}

	@ApiModelProperty(name = "recruitingCampaignId")
	@JsonProperty("recruitingCampaignId")
	public Long getRecruitingCampaignId() {
		return recruitingCampaignId;
	}

	@ApiModelProperty(name = "companyName")
	@JsonProperty("companyName")
	public String getCompanyName() {
		return companyName;
	}

	@ApiModelProperty(name = "address1")
	@JsonProperty("address1")
	public String getAddress1() {
		return address1;
	}

	@ApiModelProperty(name = "address2")
	@JsonProperty("address2")
	public String getAddress2() {
		return address2;
	}

	@ApiModelProperty(name = "country")
	@JsonProperty("country")
	public String getCountry() {
		return country;
	}

	@ApiModelProperty(name = "city")
	@JsonProperty("city")
	public String getCity() {
		return city;
	}

	@ApiModelProperty(name = "postalCode")
	@JsonProperty("postalCode")
	public String getPostalCode() {
		return postalCode;
	}

	@ApiModelProperty(name = "state")
	@JsonProperty("state")
	public String getState() {
		return state;
	}

	@ApiModelProperty(name = "longitude")
	@JsonProperty("longitude")
	public BigDecimal getLongitude() {
		return longitude;
	}

	@ApiModelProperty(name = "latitude")
	@JsonProperty("latitude")
	public BigDecimal getLatitude() {
		return latitude;
	}

	@ApiModelProperty(name = "industryId")
	@JsonProperty("industryId")
	public String getIndustryId() {
		return industryId;
	}

	@ApiModelProperty(name = "onboardingNotificationStrategy")
	@JsonProperty("onboardingNotificationStrategy")
	public ApiCreateWorkerRequestDTO.OnboardingNotificationStrategy getOnboardingNotificationStrategy() {
		return onboardingNotificationStrategy;
	}

	public CreateNewWorkerRequest asCreateNewWorkerRequest() {
		return CreateNewWorkerRequest.builder()
			.setFirstName(this.firstName)
			.setLastName(this.lastName)
			.setEmail(this.email)
			.setSecondaryEmail(this.secondaryEmail)
			.setPassword(this.password)
			.setLocale(this.locale)
			.setJobTitle(this.jobTitle)
			.setRecruitingCampaignId(this.recruitingCampaignId)
			.setCompanyName(this.companyName)
			.setAddress1(this.address1)
			.setAddress2(this.address2)
			.setCountry(this.country)
			.setCity(this.city)
			.setPostalCode(this.postalCode)
			.setState(this.state)
			.setLongitude(this.longitude)
			.setLatitude(this.latitude)
			.setIndustryId(this.industryId)
			.build();
	}
}
