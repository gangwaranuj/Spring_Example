package com.workmarket.domains.forums.dao;

import com.google.common.base.MoreObjects;

import com.workmarket.dao.DeletableAbstractDAO;
import com.workmarket.domains.forums.model.UserForumBan;
import com.workmarket.domains.forums.model.UserForumBanPagination;
import com.workmarket.domains.model.Pagination;
import com.workmarket.utility.HibernateUtilities;

import org.hibernate.Criteria;
import org.hibernate.criterion.Junction;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class UserForumBanDAOImpl extends DeletableAbstractDAO<UserForumBan> implements UserForumBanDAO {

	@Override
	protected Class<UserForumBan> getEntityClass() {
		return UserForumBan.class;
	}

	@Override
	public UserForumBanPagination getAllBannedUsers(UserForumBanPagination pagination) {
		String sortColumn = MoreObjects.firstNonNull(pagination.getSortColumn(), "createdOn");
		Order order = Pagination.SORT_DIRECTION.ASC.equals(pagination.getSortDirection())
				? Order.asc(sortColumn)
				: Order.desc(sortColumn);

		Criteria criteria = getFactory().getCurrentSession().createCriteria(UserForumBan.class)
				.add(Restrictions.eq("deleted", false))
				.createAlias("bannedUser", "user", Criteria.INNER_JOIN);

		Criteria count = getFactory().getCurrentSession().createCriteria(UserForumBan.class)
				.add(Restrictions.eq("deleted", false))
				.createAlias("bannedUser", "user", Criteria.INNER_JOIN);

		if (pagination.hasFilter(UserForumBanPagination.FILTER_KEYS.NAME_REASON)) {
			String searchQuery = pagination.getFilter(UserForumBanPagination.FILTER_KEYS.NAME_REASON);

			Junction conditions = Restrictions.disjunction();
			conditions.add(Restrictions.ilike("user.firstName", searchQuery, MatchMode.ANYWHERE))
					.add(Restrictions.ilike("user.lastName", searchQuery, MatchMode.ANYWHERE))
					.add(Restrictions.ilike("reason", searchQuery, MatchMode.ANYWHERE));

			criteria.add(conditions);
			count.add(conditions);
		}

		criteria.addOrder(order)
				.setFirstResult(pagination.getStartRow())
				.setMaxResults(pagination.getResultsLimit());

		pagination.setResults(criteria.list());
		pagination.setRowCount(HibernateUtilities.getRowCount(count));

		return pagination;
	}

	@Override
	public UserForumBan getBannedUser(Long userId) {
		return (UserForumBan) getFactory().getCurrentSession().createCriteria(UserForumBan.class)
				.add(Restrictions.eq("bannedUser.id", userId))
				.uniqueResult();
	}

	@Override
	public List<Long> getAllBannedUsersOnPost(Long postId) {
		List<Long> bannedUserOnPost = getFactory().getCurrentSession().getNamedQuery("userForumBan.getBannedUsersOnHeadPost")
				.setParameter("postId", postId)
				.list();

		List<Long> bannedUsersOnReplies = getFactory().getCurrentSession().getNamedQuery("userForumBan.getBannedUsersOnPostReplies")
				.setParameter("postId", postId)
				.list();

		bannedUserOnPost.addAll(bannedUsersOnReplies);
		return bannedUserOnPost;
	}
}
