package com.workmarket.api.v2.employer.search.worker.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.google.common.collect.ImmutableSet;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.workmarket.api.v2.employer.search.common.model.BaseDTO;
import com.workmarket.api.v2.employer.search.common.model.SortDirection;
import com.workmarket.api.v2.employer.search.common.model.SortType;

import java.util.Set;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * Value object used to pass our search criteria from the REST body in to our service
 * layer.
 */
@ApiModel("WorkerSearchRequest")
@JsonInclude(JsonInclude.Include.NON_EMPTY)
@JsonDeserialize(builder = WorkerSearchRequestDTO.Builder.class)
public class WorkerSearchRequestDTO extends BaseDTO {

	private final Long offset;
	private final Long limit;
	private final SortType sortType;
	private final SortDirection sortDirection;

	private final String work_number;

	private final String keyword;
	private final String address;
	private final Integer radius;
	private final Set<String> countries;
	private final Set<Integer> groups;
	private final Set<Integer> sharedGroups;

	private final Set<LaneType> lanes;
	private final Set<VerificationType> verifications;
	private final Set<Integer> assessments;

	private final Set<Integer> certifications;
	private final Set<String> licenses;
	private final Set<Integer> industries;
	private final Set<Integer> companies;
	private final Set<CompanyType> companyTypes;
	private final Integer satisfactionRate;
	private final Integer onTimePercentage;
	private final Integer deliverableOnTimePercentage;
	private final Integer workersCompCoverage;
	private final Integer generalLiabilityCoverage;
	private final Integer errorsAndOmissionsCoverage;
	private final Integer automobileCoverage;
	private final Integer contractorsCoverage;
	private final Integer commercialGeneralLiabilityCoverage;
	private final Integer businessLiabilityCoverage;
	private final Boolean avatar;

	/**
     * Constructor.
     */
	private WorkerSearchRequestDTO(final Builder builder) {
		this.offset = builder.offset;
		this.limit = builder.limit;
		this.sortType = builder.sortType;
		this.sortDirection = builder.sortDirection;
		this.work_number = builder.work_number;
		this.keyword = builder.keyword;
		this.address = builder.address;
		this.radius = builder.radius;
		this.countries = builder.countriesBuilder.build();
		this.groups = builder.groupsBuilder.build();
		this.sharedGroups = builder.sharedGroupsBuilder.build();
		this.lanes = builder.lanesBuilder.build();
		this.verifications = builder.verificationsBuilder.build();
		this.assessments = builder.assessmentsBuilder.build();
		this.certifications = builder.certificationsBuilder.build();
		this.licenses = builder.licensesBuilder.build();
		this.industries = builder.industriesBuilder.build();
		this.companies = builder.companiesBuilder.build();
		this.companyTypes = builder.companyTypesBuilder.build();
		this.satisfactionRate = builder.satisfactionRate;
		this.onTimePercentage = builder.onTimePercentage;
		this.deliverableOnTimePercentage = builder.deliverableOnTimePercentage;
		this.workersCompCoverage = builder.workersCompCoverage;
		this.generalLiabilityCoverage = builder.generalLiabilityCoverage;
		this.errorsAndOmissionsCoverage = builder.errorsAndOmissionsCoverage;
		this.automobileCoverage = builder.automobileCoverage;
		this.contractorsCoverage = builder.contractorsCoverage;
		this.commercialGeneralLiabilityCoverage = builder.commercialGeneralLiabilityCoverage;
		this.businessLiabilityCoverage = builder.businessLiabilityCoverage;
		this.avatar = builder.avatar;

	}


	@ApiModelProperty(name = "offset")
	@JsonProperty("offset")
	public Long getOffset() {
		return offset;
	}

	@ApiModelProperty(name = "limit")
	@JsonProperty("limit")
	public Long getLimit() {
		return limit;
	}

	@ApiModelProperty(name = "sortType")
	@JsonProperty("sortType")
	public SortType getSortType() {
		return sortType;
	}

	@ApiModelProperty(name = "sortDirection")
	@JsonProperty("sortDirection")
	public SortDirection getSortDirection() {
		return sortDirection;
	}

	@ApiModelProperty(name = "work_number")
	@JsonProperty("work_number")
	public String getWork_number() {
		return work_number;
	}

	@ApiModelProperty(name = "keyword")
	@JsonProperty("keyword")
	public String getKeyword() {
		return keyword;
	}

