package com.workmarket.dao.decisionflow;

import com.workmarket.dao.DAOInterface;
import com.workmarket.domains.model.decisionflow.WorkToDecisionFlowAssociation;
import com.workmarket.domains.work.model.AbstractWork;

public interface WorkToDecisionFlowAssociationDAO extends DAOInterface<WorkToDecisionFlowAssociation> {
	void addDecisionFlowAssociation(AbstractWork work, String UUID);
	WorkToDecisionFlowAssociation findDecisionFlowAssociation(Long workId);

	String findDecisionFlowUuid(Long workId);
}
