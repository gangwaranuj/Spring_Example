package com.workmarket.domains.forums.service;

import com.google.common.collect.Sets;
import com.workmarket.domains.forums.dao.ForumPostDAO;
import com.workmarket.domains.forums.dao.ForumPostFollowerDAO;
import com.workmarket.domains.forums.dao.ForumPostUserAssociationDAO;
import com.workmarket.domains.model.User;
import com.workmarket.domains.forums.model.ForumCategory;
import com.workmarket.domains.forums.model.ForumFlaggedPostPagination;
import com.workmarket.domains.forums.model.ForumPost;
import com.workmarket.domains.forums.model.ForumPostFollower;
import com.workmarket.domains.forums.model.ForumPostPagination;
import com.workmarket.domains.forums.model.ForumTag;
import com.workmarket.domains.forums.model.UserForumBan;
import com.workmarket.domains.forums.model.UserForumBanPagination;
import com.workmarket.service.business.BaseServiceIT;
import com.workmarket.service.business.UserService;
import com.workmarket.configuration.Constants;
import com.workmarket.test.IntegrationTest;
import com.workmarket.web.forms.forums.ForumPostForm;
import groovy.lang.Category;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;
import java.util.Set;

import static org.junit.Assert.*;

@RunWith(SpringJUnit4ClassRunner.class)
@Category(IntegrationTest.class)
public class ForumServiceIT extends BaseServiceIT {

	@Autowired ForumService forumService;
	@Autowired ForumPostDAO postDAO;
	@Autowired UserService userService;
	@Autowired ForumPostFollowerDAO followerDAO;
	@Autowired NamedParameterJdbcTemplate jdbcTemplate;
	@Autowired ForumPostUserAssociationDAO flaggedDAO;

	private ForumCategory category;
	private ForumPost post, post2, post3, post4, flaggedPost;
	private ForumPost reply, reply2, reply3, reply4;
	private ForumPostFollower follower, follower2, follower3, follower4;
	private ForumPostPagination pagination;
	private User user, user2, user3;
	private UserForumBan ban, ban2;

	private static final long
		CATEGORY_ID_1 = 1,
		CATEGORY_ID_2 = 2,
		USER_ID_1 = Constants.BACK_END_USER_ID,
		USER_ID_2 = Constants.FRONT_END_USER_ID,
		USER_ID_3 = Constants.JEFF_WALD_USER_ID;

	@Before
	public void setUp() {
		category = newForumCategory(CATEGORY_ID_1);
	}

	@Test
	public void getCategory_byId_success() throws Exception {
		ForumCategory tech = forumService.getForumCategory(CATEGORY_ID_1);
		assertNotNull(tech);
	}

	@Test
	public void getCategoryList_success() throws Exception {
		List<ForumCategory> categories = forumService.getCategoryList();
		assertNotNull(categories);
		assertTrue(categories.size() > 0);
	}

	@Test
	public void createPost_defaultValues_success() throws Exception {
		post = newForumPost(CATEGORY_ID_1);
		assertNotNull(post);
		assertNotNull(post.getId());
		assertNull(post.getParentId());
	}

	@Test
	public void createReply_defaultValues_success() throws Exception {
		post = newForumPost(CATEGORY_ID_1);
		reply = newForumPostReply(post.getId(), CATEGORY_ID_1);
		assertNotNull(reply);
		assertNull(reply.getTitle());
		assertNotNull(reply.getId());
		assertTrue(reply.getParentId().equals(post.getId()));
	}

	@Test
	public void createPost_fromForm_success() throws Exception {
		ForumPostForm form = new ForumPostForm();
		form.setTitle("title");
		form.setComment("comment");
		form.setCategoryId(category.getId());

		ForumPost postForm = forumService.savePost(form);
		assertNotNull(postForm);
		assertNotNull(postForm.getId());
		assertNull(postForm.getParentId());
		deleteTestForumPost(postForm);
	}

