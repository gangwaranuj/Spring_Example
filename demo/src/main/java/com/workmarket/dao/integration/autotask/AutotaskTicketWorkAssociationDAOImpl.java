package com.workmarket.dao.integration.autotask;

import com.google.common.base.Optional;
import com.workmarket.dao.AbstractDAO;
import com.workmarket.domains.model.integration.autotask.AutotaskTicketWorkAssociation;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;

@Repository
public class AutotaskTicketWorkAssociationDAOImpl extends AbstractDAO<AutotaskTicketWorkAssociation> implements AutotaskTicketWorkAssociationDAO {

	@Override
	protected Class<?> getEntityClass() {
		return AutotaskTicketWorkAssociation.class;
	}


	@Override
	public Optional<AutotaskTicketWorkAssociation> findAutotaskTicketWorkAssociationByWorkId(Long workId) {
		return Optional.fromNullable((AutotaskTicketWorkAssociation)
				getFactory().getCurrentSession().createCriteria(getEntityClass())
						.add(Restrictions.eq("workId", workId))
						.uniqueResult());
	}

	@Override
	public Optional<AutotaskTicketWorkAssociation> findAutotaskTicketWorkAssociationByTicketIdAndWorkId(Long ticketId, Long workId){
		return Optional.fromNullable((AutotaskTicketWorkAssociation)
				getFactory().getCurrentSession().createCriteria(getEntityClass())
						.add(Restrictions.eq("ticket.ticketId", ticketId))
						.add(Restrictions.eq("workId", workId))
						.uniqueResult());
	}
}
