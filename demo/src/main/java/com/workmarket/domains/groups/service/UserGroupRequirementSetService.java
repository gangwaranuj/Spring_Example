package com.workmarket.domains.groups.service;

import com.workmarket.domains.model.requirementset.RequirementSet;
import com.workmarket.domains.model.requirementset.agreement.AgreementRequirement;
import com.workmarket.domains.model.requirementset.availability.AvailabilityRequirement;
import com.workmarket.domains.model.requirementset.backgroundcheck.BackgroundCheckRequirement;
import com.workmarket.domains.model.requirementset.certification.CertificationRequirement;
import com.workmarket.domains.model.requirementset.companytype.CompanyTypeRequirement;
import com.workmarket.domains.model.requirementset.country.CountryRequirement;
import com.workmarket.domains.model.requirementset.document.DocumentRequirement;
import com.workmarket.domains.model.requirementset.drugtest.DrugTestRequirement;
import com.workmarket.domains.model.requirementset.industry.IndustryRequirement;
import com.workmarket.domains.model.requirementset.insurance.InsuranceRequirement;
import com.workmarket.domains.model.requirementset.license.LicenseRequirement;
import com.workmarket.domains.model.requirementset.profilevideo.ProfileVideoRequirement;
import com.workmarket.domains.model.requirementset.rating.RatingRequirement;
import com.workmarket.domains.model.requirementset.resourcetype.ResourceTypeRequirement;
import com.workmarket.domains.model.requirementset.test.TestRequirement;
import com.workmarket.domains.model.requirementset.traveldistance.TravelDistanceRequirement;

import java.math.BigDecimal;
import java.util.List;

public interface UserGroupRequirementSetService {
	void addAgreementRequirement(Long userGroupId, Long contractId);
	AgreementRequirement getAgreementRequirement(Long userGroupId, Long agreementId);
	void removeAgreementRequirement(Long userGroupId, Long contractId);

	void addAvailabilityRequirement(Long userGroupId, Integer dayOfWeek, String fromTime, String toTime);
	AvailabilityRequirement getAvailabilityRequirement(Long userGroupId, Integer dayOfWeek);
	void removeAvailabilityRequirement(Long userGroupId, int dayOfWeek);

	void addBackgroundCheckRequirement(Long userGroupId);
	BackgroundCheckRequirement getBackgroundCheckRequirement(Long userGroupId);
	void removeBackgroundCheckRequirement(Long userGroupId);

	void addCertificationRequirement(Long userGroupId, Long certificationId, boolean notifyOnExpiry, boolean removeMembershipOnExpiry);
	CertificationRequirement getCertificationRequirement(Long userGroupId, Long certificationId);
	void removeCertificationRequirement(Long userGroupId, Long certificationId);

	void addCountryRequirement(Long userGroupId, List<String> countryIds);
	CountryRequirement getCountryRequirement(Long userGroupId, String countryId);
	void removeCountryRequirement(Long userGroupId, String countryId);

	void addCompanyTypeRequirement(Long userGroupId, Long companyTypeId);
	CompanyTypeRequirement getCompanyTypeRequirement(Long userGroupId);
	void removeCompanyTypeRequirement(Long userGroupId);

	void addDocumentRequirement(Long userGroupId, Long documentId, Boolean requiresExpirationDate);
	DocumentRequirement getDocumentRequirement(Long userGroupId, Long documentId);
	void removeDocumentRequirement(Long userGroupId, Long documentId);

	void addDrugTestRequirement(Long userGroupId);
	DrugTestRequirement getDrugTestRequirement(Long userGroupId);
	void removeDrugTestRequirement(Long userGroupId);

	void addIndustryRequirement(Long userGroupId, Long industryId);
	IndustryRequirement getIndustryRequirement(Long userGroupId, Long industryId);
	void removeIndustryRequirement(Long userGroupId, Long industryId);

	void addInsuranceRequirement(Long userGroupId, Long insuranceId, BigDecimal minimumCoverageAmount, boolean notifyOnExpiry, boolean removeMembershipOnExpiry);
	InsuranceRequirement getInsuranceRequirement(Long userGroupId, Long insuranceId);
	void removeInsuranceRequirement(Long userGroupId, Long insuranceId);

	void addLicenseRequirement(Long userGroupId, Long licenseId, boolean notifyOnExpiry, boolean removeMembershipOnExpiry);
	LicenseRequirement getLicenseRequirement(Long userGroupId, Long licenseId);
	void removeLicenseRequirement(Long userGroupId, Long licenseId);

	void addProfileVideoRequirement(Long userGroupId);
	ProfileVideoRequirement getProfileVideoRequirement(Long userGroupId);
	void removeProfileVideoRequirement(Long userGroupId);

	void addRatingRequirement(Long userGroupId, Integer rating);
	RatingRequirement getRatingRequirement(Long userGroupId, Integer rating);
	void removeRatingRequirement(Long userGroupId);

	void addResourceTypeRequirement(Long userGroupId, Long resourceTypeId);
	ResourceTypeRequirement getResourceTypeRequirement(Long userGroupId, Long resourceTypeId);
	void removeResourceTypeRequirement(Long userGroupId, Long resourceTypeId);

	void addTestRequirement(Long userGroupId, Long testId);
	TestRequirement getTestRequirement(Long userGroupId, Long testId);
	void removeTestRequirement(Long userGroupId, Long testId);

	void addTravelDistanceRequirement(Long userGroupId, String address, Long distance);
	TravelDistanceRequirement getTravelDistanceRequirement(Long userGroupId, String address, Long distance);
	void removeTravelDistanceRequirement(Long userGroupId, String address, Long distance);

	boolean userGroupHasRequirements(Long userGroupId);

	boolean userGroupHasAgreementRequirements(Long userGroupId);

	List<Long> findUserGroupRequiredAgreementIds(Long groupId);

	RequirementSet findOrCreateRequirementSetByUserGroupId(Long groupId);

	List<Long> findUserGroupsRequiredInsuranceIds(long groupId);

	List<Long> findUserGroupsRequiredCertificationIds(long groupId);

	List<Long> findUserGroupsRequiredLicenseIds(long groupId);

	List<Long> findUserGroupRequiredIndustryIds(Long groupId);
}
