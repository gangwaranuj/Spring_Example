package com.workmarket.domains.forums.dao;

import com.workmarket.dao.DeletableDAOInterface;
import com.workmarket.domains.forums.model.ForumPostFollower;

import java.util.List;

public interface ForumPostFollowerDAO extends DeletableDAOInterface<ForumPostFollower> {

	List<ForumPostFollower> getPostFollowers(Long postId);

	List<ForumPostFollower> getPostsFollowedByUser(Long userId);

	ForumPostFollower getPostFollowerByUserAndPostNotDeleted(Long postId, Long userId);

	ForumPostFollower getPostFollowerByUserAndPost(Long postId, Long userId);
}
