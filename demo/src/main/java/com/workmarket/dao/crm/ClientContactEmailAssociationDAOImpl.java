package com.workmarket.dao.crm;

import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;

import com.workmarket.dao.AbstractDAO;
import com.workmarket.domains.model.crm.ClientContactEmailAssociation;

import java.util.List;

@Repository
public class ClientContactEmailAssociationDAOImpl extends AbstractDAO<ClientContactEmailAssociation> implements ClientContactEmailAssociationDAO {
		
	protected Class<ClientContactEmailAssociation> getEntityClass() {
        return ClientContactEmailAssociation.class;
    }

	@Override
	@SuppressWarnings("unchecked")
	public List<ClientContactEmailAssociation> findAllByClientContactId(long clientContactId) {
		return (List<ClientContactEmailAssociation>) getFactory().getCurrentSession().createCriteria(getEntityClass())
				.add(Restrictions.eq("entity.id", clientContactId))
				.add(Restrictions.eq("deleted", false))
				.list();
	}
}
