package com.workmarket.dao.requirement;

import com.workmarket.dao.AbstractDAO;
import com.workmarket.domains.model.requirementset.test.TestRequirable;
import org.springframework.stereotype.Repository;

@Repository
public class TestRequirableDAOImpl extends AbstractDAO<TestRequirable> implements TestRequirableDAO {
	@Override
	protected Class<?> getEntityClass() {
		return TestRequirable.class;
	}
}