	@Test
	public void createPost_fromForm_withTags_success() throws Exception {
		ForumPostForm form = new ForumPostForm();
		form.setTitle("title");
		form.setComment("comment");
		form.setCategoryId(category.getId());

		Set<String> tags = Sets.newLinkedHashSet();
		tags.add(ForumTag.CONTACT_MANAGER.toString());
		tags.add(ForumTag.ASSIGNMENTS.toString());
		form.setTags(tags);

		ForumPost post = forumService.savePost(form);
		assertEquals(2, post.getForumPostTags().size());
		deleteTestForumPost(post);
	}

	@Test
	public void createPost_fromForm_withFakeTags_fail() throws Exception {
		ForumPostForm form = new ForumPostForm();
		form.setTitle("title");
		form.setComment("comment");
		form.setCategoryId(category.getId());

		Set<String> tags = Sets.newLinkedHashSet();
		tags.add("shady tag");
		tags.add("illegal");
		form.setTags(tags);

		ForumPost post = forumService.savePost(form);
		assertEquals(0, post.getForumPostTags().size());
		deleteTestForumPost(post);
	}

	@Test
	public void createPost_thenEdit_success() throws Exception {
		ForumPostForm form = new ForumPostForm();
		form.setTitle("title");
		form.setComment("comment");
		form.setCategoryId(category.getId());

		ForumPost postForm = forumService.savePost(form);
		assertNotNull(postForm);
		assertNotNull(postForm.getId());
		assertNull(postForm.getParentId());

		ForumPost editedPost = this.forumPostEdit(postForm.getId(),"title - edited","comment - edited");
		assertNotNull(editedPost);
		assertEquals(postForm, editedPost);
		assertEquals(editedPost.getTitle(),"title - edited");
		assertEquals(editedPost.getComment(), "comment - edited");
		deleteTestForumPost(editedPost);
	}

	@Test
	public void createReply_fromForm_success() throws Exception {
		post = newForumPost(CATEGORY_ID_1);

		ForumPostForm form = new ForumPostForm();
		form.setParentId(post.getId());
		form.setRootId(post.getId());
		form.setComment("comment");
		form.setCategoryId(category.getId());

		ForumPost replyForm = forumService.saveReply(form);
		assertNotNull(replyForm);
		assertNotNull(replyForm.getId());
		assertNull(replyForm.getTitle());
		deleteTestForumPost(replyForm);
	}

	@Test
	public void createReply_thenEdit_success() throws Exception {
		post = newForumPost(CATEGORY_ID_1);
		ForumPostForm form = new ForumPostForm();
		form.setParentId(post.getId());
		form.setComment("comment");
		form.setCategoryId(category.getId());

		ForumPost postForm = forumService.savePost(form);
		assertNotNull(postForm);
		assertNotNull(postForm.getId());

		ForumPost editedPost = this.forumPostReplyEdit(postForm.getId(),"comment - edited");
		assertNotNull(editedPost);
		assertEquals(postForm, editedPost);
		assertEquals(editedPost.getComment(), "comment - edited");
		deleteTestForumPost(editedPost);
	}

	@Test
	public void createReply_bannedUser_failure() throws Exception {
		user3 = newUser(USER_ID_3);
		UserForumBan ban = newUserForumBan(user3);
		post = newForumPost(CATEGORY_ID_1);
		ForumPostForm form = new ForumPostForm();
		form.setParentId(post.getId());
		form.setComment("comment");
		form.setCategoryId(category.getId());

		authenticationService.setCurrentUser(user3);

		ForumPost replyForm = forumService.saveReply(form);
		assertNull(replyForm);

		deleteTestUserForumBan(ban);
	}

	@Test
	public void createPost_bannedUser_failure() throws Exception {
		pagination = new ForumPostPagination(true);
		user3 = newUser(USER_ID_3);

		ban = newUserForumBan(user3);

		ForumPostForm form = new ForumPostForm();
		form.setTitle("title");

		form.setComment("comment");
		form.setCategoryId(category.getId());

		authenticationService.setCurrentUser(user3);

		ForumPost postForm = forumService.savePost(form);
		assertNull(postForm);
	}

