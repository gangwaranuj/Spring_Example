package com.workmarket.domains.groups.service;

import com.google.common.collect.Lists;
import com.workmarket.domains.groups.model.UserGroup;
import com.workmarket.domains.model.Industry;
import com.workmarket.domains.model.User;
import com.workmarket.domains.model.assessment.GradedAssessment;
import com.workmarket.domains.model.asset.Asset;
import com.workmarket.domains.model.certification.Certification;
import com.workmarket.domains.model.company.CompanyType;
import com.workmarket.domains.model.contract.Contract;
import com.workmarket.domains.model.insurance.Insurance;
import com.workmarket.domains.model.license.License;
import com.workmarket.domains.model.requirementset.AbstractRequirement;
import com.workmarket.domains.model.requirementset.RequirementSet;
import com.workmarket.domains.model.requirementset.agreement.AgreementRequirable;
import com.workmarket.domains.model.requirementset.agreement.AgreementRequirement;
import com.workmarket.domains.model.requirementset.availability.AvailabilityRequirement;
import com.workmarket.domains.model.requirementset.availability.Weekday;
import com.workmarket.domains.model.requirementset.backgroundcheck.BackgroundCheckRequirement;
import com.workmarket.domains.model.requirementset.certification.CertificationRequirable;
import com.workmarket.domains.model.requirementset.certification.CertificationRequirement;
import com.workmarket.domains.model.requirementset.companytype.CompanyTypeRequirable;
import com.workmarket.domains.model.requirementset.companytype.CompanyTypeRequirement;
import com.workmarket.domains.model.requirementset.country.CountryRequirable;
import com.workmarket.domains.model.requirementset.country.CountryRequirement;
import com.workmarket.domains.model.requirementset.document.DocumentRequirable;
import com.workmarket.domains.model.requirementset.document.DocumentRequirement;
import com.workmarket.domains.model.requirementset.drugtest.DrugTestRequirement;
import com.workmarket.domains.model.requirementset.industry.IndustryRequirable;
import com.workmarket.domains.model.requirementset.industry.IndustryRequirement;
import com.workmarket.domains.model.requirementset.insurance.InsuranceRequirable;
import com.workmarket.domains.model.requirementset.insurance.InsuranceRequirement;
import com.workmarket.domains.model.requirementset.license.LicenseRequirable;
import com.workmarket.domains.model.requirementset.license.LicenseRequirement;
import com.workmarket.domains.model.requirementset.profilevideo.ProfileVideoRequirement;
import com.workmarket.domains.model.requirementset.rating.RatingRequirement;
import com.workmarket.domains.model.requirementset.resourcetype.ResourceType;
import com.workmarket.domains.model.requirementset.resourcetype.ResourceTypeRequirable;
import com.workmarket.domains.model.requirementset.resourcetype.ResourceTypeRequirement;
import com.workmarket.domains.model.requirementset.test.TestRequirable;
import com.workmarket.domains.model.requirementset.test.TestRequirement;
import com.workmarket.domains.model.requirementset.traveldistance.TravelDistanceRequirement;
import com.workmarket.service.business.BaseServiceIT;
import com.workmarket.service.business.requirementsets.AbstractRequirementsService;
import com.workmarket.test.IntegrationTest;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.math.BigDecimal;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.nullValue;

@RunWith(SpringJUnit4ClassRunner.class)
@Category(IntegrationTest.class)
public class UserGroupRequirementSetServiceImplIT extends BaseServiceIT {

	@Autowired UserGroupRequirementSetService service;
	@Autowired AbstractRequirementsService requirementsService;

	UserGroup userGroup;
	User user;

	@Before
	public void setUp() throws Exception {
		user = newEmployeeWithCashBalance();
		authenticationService.setCurrentUser(user);
		userGroup = newPublicUserGroup(user);
	}

