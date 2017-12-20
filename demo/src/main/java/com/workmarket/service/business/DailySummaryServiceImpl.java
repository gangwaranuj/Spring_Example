package com.workmarket.service.business;

import com.google.common.collect.Lists;
import com.workmarket.dao.company.CompanyDAO;
import com.workmarket.dao.account.AccountingSummaryDAO;
import com.workmarket.dao.report.internal.DailySummaryDAO;
import com.workmarket.dao.summary.work.WorkStatusTransitionDAO;
import com.workmarket.data.report.internal.BuyerSummary;
import com.workmarket.domains.model.User;
import com.workmarket.domains.model.WorkStatusType;
import com.workmarket.domains.model.reporting.DailySummary;
import com.workmarket.domains.model.reporting.DailySummaryPagination;
import com.workmarket.configuration.Constants;
import com.workmarket.service.infra.business.AuthenticationService;
import com.workmarket.utility.DateUtilities;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.util.Calendar;
import java.util.List;

import static com.workmarket.utility.NumberUtilities.nullSafeAbs;

@Service
public class DailySummaryServiceImpl implements DailySummaryService {

	@Autowired DailySummaryDAO dailySummaryDAO;
	@Autowired AccountingSummaryDAO accountingSummaryDAO;
	@Autowired CompanyDAO companyDAO;
	@Autowired AuthenticationService authenticationService;
	@Autowired UserService userService;
	@Autowired WorkStatusTransitionDAO workStatusTransitionDAO;

	@Override
	public DailySummary createNewSummary() {
		Calendar yesterday = Calendar.getInstance();
		yesterday.add(Calendar.DAY_OF_YEAR, -1);

		Calendar start = DateUtilities.getMidnightRelativeToTimezone(yesterday, Constants.EST_TIME_ZONE);
		Calendar end = DateUtilities.getMidnightTodayRelativeToTimezone(Constants.EST_TIME_ZONE);

		return createNewSummary(start, end);
	}

