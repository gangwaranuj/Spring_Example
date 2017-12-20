package com.workmarket.service.summary;

import com.google.common.collect.Sets;
import com.workmarket.configuration.Constants;
import com.workmarket.domains.model.Company;
import com.workmarket.domains.model.User;
import com.workmarket.domains.model.WorkStatusType;
import com.workmarket.domains.model.cache.MoneyAggregateSummary;
import com.workmarket.domains.model.cache.PeopleAggregateSummary;
import com.workmarket.domains.model.summary.TimeDimension;
import com.workmarket.domains.model.summary.work.WorkStatusTransition;
import com.workmarket.domains.work.model.Work;
import com.workmarket.service.business.BaseServiceIT;
import com.workmarket.service.business.CompanyService;
import com.workmarket.service.business.LaneService;
import com.workmarket.service.business.dto.CompleteWorkDTO;
import com.workmarket.test.IntegrationTest;
import com.workmarket.utility.DateUtilities;
import net.jcip.annotations.NotThreadSafe;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.List;
import java.util.Set;
import java.util.TimeZone;

@RunWith(SpringJUnit4ClassRunner.class)
@Category(IntegrationTest.class)
@NotThreadSafe
public class SummaryServiceIT extends BaseServiceIT {

	@Autowired private SummaryService summaryService;
	@Autowired private CompanyService companyService;
	@Autowired private LaneService laneService;

	@Test
	public void findMoneyAggregateSummaryByCompany_withNewUser_success() throws Exception {
		User employee = newEmployeeWithCashBalance();

		MoneyAggregateSummary summary = summaryService.findMoneyAggregateSummaryByCompany(employee.getCompany().getId());

		Assert.assertNotNull(summary.getTotal());
		Assert.assertNotNull(summary.getEarnedInProgress());
		Assert.assertNotNull(summary.getEarnedPending());
		Assert.assertNotNull(summary.getEarnedAvailable());

	}

	@Test
	public void findPeopleAggregateSummaryByCompany_success() throws Exception {
		PeopleAggregateSummary peopleAggregateSummary = summaryService.findPeopleAggregateSummaryByCompany(COMPANY_ID);
		Assert.assertNotNull(peopleAggregateSummary);
		Assert.assertNotNull(peopleAggregateSummary.getLane1());
		Assert.assertNotNull(peopleAggregateSummary.getLane2());
		Assert.assertNotNull(peopleAggregateSummary.getLane3());
		Assert.assertNotNull(peopleAggregateSummary.getLane0());
		Assert.assertNotNull(peopleAggregateSummary.getLane3WithEINTaxEntity());

		Assert.assertNotNull(peopleAggregateSummary.getGroups());
		Assert.assertNotNull(peopleAggregateSummary.getInvitations());
		Assert.assertNotNull(peopleAggregateSummary.getCampaigns());
		Assert.assertNotNull(peopleAggregateSummary.getGroupMembers());
	}


	@Test
	public void findTimeDimensionId_withToday_success() throws Exception {
		Long timeDimension = summaryService.findTimeDimensionId(Calendar.getInstance());
		Assert.assertTrue(timeDimension.intValue() > 0);
	}

	@Test
	public void workStatusTransition_fullCycle_success() throws Exception {
		User buyer = newEmployeeWithCashBalance();
		Company company = companyService.findCompanyById(buyer.getCompany().getId());
		Assert.assertTrue(company.getManageMyWorkMarket().getAutoRateEnabledFlag());

		User contractor = newContractorIndependentlane4Ready();
		laneService.addUserToCompanyLane3(contractor.getId(), company.getId());
		Set<String> users = Sets.newHashSet();
		users.add(contractor.getUserNumber());

		Work work = newWork(buyer.getId());

		workRoutingService.addToWorkResources(work.getWorkNumber(), users);
		workService.acceptWork(contractor.getId(), work.getId());

		CompleteWorkDTO dto = new CompleteWorkDTO();
		dto.setResolution("Im done");
		dto.setHoursWorked(1.5);
		dto.setSalesTaxCollectedFlag(false);

		authenticationService.setCurrentUser(contractor.getId());
		workService.completeWork(work.getId(), dto);

		authenticationService.setCurrentUser(buyer.getId());
		workService.closeWork(work.getId());

		/**
		 *  There should be 6 records:
		 *  draft
		 *  sent
		 *  active
		 *  complete
		 *  closed
		 *  paid
		 */

		List<WorkStatusTransition> transitionList = summaryService.findAllTransitionsByWork(work.getId());
		Assert.assertNotNull(transitionList);
		Assert.assertEquals(transitionList.size(), 6);
		for (WorkStatusTransition t : transitionList) {
			Assert.assertNotNull(t.getWorkPrice());
			Assert.assertTrue(t.getWorkPrice().compareTo(BigDecimal.ZERO) > 0);
		}

		Assert.assertNotNull(companyService.findCompanyById(buyer.getCompany().getId()).getFirstCreatedAssignmentOn());
	}

	@Test
	public void countWorkStatusTransitions_withDraft_success() throws Exception {

		long workId = 0;
		User buyer = newEmployeeWithCashBalance();
		authenticationService.setCurrentUser(buyer.getId());
		for (int i = 0; i <= 10; i++) {
			Work work = newWork(buyer.getId());
			workId = work.getId();
		}

		Calendar tomorrow = Calendar.getInstance();
		tomorrow.add(Calendar.DAY_OF_YEAR, 1);
		Calendar start = DateUtilities.getMidnightRelativeToTimezone(Calendar.getInstance(), Constants.EST_TIME_ZONE);
		Calendar end = DateUtilities.getMidnightRelativeToTimezone(tomorrow, Constants.EST_TIME_ZONE);

		int count = summaryService.countWorkStatusTransitions(WorkStatusType.DRAFT, start, end);
		Assert.assertTrue(count >= 10);
		workService.deleteDraft(buyer.getId(), workId);
		int afterDeletedCount = summaryService.countWorkStatusTransitions(WorkStatusType.DRAFT, start, end);
		Assert.assertTrue(count == afterDeletedCount + 1);
	}

	@Test
	public void findTimeDimension_withCalendarArg() {
		Calendar now = DateUtilities.newCalendar(2013, 5, 5, 5, 5, 0, TimeZone.getTimeZone("UTC"));
		TimeDimension timeDimension = summaryService.findTimeDimension(now);
		Assert.assertEquals(now.get(Calendar.YEAR), timeDimension.getYear());
		Assert.assertEquals(now.get(Calendar.MONTH) + 1, timeDimension.getMonthOfYear());
		Assert.assertEquals(now.get(Calendar.DAY_OF_MONTH), timeDimension.getDayOfMonth());
		Assert.assertEquals(now.get(Calendar.HOUR_OF_DAY), timeDimension.getHourOfDay());

	}
}
