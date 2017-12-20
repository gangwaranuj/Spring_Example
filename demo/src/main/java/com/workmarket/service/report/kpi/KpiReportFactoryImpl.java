package com.workmarket.service.report.kpi;

import com.google.common.collect.Lists;
import com.workmarket.dao.report.kpi.KpiDAO;
import com.workmarket.domains.model.WorkStatusType;
import com.workmarket.domains.model.kpi.DataPoint;
import com.workmarket.domains.model.kpi.KPIReportType;
import com.workmarket.domains.model.kpi.Filter;
import com.workmarket.domains.model.kpi.KPIAggregateEntityTable;
import com.workmarket.domains.model.kpi.KPIReportError;
import com.workmarket.domains.model.kpi.KPIReportErrorType;
import com.workmarket.domains.model.kpi.KPIReportException;
import com.workmarket.domains.model.kpi.KPIReportFilter;
import com.workmarket.data.report.kpi.KPIRequest;

import com.workmarket.configuration.Constants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class KpiReportFactoryImpl implements KpiReportFactory {

	@Autowired
	private KpiDAO kpiDao;

	@Override
	public List<DataPoint> getKpiReportChartData(KPIReportType kpiReportType, KPIRequest kpiRequest) throws KPIReportException {
		List<Filter> filters = kpiRequest.getFilters();
		KPIReportFilter requiredFilter = validateFilters(kpiReportType, filters);
		if (requiredFilter != null) {
			KPIReportError error = new KPIReportError("Missing filter value", KPIReportErrorType.MISSING_REQUIRED_FILTER);
			throw new KPIReportException("Filter " + requiredFilter + " is required for reportType : " + kpiReportType.toString(), Lists.newArrayList(error));
		}

		switch (kpiReportType) {
			//Sales
			case SALES_THROUGHPUT_FORECAST:
				return kpiDao.forecastThroughput(kpiRequest);
			case SALES_THROUGHPUT_ACTUAL:
				return kpiDao.amountThroughputActual(kpiRequest);
			case SALES_FEES_ACTUAL:
				return kpiDao.amountFeesActual(kpiRequest);
			case SALES_THROUGHPUT_PERCENT_CHANGE:
				return kpiDao.percentThroughputChange(kpiRequest);
			case SALES_AVERAGE_COMMISSION_EARNED:
				return kpiDao.averageFeesEarned(kpiRequest);
			case SALES_AVERAGE_FORECAST_ASSIGNMENT_MONTHLY_VOLUME:
				return kpiDao.forecastMonthlyAssignmentVolume(kpiRequest);
			case SALES_AVERAGE_FORECAST_ASSIGNMENT_VALUE:
				return kpiDao.forecastAverageAssignmentValue(kpiRequest);


			//Funds
			case AVAILABLE_CASH_ON_ACCOUNT:
				return kpiDao.amountAvailableCash(kpiRequest);
			case AVAILABLE_WITHDRAWABLE_CASH_ON_ACCOUNT:
				return kpiDao.amountWithdrawableCash(kpiRequest);

			case ASSIGNMENTS_VALUE_IN_PAID:
				return kpiDao.amountAssignmentsByStatusPaid(kpiRequest);
			case ASSIGNMENTS_VALUE_IN_SENT:
				return kpiDao.amountAssignmentsByStatus(WorkStatusType.SENT, kpiRequest);
			case ASSIGNMENTS_VALUE_APPROVED_FOR_PAYMENT:
				return kpiDao.amountAssignmentsByStatus(WorkStatusType.CLOSED, kpiRequest);
			case ASSIGNMENTS_VALUE_IN_PAYMENT_PENDING:
				return kpiDao.amountAssignmentsByStatus(WorkStatusType.PAYMENT_PENDING, kpiRequest);
			case ASSIGNMENTS_VALUE_IN_PENDING_APPROVAL:
				return kpiDao.amountAssignmentsByStatus(WorkStatusType.COMPLETE, kpiRequest);

			case ASSIGNMENTS_TOTAL_IN_DRAFT:
				return kpiDao.countAssignmentsByStatus(WorkStatusType.DRAFT, kpiRequest);
			case ASSIGNMENTS_TOTAL_IN_SENT:
				return kpiDao.countAssignmentsByStatus(WorkStatusType.SENT, kpiRequest);
			case ASSIGNMENTS_TOTAL_IN_PAID:
				return kpiDao.countAssignmentsByStatusPaid(kpiRequest);
			case ASSIGNMENTS_TOTAL_IN_PENDING_APPROVAL:
				return kpiDao.countAssignmentsByStatus(WorkStatusType.COMPLETE, kpiRequest);

			case ASSIGNMENTS_NOT_ACCEPTED_AFTER_TWO_HOURS:
				return kpiDao.countAssignmentsNotAcceptedIn2Hrs(kpiRequest);
			case ASSIGNMENTS_NOT_ACCEPTED_AFTER_TWO_HOURS_PERCENTAGE:
				return kpiDao.percentageAssignmentsNotAcceptedIn2Hrs(kpiRequest);
			case ASSIGNMENTS_TOTAL_PAID_AFTER_72_HOURS:
				return kpiDao.countAssignmentsCompletedToPaidInMoreThan72Hrs(kpiRequest);
			case ASSIGNMENTS_VALUE_PAID_AFTER_72_HOURS:
				return kpiDao.amountAssignmentsCompletedToPaidInMoreThan72Hrs(kpiRequest);

			//Delayed
			case ASSIGNMENTS_VOIDED:
				return kpiDao.countAssignmentsByStatus(WorkStatusType.VOID, kpiRequest);
			case ASSIGNMENTS_CANCELLED:
				return kpiDao.countAssignmentsByStatus(WorkStatusType.CANCELLED, kpiRequest);
			case ASSIGNMENTS_VALUE_IN_VOIDED:
				return kpiDao.amountAssignmentsByStatus(WorkStatusType.VOID, kpiRequest);
			case ASSIGNMENTS_VALUE_IN_CANCELLED:
				return kpiDao.amountAssignmentsByStatus(WorkStatusType.CANCELLED, kpiRequest);

			//Assignment Lifecycle
			case AVERAGE_HOURS_SENT_TO_ASSIGNED:
				return kpiDao.averageHoursSentToAccepted(kpiRequest);
			case AVERAGE_HOURS_ASSIGNED_TO_COMPLETE:
				return kpiDao.averageHoursAssignedToComplete(kpiRequest);
			case AVERAGE_HOURS_COMPLETE_TO_PAID:
				return kpiDao.averageHoursCompleteToClosed(kpiRequest);
			case AVERAGE_HOURS_SENT_TO_PAID:
				return kpiDao.averageHoursSentoToClosed(kpiRequest);

			//New Assignment Lifecycle
			case AVERAGE_HOURS_ASSIGNMENT_DRAFT_TO_SENT:
				return kpiDao.averageHoursAssignmentsDraftToSent(kpiRequest);
			case AVERAGE_HOURS_ASSIGNMENT_SENT_TO_ACTIVE:
				return kpiDao.averageHoursAssignmentsSentToActive(kpiRequest);
			case AVERAGE_HOURS_ASSIGNMENT_ACTIVE_TO_COMPLETE:
				return kpiDao.averageHoursAssignmentsActiveToComplete(kpiRequest);
			case AVERAGE_HOURS_ASSIGNMENT_COMPLETE_TO_CLOSED:
				return kpiDao.averageHoursAssignmentsCompleteToClosed(kpiRequest);
			case AVERAGE_HOURS_ASSIGNMENT_CLOSED_TO_PAID:
				return kpiDao.averageHoursAssignmentsClosedToPaid(kpiRequest);
			case AVERAGE_HOURS_ASSIGNMENT_SENT_TO_PAID:
				return kpiDao.averageHoursAssignmentsSentToPaid(kpiRequest);
			case AVERAGE_HOURS_ASSIGNMENT_SENT_TO_START:
				return kpiDao.averageHoursAssignmentsSentToStart(kpiRequest);
			case AVERAGE_HOURS_ASSIGNMENT_START_TO_COMPLETE:
				return kpiDao.averageHoursAssignmentsStartToComplete(kpiRequest);

			//Sent by Hour
			case ASSIGNMENTS_SENT_BY_HOUR_OF_DAY:
				return kpiDao.countAssignmentsByHourByStatus(WorkStatusType.SENT, kpiRequest);

			//Groups
			case GROUPS_CREATED:
				return kpiDao.countCreatedGroups(kpiRequest);
			case GROUPS_INVITES_SENT:
				return kpiDao.countSentGroupInvitations(kpiRequest);
			case GROUPS_NEW_MEMBERS:
				return kpiDao.countNewGroupMembers(kpiRequest);
			case GROUPS_PEOPLE_TO_APPROVE:
				return kpiDao.countUsersPendingApprovalToGroup(kpiRequest);
			case GROUPS_TOTAL_MEMBERS:
				return kpiDao.countTotalGroupMembers(kpiRequest);

			//Campaigns
			case CAMPAIGNS_CREATED:
				return kpiDao.countCreatedRecruitingCampaigns(kpiRequest);
			case CAMPAIGN_CLICKS:
				return kpiDao.countRecruitingCampaignClicks(kpiRequest);
			case CAMPAIGN_SIGNUPS:
				return kpiDao.countRecruitingCampaignSignups(kpiRequest);
			case CAMPAIGN_AVERAGE_SIGNUPS:
				return kpiDao.averageRecruitingCampaignSignups(kpiRequest);
			case CAMPAIGN_PENDING_RESOURCES:
				return kpiDao.countRecruitingCampaignUsersPendingApproval(kpiRequest);

			//Tests
			case TESTS_CREATED:
				return kpiDao.countCreatedAssessments(kpiRequest);
			case TESTS_INVITES_SENT:
				return kpiDao.countSentAssessmentsInvitations(kpiRequest);
			case TESTS_PASSED:
				return kpiDao.countPassedAssessments(kpiRequest);
			case TESTS_FAILED:
				return kpiDao.countFailedAssessments(kpiRequest);
			case TESTS_TOTAL:
				return kpiDao.countTotalTakenAssessments(kpiRequest);

			case ASSIGNMENTS_CREATED_BY_API:
				return kpiDao.countApiCreatedAssignments(kpiRequest);

			//Templates
			case TEMPLATES_TOTAL:
				return kpiDao.countCreatedTemplates(kpiRequest);
			case TEMPLATES_USED:
				return kpiDao.countUsedTemplates(kpiRequest);

			//Ratings
			case BUYER_RATINGS_BREAKDOWN:
				return kpiDao.countBuyerRatings(kpiRequest);
			case BUYER_RATINGS_AVERAGE:
				return kpiDao.averageBuyerRating(kpiRequest);
			case RESOURCE_RATINGS_BREAKDOWN:
				return kpiDao.countResourceRatings(kpiRequest);
			case RESOURCE_RATINGS_AVERAGE:
				return kpiDao.averageResourceRating(kpiRequest);

			//New users
			case NEW_SIGNUPS:
				return kpiDao.countNewSignups(kpiRequest);
			case NEW_BUYER_SIGNUPS:
				return kpiDao.countNewBuyerSignups(kpiRequest);
			case ASSIGNMENTS_SENT_BY_NEW_BUYERS:
				return kpiDao.countAssignmentsSentByNewBuyers(kpiRequest);
			case ASSIGNMENTS_SENT_BY_NON_NEW_BUYERS:
				return kpiDao.countAssignmentsSentByNonNewBuyers(kpiRequest);
			case BUYERS_SENDING_FIRST_ASSIGNMENT:
				return kpiDao.countBuyersSendingFirstAssignmentInXDaysAfterSignUp(kpiRequest, Constants.NUMBER_OF_DAYS_TO_CONSIDER_USERS_AS_NEW);
			case PERCENT_OF_BUYERS_SENDING_FIRST_ASSIGNMENT_SUBSCRIPTION:
				return kpiDao.percentageBuyersSendingFirstAssignmentSubscription(kpiRequest);
			case PERCENT_OF_BUYERS_SENDING_FIRST_ASSIGNMENT_TRANSACTIONAL:
				return kpiDao.percentageBuyersSendingFirstAssignmentTransactional(kpiRequest);
			case NEW_SIGNUPS_WITH_ASSIGNMENTS_IN_FIRST_WEEK:
				return kpiDao.countBuyersSendingFirstAssignmentInXDaysAfterSignUp(kpiRequest, 7);
			case NEW_SIGNUPS_AVERAGE_NUMBER_OF_ASSIGNMENTS:
				return kpiDao.averageNumberOfAssignmentsSentByNewBuyers(kpiRequest);

			//Community
			case SOLE_PROPRIETORS:
				return kpiDao.countSolePropietors(kpiRequest);
			case COMPANIES_FIRMS:
				return kpiDao.countCompanies(kpiRequest);
			case USERS_MANAGING_WORK:
				return kpiDao.countManageWorkUsers(kpiRequest);
			case USERS_PERFORMING_WORK:
				return kpiDao.countPerformWorkUsers(kpiRequest);
			case USERS_MANAGING_AND_PERFORMING_WORK:
				return kpiDao.countUsersPerformingAndManagingWork(kpiRequest);
			case COMPANIES_WITH_X_NUMBER_DRAFTS:
				int xNumberOfDrafts = 0;
				for (Filter filter: filters) {
					if (filter.getName().equals(KPIReportFilter.X_NUMBER_DRAFTS)) {
						xNumberOfDrafts = Integer.valueOf(filter.getValues().get(0));
					}
				}
				return kpiDao.countCompaniesWithXNumberDraftsCreated(kpiRequest, xNumberOfDrafts);

			//Resource Activity
			case RESOURCES_RECEIVING_ONE_ASSIGNMENT:
				return kpiDao.countResourcesWithAtLeast1Assignment(kpiRequest);
			case RESOURCES_RECEIVING_ONE_ASSIGNMENT_TRAILING_12_MONTHS:
				return kpiDao.countResourcesWithAtLeast1AssignmentTrailing12Months(kpiRequest);
			case ASSIGNMENTS_AVERAGE_PAID_PER_ACTIVE_RESOURCE:
				return kpiDao.averagePaidAssignmentsPerActiveResource(kpiRequest);

			case BLOCKED_USERS:
				return kpiDao.countBlockedUsers(kpiRequest);

			//User activity
			case USERS_TOTAL:
				return kpiDao.countTotalUsersOnSystem(kpiRequest);
			case BUYERS_SENDING_ONE_ASSIGNMENT:
				return kpiDao.countBuyersWithAtLeast1AssignmentByStatus(WorkStatusType.SENT, kpiRequest);
			case BUYERS_WITH_AT_LEAST_ONE_PAID_ASSIGNMENT:
				return kpiDao.countBuyersWithAtLeast1AssignmentByStatus(WorkStatusType.PAID, kpiRequest);
			case BUYERS_SENDING_ONE_ASSIGNMENT_TRAILING_12_MONTHS:
				return kpiDao.countBuyersWIthAtLeast1AssignmentTrailing12MonthsByStatus(WorkStatusType.SENT, kpiRequest);
			case BUYERS_AVERAGE_PAID_ASSIGNMENTS:
				return kpiDao.averagePaidAssignmentsPerBuyer(kpiRequest);
			case USERS_ACTIVE:
				return kpiDao.countActiveUsers(kpiRequest);

			case ASSIGNMENTS_VALUE_AVERAGE:
				return kpiDao.averagePaidAssignmentsValue(kpiRequest);
			case PERCENTAGE_PAYMENT_WITHIN_TERMS:
				return kpiDao.percentagePaymentWithinTerms(kpiRequest);
			case TOTAL_FUNDING_BY_COMPANY:
				return kpiDao.totalFundingByCompany(kpiRequest);

			default:
				return Lists.newArrayList();
		}
	}

	@Override
	public List<KPIAggregateEntityTable> getKPIAggregateEntityTableData(KPIReportType kpiReportType, KPIRequest kpiRequest) throws KPIReportException {
		List<Filter> filters = kpiRequest.getFilters();
		KPIReportFilter requiredFilter = validateFilters(kpiReportType, filters);
		if (requiredFilter != null) {
			KPIReportError error = new KPIReportError("Missing filter value", KPIReportErrorType.MISSING_REQUIRED_FILTER);
			throw new KPIReportException("Filter " + requiredFilter + " is required for reportType : " + kpiReportType.toString(), Lists.newArrayList(error));
		}

		switch (kpiReportType) {
			case ASSIGNMENTS_TOTAL_IN_DRAFT_INDUSTRY_DATABLE:
				return kpiDao.countAssignmentsByIndustryByStatusDatatable(WorkStatusType.DRAFT, kpiRequest);
			case SALES_THROUGHPUT_ACTUAL_INDUSTRY_DATABLE:
				return kpiDao.throughputAssignmentsByIndustryByStatusDatatable(WorkStatusType.PAID, kpiRequest);
			default:
				return Lists.newArrayList();
		}
	}

	private KPIReportFilter validateFilters(KPIReportType kpiReportType, List<Filter> filters) {
		if (kpiReportType.equals(KPIReportType.BUYER_RATINGS_BREAKDOWN) || kpiReportType.equals(KPIReportType.RESOURCE_RATINGS_BREAKDOWN)) {
			if (!isFilterPresent(filters, KPIReportFilter.RATING_STAR_VALUE)) {
				return KPIReportFilter.RATING_STAR_VALUE;
			}
		} else if (kpiReportType.equals(KPIReportType.COMPANIES_WITH_X_NUMBER_DRAFTS)) {
			if (!isFilterPresent(filters, KPIReportFilter.X_NUMBER_DRAFTS)) {
				return KPIReportFilter.X_NUMBER_DRAFTS;
			}
		} else if (kpiReportType.equals(KPIReportType.PERCENTAGE_PAYMENT_WITHIN_TERMS) || kpiReportType.equals(KPIReportType.TOTAL_FUNDING_BY_COMPANY)) {
			if (!isFilterPresent(filters, KPIReportFilter.COMPANY)) {
				return KPIReportFilter.COMPANY;
			}
		}
		return null;
	}

	public boolean isFilterPresent(List<Filter> filters, KPIReportFilter filter) {
		boolean found = false;
		for (Filter f: filters) {
			if (f.getName().equals(filter)) {
				found = f.isSetValues() && f.getValues().size() > 0;
			}
		}
		return found;
	}
}