	@Test
	public void addAgreementRequirement_AddsAnAgreementRequirementToAGroup() throws Exception {
		// GIVEN
		// An agreement exists
		Contract agreement = new Contract();
		agreement.setName("Super Cool Agreement");
		contractService.saveOrUpdateContract(agreement);

		// WHEN
		// The agreement is required by the group
		service.addAgreementRequirement(userGroup.getId(), agreement.getId());

		// THEN
		// The group should have a requirement set
		RequirementSet requirementSet = service.findOrCreateRequirementSetByUserGroupId(userGroup.getId());
		assertThat(requirementSet, is(not(nullValue())));

		// AND
		// The requirement set should have a requirement
		List<AbstractRequirement> requirements = requirementsService.findAllByRequirementSetId(requirementSet.getId());
		assertThat(requirements, hasSize(1));

		// AND
		// The requirement should match the agreement
		AgreementRequirable agreementRequirable = ((AgreementRequirement) requirements.get(0)).getAgreementRequirable();
		assertThat(agreement.getId(), is(agreementRequirable.getId()));
		assertThat(agreement.getName(), is(agreementRequirable.getName()));

		// WHEN
		// The agreement requirement is removed from the group
		service.removeAgreementRequirement(userGroup.getId(), agreement.getId());

		// THEN
		// The requirement set should no longer have a requirement
		requirements = requirementsService.findAllByRequirementSetId(requirementSet.getId());
		assertThat(requirements, is(empty()));
	}

	@Test
	public void addAvailabilityRequirement_AddsAnAvailabilityRequirementToAGroup() throws Exception {
		// WHEN
		// The an availability is required by the group
		service.addAvailabilityRequirement(userGroup.getId(), Weekday.MONDAY.getId(), "9:00am", "5:00pm");

		// THEN
		// The group should have a requirement set
		RequirementSet requirementSet = service.findOrCreateRequirementSetByUserGroupId(userGroup.getId());
		assertThat(requirementSet, is(not(nullValue())));

		// AND
		// The requirement set should have a requirement
		List<AbstractRequirement> requirements = requirementsService.findAllByRequirementSetId(requirementSet.getId());
		assertThat(requirements, hasSize(1));

		// AND
		// The requirement should match the availability
		AvailabilityRequirement availabilityRequirement = (AvailabilityRequirement) requirements.get(0);
		assertThat(availabilityRequirement.getDayOfWeek(), is(Weekday.MONDAY.getId()));
		assertThat(availabilityRequirement.getFromTime(), is("9:00am"));
		assertThat(availabilityRequirement.getToTime(), is("5:00pm"));

		// WHEN
		// The availability requirement is removed from the group
		service.removeAvailabilityRequirement(userGroup.getId(), Weekday.MONDAY.getId());

		// THEN
		// The requirement set should no longer have a requirement
		requirements = requirementsService.findAllByRequirementSetId(requirementSet.getId());
		assertThat(requirements, is(empty()));
	}

	@Test
	public void addBackgroundCheckRequirement_AddsABackgroundCheckRequirementToAGroup() throws Exception {
		// WHEN
		// A background check is required by the group
		service.addBackgroundCheckRequirement(userGroup.getId());

		// THEN
		// The group should have a requirement set
		RequirementSet requirementSet = service.findOrCreateRequirementSetByUserGroupId(userGroup.getId());
		assertThat(requirementSet, is(not(nullValue())));

		// AND
		// The requirement set should have a requirement
		List<AbstractRequirement> requirements = requirementsService.findAllByRequirementSetId(requirementSet.getId());
		assertThat(requirements, hasSize(1));

		// AND
		// The requirement should be for a Background Check
		assertThat(requirements.get(0), instanceOf(BackgroundCheckRequirement.class));

		// WHEN
		// The background check requirement is removed from the group
		service.removeBackgroundCheckRequirement(userGroup.getId());

		// THEN
		// The requirement set should no longer have a requirement
		requirements = requirementsService.findAllByRequirementSetId(requirementSet.getId());
		assertThat(requirements, is(empty()));
	}

	@Test
	public void addCertificationRequirement_AddsACertificationRequirementToAGroup() throws Exception {
		// GIVEN
		// A certification exists
		Certification certification = certificationService.findCertificationByName("A+");

		// WHEN
		// The certification is required by the group
		service.addCertificationRequirement(userGroup.getId(), certification.getId(), false, true);

		// THEN
		// The group should have a requirement set
		RequirementSet requirementSet = service.findOrCreateRequirementSetByUserGroupId(userGroup.getId());
		assertThat(requirementSet, is(not(nullValue())));

		// AND
		// The requirement set should have a requirement
		List<AbstractRequirement> requirements = requirementsService.findAllByRequirementSetId(requirementSet.getId());
		assertThat(requirements, hasSize(1));

		// AND
		// The requirement should have matching expiry settings
		CertificationRequirement certificationRequirement = (CertificationRequirement) requirements.get(0);
		assertThat(certificationRequirement.isNotifyOnExpiry(), is(false));
		assertThat(certificationRequirement.isRemoveMembershipOnExpiry(), is(true));

		// AND
		// The requirement should match the certification
		CertificationRequirable certificationRequirable = certificationRequirement.getCertificationRequirable();
		assertThat(certification.getId(), is(certificationRequirable.getId()));
		assertThat(certificationRequirable.getCertificationVendor().getName() + " - " + certification.getName(), is(certificationRequirable.getName()));

		// WHEN
		// The certification requirement is removed from the group
		service.removeCertificationRequirement(userGroup.getId(), certification.getId());

		// THEN
		// The requirement set should no longer have a requirement
		requirements = requirementsService.findAllByRequirementSetId(requirementSet.getId());
		assertThat(requirements, is(empty()));
	}

