package com.workmarket.dao.customfield;

import com.workmarket.dao.DAOInterface;
import com.workmarket.domains.model.customfield.WorkCustomFieldGroup;

import java.util.List;
import java.util.Map;

public interface WorkCustomFieldGroupDAO extends DAOInterface<WorkCustomFieldGroup> {

	@SuppressWarnings("unchecked") List<WorkCustomFieldGroup> findActiveWorkCustomFieldGroups(Long companyId);

	@SuppressWarnings("unchecked") List<WorkCustomFieldGroup> findInactiveWorkCustomFieldGroups(Long companyId);

	List<WorkCustomFieldGroup> findWorkCustomFieldGroups(Long companyId);

	WorkCustomFieldGroup findRequiredWorkCustomFieldGroup(Long companyId);

	List<WorkCustomFieldGroup> findByWork(Long... workIds);

	Map<Long, String> findClientFieldSetIdsMap(Long companyId);
}
