package com.workmarket.dao.note.concern;

import org.hibernate.Criteria;
import org.hibernate.criterion.Projections;
import org.springframework.stereotype.Repository;

import com.workmarket.dao.AbstractDAO;
import com.workmarket.domains.model.note.concern.Concern;

@Repository
public class ConcernDAOImpl extends AbstractDAO<Concern> implements ConcernDAO {

	protected Class<Concern> getEntityClass() {
        return Concern.class;
    }
	
	@Override
	public Integer countConcerns() {
		Criteria criteria = getFactory().getCurrentSession().createCriteria(getEntityClass())
			.setProjection(Projections.rowCount());
		
		return ((Long) criteria.uniqueResult()).intValue();
	}
}