	@Test
	public void addCountryRequirement_AddsACountryRequirementToAGroup() throws Exception {
		// WHEN
		// Some countries are required by the group
		List<String> countryIds = Lists.newArrayList("USA", "CAN");
		service.addCountryRequirement(userGroup.getId(), countryIds);

		// THEN
		// The group should have a requirement set
		RequirementSet requirementSet = service.findOrCreateRequirementSetByUserGroupId(userGroup.getId());
		assertThat(requirementSet, is(not(nullValue())));

		// AND
		// The requirement set should some requirements
		List<AbstractRequirement> requirements = requirementsService.findAllByRequirementSetId(requirementSet.getId());
		assertThat(requirements, hasSize(2));

		// AND
		// One of the requirements should match the country
		CountryRequirable usa = ((CountryRequirement) requirements.get(0)).getCountryRequirable();
		assertThat(usa.getId(), is("USA"));

		// AND
		// The other requirement should match the other country
		CountryRequirable can = ((CountryRequirement) requirements.get(1)).getCountryRequirable();
		assertThat(can.getId(), is("CAN"));

		// WHEN
		// The first country requirement is removed from the group
		service.removeCountryRequirement(userGroup.getId(), "USA");

		// THEN
		// The requirement set should no longer have only one requirement
		requirements = requirementsService.findAllByRequirementSetId(requirementSet.getId());
		assertThat(requirements, hasSize(1));

		// WHEN
		// The other country requirement is removed from the group
		service.removeCountryRequirement(userGroup.getId(), "CAN");

		// THEN
		// The requirement set should no longer have any requirements
		requirements = requirementsService.findAllByRequirementSetId(requirementSet.getId());
		assertThat(requirements, is(empty()));
	}

	@Test
	public void addCompanyTypeRequirement_AddsACompanyTypeRequirementToAGroup() throws Exception {
		// WHEN
		// A company type is required by a group
		service.addCompanyTypeRequirement(userGroup.getId(), CompanyType.SOLE_PROPRIETOR.getId());

		// THEN
		// The group should have a requirement set
		RequirementSet requirementSet = service.findOrCreateRequirementSetByUserGroupId(userGroup.getId());
		assertThat(requirementSet, is(not(nullValue())));

		// AND
		// The requirement set should have a requirement
		List<AbstractRequirement> requirements = requirementsService.findAllByRequirementSetId(requirementSet.getId());
		assertThat(requirements, hasSize(1));

		// AND
		// The requirement should match the company type
		CompanyTypeRequirable companyTypeRequirable = ((CompanyTypeRequirement) requirements.get(0)).getCompanyTypeRequirable();
		assertThat(companyTypeRequirable.getId(), is(CompanyType.SOLE_PROPRIETOR.getId()));

		// WHEN
		// A different company type is required by a group
		service.addCompanyTypeRequirement(userGroup.getId(), CompanyType.CORPORATION.getId());

		// THEN
		// The requirement set should still have only one requirement
		requirements = requirementsService.findAllByRequirementSetId(requirementSet.getId());
		assertThat(requirements, hasSize(1));

		// AND
		// The requirement should match the different company type
		companyTypeRequirable = ((CompanyTypeRequirement) requirements.get(0)).getCompanyTypeRequirable();
		assertThat(companyTypeRequirable.getId(), is(CompanyType.CORPORATION.getId()));

		// WHEN
		// The company type requirement is removed from the group
		service.removeCompanyTypeRequirement(userGroup.getId());

		// THEN
		// The requirement set should no longer have a requirement
		requirements = requirementsService.findAllByRequirementSetId(requirementSet.getId());
		assertThat(requirements, is(empty()));
	}

