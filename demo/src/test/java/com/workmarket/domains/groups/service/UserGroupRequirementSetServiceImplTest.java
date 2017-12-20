package com.workmarket.domains.groups.service;

import com.google.common.collect.Lists;
import com.workmarket.dao.requirement.*;
import com.workmarket.domains.groups.dao.UserGroupDAO;
import com.workmarket.domains.groups.model.UserGroup;
import com.workmarket.domains.model.Company;
import com.workmarket.domains.model.requirementset.AbstractRequirement;
import com.workmarket.domains.model.requirementset.Requirable;
import com.workmarket.domains.model.requirementset.RequirementSet;
import com.workmarket.domains.model.requirementset.availability.AvailabilityRequirement;
import com.workmarket.domains.model.requirementset.availability.Weekday;
import com.workmarket.domains.model.requirementset.certification.CertificationRequirement;
import com.workmarket.domains.model.requirementset.companytype.CompanyTypeRequirement;
import com.workmarket.domains.model.requirementset.document.DocumentRequirement;
import com.workmarket.domains.model.requirementset.insurance.InsuranceRequirement;
import com.workmarket.domains.model.requirementset.license.LicenseRequirement;
import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.context.ApplicationContext;

import java.math.BigDecimal;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyVararg;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class UserGroupRequirementSetServiceImplTest {
	protected static final Long USER_GROUP_ID = 9999999L;
	protected static final Long REQUIRABLE_ID = 1111111L;
	private static final String USER_GROUP_NAME = "Amazing User Group";
	public static final String COUNTRY_ID = "USA";
	private static final BigDecimal COVERAGE_AMOUNT = new BigDecimal("47000000.00");
	private static final Integer RATING = 90;
	private static final String ADDRESS = "1007 Mountain Drive, Gotham";
	private static final Long DISTANCE = 50L;

	@Mock ApplicationContext context;
	@Mock UserGroupDAO userGroupDAO;
	@Mock RequirementSetDAO requirementSetDAO;
	@InjectMocks UserGroupRequirementSetService service = new UserGroupRequirementSetServiceImpl();

	UserGroup userGroup;
	Company company;
	RequirementSet requirementSet;

	Requirable requirable;
	RequirableDAO requirableDAO;
	AbstractRequirement requirement;
	AvailabilityRequirement availabilityRequirement;
	CertificationRequirement certificationRequirement;
	DocumentRequirement documentRequirement;
	InsuranceRequirement insuranceRequirement;
	LicenseRequirement licenseRequirement;
	RequirementDAO requirementDAO;

	CompanyTypeRequirementDAO companyTypeRequirementDAO;
	CompanyTypeRequirement companyTypeRequirement;

	@Before
	public void setup() throws Exception {
		company = mock(Company.class);
		userGroup = mock(UserGroup.class);
		when(userGroup.getName()).thenReturn(USER_GROUP_NAME);
		when(userGroup.getCompany()).thenReturn(company);
		when(userGroupDAO.findBy("id", USER_GROUP_ID)).thenReturn(userGroup);

		requirementSet = mock(RequirementSet.class);
		when(requirementSetDAO.getOrInitializeBy(
			"userGroup", userGroup,
			"company", company
		)).thenReturn(requirementSet);

		requirableDAO = mock(RequirableDAO.class);
		requirable = mock(Requirable.class);
		when(requirableDAO.findBy("id", REQUIRABLE_ID)).thenReturn(requirable);
		when(requirableDAO.findBy("id", COUNTRY_ID)).thenReturn(requirable);
		when(context.getBean(Matchers.argThat(new ClassOrSubclassMatcher<>(RequirableDAO.class)))).thenReturn(requirableDAO);

		requirementDAO = mock(RequirementDAO.class);
		requirement = mock(AbstractRequirement.class);
		when(requirementDAO.getOrInitializeBy(anyVararg())).thenReturn(requirement);

		availabilityRequirement = mock(AvailabilityRequirement.class);
		when(requirementDAO.getOrInitializeBy(
			"dayOfWeek", Weekday.TUESDAY.getId(),
			"requirementSet", requirementSet
		)).thenReturn(availabilityRequirement);

		certificationRequirement = mock(CertificationRequirement.class);
		when(requirementDAO.getOrInitializeBy(
			"certificationRequirable", requirable,
			"requirementSet", requirementSet
		)).thenReturn(certificationRequirement);

		documentRequirement = mock(DocumentRequirement.class);
		when(requirementDAO.getOrInitializeBy(
			"documentRequirable", requirable,
			"requirementSet", requirementSet
		)).thenReturn(documentRequirement);

		insuranceRequirement = mock(InsuranceRequirement.class);
		when(requirementDAO.getOrInitializeBy(
			"insuranceRequirable", requirable,
			"requirementSet", requirementSet
		)).thenReturn(insuranceRequirement);

		licenseRequirement = mock(LicenseRequirement.class);
		when(requirementDAO.getOrInitializeBy(
			"licenseRequirable", requirable,
			"requirementSet", requirementSet
		)).thenReturn(licenseRequirement);

		when(context.getBean(Matchers.argThat(new ClassOrSubclassMatcher<>(RequirementDAO.class)))).thenReturn(requirementDAO);

		companyTypeRequirementDAO = mock(CompanyTypeRequirementDAO.class);
		when(context.getBean(CompanyTypeRequirementDAO.class)).thenReturn(companyTypeRequirementDAO);
		companyTypeRequirement = mock(CompanyTypeRequirement.class);
		when(companyTypeRequirementDAO.getOrInitializeBy(
			"requirementSet", requirementSet
		)).thenReturn(companyTypeRequirement);
	}

	@Test
	public void addAgreementRequirement_GetsTheRequirableDAOBean() throws Exception {
		service.addAgreementRequirement(USER_GROUP_ID, REQUIRABLE_ID);
		verify(context).getBean(AgreementRequirableDAO.class);
	}

	@Test
	public void addAgreementRequirement_FindsTheRequirable() throws Exception {
		service.addAgreementRequirement(USER_GROUP_ID, REQUIRABLE_ID);
		verify(requirableDAO).findBy("id", REQUIRABLE_ID);
	}

	@Test
	public void addAgreementRequirement_FindsTheUserGroup() throws Exception {
		service.addAgreementRequirement(USER_GROUP_ID, REQUIRABLE_ID);
		verify(userGroupDAO).findBy("id", USER_GROUP_ID);
	}

	@Test
	public void addAgreementRequirement_GetsOrInitializesARequirementSet() throws Exception {
		service.addAgreementRequirement(USER_GROUP_ID, REQUIRABLE_ID);
		verify(requirementSetDAO).getOrInitializeBy(
			"userGroup", userGroup,
			"company", company
		);
	}

	@Test
	public void addAgreementRequirement_WithAnUnnamedRequirementSet_NamesTheRequirementSet() throws Exception {
		service.addAgreementRequirement(USER_GROUP_ID, REQUIRABLE_ID);
		verify(requirementSet).setName("(Group) " + USER_GROUP_NAME);
	}

	@Test
	public void addAgreementRequirement_WithANamedRequirementSet_DoesNotNameTheRequirementSet() throws Exception {
		when(requirementSet.getName()).thenReturn("Some Name");
		service.addAgreementRequirement(USER_GROUP_ID, REQUIRABLE_ID);
		verify(requirementSet, never()).setName(any(String.class));
	}

	@Test
	public void addAgreementRequirement_SavesOrUpdatesTheRequirementSet() {
		service.addAgreementRequirement(USER_GROUP_ID, REQUIRABLE_ID);
		verify(requirementSetDAO).saveOrUpdate(requirementSet);
	}

	@Test
	public void addAgreementRequirement_GetsTheRequirementDAOBean() throws Exception {
		service.addAgreementRequirement(USER_GROUP_ID, REQUIRABLE_ID);
		verify(context, times(2)).getBean(AgreementRequirementDAO.class);
	}

	@Test
	public void addAgreementRequirement_GetsOrInitializesARequirement() throws Exception {
		service.addAgreementRequirement(USER_GROUP_ID, REQUIRABLE_ID);
		verify(requirementDAO).getOrInitializeBy(
			"agreementRequirable", requirable,
			"requirementSet", requirementSet
		);
	}

	@Test
	public void addAgreementRequirement_SavesOrUpdatesARequirement() throws Exception {
		service.addAgreementRequirement(USER_GROUP_ID, REQUIRABLE_ID);
		verify(requirementDAO).saveOrUpdate(requirement);
	}

	@Test
	public void removeAgreementRequirement_GetsTheRequirableDAOBean() throws Exception {
		service.removeAgreementRequirement(USER_GROUP_ID, REQUIRABLE_ID);
		verify(context).getBean(AgreementRequirableDAO.class);
	}

	@Test
	public void removeAgreementRequirement_FindsTheRequirable() throws Exception {
		service.removeAgreementRequirement(USER_GROUP_ID, REQUIRABLE_ID);
		verify(requirableDAO).findBy("id", REQUIRABLE_ID);
	}

	@Test
	public void removeAgreementRequirement_FindsTheUserGroup() throws Exception {
		service.removeAgreementRequirement(USER_GROUP_ID, REQUIRABLE_ID);
		verify(userGroupDAO).findBy("id", USER_GROUP_ID);
	}

	@Test
	public void removeAgreementRequirement_GetsOrInitializesARequirementSet() throws Exception {
		service.removeAgreementRequirement(USER_GROUP_ID, REQUIRABLE_ID);
		verify(requirementSetDAO).getOrInitializeBy(
			"userGroup", userGroup,
			"company", company
		);
	}

	@Test
	public void removeAgreementRequirement_SavesOrUpdatesTheRequirementSet() {
		service.removeAgreementRequirement(USER_GROUP_ID, REQUIRABLE_ID);
		verify(requirementSetDAO).saveOrUpdate(requirementSet);
	}

	@Test
	public void removeAgreementRequirement_GetsTheRequirementDAOBean() throws Exception {
		service.removeAgreementRequirement(USER_GROUP_ID, REQUIRABLE_ID);
		verify(context, times(2)).getBean(AgreementRequirementDAO.class);
	}

	@Test
	public void removeAgreementRequirement_GetsOrInitializesARequirement() throws Exception {
		service.removeAgreementRequirement(USER_GROUP_ID, REQUIRABLE_ID);
		verify(requirementDAO).getOrInitializeBy(
			"agreementRequirable", requirable,
			"requirementSet", requirementSet
		);
	}

	@Test
	public void removeAgreementRequirement_DeletesARequirement() throws Exception {
		service.removeAgreementRequirement(USER_GROUP_ID, REQUIRABLE_ID);
		verify(requirementDAO).delete(requirement);
	}

	@Test
	public void addAvailabilityRequirement_FindsTheUserGroup() throws Exception {
		service.addAvailabilityRequirement(USER_GROUP_ID, Weekday.TUESDAY.getId(), "9:00am", "5:00pm");
		verify(userGroupDAO).findBy("id", USER_GROUP_ID);
	}

	@Test
	public void addAvailabilityRequirement_GetsOrInitializesARequirementSet() throws Exception {
		service.addAvailabilityRequirement(USER_GROUP_ID, Weekday.TUESDAY.getId(), "9:00am", "5:00pm");
		verify(requirementSetDAO).getOrInitializeBy(
			"userGroup", userGroup,
			"company", company
		);
	}

	@Test
	public void addAvailabilityRequirement_WithAnUnnamedRequirementSet_NamesTheRequirementSet() throws Exception {
		service.addAvailabilityRequirement(USER_GROUP_ID, Weekday.TUESDAY.getId(), "9:00am", "5:00pm");
		verify(requirementSet).setName("(Group) " + USER_GROUP_NAME);
	}

	@Test
	public void addAvailabilityRequirement_WithANamedRequirementSet_DoesNotNameTheRequirementSet() throws Exception {
		when(requirementSet.getName()).thenReturn("Some Name");
		service.addAvailabilityRequirement(USER_GROUP_ID, Weekday.TUESDAY.getId(), "9:00am", "5:00pm");
		verify(requirementSet, never()).setName(any(String.class));
	}

	@Test
	public void addAvailabilityRequirement_SavesOrUpdatesTheRequirementSet() {
		service.addAvailabilityRequirement(USER_GROUP_ID, Weekday.TUESDAY.getId(), "9:00am", "5:00pm");
		verify(requirementSetDAO).saveOrUpdate(requirementSet);
	}

	@Test
	public void addAvailabilityRequirement_GetsTheRequirementDAOBean() throws Exception {
		service.addAvailabilityRequirement(USER_GROUP_ID, Weekday.TUESDAY.getId(), "9:00am", "5:00pm");
		verify(context, times(2)).getBean(AvailabilityRequirementDAO.class);
	}

	@Test
	public void addAvailabilityRequirement_GetsOrInitializesARequirement() throws Exception {
		service.addAvailabilityRequirement(USER_GROUP_ID, Weekday.TUESDAY.getId(), "9:00am", "5:00pm");
		verify(requirementDAO).getOrInitializeBy(
			"dayOfWeek", Weekday.TUESDAY.getId(),
			"requirementSet", requirementSet
		);
	}

	@Test
	public void addAvailabilityRequirement_SetsTheRequirementFromTime() throws Exception {
		service.addAvailabilityRequirement(USER_GROUP_ID, Weekday.TUESDAY.getId(), "9:00am", "5:00pm");
		verify(availabilityRequirement).setFromTime("9:00am");
	}

	@Test
	public void addAvailabilityRequirement_SetsTheRequirementToTime() throws Exception {
		service.addAvailabilityRequirement(USER_GROUP_ID, Weekday.TUESDAY.getId(), "9:00am", "5:00pm");
		verify(availabilityRequirement).setToTime("5:00pm");
	}

	@Test
	public void addAvailabilityRequirement_SavesOrUpdatesARequirement() throws Exception {
		service.addAvailabilityRequirement(USER_GROUP_ID, Weekday.TUESDAY.getId(), "9:00am", "5:00pm");
		verify(requirementDAO).saveOrUpdate(availabilityRequirement);
	}

	@Test
	public void removeAvailabilityRequirement_FindsTheUserGroup() throws Exception {
		service.removeAvailabilityRequirement(USER_GROUP_ID, Weekday.TUESDAY.getId());
		verify(userGroupDAO).findBy("id", USER_GROUP_ID);
	}

	@Test
	public void removeAvailabilityRequirement_GetsOrInitializesARequirementSet() throws Exception {
		service.removeAvailabilityRequirement(USER_GROUP_ID, Weekday.TUESDAY.getId());
		verify(requirementSetDAO).getOrInitializeBy(
			"userGroup", userGroup,
			"company", company
		);
	}

	@Test
	public void removeAvailabilityRequirement_SavesOrUpdatesTheRequirementSet() {
		service.removeAvailabilityRequirement(USER_GROUP_ID, Weekday.TUESDAY.getId());
		verify(requirementSetDAO).saveOrUpdate(requirementSet);
	}

	@Test
	public void removeAvailabilityRequirement_GetsTheRequirementDAOBean() throws Exception {
		service.removeAvailabilityRequirement(USER_GROUP_ID, Weekday.TUESDAY.getId());
		verify(context, times(2)).getBean(AvailabilityRequirementDAO.class);
	}

	@Test
	public void removeAvailabilityRequirement_GetsOrInitializesARequirement() throws Exception {
		service.removeAvailabilityRequirement(USER_GROUP_ID, Weekday.TUESDAY.getId());
		verify(requirementDAO).getOrInitializeBy(
			"dayOfWeek", Weekday.TUESDAY.getId(),
			"requirementSet", requirementSet
		);
	}

	@Test
	public void removeAvailabilityRequirement_DeletesARequirement() throws Exception {
		service.removeAvailabilityRequirement(USER_GROUP_ID, Weekday.TUESDAY.getId());
		verify(requirementDAO).delete(availabilityRequirement);
	}

	@Test
	public void addBackgroundCheckRequirement_FindsTheUserGroup() throws Exception {
		service.addBackgroundCheckRequirement(USER_GROUP_ID);
		verify(userGroupDAO).findBy("id", USER_GROUP_ID);
	}

	@Test
	public void addBackgroundCheckRequirement_GetsOrInitializesARequirementSet() throws Exception {
		service.addBackgroundCheckRequirement(USER_GROUP_ID);
		verify(requirementSetDAO).getOrInitializeBy(
			"userGroup", userGroup,
			"company", company
		);
	}

	@Test
	public void addBackgroundCheckRequirement_WithAnUnnamedRequirementSet_NamesTheRequirementSet() throws Exception {
		service.addBackgroundCheckRequirement(USER_GROUP_ID);
		verify(requirementSet).setName("(Group) " + USER_GROUP_NAME);
	}

	@Test
	public void addBackgroundCheckRequirement_WithANamedRequirementSet_DoesNotNameTheRequirementSet() throws Exception {
		when(requirementSet.getName()).thenReturn("Some Name");
		service.addBackgroundCheckRequirement(USER_GROUP_ID);
		verify(requirementSet, never()).setName(any(String.class));
	}

	@Test
	public void addBackgroundCheckRequirement_SavesOrUpdatesTheRequirementSet() {
		service.addBackgroundCheckRequirement(USER_GROUP_ID);
		verify(requirementSetDAO).saveOrUpdate(requirementSet);
	}

	@Test
	public void addBackgroundCheckRequirement_GetsTheRequirementDAOBean() throws Exception {
		service.addBackgroundCheckRequirement(USER_GROUP_ID);
		verify(context, times(2)).getBean(BackgroundCheckRequirementDAO.class);
	}

	@Test
	public void addBackgroundCheckRequirement_GetsOrInitializesARequirement() throws Exception {
		service.addBackgroundCheckRequirement(USER_GROUP_ID);
		verify(requirementDAO).getOrInitializeBy(
			"requirementSet", requirementSet
		);
	}

	@Test
	public void addBackgroundCheckRequirement_SavesOrUpdatesARequirement() throws Exception {
		service.addBackgroundCheckRequirement(USER_GROUP_ID);
		verify(requirementDAO).saveOrUpdate(requirement);
	}

	@Test
	public void removeBackgroundCheckRequirement_FindsTheUserGroup() throws Exception {
		service.removeBackgroundCheckRequirement(USER_GROUP_ID);
		verify(userGroupDAO).findBy("id", USER_GROUP_ID);
	}

	@Test
	public void removeBackgroundCheckRequirement_GetsOrInitializesARequirementSet() throws Exception {
		service.removeBackgroundCheckRequirement(USER_GROUP_ID);
		verify(requirementSetDAO).getOrInitializeBy(
			"userGroup", userGroup,
			"company", company
		);
	}

	@Test
	public void removeBackgroundCheckRequirement_SavesOrUpdatesTheRequirementSet() {
		service.removeBackgroundCheckRequirement(USER_GROUP_ID);
		verify(requirementSetDAO).saveOrUpdate(requirementSet);
	}

	@Test
	public void removeBackgroundCheckRequirement_GetsTheRequirementDAOBean() throws Exception {
		service.removeBackgroundCheckRequirement(USER_GROUP_ID);
		verify(context, times(2)).getBean(BackgroundCheckRequirementDAO.class);
	}

	@Test
	public void removeBackgroundCheckRequirement_GetsOrInitializesARequirement() throws Exception {
		service.removeBackgroundCheckRequirement(USER_GROUP_ID);
		verify(requirementDAO).getOrInitializeBy(
			"requirementSet", requirementSet
		);
	}

	@Test
	public void removeBackgroundCheckRequirement_DeletesARequirement() throws Exception {
		service.removeBackgroundCheckRequirement(USER_GROUP_ID);
		verify(requirementDAO).delete(requirement);
	}

	@Test
	public void addCertificationRequirement_GetsTheRequirableDAOBean() throws Exception {
		service.addCertificationRequirement(USER_GROUP_ID, REQUIRABLE_ID, true, false);
		verify(context).getBean(CertificationRequirableDAO.class);
	}

	@Test
	public void addCertificationRequirement_FindsTheRequirable() throws Exception {
		service.addCertificationRequirement(USER_GROUP_ID, REQUIRABLE_ID, true, false);
		verify(requirableDAO).findBy("id", REQUIRABLE_ID);
	}

	@Test
	public void addCertificationRequirement_FindsTheUserGroup() throws Exception {
		service.addCertificationRequirement(USER_GROUP_ID, REQUIRABLE_ID, true, false);
		verify(userGroupDAO).findBy("id", USER_GROUP_ID);
	}

	@Test
	public void addCertificationRequirement_GetsOrInitializesARequirementSet() throws Exception {
		service.addCertificationRequirement(USER_GROUP_ID, REQUIRABLE_ID, true, false);
		verify(requirementSetDAO).getOrInitializeBy(
			"userGroup", userGroup,
			"company", company
		);
	}

	@Test
	public void addCertificationRequirement_WithAnUnnamedRequirementSet_NamesTheRequirementSet() throws Exception {
		service.addCertificationRequirement(USER_GROUP_ID, REQUIRABLE_ID, true, false);
		verify(requirementSet).setName("(Group) " + USER_GROUP_NAME);
	}

	@Test
	public void addCertificationRequirement_WithANamedRequirementSet_DoesNotNameTheRequirementSet() throws Exception {
		when(requirementSet.getName()).thenReturn("Some Name");
		service.addCertificationRequirement(USER_GROUP_ID, REQUIRABLE_ID, true, false);
		verify(requirementSet, never()).setName(any(String.class));
	}

	@Test
	public void addCertificationRequirement_SavesOrUpdatesTheRequirementSet() {
		service.addCertificationRequirement(USER_GROUP_ID, REQUIRABLE_ID, true, false);
		verify(requirementSetDAO).saveOrUpdate(requirementSet);
	}

	@Test
	public void addCertificationRequirement_GetsTheRequirementDAOBean() throws Exception {
		service.addCertificationRequirement(USER_GROUP_ID, REQUIRABLE_ID, true, false);
		verify(context, times(2)).getBean(CertificationRequirementDAO.class);
	}

	@Test
	public void addCertificationRequirement_GetsOrInitializesARequirement() throws Exception {
		service.addCertificationRequirement(USER_GROUP_ID, REQUIRABLE_ID, true, false);
		verify(requirementDAO).getOrInitializeBy(
			"certificationRequirable", requirable,
			"requirementSet", requirementSet
		);
	}

	@Test
	public void addCertificationRequirement_SavesOrUpdatesARequirement() throws Exception {
		service.addCertificationRequirement(USER_GROUP_ID, REQUIRABLE_ID, true, false);
		verify(requirementDAO).saveOrUpdate(certificationRequirement);
	}

	@Test
	public void addCertificationRequirement_SetsNotifyOnExpiry() throws Exception {
		service.addCertificationRequirement(USER_GROUP_ID, REQUIRABLE_ID, true, false);
		verify(certificationRequirement).setNotifyOnExpiry(true);
	}

	@Test
	public void addCertificationRequirement_SetsRemoveMembershipOnExpiry() throws Exception {
		service.addCertificationRequirement(USER_GROUP_ID, REQUIRABLE_ID, true, false);
		verify(certificationRequirement).setRemoveMembershipOnExpiry(false);
	}

	@Test
	public void removeCertificationRequirement_GetsTheRequirableDAOBean() throws Exception {
		service.removeCertificationRequirement(USER_GROUP_ID, REQUIRABLE_ID);
		verify(context).getBean(CertificationRequirableDAO.class);
	}

	@Test
	public void removeCertificationRequirement_FindsTheRequirable() throws Exception {
		service.removeCertificationRequirement(USER_GROUP_ID, REQUIRABLE_ID);
		verify(requirableDAO).findBy("id", REQUIRABLE_ID);
	}

	@Test
	public void removeCertificationRequirement_FindsTheUserGroup() throws Exception {
		service.removeCertificationRequirement(USER_GROUP_ID, REQUIRABLE_ID);
		verify(userGroupDAO).findBy("id", USER_GROUP_ID);
	}

	@Test
	public void removeCertificationRequirement_GetsOrInitializesARequirementSet() throws Exception {
		service.removeCertificationRequirement(USER_GROUP_ID, REQUIRABLE_ID);
		verify(requirementSetDAO).getOrInitializeBy(
			"userGroup", userGroup,
			"company", company
		);
	}

	@Test
	public void removeCertificationRequirement_SavesOrUpdatesTheRequirementSet() {
		service.removeCertificationRequirement(USER_GROUP_ID, REQUIRABLE_ID);
		verify(requirementSetDAO).saveOrUpdate(requirementSet);
	}

	@Test
	public void removeCertificationRequirement_GetsTheRequirementDAOBean() throws Exception {
		service.removeCertificationRequirement(USER_GROUP_ID, REQUIRABLE_ID);
		verify(context, times(2)).getBean(CertificationRequirementDAO.class);
	}

	@Test
	public void removeCertificationRequirement_GetsOrInitializesARequirement() throws Exception {
		service.removeCertificationRequirement(USER_GROUP_ID, REQUIRABLE_ID);
		verify(requirementDAO).getOrInitializeBy(
			"certificationRequirable", requirable,
			"requirementSet", requirementSet
		);
	}

	@Test
	public void removeCertificationRequirement_DeletesARequirement() throws Exception {
		service.removeCertificationRequirement(USER_GROUP_ID, REQUIRABLE_ID);
		verify(requirementDAO).delete(certificationRequirement);
	}

	@Test
	public void addCountryRequirement_GetsTheRequirableDAOBean() throws Exception {
		service.addCountryRequirement(USER_GROUP_ID, Lists.newArrayList(COUNTRY_ID));
		verify(context).getBean(CountryRequirableDAO.class);
	}

	@Test
	public void addCountryRequirement_FindsTheRequirable() throws Exception {
		service.addCountryRequirement(USER_GROUP_ID, Lists.newArrayList(COUNTRY_ID));
		verify(requirableDAO).findBy("id", COUNTRY_ID);
	}

	@Test
	public void addCountryRequirement_FindsTheUserGroup() throws Exception {
		service.addCountryRequirement(USER_GROUP_ID, Lists.newArrayList(COUNTRY_ID));
		verify(userGroupDAO).findBy("id", USER_GROUP_ID);
	}

	@Test
	public void addCountryRequirement_GetsOrInitializesARequirementSet() throws Exception {
		service.addCountryRequirement(USER_GROUP_ID, Lists.newArrayList(COUNTRY_ID));
		verify(requirementSetDAO).getOrInitializeBy(
			"userGroup", userGroup,
			"company", company
		);
	}

	@Test
	public void addCountryRequirement_WithAnUnnamedRequirementSet_NamesTheRequirementSet() throws Exception {
		service.addCountryRequirement(USER_GROUP_ID, Lists.newArrayList(COUNTRY_ID));
		verify(requirementSet).setName("(Group) " + USER_GROUP_NAME);
	}

	@Test
	public void addCountryRequirement_WithANamedRequirementSet_DoesNotNameTheRequirementSet() throws Exception {
		when(requirementSet.getName()).thenReturn("Some Name");
		service.addCountryRequirement(USER_GROUP_ID, Lists.newArrayList(COUNTRY_ID));
		verify(requirementSet, never()).setName(any(String.class));
	}

	@Test
	public void addCountryRequirement_SavesOrUpdatesTheRequirementSet() {
		service.addCountryRequirement(USER_GROUP_ID, Lists.newArrayList(COUNTRY_ID));
		verify(requirementSetDAO).saveOrUpdate(requirementSet);
	}

	@Test
	public void addCountryRequirement_GetsTheRequirementDAOBean() throws Exception {
		service.addCountryRequirement(USER_GROUP_ID, Lists.newArrayList(COUNTRY_ID));
		verify(context, times(2)).getBean(CountryRequirementDAO.class);
	}

	@Test
	public void addCountryRequirement_GetsOrInitializesARequirement() throws Exception {
		service.addCountryRequirement(USER_GROUP_ID, Lists.newArrayList(COUNTRY_ID));
		verify(requirementDAO).getOrInitializeBy(
			"countryRequirable", requirable,
			"requirementSet", requirementSet
		);
	}

	@Test
	public void addCountryRequirement_SavesOrUpdatesARequirement() throws Exception {
		service.addCountryRequirement(USER_GROUP_ID, Lists.newArrayList(COUNTRY_ID));
		verify(requirementDAO).saveOrUpdate(requirement);
	}

	@Test
	public void removeCountryRequirement_GetsTheRequirableDAOBean() throws Exception {
		service.removeCountryRequirement(USER_GROUP_ID, COUNTRY_ID);
		verify(context).getBean(CountryRequirableDAO.class);
	}

	@Test
	public void removeCountryRequirement_FindsTheRequirable() throws Exception {
		service.removeCountryRequirement(USER_GROUP_ID, COUNTRY_ID);
		verify(requirableDAO).findBy("id", COUNTRY_ID);
	}

	@Test
	public void removeCountryRequirement_FindsTheUserGroup() throws Exception {
		service.removeCountryRequirement(USER_GROUP_ID, COUNTRY_ID);
		verify(userGroupDAO).findBy("id", USER_GROUP_ID);
	}

	@Test
	public void removeCountryRequirement_GetsOrInitializesARequirementSet() throws Exception {
		service.removeCountryRequirement(USER_GROUP_ID, COUNTRY_ID);
		verify(requirementSetDAO).getOrInitializeBy(
			"userGroup", userGroup,
			"company", company
		);
	}

	@Test
	public void removeCountryRequirement_SavesOrUpdatesTheRequirementSet() {
		service.removeCountryRequirement(USER_GROUP_ID, COUNTRY_ID);
		verify(requirementSetDAO).saveOrUpdate(requirementSet);
	}

	@Test
	public void removeCountryRequirement_GetsTheRequirementDAOBean() throws Exception {
		service.removeCountryRequirement(USER_GROUP_ID, COUNTRY_ID);
		verify(context, times(2)).getBean(CountryRequirementDAO.class);
	}

	@Test
	public void removeCountryRequirement_GetsOrInitializesARequirement() throws Exception {
		service.removeCountryRequirement(USER_GROUP_ID, COUNTRY_ID);
		verify(requirementDAO).getOrInitializeBy(
			"countryRequirable", requirable,
			"requirementSet", requirementSet
		);
	}

	@Test
	public void removeCountryRequirement_DeletesARequirement() throws Exception {
		service.removeCountryRequirement(USER_GROUP_ID, COUNTRY_ID);
		verify(requirementDAO).delete(requirement);
	}

	@Test
	public void addCompanyTypeRequirement_FindsTheUserGroup() throws Exception {
		service.addCompanyTypeRequirement(USER_GROUP_ID, REQUIRABLE_ID);
		verify(userGroupDAO).findBy("id", USER_GROUP_ID);
	}

	@Test
	public void addCompanyTypeRequirement_GetsOrInitializesARequirementSet() throws Exception {
		service.addCompanyTypeRequirement(USER_GROUP_ID, REQUIRABLE_ID);
		verify(requirementSetDAO).getOrInitializeBy(
			"userGroup", userGroup,
			"company", company
		);
	}

	@Test
	public void addCompanyTypeRequirement_WithAnUnnamedRequirementSet_NamesTheRequirementSet() throws Exception {
		service.addCompanyTypeRequirement(USER_GROUP_ID, REQUIRABLE_ID);
		verify(requirementSet).setName("(Group) " + USER_GROUP_NAME);
	}

	@Test
	public void addCompanyTypeRequirement_WithANamedRequirementSet_DoesNotNameTheRequirementSet() throws Exception {
		when(requirementSet.getName()).thenReturn("Some Name");
		service.addCompanyTypeRequirement(USER_GROUP_ID, REQUIRABLE_ID);
		verify(requirementSet, never()).setName(any(String.class));
	}

	@Test
	public void addCompanyTypeRequirement_SavesOrUpdatesTheRequirementSet() {
		service.addCompanyTypeRequirement(USER_GROUP_ID, REQUIRABLE_ID);
		verify(requirementSetDAO).saveOrUpdate(requirementSet);
	}

	@Test
	public void addCompanyTypeRequirement_GetsTheRequirementDAOBean() throws Exception {
		service.addCompanyTypeRequirement(USER_GROUP_ID, REQUIRABLE_ID);
		verify(context, times(2)).getBean(CompanyTypeRequirementDAO.class);
	}

	@Test
	public void addCompanyTypeRequirement_GetsOrInitializesARequirement() throws Exception {
		service.addCompanyTypeRequirement(USER_GROUP_ID, REQUIRABLE_ID);
		verify(companyTypeRequirementDAO).getOrInitializeBy(
			"requirementSet", requirementSet
		);
	}

	@Test
	public void addCompanyTypeRequirement_SavesOrUpdatesARequirement() throws Exception {
		service.addCompanyTypeRequirement(USER_GROUP_ID, REQUIRABLE_ID);
		verify(companyTypeRequirementDAO).saveOrUpdate(companyTypeRequirement);
	}

	@Test
	public void addCompanyTypeRequirement_SetsTheCompanyTypeId() throws Exception {
		service.addCompanyTypeRequirement(USER_GROUP_ID, REQUIRABLE_ID);
		verify(companyTypeRequirement).setCompanyTypeId(REQUIRABLE_ID);
	}

	@Test
	public void removeCompanyTypeRequirement_FindsTheUserGroup() throws Exception {
		service.removeCompanyTypeRequirement(USER_GROUP_ID);
		verify(userGroupDAO).findBy("id", USER_GROUP_ID);
	}

	@Test
	public void removeCompanyTypeRequirement_GetsOrInitializesARequirementSet() throws Exception {
		service.removeCompanyTypeRequirement(USER_GROUP_ID);
		verify(requirementSetDAO).getOrInitializeBy(
			"userGroup", userGroup,
			"company", company
		);
	}

	@Test
	public void removeCompanyTypeRequirement_SavesOrUpdatesTheRequirementSet() {
		service.removeCompanyTypeRequirement(USER_GROUP_ID);
		verify(requirementSetDAO).saveOrUpdate(requirementSet);
	}

	@Test
	public void removeCompanyTypeRequirement_GetsTheRequirementDAOBean() throws Exception {
		service.removeCompanyTypeRequirement(USER_GROUP_ID);
		verify(context, times(2)).getBean(CompanyTypeRequirementDAO.class);
	}

	@Test
	public void removeCompanyTypeRequirement_GetsOrInitializesARequirement() throws Exception {
		service.removeCompanyTypeRequirement(USER_GROUP_ID);
		verify(companyTypeRequirementDAO).getOrInitializeBy(
			"requirementSet", requirementSet
		);
	}

	@Test
	public void removeCompanyTypeRequirement_DeletesARequirement() throws Exception {
		service.removeCompanyTypeRequirement(USER_GROUP_ID);
		verify(companyTypeRequirementDAO).delete(companyTypeRequirement);
	}

	@Test
	public void addDocumentRequirement_GetsTheRequirableDAOBean() throws Exception {
		service.addDocumentRequirement(USER_GROUP_ID, REQUIRABLE_ID, true);
		verify(context).getBean(DocumentRequirableDAO.class);
	}

	@Test
	public void addDocumentRequirement_FindsTheRequirable() throws Exception {
		service.addDocumentRequirement(USER_GROUP_ID, REQUIRABLE_ID, true);
		verify(requirableDAO).findBy("id", REQUIRABLE_ID);
	}

	@Test
	public void addDocumentRequirement_FindsTheUserGroup() throws Exception {
		service.addDocumentRequirement(USER_GROUP_ID, REQUIRABLE_ID, true);
		verify(userGroupDAO).findBy("id", USER_GROUP_ID);
	}

	@Test
	public void addDocumentRequirement_GetsOrInitializesARequirementSet() throws Exception {
		service.addDocumentRequirement(USER_GROUP_ID, REQUIRABLE_ID, true);
		verify(requirementSetDAO).getOrInitializeBy(
			"userGroup", userGroup,
			"company", company
		);
	}

	@Test
	public void addDocumentRequirement_WithAnUnnamedRequirementSet_NamesTheRequirementSet() throws Exception {
		service.addDocumentRequirement(USER_GROUP_ID, REQUIRABLE_ID, true);
		verify(requirementSet).setName("(Group) " + USER_GROUP_NAME);
	}

	@Test
	public void addDocumentRequirement_WithANamedRequirementSet_DoesNotNameTheRequirementSet() throws Exception {
		when(requirementSet.getName()).thenReturn("Some Name");
		service.addDocumentRequirement(USER_GROUP_ID, REQUIRABLE_ID, true);
		verify(requirementSet, never()).setName(any(String.class));
	}

	@Test
	public void addDocumentRequirement_SavesOrUpdatesTheRequirementSet() {
		service.addDocumentRequirement(USER_GROUP_ID, REQUIRABLE_ID, true);
		verify(requirementSetDAO).saveOrUpdate(requirementSet);
	}

	@Test
	public void addDocumentRequirement_GetsTheRequirementDAOBean() throws Exception {
		service.addDocumentRequirement(USER_GROUP_ID, REQUIRABLE_ID, true);
		verify(context, times(2)).getBean(DocumentRequirementDAO.class);
	}

	@Test
	public void addDocumentRequirement_GetsOrInitializesARequirement() throws Exception {
		service.addDocumentRequirement(USER_GROUP_ID, REQUIRABLE_ID, true);
		verify(requirementDAO).getOrInitializeBy(
			"documentRequirable", requirable,
			"requirementSet", requirementSet
		);
	}

	@Test
	public void addDocumentRequirement_SavesOrUpdatesARequirement() throws Exception {
		service.addDocumentRequirement(USER_GROUP_ID, REQUIRABLE_ID, true);
		verify(requirementDAO).saveOrUpdate(documentRequirement);
	}

	@Test
	public void addDocumentRequirement_SetsRequiresExpirationDate() throws Exception {
		service.addDocumentRequirement(USER_GROUP_ID, REQUIRABLE_ID, true);
		verify(documentRequirement).setRequiresExpirationDate(true);
	}

	@Test
	public void removeDocumentRequirement_GetsTheRequirableDAOBean() throws Exception {
		service.removeDocumentRequirement(USER_GROUP_ID, REQUIRABLE_ID);
		verify(context).getBean(DocumentRequirableDAO.class);
	}

	@Test
	public void removeDocumentRequirement_FindsTheRequirable() throws Exception {
		service.removeDocumentRequirement(USER_GROUP_ID, REQUIRABLE_ID);
		verify(requirableDAO).findBy("id", REQUIRABLE_ID);
	}

	@Test
	public void removeDocumentRequirement_FindsTheUserGroup() throws Exception {
		service.removeDocumentRequirement(USER_GROUP_ID, REQUIRABLE_ID);
		verify(userGroupDAO).findBy("id", USER_GROUP_ID);
	}

	@Test
	public void removeDocumentRequirement_GetsOrInitializesARequirementSet() throws Exception {
		service.removeDocumentRequirement(USER_GROUP_ID, REQUIRABLE_ID);
		verify(requirementSetDAO).getOrInitializeBy(
			"userGroup", userGroup,
			"company", company
		);
	}

	@Test
	public void removeDocumentRequirement_SavesOrUpdatesTheRequirementSet() {
		service.removeDocumentRequirement(USER_GROUP_ID, REQUIRABLE_ID);
		verify(requirementSetDAO).saveOrUpdate(requirementSet);
	}

	@Test
	public void removeDocumentRequirement_GetsTheRequirementDAOBean() throws Exception {
		service.removeDocumentRequirement(USER_GROUP_ID, REQUIRABLE_ID);
		verify(context, times(2)).getBean(DocumentRequirementDAO.class);
	}

	@Test
	public void removeDocumentRequirement_GetsOrInitializesARequirement() throws Exception {
		service.removeDocumentRequirement(USER_GROUP_ID, REQUIRABLE_ID);
		verify(requirementDAO).getOrInitializeBy(
			"documentRequirable", requirable,
			"requirementSet", requirementSet
		);
	}

	@Test
	public void removeDocumentRequirement_DeletesARequirement() throws Exception {
		service.removeDocumentRequirement(USER_GROUP_ID, REQUIRABLE_ID);
		verify(requirementDAO).delete(documentRequirement);
	}

	@Test
	public void addDrugTestRequirement_FindsTheUserGroup() throws Exception {
		service.addDrugTestRequirement(USER_GROUP_ID);
		verify(userGroupDAO).findBy("id", USER_GROUP_ID);
	}

	@Test
	public void addDrugTestRequirement_GetsOrInitializesARequirementSet() throws Exception {
		service.addDrugTestRequirement(USER_GROUP_ID);
		verify(requirementSetDAO).getOrInitializeBy(
			"userGroup", userGroup,
			"company", company
		);
	}

	@Test
	public void addDrugTestRequirement_WithAnUnnamedRequirementSet_NamesTheRequirementSet() throws Exception {
		service.addDrugTestRequirement(USER_GROUP_ID);
		verify(requirementSet).setName("(Group) " + USER_GROUP_NAME);
	}

	@Test
	public void addDrugTestRequirement_WithANamedRequirementSet_DoesNotNameTheRequirementSet() throws Exception {
		when(requirementSet.getName()).thenReturn("Some Name");
		service.addDrugTestRequirement(USER_GROUP_ID);
		verify(requirementSet, never()).setName(any(String.class));
	}

	@Test
	public void addDrugTestRequirement_SavesOrUpdatesTheRequirementSet() {
		service.addDrugTestRequirement(USER_GROUP_ID);
		verify(requirementSetDAO).saveOrUpdate(requirementSet);
	}

	@Test
	public void addDrugTestRequirement_GetsTheRequirementDAOBean() throws Exception {
		service.addDrugTestRequirement(USER_GROUP_ID);
		verify(context, times(2)).getBean(DrugTestRequirementDAO.class);
	}

	@Test
	public void addDrugTestRequirement_GetsOrInitializesARequirement() throws Exception {
		service.addDrugTestRequirement(USER_GROUP_ID);
		verify(requirementDAO).getOrInitializeBy(
			"requirementSet", requirementSet
		);
	}

	@Test
	public void addDrugTestRequirement_SavesOrUpdatesARequirement() throws Exception {
		service.addDrugTestRequirement(USER_GROUP_ID);
		verify(requirementDAO).saveOrUpdate(requirement);
	}

	@Test
	public void removeDrugTestRequirement_FindsTheUserGroup() throws Exception {
		service.removeDrugTestRequirement(USER_GROUP_ID);
		verify(userGroupDAO).findBy("id", USER_GROUP_ID);
	}

	@Test
	public void removeDrugTestRequirement_GetsOrInitializesARequirementSet() throws Exception {
		service.removeDrugTestRequirement(USER_GROUP_ID);
		verify(requirementSetDAO).getOrInitializeBy(
			"userGroup", userGroup,
			"company", company
		);
	}

	@Test
	public void removeDrugTestRequirement_SavesOrUpdatesTheRequirementSet() {
		service.removeDrugTestRequirement(USER_GROUP_ID);
		verify(requirementSetDAO).saveOrUpdate(requirementSet);
	}

	@Test
	public void removeDrugTestRequirement_GetsTheRequirementDAOBean() throws Exception {
		service.removeDrugTestRequirement(USER_GROUP_ID);
		verify(context, times(2)).getBean(DrugTestRequirementDAO.class);
	}

	@Test
	public void removeDrugTestRequirement_GetsOrInitializesARequirement() throws Exception {
		service.removeDrugTestRequirement(USER_GROUP_ID);
		verify(requirementDAO).getOrInitializeBy(
			"requirementSet", requirementSet
		);
	}

	@Test
	public void removeDrugTestRequirement_DeletesARequirement() throws Exception {
		service.removeDrugTestRequirement(USER_GROUP_ID);
		verify(requirementDAO).delete(requirement);
	}

	@Test
	public void addIndustryRequirement_GetsTheRequirableDAOBean() throws Exception {
		service.addIndustryRequirement(USER_GROUP_ID, REQUIRABLE_ID);
		verify(context).getBean(IndustryRequirableDAO.class);
	}

	@Test
	public void addIndustryRequirement_FindsTheRequirable() throws Exception {
		service.addIndustryRequirement(USER_GROUP_ID, REQUIRABLE_ID);
		verify(requirableDAO).findBy("id", REQUIRABLE_ID);
	}

	@Test
	public void addIndustryRequirement_FindsTheUserGroup() throws Exception {
		service.addIndustryRequirement(USER_GROUP_ID, REQUIRABLE_ID);
		verify(userGroupDAO).findBy("id", USER_GROUP_ID);
	}

	@Test
	public void addIndustryRequirement_GetsOrInitializesARequirementSet() throws Exception {
		service.addIndustryRequirement(USER_GROUP_ID, REQUIRABLE_ID);
		verify(requirementSetDAO).getOrInitializeBy(
			"userGroup", userGroup,
			"company", company
		);
	}

	@Test
	public void addIndustryRequirement_WithAnUnnamedRequirementSet_NamesTheRequirementSet() throws Exception {
		service.addIndustryRequirement(USER_GROUP_ID, REQUIRABLE_ID);
		verify(requirementSet).setName("(Group) " + USER_GROUP_NAME);
	}

	@Test
	public void addIndustryRequirement_WithANamedRequirementSet_DoesNotNameTheRequirementSet() throws Exception {
		when(requirementSet.getName()).thenReturn("Some Name");
		service.addIndustryRequirement(USER_GROUP_ID, REQUIRABLE_ID);
		verify(requirementSet, never()).setName(any(String.class));
	}

	@Test
	public void addIndustryRequirement_SavesOrUpdatesTheRequirementSet() {
		service.addIndustryRequirement(USER_GROUP_ID, REQUIRABLE_ID);
		verify(requirementSetDAO).saveOrUpdate(requirementSet);
	}

	@Test
	public void addIndustryRequirement_GetsTheRequirementDAOBean() throws Exception {
		service.addIndustryRequirement(USER_GROUP_ID, REQUIRABLE_ID);
		verify(context, times(2)).getBean(IndustryRequirementDAO.class);
	}

	@Test
	public void addIndustryRequirement_GetsOrInitializesARequirement() throws Exception {
		service.addIndustryRequirement(USER_GROUP_ID, REQUIRABLE_ID);
		verify(requirementDAO).getOrInitializeBy(
			"industryRequirable", requirable,
			"requirementSet", requirementSet
		);
	}

	@Test
	public void addIndustryRequirement_SavesOrUpdatesARequirement() throws Exception {
		service.addIndustryRequirement(USER_GROUP_ID, REQUIRABLE_ID);
		verify(requirementDAO).saveOrUpdate(requirement);
	}

	@Test
	public void removeIndustryRequirement_GetsTheRequirableDAOBean() throws Exception {
		service.removeIndustryRequirement(USER_GROUP_ID, REQUIRABLE_ID);
		verify(context).getBean(IndustryRequirableDAO.class);
	}

	@Test
	public void removeIndustryRequirement_FindsTheRequirable() throws Exception {
		service.removeIndustryRequirement(USER_GROUP_ID, REQUIRABLE_ID);
		verify(requirableDAO).findBy("id", REQUIRABLE_ID);
	}

	@Test
	public void removeIndustryRequirement_FindsTheUserGroup() throws Exception {
		service.removeIndustryRequirement(USER_GROUP_ID, REQUIRABLE_ID);
		verify(userGroupDAO).findBy("id", USER_GROUP_ID);
	}

	@Test
	public void removeIndustryRequirement_GetsOrInitializesARequirementSet() throws Exception {
		service.removeIndustryRequirement(USER_GROUP_ID, REQUIRABLE_ID);
		verify(requirementSetDAO).getOrInitializeBy(
			"userGroup", userGroup,
			"company", company
		);
	}

	@Test
	public void removeIndustryRequirement_SavesOrUpdatesTheRequirementSet() {
		service.removeIndustryRequirement(USER_GROUP_ID, REQUIRABLE_ID);
		verify(requirementSetDAO).saveOrUpdate(requirementSet);
	}

	@Test
	public void removeIndustryRequirement_GetsTheRequirementDAOBean() throws Exception {
		service.removeIndustryRequirement(USER_GROUP_ID, REQUIRABLE_ID);
		verify(context, times(2)).getBean(IndustryRequirementDAO.class);
	}

	@Test
	public void removeIndustryRequirement_GetsOrInitializesARequirement() throws Exception {
		service.removeIndustryRequirement(USER_GROUP_ID, REQUIRABLE_ID);
		verify(requirementDAO).getOrInitializeBy(
			"industryRequirable", requirable,
			"requirementSet", requirementSet
		);
	}

	@Test
	public void removeIndustryRequirement_DeletesARequirement() throws Exception {
		service.removeIndustryRequirement(USER_GROUP_ID, REQUIRABLE_ID);
		verify(requirementDAO).delete(requirement);
	}

	@Test
	public void addInsuranceRequirement_GetsTheRequirableDAOBean() throws Exception {
		service.addInsuranceRequirement(USER_GROUP_ID, REQUIRABLE_ID, COVERAGE_AMOUNT, true, false);
		verify(context).getBean(InsuranceRequirableDAO.class);
	}

	@Test
	public void addInsuranceRequirement_FindsTheRequirable() throws Exception {
		service.addInsuranceRequirement(USER_GROUP_ID, REQUIRABLE_ID, COVERAGE_AMOUNT, true, false);
		verify(requirableDAO).findBy("id", REQUIRABLE_ID);
	}

	@Test
	public void addInsuranceRequirement_FindsTheUserGroup() throws Exception {
		service.addInsuranceRequirement(USER_GROUP_ID, REQUIRABLE_ID, COVERAGE_AMOUNT, true, false);
		verify(userGroupDAO).findBy("id", USER_GROUP_ID);
	}

	@Test
	public void addInsuranceRequirement_GetsOrInitializesARequirementSet() throws Exception {
		service.addInsuranceRequirement(USER_GROUP_ID, REQUIRABLE_ID, COVERAGE_AMOUNT, true, false);
		verify(requirementSetDAO).getOrInitializeBy(
			"userGroup", userGroup,
			"company", company
		);
	}

	@Test
	public void addInsuranceRequirement_WithAnUnnamedRequirementSet_NamesTheRequirementSet() throws Exception {
		service.addInsuranceRequirement(USER_GROUP_ID, REQUIRABLE_ID, COVERAGE_AMOUNT, true, false);
		verify(requirementSet).setName("(Group) " + USER_GROUP_NAME);
	}

	@Test
	public void addInsuranceRequirement_WithANamedRequirementSet_DoesNotNameTheRequirementSet() throws Exception {
		when(requirementSet.getName()).thenReturn("Some Name");
		service.addInsuranceRequirement(USER_GROUP_ID, REQUIRABLE_ID, COVERAGE_AMOUNT, true, false);
		verify(requirementSet, never()).setName(any(String.class));
	}

	@Test
	public void addInsuranceRequirement_SavesOrUpdatesTheRequirementSet() {
		service.addInsuranceRequirement(USER_GROUP_ID, REQUIRABLE_ID, COVERAGE_AMOUNT, true, false);
		verify(requirementSetDAO).saveOrUpdate(requirementSet);
	}

	@Test
	public void addInsuranceRequirement_GetsTheRequirementDAOBean() throws Exception {
		service.addInsuranceRequirement(USER_GROUP_ID, REQUIRABLE_ID, COVERAGE_AMOUNT, true, false);
		verify(context, times(2)).getBean(InsuranceRequirementDAO.class);
	}

	@Test
	public void addInsuranceRequirement_GetsOrInitializesARequirement() throws Exception {
		service.addInsuranceRequirement(USER_GROUP_ID, REQUIRABLE_ID, COVERAGE_AMOUNT, true, false);
		verify(requirementDAO).getOrInitializeBy(
			"insuranceRequirable", requirable,
			"requirementSet", requirementSet
		);
	}

	@Test
	public void addInsuranceRequirement_SavesOrUpdatesARequirement() throws Exception {
		service.addInsuranceRequirement(USER_GROUP_ID, REQUIRABLE_ID, COVERAGE_AMOUNT, true, false);
		verify(requirementDAO).saveOrUpdate(insuranceRequirement);
	}

	@Test
	public void addInsuranceRequirement_WithMinimumCoverage_SetsMinimumCoverage() throws Exception {
		service.addInsuranceRequirement(USER_GROUP_ID, REQUIRABLE_ID, COVERAGE_AMOUNT, true, false);
		verify(insuranceRequirement).setMinimumCoverageAmount(COVERAGE_AMOUNT);
	}

	@Test
	public void addInsuranceRequirement_WithNullMinimumCoverage_DoesntSetMinimumCoverage() throws Exception {
		service.addInsuranceRequirement(USER_GROUP_ID, REQUIRABLE_ID, null, true, false);
		verify(insuranceRequirement, never()).setMinimumCoverageAmount(null);
	}

	@Test
	public void addInsuranceRequirement_SetsNotifyOnExpiry() throws Exception {
		service.addInsuranceRequirement(USER_GROUP_ID, REQUIRABLE_ID, COVERAGE_AMOUNT, true, false);
		verify(insuranceRequirement).setNotifyOnExpiry(true);
	}

	@Test
	public void addInsuranceRequirement_SetsRemoveMembershipOnExpiry() throws Exception {
		service.addInsuranceRequirement(USER_GROUP_ID, REQUIRABLE_ID, COVERAGE_AMOUNT, true, false);
		verify(insuranceRequirement).setRemoveMembershipOnExpiry(false);
	}

	@Test
	public void removeInsuranceRequirement_GetsTheRequirableDAOBean() throws Exception {
		service.removeInsuranceRequirement(USER_GROUP_ID, REQUIRABLE_ID);
		verify(context).getBean(InsuranceRequirableDAO.class);
	}

	@Test
	public void removeInsuranceRequirement_FindsTheRequirable() throws Exception {
		service.removeInsuranceRequirement(USER_GROUP_ID, REQUIRABLE_ID);
		verify(requirableDAO).findBy("id", REQUIRABLE_ID);
	}

	@Test
	public void removeInsuranceRequirement_FindsTheUserGroup() throws Exception {
		service.removeInsuranceRequirement(USER_GROUP_ID, REQUIRABLE_ID);
		verify(userGroupDAO).findBy("id", USER_GROUP_ID);
	}

	@Test
	public void removeInsuranceRequirement_GetsOrInitializesARequirementSet() throws Exception {
		service.removeInsuranceRequirement(USER_GROUP_ID, REQUIRABLE_ID);
		verify(requirementSetDAO).getOrInitializeBy(
			"userGroup", userGroup,
			"company", company
		);
	}

	@Test
	public void removeInsuranceRequirement_SavesOrUpdatesTheRequirementSet() {
		service.removeInsuranceRequirement(USER_GROUP_ID, REQUIRABLE_ID);
		verify(requirementSetDAO).saveOrUpdate(requirementSet);
	}

	@Test
	public void removeInsuranceRequirement_GetsTheRequirementDAOBean() throws Exception {
		service.removeInsuranceRequirement(USER_GROUP_ID, REQUIRABLE_ID);
		verify(context, times(2)).getBean(InsuranceRequirementDAO.class);
	}

	@Test
	public void removeInsuranceRequirement_GetsOrInitializesARequirement() throws Exception {
		service.removeInsuranceRequirement(USER_GROUP_ID, REQUIRABLE_ID);
		verify(requirementDAO).getOrInitializeBy(
			"insuranceRequirable", requirable,
			"requirementSet", requirementSet
		);
	}

	@Test
	public void removeInsuranceRequirement_DeletesARequirement() throws Exception {
		service.removeInsuranceRequirement(USER_GROUP_ID, REQUIRABLE_ID);
		verify(requirementDAO).delete(insuranceRequirement);
	}

	@Test
	public void addLicenseRequirement_GetsTheRequirableDAOBean() throws Exception {
		service.addLicenseRequirement(USER_GROUP_ID, REQUIRABLE_ID, true, false);
		verify(context).getBean(LicenseRequirableDAO.class);
	}

	@Test
	public void addLicenseRequirement_FindsTheRequirable() throws Exception {
		service.addLicenseRequirement(USER_GROUP_ID, REQUIRABLE_ID, true, false);
		verify(requirableDAO).findBy("id", REQUIRABLE_ID);
	}

	@Test
	public void addLicenseRequirement_FindsTheUserGroup() throws Exception {
		service.addLicenseRequirement(USER_GROUP_ID, REQUIRABLE_ID, true, false);
		verify(userGroupDAO).findBy("id", USER_GROUP_ID);
	}

	@Test
	public void addLicenseRequirement_GetsOrInitializesARequirementSet() throws Exception {
		service.addLicenseRequirement(USER_GROUP_ID, REQUIRABLE_ID, true, false);
		verify(requirementSetDAO).getOrInitializeBy(
			"userGroup", userGroup,
			"company", company
		);
	}

	@Test
	public void addLicenseRequirement_WithAnUnnamedRequirementSet_NamesTheRequirementSet() throws Exception {
		service.addLicenseRequirement(USER_GROUP_ID, REQUIRABLE_ID, true, false);
		verify(requirementSet).setName("(Group) " + USER_GROUP_NAME);
	}

	@Test
	public void addLicenseRequirement_WithANamedRequirementSet_DoesNotNameTheRequirementSet() throws Exception {
		when(requirementSet.getName()).thenReturn("Some Name");
		service.addLicenseRequirement(USER_GROUP_ID, REQUIRABLE_ID, true, false);
		verify(requirementSet, never()).setName(any(String.class));
	}

	@Test
	public void addLicenseRequirement_SavesOrUpdatesTheRequirementSet() {
		service.addLicenseRequirement(USER_GROUP_ID, REQUIRABLE_ID, true, false);
		verify(requirementSetDAO).saveOrUpdate(requirementSet);
	}

	@Test
	public void addLicenseRequirement_GetsTheRequirementDAOBean() throws Exception {
		service.addLicenseRequirement(USER_GROUP_ID, REQUIRABLE_ID, true, false);
		verify(context, times(2)).getBean(LicenseRequirementDAO.class);
	}

	@Test
	public void addLicenseRequirement_GetsOrInitializesARequirement() throws Exception {
		service.addLicenseRequirement(USER_GROUP_ID, REQUIRABLE_ID, true, false);
		verify(requirementDAO).getOrInitializeBy(
			"licenseRequirable", requirable,
			"requirementSet", requirementSet
		);
	}

	@Test
	public void addLicenseRequirement_SavesOrUpdatesARequirement() throws Exception {
		service.addLicenseRequirement(USER_GROUP_ID, REQUIRABLE_ID, true, false);
		verify(requirementDAO).saveOrUpdate(licenseRequirement);
	}

	@Test
	public void addLicenseRequirement_SetsNotifyOnExpiry() throws Exception {
		service.addLicenseRequirement(USER_GROUP_ID, REQUIRABLE_ID, true, false);
		verify(licenseRequirement).setNotifyOnExpiry(true);
	}

	@Test
	public void addLicenseRequirement_SetsRemoveMembershipOnExpiry() throws Exception {
		service.addLicenseRequirement(USER_GROUP_ID, REQUIRABLE_ID, true, false);
		verify(licenseRequirement).setRemoveMembershipOnExpiry(false);
	}

	@Test
	public void removeLicenseRequirement_GetsTheRequirableDAOBean() throws Exception {
		service.removeLicenseRequirement(USER_GROUP_ID, REQUIRABLE_ID);
		verify(context).getBean(LicenseRequirableDAO.class);
	}

	@Test
	public void removeLicenseRequirement_FindsTheRequirable() throws Exception {
		service.removeLicenseRequirement(USER_GROUP_ID, REQUIRABLE_ID);
		verify(requirableDAO).findBy("id", REQUIRABLE_ID);
	}

	@Test
	public void removeLicenseRequirement_FindsTheUserGroup() throws Exception {
		service.removeLicenseRequirement(USER_GROUP_ID, REQUIRABLE_ID);
		verify(userGroupDAO).findBy("id", USER_GROUP_ID);
	}

	@Test
	public void removeLicenseRequirement_GetsOrInitializesARequirementSet() throws Exception {
		service.removeLicenseRequirement(USER_GROUP_ID, REQUIRABLE_ID);
		verify(requirementSetDAO).getOrInitializeBy(
			"userGroup", userGroup,
			"company", company
		);
	}

	@Test
	public void removeLicenseRequirement_SavesOrUpdatesTheRequirementSet() {
		service.removeLicenseRequirement(USER_GROUP_ID, REQUIRABLE_ID);
		verify(requirementSetDAO).saveOrUpdate(requirementSet);
	}

	@Test
	public void removeLicenseRequirement_GetsTheRequirementDAOBean() throws Exception {
		service.removeLicenseRequirement(USER_GROUP_ID, REQUIRABLE_ID);
		verify(context, times(2)).getBean(LicenseRequirementDAO.class);
	}

	@Test
	public void removeLicenseRequirement_GetsOrInitializesARequirement() throws Exception {
		service.removeLicenseRequirement(USER_GROUP_ID, REQUIRABLE_ID);
		verify(requirementDAO).getOrInitializeBy(
			"licenseRequirable", requirable,
			"requirementSet", requirementSet
		);
	}

	@Test
	public void removeLicenseRequirement_DeletesARequirement() throws Exception {
		service.removeLicenseRequirement(USER_GROUP_ID, REQUIRABLE_ID);
		verify(requirementDAO).delete(licenseRequirement);
	}

	@Test
	public void addProfileVideoRequirement_FindsTheUserGroup() throws Exception {
		service.addProfileVideoRequirement(USER_GROUP_ID);
		verify(userGroupDAO).findBy("id", USER_GROUP_ID);
	}

	@Test
	public void addProfileVideoRequirement_GetsOrInitializesARequirementSet() throws Exception {
		service.addProfileVideoRequirement(USER_GROUP_ID);
		verify(requirementSetDAO).getOrInitializeBy(
			"userGroup", userGroup,
			"company", company
		);
	}

	@Test
	public void addProfileVideoRequirement_WithAnUnnamedRequirementSet_NamesTheRequirementSet() throws Exception {
		service.addProfileVideoRequirement(USER_GROUP_ID);
		verify(requirementSet).setName("(Group) " + USER_GROUP_NAME);
	}

	@Test
	public void addProfileVideoRequirement_WithANamedRequirementSet_DoesNotNameTheRequirementSet() throws Exception {
		when(requirementSet.getName()).thenReturn("Some Name");
		service.addProfileVideoRequirement(USER_GROUP_ID);
		verify(requirementSet, never()).setName(any(String.class));
	}

	@Test
	public void addProfileVideoRequirement_SavesOrUpdatesTheRequirementSet() {
		service.addProfileVideoRequirement(USER_GROUP_ID);
		verify(requirementSetDAO).saveOrUpdate(requirementSet);
	}

	@Test
	public void addProfileVideoRequirement_GetsTheRequirementDAOBean() throws Exception {
		service.addProfileVideoRequirement(USER_GROUP_ID);
		verify(context, times(2)).getBean(ProfileVideoRequirementDAO.class);
	}

	@Test
	public void addProfileVideoRequirement_GetsOrInitializesARequirement() throws Exception {
		service.addProfileVideoRequirement(USER_GROUP_ID);
		verify(requirementDAO).getOrInitializeBy(
			"requirementSet", requirementSet
		);
	}

	@Test
	public void addProfileVideoRequirement_SavesOrUpdatesARequirement() throws Exception {
		service.addProfileVideoRequirement(USER_GROUP_ID);
		verify(requirementDAO).saveOrUpdate(requirement);
	}

	@Test
	public void removeProfileVideoRequirement_FindsTheUserGroup() throws Exception {
		service.removeProfileVideoRequirement(USER_GROUP_ID);
		verify(userGroupDAO).findBy("id", USER_GROUP_ID);
	}

	@Test
	public void removeProfileVideoRequirement_GetsOrInitializesARequirementSet() throws Exception {
		service.removeProfileVideoRequirement(USER_GROUP_ID);
		verify(requirementSetDAO).getOrInitializeBy(
			"userGroup", userGroup,
			"company", company
		);
	}

	@Test
	public void removeProfileVideoRequirement_SavesOrUpdatesTheRequirementSet() {
		service.removeProfileVideoRequirement(USER_GROUP_ID);
		verify(requirementSetDAO).saveOrUpdate(requirementSet);
	}

	@Test
	public void removeProfileVideoRequirement_GetsTheRequirementDAOBean() throws Exception {
		service.removeProfileVideoRequirement(USER_GROUP_ID);
		verify(context, times(2)).getBean(ProfileVideoRequirementDAO.class);
	}

	@Test
	public void removeProfileVideoRequirement_GetsOrInitializesARequirement() throws Exception {
		service.removeProfileVideoRequirement(USER_GROUP_ID);
		verify(requirementDAO).getOrInitializeBy(
			"requirementSet", requirementSet
		);
	}

	@Test
	public void removeProfileVideoRequirement_DeletesARequirement() throws Exception {
		service.removeProfileVideoRequirement(USER_GROUP_ID);
		verify(requirementDAO).delete(requirement);
	}

	@Test
	public void addRatingRequirement_FindsTheUserGroup() throws Exception {
		service.addRatingRequirement(USER_GROUP_ID, RATING);
		verify(userGroupDAO).findBy("id", USER_GROUP_ID);
	}

	@Test
	public void addRatingRequirement_GetsOrInitializesARequirementSet() throws Exception {
		service.addRatingRequirement(USER_GROUP_ID, RATING);
		verify(requirementSetDAO).getOrInitializeBy(
			"userGroup", userGroup,
			"company", company
		);
	}

	@Test
	public void addRatingRequirement_WithAnUnnamedRequirementSet_NamesTheRequirementSet() throws Exception {
		service.addRatingRequirement(USER_GROUP_ID, RATING);
		verify(requirementSet).setName("(Group) " + USER_GROUP_NAME);
	}

	@Test
	public void addRatingRequirement_WithANamedRequirementSet_DoesNotNameTheRequirementSet() throws Exception {
		when(requirementSet.getName()).thenReturn("Some Name");
		service.addRatingRequirement(USER_GROUP_ID, RATING);
		verify(requirementSet, never()).setName(any(String.class));
	}

	@Test
	public void addRatingRequirement_SavesOrUpdatesTheRequirementSet() {
		service.addRatingRequirement(USER_GROUP_ID, RATING);
		verify(requirementSetDAO).saveOrUpdate(requirementSet);
	}

	@Test
	public void addRatingRequirement_GetsTheRequirementDAOBean() throws Exception {
		service.addRatingRequirement(USER_GROUP_ID, RATING);
		verify(context, times(2)).getBean(RatingRequirementDAO.class);
	}

	@Test
	public void addRatingRequirement_GetsOrInitializesARequirement() throws Exception {
		service.addRatingRequirement(USER_GROUP_ID, RATING);
		verify(requirementDAO).getOrInitializeBy(
			"value", RATING,
			"requirementSet", requirementSet
		);
	}

	@Test
	public void addRatingRequirement_SavesOrUpdatesARequirement() throws Exception {
		service.addRatingRequirement(USER_GROUP_ID, RATING);
		verify(requirementDAO).saveOrUpdate(requirement);
	}

	@Test
	public void removeRatingRequirement_FindsTheUserGroup() throws Exception {
		service.removeRatingRequirement(USER_GROUP_ID);
		verify(userGroupDAO).findBy("id", USER_GROUP_ID);
	}

	@Test
	public void removeRatingRequirement_GetsOrInitializesARequirementSet() throws Exception {
		service.removeRatingRequirement(USER_GROUP_ID);
		verify(requirementSetDAO).getOrInitializeBy(
			"userGroup", userGroup,
			"company", company
		);
	}

	@Test
	public void removeRatingRequirement_SavesOrUpdatesTheRequirementSet() {
		service.removeRatingRequirement(USER_GROUP_ID);
		verify(requirementSetDAO).saveOrUpdate(requirementSet);
	}

	@Test
	public void removeRatingRequirement_GetsTheRequirementDAOBean() throws Exception {
		service.removeRatingRequirement(USER_GROUP_ID);
		verify(context, times(2)).getBean(RatingRequirementDAO.class);
	}

	@Test
	public void removeRatingRequirement_GetsOrInitializesARequirement() throws Exception {
		service.removeRatingRequirement(USER_GROUP_ID);
		verify(requirementDAO).getOrInitializeBy(
			"requirementSet", requirementSet
		);
	}

	@Test
	public void removeRatingRequirement_DeletesARequirement() throws Exception {
		service.removeRatingRequirement(USER_GROUP_ID);
		verify(requirementDAO).delete(requirement);
	}

	@Test
	public void addResourceTypeRequirement_FindsTheUserGroup() throws Exception {
		service.addResourceTypeRequirement(USER_GROUP_ID, REQUIRABLE_ID);
		verify(userGroupDAO).findBy("id", USER_GROUP_ID);
	}

	@Test
	public void addResourceTypeRequirement_GetsOrInitializesARequirementSet() throws Exception {
		service.addResourceTypeRequirement(USER_GROUP_ID, REQUIRABLE_ID);
		verify(requirementSetDAO).getOrInitializeBy(
			"userGroup", userGroup,
			"company", company
		);
	}

	@Test
	public void addResourceTypeRequirement_WithAnUnnamedRequirementSet_NamesTheRequirementSet() throws Exception {
		service.addResourceTypeRequirement(USER_GROUP_ID, REQUIRABLE_ID);
		verify(requirementSet).setName("(Group) " + USER_GROUP_NAME);
	}

	@Test
	public void addResourceTypeRequirement_WithANamedRequirementSet_DoesNotNameTheRequirementSet() throws Exception {
		when(requirementSet.getName()).thenReturn("Some Name");
		service.addResourceTypeRequirement(USER_GROUP_ID, REQUIRABLE_ID);
		verify(requirementSet, never()).setName(any(String.class));
	}

	@Test
	public void addResourceTypeRequirement_SavesOrUpdatesTheRequirementSet() {
		service.addResourceTypeRequirement(USER_GROUP_ID, REQUIRABLE_ID);
		verify(requirementSetDAO).saveOrUpdate(requirementSet);
	}

	@Test
	public void addResourceTypeRequirement_GetsTheRequirementDAOBean() throws Exception {
		service.addResourceTypeRequirement(USER_GROUP_ID, REQUIRABLE_ID);
		verify(context, times(2)).getBean(ResourceTypeRequirementDAO.class);
	}

	@Test
	public void addResourceTypeRequirement_GetsOrInitializesARequirement() throws Exception {
		service.addResourceTypeRequirement(USER_GROUP_ID, REQUIRABLE_ID);
		verify(requirementDAO).getOrInitializeBy(
			"resourceTypeId", REQUIRABLE_ID,
			"requirementSet", requirementSet
		);
	}

	@Test
	public void addResourceTypeRequirement_SavesOrUpdatesARequirement() throws Exception {
		service.addResourceTypeRequirement(USER_GROUP_ID, REQUIRABLE_ID);
		verify(requirementDAO).saveOrUpdate(requirement);
	}

	@Test
	public void removeResourceTypeRequirement_FindsTheUserGroup() throws Exception {
		service.removeResourceTypeRequirement(USER_GROUP_ID, REQUIRABLE_ID);
		verify(userGroupDAO).findBy("id", USER_GROUP_ID);
	}

	@Test
	public void removeResourceTypeRequirement_GetsOrInitializesARequirementSet() throws Exception {
		service.removeResourceTypeRequirement(USER_GROUP_ID, REQUIRABLE_ID);
		verify(requirementSetDAO).getOrInitializeBy(
			"userGroup", userGroup,
			"company", company
		);
	}

	@Test
	public void removeResourceTypeRequirement_SavesOrUpdatesTheRequirementSet() {
		service.removeResourceTypeRequirement(USER_GROUP_ID, REQUIRABLE_ID);
		verify(requirementSetDAO).saveOrUpdate(requirementSet);
	}

	@Test
	public void removeResourceTypeRequirement_GetsTheRequirementDAOBean() throws Exception {
		service.removeResourceTypeRequirement(USER_GROUP_ID, REQUIRABLE_ID);
		verify(context, times(2)).getBean(ResourceTypeRequirementDAO.class);
	}

	@Test
	public void removeResourceTypeRequirement_GetsOrInitializesARequirement() throws Exception {
		service.removeResourceTypeRequirement(USER_GROUP_ID, REQUIRABLE_ID);
		verify(requirementDAO).getOrInitializeBy(
			"resourceTypeId", REQUIRABLE_ID,
			"requirementSet", requirementSet
		);
	}

	@Test
	public void removeResourceTypeRequirement_DeletesARequirement() throws Exception {
		service.removeResourceTypeRequirement(USER_GROUP_ID, REQUIRABLE_ID);
		verify(requirementDAO).delete(requirement);
	}

	@Test
	public void addTestRequirement_GetsTheRequirableDAOBean() throws Exception {
		service.addTestRequirement(USER_GROUP_ID, REQUIRABLE_ID);
		verify(context).getBean(TestRequirableDAO.class);
	}

	@Test
	public void addTestRequirement_FindsTheRequirable() throws Exception {
		service.addTestRequirement(USER_GROUP_ID, REQUIRABLE_ID);
		verify(requirableDAO).findBy("id", REQUIRABLE_ID);
	}

	@Test
	public void addTestRequirement_FindsTheUserGroup() throws Exception {
		service.addTestRequirement(USER_GROUP_ID, REQUIRABLE_ID);
		verify(userGroupDAO).findBy("id", USER_GROUP_ID);
	}

	@Test
	public void addTestRequirement_GetsOrInitializesARequirementSet() throws Exception {
		service.addTestRequirement(USER_GROUP_ID, REQUIRABLE_ID);
		verify(requirementSetDAO).getOrInitializeBy(
			"userGroup", userGroup,
			"company", company
		);
	}

	@Test
	public void addTestRequirement_WithAnUnnamedRequirementSet_NamesTheRequirementSet() throws Exception {
		service.addTestRequirement(USER_GROUP_ID, REQUIRABLE_ID);
		verify(requirementSet).setName("(Group) " + USER_GROUP_NAME);
	}

	@Test
	public void addTestRequirement_WithANamedRequirementSet_DoesNotNameTheRequirementSet() throws Exception {
		when(requirementSet.getName()).thenReturn("Some Name");
		service.addTestRequirement(USER_GROUP_ID, REQUIRABLE_ID);
		verify(requirementSet, never()).setName(any(String.class));
	}

	@Test
	public void addTestRequirement_SavesOrUpdatesTheRequirementSet() {
		service.addTestRequirement(USER_GROUP_ID, REQUIRABLE_ID);
		verify(requirementSetDAO).saveOrUpdate(requirementSet);
	}

	@Test
	public void addTestRequirement_GetsTheRequirementDAOBean() throws Exception {
		service.addTestRequirement(USER_GROUP_ID, REQUIRABLE_ID);
		verify(context, times(2)).getBean(TestRequirementDAO.class);
	}

	@Test
	public void addTestRequirement_GetsOrInitializesARequirement() throws Exception {
		service.addTestRequirement(USER_GROUP_ID, REQUIRABLE_ID);
		verify(requirementDAO).getOrInitializeBy(
			"testRequirable", requirable,
			"requirementSet", requirementSet
		);
	}

	@Test
	public void addTestRequirement_SavesOrUpdatesARequirement() throws Exception {
		service.addTestRequirement(USER_GROUP_ID, REQUIRABLE_ID);
		verify(requirementDAO).saveOrUpdate(requirement);
	}

	@Test
	public void removeTestRequirement_GetsTheRequirableDAOBean() throws Exception {
		service.removeTestRequirement(USER_GROUP_ID, REQUIRABLE_ID);
		verify(context).getBean(TestRequirableDAO.class);
	}

	@Test
	public void removeTestRequirement_FindsTheRequirable() throws Exception {
		service.removeTestRequirement(USER_GROUP_ID, REQUIRABLE_ID);
		verify(requirableDAO).findBy("id", REQUIRABLE_ID);
	}

	@Test
	public void removeTestRequirement_FindsTheUserGroup() throws Exception {
		service.removeTestRequirement(USER_GROUP_ID, REQUIRABLE_ID);
		verify(userGroupDAO).findBy("id", USER_GROUP_ID);
	}

	@Test
	public void removeTestRequirement_GetsOrInitializesARequirementSet() throws Exception {
		service.removeTestRequirement(USER_GROUP_ID, REQUIRABLE_ID);
		verify(requirementSetDAO).getOrInitializeBy(
			"userGroup", userGroup,
			"company", company
		);
	}

	@Test
	public void removeTestRequirement_SavesOrUpdatesTheRequirementSet() {
		service.removeTestRequirement(USER_GROUP_ID, REQUIRABLE_ID);
		verify(requirementSetDAO).saveOrUpdate(requirementSet);
	}

	@Test
	public void removeTestRequirement_GetsTheRequirementDAOBean() throws Exception {
		service.removeTestRequirement(USER_GROUP_ID, REQUIRABLE_ID);
		verify(context, times(2)).getBean(TestRequirementDAO.class);
	}

	@Test
	public void removeTestRequirement_GetsOrInitializesARequirement() throws Exception {
		service.removeTestRequirement(USER_GROUP_ID, REQUIRABLE_ID);
		verify(requirementDAO).getOrInitializeBy(
			"testRequirable", requirable,
			"requirementSet", requirementSet
		);
	}

	@Test
	public void removeTestRequirement_DeletesARequirement() throws Exception {
		service.removeTestRequirement(USER_GROUP_ID, REQUIRABLE_ID);
		verify(requirementDAO).delete(requirement);
	}

	@Test
	public void addTravelDistanceRequirement_FindsTheUserGroup() throws Exception {
		service.addTravelDistanceRequirement(USER_GROUP_ID, ADDRESS, DISTANCE);
		verify(userGroupDAO).findBy("id", USER_GROUP_ID);
	}

	@Test
	public void addTravelDistanceRequirement_GetsOrInitializesARequirementSet() throws Exception {
		service.addTravelDistanceRequirement(USER_GROUP_ID, ADDRESS, DISTANCE);
		verify(requirementSetDAO).getOrInitializeBy(
			"userGroup", userGroup,
			"company", company
		);
	}

	@Test
	public void addTravelDistanceRequirement_WithAnUnnamedRequirementSet_NamesTheRequirementSet() throws Exception {
		service.addTravelDistanceRequirement(USER_GROUP_ID, ADDRESS, DISTANCE);
		verify(requirementSet).setName("(Group) " + USER_GROUP_NAME);
	}

	@Test
	public void addTravelDistanceRequirement_WithANamedRequirementSet_DoesNotNameTheRequirementSet() throws Exception {
		when(requirementSet.getName()).thenReturn("Some Name");
		service.addTravelDistanceRequirement(USER_GROUP_ID, ADDRESS, DISTANCE);
		verify(requirementSet, never()).setName(any(String.class));
	}

	@Test
	public void addTravelDistanceRequirement_SavesOrUpdatesTheRequirementSet() {
		service.addTravelDistanceRequirement(USER_GROUP_ID, ADDRESS, DISTANCE);
		verify(requirementSetDAO).saveOrUpdate(requirementSet);
	}

	@Test
	public void addTravelDistanceRequirement_GetsTheRequirementDAOBean() throws Exception {
		service.addTravelDistanceRequirement(USER_GROUP_ID, ADDRESS, DISTANCE);
		verify(context, times(2)).getBean(TravelDistanceRequirementDAO.class);
	}

	@Test
	public void addTravelDistanceRequirement_GetsOrInitializesARequirement() throws Exception {
		service.addTravelDistanceRequirement(USER_GROUP_ID, ADDRESS, DISTANCE);
		verify(requirementDAO).getOrInitializeBy(
			"distance", DISTANCE,
			"address", ADDRESS,
			"requirementSet", requirementSet
		);
	}

	@Test
	public void addTravelDistanceRequirement_SavesOrUpdatesARequirement() throws Exception {
		service.addTravelDistanceRequirement(USER_GROUP_ID, ADDRESS, DISTANCE);
		verify(requirementDAO).saveOrUpdate(requirement);
	}

	@Test
	public void removeTravelDistanceRequirement_FindsTheUserGroup() throws Exception {
		service.removeTravelDistanceRequirement(USER_GROUP_ID, ADDRESS, DISTANCE);
		verify(userGroupDAO).findBy("id", USER_GROUP_ID);
	}

	@Test
	public void removeTravelDistanceRequirement_GetsOrInitializesARequirementSet() throws Exception {
		service.removeTravelDistanceRequirement(USER_GROUP_ID, ADDRESS, DISTANCE);
		verify(requirementSetDAO).getOrInitializeBy(
			"userGroup", userGroup,
			"company", company
		);
	}

	@Test
	public void removeTravelDistanceRequirement_SavesOrUpdatesTheRequirementSet() {
		service.removeTravelDistanceRequirement(USER_GROUP_ID, ADDRESS, DISTANCE);
		verify(requirementSetDAO).saveOrUpdate(requirementSet);
	}

	@Test
	public void removeTravelDistanceRequirement_GetsTheRequirementDAOBean() throws Exception {
		service.removeTravelDistanceRequirement(USER_GROUP_ID, ADDRESS, DISTANCE);
		verify(context, times(2)).getBean(TravelDistanceRequirementDAO.class);
	}

	@Test
	public void removeTravelDistanceRequirement_GetsOrInitializesARequirement() throws Exception {
		service.removeTravelDistanceRequirement(USER_GROUP_ID, ADDRESS, DISTANCE);
		verify(requirementDAO).getOrInitializeBy(
			"distance", DISTANCE,
			"address", ADDRESS,
			"requirementSet", requirementSet
		);
	}

	@Test
	public void removeTravelDistanceRequirement_DeletesARequirement() throws Exception {
		service.removeTravelDistanceRequirement(USER_GROUP_ID, ADDRESS, DISTANCE);
		verify(requirementDAO).delete(requirement);
	}

	@Test
	public void findOrCreateRequirementSetByUserGroupId_FindsTheUserGroup() throws Exception {
		service.findOrCreateRequirementSetByUserGroupId(USER_GROUP_ID);
		verify(userGroupDAO).findBy("id", USER_GROUP_ID);
	}

	@Test
	public void findOrCreateRequirementSetByUserGroupId_GetsOrInitializesARequirementSet() throws Exception {
		service.findOrCreateRequirementSetByUserGroupId(USER_GROUP_ID);
		verify(requirementSetDAO).getOrInitializeBy(
			"userGroup", userGroup,
			"company", company
		);
	}

	@Test
	public void findOrCreateRequirementSetByUserGroupId_WhenUnnamed_SetsTheNameFromTheGroup() throws Exception {
		service.findOrCreateRequirementSetByUserGroupId(USER_GROUP_ID);
		verify(requirementSet).setName("(Group) " + USER_GROUP_NAME);
	}

	@Test
	public void findOrCreateRequirementSetByUserGroupId_WhenNamed_IgnoresTheGroupName() throws Exception {
		when(requirementSet.getName()).thenReturn("Some Name");
		service.findOrCreateRequirementSetByUserGroupId(USER_GROUP_ID);
		verify(requirementSet, never()).setName(any(String.class));
	}

	@Test
	public void findOrCreateRequirementSetByUserGroupId_SavesOrUpdatesTheRequirementSet() {
		service.findOrCreateRequirementSetByUserGroupId(USER_GROUP_ID);
		verify(requirementSetDAO).saveOrUpdate(requirementSet);
	}

	@Test
	public void findOrCreateRequirementSetByUserGroupId_ReturnsARequirementSet() throws Exception {
		RequirementSet actualRequirementSet = service.findOrCreateRequirementSetByUserGroupId(USER_GROUP_ID);
		assertThat(actualRequirementSet, is(requirementSet));
	}

	private class ClassOrSubclassMatcher<T> extends BaseMatcher<Class<T>> {
		private final Class<T> targetClass;

		public ClassOrSubclassMatcher(Class<T> targetClass) {
			this.targetClass = targetClass;
		}

		@SuppressWarnings("unchecked")
		public boolean matches(Object obj) {
			if (obj != null) {
				if (obj instanceof Class) {
					return targetClass.isAssignableFrom((Class<T>) obj);
				}
			}
			return false;
		}

		public void describeTo(Description desc) {
			desc.appendText("Matches a class or subclass");
		}
	}
}
