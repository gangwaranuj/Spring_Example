package com.workmarket.domains.groups.dao;

import com.workmarket.dao.DAOInterface;
import com.workmarket.domains.groups.model.WorkGroupAssociation;

import java.util.Set;

public interface WorkGroupAssociationDAO extends DAOInterface<WorkGroupAssociation> {
	WorkGroupAssociation findByWorkAndGroupDeleted(Long workId, Long groupId);
	Set<WorkGroupAssociation> findAllByWork(Long workId);
}
