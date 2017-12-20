package com.workmarket.dao.customfield;

import com.workmarket.dao.DAOInterface;
import com.workmarket.domains.model.customfield.WorkCustomField;
import com.workmarket.data.report.work.CustomFieldReportFilters;
import com.workmarket.data.report.work.CustomFieldReportRow;
import com.workmarket.domains.reports.model.CustomReportCustomFieldGroupDTO;

import java.util.List;
import java.util.Map;

public interface WorkCustomFieldDAO extends DAOInterface<WorkCustomField> {
	List<WorkCustomField> findRequiredBuyerFieldsForCustomFieldGroup(Long customFieldGroupId);

	List<WorkCustomField> findAllFieldsForCustomFieldGroup(Long customFieldGroupId);

	Map<Long, List<CustomFieldReportRow>> getWorkCustomFieldsMap(Long userId, Long companyId, CustomFieldReportFilters filters);

	Map<Long, List<CustomFieldReportRow>> getWorkCustomFieldsMap(CustomFieldReportFilters filters);

	List<CustomFieldReportRow> findAllWorkCustomFields(Long userId, Long companyId, CustomFieldReportFilters filters);

	List<CustomReportCustomFieldGroupDTO> findCustomReportCustomFieldGroupsForCompanyAndReport(Long companyId, Long reportId);
}