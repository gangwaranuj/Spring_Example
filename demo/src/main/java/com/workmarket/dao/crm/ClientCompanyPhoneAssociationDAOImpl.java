package com.workmarket.dao.crm;

import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;

import com.workmarket.dao.AbstractDAO;
import com.workmarket.domains.model.crm.ClientCompanyPhoneAssociation;

import java.util.List;

@Repository
public class ClientCompanyPhoneAssociationDAOImpl extends AbstractDAO<ClientCompanyPhoneAssociation> implements ClientCompanyPhoneAssociationDAO {
		
	protected Class<ClientCompanyPhoneAssociation> getEntityClass() {
        return ClientCompanyPhoneAssociation.class;
    }

	@Override
	public List<ClientCompanyPhoneAssociation> findAllByClientCompanyId(long clientCompanyId) {
		return (List<ClientCompanyPhoneAssociation>) getFactory().getCurrentSession().createCriteria(getEntityClass())
				.add(Restrictions.eq("entity.id", clientCompanyId))
				.add(Restrictions.eq("deleted", false))
				.list();
	}
	
}
