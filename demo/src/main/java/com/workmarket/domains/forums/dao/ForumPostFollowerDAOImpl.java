package com.workmarket.domains.forums.dao;

import com.workmarket.dao.DeletableAbstractDAO;
import com.workmarket.domains.forums.model.ForumPostFollower;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class ForumPostFollowerDAOImpl extends DeletableAbstractDAO<ForumPostFollower> implements ForumPostFollowerDAO {

	@Override
	protected Class<ForumPostFollower> getEntityClass() {
		return ForumPostFollower.class;
	}

	@Override
	public List<ForumPostFollower> getPostFollowers(Long postId) {
		return getFactory().getCurrentSession().createCriteria(ForumPostFollower.class)
				.add(Restrictions.eq("post.id", postId))
				.add(Restrictions.eq("deleted", false))
				.list();
	}

	@Override
	public List<ForumPostFollower> getPostsFollowedByUser(Long userId) {
		return getFactory().getCurrentSession().createCriteria(ForumPostFollower.class)
				.add(Restrictions.eq("followerUser.id", userId))
				.add(Restrictions.eq("deleted", false))
				.list();
	}

	@Override
	public ForumPostFollower getPostFollowerByUserAndPostNotDeleted(Long postId, Long userId) {
		return (ForumPostFollower) getFactory().getCurrentSession().createCriteria(ForumPostFollower.class)
				.add(Restrictions.eq("followerUser.id", userId))
				.add(Restrictions.eq("post.id", postId))
				.add(Restrictions.eq("deleted", false))
				.uniqueResult();
	}
	@Override
	public ForumPostFollower getPostFollowerByUserAndPost(Long postId, Long userId) {
		return (ForumPostFollower) getFactory().getCurrentSession().createCriteria(ForumPostFollower.class)
				.add(Restrictions.eq("followerUser.id", userId))
				.add(Restrictions.eq("post.id", postId))
				.uniqueResult();
	}

}
