package com.workmarket.dao.customfield;

import com.workmarket.dao.DAOInterface;
import com.workmarket.domains.model.customfield.WorkCustomFieldGroupAssociation;

import java.util.List;
import java.util.Set;

public interface WorkCustomFieldGroupAssociationDAO extends DAOInterface<WorkCustomFieldGroupAssociation> {
	WorkCustomFieldGroupAssociation findByWorkAndWorkCustomFieldGroup(Long workId, Long customFieldGroupId);

	WorkCustomFieldGroupAssociation findByWorkAndWorkCustomFieldGroupPosition(Long workId, Integer position);

	List<WorkCustomFieldGroupAssociation> findAllActiveByWork(Long workId);

	Set<WorkCustomFieldGroupAssociation> findAllByWork(Long workId);
}