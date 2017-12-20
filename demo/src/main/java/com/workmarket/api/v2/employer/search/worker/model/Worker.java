package com.workmarket.api.v2.employer.search.worker.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.google.common.collect.ImmutableList;
import com.workmarket.api.v2.employer.search.common.model.BaseDTO;
import com.workmarket.api.v2.employer.search.common.model.Scorecard;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.util.List;

/**
 * Value object holding our representation of a worker.
 */
@ApiModel("Worker")
@JsonInclude(JsonInclude.Include.NON_EMPTY)
@JsonDeserialize(builder = Worker.Builder.class)
public class Worker extends BaseDTO {
	private final String uuid;
	private final String userNumber;
	private final String firstName;
	private final String lastName;
	private final String email;
	private final String avatarAssetUri;
	private final String jobTitle;
	private final String companyName;
	private final String city;
	private final String state;
	private final String postalCode;
	private final String country;
	private final Integer lane;
	private final Integer rating;
	private final List<Verification> verifications;
	private final Boolean blocked;
	private final List<String> certifications;
	private final List<String> insurances;
	private final List<String> licenses;
	private final List<String> companyAssessments;
	private final List<String> groups;
	private final List<String> languages;
	private final List<String> skills;
	private final Scorecard scorecard;
	private final Scorecard companyScorecard;
	private final String createdOn;

	private Worker(final Builder builder) {
		this.uuid = builder.uuid;
		this.userNumber = builder.userNumber;
		this.firstName = builder.firstName;
		this.lastName = builder.lastName;
		this.email = builder.email;
		this.avatarAssetUri = builder.avatarAssetUri;
		this.jobTitle = builder.jobTitle;
		this.companyName = builder.companyName;
		this.city = builder.city;
		this.state = builder.state;
		this.postalCode = builder.postalCode;
		this.country = builder.country;
		this.lane = builder.lane;
		this.rating = builder.rating;
		this.verifications = builder.verificationsBuilder.build();
		this.blocked = builder.blocked;
		this.certifications = builder.certificationsBuilder.build();
		this.insurances = builder.insurancesBuilder.build();
		this.licenses = builder.licensesBuilder.build();
		this.companyAssessments = builder.companyAssessmentsBuilder.build();
		this.groups = builder.groupsBuilder.build();
		this.languages = builder.languagesBuilder.build();
		this.skills = builder.skillsBuilder.build();
		this.scorecard = builder.scorecard;
		this.companyScorecard = builder.companyScorecard;
		this.createdOn = builder.createdOn;

	}

	@ApiModelProperty(name = "uuid")
	@JsonProperty("uuid")
	public String getUuid() {
		return uuid;
	}

