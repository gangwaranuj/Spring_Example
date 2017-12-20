package com.workmarket.dao.google;

import com.workmarket.dao.AbstractDAO;
import com.workmarket.domains.model.google.RefreshToken;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;

@Repository
public class RefreshTokenDAOImpl extends AbstractDAO<RefreshToken> implements RefreshTokenDAO {

	@Override
	protected Class<RefreshToken> getEntityClass() {
		return RefreshToken.class;
	}

	@Override
	public RefreshToken findByUserAndProvider(Long userId, String oAuthTokenProviderTypeCode) {
		return (RefreshToken) getFactory().getCurrentSession().createCriteria(RefreshToken.class)
				.add(Restrictions.eq("user.id", userId))
				.add(Restrictions.eq("providerType.code", oAuthTokenProviderTypeCode))
				.uniqueResult();
	}

}
