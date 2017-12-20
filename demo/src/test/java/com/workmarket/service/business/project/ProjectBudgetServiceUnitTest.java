package com.workmarket.service.business.project;

import com.workmarket.domains.work.model.project.Project;
import com.workmarket.domains.work.service.project.ProjectBudgetServiceImpl;
import com.workmarket.domains.work.service.project.ProjectServiceImpl;
import com.workmarket.service.exception.project.InsufficientBudgetException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.math.BigDecimal;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Date: 7/16/13
 * Time: 2:39 PM
 */
@RunWith(MockitoJUnitRunner.class)
public class ProjectBudgetServiceUnitTest {

	@InjectMocks ProjectBudgetServiceImpl projectBudgetService;
	@Mock ProjectServiceImpl projectServcie;
	private Project project;

	@Before
	public void setup() {
		project = mock(Project.class);
		when(project.getId()).thenReturn(1L);
		when(project.getRemainingBudget()).thenReturn(BigDecimal.valueOf(100.00));
		when(project.getBudgetEnabledFlag()).thenReturn(true);
		when(projectServcie.findById(project.getId())).thenReturn(project);

	}

	@Test
	public void increaseRemainingBudget_success() {
		projectBudgetService.increaseRemainingBudget(project, BigDecimal.valueOf(50.00));
		verify(project).setRemainingBudget(BigDecimal.valueOf(150.00));
	}

	@Test
	public void decreaseRemainingBudget_haveSuffcientBudget_success() {
		projectBudgetService.decreaseRemainingBudget(project, BigDecimal.valueOf(40.00));
		verify(project).setRemainingBudget(BigDecimal.valueOf(60.00));
	}

	@Test (expected = InsufficientBudgetException.class)
	public void decreaseRemainingBudget_doNotHaveSufficientBudget_throwsInsufficientBudgetException() {
		projectBudgetService.decreaseRemainingBudget(project, BigDecimal.valueOf(200.00));
	}

}
