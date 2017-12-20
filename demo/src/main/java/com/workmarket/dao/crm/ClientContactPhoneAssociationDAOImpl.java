package com.workmarket.dao.crm;

import org.hibernate.Query;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;

import com.workmarket.dao.AbstractDAO;
import com.workmarket.domains.model.crm.ClientContactPhoneAssociation;

import java.util.List;

@Repository
public class ClientContactPhoneAssociationDAOImpl extends AbstractDAO<ClientContactPhoneAssociation> implements ClientContactPhoneAssociationDAO {
		
	protected Class<ClientContactPhoneAssociation> getEntityClass() {
        return ClientContactPhoneAssociation.class;
    }

	@Override
	public ClientContactPhoneAssociation findByClientContactIdAndPhoneId(long clientContactId, long phoneId)  {
		Query query = getFactory().getCurrentSession().createQuery(" from clientContactPhoneAssociation c where c.entity.id = :clientContactId and c.phone.id = :phoneId");
		
		query.setParameter("clientContactId", clientContactId)
			.setParameter("phoneId", phoneId)
			.setMaxResults(1);
	
		return (ClientContactPhoneAssociation) query.uniqueResult();
	}

	@Override
	@SuppressWarnings("unchecked")
	public List<ClientContactPhoneAssociation> findByClientContactId(long clientContactId) {
		return (List<ClientContactPhoneAssociation>) getFactory().getCurrentSession().createCriteria(getEntityClass())
				.add(Restrictions.eq("entity.id", clientContactId))
				.add(Restrictions.eq("deleted", false))
				.list();

	}
}