	@ApiModelProperty(name = "address")
	@JsonProperty("address")
	public String getAddress() {
		return address;
	}

	@ApiModelProperty(name = "radius")
	@JsonProperty("radius")
	public Integer getRadius() {
		return radius;
	}

	@ApiModelProperty(name = "countries")
	@JsonProperty("countries")
	public Set<String> getCountries() {
		return countries;
	}

	@ApiModelProperty(name = "groups")
	@JsonProperty("groups")
	public Set<Integer> getGroups() {
		return groups;
	}

	@ApiModelProperty(name = "sharedGroups")
	@JsonProperty("sharedGroups")
	public Set<Integer> getSharedGroups() {
		return sharedGroups;
	}

	@ApiModelProperty(name = "lanes")
	@JsonProperty("lanes")
	public Set<LaneType> getLanes() {
		return lanes;
	}

	@ApiModelProperty(name = "verifications")
	@JsonProperty("verifications")
	public Set<VerificationType> getVerifications() {
		return verifications;
	}

	@ApiModelProperty(name = "assessments")
	@JsonProperty("assessments")
	public Set<Integer> getAssessments() {
		return assessments;
	}

	@ApiModelProperty(name = "certifications")
	@JsonProperty("certifications")
	public Set<Integer> getCertifications() {
		return certifications;
	}

	@ApiModelProperty(name = "licenses")
	@JsonProperty("licenses")
	public Set<String> getLicenses() {
		return licenses;
	}

	@ApiModelProperty(name = "industries")
	@JsonProperty("industries")
	public Set<Integer> getIndustries() {
		return industries;
	}

	@ApiModelProperty(name = "companies")
	@JsonProperty("companies")
	public Set<Integer> getCompanies() {
		return companies;
	}

	@ApiModelProperty(name = "companyTypes")
	@JsonProperty("companyTypes")
	public Set<CompanyType> getCompanyTypes() {
		return companyTypes;
	}

	@ApiModelProperty(name = "satisfactionRate")
	@JsonProperty("satisfactionRate")
	public Integer getSatisfactionRate() {
		return satisfactionRate;
	}

	@ApiModelProperty(name = "onTimePercentage")
	@JsonProperty("onTimePercentage")
	public Integer getOnTimePercentage() {
		return onTimePercentage;
	}

	@ApiModelProperty(name = "deliverableOnTimePercentage")
	@JsonProperty("deliverableOnTimePercentage")
	public Integer getDeliverableOnTimePercentage() {
		return deliverableOnTimePercentage;
	}

	@ApiModelProperty(name = "workersCompCoverage")
	@JsonProperty("workersCompCoverage")
	public Integer getWorkersCompCoverage() {
		return workersCompCoverage;
	}

	@ApiModelProperty(name = "generalLiabilityCoverage")
	@JsonProperty("generalLiabilityCoverage")
	public Integer getGeneralLiabilityCoverage() {
		return generalLiabilityCoverage;
	}

	@ApiModelProperty(name = "errorsAndOmissionsCoverage")
	@JsonProperty("errorsAndOmissionsCoverage")
	public Integer getErrorsAndOmissionsCoverage() {
		return errorsAndOmissionsCoverage;
	}

	@ApiModelProperty(name = "automobileCoverage")
	@JsonProperty("automobileCoverage")
	public Integer getAutomobileCoverage() {
		return automobileCoverage;
	}

	@ApiModelProperty(name = "contractorsCoverage")
	@JsonProperty("contractorsCoverage")
	public Integer getContractorsCoverage() {
		return contractorsCoverage;
	}

	@ApiModelProperty(name = "commercialGeneralLiabilityCoverage")
	@JsonProperty("commercialGeneralLiabilityCoverage")
	public Integer getCommercialGeneralLiabilityCoverage() {
		return commercialGeneralLiabilityCoverage;
	}

	@ApiModelProperty(name = "businessLiabilityCoverage")
	@JsonProperty("businessLiabilityCoverage")
	public Integer getBusinessLiabilityCoverage() {
		return businessLiabilityCoverage;
	}

	@ApiModelProperty(name = "avatar")
	@JsonProperty("avatar")
	public Boolean getAvatar() {
		return avatar;
	}

