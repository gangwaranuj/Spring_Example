package com.workmarket.service.business;

import com.workmarket.domains.model.User;
import com.workmarket.domains.model.crm.ClientCompany;
import com.workmarket.domains.work.model.project.Project;
import com.workmarket.domains.work.model.project.ProjectPagination;
import com.workmarket.service.business.dto.ProjectDTO;
import com.workmarket.service.infra.security.RequestContext;
import com.workmarket.test.IntegrationTest;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.Assert.*;

@RunWith(SpringJUnit4ClassRunner.class)
@Category(IntegrationTest.class)
public class ProjectServiceIT extends BaseServiceIT {

	@Autowired UserService userService;

	@Test
	public void test_findProject() throws Exception {
		Project project = projectService.findById(PROJECT_ID);

		assertNotNull(project);
	}

	@Test
	public void test_findProjectsForCompany() throws Exception {
		ProjectPagination pagination = new ProjectPagination();
		pagination.setReturnAllRows();

		ProjectPagination results = projectService.findProjectsForCompany(COMPANY_ID, pagination);

		assertTrue(results.getResults().size() > 0);

		for (Project project : results.getResults()) {
			User user = userService.findUserById(project.getCreatorId());
			assertEquals(COMPANY_ID, user.getCompany().getId());
		}
	}

	@Test
	@Transactional
	public void test_createProject() throws Exception {
		ProjectDTO dto = new ProjectDTO();
		dto.setName("Project Title");
		dto.setDescription("Project Description");

		dto.setDueDateString("2010-12-01T09:00:00Z");
		dto.setAnticipatedCost(new BigDecimal(522363.50));
		dto.setExpectedRevenue(new BigDecimal(5689232.22));
		dto.setTargetMargin(new BigDecimal(56233.12));
		dto.setStartDateString("2010-12-01T09:00:00Z");
		dto.setClientCompanyId(CLIENT_COMPANY_ID);

		Project project = projectService.saveOrUpdateProject(FRONT_END_USER_ID, dto);

		assertNotNull(project);
		assertTrue(project.getId() > 0);

		assertEquals(project.getName(), "Project Title");
		assertEquals(project.getDescription(), "Project Description");

		assertNotNull(project.getDueDate());
		assertNotNull(project.getAnticipatedCost());
		assertNotNull(project.getExpectedRevenue());
		assertNotNull(project.getTargetMargin());
		assertNotNull(project.getStartDate());
	}

	@Test
	public void test_findAllProjectsByClientCompany() throws Exception {
		User employee = newFirstEmployeeWithCashBalance();

		ClientCompany clientCompany = newClientCompany(employee.getId());

		ProjectPagination pagination = new ProjectPagination();
		pagination.setReturnAllRows();

		pagination = projectService.findAllProjectsForClientCompany(employee.getCompany().getId(), clientCompany.getId(), pagination);

		Assert.assertEquals(Integer.valueOf(0), pagination.getRowCount());
		newProject(employee.getId(), clientCompany.getId(), "Project Title");

		pagination = projectService.findAllProjectsForClientCompany(employee.getCompany().getId(), clientCompany.getId(), pagination);
		Assert.assertEquals(Integer.valueOf(1), pagination.getRowCount());
	}

	@Test
	public void test_requestContext() throws Exception {
		User employee1 = newFirstEmployeeWithCashBalance();
		User employee2 = newCompanyEmployee(employee1.getCompany().getId());
		User contractor = newContractorIndependentLane4ReadyWithCashBalance();

		authenticationService.setCurrentUser(employee1);

		ClientCompany clientCompany = newClientCompany(employee1.getId());
		Project project = newProject(employee1.getId(), clientCompany.getId(), "Project Title");

		List<RequestContext> contexts = projectService.getRequestContext(project.getId());
		Assert.assertEquals(2, contexts.size());
		Assert.assertTrue(contexts.contains(RequestContext.OWNER));

		authenticationService.setCurrentUser(employee2);
		contexts = projectService.getRequestContext(project.getId());
		Assert.assertEquals(2, contexts.size());
		Assert.assertTrue(contexts.contains(RequestContext.COMPANY_OWNED));

		authenticationService.setCurrentUser(contractor);
		contexts = projectService.getRequestContext(project.getId());
		Assert.assertEquals(1, contexts.size());
		Assert.assertTrue(contexts.contains(RequestContext.PUBLIC));
		Assert.assertFalse(contexts.contains(RequestContext.COMPANY_OWNED));
	}

}
