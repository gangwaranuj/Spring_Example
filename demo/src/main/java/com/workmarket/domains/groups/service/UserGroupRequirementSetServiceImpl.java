package com.workmarket.domains.groups.service;

import com.google.common.collect.Lists;
import com.workmarket.dao.DAOInterface;
import com.workmarket.dao.requirement.*;
import com.workmarket.domains.groups.dao.UserGroupDAO;
import com.workmarket.domains.groups.model.UserGroup;
import com.workmarket.domains.model.requirementset.AbstractRequirement;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

@Service
public class UserGroupRequirementSetServiceImpl implements UserGroupRequirementSetService {
	public static final String GROUP_TITLE_TEMPLATE = "(Group) %s";
	@Autowired private UserGroupDAO userGroupDAO;
	@Autowired private RequirementSetDAO requirementSetDAO;
	@Autowired private ApplicationContext applicationContext;

	@Override
	public void addAgreementRequirement(Long userGroupId, Long agreementId) {
		addRequirementToGroupBy(AgreementRequirementDAO.class, userGroupId,
			"agreementRequirable", findRequirableById(AgreementRequirableDAO.class, agreementId)
		);
	}

	@Override
	public AgreementRequirement getAgreementRequirement(Long userGroupId, Long agreementId) {
		return getOrInitializeRequirement(AgreementRequirementDAO.class, userGroupId,
			"agreementRequirable", findRequirableById(AgreementRequirableDAO.class, agreementId)
		);
	}

	@Override
	public void removeAgreementRequirement(Long userGroupId, Long agreementId) {
		removeRequirementToGroupBy(AgreementRequirementDAO.class, userGroupId,
			"agreementRequirable", findRequirableById(AgreementRequirableDAO.class, agreementId)
		);
	}

	@Override
	public void addAvailabilityRequirement(Long userGroupId, Integer dayOfWeek, String fromTime, String toTime) {
		// Only one availability per day is allowed
		AvailabilityRequirement requirement = getOrInitializeRequirement(AvailabilityRequirementDAO.class, userGroupId,
			"dayOfWeek", dayOfWeek
		);

		requirement.setFromTime(fromTime);
		requirement.setToTime(toTime);

		saveOrUpdateRequirement(AvailabilityRequirementDAO.class, requirement);
	}

	@Override
	public AvailabilityRequirement getAvailabilityRequirement(Long userGroupId, Integer dayOfWeek) {
		return getOrInitializeRequirement(AvailabilityRequirementDAO.class, userGroupId,
			"dayOfWeek", dayOfWeek
		);
	}

	@Override
	public void removeAvailabilityRequirement(Long userGroupId, int dayOfWeek) {
		removeRequirementToGroupBy(AvailabilityRequirementDAO.class, userGroupId,
			"dayOfWeek", dayOfWeek
		);
	}

	@Override
	public void addBackgroundCheckRequirement(Long userGroupId) {
		addRequirementToGroupBy(BackgroundCheckRequirementDAO.class, userGroupId);
	}

	@Override
	public BackgroundCheckRequirement getBackgroundCheckRequirement(Long userGroupId) {
		return getOrInitializeRequirement(BackgroundCheckRequirementDAO.class, userGroupId);
	}

	@Override
	public void removeBackgroundCheckRequirement(Long userGroupId) {
		removeRequirementToGroupBy(BackgroundCheckRequirementDAO.class, userGroupId);
	}

	@Override
	public void addCertificationRequirement(Long userGroupId, Long certificationId, boolean notifyOnExpiry, boolean removeMembershipOnExpiry) {
		CertificationRequirement requirement = addRequirementToGroupBy(CertificationRequirementDAO.class, userGroupId,
			"certificationRequirable", findRequirableById(CertificationRequirableDAO.class, certificationId)
		);

		requirement.setNotifyOnExpiry(notifyOnExpiry);
		requirement.setRemoveMembershipOnExpiry(removeMembershipOnExpiry);
	}

	@Override
	public CertificationRequirement getCertificationRequirement(Long userGroupId, Long certificationId) {
		return getOrInitializeRequirement(CertificationRequirementDAO.class, userGroupId,
			"certificationRequirable", findRequirableById(CertificationRequirableDAO.class, certificationId)
		);
	}

	@Override
	public void removeCertificationRequirement(Long userGroupId, Long certificationId) {
		removeRequirementToGroupBy(CertificationRequirementDAO.class, userGroupId,
			"certificationRequirable", findRequirableById(CertificationRequirableDAO.class, certificationId)
		);
	}

	@Override
	public void addCountryRequirement(Long userGroupId, List<String> countryIds) {
		for (String countryId : countryIds) {
			addRequirementToGroupBy(CountryRequirementDAO.class, userGroupId,
				"countryRequirable", findRequirableById(CountryRequirableDAO.class, countryId)
			);
		}
	}