	public static class Builder {
		private Long offset;
		private Long limit;
		private SortType sortType;
		private SortDirection sortDirection;
		private String work_number;
		private String keyword;
		private String address;
		private Integer radius;
		private ImmutableSet.Builder<String> countriesBuilder = new ImmutableSet.Builder<>();
		private ImmutableSet.Builder<Integer> groupsBuilder = new ImmutableSet.Builder<>();
		private ImmutableSet.Builder<Integer> sharedGroupsBuilder = new ImmutableSet.Builder<>();
		private ImmutableSet.Builder<LaneType> lanesBuilder = new ImmutableSet.Builder<>();
		private ImmutableSet.Builder<VerificationType> verificationsBuilder = new ImmutableSet.Builder<>();
		private ImmutableSet.Builder<Integer> assessmentsBuilder = new ImmutableSet.Builder<>();
		private ImmutableSet.Builder<Integer> certificationsBuilder = new ImmutableSet.Builder<>();
		private ImmutableSet.Builder<String> licensesBuilder = new ImmutableSet.Builder<>();
		private ImmutableSet.Builder<Integer> industriesBuilder = new ImmutableSet.Builder<>();
		private ImmutableSet.Builder<Integer> companiesBuilder = new ImmutableSet.Builder<>();
		private ImmutableSet.Builder<CompanyType> companyTypesBuilder = new ImmutableSet.Builder<>();
		private Integer satisfactionRate;
		private Integer onTimePercentage;
		private Integer deliverableOnTimePercentage;
		private Integer workersCompCoverage;
		private Integer generalLiabilityCoverage;
		private Integer errorsAndOmissionsCoverage;
		private Integer automobileCoverage;
		private Integer contractorsCoverage;
		private Integer commercialGeneralLiabilityCoverage;
		private Integer businessLiabilityCoverage;
		private Boolean avatar;

		public Builder() {

		}

		public Builder(final WorkerSearchRequestDTO workerSearchRequestDTO) {
			this.offset = workerSearchRequestDTO.offset;
			this.limit = workerSearchRequestDTO.limit;
			this.sortType = workerSearchRequestDTO.sortType;
			this.sortDirection = workerSearchRequestDTO.sortDirection;
			this.work_number = workerSearchRequestDTO.work_number;
			this.keyword = workerSearchRequestDTO.keyword;
			this.address = workerSearchRequestDTO.address;
			this.radius = workerSearchRequestDTO.radius;
			this.countriesBuilder.addAll(workerSearchRequestDTO.countries);
			this.groupsBuilder.addAll(workerSearchRequestDTO.groups);
			this.sharedGroupsBuilder.addAll(workerSearchRequestDTO.sharedGroups);
			this.lanesBuilder.addAll(workerSearchRequestDTO.lanes);
			this.verificationsBuilder.addAll(workerSearchRequestDTO.verifications);
			this.assessmentsBuilder.addAll(workerSearchRequestDTO.assessments);
			this.certificationsBuilder.addAll(workerSearchRequestDTO.certifications);
			this.licensesBuilder.addAll(workerSearchRequestDTO.licenses);
			this.industriesBuilder.addAll(workerSearchRequestDTO.industries);
			this.companiesBuilder.addAll(workerSearchRequestDTO.companies);
			this.companyTypesBuilder.addAll(workerSearchRequestDTO.companyTypes);
			this.satisfactionRate = workerSearchRequestDTO.satisfactionRate;
			this.onTimePercentage = workerSearchRequestDTO.onTimePercentage;
			this.deliverableOnTimePercentage = workerSearchRequestDTO.deliverableOnTimePercentage;
			this.workersCompCoverage = workerSearchRequestDTO.workersCompCoverage;
			this.generalLiabilityCoverage = workerSearchRequestDTO.generalLiabilityCoverage;
			this.errorsAndOmissionsCoverage = workerSearchRequestDTO.errorsAndOmissionsCoverage;
			this.automobileCoverage = workerSearchRequestDTO.automobileCoverage;
			this.contractorsCoverage = workerSearchRequestDTO.contractorsCoverage;
			this.commercialGeneralLiabilityCoverage = workerSearchRequestDTO.commercialGeneralLiabilityCoverage;
			this.businessLiabilityCoverage = workerSearchRequestDTO.businessLiabilityCoverage;
			this.avatar = workerSearchRequestDTO.avatar;
		}

		@JsonProperty("offset") public Builder setOffset(final Long offset) {
			this.offset = offset;
			return this;
		}

