package com.workmarket.dao.requirement;

import com.workmarket.dao.AbstractDAO;
import com.workmarket.domains.model.requirementset.document.DocumentRequirement;
import org.springframework.stereotype.Repository;

@Repository
public class DocumentRequirementDAOImpl extends AbstractDAO<DocumentRequirement> implements DocumentRequirementDAO {
	@Override
	protected Class<?> getEntityClass() {
		return DocumentRequirement.class;
	}
}
