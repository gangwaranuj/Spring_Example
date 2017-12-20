package com.workmarket.dao.requirement;

import com.workmarket.dao.AbstractDAO;
import com.workmarket.domains.model.requirementset.certification.CertificationRequirement;
import org.springframework.stereotype.Repository;

@Repository
public class CertificationRequirementDAOImpl extends AbstractDAO<CertificationRequirement> implements CertificationRequirementDAO {
	@Override
	protected Class<?> getEntityClass() {
		return CertificationRequirement.class;
	}
}
