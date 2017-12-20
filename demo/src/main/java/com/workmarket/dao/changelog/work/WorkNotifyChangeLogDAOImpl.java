package com.workmarket.dao.changelog.work;

import com.workmarket.dao.AbstractDAO;
import com.workmarket.domains.model.changelog.work.WorkNotifyChangeLog;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;

import java.util.Calendar;

/**
 * Created by ianha on 12/24/14
 */
@Repository
public class WorkNotifyChangeLogDAOImpl extends AbstractDAO<WorkNotifyChangeLog> implements WorkNotifyChangeLogDAO {
	@Override
	public int getLogCountSinceDate(Long workId, Calendar date) {
		int count = ((Long)getFactory().getCurrentSession().createCriteria(WorkNotifyChangeLog.class)
				.add(Restrictions.eq("workId", workId))
				.add(Restrictions.gt("createdOn", date))
				.setProjection(Projections.rowCount())
				.uniqueResult()).intValue();

		return count;
	}

	@Override
	protected Class<?> getEntityClass() {
		return WorkNotifyChangeLog.class;
	}
}