	@Test
	public void addDocumentRequirement_AddsADocumentRequirementToAGroup() throws Exception {
		// GIVEN
		// A document exists
		Asset document = newAsset();

		// WHEN
		// The document is required by the group
		service.addDocumentRequirement(userGroup.getId(), document.getId(), true);

		// THEN
		// The group should have a requirement set
		RequirementSet requirementSet = service.findOrCreateRequirementSetByUserGroupId(userGroup.getId());
		assertThat(requirementSet, is(not(nullValue())));

		// AND
		// The requirement set should have a requirement
		List<AbstractRequirement> requirements = requirementsService.findAllByRequirementSetId(requirementSet.getId());
		assertThat(requirements, hasSize(1));

		// AND
		// The requirement should match the document
		DocumentRequirable documentRequirable = ((DocumentRequirement) requirements.get(0)).getDocumentRequirable();
		assertThat(document.getId(), is(documentRequirable.getId()));
		assertThat(document.getName(), is(documentRequirable.getName()));

		// WHEN
		// The document requirement is removed from the group
		service.removeDocumentRequirement(userGroup.getId(), document.getId());

		// THEN
		// The requirement set should no longer have a requirement
		requirements = requirementsService.findAllByRequirementSetId(requirementSet.getId());
		assertThat(requirements, is(empty()));
	}

	@Test
	public void addDrugTestRequirement_AddsADrugTestRequirementToAGroup() throws Exception {
		// WHEN
		// A drug test is required by a group
		service.addDrugTestRequirement(userGroup.getId());

		// THEN
		// The group should have a requirement set
		RequirementSet requirementSet = service.findOrCreateRequirementSetByUserGroupId(userGroup.getId());
		assertThat(requirementSet, is(not(nullValue())));

		// AND
		// The requirement set should have a requirement
		List<AbstractRequirement> requirements = requirementsService.findAllByRequirementSetId(requirementSet.getId());
		assertThat(requirements, hasSize(1));

		// AND
		// The requirement should be for a drug test
		assertThat(requirements.get(0), instanceOf(DrugTestRequirement.class));

		// WHEN
		// The drug test requirement is removed from the group
		service.removeDrugTestRequirement(userGroup.getId());

		// THEN
		// The requirement set should no longer have a requirement
		requirements = requirementsService.findAllByRequirementSetId(requirementSet.getId());
		assertThat(requirements, is(empty()));
	}

	@Test
	public void addIndustryRequirement_AddsAnIndustryRequirementToAGroup() throws Exception {
		// GIVEN
		// An industry exists
		Industry technology = invariantDataService.findIndustry(1000L);

		// WHEN
		// The industry is required by a group
		service.addIndustryRequirement(userGroup.getId(), technology.getId());

		// THEN
		// The group should have a requirement set
		RequirementSet requirementSet = service.findOrCreateRequirementSetByUserGroupId(userGroup.getId());
		assertThat(requirementSet, is(not(nullValue())));

		// AND
		// The requirement set should have a requirement
		List<AbstractRequirement> requirements = requirementsService.findAllByRequirementSetId(requirementSet.getId());
		assertThat(requirements, hasSize(1));

		// AND
		// The requirement should match the industry
		IndustryRequirable industryRequirable = ((IndustryRequirement) requirements.get(0)).getIndustryRequirable();
		assertThat(technology.getId(), is(industryRequirable.getId()));
		assertThat(technology.getName(), is(industryRequirable.getName()));

		// WHEN
		// The industry requirement is removed from the group
		service.removeIndustryRequirement(userGroup.getId(), technology.getId());

		// THEN
		// The requirement set should no longer have a requirement
		requirements = requirementsService.findAllByRequirementSetId(requirementSet.getId());
		assertThat(requirements, is(empty()));
	}

	@Test
	public void addInsuranceRequirement_AddsAnInsuranceRequirementToAGroup() throws Exception {
		// GIVEN
		// Insurance exists
		Insurance generalLiability = insuranceService.findInsurance(1001L);

		// WHEN
		// The insurance is required by the group
		BigDecimal amount = new BigDecimal("47000000.00");
		service.addInsuranceRequirement(
			userGroup.getId(),
			generalLiability.getId(),
			amount,
			true,
			false
		);

		// THEN
		// The group should have a requirement set
		RequirementSet requirementSet = service.findOrCreateRequirementSetByUserGroupId(userGroup.getId());
		assertThat(requirementSet, is(not(nullValue())));

		// AND
		// The requirement set should have a requirement
		List<AbstractRequirement> requirements = requirementsService.findAllByRequirementSetId(requirementSet.getId());
		assertThat(requirements, hasSize(1));

		// AND
		// The requirement should have matching expiry settings
		InsuranceRequirement insuranceRequirement = (InsuranceRequirement) requirements.get(0);
		assertThat(insuranceRequirement.getMinimumCoverageAmount(), is(amount));
		assertThat(insuranceRequirement.isNotifyOnExpiry(), is(true));
		assertThat(insuranceRequirement.isRemoveMembershipOnExpiry(), is(false));

		// AND
		// The requirement matches the insurance
		InsuranceRequirable insuranceRequirable = insuranceRequirement.getInsuranceRequirable();
		assertThat(generalLiability.getId(), is(insuranceRequirable.getId()));
		assertThat(generalLiability.getName(), is(insuranceRequirable.getName()));

		// WHEN
		// The insurance requirement is removed from the group
		service.removeInsuranceRequirement(userGroup.getId(), generalLiability.getId());

		// THEN
		// The requirement set should no longer have a requirement
		requirements = requirementsService.findAllByRequirementSetId(requirementSet.getId());
		assertThat(requirements, is(empty()));
	}

