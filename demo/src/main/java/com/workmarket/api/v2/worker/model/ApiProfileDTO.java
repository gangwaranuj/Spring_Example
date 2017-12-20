package com.workmarket.api.v2.worker.model;

import ch.lambdaj.function.convert.Converter;
import com.fasterxml.jackson.annotation.JsonFilter;
import com.google.api.client.util.Lists;
import com.workmarket.api.ApiBaseHttpMessageConverter;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.workmarket.api.model.ApiPhoneNumberDTO;
import com.workmarket.api.v2.model.AddressApiDTO;
import com.workmarket.api.v2.model.ApiImageDTO;
import com.workmarket.api.v2.model.ApiJobTitleDTO;
import com.workmarket.api.v2.model.SkillApiDTO;
import com.workmarket.configuration.Constants;
import com.workmarket.domains.onboarding.model.OnboardingSkillDTO;
import com.workmarket.domains.onboarding.model.PhoneInfoDTO;
import com.workmarket.domains.onboarding.model.WorkerOnboardingDTO;
import com.workmarket.service.business.dto.InvitationUserRegistrationDTO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import static ch.lambdaj.Lambda.convert;

import java.util.List;

/**
 * Created by jlevine on 14/12/2016.
 */
@ApiModel(value = "Profile", description = "A worker profile")
@JsonFilter(ApiBaseHttpMessageConverter.FILTER_API_PROJECTIONS)
@JsonDeserialize(builder = ApiProfileDTO.Builder.class)
public class ApiProfileDTO {
	private final String userNumber;
	private final String firstName;
	private final String lastName;
	private final String email;
	private final String secondaryEmail;
	private final String avatarUri;
	private final ApiImageDTO avatar;
	private final String phoneNumber;
	private final List<ApiPhoneNumberDTO> phoneNumbers;
	private final String jobTitle;
	private final Integer maxTravelDistance;
	private final List<SkillApiDTO> skills;
	private final AddressApiDTO address;

	public ApiProfileDTO(
		final String userNumber,
		final String firstName,
		final String lastName,
		final String email,
		final String avatarUri,
		final ApiImageDTO avatar,
		final String phoneNumber,
		final List<ApiPhoneNumberDTO> phoneNumbers,
		final String jobTitle,
		final Integer maxTravelDistance,
		final List<SkillApiDTO> skills,
		final AddressApiDTO address) {
		this.userNumber = userNumber;
		this.firstName = firstName;
		this.lastName = lastName;
		this.email = email;
		this.avatarUri = avatarUri;
		this.avatar = avatar;
		this.phoneNumber = phoneNumber;
		this.phoneNumbers = phoneNumbers;
		this.jobTitle = jobTitle;
		this.maxTravelDistance = maxTravelDistance;
		this.skills = skills;
		this.address = address;
		this.secondaryEmail = null;
	}

	private ApiProfileDTO(Builder builder) {
		userNumber = builder.userNumber;
		firstName = builder.firstName;
		lastName = builder.lastName;
		email = builder.email;
		avatarUri = builder.avatarUri;
		avatar = builder.avatar;
		phoneNumber = builder.phoneNumber;
		secondaryEmail = builder.secondaryEmail;
		if(builder.phoneNumbers != null) {
			this.phoneNumbers = Lists.newArrayList();
			for (ApiPhoneNumberDTO.Builder phoneNumber : builder.phoneNumbers) {
				this.phoneNumbers.add(phoneNumber.build());
			}
		}
		else {
			phoneNumbers = null;
		}
		jobTitle = builder.jobTitle;
		maxTravelDistance = builder.maxTravelDistance;
		if(builder.skills != null) {
			this.skills = Lists.newArrayList();
			for (SkillApiDTO.Builder skill : builder.skills) {
				this.skills.add(skill.build());
			}
		}
		else {
			skills = null;
		}
		address = builder.address;
	}

	@ApiModelProperty(name = "userNumber", value = "Unique identifier for the user who owns this profile")
	@JsonProperty("userNumber")
	public String getUserNumber() {
		return userNumber;
	}

	@ApiModelProperty(name = "firstName", value = "User's first name")
	@JsonProperty("firstName")
	public String getFirstName() {
		return firstName;
	}

