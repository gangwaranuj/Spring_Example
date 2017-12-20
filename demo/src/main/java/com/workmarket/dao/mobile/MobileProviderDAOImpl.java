package com.workmarket.dao.mobile;

import com.workmarket.dao.AbstractDAO;
import com.workmarket.domains.model.MobileProvider;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class MobileProviderDAOImpl extends AbstractDAO<MobileProvider> implements MobileProviderDAO {

	protected Class<MobileProvider> getEntityClass() {
		return MobileProvider.class;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<MobileProvider> findAllMobileProviders() {
		return getFactory().getCurrentSession()
			.createQuery("from mobileProvider where deleted=0 order by name asc")
			.list();
	}

	@Override
	public MobileProvider findMobileProviderById(Long mobileProviderId) {
		return (MobileProvider)getFactory().getCurrentSession()
			.createQuery("select wp from mobileProvider wp where id = :mobileProviderId")
			.setParameter("mobileProviderId", mobileProviderId)
			.uniqueResult();
	}
}
