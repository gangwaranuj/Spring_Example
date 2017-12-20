package com.workmarket.dao.assessment;

import org.springframework.stereotype.Repository;

import com.workmarket.dao.AbstractDAO;
import com.workmarket.domains.model.assessment.AbstractItem;

@Repository
public class AbstractItemDAOImpl extends AbstractDAO<AbstractItem> implements AbstractItemDAO {
	protected Class<AbstractItem> getEntityClass() {
		return AbstractItem.class;
	}
}
