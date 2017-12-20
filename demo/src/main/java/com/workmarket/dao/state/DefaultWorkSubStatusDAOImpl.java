package com.workmarket.dao.state;

import com.workmarket.dao.AbstractDAO;
import com.workmarket.domains.model.DefaultWorkSubStatusType;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Criteria;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class DefaultWorkSubStatusDAOImpl extends AbstractDAO<DefaultWorkSubStatusType> implements DefaultWorkSubStatusDAO {

	private static final Log logger = LogFactory.getLog(DefaultWorkSubStatusDAOImpl.class);

	@Override
	protected Class<DefaultWorkSubStatusType> getEntityClass() {
		return DefaultWorkSubStatusType.class;
	}

	@Override
	public List<DefaultWorkSubStatusType> findAll() {
		Criteria criteria = getFactory().getCurrentSession().createCriteria(getEntityClass());
		return criteria.list();
	}
}