package com.workmarket.api.v2.employer.search.worker.model;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.workmarket.api.v2.employer.search.common.model.LongFilter;
import com.workmarket.api.v2.employer.search.common.model.StringFilter;

import java.util.List;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel("WorkerFilters")
@JsonDeserialize(builder = WorkerFilters.Builder.class)
public class WorkerFilters {
	private final List<LongFilter> assessments;
	private final List<StringFilter> avatars;
	private final List<LongFilter> certifications;
	private final List<LongFilter> companies;
	private final List<StringFilter> companyTypes;
	private final List<StringFilter> countries;
	private final List<LongFilter> industries;
	private final List<StringFilter> lanes;
	private final List<LongFilter> licenses;
	private final List<LongFilter> groups;
	private final List<LongFilter> sharedGroups;
	private final List<StringFilter> verifications;

	private WorkerFilters(final Builder builder) {
		this.countries = builder.countriesBuilder.build();
		this.assessments = builder.assessmentsBuilder.build();
		this.avatars = builder.avatarsBuilder.build();
		this.certifications = builder.certificationsBuilder.build();
		this.companies = builder.companiesBuilder.build();
		this.companyTypes = builder.companyTypesBuilder.build();
		this.industries = builder.industriesBuilder.build();
		this.lanes = builder.lanesBuilder.build();
		this.licenses = builder.licensesBuilder.build();
		this.groups = builder.groupsBuilder.build();
		this.sharedGroups = builder.sharedGroupsBuilder.build();
		this.verifications = builder.verificationsBuilder.build();
	}

	/**
	 * Gets the countries.
	 *
	 * @return ImmutableList The countries
	 */

	@ApiModelProperty(name = "countries")
	@JsonProperty("countries")
	public List<StringFilter> getCountries() {
		return countries;
	}
	@ApiModelProperty(name = "assessments")
	@JsonProperty("assessments")
	public List<LongFilter> getAssessments() {
		return assessments;
	}

	@ApiModelProperty(name = "avatars")
	@JsonProperty("avatars")
	public List<StringFilter> getAvatars() {
		return avatars;
	}

	@ApiModelProperty(name = "certifications")
	@JsonProperty("certifications")
	public List<LongFilter> getCertifications() {
		return certifications;
	}

	@ApiModelProperty(name = "companies")
	@JsonProperty("companies")
	public List<LongFilter> getCompanies() {
		return companies;
	}

	@ApiModelProperty(name = "companyTypes")
	@JsonProperty("companyTypes")
	public List<StringFilter> getCompanyTypes() {
		return companyTypes;
	}

	@ApiModelProperty(name = "industries")
	@JsonProperty("industries")
	public List<LongFilter> getIndustries() {
		return industries;
	}

	@ApiModelProperty(name = "lanes")
	@JsonProperty("lanes")
	public List<StringFilter> getLanes() {
		return lanes;
	}

	@ApiModelProperty(name = "licenses")
	@JsonProperty("licenses")
	public List<LongFilter> getLicenses() {
		return licenses;
	}

	@ApiModelProperty(name = "groups")
	@JsonProperty("groups")
	public List<LongFilter> getGroups() {
		return groups;
	}

	@ApiModelProperty(name = "sharedGroups")
	@JsonProperty("sharedGroups")
	public List<LongFilter> getSharedGroups() {
		return sharedGroups;
	}

	@ApiModelProperty(name = "verifications")
	@JsonProperty("verifications")
	public List<StringFilter> getVerifications() {
		return verifications;
	}