	@Test
	public void getPost_byIdNoMetadataImplicit_success() throws Exception {
		post = newForumPost(CATEGORY_ID_1);
		ForumPost gotPost = forumService.getPostById(post.getId());
		assertEquals(gotPost, post);
	}

	@Test
	public void getPost_byIdNoMetadataExplicit_success() throws Exception {
		post = newForumPost(CATEGORY_ID_1);
		ForumPost gotPost = forumService.getPostById(post.getId());
		assertEquals(gotPost, post);
	}

	@Test
	public void getPost_byIdWithMetadata_success() throws Exception {
		post = newForumPost(CATEGORY_ID_1);
		ForumPost gotPost = forumService.getPostById(post.getId());
		assertEquals(gotPost, post);
	}

	@Test
	public void getPostReplies_withThreeReplies_success() throws Exception {
		pagination = new ForumPostPagination(true);
		user = newUser(USER_ID_1);
		user3 = newUser(USER_ID_3);

		post = newForumPost(CATEGORY_ID_1);
		post2 = newForumPost(CATEGORY_ID_2);

		reply = newForumPostReply(post.getId(), CATEGORY_ID_1);
		reply2 = newForumPostReply(post.getId(), CATEGORY_ID_1);
		reply3 = newForumPostReply(post.getId(), CATEGORY_ID_1);

		authenticationService.setCurrentUser(user3);
		reply4 = newForumPostReply(post2.getId(), CATEGORY_ID_1);
		authenticationService.setCurrentUser(Constants.WORKMARKET_SYSTEM_USER_ID);

		ForumPostPagination paginate = forumService.getPostReplies(post.getId(), pagination);
		assertNotNull(paginate);
		List<ForumPost> post = paginate.getResults();
		assertTrue(post.contains(reply2));
		assertTrue(post.contains(reply3));
		assertFalse(post.contains(reply4));
	}

	@Test
	public void getPostReplies_withThreeRepliesAndNested_success() throws Exception {
		post = newForumPost(CATEGORY_ID_1);
		post2 = newForumPost(CATEGORY_ID_2);
		reply = newForumPostReply(post.getId(), CATEGORY_ID_1);
		reply2 = newForumPostReply(post.getId(), CATEGORY_ID_1);
		reply3 = newForumPostReply(post.getId(), CATEGORY_ID_1);
		reply4 = newForumPostReply(post2.getId(), CATEGORY_ID_1);
		ForumPost nestedReply = newForumPostNestedReply(post.getId(), reply.getId(), CATEGORY_ID_1);
		ForumPost nestedReply2 = newForumPostNestedReply(post.getId(), reply.getId(),  CATEGORY_ID_1);
		ForumPost nestedReply3 = newForumPostNestedReply(post.getId(), reply2.getId(),  CATEGORY_ID_1);

		pagination = new ForumPostPagination(true);
		ForumPostPagination paginate = forumService.getPostReplies(post.getId(), pagination);
		assertNotNull(paginate);
		List<ForumPost> post = paginate.getResults();

		assertTrue(post.get(0).equals(reply));
		assertTrue(post.get(1).equals(nestedReply));
		assertTrue(post.get(2).equals(nestedReply2));
		assertTrue(post.get(3).equals(reply2));
		assertTrue(post.get(4).equals(nestedReply3));
		assertTrue(post.contains(reply3));
		assertFalse(post.contains(reply4));

		deleteTestForumPost(nestedReply);
		deleteTestForumPost(nestedReply2);
		deleteTestForumPost(nestedReply3);
	}