	@Override
	public CountryRequirement getCountryRequirement(Long userGroupId, String countryId) {
		return getOrInitializeRequirement(CountryRequirementDAO.class, userGroupId,
			"countryRequirable", findRequirableById(CountryRequirableDAO.class, countryId)
		);
	}

	@Override
	public void removeCountryRequirement(Long userGroupId, String countryId) {
		removeRequirementToGroupBy(CountryRequirementDAO.class, userGroupId,
			"countryRequirable", findRequirableById(CountryRequirableDAO.class, countryId)
		);
	}

	@Override
	public void addCompanyTypeRequirement(Long userGroupId, Long companyTypeId) {
		CompanyTypeRequirement requirement = getOrInitializeRequirement(CompanyTypeRequirementDAO.class, userGroupId);
		requirement.setCompanyTypeId(companyTypeId);
		saveOrUpdateRequirement(CompanyTypeRequirementDAO.class, requirement);
	}

	@Override
	public CompanyTypeRequirement getCompanyTypeRequirement(Long userGroupId) {
		return getOrInitializeRequirement(CompanyTypeRequirementDAO.class, userGroupId);
	}

	@Override
	public void removeCompanyTypeRequirement(Long userGroupId) {
		removeRequirementToGroupBy(CompanyTypeRequirementDAO.class, userGroupId);
	}

	@Override
	public void addDocumentRequirement(Long userGroupId, Long documentId, Boolean requiresExpirationDate) {
		DocumentRequirement requirement = addRequirementToGroupBy(DocumentRequirementDAO.class, userGroupId,
			"documentRequirable", findRequirableById(DocumentRequirableDAO.class, documentId)
		);

		requirement.setRequiresExpirationDate(requiresExpirationDate);
	}

	@Override
	public DocumentRequirement getDocumentRequirement(Long userGroupId, Long documentId) {
		return getOrInitializeRequirement(DocumentRequirementDAO.class, userGroupId,
			"documentRequirable", findRequirableById(DocumentRequirableDAO.class, documentId)
		);
	}

	@Override
	public void removeDocumentRequirement(Long userGroupId, Long documentId) {
		removeRequirementToGroupBy(DocumentRequirementDAO.class, userGroupId,
			"documentRequirable", findRequirableById(DocumentRequirableDAO.class, documentId)
		);
	}

	@Override
	public void addDrugTestRequirement(Long userGroupId) {
		addRequirementToGroupBy(DrugTestRequirementDAO.class, userGroupId);
	}

	@Override
	public DrugTestRequirement getDrugTestRequirement(Long userGroupId) {
		return getOrInitializeRequirement(DrugTestRequirementDAO.class, userGroupId);
	}

	@Override
	public void removeDrugTestRequirement(Long userGroupId) {
		removeRequirementToGroupBy(DrugTestRequirementDAO.class, userGroupId);
	}

	@Override
	public void addIndustryRequirement(Long userGroupId, Long industryId) {
		addRequirementToGroupBy(IndustryRequirementDAO.class, userGroupId,
			"industryRequirable", findRequirableById(IndustryRequirableDAO.class, industryId)
		);
	}

	@Override
	public IndustryRequirement getIndustryRequirement(Long userGroupId, Long industryId) {
		return getOrInitializeRequirement(IndustryRequirementDAO.class, userGroupId,
			"industryRequirable", findRequirableById(IndustryRequirableDAO.class, industryId)
		);
	}

	@Override
	public void removeIndustryRequirement(Long userGroupId, Long industryId) {
		removeRequirementToGroupBy(IndustryRequirementDAO.class, userGroupId,
			"industryRequirable", findRequirableById(IndustryRequirableDAO.class, industryId)
		);
	}

	@Override
	public void addInsuranceRequirement(
		Long userGroupId,
		Long insuranceId,
		BigDecimal minimumCoverageAmount,
		boolean notifyOnExpiry,
		boolean removeMembershipOnExpiry
	) {
		InsuranceRequirement requirement = addRequirementToGroupBy(InsuranceRequirementDAO.class, userGroupId,
			"insuranceRequirable", findRequirableById(InsuranceRequirableDAO.class, insuranceId)
		);

		if (minimumCoverageAmount != null) {
			requirement.setMinimumCoverageAmount(minimumCoverageAmount);
		}
		requirement.setNotifyOnExpiry(notifyOnExpiry);
		requirement.setRemoveMembershipOnExpiry(removeMembershipOnExpiry);
	}

