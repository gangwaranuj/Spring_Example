package com.workmarket.domains.compliance.dao;

import com.workmarket.domains.compliance.model.AssignmentCountComplianceRule;
import com.workmarket.domains.compliance.model.ComplianceRuleType;
import com.workmarket.domains.compliance.model.PeriodicComplianceRule;
import com.workmarket.service.business.BaseServiceIT;
import com.workmarket.test.IntegrationTest;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.lang.reflect.InvocationTargetException;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsCollectionContaining.hasItem;
import static org.hamcrest.core.IsNot.not;

@RunWith(SpringJUnit4ClassRunner.class)
@Category(IntegrationTest.class)
public class ComplianceRuleTypeDAOIT extends BaseServiceIT {
	@Autowired ComplianceRuleTypeDAO dao;

	List<ComplianceRuleType> types;

	@Before
	public void setUp() {
		types = dao.findAll();
	}

	@Test
	public void types_doesNot_have_AbstractClasses_PeriodicComplianceRule() throws InvocationTargetException, NoSuchMethodException, IllegalAccessException {
		assertThat(types, not(hasItem(createComplianeRuleType(PeriodicComplianceRule.class))));
	}

	@Test
	public void types_has_AssignmentCountComplianceRule() throws InvocationTargetException, NoSuchMethodException, IllegalAccessException {
		assertThat(types, hasItem(createComplianeRuleType(AssignmentCountComplianceRule.class)));
	}

	@SuppressWarnings(value="unchecked")
	private ComplianceRuleType createComplianeRuleType(Class klass) throws InvocationTargetException, NoSuchMethodException, IllegalAccessException {
		String humanName = (String) klass.getMethod("getHumanTypeName").invoke(null);
		boolean allowMultiple = (boolean) klass.getMethod("allowMultiple").invoke(null);
		return new ComplianceRuleType(klass.getSimpleName(), humanName, allowMultiple);
	}
}