	@Test
	public void getCategoryPosts_withTwoPosts_success() throws Exception {
		post = newForumPost(CATEGORY_ID_1);
		post2 = newForumPost(CATEGORY_ID_2);
		post3 = newForumPost(CATEGORY_ID_1);
		post4 = newForumPost(CATEGORY_ID_2);
		pagination = new ForumPostPagination(true);
		ForumPostPagination paginate = forumService.getCategoryPosts(category.getId(), pagination);
		assertNotNull(paginate);

		List<ForumPost> posts = paginate.getResults();
		assertTrue(posts.contains(post));
		assertTrue(posts.contains(post3));
		assertFalse(posts.contains(post2));
		assertFalse(posts.contains(post4));
	}

	@Test
	public void getCategoryPosts_noRepliesInList_success() throws Exception {
		pagination = new ForumPostPagination(true);

		post = newForumPost(CATEGORY_ID_1);

		ForumPostPagination paginate = forumService.getCategoryPosts(category.getId(), pagination);
		assertNotNull(paginate);

		List<ForumPost> posts = paginate.getResults();
		assertFalse(posts.contains(reply));
	}

	@Test
	public void getFollowersOnPost_hasFollowers_success() throws Exception {
		user = newUser(USER_ID_1);
		user2 = newUser(USER_ID_2);
		post = newForumPost(CATEGORY_ID_1);
		post2 = newForumPost(CATEGORY_ID_2);
		post3 = newForumPost(CATEGORY_ID_1);
		follower = newForumPostFollower(post, user);
		follower2 = newForumPostFollower(post2, user);
		follower3 = newForumPostFollower(post, user2);
		follower4 = newForumPostFollower(post3, user2);
		List<ForumPostFollower> followers = forumService.getFollowersOnPost(post.getId());

		assertTrue(followers.contains(follower));
		assertTrue(followers.contains(follower3));
		assertFalse(followers.contains(follower2));
		assertFalse(followers.contains(follower4));
	}

	@Test
	public void getPostsUserIsFollowing_isFollowing_success() throws Exception {
		user = newUser(USER_ID_1);
		user2 = newUser(USER_ID_2);
		post = newForumPost(CATEGORY_ID_1);
		post2 = newForumPost(CATEGORY_ID_2);
		post3 = newForumPost(CATEGORY_ID_1);
		follower = newForumPostFollower(post, user);
		follower2 = newForumPostFollower(post2, user);
		follower3 = newForumPostFollower(post, user2);
		follower4 = newForumPostFollower(post3, user2);

		List<ForumPostFollower> followers = forumService.getPostsUserIsFollowing(USER_ID_1);

		assertTrue(followers.contains(follower));
		assertTrue(followers.contains(follower2));
		assertFalse(followers.contains(follower3));
		assertFalse(followers.contains(follower4));
	}

	@Test
	public void isFollowerOnPost_true_success()  throws Exception {
		pagination = new ForumPostPagination(true);
		user = newUser(USER_ID_1);

		post = newForumPost(CATEGORY_ID_1);

		follower = newForumPostFollower(post, user);

		boolean isFollower = forumService.isFollowerOnPost(post.getId(), USER_ID_1);

		assertTrue(isFollower);
	}

	@Test
	public void isFollowerOnPost_false_success() throws Exception {
		post3 = newForumPost(CATEGORY_ID_1);
		boolean isFollower = forumService.isFollowerOnPost(post3.getId(), USER_ID_1);

		assertFalse(isFollower);
	}

	@Test
	public void getFollowedPost_success() throws Exception {
		pagination = new ForumPostPagination(true);
		user = newUser(USER_ID_1);
		user2 = newUser(USER_ID_2);

		post = newForumPost(CATEGORY_ID_1);
		post2 = newForumPost(CATEGORY_ID_2);
		post3 = newForumPost(CATEGORY_ID_1);
		post4 = newForumPost(CATEGORY_ID_2);

		follower = newForumPostFollower(post, user);
		follower2 = newForumPostFollower(post2, user);
		follower3 = newForumPostFollower(post, user2);
		follower4 = newForumPostFollower(post3, user2);

		ForumPostPagination paginate = forumService.getFollowedPost(USER_ID_1, pagination);
		assertNotNull(paginate);

		List<ForumPost> posts = paginate.getResults();
		assertTrue(posts.contains(post));
		assertTrue(posts.contains(post2));
		assertFalse(posts.contains(post3));
		assertFalse(posts.contains(post4));
	}

