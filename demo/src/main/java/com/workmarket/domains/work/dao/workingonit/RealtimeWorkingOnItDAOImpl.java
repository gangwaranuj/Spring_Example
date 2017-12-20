package com.workmarket.domains.work.dao.workingonit;

import com.workmarket.dao.AbstractDAO;
import com.workmarket.domains.work.model.RealtimeWorkingOnIt;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Component;

@Component
public class RealtimeWorkingOnItDAOImpl extends AbstractDAO<RealtimeWorkingOnIt> implements RealtimeWorkingOnItDAO {

	@Override
	protected Class<?> getEntityClass() {
		return RealtimeWorkingOnIt.class;
	}

	@Override
	public RealtimeWorkingOnIt findOpenedByWorkId(Long workId) {
		Object returnVal = getFactory().getCurrentSession().createCriteria(getEntityClass())
				.add(Restrictions.eq("workId", workId))
				.add(Restrictions.eq("isOpen", true)).uniqueResult();
		if (returnVal == null) {
			return null;
		} else { 
			return (RealtimeWorkingOnIt) returnVal;
		}
	}
}
