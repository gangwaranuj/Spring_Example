package com.workmarket.service.business;

import com.google.common.collect.Lists;
import com.workmarket.dao.PlanDAO;
import com.workmarket.domains.model.Plan;
import com.workmarket.domains.model.planconfig.AbstractPlanConfig;
import com.workmarket.domains.model.planconfig.TransactionFeePlanConfig;
import com.workmarket.domains.model.planconfig.PlanConfigVisitor;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.List;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.never;

@RunWith(MockitoJUnitRunner.class)
public class PlanServiceImplTest {
	private static final long SOME_PLAN_ID = 999999L;
	private static final String SOME_PLAN_CODE = "SUPERDUPER";
	private static final Long COMPANY_ID = 111111L;

	@Mock PlanDAO dao;
	@Mock PlanConfigVisitor planConfigVisitor;
	@InjectMocks PlanService service = new PlanServiceImpl();

	List<Plan> plans;
	Plan plan;

	TransactionFeePlanConfig transactionFeePlanConfig;

	@Before
	public void setUp() throws Exception {
		plan = mock(Plan.class);

		transactionFeePlanConfig = mock(TransactionFeePlanConfig.class);
		List<AbstractPlanConfig> planConfigs = Lists.newArrayList();
		planConfigs.add(transactionFeePlanConfig);
		when(plan.getPlanConfigs()).thenReturn(planConfigs);

		plans = Lists.newArrayList(plan);

		when(dao.findBy("id", SOME_PLAN_ID)).thenReturn(plan);
		when(dao.findBy("code", SOME_PLAN_CODE)).thenReturn(plan);
		when(dao.getAll()).thenReturn(plans);
	}

	@Test
	public void getAllPlans_CallsGetAllOnDao() throws Exception {
		service.getAllPlans();
		verify(dao).getAll();
	}

	@Test
	public void getAllPlans_ReturnsAListOfPlans() throws Exception {
		assertThat(service.getAllPlans(), is(plans));
	}

	@Test
	public void find_WithAnId_CallsFindByOnTheDao() throws Exception {
		service.find(SOME_PLAN_ID);
		verify(dao).findBy("id", SOME_PLAN_ID);
	}

	@Test
	public void find_WithAnId_ReturnsThePlan() throws Exception {
		assertThat(service.find(SOME_PLAN_ID), is(plan));
	}

	@Test
	public void find_WithACode_CallsFindByOnTheDao() throws Exception {
		service.find(SOME_PLAN_CODE);
		verify(dao).findBy("code", SOME_PLAN_CODE);
	}

	@Test
	public void find_WithACode_ReturnsThePlan() throws Exception {
		assertThat(service.find(SOME_PLAN_CODE), is(plan));
	}

	@Test
	public void save_WithAPlan_SavesOrUpdatesThePlan() throws Exception {
		service.save(plan);
		verify(dao).saveOrUpdate(plan);
	}

	@Test
	public void save_WithAPlan_ReturnsThePlan() throws Exception {
		assertThat(service.save(plan), is(plan));
	}

	@Test
	public void update_WithAPlan_MergesThePlan() throws Exception {
		service.update(plan);
		verify(dao).merge(plan);
	}

	@Test
	public void destroy_WithAPlanId_FindsThePlan() throws Exception {
		service.destroy(SOME_PLAN_ID);
		verify(dao).findBy("id", SOME_PLAN_ID);
	}

	@Test
	public void destroy_WithAPlanId_DeletesThePlan() throws Exception {
		service.destroy(SOME_PLAN_ID);
		verify(dao).delete(plan);
	}

	@Test
	public void applyPlanCodes_VerifyAccept() throws Exception {
		service.applyPlanConfigs(COMPANY_ID, SOME_PLAN_CODE);

		verify(transactionFeePlanConfig).accept(planConfigVisitor, COMPANY_ID);
	}

	@Test
	public void applyPlanCodes_NoPlanNull() throws Exception {
		service.applyPlanConfigs(COMPANY_ID, null);

		verify(transactionFeePlanConfig, never()).accept(planConfigVisitor, COMPANY_ID);
	}

	@Test
	public void applyPlanCodes_NoPlanNotFound() throws Exception {
		when(dao.findBy("code", SOME_PLAN_CODE)).thenReturn(null);
		service.applyPlanConfigs(COMPANY_ID, SOME_PLAN_CODE);

		verify(transactionFeePlanConfig, never()).accept(planConfigVisitor, COMPANY_ID);
	}
}
