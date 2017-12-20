package com.workmarket.dao.tag;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;
import org.springframework.util.Assert;

import com.workmarket.dao.AbstractDAO;
import com.workmarket.domains.model.ApprovalStatus;
import com.workmarket.domains.model.tag.TagPagination;
import com.workmarket.domains.model.tag.UserTag;
import com.workmarket.domains.model.tag.UserTagAssociation;

@Repository
public class UserTagAssociationDAOImpl extends AbstractDAO<UserTagAssociation> implements UserTagAssociationDAO {

	protected Class<UserTagAssociation> getEntityClass() {
		return UserTagAssociation.class;
	}

	@SuppressWarnings("unchecked")
	@Override
	public TagPagination findAllUserTags(Long userId, TagPagination pagination) {
		Assert.notNull(userId);
		Assert.notNull(pagination);

		Criteria criteria = getFactory().getCurrentSession().createCriteria(getEntityClass());
		criteria.setFirstResult(pagination.getStartRow());
		criteria.setMaxResults(pagination.getResultsLimit());

		criteria.createAlias("tag", "tag");

		// filters
		criteria.add(Restrictions.eq("user.id", userId));
		criteria.add(Restrictions.eq("deleted", false));
		criteria.add(Restrictions.ne("tag.approvalStatus", ApprovalStatus.DECLINED));

		// sorts
		criteria.addOrder(Order.asc("tag.name"));

		pagination.setResults(criteria.list());
		// TODO AP
		pagination.setRowCount(((Long) criteria.setProjection(Projections.rowCount()).list().get(0)).intValue());
		return pagination;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<UserTag> findAllUserTags() {
		return getFactory().getCurrentSession().getNamedQuery("UserTagAssociation.findAllUserTags").list();
	}
}
