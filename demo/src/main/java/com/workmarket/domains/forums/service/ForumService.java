package com.workmarket.domains.forums.service;

import com.workmarket.domains.model.User;
import com.workmarket.domains.forums.model.ForumCategory;
import com.workmarket.domains.forums.model.ForumFlaggedPostPagination;
import com.workmarket.domains.forums.model.ForumPost;
import com.workmarket.domains.forums.model.ForumPostFollower;
import com.workmarket.domains.forums.model.ForumPostPagination;
import com.workmarket.domains.forums.model.ForumTag;
import com.workmarket.domains.forums.model.UserForumBan;
import com.workmarket.domains.forums.model.UserForumBanPagination;
import com.workmarket.web.forms.forums.ForumPostForm;

import java.util.List;
import java.util.Map;
import java.util.Set;

public interface ForumService {

	List<ForumCategory> getCategoryList();
	ForumCategory getForumCategory(Long categoryId);
	ForumCategory saveCategory(ForumCategory category);
	ForumPostPagination getCategoryPosts(Long categoryId, ForumPostPagination pagination);

	ForumPost getPostById(Long id);
	ForumPostPagination getPostReplies(Long parentId, ForumPostPagination pagination);

	ForumPostPagination getFollowedPost(Long userId, ForumPostPagination pagination);
	ForumPostPagination getFollowedPost(Long userId, Long categoryId, ForumPostPagination pagination);
	ForumPost toggleForumPostDelete(Long postId);

	ForumPost toggleFlaggedStatus(Long postId, Long userId);
	ForumPost unflagPost(Long postId);
	Set<Long> getUserFlaggedPostsInThread(Long postId, Long userId);
	void getAllFlaggedPostsStatistics(ForumFlaggedPostPagination pagination);
	UserForumBanPagination getAllBannedUsers(UserForumBanPagination pagination);

	void createWorkFromFlaggedPostEvent(ForumPost post);
	void notifyPostFollowersEvent(ForumPost post);

	boolean isUserBanned(Long userId);
	UserForumBan banUser(Long userId, String reason);
	UserForumBan banUser(String email, String reason);
	UserForumBan unbanUser(Long userBanId);
	Set<Long> getBannedUsersInThread(Long postId);

	ForumPost savePost(ForumPostForm form);
	ForumPost savePost(Long postId, String newTitle, String newPost);
	ForumPost savePost(Long postId, String newPost);
	ForumPost saveReply(ForumPostForm form);
	List<ForumPostFollower> getFollowersOnPost(Long postId);
	List<ForumPostFollower> getPostsUserIsFollowing(Long userId);
	ForumPostFollower toggleFollower(Long postId, Long userId);
	Set<ForumTag> getTagsOnPost(Long postId);
	boolean isFollowerOnPost(Long postId, Long userId);

	Map<Long, String> getAvatarURIs(List<ForumPost> posts) throws Exception;
	String getAvatarURI(ForumPost post) throws Exception;

	Map<Long, String> getPostCreatorNames(List<ForumPost> posts);
	String getPostCreatorName(ForumPost post);

	boolean isUserAdmin(User user);
}
