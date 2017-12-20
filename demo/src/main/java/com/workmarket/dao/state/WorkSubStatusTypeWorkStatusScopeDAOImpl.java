package com.workmarket.dao.state;

import com.workmarket.dao.AbstractDAO;
import com.workmarket.domains.work.model.state.WorkSubStatusTypeWorkStatusScope;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created by nick on 12/20/13 3:55 PM
 */
@Repository
public class WorkSubStatusTypeWorkStatusScopeDAOImpl extends AbstractDAO<WorkSubStatusTypeWorkStatusScope> implements WorkSubStatusTypeWorkStatusScopeDAO {

	@Override
	protected Class<?> getEntityClass() {
		return WorkSubStatusTypeWorkStatusScope.class;
	}

	@Override
	@SuppressWarnings("unchecked")
	public List<WorkSubStatusTypeWorkStatusScope> findAllBySubStatusId(long labelId) {
		return getFactory().getCurrentSession().createCriteria(getEntityClass())
				.add(Restrictions.eq("strong.id", labelId))
				.add(Restrictions.eq("deleted", false))
				.list();
	}
}
