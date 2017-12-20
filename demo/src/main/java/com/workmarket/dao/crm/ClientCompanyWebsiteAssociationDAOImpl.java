package com.workmarket.dao.crm;

import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;

import com.workmarket.dao.AbstractDAO;
import com.workmarket.domains.model.crm.ClientCompanyWebsiteAssociation;

import java.util.List;

@Repository
public class ClientCompanyWebsiteAssociationDAOImpl extends AbstractDAO<ClientCompanyWebsiteAssociation> implements ClientCompanyWebsiteAssociationDAO {
		
	protected Class<ClientCompanyWebsiteAssociation> getEntityClass() {
        return ClientCompanyWebsiteAssociation.class;
    }

	@Override
	public List<ClientCompanyWebsiteAssociation> findAllByClientCompanyId(long clientCompanyId) {
		return (List<ClientCompanyWebsiteAssociation>) getFactory().getCurrentSession().createCriteria(getEntityClass())
				.add(Restrictions.eq("entity.id", clientCompanyId))
				.add(Restrictions.eq("deleted", false))
				.list();
	}
}
