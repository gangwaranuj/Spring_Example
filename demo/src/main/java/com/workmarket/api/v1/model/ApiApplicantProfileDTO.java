package com.workmarket.api.v1.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.workmarket.api.model.ApiPhoneNumberDTO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.util.List;

@ApiModel(value = "ApplicantProfile")
@JsonDeserialize(builder = ApiApplicantProfileDTO.Builder.class)
public class ApiApplicantProfileDTO {

	private final String id;
	private final String firstName;
	private final String lastName;
	private final String email;
	private final String companyName;
	private final Double rating;
	private final Integer numberOfRatings;
	private final String jobTitle;
	private final String overview;
	private final List<ApiPhoneNumberDTO> phoneNumbers;

	private ApiApplicantProfileDTO(Builder builder) {
		id = builder.id;
		firstName = builder.firstName;
		lastName = builder.lastName;
		email = builder.email;
		companyName = builder.companyName;
		rating = builder.rating;
		numberOfRatings = builder.numberOfRatings;
		jobTitle = builder.jobTitle;
		overview = builder.overview;
		phoneNumbers = builder.phoneNumbers;
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

	@ApiModelProperty(name = "email")
	@JsonProperty("email")
	public String getEmail() {
		return email;
	}

	@ApiModelProperty(name = "company_name")
	@JsonProperty("company_name")
	public String getCompanyName() {
		return companyName;
	}

	@ApiModelProperty(name = "rating")
	@JsonProperty("rating")
	public Double getRating() {
		return rating;
	}

	@ApiModelProperty(name = "number_of_ratings")
	@JsonProperty("number_of_ratings")
	public Integer getNumberOfRatings() {
		return numberOfRatings;
	}

	@ApiModelProperty(name = "job_title")
	@JsonProperty("job_title")
	public String getJobTitle() {
		return jobTitle;
	}

	@ApiModelProperty(name = "overview")
	@JsonProperty("overview")
	public String getOverview() {
		return overview;
	}

	@ApiModelProperty(name = "phone_numbers")
	@JsonProperty("phone_numbers")
	public List<ApiPhoneNumberDTO> getPhoneNumbers() {
		return phoneNumbers;
	}

	public static final class Builder {
		private String id;
		private String firstName;
		private String lastName;
		private String email;
		private String companyName;
		private Double rating;
		private Integer numberOfRatings;
		private String jobTitle;
		private String overview;
		private List<ApiPhoneNumberDTO> phoneNumbers;

		public Builder() {
		}

		public Builder(ApiApplicantProfileDTO copy) {
			this.id = copy.id;
			this.firstName = copy.firstName;
			this.lastName = copy.lastName;
			this.email = copy.email;
			this.companyName = copy.companyName;
			this.rating = copy.rating;
			this.numberOfRatings = copy.numberOfRatings;
			this.jobTitle = copy.jobTitle;
			this.overview = copy.overview;
			this.phoneNumbers = copy.phoneNumbers;
		}

		@JsonProperty("id")
		public Builder withId(String id) {
			this.id = id;
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

		@JsonProperty("companyName")
		public Builder withCompanyName(String companyName) {
			this.companyName = companyName;
			return this;
		}

		@JsonProperty("rating")
		public Builder withRating(Double rating) {
			this.rating = rating;
			return this;
		}

		@JsonProperty("numberOfRatings")
		public Builder withNumberOfRatings(Integer numberOfRatings) {
			this.numberOfRatings = numberOfRatings;
			return this;
		}

		@JsonProperty("jobTitle")
		public Builder withJobTitle(String jobTitle) {
			this.jobTitle = jobTitle;
			return this;
		}

		@JsonProperty("overview")
		public Builder withOverview(String overview) {
			this.overview = overview;
			return this;
		}

		@JsonProperty("phoneNumbers")
		public Builder withPhoneNumbers(List<ApiPhoneNumberDTO> phoneNumbers) {
			this.phoneNumbers = phoneNumbers;
			return this;
		}

		public ApiApplicantProfileDTO build() {
			return new ApiApplicantProfileDTO(this);
		}
	}
}