	@Test
	public void getFollowedPosts_ByCategory_success() throws Exception {
		newForumCategory(CATEGORY_ID_2);
		pagination = new ForumPostPagination(true);
		user = newUser(USER_ID_1);
		user2 = newUser(USER_ID_2);

		post = newForumPost(CATEGORY_ID_1);
		post2 = newForumPost(CATEGORY_ID_2);
		post3 = newForumPost(CATEGORY_ID_1);
		post4 = newForumPost(CATEGORY_ID_2);

		follower = newForumPostFollower(post, user);
		follower2 = newForumPostFollower(post2, user);
		follower3 = newForumPostFollower(post, user2);
		follower4 = newForumPostFollower(post3, user2);

		ForumPostPagination paginate = forumService.getFollowedPost(USER_ID_1, CATEGORY_ID_1, pagination);
		assertNotNull(paginate);

		List<ForumPost> posts = paginate.getResults();
		assertTrue(posts.contains(post));
		assertFalse(posts.contains(post2));
		assertFalse(posts.contains(post3));
		assertFalse(posts.contains(post4));
	}

	@Test
	public void toggleFollower_setDeleted_success() throws Exception {
		user = newUser(USER_ID_1);
		post = newForumPost(CATEGORY_ID_1);
		follower = newForumPostFollower(post, user);
		ForumPostFollower follower1 = forumService.toggleFollower(follower.getPost().getId(), follower.getFollowerUser().getId());
		assertNotNull(follower1);
		assertTrue(follower1.getDeleted());
		deleteTestForumPostFollower(follower1);
	}

	@Test
	public void toggleForumPostDelete_setDeleted_success() throws Exception {
		post = newForumPost(CATEGORY_ID_1);

		ForumPost post1 = forumService.toggleForumPostDelete(post.getId());
		assertTrue(post1.getDeleted());
		deleteTestForumPost(post1);
	}

	@Test
	public void toggleForumPostDelete_setNotDeleted_success() throws Exception {
		post = newForumPost(CATEGORY_ID_1);
		forumService.toggleForumPostDelete(post.getId());
		ForumPost post1 = forumService.toggleForumPostDelete(post.getId());
		assertFalse(post1.getDeleted());
		deleteTestForumPost(post1);
	}

	@Test
	public void toggleFlaggedStatus_setFlagged_success() throws Exception {
		user = newUser(USER_ID_1);
		post = newForumPost(CATEGORY_ID_1);
		ForumPost post1 = forumService.toggleFlaggedStatus(post.getId(), user.getId());
		assertTrue(post1.getFlagged());
		deleteTestForumPost(post1);
	}

	@Test
	public void getAllFlaggedPosts_success() throws Exception {
		user = newUser(USER_ID_1);
		flaggedPost = newForumPost(CATEGORY_ID_1);
		flaggedPost.setFlagged(true);
		newForumPostFlaggedAssociation(flaggedPost, user);

		ForumFlaggedPostPagination flaggedPosts = new ForumFlaggedPostPagination();
		forumService.getAllFlaggedPostsStatistics(flaggedPosts);
		assertFalse(flaggedPosts.getResults().isEmpty());
	}

	@Test
	public void getAllBannedUsers_success() throws Exception {
		user = newUser(USER_ID_1);
		user3 = newUser(USER_ID_3);

		ban = newUserForumBan(user3);
		ban2 = newUserForumBan(user);

		UserForumBanPagination userForumBanPagination = new UserForumBanPagination(true);
		userForumBanPagination = forumService.getAllBannedUsers(userForumBanPagination);
		List<UserForumBan> results = userForumBanPagination.getResults();
		assertTrue(results.contains(ban));
		assertTrue(results.contains(ban2));

		deleteTestUserForumBan(ban);
		deleteTestUserForumBan(ban2);
	}

