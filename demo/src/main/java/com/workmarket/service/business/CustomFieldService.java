package com.workmarket.service.business;

import com.google.common.collect.ImmutableList;
import com.workmarket.domains.model.User;
import com.workmarket.domains.model.customfield.WorkCustomField;
import com.workmarket.domains.model.customfield.WorkCustomFieldGroup;
import com.workmarket.domains.model.customfield.WorkCustomFieldGroupAssociation;
import com.workmarket.domains.reports.model.CustomReportCustomFieldGroupDTO;
import com.workmarket.domains.work.model.AbstractWork;
import com.workmarket.service.business.dto.WorkCustomFieldDTO;
import com.workmarket.service.business.dto.WorkCustomFieldGroupDTO;

import java.util.List;
import java.util.Map;
import java.util.Set;

public interface CustomFieldService {

	// Custom Fields
	List<WorkCustomFieldGroup> findActiveWorkCustomFieldGroups(Long companyId);

	List<WorkCustomFieldGroup> findInactiveWorkCustomFieldGroups(Long companyId);

	List<WorkCustomFieldGroup> findWorkCustomFieldGroups(Long companyId);

	WorkCustomFieldGroup findWorkCustomFieldGroup(Long fieldGroupId);

	WorkCustomFieldGroup findWorkCustomFieldGroup(Long workId, Long companyId,Integer position);

	WorkCustomFieldGroup findWorkCustomFieldGroupByCompany(Long fieldGroupId, Long companyId);

	WorkCustomFieldGroup saveOrUpdateWorkFieldGroup(Long userId, WorkCustomFieldGroupDTO workFieldGroupDTO) throws Exception;

	void requireWorkCustomFieldGroup(Long fieldGroupId, Long companyId, boolean isRequired) throws Exception;

	WorkCustomFieldGroup findRequiredWorkCustomFieldGroup(Long companyId);

	List<WorkCustomField> findAllFieldsForCustomFieldGroup(Long customFieldGroupId);

	void saveOrUpdateWorkCustomField(WorkCustomField field);

	void deleteWorkCustomFieldGroupByCompany(Long fieldGroupId, Long companyId) throws Exception;

	void deactivateWorkCustomFieldGroupByCompany(Long fieldGroupId, Long companyId) throws Exception;

	void activateWorkCustomFieldGroupByCompany(Long fieldGroupId, Long companyId) throws Exception;

	WorkCustomFieldGroup copyCustomFieldGroupByCompany(Long fieldGroupId, String newName, Long companyId);

	void addWorkCustomFieldGroupToWork(long customFieldGroupId, long workId, int position);

	void removeWorkCustomFieldGroupForWork(Long customFieldGroupId, Long workId);

	void saveWorkCustomFieldForWork(WorkCustomFieldDTO customFieldDTO, Long workId);

	void saveWorkCustomFieldsForWorkAndIndex(WorkCustomFieldDTO[] customFieldDTOs, Long workId);

	void deleteWorkCustomFieldGroupsFromWork(Long workId);

	void saveWorkCustomFieldsForWork(
		WorkCustomFieldDTO[] customFieldDTOs,
		Long workId,
		User user,
		Long onBehalfOfUserId);

	void deleteWorkCustomFieldGroupFromWork(Long workId, Long customFieldGroupId);

	void setWorkCustomFieldGroupsForWork(Long workId, Map<Long, Integer> workCustomFieldGroupsId);

	void saveFlatWorkCustomFields(AbstractWork work, List<WorkCustomFieldGroup> groups, List<List<WorkCustomFieldDTO>> dtoSet);

	List<WorkCustomField> findRequiredBuyerFieldsForCustomFieldGroup(Long customFieldGroupId);

	Set<WorkCustomFieldGroupAssociation> findAllByWork(long workId);

	Map<Long, String> findClientFieldSetIdsMap(Long companyId);

	List<WorkCustomField> findWorkCustomFieldByIds(List<Long> workCustomFieldIds);

	List<CustomReportCustomFieldGroupDTO> findCustomReportCustomFieldGroupsForCompanyAndReport(Long companyId, Long reportId);

	void replaceCustomFieldGroupForWorkByPosition(Long workId,Long customFieldGroupId,List<WorkCustomFieldDTO> customFieldDTOs,  Integer position);

	void replaceCustomFieldGroupForWork(long workId, long customFieldGroupId, List<WorkCustomFieldDTO> customFieldDTOs);

	ImmutableList<Map> getProjectedCustomFieldGroups(String[] fields) throws Exception;

	ImmutableList<Map> getProjectedCustomFields(long customFieldGroupId, String[] fields) throws Exception;
}
