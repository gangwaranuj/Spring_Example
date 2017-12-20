package com.workmarket.domains.forums.dao;

import com.workmarket.dao.DeletableDAOInterface;
import com.workmarket.domains.forums.model.ForumFlaggedPostPagination;
import com.workmarket.domains.forums.model.ForumPostUserAssociation;

import java.util.List;

public interface ForumPostUserAssociationDAO extends DeletableDAOInterface<ForumPostUserAssociation> {

	List<ForumPostUserAssociation> getFlaggedPostsByUser(Long userId);

	List<Long> getUserFlaggedPostsInThread(Long postId, Long userId);

	ForumPostUserAssociation getFlaggedPostByUser(Long postId, Long userId);

	ForumFlaggedPostPagination getAllFlaggedPostStatistics(ForumFlaggedPostPagination pagination);

	List<ForumPostUserAssociation> getAllFlagsByPostId(Long postId);
}
