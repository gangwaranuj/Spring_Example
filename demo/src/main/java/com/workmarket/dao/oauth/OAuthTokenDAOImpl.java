package com.workmarket.dao.oauth;

import org.hibernate.Hibernate;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;

import com.workmarket.dao.AbstractDAO;
import com.workmarket.domains.model.oauth.OAuthToken;

import java.util.List;

@Repository
public class OAuthTokenDAOImpl extends AbstractDAO<OAuthToken> implements OAuthTokenDAO  {
	protected Class<OAuthToken> getEntityClass() {
		return OAuthToken.class;
	}
	
	@Override
	public OAuthToken findByAccessToken(String accessToken) {
		return (OAuthToken)getFactory().getCurrentSession().createCriteria(getEntityClass())
			.add(Restrictions.eq("accessToken", accessToken))
			.addOrder(Order.desc("id"))
			.setMaxResults(1)
			.uniqueResult();
	}

	@Override
	public OAuthToken findByUserAndProvider(Long userId, String providerTypeCode)  {
		List oAuthTokens = getFactory().getCurrentSession().getNamedQuery("oauthToken.byUserAndProvider")
			.setLong("user_id", userId)
			.setString("provider_type_code", providerTypeCode)
			.list();
		return (oAuthTokens.size() > 0)?(OAuthToken)oAuthTokens.get(0):null;
	}
	
	@Override
	public OAuthToken findBySessionIdAndProvider(String sessionId, String providerTypeCode)  {
		OAuthToken token = (OAuthToken)getFactory().getCurrentSession().getNamedQuery("oauthToken.bySessionIdAndProvider")
			.setString("session_id", sessionId)
			.setString("provider_type_code", providerTypeCode)
			.setMaxResults(1)
			.uniqueResult();

		if (token != null) {
			Hibernate.initialize(token.getUser());
		}


		return token;
	}
}