		@JsonProperty("limit") public Builder setLimit(final Long limit) {
			this.limit = limit;
			return this;
		}

		@JsonProperty("sortType") public Builder setSortType(final SortType sortType) {
			this.sortType = sortType;
			return this;
		}

		@JsonProperty("sortDirection") public Builder setSortDirection(final SortDirection sortDirection) {
			this.sortDirection = sortDirection;
			return this;
		}

		@JsonProperty("work_number") public Builder setWorkNumber(final String work_number) {
			this.work_number = work_number;
			return this;
		}

		@JsonProperty("keyword") public Builder setKeyword(final String keyword) {
			this.keyword = keyword;
			return this;
		}

		@JsonProperty("address") public Builder setAddress(final String address) {
			this.address = address;
			return this;
		}

		@JsonProperty("radius") public Builder setRadius(final Integer radius) {
			this.radius = radius;
			return this;
		}

		public Builder addCountry(final String country) {
			this.countriesBuilder.add(country);
			return this;
		}
		public Builder addCountries(final Set<String> countries) {
			this.countriesBuilder.addAll(countries);
			return this;
		}
		@JsonProperty("countries") public Builder setCountries(final Set<String> countries) {
			this.countriesBuilder = new ImmutableSet.Builder<>();
			this.countriesBuilder.addAll(countries);
			return this;
		}
		public Builder addGroup(final Integer group) {
			this.groupsBuilder.add(group);
			return this;
		}
		public Builder addGroups(final Set<Integer> groups) {
			this.groupsBuilder.addAll(groups);
			return this;
		}
		@JsonProperty("groups") public Builder setGroups(final Set<Integer> groups) {
			this.groupsBuilder = new ImmutableSet.Builder<>();
			this.groupsBuilder.addAll(groups);
			return this;
		}


		public Builder addSharedGroup(final Integer sharedGroup) {
			this.sharedGroupsBuilder.add(sharedGroup);
			return this;
		}
		public Builder addSharedGroups(final Set<Integer> sharedGroups) {
			this.sharedGroupsBuilder.addAll(sharedGroups);
			return this;
		}
		@JsonProperty("sharedGroups") public Builder setSharedGroups(final Set<Integer> sharedGroups) {
			this.sharedGroupsBuilder = new ImmutableSet.Builder<>();
			this.sharedGroupsBuilder.addAll(sharedGroups);
			return this;
		}


		public Builder addLane(final LaneType lane) {
			this.lanesBuilder.add(lane);
			return this;
		}
		public Builder addLanes(final Set<LaneType> lanes) {
			this.lanesBuilder.addAll(lanes);
			return this;
		}
		@JsonProperty("lanes") public Builder setLanes(final Set<LaneType> lanes) {
			this.lanesBuilder = new ImmutableSet.Builder<>();
			this.lanesBuilder.addAll(lanes);
			return this;
		}


		public Builder addVerification(final VerificationType verification) {
			this.verificationsBuilder.add(verification);
			return this;
		}
		public Builder addVerifications(final Set<VerificationType> verifications) {
			this.verificationsBuilder.addAll(verifications);
			return this;
		}
		@JsonProperty("verifications") public Builder setVerifications(final Set<VerificationType> verifications) {
			this.verificationsBuilder = new ImmutableSet.Builder<>();
			this.verificationsBuilder.addAll(verifications);
			return this;
		}


		public Builder addAssessment(final Integer assessment) {
			this.assessmentsBuilder.add(assessment);
			return this;
		}
		public Builder addAssessments(final Set<Integer> assessments) {
			this.assessmentsBuilder.addAll(assessments);
			return this;
		}
		@JsonProperty("assessments") public Builder setAssessments(final Set<Integer> assessments) {
			this.assessmentsBuilder = new ImmutableSet.Builder<>();
			this.assessmentsBuilder.addAll(assessments);
			return this;
		}


		public Builder addCertification(final Integer certification) {
			this.certificationsBuilder.add(certification);
			return this;
		}
		public Builder addCertifications(final Set<Integer> certifications) {
			this.certificationsBuilder.addAll(certifications);
			return this;
		}
		@JsonProperty("certifications") public Builder setCertifications(final Set<Integer> certifications) {
			this.certificationsBuilder = new ImmutableSet.Builder<>();
			this.certificationsBuilder.addAll(certifications);
			return this;
		}