	@Override
	public InsuranceRequirement getInsuranceRequirement(Long userGroupId, Long insuranceId) {
		return getOrInitializeRequirement(InsuranceRequirementDAO.class, userGroupId,
			"insuranceRequirable", findRequirableById(InsuranceRequirableDAO.class, insuranceId)
		);
	}

	@Override
	public void removeInsuranceRequirement(Long userGroupId, Long insuranceId) {
		removeRequirementToGroupBy(InsuranceRequirementDAO.class, userGroupId,
			"insuranceRequirable", findRequirableById(InsuranceRequirableDAO.class, insuranceId)
		);
	}

	@Override
	public void addLicenseRequirement(Long userGroupId, Long licenseId, boolean notifyOnExpiry, boolean removeMembershipOnExpiry) {
		LicenseRequirement requirement = addRequirementToGroupBy(LicenseRequirementDAO.class, userGroupId,
			"licenseRequirable", findRequirableById(LicenseRequirableDAO.class, licenseId)
		);

		requirement.setNotifyOnExpiry(notifyOnExpiry);
		requirement.setRemoveMembershipOnExpiry(removeMembershipOnExpiry);
	}

	@Override
	public LicenseRequirement getLicenseRequirement(Long userGroupId, Long licenseId) {
		return getOrInitializeRequirement(LicenseRequirementDAO.class, userGroupId,
			"licenseRequirable", findRequirableById(LicenseRequirableDAO.class, licenseId)
		);
	}

	@Override
	public void removeLicenseRequirement(Long userGroupId, Long licenseId) {
		removeRequirementToGroupBy(LicenseRequirementDAO.class, userGroupId,
			"licenseRequirable", findRequirableById(LicenseRequirableDAO.class, licenseId)
		);
	}

	@Override
	public void addProfileVideoRequirement(Long userGroupId) {
		addRequirementToGroupBy(ProfileVideoRequirementDAO.class, userGroupId);
	}

	@Override
	public ProfileVideoRequirement getProfileVideoRequirement(Long userGroupId) {
		return getOrInitializeRequirement(ProfileVideoRequirementDAO.class, userGroupId);
	}

	@Override
	public void removeProfileVideoRequirement(Long userGroupId) {
		removeRequirementToGroupBy(ProfileVideoRequirementDAO.class, userGroupId);
	}

	@Override
	public void addRatingRequirement(Long userGroupId, Integer rating) {
		addRequirementToGroupBy(RatingRequirementDAO.class, userGroupId,
			"value", rating
		);
	}

	@Override
	public RatingRequirement getRatingRequirement(Long userGroupId, Integer rating) {
		return getOrInitializeRequirement(RatingRequirementDAO.class, userGroupId,
			"value", rating
		);
	}

	@Override
	public void removeRatingRequirement(Long userGroupId) {
		removeRequirementToGroupBy(RatingRequirementDAO.class, userGroupId);
	}

	@Override
	public void addResourceTypeRequirement(Long userGroupId, Long resourceTypeId) {
		addRequirementToGroupBy(ResourceTypeRequirementDAO.class, userGroupId,
			"resourceTypeId", resourceTypeId
		);
	}

	@Override
	public ResourceTypeRequirement getResourceTypeRequirement(Long userGroupId, Long resourceTypeId) {
		return getOrInitializeRequirement(ResourceTypeRequirementDAO.class, userGroupId,
			"resourceTypeId", resourceTypeId
		);
	}

	@Override
	public void removeResourceTypeRequirement(Long userGroupId, Long resourceTypeId) {
		removeRequirementToGroupBy(ResourceTypeRequirementDAO.class, userGroupId,
			"resourceTypeId", resourceTypeId
		);
	}

	@Override
	public void addTestRequirement(Long userGroupId, Long testId) {
		addRequirementToGroupBy(TestRequirementDAO.class, userGroupId,
			"testRequirable", findRequirableById(TestRequirableDAO.class, testId)
		);
	}

	@Override
	public TestRequirement getTestRequirement(Long userGroupId, Long testId) {
		return getOrInitializeRequirement(TestRequirementDAO.class, userGroupId,
			"testRequirable", findRequirableById(TestRequirableDAO.class, testId)
		);
	}

	@Override
	public void removeTestRequirement(Long userGroupId, Long testId) {
		removeRequirementToGroupBy(TestRequirementDAO.class, userGroupId,
			"testRequirable", findRequirableById(TestRequirableDAO.class, testId)
		);
	}

	@Override
	public void addTravelDistanceRequirement(Long userGroupId, String address, Long distance) {
		addRequirementToGroupBy(TravelDistanceRequirementDAO.class, userGroupId,
			"distance", distance,
			"address", address
		);
	}

