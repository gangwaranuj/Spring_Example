package com.workmarket.api.v2.worker.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ianha on 4/3/15.
 */
public class WorkersSearchResult {
	// TODO API - the nested builders stuff needs review
	private final String firstName;
	private final String lastName;
	private final String jobTitle;
	private final String userNumber;
	private final Company company;
	private final List<String> certifications;
	private final List<String> insurances;
	private final List<String> groups;
	private final String lastBackgroundCheckDate;
	private final String lastDrugTestDate;
	private final String lastAssignedWorkDate;
	private final String email;
	private final String workPhone;
	private final String mobilePhone;
	private final Address address;
	private final String smallAvatarAssetUri;
	private final List<String> skillNames;
	private final ScoreCard scoreCard;
	private final List<String> toolNames;

	private WorkersSearchResult(Builder builder) {
		firstName = builder.firstName;
		lastName = builder.lastName;
		jobTitle = builder.jobTitle;
		userNumber = builder.userNumber;
		company = builder.company.build();
		certifications = builder.certifications;
		insurances = builder.insurances;
		groups = builder.groups;
		lastBackgroundCheckDate = builder.lastBackgroundCheckDate;
		lastDrugTestDate = builder.lastDrugTestDate;
		lastAssignedWorkDate = builder.lastAssignedWorkDate;
		email = builder.email;
		workPhone = builder.workPhone;
		mobilePhone = builder.mobilePhone;
		address = builder.address.build();
		smallAvatarAssetUri = builder.smallAvatarAssetUri;
		skillNames = builder.skillNames;
		scoreCard = builder.scoreCard.build();
		toolNames = builder.toolNames;
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

	@ApiModelProperty(name = "jobTitle")
	@JsonProperty("jobTitle")
	public String getJobTitle() {
		return jobTitle;
	}

	@ApiModelProperty(name = "userNumber")
	@JsonProperty("userNumber")
	public String getUserNumber() {
		return userNumber;
	}

	@ApiModelProperty(name = "company")
	@JsonProperty("company")
	public Company getCompany() {
		return company;
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

	@ApiModelProperty(name = "groups")
	@JsonProperty("groups")
	public List<String> getGroups() {
		return groups;
	}

	@ApiModelProperty(name = "lastBackgroundCheckDate")
	@JsonProperty("lastBackgroundCheckDate")
	public String getLastBackgroundCheckDate() {
		return lastBackgroundCheckDate;
	}

	@ApiModelProperty(name = "lastDrugTestDate")
	@JsonProperty("lastDrugTestDate")
	public String getLastDrugTestDate() {
		return lastDrugTestDate;
	}

	@ApiModelProperty(name = "lastAssignedWorkDate")
	@JsonProperty("lastAssignedWorkDate")
	public String getLastAssignedWorkDate() {
		return lastAssignedWorkDate;
	}

	@ApiModelProperty(name = "email")
	@JsonProperty("email")
	public String getEmail() {
		return email;
	}

	@ApiModelProperty(name = "workPhone")
	@JsonProperty("workPhone")
	public String getWorkPhone() {
		return workPhone;
	}

	@ApiModelProperty(name = "mobilePhone")
	@JsonProperty("mobilePhone")
	public String getMobilePhone() {
		return mobilePhone;
	}

	@ApiModelProperty(name = "address")
	@JsonProperty("address")
	public Address getAddress() {
		return address;
	}

	@ApiModelProperty(name = "smallAvatarAssetUri")
	@JsonProperty("smallAvatarAssetUri")
	public String getSmallAvatarAssetUri() {
		return smallAvatarAssetUri;
	}

	@ApiModelProperty(name = "skillNames")
	@JsonProperty("skillNames")
	public List<String> getSkillNames() {
		return skillNames;
	}

	@ApiModelProperty(name = "scoreCard")
	@JsonProperty("scoreCard")
	public ScoreCard getScoreCard() {
		return scoreCard;
	}

	@ApiModelProperty(name = "toolNames")
	@JsonProperty("toolNames")
	public List<String> getToolNames() {
		return toolNames;
	}

	public static final class Builder {
		private String firstName = "";
		private String lastName = "";
		private String jobTitle = "";
		private String userNumber = "";
		private Company.Builder company = new Company.Builder();
		private List<String> certifications = new ArrayList<>();
		private List<String> insurances = new ArrayList<>();
		private List<String> groups = new ArrayList<>();
		private String lastBackgroundCheckDate = "";
		private String lastDrugTestDate = "";
		private String lastAssignedWorkDate = "";
		private String email = "";
		private String workPhone = "";
		private String mobilePhone = "";
		private Address.Builder address = new Address.Builder();
		private String smallAvatarAssetUri = "";
		private List<String> skillNames = new ArrayList<>();
		private ScoreCard.Builder scoreCard = new ScoreCard.Builder();
		private List<String> toolNames = new ArrayList<>();

		public Builder() {
		}

		public Builder(WorkersSearchResult.Builder copy) {
			this.firstName = copy.firstName;
			this.lastName = copy.lastName;
			this.jobTitle = copy.jobTitle;
			this.userNumber = copy.userNumber;
			this.company = copy.company;
			this.certifications = copy.certifications;
			this.insurances = copy.insurances;
			this.groups = copy.groups;
			this.lastBackgroundCheckDate = copy.lastBackgroundCheckDate;
			this.lastDrugTestDate = copy.lastDrugTestDate;
			this.lastAssignedWorkDate = copy.lastAssignedWorkDate;
			this.email = copy.email;
			this.workPhone = copy.workPhone;
			this.mobilePhone = copy.mobilePhone;
			this.address = copy.address;
			this.smallAvatarAssetUri = copy.smallAvatarAssetUri;
			this.skillNames = copy.skillNames;
			this.scoreCard = copy.scoreCard;
			this.toolNames = copy.toolNames;
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

		@JsonProperty("jobTitle")
		public Builder withJobTitle(String jobTitle) {
			this.jobTitle = jobTitle;
			return this;
		}

		@JsonProperty("userNumber")
		public Builder withUserNumber(String userNumber) {
			this.userNumber = userNumber;
			return this;
		}

		@JsonProperty("company")
		public Builder withCompany(Company.Builder companyBuilder) {
			this.company = company;
			return this;
		}

		@JsonProperty("certifications")
		public Builder withCertifications(List<String> certifications) {
			this.certifications = certifications;
			return this;
		}

		@JsonProperty("insurances")
		public Builder withInsurances(List<String> insurances) {
			this.insurances = insurances;
			return this;
		}

		@JsonProperty("groups")
		public Builder withGroups(List<String> groups) {
			this.groups = groups;
			return this;
		}

		@JsonProperty("lastBackgroundCheckDate")
		public Builder withLastBackgroundCheckDate(String lastBackgroundCheckDate) {
			this.lastBackgroundCheckDate = lastBackgroundCheckDate;
			return this;
		}

		@JsonProperty("lastDrugTestDate")
		public Builder withLastDrugTestDate(String lastDrugTestDate) {
			this.lastDrugTestDate = lastDrugTestDate;
			return this;
		}

		@JsonProperty("lastAssignedWorkDate")
		public Builder withLastAssignedWorkDate(String lastAssignedWorkDate) {
			this.lastAssignedWorkDate = lastAssignedWorkDate;
			return this;
		}

		@JsonProperty("email")
		public Builder withEmail(String email) {
			this.email = email;
			return this;
		}

		@JsonProperty("workPhone")
		public Builder withWorkPhone(String workPhone) {
			this.workPhone = workPhone;
			return this;
		}

		@JsonProperty("mobilePhone")
		public Builder withMobilePhone(String mobilePhone) {
			this.mobilePhone = mobilePhone;
			return this;
		}

		@JsonProperty("address")
		public Builder withaddress(Address.Builder addressBuilder) {
			this.address = address;
			return this;
		}

		@JsonProperty("smallAvatarAssetUri")
		public Builder withSmallAvatarAssetUri(String smallAvatarAssetUri) {
			this.smallAvatarAssetUri = smallAvatarAssetUri;
			return this;
		}

		@JsonProperty("skillNames")
		public Builder withSkillNames(List<String> skillNames) {
			this.skillNames = skillNames;
			return this;
		}

		@JsonProperty("scoreCard")
		public Builder withScoreCard(ScoreCard.Builder scoreCardBuilder) {
			this.scoreCard = scoreCard;
			return this;
		}

		@JsonProperty("toolNames")
		public Builder withToolNames(List<String> toolNames) {
			this.toolNames = toolNames;
			return this;
		}

		public WorkersSearchResult build() {
			return new WorkersSearchResult(this);
		}

		public Company.Builder getCompany() {
			return company;
		}

		public ScoreCard.Builder getScoreCard() {
			return scoreCard;
		}

		public Address.Builder getAddress() {
			return address;
		}
	}
}
