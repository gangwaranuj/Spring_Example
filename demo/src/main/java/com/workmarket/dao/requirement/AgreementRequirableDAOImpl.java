package com.workmarket.dao.requirement;

import com.workmarket.dao.AbstractDAO;
import com.workmarket.domains.model.requirementset.agreement.AgreementRequirable;
import org.springframework.stereotype.Repository;

@Repository
public class AgreementRequirableDAOImpl extends AbstractDAO<AgreementRequirable> implements AgreementRequirableDAO {
	@Override
	protected Class<?> getEntityClass() {
		return AgreementRequirable.class;
	}
}
