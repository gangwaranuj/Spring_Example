package com.workmarket.dao.requirement;

import com.workmarket.dao.AbstractDAO;
import com.workmarket.domains.model.requirementset.certification.CertificationRequirable;
import org.springframework.stereotype.Repository;

@Repository
public class CertificationRequirableDAOImpl extends AbstractDAO<CertificationRequirable> implements CertificationRequirableDAO {
	@Override
	protected Class<?> getEntityClass() {
		return CertificationRequirable.class;
	}
}
