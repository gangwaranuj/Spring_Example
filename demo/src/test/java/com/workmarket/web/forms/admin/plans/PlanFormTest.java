package com.workmarket.web.forms.admin.plans;

import com.google.common.collect.Lists;
import com.workmarket.domains.velvetrope.model.Admission;
import com.workmarket.domains.model.Plan;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.*;

public class PlanFormTest {

	private PlanForm planForm;
	private List<Admission> admissions;
	private Plan plan;

	@Before
	public void setUp() throws Exception {
		plan = new Plan();
		plan.setId(999999L);
		plan.setCode("SOME_CODE");
		plan.setDescription("Some description");
		admissions = Lists.newArrayList();
		planForm = new PlanForm(plan, admissions);
	}

	@Test
	public void getPlan_GetsAPlanWithTheSameID() throws Exception {
		Plan newPlan = planForm.getPlan();

		assertThat(newPlan.getId(), is(plan.getId()));
	}

	@Test
	public void getPlan_GetsAPlanWithTheSameCode() throws Exception {
		Plan newPlan = planForm.getPlan();
		assertThat(newPlan.getCode(), is(plan.getCode()));
	}

	@Test
	public void getPlan_GetsAPlanWithTheSameDescription() throws Exception {
		Plan newPlan = planForm.getPlan();
		assertThat(newPlan.getDescription(), is(plan.getDescription()));
	}

	@Test
	public void getId_ReturnsThePlansID() throws Exception {
		assertThat(planForm.getId(), is(plan.getId()));
	}

	@Test
	public void getCode_ReturnsThePlansCode() throws Exception {
		assertThat(planForm.getCode(), is(plan.getCode()));
	}

	@Test
	public void getDescription_ReturnsThePlansDescription() throws Exception {
		assertThat(planForm.getDescription(), is(plan.getDescription()));
	}

	@Test
	public void getAdmissions_ReturnsThePlansAdmissions() throws Exception {
		assertThat(planForm.getAdmissions(), is(admissions));
	}
}
