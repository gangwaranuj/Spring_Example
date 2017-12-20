package com.workmarket.domains.forums.dao;

import com.workmarket.dao.AbstractDAO;
import com.workmarket.domains.forums.model.ForumPostTagAssociation;
import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;
import org.springframework.util.Assert;

import java.util.HashSet;
import java.util.Set;

@Repository
public class ForumPostTagAssociationDAOImpl extends AbstractDAO<ForumPostTagAssociation> implements ForumPostTagAssociationDAO {

	@Override
	protected Class<ForumPostTagAssociation> getEntityClass() {
		return ForumPostTagAssociation.class;
	}

	public Set<ForumPostTagAssociation> findAllPostTags(Long postId) {
		Assert.notNull(postId);

		Criteria cr = getFactory().getCurrentSession().createCriteria(getEntityClass())
				.createAlias("forumPost", "post")
				.add(Restrictions.eq("post.id", postId))
				.add(Restrictions.eq("deleted", false));
		return new HashSet<>(cr.list());
	}


}
