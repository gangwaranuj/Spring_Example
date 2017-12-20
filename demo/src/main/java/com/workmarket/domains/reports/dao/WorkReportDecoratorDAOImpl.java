package com.workmarket.domains.reports.dao;

import com.workmarket.dao.UserDAO;
import com.workmarket.dao.customfield.WorkCustomFieldDAO;
import com.workmarket.dao.state.WorkSubStatusDAO;
import com.workmarket.data.report.work.CustomFieldReportFilters;
import com.workmarket.data.report.work.CustomFieldReportRow;
import com.workmarket.data.report.work.DecoratedWorkReportRow;
import com.workmarket.data.report.work.WorkSubStatusTypeReportRow;
import com.workmarket.domains.model.User;
import com.workmarket.domains.model.filter.WorkSubStatusTypeFilter;
import com.workmarket.utility.CollectionUtilities;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class WorkReportDecoratorDAOImpl implements WorkReportDecoratorDAO {

	@Autowired private WorkCustomFieldDAO workCustomFieldDAO;
	@Autowired private WorkSubStatusDAO workSubStatusDAO;
	@Autowired private UserDAO userDAO;
	
	@Override
	public <T extends DecoratedWorkReportRow> List<T> addCustomFields(Long userId, List<T> rows, CustomFieldReportFilters filters) {
		User user = userDAO.getUser(userId);
		@SuppressWarnings("unchecked")
		List<Long> workIds = CollectionUtilities.newListPropertyProjection(rows, "workId");
		filters.setWorkIds(workIds);
		Map<Long, List<CustomFieldReportRow>> customFieldsMap = workCustomFieldDAO.getWorkCustomFieldsMap(userId, user.getCompany().getId(), filters);
		
		for (DecoratedWorkReportRow row : rows) {
			if (customFieldsMap.containsKey(row.getWorkId())) {
				row.setCustomFields(customFieldsMap.get(row.getWorkId()));
			}
		}
		return rows;
	}
	
	@Override
	public <T extends DecoratedWorkReportRow> List<T> addCustomFields(Long userId, Long companyId, List<T> rows, CustomFieldReportFilters filters) {
		@SuppressWarnings("unchecked")
		List<Long> workIds = CollectionUtilities.newListPropertyProjection(rows, "workId");
		filters.setWorkIds(workIds);
		Map<Long, List<CustomFieldReportRow>> customFieldsMap = workCustomFieldDAO.getWorkCustomFieldsMap(userId, companyId, filters);
		
		for (DecoratedWorkReportRow row : rows) {
			if (customFieldsMap.containsKey(row.getWorkId())) {
				row.setCustomFields(customFieldsMap.get(row.getWorkId()));
			}
		}
		return rows;
	}
	
	@Override
	public <T extends DecoratedWorkReportRow> List<T> addWorkSubStatus(List<T> rows, WorkSubStatusTypeFilter workSubStatusTypeFilter) {
		@SuppressWarnings("unchecked")
		List<Long> workIds = CollectionUtilities.newListPropertyProjection(rows, "workId");
		
		Map<Long, List<WorkSubStatusTypeReportRow>> workSubstatusMap = workSubStatusDAO.getUnresolvedWorkSubStatusTypeWorkMap(workSubStatusTypeFilter, workIds);
				
		for (DecoratedWorkReportRow row : rows) {
			if (workSubstatusMap.containsKey(row.getWorkId())) {
				row.setWorkSubStatusTypes(workSubstatusMap.get(row.getWorkId()));
			}
		}
		return rows;
	}
}
