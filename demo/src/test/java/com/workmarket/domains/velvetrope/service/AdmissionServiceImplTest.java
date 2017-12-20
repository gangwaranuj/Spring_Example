package com.workmarket.domains.velvetrope.service;

import com.google.common.collect.Sets;
import com.workmarket.dao.changelog.company.CompanyChangeLogDAO;
import com.workmarket.domains.model.Plan;
import com.workmarket.domains.velvetrope.dao.AdmissionDAO;
import com.workmarket.domains.velvetrope.model.Admission;
import com.workmarket.service.business.CompanyService;
import com.workmarket.service.business.PlanService;
import com.workmarket.service.infra.business.AuthenticationService;
import com.workmarket.velvetrope.TokenService;
import com.workmarket.velvetrope.Venue;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Arrays;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class AdmissionServiceImplTest {
	private static final Long SOME_COMPANY_ID = 99999L;
	private static final long SOME_PLAN_ID = 88888L;
	private static final String SOME_PLAN_CODE = "SOMEPLANCODE";
	private static final String SOME_BOGUS_PLAN_CODE = "BOGUS";
	public static final String BLANK = " ";
	public static final String EMPTY = "";

	@Mock AdmissionDAO dao;
	@Mock TokenService tokenService;
	@Mock PlanService planService;
	@Mock CompanyService companyService;
	@Mock AuthenticationService authenticationService;
	@Mock CompanyChangeLogDAO companyChangeLogDAO;
	@InjectMocks AdmissionService service = new AdmissionServiceImpl();

	List<Admission> admissions;
	Admission admission;
	Plan plan;

	@Before
	public void setUp() throws Exception {
		admission = mock(Admission.class);
		when(admission.getVenue()).thenReturn(Venue.TRANSACTIONAL);
		when(admission.getProvidedVenues()).thenReturn(Sets.newHashSet(Venue.TRANSACTIONAL));
		when(
			dao.getOrInitializeBy(
				"value", String.valueOf(SOME_COMPANY_ID),
				"keyName", "companyId",
				"venue", Venue.TRANSACTIONAL
			)
		).thenReturn(admission);
		when(
			dao.getOrInitializeBy(
				"value", String.valueOf(SOME_PLAN_ID),
				"keyName", "planId",
				"venue", Venue.TRANSACTIONAL
			)
		).thenReturn(admission);

		admissions = Arrays.asList(admission);
		when(
			dao.findAllBy(
				"value", String.valueOf(SOME_COMPANY_ID),
				"keyName", "companyId",
				"deleted", false
			)
		).thenReturn(admissions);
		when(
			dao.findAllBy(
				"value", String.valueOf(SOME_PLAN_ID),
				"keyName", "planId",
				"deleted", false
			)
		).thenReturn(admissions);
		when(dao.findAllAdmissionsByKeyNameForVenue("companyId", Venue.COMPANY)).thenReturn(admissions);

		plan = mock(Plan.class);
		when(plan.getId()).thenReturn(SOME_PLAN_ID);
		when(planService.find(SOME_PLAN_CODE)).thenReturn(plan);
		when(planService.find(SOME_BOGUS_PLAN_CODE)).thenReturn(null);
	}

	@Test
	public void findAllAdmissionsForCompanyId_ReturnsAListOfAdmissions() throws Exception {
		List<Admission> actual = service.findAllAdmissionsForCompanyId(SOME_COMPANY_ID);

		assertThat(actual, is(admissions));
	}

	@Test
	public void findAllAdmissionsForCompanyId_FindsAllByValueAndKeyName() throws Exception {
		service.findAllAdmissionsForCompanyId(SOME_COMPANY_ID);

		verify(dao).findAllBy(
			"value", String.valueOf(SOME_COMPANY_ID),
			"keyName", "companyId",
			"deleted", false
		);
	}

	@Test
	public void findAllAdmissionsForPlanId_ReturnsAListOfAdmissions() throws Exception {
		List<Admission> actual = service.findAllAdmissionsForPlanId(SOME_PLAN_ID);

		assertThat(actual, is(admissions));
	}

	@Test
	public void findAllAdmissionsForPlanId_FindsAllByValueAndKeyName() throws Exception {
		service.findAllAdmissionsForPlanId(SOME_PLAN_ID);

		verify(dao).findAllBy(
			"value", String.valueOf(SOME_PLAN_ID),
			"keyName", "planId",
			"deleted", false
		);
	}

	@Test
	public void findAllCompanyAdmissionsForVenues_findsAllCompanyAdmissionsForVenuesWithOneVenue() throws Exception {
		service.findAllAdmissionsByKeyNameForVenue("companyId", Venue.COMPANY);

		verify(dao).findAllAdmissionsByKeyNameForVenue("companyId", Venue.COMPANY);
	}

	@Test
	public void findAllCompanyAdmissionsForVenues_findsAllCompanyAdmissionsForVenuesWithMultipleVenues() throws Exception {
		service.findAllAdmissionsByKeyNameForVenue("companyId", Venue.COMPANY, Venue.TRANSACTIONAL, Venue.PROFESSIONAL);

		verify(dao).findAllAdmissionsByKeyNameForVenue("companyId", Venue.COMPANY, Venue.TRANSACTIONAL, Venue.PROFESSIONAL);
	}

	@Test
	public void findAllCompanyAdmissionsForVenues_ReturnsAListOfAdmissions() throws Exception {
		List<Admission> actual = service.findAllAdmissionsByKeyNameForVenue("companyId", Venue.COMPANY);

		assertThat(actual, is(admissions));
	}

	@Test
	public void saveAdmissionForCompanyIdAndVenue_GetsOrInitializesAnAdmission() throws Exception {
		service.saveAdmissionForCompanyIdAndVenue(SOME_COMPANY_ID, Venue.TRANSACTIONAL);

		verify(dao).getOrInitializeBy(
			"value", String.valueOf(SOME_COMPANY_ID),
			"keyName", "companyId",
			"venue", Venue.TRANSACTIONAL
		);
	}

	@Test
	public void saveAdmissionForCompanyIdAndVenue_WithSystemVenue_DoesNotGetOrInitializeAnAdmission() throws Exception {
		service.saveAdmissionForCompanyIdAndVenue(SOME_COMPANY_ID, Venue.LOBBY);

		verify(dao, never()).getOrInitializeBy(
			"value", String.valueOf(SOME_COMPANY_ID),
			"keyName", "companyId",
			"venue", Venue.LOBBY
		);
	}

	@Test
	public void saveAdmissionForCompanyIdAndVenue_SavesOrUpdatesAnAdmission() throws Exception {
		service.saveAdmissionForCompanyIdAndVenue(SOME_COMPANY_ID, Venue.TRANSACTIONAL);

		verify(dao).saveOrUpdate(admission);
	}

	@Test
	public void saveAdmissionForCompanyIdAndVenue_WithASystemVenue_DoesNotSaveOrUpdateAnAdmission() throws Exception {
		service.saveAdmissionForCompanyIdAndVenue(SOME_COMPANY_ID, Venue.LOBBY);

		verify(dao, never()).saveOrUpdate(admission);
	}

	@Test
	public void saveAdmissionForCompanyIdAndVenue_DeletesToken() throws Exception {
		service.saveAdmissionForCompanyIdAndVenue(SOME_COMPANY_ID, Venue.TRANSACTIONAL);

		verify(tokenService).deleteTokenFor(SOME_COMPANY_ID);
	}

	@Test
	public void saveAdmissionForCompanyIdAndVenue_WithASystemVenue_DoesNotDeleteToken() throws Exception {
		service.saveAdmissionForCompanyIdAndVenue(SOME_COMPANY_ID, Venue.LOBBY);

		verify(tokenService, never()).deleteTokenFor(SOME_COMPANY_ID);
	}

	@Test
	public void saveAdmissionsForPlanId_GetsOrInitializesAdmissions() throws Exception {
		service.saveAdmissionsForPlanId(SOME_PLAN_ID, admissions);

		verify(dao).getOrInitializeBy(
			"value", String.valueOf(SOME_PLAN_ID),
			"keyName", "planId",
			"venue", admission.getVenue()
		);
	}

	@Test
	public void saveAdmissionsForPlanId_SavesOrUpdatesAnAdmission() throws Exception {
		service.saveAdmissionsForPlanId(SOME_PLAN_ID, admissions);

		verify(dao).saveOrUpdate(admission);
	}

	@Test
	public void destroyAdmissionForCompanyIdAndVenue_DeletesAnAdmission() throws Exception {
		service.destroyAdmissionForCompanyIdAndVenue(SOME_COMPANY_ID, Venue.TRANSACTIONAL);

		verify(admission).setDeleted(true);
	}

	@Test
	public void destroyAdmissionForCompanyIdAndVenue_DeletesToken() throws Exception {
		service.destroyAdmissionForCompanyIdAndVenue(SOME_COMPANY_ID, Venue.TRANSACTIONAL);

		verify(tokenService).deleteTokenFor(SOME_COMPANY_ID);
	}

	@Test
	public void destroyAdmissionsForCompanyId_FindsAllAdmissionsForTheCompany() throws Exception {
		service.destroyAdmissionsForCompanyId(SOME_COMPANY_ID);

		verify(dao).findAllBy(
			"value", String.valueOf(SOME_COMPANY_ID),
			"keyName", "companyId",
			"deleted", false
		);
	}

	@Test
	public void destroyAdmissionsForCompanyId_DeletesTheAdmissionsForTheCompany() throws Exception {
		ArgumentCaptor<Admission> captor = ArgumentCaptor.forClass(Admission.class);

		service.destroyAdmissionsForCompanyId(SOME_COMPANY_ID);

		List<Admission> capturedAdmissions = captor.getAllValues();
		for (Admission admission : capturedAdmissions) {
			assertThat(admission.getDeleted(), is(true));
		}
	}

	@Test
	public void destroyAdmissionsForCompanyId_DeletesToken() throws Exception {
		service.destroyAdmissionsForCompanyId(SOME_COMPANY_ID);

		verify(tokenService).deleteTokenFor(SOME_COMPANY_ID);
	}

	@Test
	public void grantAdmissionsForCompanyIdByPlanCode_WithNullPlanCode_DoesNotFindAPlan() throws Exception {
		service.grantAdmissionsForCompanyIdByPlanCode(SOME_COMPANY_ID, null);

		verify(planService, never()).find(any(String.class));
	}

	@Test
	public void grantAdmissionsForCompanyIdByPlanCode_WithAnEmptyPlanCode_DoesNotFindAPlan() throws Exception {
		service.grantAdmissionsForCompanyIdByPlanCode(SOME_COMPANY_ID, EMPTY);

		verify(planService, never()).find(any(String.class));
	}

	@Test
	public void grantAdmissionsForCompanyIdByPlanCode_WithABlankPlanCode_DoesNotFindAPlan() throws Exception {
		service.grantAdmissionsForCompanyIdByPlanCode(SOME_COMPANY_ID, BLANK);

		verify(planService, never()).find(any(String.class));
	}

	@Test
	public void grantAdmissionsForCompanyIdByPlanCode_WithAPlanCode_FindsAPlan() throws Exception {
		service.grantAdmissionsForCompanyIdByPlanCode(SOME_COMPANY_ID, SOME_PLAN_CODE);

		verify(planService).find(SOME_PLAN_CODE);
	}

	@Test
	public void grantAdmissionsForCompanyIdByPlanCode_WithNullPlanCode_DoesNotFindAdmissionsForAPlan() throws Exception {
		service.grantAdmissionsForCompanyIdByPlanCode(SOME_COMPANY_ID, null);

		verify(dao, never()).findAllBy(
			eq("value"), any(Object.class),
			eq("keyName"), eq("planId"));
	}

	@Test
	public void grantAdmissionsForCompanyIdByPlanCode_WithAnEmptyPlanCode_DoesNotFindAdmissionsForAPlan() throws Exception {
		service.grantAdmissionsForCompanyIdByPlanCode(SOME_COMPANY_ID, EMPTY);

		verify(dao, never()).findAllBy(
			eq("value"), any(Object.class),
			eq("keyName"), eq("planId"));
	}

	@Test
	public void grantAdmissionsForCompanyIdByPlanCode_WithABlankPlanCode_DoesNotFindAdmissionsForAPlan() throws Exception {
		service.grantAdmissionsForCompanyIdByPlanCode(SOME_COMPANY_ID, BLANK);

		verify(dao, never()).findAllBy(
			eq("value"), any(Object.class),
			eq("keyName"), eq("planId"));
	}

	@Test
	public void grantAdmissionsForCompanyIdByPlanCode_WithAPlanCode_FindsAdmissionsForAPlan() throws Exception {
		service.grantAdmissionsForCompanyIdByPlanCode(SOME_COMPANY_ID, SOME_PLAN_CODE);

		verify(dao).findAllBy("value", String.valueOf(SOME_PLAN_ID), "keyName", "planId", "deleted", false);
	}

	@Test
	public void grantAdmissionsForCompanyIdByPlanCode_WithABogusPlanCode_NeverFindsAdmissionsForAPlan() throws Exception {
		service.grantAdmissionsForCompanyIdByPlanCode(SOME_COMPANY_ID, SOME_BOGUS_PLAN_CODE);

		verify(dao, never()).findAllBy("value", String.valueOf(SOME_PLAN_ID), "keyName", "planId");
	}

	@Test
	public void grantAdmissionsForCompanyIdByPlanCode_WithNullPlanCode_DoesNotGetProvidedVenuesForAnAdmission() throws Exception {
		service.grantAdmissionsForCompanyIdByPlanCode(SOME_COMPANY_ID, null);

		verify(admission, never()).getProvidedVenues();
	}

	@Test
	public void grantAdmissionsForCompanyIdByPlanCode_WithAnEmptyPlanCode_DoesNotGetProvidedVenuesForAnAdmission() throws Exception {
		service.grantAdmissionsForCompanyIdByPlanCode(SOME_COMPANY_ID, EMPTY);

		verify(admission, never()).getProvidedVenues();
	}

	@Test
	public void grantAdmissionsForCompanyIdByPlanCode_WithABlankPlanCode_DoesNotGetProvidedVenuesForAnAdmission() throws Exception {
		service.grantAdmissionsForCompanyIdByPlanCode(SOME_COMPANY_ID, BLANK);

		verify(admission, never()).getProvidedVenues();
	}

	@Test
	public void grantAdmissionsForCompanyIdByPlanCode_WithABogusPlanCode_NeverGetsProvidedVenuesForAnAdmission() throws Exception {
		service.grantAdmissionsForCompanyIdByPlanCode(SOME_COMPANY_ID, SOME_BOGUS_PLAN_CODE);

		verify(admission, never()).getProvidedVenues();
	}

	@Test
	public void grantAdmissionsForCompanyIdByPlanCode_WithNullPlanCode_DoesNotGetOrInitializeAnAdmission() throws Exception {
		service.grantAdmissionsForCompanyIdByPlanCode(SOME_COMPANY_ID, null);

		verify(dao, never()).getOrInitializeBy(
			eq("value"), any(String.class),
			eq("keyName"), eq("planId"),
			eq("venue"), any(Venue.class));
	}

	@Test
	public void grantAdmissionsForCompanyIdByPlanCode_WithAnEmptyPlanCode_DoesNotGetOrInitializeAnAdmission() throws Exception {
		service.grantAdmissionsForCompanyIdByPlanCode(SOME_COMPANY_ID, EMPTY);

		verify(dao, never()).getOrInitializeBy(
			eq("value"), any(String.class),
			eq("keyName"), eq("planId"),
			eq("venue"), any(Venue.class));
	}

	@Test
	public void grantAdmissionsForCompanyIdByPlanCode_WithABlankPlanCode_DoesNotGetOrInitializeAnAdmission() throws Exception {
		service.grantAdmissionsForCompanyIdByPlanCode(SOME_COMPANY_ID, BLANK);

		verify(dao, never()).getOrInitializeBy(
			eq("value"), any(String.class),
			eq("keyName"), eq("planId"),
			eq("venue"), any(Venue.class));
	}

	@Test
	public void grantAdmissionsForCompanyIdByPlanCode_WithAPlanCode_GetsOrInitializesAnAdmission() throws Exception {
		service.grantAdmissionsForCompanyIdByPlanCode(SOME_COMPANY_ID, SOME_PLAN_CODE);

		verify(dao).getOrInitializeBy(
			"value", String.valueOf(SOME_COMPANY_ID),
			"keyName", "companyId",
			"venue", admission.getVenue());
	}

	@Test
	public void grantAdmissionsForCompanyIdByPlanCode_WithABogusPlanCode_NeverGetsOrInitializesAnAdmission() throws Exception {
		service.grantAdmissionsForCompanyIdByPlanCode(SOME_COMPANY_ID, SOME_BOGUS_PLAN_CODE);

		verify(dao, never()).getOrInitializeBy(
			"value", String.valueOf(SOME_COMPANY_ID),
			"keyName", "companyId",
			"venue", admission.getVenue());
	}

	@Test
	public void grantAdmissionsForCompanyIdByPlanCode_WithNullPlanCode_DoesNotSaveAnAdmission() throws Exception {
		service.grantAdmissionsForCompanyIdByPlanCode(SOME_COMPANY_ID, null);

		verify(dao, never()).saveOrUpdate(admission);
	}

	@Test
	public void grantAdmissionsForCompanyIdByPlanCode_WithAnEmptyPlanCode_DoesNotSaveAnAdmission() throws Exception {
		service.grantAdmissionsForCompanyIdByPlanCode(SOME_COMPANY_ID, EMPTY);

		verify(dao, never()).saveOrUpdate(admission);
	}

	@Test
	public void grantAdmissionsForCompanyIdByPlanCode_WithABlankPlanCode_DoesNotSaveAnAdmission() throws Exception {
		service.grantAdmissionsForCompanyIdByPlanCode(SOME_COMPANY_ID, BLANK);

		verify(dao, never()).saveOrUpdate(admission);
	}

	@Test
	public void grantAdmissionsForCompanyIdByPlanCode_WithAPlanCode_SavesAnAdmission() throws Exception {
		service.grantAdmissionsForCompanyIdByPlanCode(SOME_COMPANY_ID, SOME_PLAN_CODE);

		verify(dao).saveOrUpdate(admission);
	}

	@Test
	public void grantAdmissionsForCompanyIdByPlanCode_WithABogusPlanCode_NeverSavesAnAdmission() throws Exception {
		service.grantAdmissionsForCompanyIdByPlanCode(SOME_COMPANY_ID, SOME_BOGUS_PLAN_CODE);

		verify(dao, never()).saveOrUpdate(admission);
	}

	@Test
	public void grantAdmissionsForCompanyIdByPlanCode_WithNullPlanCode_DoesNotDeleteTheToken() throws Exception {
		service.grantAdmissionsForCompanyIdByPlanCode(SOME_COMPANY_ID, null);

		verify(tokenService, never()).deleteTokenFor(SOME_COMPANY_ID);
	}

	@Test
	public void grantAdmissionsForCompanyIdByPlanCode_WithAnEmptyPlanCode_DoesNotDeleteTheToken() throws Exception {
		service.grantAdmissionsForCompanyIdByPlanCode(SOME_COMPANY_ID, EMPTY);

		verify(tokenService, never()).deleteTokenFor(SOME_COMPANY_ID);
	}

	@Test
	public void grantAdmissionsForCompanyIdByPlanCode_WithABlankPlanCode_DoesNotDeleteTheToken() throws Exception {
		service.grantAdmissionsForCompanyIdByPlanCode(SOME_COMPANY_ID, BLANK);

		verify(tokenService, never()).deleteTokenFor(SOME_COMPANY_ID);
	}

	@Test
	public void grantAdmissionsForCompanyIdByPlanCode_WithAPlanCode_DeletesTheToken() throws Exception {
		service.grantAdmissionsForCompanyIdByPlanCode(SOME_COMPANY_ID, SOME_PLAN_CODE);

		verify(tokenService).deleteTokenFor(SOME_COMPANY_ID);
	}

	@Test
	public void grantAdmissionsForCompanyIdByPlanCode_WithABogusPlanCode_NeverDeletesTheToken() throws Exception {
		service.grantAdmissionsForCompanyIdByPlanCode(SOME_COMPANY_ID, SOME_BOGUS_PLAN_CODE);

		verify(tokenService, never()).deleteTokenFor(SOME_COMPANY_ID);
	}
}