	@ApiModelProperty(name = "lastName", value = "User's last name")
	@JsonProperty("lastName")
	public String getLastName() {
		return lastName;
	}

	@ApiModelProperty(name = "email", value = "User's primary email address")
	@JsonProperty("email")
	public String getEmail() {
		return email;
	}

	@ApiModelProperty(name = "secondaryEmail", value = "User's secondary email address")
	@JsonProperty("secondaryEmail")
	public String getSecondaryEmail() {
		return secondaryEmail;
	}

	@ApiModelProperty(name = "avatarUri", value = "User's avatar image uri")
	@JsonProperty("avatarUri")
	public String getAvatarUri() {
		return avatarUri;
	}

	@ApiModelProperty(name = "avatar", value = "User's avatar image")
	@JsonProperty("avatar")
	public ApiImageDTO getAvatar() {
		return avatar;
	}

	@ApiModelProperty(name = "phoneNumber", value = "User's primary phone number")
	@JsonProperty("phoneNumber")
	public String getPhoneNumber() {
		return phoneNumber;
	}

	@ApiModelProperty(name = "phoneNumbers", value = "User's phone numbers - only supports WORK or MOBILE types")
	@JsonProperty("phoneNumbers")
	public List<ApiPhoneNumberDTO> getPhoneNumbers() {
		return phoneNumbers;
	}

	@ApiModelProperty(name = "jobTitle", value = "User's job title")
	@JsonProperty("jobTitle")
	public String getJobTitle() {
		return jobTitle;
	}

	@ApiModelProperty(name = "maxTravelDistance", value = "Maximum distance in miles this user is willing to travel for work")
	@JsonProperty("maxTravelDistance")
	public Integer getMaxTravelDistance() {
		return maxTravelDistance;
	}

	@ApiModelProperty(name = "skills", value = "Skills that this user has listed in their profile")
	@JsonProperty("skills")
	public List<SkillApiDTO> getSkills() {
		return skills;
	}

	@ApiModelProperty(name = "address", value = "User's primary address")
	@JsonProperty("address")
	public AddressApiDTO getAddress() {
		return address;
	}

	public static final class Builder {
		private String userNumber;
		private String firstName;
		private String lastName;
		private String email;
		private String secondaryEmail;
		private String avatarUri;
		private ApiImageDTO avatar;
		private String phoneNumber;
		private List<ApiPhoneNumberDTO.Builder> phoneNumbers;
		private String jobTitle;
		private Integer maxTravelDistance = Constants.MAX_TRAVEL_DISTANCE;
		private List<SkillApiDTO.Builder> skills;
		private AddressApiDTO address;

		public Builder() {
		}

		public Builder(ApiProfileDTO copy) {
			this.userNumber = copy.userNumber;
			this.firstName = copy.firstName;
			this.lastName = copy.lastName;
			this.email = copy.email;
			this.avatarUri = copy.avatarUri;
			this.avatar = copy.avatar;
			if(copy.phoneNumbers != null) {
				this.phoneNumbers = Lists.newArrayList();
				for (ApiPhoneNumberDTO phoneNumber : copy.phoneNumbers) {
					this.phoneNumbers.add(new ApiPhoneNumberDTO.Builder(phoneNumber));
				}
			}
			this.jobTitle = copy.jobTitle;
			this.maxTravelDistance = copy.maxTravelDistance;
			if(copy.skills != null) {
				this.skills = Lists.newArrayList();
				for (SkillApiDTO skill : copy.skills) {
					this.skills.add(new SkillApiDTO.Builder(skill));
				}
			}
			this.address = copy.address;
		}

