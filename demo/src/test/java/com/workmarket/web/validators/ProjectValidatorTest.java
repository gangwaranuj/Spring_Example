package com.workmarket.web.validators;

import com.workmarket.domains.model.crm.ClientCompany;
import com.workmarket.domains.work.model.project.Project;
import com.workmarket.domains.model.User;
import com.workmarket.domains.model.Company;
import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.BlockJUnit4ClassRunner;
import java.math.BigDecimal;
import java.util.ArrayList;
import org.springframework.validation.Validator;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

@RunWith(BlockJUnit4ClassRunner.class)
public class ProjectValidatorTest extends BaseValidatorTest {

	private ProjectValidator validator;
	private Project project;

	@Before
	public void setup() {
		validator = new ProjectValidator();
		User user = new User();
		Company company = new Company();
		ClientCompany clientCompany = new ClientCompany();

		project = new Project();
		project.setName("Testing");
		project.setDescription("This is a test project");
		project.setStartDate(new DateTime().toCalendar(null));
		project.setDueDate(new DateTime().plus(10000L).toCalendar(null));
		project.setExpectedRevenue(new BigDecimal(100));
		project.setAnticipatedCost(new BigDecimal(10));
		project.setCode("10001");
		project.setReservedFundsEnabled(false);
		project.setBudgetEnabledFlag(false);
		project.setOwner(user);
		project.setCompany(company);
		project.setClientCompany(clientCompany);
	}

	@Test
	public void test_support_pass() {
		assertTrue(validator.supports(Project.class));
	}

	@Test
	public void test_support_fail() {
		assertFalse(validator.supports(ArrayList.class));
	}

	@Test
	public void test_NameEmpty_fail() {
		project.setName(EMPTY_TOKEN);
		assertTrue(hasErrorCode(validate(project), "projects.title.required"));
	}

	@Test
	public void test_DescEmpty_fail() {
		project.setDescription(EMPTY_TOKEN);
		assertTrue(hasErrorCode(validate(project), "projects.description.required"));
	}

	@Test
	public void test_OwnerNull_fail() {
		project.setOwner(null);
		assertTrue(hasErrorCode(validate(project), "projects.creator.required"));
	}

	@Test
	public void test_ClientCompanyNull_fail() {
		project.setClientCompany(null);
		assertTrue(hasErrorCode(validate(project), "projects.client.required"));
	}

	@Test
	public void test_Budget_fail() {
		project.setBudgetEnabledFlag(true);
		project.setRemainingBudget(BigDecimal.ZERO);
		assertTrue(hasErrorCode(validate(project), "projects.remainingBudget.greaterThanZero"));
	}

	@Test
	public void test_BudgetRemaining_pass() {
		project.setBudgetEnabledFlag(true);
		project.setRemainingBudget(BigDecimal.ONE);
		assertFalse(validate(project).hasErrors());
	}

	@Test
	public void test_ProjectNameXSS_fail() {
		project.setName("helloo <script>alert('bleh!');</script>");
		assertTrue(hasErrorCode(validate(project), "projects.title.invalid"));
	}

	@Test
	public void test_ProjectNameHTML_fail() {
		project.setName("helloo <a href='place'>title</a>");
		assertTrue(hasErrorCode(validate(project), "projects.title.invalid"));

		project.setName("<b>very</b> large");
		assertTrue(hasErrorCode(validate(project), "projects.title.invalid"));

		project.setName("br<br/>br<br/>breakdown");
		assertTrue(hasErrorCode(validate(project), "projects.title.invalid"));
	}

	@Test
	public void test_Valid_pass() {
		assertFalse(validate(project).hasErrors());
	}

	protected Validator getValidator() {
		return validator;
	}
}