	@ApiModelProperty(name = "userNumber")
	@JsonProperty("userNumber")
	public String getUserNumber() {
		return userNumber;
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

	@ApiModelProperty(name = "avatarAssetUri")
	@JsonProperty("avatarAssetUri")
	public String getAvatarAssetUri() {
		return avatarAssetUri;
	}

	@ApiModelProperty(name = "jobTitle")
	@JsonProperty("jobTitle")
	public String getJobTitle() {
		return jobTitle;
	}

	@ApiModelProperty(name = "companyName")
	@JsonProperty("companyName")
	public String getCompanyName() {
		return companyName;
	}

	@ApiModelProperty(name = "city")
	@JsonProperty("city")
	public String getCity() {
		return city;
	}

	@ApiModelProperty(name = "state")
	@JsonProperty("state")
	public String getState() {
		return state;
	}

	@ApiModelProperty(name = "postalCode")
	@JsonProperty("postalCode")
	public String getPostalCode() {
		return postalCode;
	}

	@ApiModelProperty(name = "country")
	@JsonProperty("country")
	public String getCountry() {
		return country;
	}

	@ApiModelProperty(name = "lane")
	@JsonProperty("lane")
	public Integer getLane() {
		return lane;
	}

	@ApiModelProperty(name = "rating")
	@JsonProperty("rating")
	public Integer getRating() {
		return rating;
	}

	@ApiModelProperty(name = "verifications")
	@JsonProperty("verifications")
	public List<Verification> getVerifications() {
		return verifications;
	}

	@ApiModelProperty(name = "blocked")
	@JsonProperty("blocked")
	public Boolean getBlocked() {
		return blocked;
	}

	@ApiModelProperty(name = "certifications")
	@JsonProperty("certifications")
	public List<String> getCertifications() {
		return certifications;
	}

	@ApiModelProperty(name = "insurances")
	@JsonProperty("insurances")
	public List<String> getInsurances() {
		return insurances;
	}

	@ApiModelProperty(name = "licenses")
	@JsonProperty("licenses")
	public List<String> getLicenses() {
		return licenses;
	}

	@ApiModelProperty(name = "companyAssessments")
	@JsonProperty("companyAssessments")
	public List<String> getCompanyAssessments() {
		return companyAssessments;
	}

	@ApiModelProperty(name = "groups")
	@JsonProperty("groups")
	public List<String> getGroups() {
		return groups;
	}

	@ApiModelProperty(name = "languages")
	@JsonProperty("languages")
	public List<String> getLanguages() {
		return languages;
	}

	@ApiModelProperty(name = "skills")
	@JsonProperty("skills")
	public List<String> getSkills() {
		return skills;
	}

	@ApiModelProperty(name = "scorecard")
	@JsonProperty("scorecard")
	public Scorecard getScorecard() {
		return scorecard;
	}

	@ApiModelProperty(name = "companyScorecard")
	@JsonProperty("companyScorecard")
	public Scorecard getCompanyScorecard() {
		return companyScorecard;
	}

	@ApiModelProperty(name = "createdOn")
	@JsonProperty("createdOn")
	public String getCreatedOn() {
		return createdOn;
	}

	public static class Builder {
		private String uuid;
		private String userNumber;
		private String firstName;
		private String lastName;
		private String email;
		private String avatarAssetUri;
		private String jobTitle;
		private String companyName;
		private String city;
		private String state;
		private String postalCode;
		private String country;
		private Integer lane;
		private Integer rating;
		private ImmutableList.Builder<Verification> verificationsBuilder = new ImmutableList.Builder<>();
		private Boolean blocked;
		private ImmutableList.Builder<String> certificationsBuilder = new ImmutableList.Builder<>();
		private ImmutableList.Builder<String> insurancesBuilder = new ImmutableList.Builder<>();
		private ImmutableList.Builder<String> licensesBuilder = new ImmutableList.Builder<>();
		private ImmutableList.Builder<String> companyAssessmentsBuilder = new ImmutableList.Builder<>();
		private ImmutableList.Builder<String> groupsBuilder = new ImmutableList.Builder<>();
		private ImmutableList.Builder<String> languagesBuilder = new ImmutableList.Builder<>();
		private ImmutableList.Builder<String> skillsBuilder = new ImmutableList.Builder<>();
		private Scorecard scorecard;
		private Scorecard companyScorecard;
		private String createdOn;

		public Builder() {

		}

		public Builder(final Builder builder) {
			this.uuid = builder.uuid;
			this.userNumber = builder.userNumber;
			this.firstName = builder.firstName;
			this.lastName = builder.lastName;
			this.email = builder.email;
			this.avatarAssetUri = builder.avatarAssetUri;
			this.jobTitle = builder.jobTitle;
			this.companyName = builder.companyName;
			this.city = builder.city;
			this.state = builder.state;
			this.postalCode = builder.postalCode;
			this.country = builder.country;
			this.lane = builder.lane;
			this.rating = builder.rating;

			this.verificationsBuilder.addAll(builder.verificationsBuilder.build());

			this.blocked = builder.blocked;
			this.certificationsBuilder.addAll(builder.certificationsBuilder.build());
			this.insurancesBuilder.addAll(builder.insurancesBuilder.build());
			this.licensesBuilder.addAll(builder.licensesBuilder.build());
			this.companyAssessmentsBuilder.addAll(builder.companyAssessmentsBuilder.build());
			this.groupsBuilder.addAll(builder.groupsBuilder.build());
			this.languagesBuilder.addAll(builder.languagesBuilder.build());
			this.skillsBuilder.addAll(builder.skillsBuilder.build());
			this.scorecard = builder.scorecard;
			this.companyScorecard = builder.companyScorecard;
			this.createdOn = builder.createdOn;
		}

		@JsonProperty("uuid") public Builder setUuid(final String uuid) {
			this.uuid = uuid;
			return this;
		}

		@JsonProperty("userNumber") public Builder setUserNumber(final String userNumber) {
			this.userNumber = userNumber;
			return this;
		}

		@JsonProperty("firstName") public Builder setFirstName(final String firstName) {
			this.firstName = firstName;
			return this;
		}

		@JsonProperty("lastName") public Builder setLastName(final String lastName) {
			this.lastName = lastName;
			return this;
		}
		@JsonProperty("email") public Builder setEmail(final String email) {
			this.email = email;
			return this;
		}
		@JsonProperty("avatarAssetUri") public Builder setAvatarAssetUri(final String avatarAssetUri) {
			this.avatarAssetUri = avatarAssetUri;
			return this;
		}
		@JsonProperty("jobTitle") public Builder setJobTitle(final String jobTitle) {
			this.jobTitle = jobTitle;
			return this;
		}
		@JsonProperty("companyName") public Builder setCompanyName(final String companyName) {
			this.companyName = companyName;
			return this;
		}
		@JsonProperty("city") public Builder setCity(final String city) {
			this.city = city;
			return this;
		}
		@JsonProperty("state") public Builder setState(final String state) {
			this.state = state;
			return this;
		}
		@JsonProperty("postalCode") public Builder setPostalCode(final String postalCode) {
			this.postalCode = postalCode;
			return this;
		}
		@JsonProperty("country") public Builder setCountry(final String country) {
			this.country = country;
			return this;
		}
		@JsonProperty("lane") public Builder setLane(final Integer lane) {
			this.lane = lane;
			return this;
		}
		@JsonProperty("rating") public Builder setRating(final Integer rating) {
			this.rating = rating;
			return this;
		}

		public Builder addVerifications(final List<Verification> verifications) {
			this.verificationsBuilder.addAll(verifications);
			return this;
		}

		public Builder addVerification(final Verification verification) {
			this.verificationsBuilder.add(verification);
			return this;
		}

		@JsonProperty("verifications") public Builder setVerifications(final List<Verification> verifications) {
			this.verificationsBuilder = new ImmutableList.Builder<>();
			this.verificationsBuilder.addAll(verifications);
			return this;
		}

		@JsonProperty("blocked") public Builder setBlocked(final Boolean blocked) {
			this.blocked = blocked;
			return this;
		}

		public Builder addCertifications(final List<String> certifications) {
			this.certificationsBuilder.addAll(certifications);
			return this;
		}

		@JsonProperty("certifications") public Builder setCertifications(final List<String> certifications) {
			this.certificationsBuilder = new ImmutableList.Builder<>();
			this.certificationsBuilder.addAll(certifications);
			return this;
		}

		public Builder addCertification(final String certification) {
			this.certificationsBuilder.add(certification);
			return this;
		}

		public Builder addInsurances(final List<String> insurances) {
			this.insurancesBuilder.addAll(insurances);
			return this;
		}

		@JsonProperty("insurances") public Builder setInsurances(final List<String> insurances) {
			this.insurancesBuilder = new ImmutableList.Builder<>();
			this.insurancesBuilder.addAll(insurances);
			return this;
		}

		public Builder addInsurance(final String insurance) {
			this.insurancesBuilder.add(insurance);
			return this;
		}

		public Builder addLicenses(final List<String> licenses) {
			this.licensesBuilder.addAll(licenses);
			return this;
		}

		@JsonProperty("licenses") public Builder setLicenses(final List<String> licenses) {
			this.licensesBuilder = new ImmutableList.Builder<>();
			this.licensesBuilder.addAll(licenses);
			return this;
		}

		public Builder addLicense(final String license) {
			this.licensesBuilder.add(license);
			return this;
		}

		public Builder addCompanyAssessments(final List<String> companyAssessments) {
			this.companyAssessmentsBuilder.addAll(companyAssessments);
			return this;
		}

		@JsonProperty("companyAssessments") public Builder setCompanyAssessments(final List<String> companyAssessments) {
			this.companyAssessmentsBuilder = new ImmutableList.Builder<>();
			this.companyAssessmentsBuilder.addAll(companyAssessments);
			return this;
		}

		public Builder addCompanyAssessment(final String companyAssessment) {
			this.companyAssessmentsBuilder.add(companyAssessment);
			return this;
		}

		public Builder addGroups(final List<String> groups) {
			this.groupsBuilder.addAll(groups);
			return this;
		}

		@JsonProperty("groups") public Builder setGroups(final List<String> groups) {
			this.groupsBuilder = new ImmutableList.Builder<>();
			this.groupsBuilder.addAll(groups);
			return this;
		}

		public Builder addGroup(final String group) {
			this.groupsBuilder.add(group);
			return this;
		}

		public Builder addLanguages(final List<String> languages) {
			this.languagesBuilder.addAll(languages);
			return this;
		}

		@JsonProperty("languages") public Builder setLanguages(final List<String> languages) {
			this.languagesBuilder = new ImmutableList.Builder<>();
			this.languagesBuilder.addAll(languages);
			return this;
		}

		public Builder addLanguage(final String language) {
			this.languagesBuilder.add(language);
			return this;
		}

		public Builder addSkills(final List<String> skills) {
			this.skillsBuilder.addAll(skills);
			return this;
		}

		@JsonProperty("skills") public Builder setSkills(final List<String> skills) {
			this.skillsBuilder = new ImmutableList.Builder<>();
			this.skillsBuilder.addAll(skills);
			return this;
		}

		public Builder addSkill(final String skill) {
			this.skillsBuilder.add(skill);
			return this;
		}

		@JsonProperty("scorecard") public Builder setScorecard(final Scorecard scorecard) {
			this.scorecard = scorecard;
			return this;
		}

		@JsonProperty("companyScorecard") public Builder setCompanyScorecard(final Scorecard companyScorecard) {
			this.companyScorecard = companyScorecard;
			return this;
		}

		@JsonProperty("createdOn") public Builder setCreatedOn(final String createdOn) {
			this.createdOn = createdOn;
			return this;
		}

		public Worker build() {
			return new Worker(this);
		}
	}
}
