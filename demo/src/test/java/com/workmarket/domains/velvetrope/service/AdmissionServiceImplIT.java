package com.workmarket.domains.velvetrope.service;

import com.google.common.collect.Lists;
import com.workmarket.domains.model.Plan;
import com.workmarket.domains.velvetrope.model.Admission;
import com.workmarket.service.business.BaseServiceIT;
import com.workmarket.service.business.PlanService;
import com.workmarket.test.IntegrationTest;
import com.workmarket.velvetrope.TokenService;
import com.workmarket.velvetrope.Venue;
import org.hamcrest.Matchers;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.collection.IsEmptyCollection.empty;
import static org.hamcrest.core.Is.is;

@RunWith(SpringJUnit4ClassRunner.class)
@Category(IntegrationTest.class)
public class AdmissionServiceImplIT extends BaseServiceIT {
	@Autowired AdmissionService service;
	@Autowired PlanService planService;
	@Autowired TokenService tokenService;

	Plan plan;

	@Before
	public void setup() {
		service.destroyAdmissionsForCompanyId(COMPANY_ID);

		plan = planService.find("SUPERDUPER");
		if (plan != null) { planService.destroy(plan.getId()); }

		plan = planService.save(makePlan("SUPERDUPER", "Super Duper"));
		service.destroyAdmissionsForPlanId(plan.getId());
	}

	@After
	public void teardown() {
		setup();
	}

	@Test
	public void findAllAdmissionsForCompanyId_WhenAnAdmissionExists_HasItem() throws Exception {
		service.saveAdmissionForCompanyIdAndVenue(COMPANY_ID, Venue.SHARED_GROUPS);
		List<Admission> admissions = service.findAllAdmissionsForCompanyId(COMPANY_ID);

		assertThat(admissions, hasItem(Matchers.<Admission>hasProperty("venue", equalTo(Venue.SHARED_GROUPS))));
	}

	@Test
	public void findAllAdmissionsForCompanyId_WhenNoAdmissionExists_IsEmpty() throws Exception {
		List<Admission> admissions = service.findAllAdmissionsForCompanyId(COMPANY_ID);

		for (Admission admission : admissions) {
			assertThat(admission.getDeleted(), is(true));
		}
	}

	@Test
	public void findAllAdmissionsForCompanyId_WhenCalledWithNull_IsEmpty() throws Exception {
		List<Admission> admissions = service.findAllAdmissionsForCompanyId(null);

		assertThat(admissions, is(empty()));
	}

	@Test
	public void findAllAdmissionsForPlanId_WhenAnAdmissionExists_HasItem() throws Exception {
		service.saveAdmissionsForPlanId(plan.getId(), Lists.newArrayList(makeAdmission(Venue.SHARED_GROUPS)));
		List<Admission> admissions = service.findAllAdmissionsForPlanId(plan.getId());

		assertThat(admissions, hasItem(Matchers.<Admission>hasProperty("venue", equalTo(Venue.SHARED_GROUPS))));
	}

	@Test
	public void findAllAdmissionsForPlanId_WhenNoAdmissionExists_IsEmpty() throws Exception {
		List<Admission> admissions = service.findAllAdmissionsForPlanId(plan.getId());

		assertThat(admissions, is(empty()));
	}

	@Test
	public void findAllAdmissionsForPlanId_WhenCalledWithNull_IsEmpty() throws Exception {
		List<Admission> admissions = service.findAllAdmissionsForPlanId(null);

		assertThat(admissions, is(empty()));
	}


	@Test
	public void findAllCompanyAdmissionsForVenues_nullVenue_emptyList() throws Exception {
		service.saveAdmissionForCompanyIdAndVenue(COMPANY_ID, Venue.COMPANY);
		List<Admission> admissions = service.findAllAdmissionsByKeyNameForVenue("companyId", null);

		assertThat(admissions, is(empty()));
	}

	@Test
	public void findAllCompanyAdmissionsForVenues_DoesNotContainPlanIds() throws Exception {
		service.saveAdmissionForCompanyIdAndVenue(COMPANY_ID, Venue.COMPANY);
		List<Admission> admissions = service.findAllAdmissionsByKeyNameForVenue("companyId", Venue.COMPANY);

		assertThat(admissions, not(hasItem(Matchers.<Admission>hasProperty("keyName", equalTo("planId")))));
	}

	@Test
	public void findAllCompanyAdmissionsForMultipleVenues_WhenAnAdmissionExists_noItems() throws Exception {

		List<Admission> admissions = service.findAllAdmissionsByKeyNameForVenue("companyId", Venue.COMPANY, Venue.TRANSACTIONAL, Venue.PROFESSIONAL, Venue.ENTERPRISE);

		assertThat(admissions,
			not(hasItem(allOf(
				Matchers.<Admission>hasProperty("venue", equalTo(Venue.COMPANY)),
				Matchers.<Admission>hasProperty("keyName", equalTo("companyId"))))));
	}

	@Test
	public void findAllCompanyAdmissionsForMultipleVenues_WhenAnAdmissionExists_HasSomeItems() throws Exception {
		service.saveAdmissionForCompanyIdAndVenue(COMPANY_ID, Venue.TRANSACTIONAL);

		List<Admission> admissions = service.findAllAdmissionsByKeyNameForVenue("companyId", Venue.COMPANY, Venue.TRANSACTIONAL);

		assertThat(admissions,
			allOf(
				hasItem(allOf(
					Matchers.<Admission>hasProperty("venue", equalTo(Venue.TRANSACTIONAL)),
					Matchers.<Admission>hasProperty("keyName", equalTo("companyId")),
					Matchers.<Admission>hasProperty("value", equalTo(String.valueOf(COMPANY_ID))))),
				not(hasItem(allOf(
					Matchers.<Admission>hasProperty("venue", equalTo(Venue.COMPANY)),
					Matchers.<Admission>hasProperty("keyName", equalTo("companyId")),
					Matchers.<Admission>hasProperty("value", equalTo(String.valueOf(COMPANY_ID))))))));
	}

