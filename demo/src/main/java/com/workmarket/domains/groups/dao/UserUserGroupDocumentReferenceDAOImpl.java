package com.workmarket.domains.groups.dao;

import com.workmarket.dao.AbstractDAO;
import com.workmarket.domains.groups.model.UserUserGroupDocumentReference;
import com.workmarket.domains.groups.model.UserUserGroupDocumentReferencePagination;
import org.hibernate.Criteria;
import org.hibernate.FetchMode;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;
import org.springframework.util.Assert;

import java.util.Calendar;
import java.util.List;

/**
 * User: micah
 * Date: 12/16/13
 * Time: 4:21 PM
 */
@Repository
public class UserUserGroupDocumentReferenceDAOImpl extends AbstractDAO<UserUserGroupDocumentReference> implements UserUserGroupDocumentReferenceDAO {
	@Override
	protected Class<UserUserGroupDocumentReference> getEntityClass() {
		return UserUserGroupDocumentReference.class;
	}

	@Override
	public List<UserUserGroupDocumentReference> findAllDocumentReferencesByDate(Calendar date) {
		Criteria criteria = getFactory().getCurrentSession().createCriteria(getEntityClass());
		criteria.setFetchMode("userUserGroupDocumentReference", FetchMode.JOIN);
		criteria.setFetchMode("userUserGroupDocumentReference.requiredDocument", FetchMode.JOIN);
		criteria.setFetchMode("userUserGroupDocumentReference.referencedDocument", FetchMode.JOIN);

		criteria.add(Restrictions.le("expirationDate", date));

		return criteria.list();
	}

	@Override
	public UserUserGroupDocumentReferencePagination findAllDocumentReferencesByUserIdAndUserGroupId(Long userId, Long userGroupId, UserUserGroupDocumentReferencePagination pagination) {
		Assert.notNull(userId);
		Assert.notNull(userGroupId);
		Assert.notNull(pagination);

		Criteria criteria = getFactory().getCurrentSession().createCriteria(getEntityClass());
		Criteria count = getFactory().getCurrentSession().createCriteria(getEntityClass());
		count.setProjection(Projections.rowCount());
		criteria.setFirstResult(pagination.getStartRow());
		criteria.setMaxResults(pagination.getResultsLimit());
		criteria.setFetchMode("userUserGroupDocumentReference", FetchMode.JOIN);
		criteria.setFetchMode("userUserGroupDocumentReference.requiredDocument", FetchMode.JOIN);
		criteria.setFetchMode("userUserGroupDocumentReference.referencedDocument", FetchMode.JOIN);

		criteria.add(Restrictions.eq("user.id", userId));
		criteria.add(Restrictions.eq("userGroup.id", userGroupId));
		count.add(Restrictions.eq("user.id", userId));
		count.add(Restrictions.eq("userGroup.id", userGroupId));

		pagination.setResults(criteria.list());
		if (count.list().size() > 0)
			pagination.setRowCount(((Long) count.list().get(0)).intValue());
		else
			pagination.setRowCount(0);
		return pagination;
	}

	@Override
	public UserUserGroupDocumentReference findDocumentReferenceByUserIdAndDocumentId(Long userId, Long documentId) {
		Criteria criteria = getFactory().getCurrentSession().createCriteria(getEntityClass())
			.add(Restrictions.eq("user.id", userId))
			.add(Restrictions.eq("requiredDocument.id", documentId))
			.setMaxResults(1);
		return (UserUserGroupDocumentReference) criteria.uniqueResult();
	}
}
