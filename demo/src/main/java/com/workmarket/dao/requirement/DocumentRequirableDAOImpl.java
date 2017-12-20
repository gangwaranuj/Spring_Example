package com.workmarket.dao.requirement;

import com.workmarket.dao.AbstractDAO;
import com.workmarket.domains.model.requirementset.document.DocumentRequirable;
import org.springframework.stereotype.Repository;

@Repository
public class DocumentRequirableDAOImpl extends AbstractDAO<DocumentRequirable> implements DocumentRequirableDAO {
	@Override
	protected Class<?> getEntityClass() {
		return DocumentRequirable.class;
	}
}