	private DailySummary createNewSummary(Calendar start, Calendar end) {

		User currentUser = userService.getUser(Constants.WORKMARKET_SYSTEM_USER_ID);
		authenticationService.setCurrentUser(currentUser);

		DailySummary summary = new DailySummary();

		summary.setAssignmentsOnTerms(0);
		summary.setUniqueBuyers(0);

		//Assignment counts
		summary.setAssignments(workStatusTransitionDAO.countWorkStatusTransitions(WorkStatusType.DRAFT, start, end));
		summary.setRouted(workStatusTransitionDAO.countWorkStatusTransitions(WorkStatusType.SENT, start, end));
		summary.setCompleted(workStatusTransitionDAO.countWorkStatusTransitions(WorkStatusType.COMPLETE, start, end));
		summary.setVoidAssignments(workStatusTransitionDAO.countWorkStatusTransitions(WorkStatusType.VOID, start, end));
		summary.setCancelledAssignments(workStatusTransitionDAO.countWorkStatusTransitions(WorkStatusType.CANCELLED, start, end));
		summary.setPaidAssignments(workStatusTransitionDAO.countWorkStatusTransitions(WorkStatusType.PAID, start, end));

		Integer closedAssignments = workStatusTransitionDAO.countWorkStatusTransitions(WorkStatusType.CLOSED, start, end);
		Integer paymentPendingAssignments = workStatusTransitionDAO.countWorkStatusTransitions(WorkStatusType.PAYMENT_PENDING, start, end);
		summary.setClosedAssignments(closedAssignments + paymentPendingAssignments);

		//Unique creators-routers
		summary.setUniqueCreators(workStatusTransitionDAO.countUniqueCompaniesWithWorkStatusTransitions(WorkStatusType.DRAFT, start, end));
		summary.setUniqueRouters(workStatusTransitionDAO.countUniqueCompaniesWithWorkStatusTransitions(WorkStatusType.SENT, start, end));

		//Potential revenue
		summary.setDraftsCreated(workStatusTransitionDAO.calculatePotentialRevenueByWorkStatusType(WorkStatusType.DRAFT, start, end));
		summary.setTotalRoutedToday(workStatusTransitionDAO.calculatePotentialRevenueByWorkStatusType(WorkStatusType.SENT, start, end));

		//Average price
		summary.setAveragePriceCreatedAssignments(workStatusTransitionDAO.calculateAveragePriceByWorkStatusType(WorkStatusType.DRAFT, start, end));

		//Other data
		summary.setNewUsers(dailySummaryDAO.countNewUsers(start, end));
		summary.setDrugTests(dailySummaryDAO.countDrugTests(start, end));
		summary.setBackgroundChecks(dailySummaryDAO.countBackgroundChecks(start, end));
		summary.setPublicGroups(dailySummaryDAO.countPublicGroups());
		summary.setPrivateGroups(dailySummaryDAO.countPrivateGroups());
		summary.setInviteOnlyGroups(dailySummaryDAO.countInviteOnlyGroups());
		summary.setInvitations(dailySummaryDAO.countInvitations(start, end));
		summary.setCampaigns(dailySummaryDAO.countCampaigns(start, end));
		summary.setNewBuyers(dailySummaryDAO.countNewBuyers(start, end));

		Calendar startOfEpoch = DateUtilities.getCalendarFromMillis(0L);

		//Money stuff
		summary.setUnspentAp(accountingSummaryDAO.calculateTotalApAvailable());
		summary.setTotalAssignmentCost(nullSafeAbs(accountingSummaryDAO.calculateTotalCompletedAssignments(start, end)));
		summary.setTotalFees(nullSafeAbs(accountingSummaryDAO.calculateMoneyOutFees(start, end)));
		summary.setCashOnPlatform(accountingSummaryDAO.calculateTotalMoneyOnSystem(startOfEpoch, end));
		summary.setTotalMoneyExposedOnTerms(accountingSummaryDAO.calculateTotalApExposure());

		//Terms
		summary.setTermsExpired(nullSafeAbs(dailySummaryDAO.calculateTermsExpired()));
		summary.setTermsOverdue(nullSafeAbs(dailySummaryDAO.calculateTermsOverdue()));

		dailySummaryDAO.saveOrUpdate(summary);
		return summary;
	}

	@Override
	public DailySummary findSummary(Long id) {
		return dailySummaryDAO.get(id);
	}

	@Override
	public List<DailySummary> findAllSummaries() {
		return dailySummaryDAO.findAllSummaries();
	}

	@Override
	public List<BuyerSummary> findUniqueBuyersForSummary(Long id) {
		DailySummary dailySummary = findSummary(id);
		if (dailySummary != null) {
			Calendar start = (Calendar) dailySummary.getCreatedOn().clone();
			start.add(Calendar.DAY_OF_YEAR, -1);
			Calendar end = DateUtilities.getMidnightRelativeToTimezone(dailySummary.getCreatedOn(), Constants.EST_TIME_ZONE);
			return workStatusTransitionDAO.findUniqueBuyersSummary(DateUtilities.getMidnightRelativeToTimezone(start, Constants.EST_TIME_ZONE), end);
		}
		return Lists.newArrayList();
	}

	@Override
	public List<Object[]> findRoutedAssignmentsPerCompany(Long id) {
		DailySummary dailySummary = findSummary(id);
		if (dailySummary != null) {
			Calendar start = (Calendar) dailySummary.getCreatedOn().clone();
			start.add(Calendar.DAY_OF_YEAR, -1);
			Calendar end = DateUtilities.getMidnightRelativeToTimezone(dailySummary.getCreatedOn(), Constants.EST_TIME_ZONE);
			return workStatusTransitionDAO.findRoutedAssignmentsPerCompany(DateUtilities.getMidnightRelativeToTimezone(start, Constants.EST_TIME_ZONE), end);
		}
		return Lists.newArrayList();

	}

	@Override
	public DailySummaryPagination findAllSummaries(DailySummaryPagination pagination) {
		Assert.notNull(pagination);
		return dailySummaryDAO.findAllSummaries(pagination);
	}
}
