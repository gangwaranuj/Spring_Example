package com.workmarket.domains.reports.service;

import com.google.api.client.util.Lists;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;

import com.workmarket.dao.customfield.WorkCustomFieldDAO;
import com.workmarket.domains.model.customfield.WorkCustomField;
import com.workmarket.domains.reports.dao.ReportDAO;
import com.workmarket.data.report.work.CustomFieldReportFilters;
import com.workmarket.data.report.work.CustomFieldReportRow;
import com.workmarket.data.report.work.DecoratedWorkReportRow;
import com.workmarket.data.report.work.WorkReportPagination;
import com.workmarket.data.solr.model.WorkSearchDataPagination;
import com.workmarket.domains.model.User;
import com.workmarket.domains.model.acl.Permission;
import com.workmarket.domains.reports.dao.WorkReportDAO;
import com.workmarket.domains.reports.dao.WorkReportDecoratorDAO;
import com.workmarket.service.business.UserService;
import com.workmarket.service.business.dto.WorkAggregatesDTO;
import com.workmarket.service.infra.business.AuthenticationService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import static com.google.common.collect.Lists.partition;
import static org.apache.commons.collections.CollectionUtils.isNotEmpty;

@Service
public class WorkReportServiceImpl implements WorkReportService {
		
	@Autowired private AuthenticationService authenticationService;
	@Autowired private UserService userService;
	@Autowired private ReportDAO reportDAO;
	@Autowired private WorkReportDAO workReportDAO;
	@Autowired private WorkCustomFieldDAO workCustomFieldDAO;
	@Autowired private WorkReportDecoratorDAO workReportDecoratorDAO;

	private final static int CUSTOM_FIELDS_BUFFER_SIZE = 200;

	@Override
	public WorkSearchDataPagination generateWorkDashboardReportBuyer(Long companyId, Long userId, WorkSearchDataPagination pagination) {
		Assert.notNull(companyId);
		Assert.notNull(userId);
		Assert.notNull(pagination);
		if (pagination.isShowAllCompanyAssignments()) {
			pagination.setShowAllCompanyAssignments(canViewAllCompanyAssignmentData(userId));
		}
		
		return workReportDAO.generateWorkDashboardReportBuyer(companyId, userId, pagination);
	}

	@Override
	public WorkSearchDataPagination generateWorkDashboardReportBuyerForList(Long companyId, Long userId, WorkSearchDataPagination pagination) {
		Assert.notNull(companyId);
		Assert.notNull(userId);
		Assert.notNull(pagination);
		if (pagination.isShowAllCompanyAssignments()) {
			pagination.setShowAllCompanyAssignments(canViewAllCompanyAssignmentData(userId));
		}

		return workReportDAO.generateWorkDashboardReportBuyerForList(companyId, userId, pagination);
	}

	@Override
	public WorkSearchDataPagination generateWorkDashboardReportAvailable(Long companyId, Long userId, WorkSearchDataPagination pagination) {
		Assert.notNull(companyId);
		Assert.notNull(userId);
		Assert.notNull(pagination);
		if (pagination.isShowAllCompanyAssignments()) {
			pagination.setShowAllCompanyAssignments(canViewAllCompanyAssignmentData(userId));
		}
		return workReportDAO.generateWorkDashboardReportAvailable(companyId, userId, pagination);
	}

	@Override
	public WorkAggregatesDTO generateWorkDashboardStatusAggregate(Long companyId, WorkSearchDataPagination pagination) {
		Assert.notNull(companyId);
		Assert.notNull(pagination);
		pagination.setShowAllCompanyAssignments(true);
		return workReportDAO.generateWorkDashboardStatusAggregateBuyer(companyId, null, pagination);
	}

	@Override
	public WorkReportPagination generateWorkReportBuyer(Long userId, WorkReportPagination pagination, boolean includeCustomFields) {
		Assert.notNull(userId);
		Assert.notNull(pagination);
		pagination.setShowAllCompanyAssignments(canViewAllCompanyAssignmentData(userId));
		if (!pagination.isShowAllCompanyAssignments() && !pagination.hasFilter(WorkReportPagination.FILTER_KEYS.BUYER_ID))
			pagination.addFilter(WorkReportPagination.FILTER_KEYS.BUYER_ID, userId);
		User user = userService.getUser(userId);
		return reportDAO.generateWorkReportBuyer(user.getCompany().getId(), userId, pagination, includeCustomFields);
	}

	@Override
	public WorkReportPagination generateBudgetReportBuyer(Long userId, WorkReportPagination pagination, boolean includeCustomFields) {
		Assert.notNull(userId);
		Assert.notNull(pagination);
		User user = userService.getUser(userId);
		Assert.notNull(user);
		Assert.notNull(user.getCompany());
		pagination.setShowAllCompanyAssignments(canViewAllCompanyAssignmentData(userId));
		return reportDAO.generateBudgetReportBuyer(user.getCompany().getId(), userId, pagination, includeCustomFields);
	}
	
	@Override
	public WorkReportPagination generateEarningsReportResource(Long userId, WorkReportPagination pagination, boolean includeCustomFields) {
		Assert.notNull(userId);
		Assert.notNull(pagination);
		User user = userService.getUser(userId);
		pagination.setShowAllCompanyAssignments(canViewAllCompanyAssignmentData(userId));
		return reportDAO.generateEarningsReportResource(user.getCompany().getId(), userId, pagination, includeCustomFields);
	}