	@Test
	public void unbanUser_success() throws Exception {
		user3 = newUser(USER_ID_3);
		ban = newUserForumBan(user3);

		UserForumBan ban3 = forumService.unbanUser(ban.getBannedUser().getId());
		assertTrue(ban3.getDeleted());
		deleteTestUserForumBan(ban3);
	}

	@Test
	public void banUser_byEmail_success() throws Exception {
		user2 = newUser(USER_ID_2);
		UserForumBan ban3 = forumService.banUser(user2.getEmail(), "reason");
		assertNotNull(ban3.getId());
		assertEquals(user2, ban3.getBannedUser());
		deleteTestUserForumBan(ban3);
	}

	@Test
	public void banUser_byId_success() throws Exception {
		user2 = newUser(USER_ID_2);
		UserForumBan ban3 = forumService.banUser(user2.getId(), "reason");
		assertNotNull(ban3.getId());
		assertEquals(user2, ban3.getBannedUser());
		deleteTestUserForumBan(ban3);
	}

	@Test
	public void unbanUser_usingForumService_success() throws Exception {
		user2 = newUser(USER_ID_2);

		UserForumBan ban3 = forumService.banUser(user2.getId(), "reason");
		assertNotNull(ban3.getId());
		assertEquals(user2, ban3.getBannedUser());
		forumService.unbanUser(user2.getId());
		assertEquals(forumService.isUserBanned(user2.getId()), false);
		deleteTestUserForumBan(ban3);
	}

	@Test
	public void banUser_deletedBan_success() throws Exception {
		user = newUser(USER_ID_1);
		UserForumBan ban3 = forumService.banUser(user.getId(), "reason2");
		assertFalse(ban3.getDeleted());
		assertEquals("reason2", ban3.getReason());
		deleteTestUserForumBan(ban3);
	}

	@Test
	public void getBannedUsersInThread_success() throws Exception {
		user3 = newUser(USER_ID_3);
		post2 = newForumPost(CATEGORY_ID_2);

		authenticationService.setCurrentUser(user3);
		reply4 = newForumPostReply(post2.getId(), CATEGORY_ID_1);
		authenticationService.setCurrentUser(Constants.WORKMARKET_SYSTEM_USER_ID);

		UserForumBan ban = newUserForumBan(user3);

		Set<Long> users = forumService.getBannedUsersInThread(post2.getId());
		assertTrue(users.contains(user3.getId()));

		deleteTestUserForumBan(ban);
	}

	@Test
	public void getFlaggedPostsInThreadByUser_success() throws Exception {
		user = newUser(USER_ID_1);
		post = newForumPost(CATEGORY_ID_1);
		reply = newForumPostReply(post.getId(), CATEGORY_ID_1);
		ForumPost reply5 = forumService.toggleFlaggedStatus(reply.getId(), user.getId());
		Set<Long> posts = forumService.getUserFlaggedPostsInThread(post.getId(), user.getId());
		assertTrue(posts.contains(reply5.getId()));
		deleteTestForumPost(reply5);
	}

	private void deleteTestForumPost(ForumPost post) {
		if (post.getParentId() == null) {
			deleteTestForumPostFollowers(post);
			deleteTestForumPostGrandchildren(post);
			deleteTestForumPostChildren(post);
		}
		deleteTestForumPostUserAssociations(post);
		deleteTestForumPostTags(post);
		deleteTestForumPostHistory(post);

		String sql = "DELETE FROM forum_post WHERE id = :postId";
		MapSqlParameterSource params = new MapSqlParameterSource();
		params.addValue("postId", post.getId());
		jdbcTemplate.update(sql, params);
	}

	private void deleteTestForumPostTags(ForumPost post) {
		String sql = "DELETE FROM forum_post_tag_association WHERE post_id = :postId";
		MapSqlParameterSource params = new MapSqlParameterSource();
		params.addValue("postId", post.getId());
		jdbcTemplate.update(sql, params);
	}

