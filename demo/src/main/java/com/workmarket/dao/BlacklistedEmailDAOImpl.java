package com.workmarket.dao;

import org.springframework.stereotype.Repository;

import com.workmarket.domains.model.BlacklistedEmail;

@Repository
public class BlacklistedEmailDAOImpl extends DeletableAbstractDAO<BlacklistedEmail> implements BlacklistedEmailDAO  {
	protected Class<BlacklistedEmail> getEntityClass() {
		return BlacklistedEmail.class;
	}
	
	public Boolean isBlacklisted(String email) {
		Long count = (Long)getFactory().getCurrentSession().getNamedQuery("blacklistedEmail.count")
			.setString("email", email)
			.uniqueResult();
		return (count > 0);
	}
	
	@Override
	public void deleteFromBlackList(String email) {
		getFactory().getCurrentSession().getNamedQuery("blacklistedEmail.delete")
			.setString("email", email).executeUpdate();
	}
}