	public static class Builder {
		private ImmutableList.Builder<LongFilter> assessmentsBuilder = new ImmutableList.Builder<>();
		private ImmutableList.Builder<StringFilter> avatarsBuilder = new ImmutableList.Builder<>();
		private ImmutableList.Builder<LongFilter> certificationsBuilder = new ImmutableList.Builder<>();
		private ImmutableList.Builder<LongFilter> companiesBuilder = new ImmutableList.Builder<>();
		private ImmutableList.Builder<StringFilter> companyTypesBuilder = new ImmutableList.Builder<>();
		private ImmutableList.Builder<StringFilter> countriesBuilder = new ImmutableList.Builder<>();
		private ImmutableList.Builder<LongFilter> industriesBuilder = new ImmutableList.Builder<>();
		private ImmutableList.Builder<StringFilter> lanesBuilder = new ImmutableList.Builder<>();
		private ImmutableList.Builder<LongFilter> licensesBuilder = new ImmutableList.Builder<>();
		private ImmutableList.Builder<LongFilter> groupsBuilder = new ImmutableList.Builder<>();
		private ImmutableList.Builder<LongFilter> sharedGroupsBuilder = new ImmutableList.Builder<>();
		private ImmutableList.Builder<StringFilter> verificationsBuilder = new ImmutableList.Builder<>();

		public Builder() {

		}
		public Builder(final WorkerFilters workerFilters) {
			this.countriesBuilder.addAll(workerFilters.countries);
			this.assessmentsBuilder.addAll(workerFilters.assessments);
			this.avatarsBuilder.addAll(workerFilters.avatars);
			this.certificationsBuilder.addAll(workerFilters.certifications);
			this.companiesBuilder.addAll(workerFilters.companies);
			this.companyTypesBuilder.addAll(workerFilters.companyTypes);
			this.industriesBuilder.addAll(workerFilters.industries);
			this.lanesBuilder.addAll(workerFilters.lanes);
			this.licensesBuilder.addAll(workerFilters.licenses);
			this.groupsBuilder.addAll(workerFilters.groups);
			this.sharedGroupsBuilder.addAll(workerFilters.sharedGroups);
			this.verificationsBuilder.addAll(workerFilters.verifications);

		}

		public Builder addCountry(final StringFilter country) {
			countriesBuilder.add(country);
			return this;
		}
		public Builder addCountries(final List<StringFilter> countries) {
			countriesBuilder.addAll(countries);
			return this;
		}

		@JsonProperty("countries") public Builder setCountries(final List<StringFilter> countries) {
			countriesBuilder = new ImmutableList.Builder<StringFilter>();
			countriesBuilder.addAll(countries);
			return this;
		}

		public Builder addAssessment(final LongFilter assessment) {
			assessmentsBuilder.add(assessment);
			return this;
		}
		public Builder addAssessments(final List<LongFilter> assessments) {
			assessmentsBuilder.addAll(assessments);
			return this;
		}
		@JsonProperty("assessments") public Builder setAssessments(final List<LongFilter> assessments) {
			assessmentsBuilder = new ImmutableList.Builder<LongFilter>();
			assessmentsBuilder.addAll(assessments);
			return this;
		}

		public Builder addAvatar(final StringFilter avatar) {
			avatarsBuilder.add(avatar);
			return this;
		}
		public Builder addAvatars(final List<StringFilter> avatars) {
			avatarsBuilder.addAll(avatars);
			return this;
		}
		@JsonProperty("avatars") public Builder setAvatars(final List<StringFilter> avatars) {
			avatarsBuilder = new ImmutableList.Builder<>();
			avatarsBuilder.addAll(avatars);
			return this;
		}

		public Builder addCertification(final LongFilter certification) {
			certificationsBuilder.add(certification);
			return this;
		}
		public Builder addCertifications(final List<LongFilter> certifications) {
			certificationsBuilder.addAll(certifications);
			return this;
		}
		@JsonProperty("certifications") public Builder setCertifications(final List<LongFilter> certifications) {
			certificationsBuilder = new ImmutableList.Builder<LongFilter>();
			certificationsBuilder.addAll(certifications);
			return this;
		}

