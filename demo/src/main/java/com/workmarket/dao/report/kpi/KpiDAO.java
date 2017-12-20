package com.workmarket.dao.report.kpi;

import java.util.List;

import com.workmarket.data.report.internal.AssignmentReport;
import com.workmarket.data.report.internal.SnapshotReport;
import com.workmarket.data.report.internal.TopUser;
import com.workmarket.data.report.internal.TopEntity;
import com.workmarket.data.report.kpi.KPIRequest;
import com.workmarket.domains.model.kpi.DataPoint;
import com.workmarket.domains.model.kpi.KPIAggregateEntityTable;

public interface KpiDAO {

	//Sales 
	List<DataPoint> amountThroughputActual(KPIRequest kpiRequest);
	List<DataPoint> amountFeesActual(KPIRequest kpiRequest);
	List<DataPoint> percentThroughputChange(KPIRequest kpiRequest);
	List<DataPoint> averageFeesEarned(KPIRequest kpiRequest);
	//Forecast
	List<DataPoint> forecastThroughput(KPIRequest kpiRequest);
	List<DataPoint> forecastMonthlyAssignmentVolume(KPIRequest kpiRequest);
	List<DataPoint> forecastAverageAssignmentValue(KPIRequest kpiRequest);

	//Funds 
	List<DataPoint> amountAvailableCash(KPIRequest kpiRequest);
	List<DataPoint> amountWithdrawableCash(KPIRequest kpiRequest);
	List<DataPoint> amountAccountPayableBalance(KPIRequest kpiRequest);

	//Assignment Pipeline
	List<DataPoint> countAssignmentsByStatus(String workStatusTypeCode, KPIRequest kpiRequest);
	List<DataPoint> countAssignmentsByStatusPaid(KPIRequest kpiRequest);
	/**
	 * It will ignore the date filters
	 * @param workStatusTypeCode
	 * @param kpiRequest
	 * @return
	 */
	List<DataPoint> countAllAssignmentsByStatus(String workStatusTypeCode, KPIRequest kpiRequest);
	List<DataPoint> amountAssignmentsByStatus(String workStatusTypeCode, KPIRequest kpiRequest);
	List<DataPoint> amountAssignmentsByStatusPaid(KPIRequest kpiRequest);

	List<TopUser> getTopUsersByCompany(KPIRequest kpiRequest, Integer limit);
	List<TopEntity> getTopProjectsByCompany(KPIRequest kpiRequest, Integer limit);
	List<TopEntity> getTopResourcesByCompany(KPIRequest kpiRequest, Integer limit);
	AssignmentReport getAssignmentSegmentationReportAssignment(KPIRequest kpiRequest);
	AssignmentReport getAssignmentSegmentationReportRouting(KPIRequest kpiRequest);
	SnapshotReport getSnapshotReportVoidRateAndLifeCycle(KPIRequest kpiRequest);

	//Assignment Lifecycle
	List<DataPoint> averageHoursSentToAccepted(KPIRequest kpiRequest);
	List<DataPoint> averageHoursCompleteToClosed(KPIRequest kpiRequest);
	List<DataPoint> averageHoursAssignedToComplete(KPIRequest kpiRequest);
	List<DataPoint> averageHoursSentoToClosed(KPIRequest kpiRequest);

	//New/Efficient Assignment Lifecycle
	List<DataPoint> averageHoursAssignmentsDraftToSent(KPIRequest kpiRequest);
	List<DataPoint> averageHoursAssignmentsSentToActive(KPIRequest kpiRequest);
	List<DataPoint> averageHoursAssignmentsActiveToComplete(KPIRequest kpiRequest);
	List<DataPoint> averageHoursAssignmentsCompleteToClosed(KPIRequest kpiRequest);
	List<DataPoint> averageHoursAssignmentsClosedToPaid(KPIRequest kpiRequest);
	List<DataPoint> averageHoursAssignmentsSentToPaid(KPIRequest kpiRequest);
	List<DataPoint> averageHoursAssignmentsSentToStart(KPIRequest kpiRequest);
	List<DataPoint> averageHoursAssignmentsStartToComplete(KPIRequest kpiRequest);

	//Delayed Approval
	List<DataPoint> countAssignmentsCompletedToPaidInMoreThan72Hrs(KPIRequest kpiRequest);
	List<DataPoint> amountAssignmentsCompletedToPaidInMoreThan72Hrs(KPIRequest kpiRequest);

	//Delayed Acceptance
	List<DataPoint> countAssignmentsNotAcceptedIn2Hrs(KPIRequest kpiRequest);
	List<DataPoint> percentageAssignmentsNotAcceptedIn2Hrs(KPIRequest kpiRequest);

	/**
	 * Answers the question: "How many assignments were created by hour within the selected time frame?"  
	 * @param kpiRequest
	 * @return A {@link java.util.List List} of {@link com.workmarket.domains.model.kpi.DataPoint DataPoint}
	 */
	List<DataPoint> countAssignmentsByHourByStatus(String workStatusTypeCode, KPIRequest kpiRequest);

