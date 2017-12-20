package com.workmarket.dao.integration.autotask;

import com.google.common.base.Optional;
import com.workmarket.dao.AbstractDAO;
import com.workmarket.domains.model.integration.autotask.AutotaskUser;
import org.hibernate.FetchMode;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;

@Repository
public class AutotaskUserDAOImpl extends AbstractDAO<AutotaskUser> implements AutotaskUserDAO {

	protected Class<AutotaskUser> getEntityClass() {
		return AutotaskUser.class;
	}

	@Override
	public Optional<AutotaskUser> findUserByUserName(String username) {
		return Optional.fromNullable((AutotaskUser)
				getFactory().getCurrentSession().createCriteria(getEntityClass())
						.add(Restrictions.eq("userName", username))
						.uniqueResult());
	}

	@Override
	public Optional<AutotaskUser> findUserByUserId(Long userId) {
		return Optional.fromNullable((AutotaskUser)
				getFactory().getCurrentSession().createCriteria(getEntityClass())
						.setFetchMode("user", FetchMode.JOIN)
						.add(Restrictions.eq("user.id", userId))
						.uniqueResult());
	}

	@Override
	public AutotaskUser findUserByCompanyId(Long companyId) {
		return (AutotaskUser) getFactory().getCurrentSession().getNamedQuery("autotaskUser.byCompanyId").setParameter("companyId", companyId).uniqueResult();
	}

	@Override
	public void addUser(AutotaskUser autotaskUser) {
		getFactory().getCurrentSession().save(autotaskUser);
	}

	@Override
	public void removeUser(Long userId) {
		AutotaskUser autotaskUser = (AutotaskUser) getFactory().getCurrentSession().load(AutotaskUser.class, userId);
		if (autotaskUser != null) {
			getFactory().getCurrentSession().delete(autotaskUser); // TODO: set deleted = 1 instead
		}
	}
}