		@JsonProperty("userNumber")
		public Builder withUserNumber(String userNumber) {
			this.userNumber = userNumber;
			return this;
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

		@JsonProperty("avatarUri")
		public Builder withAvatarUri(String avatarUri) {
			this.avatarUri = avatarUri;
			return this;
		}

		@JsonProperty("avatar")
		public Builder withAvatar(ApiImageDTO avatar) {
			this.avatar = avatar;
			return this;
		}


		@JsonProperty("phoneNumber")
		public Builder withPhoneNumber(String phoneNumber) {
			this.phoneNumber = phoneNumber;
			return this;
		}

		@JsonProperty("phoneNumbers")
		public Builder withPhoneNumbers(List<ApiPhoneNumberDTO.Builder> phoneNumbers) {
			this.phoneNumbers = phoneNumbers;
			return this;
		}

		@JsonProperty("jobTitle")
		public Builder withJobTitle(String jobTitle) {
			this.jobTitle = jobTitle;
			return this;
		}

		@JsonProperty("maxTravelDistance")
		public Builder withMaxTravelDistance(Integer maxTravelDistance) {
			this.maxTravelDistance = maxTravelDistance;
			return this;
		}

		@JsonProperty("skills")
		public Builder withSkills(List<SkillApiDTO.Builder> skills) {
			this.skills = skills;
			return this;
		}

		@JsonProperty("address")
		public Builder withAddress(AddressApiDTO address) {
			this.address = address;
			return this;
		}

		public ApiProfileDTO build() {
			return new ApiProfileDTO(this);
		}
	}

	public WorkerOnboardingDTO asWorkerOnboardingDTO() {
		WorkerOnboardingDTO workerOnboardingDTO = new WorkerOnboardingDTO();

		workerOnboardingDTO.setSecondaryEmail(secondaryEmail);
		workerOnboardingDTO.setFirstName(firstName);
		workerOnboardingDTO.setLastName(lastName);
		workerOnboardingDTO.setEmail(email);
		workerOnboardingDTO.setAvatar(avatar != null ? avatar.asImageDTO() : null);
		if(phoneNumbers != null) {
			List<PhoneInfoDTO> phoneInfoDTOs = convert(phoneNumbers, new Converter<ApiPhoneNumberDTO, PhoneInfoDTO>() {
				@Override
				public PhoneInfoDTO convert(ApiPhoneNumberDTO apiPhoneNumberDTO) {
					return apiPhoneNumberDTO.asPhoneInfoDTO();
				}
			});
			workerOnboardingDTO.setPhones(phoneInfoDTOs);
		}
		workerOnboardingDTO.setJobTitle(new ApiJobTitleDTO(null, jobTitle));
		if(skills != null) {
			workerOnboardingDTO.setSkills(convert(skills, new Converter<SkillApiDTO, OnboardingSkillDTO>() {
				@Override
				public OnboardingSkillDTO convert(SkillApiDTO skillApiDTO) {
					return new OnboardingSkillDTO(skillApiDTO.getId(), skillApiDTO.getName(), skillApiDTO.getType());
				}
			}));
		}

		if(address != null) {
			workerOnboardingDTO.setAddress1(address.getAddressLine1());
			workerOnboardingDTO.setAddress2(address.getAddressLine2());
			workerOnboardingDTO.setCity(address.getCity());
			workerOnboardingDTO.setStateShortName(address.getState());
			workerOnboardingDTO.setCountryIso(address.getCountry());
			workerOnboardingDTO.setPostalCode(address.getPostalCode());
			workerOnboardingDTO.setLongitude(String.valueOf(address.getLongitude()));
			workerOnboardingDTO.setLatitude(String.valueOf(address.getLatitude()));
		}
		workerOnboardingDTO.setMaxTravelDistance(maxTravelDistance);

		return workerOnboardingDTO;
	}

	public InvitationUserRegistrationDTO asInvitationUserRegistrationDTO() {
		InvitationUserRegistrationDTO invitationUserRegistrationDTO = new InvitationUserRegistrationDTO();


		invitationUserRegistrationDTO.setFirstName(firstName);
		invitationUserRegistrationDTO.setLastName(lastName);
		invitationUserRegistrationDTO.setEmail(email);
		invitationUserRegistrationDTO.setWorkPhone(phoneNumber);

		if(address != null) {
			invitationUserRegistrationDTO.setAddress1(address.getAddressLine1());
			invitationUserRegistrationDTO.setAddress2(address.getAddressLine2());
			invitationUserRegistrationDTO.setCity(address.getCity());
			invitationUserRegistrationDTO.setState(address.getState());
			invitationUserRegistrationDTO.setCountry(address.getCountry());
			invitationUserRegistrationDTO.setPostalCode(address.getPostalCode());
		}
		
		return invitationUserRegistrationDTO;
	}
}