	@Override
	public List<CustomFieldReportRow> findAllWorkCustomFields(Long userId, CustomFieldReportFilters filters) {
		Assert.notNull(userId);
		User user = userService.getUser(userId);
		Assert.notNull(user);

		if (isNotEmpty(filters.getWorkIds())) {
			return findAllWorkCustomFieldsByWorkId(userId, user.getCompany().getId(), filters);
		}

		return workCustomFieldDAO.findAllWorkCustomFields(userId, user.getCompany().getId(), filters);
	}

	List<CustomFieldReportRow> findAllWorkCustomFieldsByWorkId(long userId, long companyId, CustomFieldReportFilters filters) {
		Assert.notNull(filters);
		final List<CustomFieldReportRow> result = Lists.newArrayList();
		List<Long> workIds = Lists.newArrayList(filters.getWorkIds());

		//We are seeing cases where the work ids list has thousands of ids.
		if (isNotEmpty(workIds)) {
			List<List<Long>> subList = partition(workIds, CUSTOM_FIELDS_BUFFER_SIZE);
			for (List<Long> list : subList) {
				filters.setWorkIds(list);
				result.addAll(workCustomFieldDAO.findAllWorkCustomFields(userId, companyId, filters));
			}
			//return the original list
			filters.setWorkIds(workIds);
			return result;
		}
		return Collections.EMPTY_LIST;
	}

	@Override
	public Integer countInprogressAssignmentsWithPaymentTermsByCompany(Long companyId, WorkSearchDataPagination pagination) {
		return workReportDAO.countInprogressAssignmentsWithPaymentTermsByCompany(companyId, pagination);
	}
	
	@Override
	public Integer countInprogressAssignmentsPrefundByCompany(Long companyId, WorkSearchDataPagination pagination) {
		return workReportDAO.countInprogressAssignmentsPrefundByCompany(companyId, pagination);
	}

	@Override
	public boolean canViewAllCompanyAssignmentData(Long userId) {
		Assert.notNull(userId);
		return (authenticationService.authorizeUserByAclPermission(userId, Permission.VIEW_AND_MANAGE_MYCOMPANY_ASSIGNMENTS));
	}

	@Override
	public <T extends DecoratedWorkReportRow> List<T> addCustomFields(Long userId, Long companyId, List<T> rows, CustomFieldReportFilters filters) {
		return workReportDecoratorDAO.addCustomFields(userId, companyId, rows, filters);
	}

	@Override
	public WorkReportPagination findAllWorkByWorkNumber(Long companyId, Long userId, String[] workNumbers, WorkReportPagination pagination) {
		return workReportDAO.findAllWorkByWorkNumber(companyId, userId, workNumbers, pagination);
	}
	
	@Override
	public WorkReportPagination findAllWorkByWorkNumber(Long companyId, Long userId, List<String> workNumbers, WorkReportPagination pagination) {
		if (workNumbers == null) {
			workNumbers = Collections.emptyList();
		}
		return findAllWorkByWorkNumber(companyId, userId, workNumbers.toArray(new String[workNumbers.size()]), pagination);
	}

	/**
	 * Assumes custom field names are unique within a company.
	 *
	 * @param params { work_id: [Long], custom_field: [Long] }
	 * @return
	 * {
	 * 	workIdToCustomFields: {
	 * 		workId: [{
	 * 		  	customFieldId: Long
	 *	 		  customFieldValue: String
	 * 			}
	 * 		]
	 * 	}
	 *
	 * 	customFields: [{
	 * 		customFieldId: Long
	 * 		customFieldName: String
	 * 	}]
	 * }
	 *
   */
	@Override
	public Map<String, Object> getWorkCustomFieldsMapForBuyer(Map<String, List<Long>> params) {
		final List<Long> workIds = params.get("work_id");

		final CustomFieldReportFilters filters = new CustomFieldReportFilters();
		filters.setWorkIds(workIds);
		filters.setWorkCustomFieldIds(params.get("custom_field"));
		filters.setVisibleToBuyer(true);

		final Map<Long, List<Map<String, Object>>> workIdToCustomFieldData = Maps.newHashMap();
		for (final Map.Entry<Long, List<CustomFieldReportRow>> workIdToCustomFieldReportRow : workCustomFieldDAO.getWorkCustomFieldsMap(filters).entrySet()) {
			workIdToCustomFieldData.put(workIdToCustomFieldReportRow.getKey(), Lists.<Map<String, Object>>newArrayList());

			for (final CustomFieldReportRow customFieldReportRow : workIdToCustomFieldReportRow.getValue()) {
				workIdToCustomFieldData.get(workIdToCustomFieldReportRow.getKey()).add(
						ImmutableMap.<String, Object>of(
								"customFieldId", customFieldReportRow.getFieldId(),
								"customFieldValue", customFieldReportRow.getFieldValue()));
			}
		}

		final List<Map<String, Object>> workCustomFields = Lists.newArrayList();
		for (final WorkCustomField workCustomField : workCustomFieldDAO.get(filters.getWorkCustomFieldIds())) {
			workCustomFields.add(ImmutableMap.<String, Object>of(
					"customFieldName", workCustomField.getName(),
					"customFieldId", workCustomField.getId()));
		}

		return ImmutableMap.of(
				"workIdToCustomFields", workIdToCustomFieldData,
				"customFields", workCustomFields);
	}
}