	private void deleteTestForumPostHistory(ForumPost post) {
		String sql = "DELETE FROM forum_post_edit_history WHERE post_id = :postId";
		MapSqlParameterSource params = new MapSqlParameterSource();
		params.addValue("postId", post.getId());
		jdbcTemplate.update(sql, params);
	}

	private void deleteTestForumPostChildren(ForumPost post) {
		String sql = "DELETE FROM forum_post WHERE parent_id = :postId";
		MapSqlParameterSource params = new MapSqlParameterSource();
		params.addValue("postId", post.getId());
		jdbcTemplate.update(sql, params);
	}

	private void deleteTestForumPostGrandchildren(ForumPost post) {
		String sql = "DELETE FROM forum_post WHERE root_id = :postId and parent_id != :postId";
		MapSqlParameterSource params = new MapSqlParameterSource();
		params.addValue("postId", post.getId());
		jdbcTemplate.update(sql, params);
	}

	private void deleteTestForumPostFollower(ForumPostFollower follower) {
		String sql = "DELETE FROM forum_post_follower WHERE id = :followerId";
		MapSqlParameterSource params = new MapSqlParameterSource();
		params.addValue("followerId", follower.getId());
		jdbcTemplate.update(sql, params);
	}

	private void deleteTestForumPostFollowers(ForumPost post) {
		String sql = "DELETE FROM forum_post_follower WHERE post_id = :postId ";
		MapSqlParameterSource params = new MapSqlParameterSource();
		params.addValue("postId", post.getId());
		jdbcTemplate.update(sql, params);
	}

	private void deleteTestForumPostUserAssociations(ForumPost post) {
		String sql = "DELETE FROM forum_post_user_association WHERE post_id = :postId";
		MapSqlParameterSource params = new MapSqlParameterSource();
		params.addValue("postId", post.getId());
		jdbcTemplate.update(sql, params);
	}

	private void deleteTestUserForumBan(UserForumBan ban) {
		String sql = "DELETE FROM user_forum_ban WHERE id = :banId";
		MapSqlParameterSource params = new MapSqlParameterSource();
		params.addValue("banId", ban.getId());
		jdbcTemplate.update(sql, params);
	}

	private ForumPost newForumPost(Long categoryId) {
		ForumPostForm post = new ForumPostForm();
		post.setTitle("title");
		post.setComment("comment");
		post.setCategoryId(categoryId);
		return forumService.savePost(post);
	}

	private ForumPost newForumPostReply(Long parentId, Long categoryId) {
		ForumPostForm post = new ForumPostForm();
		post.setComment("reply");
		post.setParentId(parentId);
		post.setRootId(parentId);
		post.setCategoryId(categoryId);
		return forumService.saveReply(post);
	}

	private ForumPost newForumPostNestedReply(Long rootId, Long parentId, Long categoryId) {
		ForumPostForm post = new ForumPostForm();
		post.setComment("nested reply");
		post.setParentId(parentId);
		post.setRootId(rootId);
		post.setCategoryId(categoryId);
		return forumService.saveReply(post);
	}

	private ForumPost forumPostEdit(Long postId, String title, String comment) {
		return forumService.savePost(postId, title, comment);
	}

	private ForumPost forumPostReplyEdit(Long postId, String comment) {
		return forumService.savePost(postId, comment);
	}

	private ForumCategory newForumCategory(long categoryId) {
		return forumService.getForumCategory(categoryId);
	}

	private User newUser(long userId) {
		return userService.getUser(userId);
	}

	private ForumPostFollower newForumPostFollower(ForumPost post, User user) {
		return forumService.toggleFollower(post.getId(), user.getId());
	}

	private void newForumPostFlaggedAssociation(ForumPost flaggedPost, User user) {
		forumService.toggleFlaggedStatus(flaggedPost.getId(), user.getId());
	}

	private UserForumBan newUserForumBan(User user){
		return forumService.banUser(user.getId(), "reason");
	}

}
