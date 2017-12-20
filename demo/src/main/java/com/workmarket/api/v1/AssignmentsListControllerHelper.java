package com.workmarket.api.v1;

import com.workmarket.api.v1.model.ApiAssignmentListItemDTO;
import com.workmarket.data.solr.model.WorkSearchDataPagination;
import com.workmarket.domains.model.Pagination;
import com.workmarket.domains.model.WorkStatusType;
import com.workmarket.domains.reports.service.WorkReportService;
import com.workmarket.utility.DateUtilities;

import org.apache.commons.lang.ArrayUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class AssignmentsListControllerHelper {
	private static final Logger logger = LoggerFactory.getLogger(AssignmentsListControllerHelper.class);

	@Autowired private WorkReportService workReportService;
	@Autowired private ApiHelper apiHelper;

	ApiV1Pagination<ApiAssignmentListItemDTO> getAssignmentList(String status, long companyId, long userId, int start, int limit, String[] labelIds, Long clientId, String sortDir) {
		WorkSearchDataPagination pagination = getAssignmentListLegacy(status, companyId, userId, start, limit, labelIds, clientId, sortDir);
		return buildResponseMap(pagination, start, limit);
	}

	ApiV1Pagination<ApiAssignmentListItemDTO> getAssignmentListUpdated(String status, long companyId, long userId, int start, int limit, long modifiedSince, String sortDir) {
		WorkSearchDataPagination pagination = getAssignmentListUpdatedLegacy(status, companyId, userId, start, limit, modifiedSince, sortDir);
		return buildResponseMap(pagination, start, limit);
	}

	ApiV1Pagination<ApiAssignmentListItemDTO> buildResponseMap(WorkSearchDataPagination pagination, int start, int limit) {
		List<ApiAssignmentListItemDTO> assignments = apiHelper.buildAssignmentList(pagination.getResults());
		return new ApiV1Pagination<>(
				pagination.getRowCount(), assignments.size(), start, limit, assignments
		);
	}

	private WorkSearchDataPagination getAssignmentListLegacy(String status, long companyId, long userId, int start, int limit, String[] labelIds, Long clientId, String sortDir) {
		// Do the query.
		WorkSearchDataPagination pagination = new WorkSearchDataPagination();
		pagination.setStartRow(start);
		pagination.setResultsLimit(limit);

		// Default sorting.
		pagination.setSortColumn(WorkSearchDataPagination.SORTS.SCHEDULE_FROM);
		pagination.setSortDirection(Pagination.SORT_DIRECTION.valueOf(sortDir.toUpperCase()));

		// Do not filter to the user level. Make sure all assignments for all users are returned.
		pagination.addFilter(WorkSearchDataPagination.FILTER_KEYS.COMPANY_ID, String.valueOf(companyId));
		pagination.setShowAllCompanyAssignments(true);

		// Filter by status
		if (WorkStatusType.AVAILABLE.equals(status)) {
			pagination.addFilter(WorkSearchDataPagination.FILTER_KEYS.WORK_STATUS, "sent");
		} else if (WorkStatusType.SENT_WITH_OPEN_QUESTIONS.equals(status)) {
			pagination.addFilter(WorkSearchDataPagination.FILTER_KEYS.WORK_STATUS, WorkStatusType.SENT_WITH_OPEN_QUESTIONS);
		} else if (WorkStatusType.SENT_WITH_OPEN_NEGOTIATIONS.equals(status)) {
			pagination.addFilter(WorkSearchDataPagination.FILTER_KEYS.WORK_STATUS, WorkStatusType.SENT_WITH_OPEN_NEGOTIATIONS);
		} else if (apiHelper.hasValidStatus(status)) {
			pagination.addFilter(WorkSearchDataPagination.FILTER_KEYS.WORK_STATUS, status);
		}

		if (ArrayUtils.isNotEmpty(labelIds)) {
			for (String labelId : labelIds) {
				pagination.addFilter(WorkSearchDataPagination.FILTER_KEYS.WORK_SUB_STATUS_TYPE_ID, labelId);
			}
		}

		if (clientId != null) {
			pagination.addFilter(WorkSearchDataPagination.FILTER_KEYS.CLIENT_ID, String.valueOf(clientId));
		}

		// Do the query!!
		if (WorkStatusType.AVAILABLE.equals(status)) {
			return workReportService.generateWorkDashboardReportAvailable(companyId, userId, pagination);
		} else {
			return workReportService.generateWorkDashboardReportBuyer(companyId, userId, pagination);
		}
	}

	private WorkSearchDataPagination getAssignmentListUpdatedLegacy(String status, long companyId, long userId, int start, int limit, long modifiedSince, String sortDir) {
		// Do the query.
		WorkSearchDataPagination pagination = new WorkSearchDataPagination();
		pagination.setStartRow(start);
		pagination.setResultsLimit(limit);

		// Default sorting.
		pagination.setSortColumn(WorkSearchDataPagination.SORTS.LAST_MODIFIED_DATE);
		pagination.setSortDirection(Pagination.SORT_DIRECTION.valueOf(sortDir.toUpperCase()));

		// Do not filter to the user level. Make sure all assignments for all users are returned.
		pagination.addFilter(WorkSearchDataPagination.FILTER_KEYS.COMPANY_ID, String.valueOf(companyId));
		pagination.setShowAllCompanyAssignments(true);

		// Filter by status
		if (WorkStatusType.AVAILABLE.equals(status)) {
			pagination.addFilter(WorkSearchDataPagination.FILTER_KEYS.WORK_STATUS, "sent");
		} else if (WorkStatusType.SENT_WITH_OPEN_QUESTIONS.equals(status)) {
			pagination.addFilter(WorkSearchDataPagination.FILTER_KEYS.WORK_STATUS, WorkStatusType.SENT_WITH_OPEN_QUESTIONS);
		} else if (WorkStatusType.SENT_WITH_OPEN_NEGOTIATIONS.equals(status)) {
			pagination.addFilter(WorkSearchDataPagination.FILTER_KEYS.WORK_STATUS, WorkStatusType.SENT_WITH_OPEN_NEGOTIATIONS);
		} else if (apiHelper.hasValidStatus(status)) {
			pagination.addFilter(WorkSearchDataPagination.FILTER_KEYS.WORK_STATUS, status);
		}

		String modifiedSinceISO8601 = DateUtilities.getISO8601(modifiedSince * 1000L);

		logger.debug("list updated assignments for companyId={} and userId={} using requested parameters: status={}, modifiedSinceISO8601={}, start={} and limit={}",
				companyId, userId, status, modifiedSinceISO8601, start, limit);

		pagination.addFilter(WorkSearchDataPagination.FILTER_KEYS.MODIFIED_DATE_FROM, modifiedSinceISO8601);

		// Do the query!!
		if (WorkStatusType.AVAILABLE.equals(status)) {
			return workReportService.generateWorkDashboardReportAvailable(companyId, userId, pagination);
		} else {
			return workReportService.generateWorkDashboardReportBuyer(companyId, userId, pagination);
		}
	}
}