		public Builder addCompany(final LongFilter company) {
			companiesBuilder.add(company);
			return this;
		}
		public Builder addCompanies(final List<LongFilter> companies) {
			companiesBuilder.addAll(companies);
			return this;
		}
		@JsonProperty("companies") public Builder setCompanies(final List<LongFilter> companies) {
			companiesBuilder = new ImmutableList.Builder<LongFilter>();
			companiesBuilder.addAll(companies);
			return this;
		}

		public Builder addCompanyType(final StringFilter companyType) {
			companyTypesBuilder.add(companyType);
			return this;
		}
		public Builder addCompanyTypes(final List<StringFilter> companyTypes) {
			companyTypesBuilder.addAll(companyTypes);
			return this;
		}
		@JsonProperty("companyTypes") public Builder setCompanyTypes(final List<StringFilter> companyTypes) {
			companyTypesBuilder = new ImmutableList.Builder<StringFilter>();
			companyTypesBuilder.addAll(companyTypes);
			return this;
		}

		public Builder addIndustry(final LongFilter industry) {
			industriesBuilder.add(industry);
			return this;
		}
		public Builder addIndustries(final List<LongFilter> industries) {
			industriesBuilder.addAll(industries);
			return this;
		}
		@JsonProperty("industries") public Builder setIndustries(final List<LongFilter> industries) {
			industriesBuilder = new ImmutableList.Builder<LongFilter>();
			industriesBuilder.addAll(industries);
			return this;
		}

		public Builder addLane(final StringFilter lane) {
			lanesBuilder.add(lane);
			return this;
		}
		public Builder addLanes(final List<StringFilter> lanes) {
			lanesBuilder.addAll(lanes);
			return this;
		}
		@JsonProperty("lanes") public Builder setLanes(final List<StringFilter> lanes) {
			lanesBuilder = new ImmutableList.Builder<StringFilter>();
			lanesBuilder.addAll(lanes);
			return this;
		}

		public Builder addLicense(final LongFilter license) {
			licensesBuilder.add(license);
			return this;
		}
		public Builder addLicenses(final List<LongFilter> licenses) {
			licensesBuilder.addAll(licenses);
			return this;
		}
		@JsonProperty("licenses") public Builder setLicenses(final List<LongFilter> licenses) {
			licensesBuilder = new ImmutableList.Builder<LongFilter>();
			licensesBuilder.addAll(licenses);
			return this;
		}

		public Builder addGroup(final LongFilter group) {
			groupsBuilder.add(group);
			return this;
		}
		public Builder addGroups(final List<LongFilter> groups) {
			groupsBuilder.addAll(groups	);
			return this;
		}
		@JsonProperty("groups") public Builder setGroups(final List<LongFilter> groups) {
			groupsBuilder = new ImmutableList.Builder<LongFilter>();
			groupsBuilder.addAll(groups);
			return this;
		}

		public Builder addSharedGroup(final LongFilter sharedGroup) {
			sharedGroupsBuilder.add(sharedGroup);
			return this;
		}
		public Builder addSharedGroups(final List<LongFilter> sharedGroups) {
			sharedGroupsBuilder.addAll(sharedGroups);
			return this;
		}
		@JsonProperty("sharedGroups") public Builder setSharedGroups(final List<LongFilter> sharedGroups) {
			sharedGroupsBuilder = new ImmutableList.Builder<LongFilter>();
			sharedGroupsBuilder.addAll(sharedGroups);
			return this;
		}

		public Builder addVerification(final StringFilter verification) {
			verificationsBuilder.add(verification);
			return this;
		}
		public Builder addVerifications(final List<StringFilter> verifications) {
			verificationsBuilder.addAll(verifications);
			return this;
		}
		@JsonProperty("verifications") public Builder setVerifications(final List<StringFilter> verifications) {
			verificationsBuilder = new ImmutableList.Builder<StringFilter>();
			verificationsBuilder.addAll(verifications);
			return this;
		}

		public WorkerFilters build() {
			return new WorkerFilters(this);
		}

	}
}
