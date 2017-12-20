package com.workmarket.dao.requirement;

import com.workmarket.dao.AbstractDAO;
import com.workmarket.domains.model.requirementset.industry.IndustryRequirable;
import org.springframework.stereotype.Repository;

@Repository
public class IndustryRequirableDAOImpl extends AbstractDAO<IndustryRequirable> implements IndustryRequirableDAO {
	@Override
	protected Class<?> getEntityClass() {
		return IndustryRequirable.class;
	}
}
