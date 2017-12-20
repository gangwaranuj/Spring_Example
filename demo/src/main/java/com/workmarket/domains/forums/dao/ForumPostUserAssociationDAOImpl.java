package com.workmarket.domains.forums.dao;

import com.google.common.base.MoreObjects;

import com.workmarket.dao.DeletableAbstractDAO;
import com.workmarket.domains.forums.model.FlaggedPostStatistics;
import com.workmarket.domains.forums.model.ForumFlaggedPostPagination;
import com.workmarket.domains.forums.model.ForumPostUserAssociation;
import com.workmarket.domains.model.Pagination;

import org.hibernate.Criteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.transform.Transformers;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class ForumPostUserAssociationDAOImpl extends DeletableAbstractDAO<ForumPostUserAssociation> implements ForumPostUserAssociationDAO {

	@Override
	protected Class<ForumPostUserAssociation> getEntityClass() {
		return ForumPostUserAssociation.class;
	}

	@Override
	public List<Long> getUserFlaggedPostsInThread(Long postId, Long userId) {
		return getFactory().getCurrentSession().createCriteria(ForumPostUserAssociation.class)
				.createAlias("post", "fp")
				.add(Restrictions.or(Restrictions.eq("fp.parentId", postId), Restrictions.eq("fp.id", postId)))
				.add(Restrictions.eqProperty("post.id", "fp.id"))
				.add(Restrictions.eq("userId", userId))
				.add(Restrictions.eq("deleted", false))
				.add(Restrictions.eq("isFlagged", true))
				.addOrder(Order.desc("post.id"))
				.setProjection(Projections.property("post.id"))
				.list();
	}

	@Override
	public ForumPostUserAssociation getFlaggedPostByUser(Long postId, Long userId) {
		return (ForumPostUserAssociation) getFactory().getCurrentSession().createCriteria(ForumPostUserAssociation.class)
				.add(Restrictions.eq("post.id", postId))
				.add(Restrictions.eq("userId", userId))
				.add(Restrictions.eq("isFlagged", true))
				.uniqueResult();
	}

	@Override
	public List<ForumPostUserAssociation> getFlaggedPostsByUser(Long userId) {
		return getFactory().getCurrentSession().createCriteria(ForumPostUserAssociation.class)
				.add(Restrictions.eq("userId", userId))
				.add(Restrictions.eq("isFlagged", true))
				.addOrder(Order.desc("postId"))
				.list();
	}

	@Override
	public ForumFlaggedPostPagination getAllFlaggedPostStatistics(ForumFlaggedPostPagination pagination) {
		String sortColumn = MoreObjects.firstNonNull(pagination.getSortColumn(), "count");
		Order order = Pagination.SORT_DIRECTION.ASC.equals(pagination.getSortDirection())
										? Order.asc(sortColumn)
										: Order.desc(sortColumn);

		Criteria criteria = getFactory().getCurrentSession().createCriteria(ForumPostUserAssociation.class)
				.add(Restrictions.eq("deleted", false))
				.add(Restrictions.eq("isFlagged", true));

		Criteria count = getFactory().getCurrentSession().createCriteria(ForumPostUserAssociation.class)
				.add(Restrictions.eq("deleted", false))
				.add(Restrictions.eq("isFlagged", true));

		List<FlaggedPostStatistics> statistics = criteria.setProjection(Projections.projectionList()
						.add(Projections.groupProperty("post.id"), "postId")
						.add(Projections.property("createdOn"), "dateReported")
						.add(Projections.count("userId"), "count"))
				.addOrder(order)
				.setFirstResult(pagination.getStartRow())
				.setMaxResults(pagination.getResultsLimit())
				.setResultTransformer(Transformers.aliasToBean(FlaggedPostStatistics.class))
				.list();

		int rowCount = count.setProjection(Projections.projectionList()
						.add(Projections.groupProperty("post.id"), "postId")
						.add(Projections.property("modifiedOn"), "dateReported")
						.add(Projections.count("userId"), "count"))
				.setResultTransformer(Transformers.aliasToBean(FlaggedPostStatistics.class))
				.list()
				.size();

		pagination.setResults(statistics);
		pagination.setRowCount(rowCount);

		return pagination;
	}

	@Override
	public List<ForumPostUserAssociation> getAllFlagsByPostId(Long postId) {
		return getFactory().getCurrentSession().createCriteria(ForumPostUserAssociation.class)
				.add(Restrictions.eq("post.id", postId))
				.add(Restrictions.eq("isFlagged", true))
				.list();
	}

}