	@Test
	public void addLicenseRequirement_AddsALicenseRequirementToAGroup() throws Exception {
		// GIVEN
		// A license exists
		License hairDresser = licenseService.findLicenseById(1001L);

		// WHEN
		// The license is required by the group
		service.addLicenseRequirement(userGroup.getId(), hairDresser.getId(), false, false);

		// THEN
		// The group should have a requirement set
		RequirementSet requirementSet = service.findOrCreateRequirementSetByUserGroupId(userGroup.getId());
		assertThat(requirementSet, is(not(nullValue())));

		// AND
		// The requirement set should have a requirement
		List<AbstractRequirement> requirements = requirementsService.findAllByRequirementSetId(requirementSet.getId());
		assertThat(requirements, hasSize(1));

		// AND
		// The requirement should have matching expiry settings
		LicenseRequirement licenseRequirement = (LicenseRequirement) requirements.get(0);
		assertThat(licenseRequirement.isNotifyOnExpiry(), is(false));
		assertThat(licenseRequirement.isRemoveMembershipOnExpiry(), is(false));

		// AND
		// The requirement should match the license
		LicenseRequirable licenseRequirable = licenseRequirement.getLicenseRequirable();
		assertThat(hairDresser.getId(), is(licenseRequirable.getId()));
		assertThat(licenseRequirable.getState() + " - " + hairDresser.getName(), is(licenseRequirable.getName()));

		// WHEN
		// The license requirement is removed from the group
		service.removeLicenseRequirement(userGroup.getId(), hairDresser.getId());

		// THEN
		// The requirement set should no longer have a requirement
		requirements = requirementsService.findAllByRequirementSetId(requirementSet.getId());
		assertThat(requirements, is(empty()));
	}

	@Test
	public void addProfileVideoRequirement_AddsAProfileVideoRequirementToAGroup() throws Exception {
		// WHEN
		// A profile video is required by a group
		service.addProfileVideoRequirement(userGroup.getId());

		// THEN
		// The group should have a requirement set
		RequirementSet requirementSet = service.findOrCreateRequirementSetByUserGroupId(userGroup.getId());
		assertThat(requirementSet, is(not(nullValue())));

		// AND
		// The requirement set should have a requirement
		List<AbstractRequirement> requirements = requirementsService.findAllByRequirementSetId(requirementSet.getId());
		assertThat(requirements, hasSize(1));

		// AND
		// The requirement should for a profile video
		assertThat(requirements.get(0), instanceOf(ProfileVideoRequirement.class));

		// WHEN
		// The profile video requirement is removed from the group
		service.removeProfileVideoRequirement(userGroup.getId());

		// THEN
		// The requirement set should no longer have a requirement
		requirements = requirementsService.findAllByRequirementSetId(requirementSet.getId());
		assertThat(requirements, is(empty()));
	}

	@Test
	public void addRatingRequirement_AddsARatingRequirementToAGroup() throws Exception {
		// WHEN
		// A rating is required by a group
		int rating = 80;
		service.addRatingRequirement(userGroup.getId(), rating);

		// THEN
		// The group should have a requirement set
		RequirementSet requirementSet = service.findOrCreateRequirementSetByUserGroupId(userGroup.getId());
		assertThat(requirementSet, is(not(nullValue())));

		// AND
		// The requirement set should have a requirement
		List<AbstractRequirement> requirements = requirementsService.findAllByRequirementSetId(requirementSet.getId());
		assertThat(requirements, hasSize(1));

		// AND
		// The requirement matches the rating
		RatingRequirement ratingRequirement = (RatingRequirement) requirements.get(0);
		assertThat(ratingRequirement.getValue(), is(rating));

		// WHEN
		// The rating requirement is removed from the group
		service.removeRatingRequirement(userGroup.getId());

		// THEN
		// The requirement set should no longer have a requirement
		requirements = requirementsService.findAllByRequirementSetId(requirementSet.getId());
		assertThat(requirements, is(empty()));
	}

