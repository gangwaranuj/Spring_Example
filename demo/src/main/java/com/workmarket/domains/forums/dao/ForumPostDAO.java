package com.workmarket.domains.forums.dao;

import com.workmarket.dao.DeletableDAOInterface;
import com.workmarket.domains.forums.model.ForumPost;
import com.workmarket.domains.forums.model.ForumPostPagination;

public interface ForumPostDAO extends DeletableDAOInterface<ForumPost> {

	ForumPostPagination findAllPostReplies(Long parentId, ForumPostPagination pagination);

	ForumPostPagination findAllCategoryPosts(Long categoryId, ForumPostPagination pagination);

	ForumPostPagination findAllFollowingPosts(Long userId, ForumPostPagination pagination);

	ForumPostPagination findAllFollowingPostsByCategory(Long userId, Long categoryId, ForumPostPagination pagination);

	Long getMaxPostId();
}
