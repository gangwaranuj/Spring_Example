package com.workmarket.dao.decisionflow;

import com.workmarket.dao.DAOInterface;
import com.workmarket.domains.model.Company;
import com.workmarket.domains.model.decisionflow.CompanyToDecisionFlowTemplateAssociation;

import java.util.List;

public interface CompanyToDecisionFlowTemplateAssociationDAO extends DAOInterface<CompanyToDecisionFlowTemplateAssociation> {
	void updateDecisionFlowTemplateAssociation(Company company, String oldUuid, String uuid);

	void addDecisionFlowTemplateAssociation(Company company, String UUID);
	List<CompanyToDecisionFlowTemplateAssociation> findDecisionFlowTemplateAssociations(Long companyId);
	List<String> findDecisionFlowTemplateUuids(Long companyId);

	CompanyToDecisionFlowTemplateAssociation findDecisionFlowTemplateAssociation(Long companyId, String uuid);
}
