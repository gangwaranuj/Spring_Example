package com.workmarket.service.business;

import com.google.common.collect.Lists;
import com.workmarket.domains.model.Company;
import com.workmarket.domains.model.Plan;
import com.workmarket.domains.model.account.AccountRegister;
import com.workmarket.domains.model.account.WorkFeeBand;
import com.workmarket.domains.model.planconfig.AbstractPlanConfig;
import com.workmarket.domains.model.planconfig.TransactionFeePlanConfig;
import com.workmarket.test.IntegrationTest;
import org.hamcrest.Matchers;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasProperty;
import static org.hamcrest.collection.IsEmptyCollection.empty;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsCollectionContaining.hasItem;

@RunWith(SpringJUnit4ClassRunner.class)
@Category(IntegrationTest.class)
public class PlanServiceImplIT extends BaseServiceIT {
	private static final BigDecimal TRANSACTION_FEE_PERCENTAGE = new BigDecimal(6);

	@Autowired PlanService service;

	@Before
	public void setup() throws Exception {
		for (Plan plan : service.getAllPlans()) {
			service.destroy(plan.getId());
		}
	}

	@After
	public void teardown() throws Exception {
		setup();
	}

	@Test
	public void getAllPlans_getsAllTheSavedPlans() {
		List<Plan> plans = service.getAllPlans();
		assertThat(plans, is(empty()));

		Plan plan = makePlan("SUPERDUPER", "Super Duper");
		service.save(plan);

		plans = service.getAllPlans();
		assertThat(plans, hasItem(Matchers.<Plan>hasProperty("code", equalTo("SUPERDUPER"))));
	}

	@Test
	public void find_WithId_FindsThePlan() {
		Plan plan = makePlan("SUPERDUPER", "Super Duper");
		service.save(plan);

		assertThat(service.find(plan.getId()), hasProperty("code", equalTo("SUPERDUPER")));
	}

	@Test
	public void find_WithCode_FindsThePlan() {
		Plan plan = makePlan("SUPERDUPER", "Super Duper");
		service.save(plan);

		assertThat(service.find(plan.getCode()), hasProperty("id", equalTo(plan.getId())));
	}

	@Test
	public void update_WithPlan_UpdatesThePlan() {
		Plan plan = makePlan("SUPERDUPER", "Super Duper");
		service.save(plan);
		plan.setCode("WHATEVS");
		service.update(plan);

		assertThat(service.find(plan.getCode()), hasProperty("code", equalTo("WHATEVS")));
	}

	@Test
	public void destroy_WithSavedPlan_DeletesThePlan() {
		Plan plan = makePlan("SUPERDUPER", "Super Duper");
		service.save(plan);
		service.destroy(plan.getId());

		assertThat(service.find(plan.getCode()), is(nullValue()));
	}

	@Test
	@Transactional
	public void applyPlanConfigs_WithCompanyIdAndPlanCode_SetsCurrentWorkFeePercentageOnAccountRegister() {
		Plan plan = makePlan("WONDERFUL", "Super Duper", TRANSACTION_FEE_PERCENTAGE);
		service.save(plan);

		Company company = newCompany();

		service.applyPlanConfigs(company.getId(), plan.getCode());

		company = companyService.findCompanyById(company.getId());
		AccountRegister accountRegister =  company.getAccountRegisters().iterator().next();
		WorkFeeBand workFeeBand = accountRegister.getWorkFeeConfigurations().iterator().next().getWorkFeeBands().iterator().next();

		assertThat(accountRegister.getCurrentWorkFeePercentage(), is(TRANSACTION_FEE_PERCENTAGE));
		assertThat(workFeeBand.getPercentage(), is(TRANSACTION_FEE_PERCENTAGE));
	}

	private Plan makePlan(String code, String description) {
		return makePlan(code, description, null);
	}

	private Plan makePlan(String code, String description, BigDecimal transactionFeePercentage) {
		Plan plan = new Plan();
		plan.setCode(code);
		plan.setDescription(description);

		if (transactionFeePercentage != null) {
			TransactionFeePlanConfig transactionFeePlanConfig = new TransactionFeePlanConfig();
			transactionFeePlanConfig.setPercentage(transactionFeePercentage);
			transactionFeePlanConfig.setPlan(plan);
			List<AbstractPlanConfig> planConfigs = Lists.newArrayList();
			planConfigs.add(transactionFeePlanConfig);
			plan.setPlanConfigs(planConfigs);
		}

		return plan;
	}
}
