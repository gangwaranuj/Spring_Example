package com.workmarket.domains.model.compliance;

import com.google.api.client.util.Lists;
import com.workmarket.domains.compliance.model.AbstractComplianceRule;
import com.workmarket.domains.compliance.model.AssignmentCountComplianceRule;
import com.workmarket.domains.compliance.model.ComplianceRuleSet;
import com.workmarket.domains.compliance.model.PeriodicComplianceRule;
import com.workmarket.domains.compliance.service.ComplianceRuleSetsService;
import com.workmarket.service.business.BaseServiceIT;
import com.workmarket.test.IntegrationTest;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;

import static ch.lambdaj.Lambda.extract;
import static ch.lambdaj.Lambda.on;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsCollectionContaining.hasItem;
import static org.hamcrest.core.IsNot.not;

@RunWith(SpringJUnit4ClassRunner.class)
@Category(IntegrationTest.class)
public class ComplianceRuleSetServiceIT extends BaseServiceIT {
	@Autowired ComplianceRuleSetsService service;

	@Test
	public void ComplianceRuleSetsService_Find() {
		Long expected = createBasicComplianceRuleSet().getId();
		Long actual = service.find(expected).getId();

		assertThat(actual, is(expected));
	}

	@Test
	public void ComplianceRuleSetsService_Active_Only() {
		ComplianceRuleSet ruleSetOne = createBasicComplianceRuleSet();
		ComplianceRuleSet ruleSetTwo = createBasicComplianceRuleSet();

		ruleSetTwo.setActive(false);
		service.update(ruleSetTwo);

		List<Long> activeIds = extract(service.findAllActive(), on(ComplianceRuleSet.class).getId());

		assertThat(activeIds, not(hasItem(ruleSetTwo.getId())));
		assertThat(activeIds, hasItem(ruleSetOne.getId()));
	}

	private ComplianceRuleSet createBasicComplianceRuleSet() {
		AssignmentCountComplianceRule a = new AssignmentCountComplianceRule();
		a.setMaxAssignments(10L);
		a.setPeriodType(PeriodicComplianceRule.PeriodType.WEEK);
		a.setPeriodValue(5L);

		ComplianceRuleSet c = new ComplianceRuleSet();
		c.setName("Test Rule Set");
		c.setActive(true);

		List<AbstractComplianceRule> rules = Lists.newArrayList();
		rules.add(a);
		c.setComplianceRules(rules);

		service.save(c);

		return c;
	}
}
