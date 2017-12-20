package com.workmarket.domains.model.compliance;

import com.google.api.client.util.Lists;
import com.google.common.collect.ImmutableList;
import com.workmarket.domains.compliance.model.AbstractComplianceRule;
import com.workmarket.domains.compliance.model.AssignmentCountComplianceRule;
import com.workmarket.domains.compliance.model.Compliance;
import com.workmarket.domains.compliance.model.ComplianceRuleSet;
import com.workmarket.domains.compliance.model.PeriodicComplianceRule;
import com.workmarket.domains.compliance.service.ComplianceRuleSetsService;
import com.workmarket.domains.compliance.service.ComplianceService;
import com.workmarket.domains.model.User;
import com.workmarket.domains.work.model.Work;
import com.workmarket.domains.work.model.WorkBundle;
import com.workmarket.domains.work.service.WorkService;
import com.workmarket.domains.work.service.route.WorkBundleRouting;
import com.workmarket.service.business.BaseServiceIT;
import com.workmarket.service.business.LaneService;
import com.workmarket.service.infra.business.AuthenticationService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

@RunWith(SpringJUnit4ClassRunner.class)
public class ComplianceServiceImplIT extends BaseServiceIT {

	@Autowired private AuthenticationService authenticationService;
	@Autowired private LaneService laneService;
	@Autowired private WorkService workService;
	@Autowired private ComplianceRuleSetsService complianceRuleSetService;
	@Autowired private ComplianceService complianceService;
	@Autowired private WorkBundleRouting workBundleRouting;

	User buyer, worker;

	@Before
	public void setup() throws Exception {
		buyer = newEmployeeWithCashBalance();
		worker = newContractorIndependentLane4ReadyWithCashBalanceWithTaxEntity(true);
		laneService.addUserToCompanyLane3(worker.getId(), buyer.getCompany().getId());
		authenticationService.setCurrentUser(buyer);
		createComplianceRuleSet();
	}

	@Test
	public void test_ComplianceRuleForWork() throws Exception {
		Work work1 = newWork(buyer.getId());
		Work work2 = newWork(buyer.getId());
		Work work3 = newWork(buyer.getId());

		workRoutingService.addToWorkResources(work1.getId(), worker.getId());
		Compliance compliant = complianceService.getComplianceFor(worker, work1);
		assertThat(compliant.isCompliant(), is(true));

		workService.acceptWork(worker.getId(), work1.getId());

		workRoutingService.addToWorkResources(work2.getId(), worker.getId());
		compliant = complianceService.getComplianceFor(worker, work2);
		assertThat(compliant.isCompliant(), is(true));

		workService.acceptWork(worker.getId(), work2.getId());

		workRoutingService.addToWorkResources(work3.getId(), worker.getId());
		compliant = complianceService.getComplianceFor(worker, work3);
		assertThat(compliant.isCompliant(), is(false));
	}

	@Test
	public void test_ComplianceRuleForBundle_compliant() throws Exception {
		WorkBundle bundle = newWorkBundle(buyer.getId());
		Work work1 = newWork(buyer.getId());
		Work work2 = newWork(buyer.getId());
		workBundleService.addAllToBundleByWork(bundle, ImmutableList.of(work1, work2));

		workRoutingService.addToWorkResources(bundle.getId(), worker.getId());
		workBundleRouting.routeWorkBundle(bundle.getId());

		Compliance compliant = complianceService.getComplianceFor(worker, bundle);
		assertThat(compliant.isCompliant(), is(true));
	}

	@Test
	public void test_ComplianceRuleForBundle_noncompliant() throws Exception {
		WorkBundle bundle = newWorkBundle(buyer.getId());
		Work work1 = newWork(buyer.getId());
		Work work2 = newWork(buyer.getId());
		Work work3 = newWork(buyer.getId());
		workBundleService.addAllToBundleByWork(bundle, ImmutableList.of(work1, work2, work3));

		workRoutingService.addToWorkResources(bundle.getId(), worker.getId());
		workBundleRouting.routeWorkBundle(bundle.getId());

		Compliance compliant = complianceService.getComplianceFor(worker, bundle);
		assertThat(compliant.isCompliant(), is(false));
	}

	private void createComplianceRuleSet() {
		AssignmentCountComplianceRule rule1 = new AssignmentCountComplianceRule();
		AssignmentCountComplianceRule rule2 = new AssignmentCountComplianceRule();
		rule1.setMaxAssignments(2L);
		rule1.setPeriodType(PeriodicComplianceRule.PeriodType.WEEK);
		rule1.setPeriodValue(1L);
		rule2.setMaxAssignments(3L);
		rule2.setPeriodType(PeriodicComplianceRule.PeriodType.MONTH);
		rule2.setPeriodValue(1L);

		List<AbstractComplianceRule> rules = Lists.newArrayList();
		rules.add(rule1);
		rules.add(rule2);

		ComplianceRuleSet ruleSet = new ComplianceRuleSet();
		ruleSet.setName("default");
		ruleSet.setActive(true);
		ruleSet.setComplianceRules(rules);

		complianceRuleSetService.save(ruleSet);
	}
}