	@Test
	public void findAllCompanyAdmissionsForMultipleVenues_WhenAnAdmissionExists_HasItem() throws Exception {
		service.saveAdmissionForCompanyIdAndVenue(COMPANY_ID, Venue.SHARED_GROUPS);
		service.saveAdmissionForCompanyIdAndVenue(COMPANY_ID, Venue.TRANSACTIONAL);

		List<Admission> admissions = service.findAllAdmissionsByKeyNameForVenue("companyId", Venue.SHARED_GROUPS, Venue.TRANSACTIONAL);

		assertThat(admissions,
			allOf(
				hasItem(allOf(
					Matchers.<Admission>hasProperty("venue", equalTo(Venue.SHARED_GROUPS)),
					Matchers.<Admission>hasProperty("keyName", equalTo("companyId")),
					Matchers.<Admission>hasProperty("value", equalTo(String.valueOf(COMPANY_ID))))),
				hasItem(allOf(
					Matchers.<Admission>hasProperty("venue", equalTo(Venue.TRANSACTIONAL)),
					Matchers.<Admission>hasProperty("keyName", equalTo("companyId")),
					Matchers.<Admission>hasProperty("value", equalTo(String.valueOf(COMPANY_ID)))))));
	}

	@Test
	public void saveAdmissionForCompanyIdAndVenue_DeletesToken() {
		tokenService.cacheToken(COMPANY_ID, 12345);

		service.saveAdmissionForCompanyIdAndVenue(COMPANY_ID, Venue.SHARED_GROUPS);

		int token = tokenService.tokenFor(COMPANY_ID);
		assertThat(token, is(Venue.SHARED_GROUPS.mask()));
	}

	@Test
	public void saveAdmissionForCompanyIdAndVenue_WithSystemVenue_SavesNothing() {
		service.saveAdmissionForCompanyIdAndVenue(COMPANY_ID, Venue.LOBBY);
		List<Admission> admissions = service.findAllAdmissionsForCompanyId(COMPANY_ID);

		for (Admission admission : admissions) {
			assertThat(admission.getDeleted(), is(true));
		}
	}

	@Test
	public void destroyAdmissionForCompanyIdAndVenue_DeletesToken() {
		tokenService.cacheToken(COMPANY_ID, 12345);

		service.destroyAdmissionForCompanyIdAndVenue(COMPANY_ID, Venue.SHARED_GROUPS);

		// VelvetRopeTokenService returns a default token (LOBBY) value when none is cached
		int token = tokenService.tokenFor(COMPANY_ID);
		assertThat(token, is(Venue.LOBBY.mask()));
	}

	@Test
	public void grantAdmissionsForCompanyIdByPlanCode_WithAPlanCode_CreatesAdmissionsForTheCompanyId() {
		// Create a plan with a single venue
		service.saveAdmissionsForPlanId(plan.getId(), Lists.newArrayList(makeAdmission(Venue.SHARED_GROUPS)));

		// Verify the company has no admissions
		List<Admission> admissions = service.findAllAdmissionsForCompanyId(COMPANY_ID);
		for (Admission admission : admissions) {
			assertThat(admission.getDeleted(), is(true));
		}

		// grant the admissions in the plan to the company
		service.grantAdmissionsForCompanyIdByPlanCode(COMPANY_ID, plan.getCode());

		// Verify the company has the admissions
		admissions = service.findAllAdmissionsForCompanyId(COMPANY_ID);
		assertThat(admissions, hasItem(Matchers.<Admission>hasProperty("venue", equalTo(Venue.SHARED_GROUPS))));
	}

	@Test
	public void grantAdmissionsForCompanyIdByPlanCode_WithAPlanCode_CreatesNonSystemAdmissionsForTheCompanyId() {
		// Create a plan with a single venue
		service.saveAdmissionsForPlanId(plan.getId(), Lists.newArrayList(makeAdmission(Venue.ENTERPRISE)));

		// Verify the company has no admissions
		List<Admission> admissions = service.findAllAdmissionsForCompanyId(COMPANY_ID);
		for (Admission admission : admissions) {
			assertThat(admission.getDeleted(), is(true));
		}

		// grant the admissions in the plan to the company
		service.grantAdmissionsForCompanyIdByPlanCode(COMPANY_ID, plan.getCode());

		// Verify the company has all the admissions provided by the "parent" venue
		admissions = service.findAllAdmissionsForCompanyId(COMPANY_ID);
		assertThat(admissions, hasItem(Matchers.<Admission>hasProperty("venue", equalTo(Venue.ENTERPRISE))));
		assertThat(admissions, hasItem(Matchers.<Admission>hasProperty("venue", equalTo(Venue.PROFESSIONAL))));
		assertThat(admissions, hasItem(Matchers.<Admission>hasProperty("venue", equalTo(Venue.TRANSACTIONAL))));

		// Verify that the company did not receive the system venue admission
		assertThat(admissions, not(hasItem(Matchers.<Admission>hasProperty("venue", equalTo(Venue.COMPANY)))));
	}

	private Admission makeAdmission(Venue venue) {
		Admission admission = new Admission();
		admission.setVenue(venue);
		return admission;
	}

	private Plan makePlan(String code, String description) {
		Plan plan = new Plan();
		plan.setCode(code);
		plan.setDescription(description);

		return plan;
	}
}