	//Recruiting Campaigns
	/**
	 * Answers the question: "How many recruiting campaigns were created within the selected time frame?"  
	 * @param kpiRequest
	 * @return A {@link java.util.List List} of {@link com.workmarket.domains.model.kpi.DataPoint DataPoint}
	 */
	List<DataPoint> countCreatedRecruitingCampaigns(KPIRequest kpiRequest);
	List<DataPoint> countRecruitingCampaignClicks(KPIRequest kpiRequest);
	List<DataPoint> countRecruitingCampaignSignups(KPIRequest kpiRequest);
	List<DataPoint> averageRecruitingCampaignSignups(KPIRequest kpiRequest);
	List<DataPoint> countRecruitingCampaignUsersPendingApproval(KPIRequest kpiRequest);

	/**
	 * Answers the question: "How many groups were created within the selected time frame?"  
	 * @param kpiRequest
	 * @return A {@link java.util.List List} of {@link com.workmarket.domains.model.kpi.DataPoint DataPoint}
	 */
	List<DataPoint> countCreatedGroups(KPIRequest kpiRequest);

	/**
	 * Answers the question: "How many group invitations were sent within the selected time frame?"  
	 * @param kpiRequest
	 * @return A {@link java.util.List List} of {@link com.workmarket.domains.model.kpi.DataPoint DataPoint}
	 */
	List<DataPoint> countSentGroupInvitations(KPIRequest kpiRequest);

	/**
	 * Answers the question: "How many new users joined a group?"
	 * @param kpiRequest
	 * @return A {@link java.util.List List} of {@link com.workmarket.domains.model.kpi.DataPoint DataPoint}
	 */
	List<DataPoint> countNewGroupMembers(KPIRequest kpiRequest);

	/**
	 * Answers the question: "How many users were pending approval for a group at any point in time?"  
	 * @param kpiRequest
	 * @return A {@link java.util.List List} of {@link com.workmarket.domains.model.kpi.DataPoint DataPoint}
	 */
	List<DataPoint> countUsersPendingApprovalToGroup(KPIRequest kpiRequest);

	/**
	 * Answers the question: "How many users were members of a group at any point in time?"  
	 * @param kpiRequest
	 * @return A {@link java.util.List List} of {@link com.workmarket.domains.model.kpi.DataPoint DataPoint}
	 */
	List<DataPoint> countTotalGroupMembers(KPIRequest kpiRequest);

	/**
	 * Answers the question: "How many tests were created within the selected time frame?"  
	 * @param kpiRequest
	 * @return A {@link java.util.List List} of {@link com.workmarket.domains.model.kpi.DataPoint DataPoint}
	 */
	List<DataPoint> countCreatedAssessments(KPIRequest kpiRequest);
	List<DataPoint> countSentAssessmentsInvitations(KPIRequest kpiRequest);
	List<DataPoint> countPassedAssessments(KPIRequest kpiRequest);
	List<DataPoint> countFailedAssessments(KPIRequest kpiRequest);
	List<DataPoint> countTotalTakenAssessments(KPIRequest kpiRequest);

	/**
	 * Answers the question: "How many assignments were created within the selected time frame using the API?"  
	 * @param kpiRequest
	 * @return A {@link java.util.List List} of {@link com.workmarket.domains.model.kpi.DataPoint DataPoint}
	 */
	List<DataPoint> countApiCreatedAssignments(KPIRequest kpiRequest);

	/**
	 * Answers the question: "How many templates were created within the selected time frame?"  
	 * @param kpiRequest
	 * @return A {@link java.util.List List} of {@link com.workmarket.domains.model.kpi.DataPoint DataPoint}
	 */
	List<DataPoint> countCreatedTemplates(KPIRequest kpiRequest);
	List<DataPoint> countUsedTemplates(KPIRequest kpiRequest);

	//Ratings 
	List<DataPoint> countBuyerRatings(KPIRequest kpiRequest);
	List<DataPoint> averageBuyerRating(KPIRequest kpiRequest);
	List<DataPoint> countResourceRatings(KPIRequest kpiRequest);
	List<DataPoint> averageResourceRating(KPIRequest kpiRequest);

	//New Accounts Activity
	List<DataPoint> countNewSignups(KPIRequest kpiRequest);
	List<DataPoint> countNewBuyerSignups(KPIRequest kpiRequest);
	List<DataPoint> countAssignmentsSentByNewBuyers(KPIRequest kpiRequest);
	List<DataPoint> countAssignmentsSentByNonNewBuyers(KPIRequest kpiRequest);
	List<DataPoint> percentageBuyersSendingFirstAssignmentSubscription(KPIRequest kpiRequest);
	List<DataPoint> percentageBuyersSendingFirstAssignmentTransactional(KPIRequest kpiRequest);
	List<DataPoint> countBuyersSendingFirstAssignmentInXDaysAfterSignUp(KPIRequest kpiRequest, Integer numberOfDaysAfterSignup);

	/**
	 * Answers the question: "How many sole propietors existed on the system at any point in time?"
	 * @param kpiRequest
	 * @return A {@link java.util.List List} of {@link com.workmarket.domains.model.kpi.DataPoint DataPoint}
	 */
	List<DataPoint> countSolePropietors(KPIRequest kpiRequest);

