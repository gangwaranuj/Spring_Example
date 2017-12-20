package com.workmarket.api.v1.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.workmarket.api.model.ApiPhoneNumberDTO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.util.List;

@ApiModel(value = "ActiveResource")
@JsonDeserialize(builder = ApiActiveResourceDTO.Builder.class)
public class ApiActiveResourceDTO {

	private final String id;
	private final String firstName;
	private final String lastName;
	private final String companyName;
	private final String email;
	private final String profilePicture;
	private final String profilePictureLarge;
	private final Integer rating;
	private final Long numberOfRatings;
	private final Boolean confirmedOnsite;
	private final Long confirmedDate;
	private final List<ApiPhoneNumberDTO> phoneNumbers;
	private final ApiAddressDTO address;
	private final List<ApiCheckInOutDTO> checkInOut;

	private ApiActiveResourceDTO(Builder builder) {
		id = builder.id;
		firstName = builder.firstName;
		lastName = builder.lastName;
		companyName = builder.companyName;
		email = builder.email;
		profilePicture = builder.profilePicture;
		profilePictureLarge = builder.profilePictureLarge;
		rating = builder.rating;
		numberOfRatings = builder.numberOfRatings;
		confirmedOnsite = builder.confirmedOnsite;
		confirmedDate = builder.confirmedDate;
		phoneNumbers = builder.phoneNumbers;
		address = builder.address;
		checkInOut = builder.checkInOut;
	}

	@ApiModelProperty(name = "id")
	@JsonProperty("id")
	public String getId() {
		return id;
	}

	@ApiModelProperty(name = "first_name")
	@JsonProperty("first_name")
	public String getFirstName() {
		return firstName;
	}

	@ApiModelProperty(name = "last_name")
	@JsonProperty("last_name")
	public String getLastName() {
		return lastName;
	}

	@ApiModelProperty(name = "company_name")
	@JsonProperty("company_name")
	public String getCompanyName() {
		return companyName;
	}

	@ApiModelProperty(name = "email")
	@JsonProperty("email")
	public String getEmail() {
		return email;
	}

	@ApiModelProperty(name = "profile_picture")
	@JsonProperty("profile_picture")
	public String getProfilePicture() {
		return profilePicture;
	}

	@ApiModelProperty(name = "profile_picture_large")
	@JsonProperty("profile_picture_large")
	public String getProfilePictureLarge() {
		return profilePictureLarge;
	}

	@ApiModelProperty(name = "rating")
	@JsonProperty("rating")
	public Integer getRating() {
		return rating;
	}

	@ApiModelProperty(name = "number_of_ratings")
	@JsonProperty("number_of_ratings")
	public Long getNumberOfRatings() {
		return numberOfRatings;
	}

	@ApiModelProperty(name = "confirmed_onsite")
	@JsonProperty("confirmed_onsite")
	public Boolean getConfirmedOnsite() {
		return confirmedOnsite;
	}

	@ApiModelProperty(name = "confirmed_date")
	@JsonProperty("confirmed_date")
	public Long getConfirmedDate() {
		return confirmedDate;
	}

	@ApiModelProperty(name = "phone_numbers")
	@JsonProperty("phone_numbers")
	public List<ApiPhoneNumberDTO> getPhoneNumbers() {
		return phoneNumbers;
	}

	@ApiModelProperty(name = "address")
	@JsonProperty("address")
	public ApiAddressDTO getAddress() {
		return address;
	}

	@ApiModelProperty(name = "check_in_out")
	@JsonProperty("check_in_out")
	public List<ApiCheckInOutDTO> getCheckInOut() {
		return checkInOut;
	}

	public static final class Builder {
		private String id;
		private String firstName;
		private String lastName;
		private String companyName;
		private String email;
		private String profilePicture;
		private String profilePictureLarge;
		private Integer rating;
		private Long numberOfRatings;
		private Boolean confirmedOnsite;
		private Long confirmedDate;
		private List<ApiPhoneNumberDTO> phoneNumbers;
		private ApiAddressDTO address;
		private List<ApiCheckInOutDTO> checkInOut;

		public Builder() {
		}

		public Builder(ApiActiveResourceDTO copy) {
			this.id = copy.id;
			this.firstName = copy.firstName;
			this.lastName = copy.lastName;
			this.companyName = copy.companyName;
			this.email = copy.email;
			this.profilePicture = copy.profilePicture;
			this.profilePictureLarge = copy.profilePictureLarge;
			this.rating = copy.rating;
			this.numberOfRatings = copy.numberOfRatings;
			this.confirmedOnsite = copy.confirmedOnsite;
			this.confirmedDate = copy.confirmedDate;
			this.phoneNumbers = copy.phoneNumbers;
			this.address = copy.address;
			this.checkInOut = copy.checkInOut;
		}

		@JsonProperty("id")
		public Builder withId(String id) {
			this.id = id;
			return this;
		}

		@JsonProperty("first_name")
		public Builder withFirstName(String firstName) {
			this.firstName = firstName;
			return this;
		}

		@JsonProperty("last_name")
		public Builder withLastName(String lastName) {
			this.lastName = lastName;
			return this;
		}

		@JsonProperty("company_name")
		public Builder withCompanyName(String companyName) {
			this.companyName = companyName;
			return this;
		}

		@JsonProperty("email")
		public Builder withEmail(String email) {
			this.email = email;
			return this;
		}

		@JsonProperty("profile_picture")
		public Builder withProfilePicture(String profilePicture) {
			this.profilePicture = profilePicture;
			return this;
		}

		@JsonProperty("profile_picture_large")
		public Builder withProfilePictureLarge(String profilePictureLarge) {
			this.profilePictureLarge = profilePictureLarge;
			return this;
		}

		@JsonProperty("rating")
		public Builder withRating(Integer rating) {
			this.rating = rating;
			return this;
		}

		@JsonProperty("number_of_ratings")
		public Builder withNumberOfRatings(Long numberOfRatings) {
			this.numberOfRatings = numberOfRatings;
			return this;
		}

		@JsonProperty("confirmed_onsite")
		public Builder withConfirmedOnsite(Boolean confirmedOnsite) {
			this.confirmedOnsite = confirmedOnsite;
			return this;
		}

		@JsonProperty("confirmed_date")
		public Builder withConfirmedDate(Long confirmedDate) {
			this.confirmedDate = confirmedDate;
			return this;
		}

		@JsonProperty("phone_numbers")
		public Builder withPhoneNumbers(List<ApiPhoneNumberDTO> phoneNumbers) {
			this.phoneNumbers = phoneNumbers;
			return this;
		}

		@JsonProperty("address")
		public Builder withAddress(ApiAddressDTO address) {
			this.address = address;
			return this;
		}

		@JsonProperty("check_in_out")
		public Builder withCheckInOut(List<ApiCheckInOutDTO> checkInOut) {
			this.checkInOut = checkInOut;
			return this;
		}

		public ApiActiveResourceDTO build() {
			return new ApiActiveResourceDTO(this);
		}
	}
}
