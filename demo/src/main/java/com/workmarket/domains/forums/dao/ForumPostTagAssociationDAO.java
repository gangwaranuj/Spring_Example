package com.workmarket.domains.forums.dao;

import com.workmarket.dao.DAOInterface;
import com.workmarket.domains.forums.model.ForumPostTagAssociation;

import java.util.Set;

public interface ForumPostTagAssociationDAO extends DAOInterface<ForumPostTagAssociation> {

	public Set<ForumPostTagAssociation> findAllPostTags(Long postId);

}
