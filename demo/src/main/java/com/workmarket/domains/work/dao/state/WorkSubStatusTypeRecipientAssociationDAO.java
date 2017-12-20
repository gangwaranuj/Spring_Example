package com.workmarket.domains.work.dao.state;

import com.workmarket.dao.DAOInterface;
import com.workmarket.domains.work.model.state.WorkSubStatusTypeRecipientAssociation;

import java.util.List;

public interface WorkSubStatusTypeRecipientAssociationDAO extends DAOInterface<WorkSubStatusTypeRecipientAssociation> {

	List<Long> findRecipientsByWorkSubStatusId(Long workSubStatusId);

	List<Long> findRecipientsByWorkSubStatusCodeAndCompanyId(String workSubStatusCode, Long companyId);

	List<WorkSubStatusTypeRecipientAssociation> findAssociationsByWorkSubStatusId(Long workSubStatusId);

	WorkSubStatusTypeRecipientAssociation findUniqueAssociationByUserIdAndWorkSubStatusId(Long userId, Long workSubStatusId);
}