	/**
	 * Answers the question: "How many companies existed on the system at any point in time?"  
	 * @param kpiRequest
	 * @return A {@link java.util.List List} of {@link com.workmarket.domains.model.kpi.DataPoint DataPoint}
	 */
	List<DataPoint> countCompanies(KPIRequest kpiRequest);

	/**
	 * Answers the question: "How many users registered to manage work existed on the system at any point in time?"  
	 * @param kpiRequest
	 * @return A {@link java.util.List List} of {@link com.workmarket.domains.model.kpi.DataPoint DataPoint}
	 */
	List<DataPoint> countManageWorkUsers(KPIRequest kpiRequest);

	/**
	 * Answers the question: "How many users registered to find work existed on the system at any point in time?"  
	 * @param kpiRequest
	 * @return A {@link java.util.List List} of {@link com.workmarket.domains.model.kpi.DataPoint DataPoint}
	 */
	List<DataPoint> countPerformWorkUsers(KPIRequest kpiRequest);

	/**
	 * Answers the question: "How many users registered to manage and find work existed on the system at any point in time?"  
	 * @param kpiRequest
	 * @return A {@link java.util.List List} of {@link com.workmarket.domains.model.kpi.DataPoint DataPoint}
	 */
	List<DataPoint> countUsersPerformingAndManagingWork(KPIRequest kpiRequest);
	List<DataPoint> countCompaniesWithXNumberDraftsCreated(KPIRequest kpiRequest, Integer X);

	/**
	 * Answers the question: "How many distinct users were invited to an assignment at least once in the selected time frame?"  
	 * @param kpiRequest
	 * @return A {@link java.util.List List} of {@link com.workmarket.domains.model.kpi.DataPoint DataPoint}
	 */
	List<DataPoint> countResourcesWithAtLeast1Assignment(KPIRequest kpiRequest);

	/**
	 * Answers the question: "How many distinct users were invited to an assignment at least once in the 12 months preceding each period.?"  
	 * @param kpiRequest
	 * @return A {@link java.util.List List} of {@link com.workmarket.domains.model.kpi.DataPoint DataPoint}
	 */
	List<DataPoint> countResourcesWithAtLeast1AssignmentTrailing12Months(KPIRequest kpiRequest);

	//Blocked users
	List<DataPoint> countBlockedUsers(KPIRequest kpiRequest);

	//User activity
	/**
	 * Generates a running summary of the number of users that existed on the system at any point of time. 
	 * Searches for the DISTINCT users that where created before the end of the specified time frame (fromTime)
	 * and generates the running summary having as baseline how many users were on the system 
	 * at the beginning of the specified time frame (toTime).
	 *
	 * @param kpiRequest
	 * @return A {@link java.util.List List} of {@link com.workmarket.domains.model.kpi.DataPoint DataPoint}
	 */
	List<DataPoint> countTotalUsersOnSystem(KPIRequest kpiRequest);
	List<DataPoint> countBuyersWithAtLeast1AssignmentByStatus(String workStatusTypeCode, KPIRequest kpiRequest);
	List<DataPoint> countBuyersWIthAtLeast1AssignmentTrailing12MonthsByStatus(String workStatusTypeCode, KPIRequest kpiRequest);
	List<DataPoint> countActiveUsers(KPIRequest kpiRequest);

	List<DataPoint> averagePaidAssignmentsPerBuyer(KPIRequest kpiRequest);
	List<DataPoint> averagePaidAssignmentsPerActiveResource(KPIRequest kpiRequest);

	List<DataPoint> averagePaidAssignmentsValue(KPIRequest kpiRequest);
	List<DataPoint> averageNumberOfAssignmentsSentByNewBuyers(KPIRequest kpiRequest);

	List<KPIAggregateEntityTable> countAssignmentsByIndustryByStatusDatatable(String workStatusTypeCode, KPIRequest kpiRequest);
	List<KPIAggregateEntityTable> throughputAssignmentsByIndustryByStatusDatatable(String workStatusTypeCode, KPIRequest kpiRequest);
	List<KPIAggregateEntityTable> countActiveResourcesByIndustryDatatable(KPIRequest kpiRequest);
	List<KPIAggregateEntityTable> countAssignmentsByHourByStatusDatatable(String workStatusTypeCode, KPIRequest kpiRequest);

	/**
	 * Defined by the formula:
	 * 		number of assignments paid on date <= due on date / number of assignments with a due on date
	 *
	 * The due date should be within the calendar month.
	 * If the paid on date is after the due date and in the next month, it IS NOT counted for the next month.
	 *
	 * @return A {@link java.util.List List} of {@link com.workmarket.domains.model.kpi.DataPoint DataPoint}
	 */
	List<DataPoint> percentagePaymentWithinTerms(KPIRequest kpiRequest);

	/**
	 * Any register_transaction where the register_transaction_type_code = 'addfunds'
	 *
	 * @param kpiRequest
	 * @return A {@link java.util.List List} of {@link com.workmarket.domains.model.kpi.DataPoint DataPoint}
	 */
	List<DataPoint> totalFundingByCompany(KPIRequest kpiRequest);
}
