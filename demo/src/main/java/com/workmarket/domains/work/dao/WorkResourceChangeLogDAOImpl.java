package com.workmarket.domains.work.dao;

import com.workmarket.dao.AbstractDAO;
import com.workmarket.domains.work.model.WorkResourceChangeLog;
import org.hibernate.FetchMode;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class WorkResourceChangeLogDAOImpl extends AbstractDAO<WorkResourceChangeLog> implements WorkResourceChangeLogDAO {

	@Override
	protected Class<?> getEntityClass() {
		return WorkResourceChangeLog.class;
	}

	@Override
	public List<WorkResourceChangeLog> findAllWorkResourceChangeLogWithNotesByWorkId(Long workId) {
		return getFactory().getCurrentSession().createCriteria(getEntityClass())
			.setFetchMode("workResource", FetchMode.JOIN)
			.createAlias("workResource", "workResource")
			.createAlias("workResource.work", "work")
			.add(Restrictions.eq("work.id", workId))
			.add(Restrictions.isNotNull("changeLogNote"))
			.list();
	}
}