	@Test
	public void addResourceTypeRequirement_AddsAResourceTypeRequirementToAGroup() throws Exception {
		// WHEN
		// A resource type is required by a group
		service.addResourceTypeRequirement(userGroup.getId(), ResourceType.EMPLOYEE.getId());

		// THEN
		// The group should have a requirement set
		RequirementSet requirementSet = service.findOrCreateRequirementSetByUserGroupId(userGroup.getId());
		assertThat(requirementSet, is(not(nullValue())));

		// AND
		// The requirement set should have a requirement
		List<AbstractRequirement> requirements = requirementsService.findAllByRequirementSetId(requirementSet.getId());
		assertThat(requirements, hasSize(1));

		// AND
		// The requirement should match the resource type
		ResourceTypeRequirable resourceTypeRequirable = ((ResourceTypeRequirement) requirements.get(0)).getResourceTypeRequirable();
		assertThat(resourceTypeRequirable.getId(), is(ResourceType.EMPLOYEE.getId()));

		// WHEN
		// The resource type requirement is removed from the group
		service.removeResourceTypeRequirement(userGroup.getId(), ResourceType.EMPLOYEE.getId());

		// THEN
		// The requirement set should no longer have a requirement
		requirements = requirementsService.findAllByRequirementSetId(requirementSet.getId());
		assertThat(requirements, is(empty()));
	}

	@Test
	public void addTestRequirement_AddsATestRequirementToAGroup() throws Exception {
		// GIVEN
		// There is a test
		Industry technology = invariantDataService.findIndustry(1000L);
		GradedAssessment test = (GradedAssessment) newAssessmentForUser(user, technology, true);

		// WHEN
		// The test is required by a group
		service.addTestRequirement(userGroup.getId(), test.getId());

		// THEN
		// The group should have a requirement set
		RequirementSet requirementSet = service.findOrCreateRequirementSetByUserGroupId(userGroup.getId());
		assertThat(requirementSet, is(not(nullValue())));

		// AND
		// The requirement set should have a requirement
		List<AbstractRequirement> requirements = requirementsService.findAllByRequirementSetId(requirementSet.getId());
		assertThat(requirements, hasSize(1));

		// AND
		// The requirement should match the test
		TestRequirable testRequirable = ((TestRequirement) requirements.get(0)).getTestRequirable();
		assertThat(testRequirable.getId(), is(test.getId()));
		assertThat(testRequirable.getName(), is(test.getName()));

		// WHEN
		// The test requirement is removed from the group
		service.removeTestRequirement(userGroup.getId(), test.getId());

		// THEN
		// The requirement set should no longer have a requirement
		requirements = requirementsService.findAllByRequirementSetId(requirementSet.getId());
		assertThat(requirements, is(empty()));
	}

	@Test
	public void addTravelDistanceRequirement_AddsATravelDistanceRequirementToAGroup() throws Exception {
		// WHEN
		// A travel distance is required by a group
		String address = "4 Abbot Rd";
		long distance = 47L;
		service.addTravelDistanceRequirement(userGroup.getId(), address, distance);

		// THEN
		// The group should have a requirement set
		RequirementSet requirementSet = service.findOrCreateRequirementSetByUserGroupId(userGroup.getId());
		assertThat(requirementSet, is(not(nullValue())));

		// AND
		// The requirement set should have a requirement
		List<AbstractRequirement> requirements = requirementsService.findAllByRequirementSetId(requirementSet.getId());
		assertThat(requirements, hasSize(1));

		// AND
		// The requirement should match the travel distance
		TravelDistanceRequirement travelDistanceRequirement = (TravelDistanceRequirement) requirements.get(0);
		assertThat(travelDistanceRequirement.getAddress(), is(address));
		assertThat(travelDistanceRequirement.getDistance(), is(distance));


		// WHEN
		// The travel distance requirement is removed from the group
		service.removeTravelDistanceRequirement(userGroup.getId(), address, distance);

		// THEN
		// The requirement set should no longer have a requirement
		requirements = requirementsService.findAllByRequirementSetId(requirementSet.getId());
		assertThat(requirements, is(empty()));
	}
}
