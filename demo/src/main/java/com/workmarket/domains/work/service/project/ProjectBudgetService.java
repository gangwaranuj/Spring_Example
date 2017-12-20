package com.workmarket.domains.work.service.project;

import com.workmarket.domains.work.model.project.Project;
import com.workmarket.service.exception.project.InsufficientBudgetException;

import java.math.BigDecimal;

/**
 * Date: 6/18/13
 * Time: 11:11 AM
 */
public interface ProjectBudgetService {

	public BigDecimal calcTotalWorkValue(Project project);

	public BigDecimal calcTotalWorkValueInProcess(Project project);

	public BigDecimal calcTotalWorkValueInPaid(Project project);

	public int countAssignmentsByProject(Project project);

	public void increaseRemainingBudget(Project project, BigDecimal amount);

	public void decreaseRemainingBudget(Project project, BigDecimal amount) throws InsufficientBudgetException;

}
