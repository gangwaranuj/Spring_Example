package com.workmarket.dao.report.kpi;

import com.workmarket.data.report.kpi.KPIRequest;
import com.workmarket.utility.sql.SQLBuilder;

import java.util.List;

public interface KpiSqlBuilderFactory {

	SQLBuilder buildAssignmentHistorySummarySQL(String workStatusTypeCode, KPIRequest kpiRequest);

	SQLBuilder buildAssignmentHistorySummaryPaidSQL(KPIRequest kpiRequest);

	SQLBuilder buildAssignmentHistorySummaryMonthlySQL(String workStatusTypeCode, KPIRequest kpiRequest);

	SQLBuilder buildAllAssignmentHistorySummarySQL(String workStatusTypeCode, KPIRequest kpiRequest);

	SQLBuilder buildAssignmentThroughputActualSQL(KPIRequest kpiRequest);

	SQLBuilder buildAssignmentThroughputActualMonthlySQL(KPIRequest kpiRequest);

	SQLBuilder buildCountUserGroupAssociationsByStatusSQL(KPIRequest kpiRequest, String userGroupAssociationStatusCode, boolean recruitingCampaignGroups);

	SQLBuilder buildCountBuyersSendingFirstAssignmentInXDaysAfterSignUpSQL(KPIRequest kpiRequest, Integer numberOfDaysAfterSignup);

	SQLBuilder buildAverageNumberOfAssignmentsByNewBuyersByStatusSQL(String workStatusTypeCode, KPIRequest kpiRequest);

	SQLBuilder buildAverageWorkStatusTransitionHistorySummarySQL(String fromWorkStatusTypeCode, String toWorkStatusTypeCode, KPIRequest kpiRequest);

	SQLBuilder buildAverageWorkMilestonesTransitionSummarySQL(String fromStatusColumn, String toStatusColumn, KPIRequest kpiRequest);

	SQLBuilder buildCountAssignmentsByHourByStatusSQL(String workStatusTypeCode, KPIRequest kpiRequest);

	SQLBuilder buildCountAssignmentsNotAcceptedIn2HrsSQL(KPIRequest kpiRequest);

	SQLBuilder buidPercentageAssignmentsNotAcceptedIn2HrsSQL(KPIRequest kpiRequest);

	SQLBuilder buildCountAssignmentsCompletedToPaidInMoreThan72HrsSQL(KPIRequest kpiRequest);

	SQLBuilder buildAmountAssignmentsCompletedToPaidInMoreThan72HrsSQL(KPIRequest kpiRequest);

	SQLBuilder buildCountCompaniesWithXNumberDraftsCreatedSQL(KPIRequest kpiRequest, Integer X);

	SQLBuilder buildCountBuyersWIthAtLeast1AssignmentTrailing12MonthsByStatusSQL(String workStatusTypeCode, KPIRequest kpiRequest);

	SQLBuilder buildCountUsersSQL(KPIRequest kpiRequest, boolean manageWorkFlag, boolean findWorkFlag);

	SQLBuilder buildPercentageBuyersSendingFirstAssignmentSQL(KPIRequest kpiRequest, String accountPricingType);

	SQLBuilder buildCountAssessmentsUserAssociationsSubQuerySQL(KPIRequest kpiRequest);

	SQLBuilder buildCountBlockedUsersSQL(KPIRequest kpiRequest, boolean deleted);

	SQLBuilder buildCountNewSignupsSQL(KPIRequest kpiRequest);

	SQLBuilder buildCountNewBuyerSignupsSQL(KPIRequest kpiRequest);

	SQLBuilder buildCountAssignmentsByIndustryByStatusDatatableSQL(String workStatusTypeCode, KPIRequest kpiRequest);

	SQLBuilder buildThroughputAssignmentsByIndustryByStatusDatatableSQL(String workStatusTypeCode, KPIRequest kpiRequest);

	SQLBuilder buildSumAvailableCashSQL(KPIRequest kpiRequest);

	SQLBuilder buildSumWithdrawableCashSQL(KPIRequest kpiRequest);

	SQLBuilder buildSumAccountPayableBalanceSQL(KPIRequest kpiRequest);

	SQLBuilder buildAverageBuyerRatingSQL(KPIRequest kpiRequest);

	SQLBuilder buildCountResourceRatingsSQL(KPIRequest kpiRequest);

	SQLBuilder buildAverageResourceRatingSQL(KPIRequest kpiRequest);

	SQLBuilder buildCountBuyerRatingsSQL(KPIRequest kpiRequest);

	SQLBuilder buildCountCreatedRecruitingCampaignsSQL(KPIRequest kpiRequest);

	SQLBuilder buildCountRecruitingCampaignClicksSQL(KPIRequest kpiRequest);

	SQLBuilder buildCountRecruitingCampaignSignupsSQL(KPIRequest kpiRequest);

	SQLBuilder buildAverageRecruitingCampaignSignupsSQL(KPIRequest kpiRequest);

	SQLBuilder buildCountCreatedGroupsSQL(KPIRequest kpiRequest);

	SQLBuilder buildCountSentGroupInvitationsSQL(KPIRequest kpiRequest);

	SQLBuilder buildCountNewGroupMembersSQL(KPIRequest kpiRequest);

	SQLBuilder buildTopUsersByCompanySQL(KPIRequest kpiRequest, Integer limit);

	SQLBuilder buildTopProjectsByCompanySQL(KPIRequest kpiRequest, Integer limit);

	SQLBuilder buildTopResourcesByCompanySQL(KPIRequest kpiRequest, Integer limit);

	SQLBuilder buildSnapshotReportVoidRate(KPIRequest kpiRequest);

	SQLBuilder buildAssignmentLifeCycleOverTimeSQL(KPIRequest kpiRequest);

	SQLBuilder buildAssignmentSegmentationReportSQL(KPIRequest kpiRequest);

	SQLBuilder buildAssignmentSegmentationAssignmentStatusSQL(KPIRequest kpiRequest);

	SQLBuilder buildTopUsersMetadataSQL(KPIRequest kpiRequest, List<Long> userIds);

	SQLBuilder buildPercentagePaymentWithinTermsSQL(KPIRequest kpiRequest);

	SQLBuilder buildTotalFundingByCompanySQL(KPIRequest kpiRequest);
}
