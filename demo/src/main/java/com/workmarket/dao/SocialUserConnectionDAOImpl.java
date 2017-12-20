package com.workmarket.dao;

import com.workmarket.domains.model.SocialUserConnection;
import org.hibernate.FetchMode;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.springframework.social.connect.ConnectionKey;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;

/**
 * User: micah
 * Date: 3/17/13
 * Time: 12:17 PM
 */
@Repository
public class SocialUserConnectionDAOImpl implements SocialUserConnectionDAO {
	@Resource(name = "sessionFactory") private SessionFactory factory;

	@Override
	public SocialUserConnection findBySocialKey(ConnectionKey key) {
		return
			(SocialUserConnection)factory.getCurrentSession().
			createCriteria(SocialUserConnection.class).
			setFetchMode("user", FetchMode.JOIN).
			add(Restrictions.eq("id.providerId", key.getProviderId())).
			add(Restrictions.eq("id.providerUserId", key.getProviderUserId())).
			uniqueResult();
	}
}
