package com.workmarket.dao.requirement;

import com.workmarket.dao.AbstractDAO;
import com.workmarket.domains.model.requirementset.agreement.AgreementRequirement;
import org.springframework.stereotype.Repository;

@Repository
public class AgreementRequirementDAOImpl extends AbstractDAO<AgreementRequirement> implements AgreementRequirementDAO {
	@Override
	protected Class<?> getEntityClass() {
		return AgreementRequirement.class;
	}
}