		public Builder addLicense(final String license) {
			this.licensesBuilder.add(license);
			return this;
		}
		public Builder addLicenses(final Set<String> licenses) {
			this.licensesBuilder.addAll(licenses);
			return this;
		}
		@JsonProperty("licenses") public Builder setLicenses(final Set<String> licenses) {
			this.licensesBuilder = new ImmutableSet.Builder<>();
			this.licensesBuilder.addAll(licenses);
			return this;
		}


		public Builder addIndustry(final Integer industry) {
			this.industriesBuilder.add(industry);
			return this;
		}
		public Builder addIndustries(final Set<Integer> industries) {
			this.industriesBuilder.addAll(industries);
			return this;
		}
		@JsonProperty("industries") public Builder setIndustries(final Set<Integer> industries) {
			this.industriesBuilder = new ImmutableSet.Builder<>();
			this.industriesBuilder.addAll(industries);
			return this;
		}


		public Builder addCompany(final Integer company) {
			this.companiesBuilder.add(company);
			return this;
		}
		public Builder addCompanies(final Set<Integer> companies) {
			this.companiesBuilder.addAll(companies);
			return this;
		}
		@JsonProperty("companies") public Builder setCompanies(final Set<Integer> companies) {
			this.companiesBuilder = new ImmutableSet.Builder<>();
			this.companiesBuilder.addAll(companies);
			return this;
		}


		public Builder addCompanyType(final CompanyType companyType) {
			this.companyTypesBuilder.add(companyType);
			return this;
		}
		public Builder addCompanyTypes(final Set<CompanyType> companyTypes) {
			this.companyTypesBuilder.addAll(companyTypes);
			return this;
		}
		@JsonProperty("companyTypes") public Builder setCompanyTypes(final Set<CompanyType> companyTypes) {
			this.companyTypesBuilder = new ImmutableSet.Builder<>();
			this.companyTypesBuilder.addAll(companyTypes);
			return this;
		}

		@JsonProperty("satisfactionRate") public Builder setSatisfactionRate(final Integer satisfactionRate) {
			this.satisfactionRate = satisfactionRate;
			return this;
		}

		@JsonProperty("onTimePercentage") public Builder setOnTimePercentage(final Integer onTimePercentage) {
			this.onTimePercentage = onTimePercentage;
			return this;
		}

		@JsonProperty("deliverableOnTimePercentage") public Builder setDeliverableOnTimePercentage(final Integer deliverableOnTimePercentage) {
			this.deliverableOnTimePercentage = deliverableOnTimePercentage;
			return this;
		}

		@JsonProperty("workersCompCoverage") public Builder setWorkersCompCoverage(final Integer workersCompCoverage) {
			this.workersCompCoverage = workersCompCoverage;
			return this;
		}

		@JsonProperty("generalLiabilityCoverage") public Builder setGeneralLiabilityCoverage(final Integer generalLiabilityCoverage) {
			this.generalLiabilityCoverage = generalLiabilityCoverage;
			return this;
		}

		@JsonProperty("errorsAndOmissionsCoverage") public Builder setErrorsAndOmissionsCoverage(final Integer errorsAndOmissionsCoverage) {
			this.errorsAndOmissionsCoverage = errorsAndOmissionsCoverage;
			return this;
		}

		@JsonProperty("automobileCoverage") public Builder setAutomobileCoverage(final Integer automobileCoverage) {
			this.automobileCoverage = automobileCoverage;
			return this;
		}

		@JsonProperty("contractorsCoverage") public Builder setContractorsCoverage(final Integer contractorsCoverage) {
			this.contractorsCoverage = contractorsCoverage;
			return this;
		}

		@JsonProperty("commercialGeneralLiabilityCoverage") public Builder setCommercialGeneralLiabilityCoverage(final Integer commercialGeneralLiabilityCoverage) {
			this.commercialGeneralLiabilityCoverage = commercialGeneralLiabilityCoverage;
			return this;
		}

		@JsonProperty("businessLiabilityCoverage") public Builder setBusinessLiabilityCoverage(final Integer businessLiabilityCoverage) {
			this.businessLiabilityCoverage = businessLiabilityCoverage;
			return this;
		}

		@JsonProperty("avatar") public Builder setAvatar(final Boolean avatar) {
			this.avatar = avatar;
			return this;
		}

		public WorkerSearchRequestDTO build() {
			return new WorkerSearchRequestDTO(this);
		}

	}
}