	@Override
	public TravelDistanceRequirement getTravelDistanceRequirement(Long userGroupId, String address, Long distance) {
		return getOrInitializeRequirement(TravelDistanceRequirementDAO.class, userGroupId,
			"distance", distance,
			"address", address
		);
	}

	@Override
	public void removeTravelDistanceRequirement(Long userGroupId, String address, Long distance) {
		removeRequirementToGroupBy(TravelDistanceRequirementDAO.class, userGroupId,
			"distance", distance,
			"address", address
		);
	}

	@Override
	public boolean userGroupHasRequirements(Long userGroupId) {
		UserGroup userGroup = userGroupDAO.findBy("id", userGroupId);

		RequirementSet requirementSet = requirementSetDAO.getOrInitializeBy(
			"userGroup", userGroup,
			"company", userGroup.getCompany()
		);
		return (requirementSet.getName() != null && requirementSet.getRequirements() != null && !requirementSet.getRequirements().isEmpty());
	}

	@Override
	public boolean userGroupHasAgreementRequirements(Long userGroupId) {
		UserGroup userGroup = userGroupDAO.findBy("id", userGroupId);
		if (userGroup == null) {
			return false;
		}

		RequirementSet requirementSet = requirementSetDAO.getOrInitializeBy(
			"userGroup", userGroup,
			"company", userGroup.getCompany()
		);

		if (requirementSet == null ||
			requirementSet.getName() == null ||
			requirementSet.getRequirements() == null) {
			return false;
		}

		for (AbstractRequirement requirement : requirementSet.getRequirements()) {
			if (requirement instanceof AgreementRequirement) {
				return true;
			}
		}

		return false;
	}

	@Override
	public RequirementSet findOrCreateRequirementSetByUserGroupId(Long userGroupId) {
		UserGroup userGroup = userGroupDAO.findBy("id", userGroupId);

		RequirementSet requirementSet = requirementSetDAO.getOrInitializeBy(
			"userGroup", userGroup,
			"company", userGroup.getCompany()
		);

		if (requirementSet.getName() == null) {
			requirementSet.setName(String.format(GROUP_TITLE_TEMPLATE, userGroup.getName()));
		}

		requirementSetDAO.saveOrUpdate(requirementSet);
		return requirementSet;
	}

	@Override
	public List<Long> findUserGroupsRequiredInsuranceIds(long groupId) {
		return userGroupDAO.findUserGroupsRequiredInsuranceIds(groupId);
	}

	@Override
	public List<Long> findUserGroupsRequiredCertificationIds(long groupId) {
		return userGroupDAO.findUserGroupsRequiredCertificationIds(groupId);
	}

	@Override
	public List<Long> findUserGroupsRequiredLicenseIds(long groupId) {
		return userGroupDAO.findUserGroupsRequiredLicenseIds(groupId);
	}

	@Override
	public List<Long> findUserGroupRequiredAgreementIds(Long groupId) {
		return userGroupDAO.findUserGroupsRequiredAgreementIds(groupId);
	}

	@Override
	public List<Long> findUserGroupRequiredIndustryIds(Long groupId) {
		return userGroupDAO.findUserGroupsRequiredIndustryIds(groupId);
	}

	private <T> T addRequirementToGroupBy(
		Class<? extends DAOInterface<T>> requirementDAOType,
		Long userGroupId,
		Object... objects
	) {
		T requirement = getOrInitializeRequirement(requirementDAOType, userGroupId, objects);
		saveOrUpdateRequirement(requirementDAOType, requirement);
		return requirement;
	}

	private <T> void removeRequirementToGroupBy(
		Class<? extends DAOInterface<T>> requirementDAOType,
		Long userGroupId,
		Object... objects
	) {
		applicationContext.getBean(requirementDAOType).delete(
			getOrInitializeRequirement(requirementDAOType, userGroupId, objects)
		);
	}

	private <T> T getOrInitializeRequirement(
		Class<? extends DAOInterface<T>> requirementDAOType,
		Long userGroupId,
		Object... objects
	) {
		List<Object> args = Lists.newArrayList(Arrays.asList(objects));
		args.add("requirementSet");
		args.add(findOrCreateRequirementSetByUserGroupId(userGroupId));

		return applicationContext.getBean(requirementDAOType).getOrInitializeBy(args.toArray());
	}

	private <T> void saveOrUpdateRequirement(
		Class<? extends DAOInterface<T>> requirementDAOType,
		T requirement
	) {
		applicationContext.getBean(requirementDAOType).saveOrUpdate(requirement);
	}

	private <T> T findRequirableById(
		Class<? extends DAOInterface<T>> requirableDAOType,
		Object id
	) {
		return applicationContext.getBean(requirableDAOType).findBy(
			"id", id
		);
	}

}
