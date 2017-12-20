package com.workmarket.domains.work.service.project;

import com.workmarket.domains.model.WorkStatusType;
import com.workmarket.domains.work.model.Work;
import com.workmarket.domains.work.model.project.Project;
import com.workmarket.service.business.PricingService;
import com.workmarket.domains.work.service.WorkService;
import com.workmarket.service.exception.project.InsufficientBudgetException;
import com.workmarket.utility.NumberUtilities;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.math.BigDecimal;
import java.util.List;

/**
 * Date: 6/18/13
 * Time: 11:19 AM
 */

@Service
public class ProjectBudgetServiceImpl implements ProjectBudgetService {

	private static final Log logger = LogFactory.getLog(ProjectBudgetServiceImpl.class);
	@Autowired ProjectService projectService;
	@Autowired WorkService workService;
	@Autowired PricingService pricingService;


	@Override
	public BigDecimal calcTotalWorkValue(Project project) {
		return calcTotalWorkValueInProcess(project).add(calcTotalWorkValueInPaid(project));
	}

	@Override
	public BigDecimal calcTotalWorkValueInProcess(Project project) {
		List<Work> workList = workService.findAllWorkByProjectByStatus(project.getId(), WorkStatusType.SENT, WorkStatusType.INPROGRESS, WorkStatusType.ACTIVE, WorkStatusType.PAYMENT_PENDING, WorkStatusType.CANCELLED_PAYMENT_PENDING);
		return calcWorkValue(workList);
	}

	@Override
	public BigDecimal calcTotalWorkValueInPaid(Project project) {
		List<Work> workList = workService.findAllWorkByProjectByStatus(project.getId(), WorkStatusType.PAID, WorkStatusType.CANCELLED_WITH_PAY);
		return calcWorkValue(workList);
	}

	private BigDecimal calcWorkValue(List<Work> workList) {
		BigDecimal totalWorkValue = BigDecimal.ZERO;

		for(Work work: workList) {
			BigDecimal workValue = NumberUtilities.roundHalfUp(pricingService.calculateWorkPrice(work));
			// Use round half down for consistency with account register round money method
			// See detail in accountRegisterServiceAbstract.roundCostAndFees
			BigDecimal transactionFee = NumberUtilities.roundHalfDown(pricingService.calculateBuyerNetMoneyFee(work, workValue));
			totalWorkValue = totalWorkValue.add(workValue);
			totalWorkValue = totalWorkValue.add(transactionFee);
		}
		return totalWorkValue;
	}

	@Override
	public int countAssignmentsByProject(Project project) {
		Assert.notNull(project);
		return workService.findAllWorkByProject(project.getId()).size();
	}

	@Override
	public void increaseRemainingBudget(Project project, BigDecimal amount){
		Assert.notNull(project);
		Assert.notNull(amount);
		if(project.getBudgetEnabledFlag()) {
			Assert.notNull(project.getRemainingBudget());
			project.setRemainingBudget(project.getRemainingBudget().add(amount));
			projectService.saveOrUpdate(project);
		}
	}

	@Override
	public void decreaseRemainingBudget(Project project, BigDecimal amount) throws InsufficientBudgetException{
		Assert.notNull(project);
		Assert.notNull(amount);
		if(project.getBudgetEnabledFlag()) {
			Assert.notNull(project.getRemainingBudget());
			if(project.getRemainingBudget().compareTo(amount) == -1) {
				logger.debug("Project remaining budget are: " + project.getRemainingBudget().toString() + ". The amount are : " + amount.toString());
				throw new InsufficientBudgetException("There is no enough budget in the project !");
			}
			project.setRemainingBudget(project.getRemainingBudget().subtract(amount));
			projectService.saveOrUpdate(project);
		}

	}
}
