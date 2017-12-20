package com.workmarket.domains.forums.dao;

import com.workmarket.dao.DeletableAbstractDAO;
import com.workmarket.domains.model.Pagination;
import com.workmarket.domains.forums.model.ForumPost;
import com.workmarket.domains.forums.model.ForumPostPagination;
import com.workmarket.utility.HibernateUtilities;
import com.workmarket.utility.sql.SQLBuilder;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Criteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.Collections;
import java.util.List;

@Repository
public class ForumPostDAOImpl extends DeletableAbstractDAO<ForumPost> implements ForumPostDAO {
	private static final Log logger = LogFactory.getLog(ForumPostDAOImpl.class);

	@Qualifier("jdbcTemplate") @Autowired private NamedParameterJdbcTemplate jdbcTemplate;

	@Override
	protected Class<ForumPost> getEntityClass() {
		return ForumPost.class;
	}

	@Override
	public ForumPostPagination findAllCategoryPosts(Long categoryId, ForumPostPagination pagination) {
		Criteria criteria = getFactory().getCurrentSession().createCriteria(ForumPost.class)
				.setFirstResult(pagination.getStartRow())
				.setMaxResults(pagination.getResultsLimit())
				.add(Restrictions.eq("categoryId", categoryId))
				.add(Restrictions.isNull("parentId"))
				.add(Restrictions.eq("deleted", false));


		Criteria count = getFactory().getCurrentSession().createCriteria(ForumPost.class)
				.add(Restrictions.eq("categoryId", categoryId))
				.add(Restrictions.isNull("parentId"))
				.add(Restrictions.eq("deleted", false));

		Order order = pagination.getSortDirection().equals(Pagination.SORT_DIRECTION.DESC) ? Order.desc("lastPostOn") : Order.asc("lastPostOn");
		criteria.addOrder(order);

		pagination.setResults(criteria.list());
		pagination.setRowCount(count.list().size());

		return pagination;
	}

	@Override
	public ForumPostPagination findAllPostReplies(Long rootId, ForumPostPagination pagination) {
		Criteria criteria = getFactory().getCurrentSession().createCriteria(ForumPost.class)
				.setFirstResult(pagination.getStartRow())
				.setMaxResults(pagination.getResultsLimit())
				.add(Restrictions.eq("rootId", rootId))
				.addOrder(Order.asc("createdOn"));

		Criteria count = getFactory().getCurrentSession().createCriteria(ForumPost.class)
				.setFirstResult(pagination.getStartRow())
				.setMaxResults(pagination.getResultsLimit())
				.add(Restrictions.eq("rootId", rootId));

		Long rowCount = HibernateUtilities.getRowCount(count);

		pagination.setResults(sortReplies(criteria.list()));
		pagination.setRowCount(rowCount);

		return pagination;
	}

	@Override
	public ForumPostPagination findAllFollowingPosts(Long userId, ForumPostPagination pagination) {
		Criteria criteria = getFactory().getCurrentSession().createCriteria(getEntityClass())
				.setFirstResult(pagination.getStartRow())
				.setMaxResults(pagination.getResultsLimit())
				.add(Restrictions.eq("deleted", false))
				.createAlias("forumPostFollowers", "follower")
				.add(Restrictions.eq("follower.deleted", false))
				.add(Restrictions.eq("follower.followerUser.id", userId));

		Criteria count = getFactory().getCurrentSession().createCriteria(ForumPost.class)
				.add(Restrictions.eq("deleted", false))
				.createAlias("forumPostFollowers", "follower")
				.add(Restrictions.eq("follower.deleted", false))
				.add(Restrictions.eq("follower.followerUser.id", userId));

		Order order = pagination.getSortDirection().equals(Pagination.SORT_DIRECTION.DESC) ? Order.desc("lastPostOn") : Order.asc("lastPostOn");
		criteria.addOrder(order);

		List<ForumPost> list = criteria.list();
		pagination.setResults(list);
		pagination.setRowCount(count.list().size());

		return pagination;
	}

	@Override
	public ForumPostPagination findAllFollowingPostsByCategory(Long userId, Long categoryId, ForumPostPagination pagination) {
		Criteria criteria = getFactory().getCurrentSession().createCriteria(getEntityClass())
				.setFirstResult(pagination.getStartRow())
				.setMaxResults(pagination.getResultsLimit())
				.add(Restrictions.eq("deleted", false))
				.add(Restrictions.eq("categoryId", categoryId))
				.createAlias("forumPostFollowers", "follower")
				.add(Restrictions.eq("follower.deleted", false))
				.add(Restrictions.eq("follower.followerUser.id", userId));

		Criteria count = getFactory().getCurrentSession().createCriteria(ForumPost.class)
				.add(Restrictions.eq("deleted", false))
				.add(Restrictions.eq("categoryId", categoryId))
				.createAlias("forumPostFollowers", "follower")
				.add(Restrictions.eq("follower.deleted", false))
				.add(Restrictions.eq("follower.followerUser.id", userId));

		Order order = pagination.getSortDirection().equals(Pagination.SORT_DIRECTION.DESC) ? Order.desc("lastPostOn") : Order.asc("lastPostOn");
		criteria.addOrder(order);

		List<ForumPost> list = criteria.list();
		pagination.setResults(list);
		pagination.setRowCount(count.list().size());

		return pagination;
	}

	@Override
	public Long getMaxPostId() {
		SQLBuilder builder = new SQLBuilder();
		builder.addColumn("MAX(forum_post.id) as forumPostId")
			   .addTable("forum_post");
		try {
			return jdbcTemplate.queryForObject(builder.build(), builder.getParams(), Long.class);
		} catch (EmptyResultDataAccessException e) {
			logger.error("Failed to get max post id", e);
			return 0L;
		}
	}

	private List<ForumPost> sortReplies(List<ForumPost> posts) {
		Collections.sort(posts, ForumPost.THREAD_ORDER);
		return posts;
	}
}
