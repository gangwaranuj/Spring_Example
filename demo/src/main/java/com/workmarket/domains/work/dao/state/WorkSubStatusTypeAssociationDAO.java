package com.workmarket.domains.work.dao.state;

import com.workmarket.dao.DAOInterface;
import com.workmarket.domains.work.model.state.WorkSubStatusType;
import com.workmarket.domains.work.model.state.WorkSubStatusTypeAssociation;

import java.util.List;

public interface WorkSubStatusTypeAssociationDAO extends DAOInterface<WorkSubStatusTypeAssociation> {

	WorkSubStatusTypeAssociation findByWorkSubStatusAndWorkId(long subStatusId, long workId);
	
	List<WorkSubStatusType> findAllUnResolvedSubStatuses(long workId);

	List<WorkSubStatusTypeAssociation> findByWorkSubStatusId(long subStatusId);
}
