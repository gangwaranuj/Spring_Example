package com.workmarket.domains.model.compliance;

import com.google.api.client.util.Lists;
import com.workmarket.domains.authentication.features.FeatureEvaluator;
import com.workmarket.domains.compliance.model.AbstractComplianceRule;
import com.workmarket.domains.compliance.model.AssignmentCountComplianceRule;
import com.workmarket.domains.compliance.model.ComplianceRuleSet;
import com.workmarket.domains.compliance.model.WorkComplianceCriterion;
import com.workmarket.domains.compliance.service.ComplianceRuleSetsService;
import com.workmarket.domains.compliance.service.ComplianceServiceImpl;
import com.workmarket.domains.compliance.service.CompliantVisitor;
import com.workmarket.domains.model.Company;
import com.workmarket.domains.model.DateRange;
import com.workmarket.domains.model.User;
import com.workmarket.domains.work.model.Work;
import com.workmarket.domains.work.service.WorkService;
import com.workmarket.service.business.UserService;
import com.workmarket.service.exception.search.SearchException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import static org.mockito.Mockito.any;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.List;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ComplianceServiceImplTest {
	@Mock private WorkService workService;
	@Mock private UserService userService;
	@Mock private CompliantVisitor compliantVisitor;
	@Mock private ComplianceRuleSetsService complianceRuleSetsService;
	@Mock private FeatureEvaluator featureEvaluator;
	@InjectMocks ComplianceServiceImpl service = spy(new ComplianceServiceImpl());

	User user;
	Work work;
	DateRange schedule;
	Company company;
	AssignmentCountComplianceRule assignmentCountComplianceRule;

	@Before
	public void setUp() throws SearchException {
		schedule = mock(DateRange.class);
		user = mock(User.class);
			when(user.getId()).thenReturn(1L);
		company = mock(Company.class);
			when(company.getId()).thenReturn(1L);
		work = mock(Work.class);
			when(work.getCompany()).thenReturn(company);
			when(work.getSchedule()).thenReturn(schedule);

		assignmentCountComplianceRule = mock(AssignmentCountComplianceRule.class);
		List<AbstractComplianceRule> assignmentCountComplianceRules = Lists.newArrayList();
		assignmentCountComplianceRules.add(assignmentCountComplianceRule);

		ComplianceRuleSet complianceRuleSet = mock(ComplianceRuleSet.class);
		when(complianceRuleSet.getComplianceRules()).thenReturn(assignmentCountComplianceRules);

		List<ComplianceRuleSet> complianceRuleSets = Lists.newArrayList();
		complianceRuleSets.add(complianceRuleSet);
		when(complianceRuleSetsService.findAll(company.getId())).thenReturn(complianceRuleSets);
	}

	@Test
	public void getComplianceFor_callsRuleSetsService() {
		service.getComplianceFor(user, work, schedule);
		verify(complianceRuleSetsService, times(1)).findAll(company.getId());
	}

	@Test
	public void getComplianceFor_callsAccept() {
		service.getComplianceFor(user, work, schedule);
		verify(assignmentCountComplianceRule, times(1)).accept(any(CompliantVisitor.class), any(WorkComplianceCriterion.class));
	}
}

