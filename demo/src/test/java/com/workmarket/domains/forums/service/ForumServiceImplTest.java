package com.workmarket.domains.forums.service;

import com.workmarket.common.service.helpers.ServiceMessageHelper;
import com.workmarket.configuration.Constants;
import com.workmarket.dao.UserDAO;
import com.workmarket.domains.forums.dao.ForumCategoryDAO;
import com.workmarket.domains.forums.dao.ForumPostDAO;
import com.workmarket.domains.forums.dao.ForumPostFollowerDAO;
import com.workmarket.domains.forums.dao.ForumPostUserAssociationDAO;
import com.workmarket.domains.forums.dao.UserForumBanDAO;
import com.workmarket.domains.forums.model.ForumCategory;
import com.workmarket.domains.forums.model.ForumFlaggedPostPagination;
import com.workmarket.domains.forums.model.ForumPost;
import com.workmarket.domains.forums.model.ForumPostFollower;
import com.workmarket.domains.forums.model.ForumPostPagination;
import com.workmarket.domains.forums.model.ForumPostUserAssociation;
import com.workmarket.domains.forums.model.UserForumBan;
import com.workmarket.domains.forums.model.UserForumBanPagination;
import com.workmarket.domains.forums.service.event.NotifyPostFollowerEvent;
import com.workmarket.domains.model.User;
import com.workmarket.domains.work.facade.service.WorkFacadeService;
import com.workmarket.domains.work.model.Work;
import com.workmarket.domains.work.service.WorkService;
import com.workmarket.service.business.UserNotificationService;
import com.workmarket.service.business.UserService;
import com.workmarket.service.business.dto.WorkDTO;
import com.workmarket.service.business.event.forums.CreateWorkFromFlaggedPostEvent;
import com.workmarket.service.infra.EnvironmentDetectionService;
import com.workmarket.service.infra.business.AuthenticationService;
import com.workmarket.service.infra.event.EventRouter;
import com.workmarket.service.infra.event.transactional.EventService;
import com.workmarket.utility.RandomUtilities;
import com.workmarket.web.forms.forums.ForumPostForm;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.LinkedList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class ForumServiceImplTest {

	@Mock ForumPostDAO postDAO;
	@Mock ForumCategoryDAO categoryDAO;
	@Mock ForumPostFollowerDAO followerDAO;
	@Mock UserDAO userDAO;
	@Mock AuthenticationService authenticationService;
	@Mock UserNotificationService userNotificationService;
	@Mock ForumPostUserAssociationDAO flaggedDAO;
	@Mock UserForumBanDAO banDAO;
	@Mock EventRouter eventRouter;
	@Mock UserService userService;
	@Mock WorkService workService;
	@Mock WorkFacadeService workFacadeService;
	@Mock EnvironmentDetectionService environmentDetectionService;
	@Mock EventService eventService;
	@Mock ServiceMessageHelper serviceMessageHelper;

	@InjectMocks ForumServiceImpl forumService;

	private User user;
	private ForumPost post, reply;
	private ForumCategory category;
	private ForumPostForm postForm, replyForm;
	private ForumPostFollower follower;
	private ForumPostPagination forumPostPagination;
	private UserForumBanPagination userForumBanPagination;
	private ForumFlaggedPostPagination forumFlaggedPostPagination;
	private List<ForumCategory> categoryList;
	private List<ForumPostFollower> followerList;
	private ForumPostUserAssociation flaggedAssociation;
	private UserForumBan ban;

	private static final long  USER_ID = 1, CATEGORY_ID = 1, POST_ID = 1, REPLY_ID = 2, USER_BAN_ID = 1, WORK_ID = 1;
	private static final String EMAIL = "user@user.com", REASON = "banned", COMMENT = "comment", TITLE = "title";

	@Before
	public void setUp() throws Exception {
		categoryList = new LinkedList<>();
		followerList = new LinkedList<>();
		List<ForumPostUserAssociation> flaggedList = new LinkedList<>();
		List<Long> idList = new LinkedList<>();

		flaggedList.add(new ForumPostUserAssociation());
		flaggedList.add(new ForumPostUserAssociation());

		idList.add(1L);
		idList.add(2L);

		forumPostPagination = mock(ForumPostPagination.class);
		userForumBanPagination = mock(UserForumBanPagination.class);
		forumFlaggedPostPagination = mock(ForumFlaggedPostPagination.class);
		flaggedAssociation = mock(ForumPostUserAssociation.class);

		user = mock(User.class, RETURNS_DEEP_STUBS);
		when(user.getId()).thenReturn(Constants.WORKMARKET_SYSTEM_USER_ID);
		when(user.getCompany().getId()).thenReturn(Constants.WM_COMPANY_ID);

		category = mock(ForumCategory.class);

		post = mock(ForumPost.class);
		when(post.getId()).thenReturn(POST_ID);
		when(post.getCategoryId()).thenReturn(CATEGORY_ID);
		when(post.getParentId()).thenReturn(null);
		when(post.getDeleted()).thenReturn(false);
		when(post.getCreatorId()).thenReturn(USER_ID);
		when(post.getComment()).thenReturn(COMMENT);
		when(post.getTitle()).thenReturn(TITLE);
		when(post.isReply()).thenReturn(false);
		when(post.getRootId()).thenReturn(POST_ID);

		postForm = mock(ForumPostForm.class);
		when(postForm.getCategoryId()).thenReturn(CATEGORY_ID);
		when(postForm.getParentId()).thenReturn(null);
		when(postForm.toPost()).thenReturn(post);

		reply = mock(ForumPost.class);
		when(reply.getId()).thenReturn(REPLY_ID);
		when(reply.getParentId()).thenReturn(POST_ID);
		when(reply.getCategoryId()).thenReturn(CATEGORY_ID);

		replyForm = mock(ForumPostForm.class);
		when(postForm.getCategoryId()).thenReturn(CATEGORY_ID);
		when(postForm.getParentId()).thenReturn(POST_ID);
		when(replyForm.toPost()).thenReturn(reply);

		follower = mock(ForumPostFollower.class);
		when(follower.getPost()).thenReturn(post);
		when(follower.getFollowerUser()).thenReturn(user);
		when(follower.getDeleted()).thenReturn(false);
		when(follower.isFollowing()).thenReturn(true);

		ban = mock(UserForumBan.class);
		when(ban.getDeleted()).thenReturn(false);

		Work work = mock(Work.class);
		when(work.getId()).thenReturn(WORK_ID);

		when(postDAO.get(anyLong())).thenReturn(post);
		when(postDAO.findAllCategoryPosts(CATEGORY_ID, forumPostPagination)).thenReturn(forumPostPagination);
		when(postDAO.findAllPostReplies(POST_ID, forumPostPagination)).thenReturn(forumPostPagination);
		when(postDAO.findAllFollowingPosts(USER_ID, forumPostPagination)).thenReturn(forumPostPagination);
		when(postDAO.findAllFollowingPostsByCategory(USER_ID, CATEGORY_ID, forumPostPagination)).thenReturn(forumPostPagination);

		when(categoryDAO.getAll()).thenReturn(categoryList);
		when(categoryDAO.get(anyLong())).thenReturn(category);

		when(banDAO.getBannedUser(USER_ID)).thenReturn(null);
		when(banDAO.get(USER_BAN_ID)).thenReturn(ban);
		when(banDAO.getAllBannedUsers(userForumBanPagination)).thenReturn(userForumBanPagination);

		when(userService.findUserById(USER_ID)).thenReturn(user);
		when(userService.findUserByEmail(EMAIL)).thenReturn(user);
		when(userDAO.getUser(USER_ID)).thenReturn(user);

		when(flaggedDAO.getUserFlaggedPostsInThread(POST_ID, USER_ID)).thenReturn(idList);
		when(flaggedDAO.getFlaggedPostByUser(POST_ID, USER_ID)).thenReturn(flaggedAssociation);
		when(flaggedDAO.getUserFlaggedPostsInThread(POST_ID, USER_ID)).thenReturn(idList);
		when(flaggedDAO.getAllFlaggedPostStatistics(forumFlaggedPostPagination)).thenReturn(forumFlaggedPostPagination);
		when(flaggedDAO.getAllFlagsByPostId(POST_ID)).thenReturn(flaggedList);

		when(followerDAO.getPostFollowers(POST_ID)).thenReturn(followerList);
		when(followerDAO.getPostsFollowedByUser(USER_ID)).thenReturn(followerList);
		when(followerDAO.getPostFollowerByUserAndPostNotDeleted(POST_ID, USER_ID)).thenReturn(follower);
		when(followerDAO.getPostFollowerByUserAndPost(POST_ID, USER_ID)).thenReturn(follower);

		when(authenticationService.getCurrentUser()).thenReturn(user);
		when(authenticationService.getCurrentUserId()).thenReturn(USER_ID);
		when(workFacadeService.saveOrUpdateWork(eq(Constants.WORKMARKET_SYSTEM_USER_ID), any(WorkDTO.class))).thenReturn(work);
		when(environmentDetectionService.isProd()).thenReturn(false);

		when(serviceMessageHelper.getMessage(anyString(), any())).thenReturn(RandomUtilities.generateAlphaString(1));

		when(forumService.getFollowersOnPost(anyLong())).thenReturn(followerList);
	}

	@Test
	public void getCategoryList_success() {
		forumService.getCategoryList();

		verify(categoryDAO, times(1)).getAll();
	}

	@Test
	public void getCategoryList_returnsExpected_success() {
		List<ForumCategory> actualResult = forumService.getCategoryList();

		assertEquals(categoryList, actualResult);
	}

	@Test
	public void getCategorybyId_success() {
		forumService.getForumCategory(CATEGORY_ID);

		verify(categoryDAO, times(1)).get(CATEGORY_ID);
	}

	@Test
	public void getCategoryById_returnsExpected_success() {
		ForumCategory actualResult = forumService.getForumCategory(CATEGORY_ID);

		assertEquals(category, actualResult);
	}

	@Test
	public void getCategoryPosts_success() {
		forumService.getCategoryPosts(CATEGORY_ID, forumPostPagination);

		verify(postDAO, times(1)).findAllCategoryPosts(CATEGORY_ID, forumPostPagination);
	}

	@Test
	public void getCategoryPosts_returnsExpected_success() {
		ForumPostPagination actualResult = forumService.getCategoryPosts(CATEGORY_ID, forumPostPagination);

		assertEquals(forumPostPagination, actualResult);
	}

	@Test
	public void getPostReplies_success() {
		forumService.getPostReplies(POST_ID, forumPostPagination);

		verify(postDAO, times(1)).findAllPostReplies(POST_ID, forumPostPagination);
	}

	@Test
	public void getPostReplies_returnsExpected_success() {
		ForumPostPagination actualResult = forumService.getPostReplies(POST_ID, forumPostPagination);

		assertEquals(forumPostPagination, actualResult);
	}

	@Test
	public void savePostForm_toPost_success() {
		forumService.savePost(postForm);

		verify(postForm, times(1)).toPost();
	}

	@Test
	public void savePostForm_returnsExpected_success() {
		ForumPost actualResult = forumService.savePost(postForm);

		assertEquals(post, actualResult);
	}

	@Test
	public void saveReplyForm_notifyFollowersEvent_success() {
		forumService.saveReply(replyForm);

		verify(eventService, times(1)).processEvent(any(NotifyPostFollowerEvent.class));
	}

	@Test
	public void saveReplyForm_toReply_success() {
		forumService.saveReply(replyForm);

		verify(replyForm, times(1)).toPost();
	}

	@Test
	public void saveReplyForm_returnsExpected_success() {
		ForumPost actualResult = forumService.saveReply(replyForm);

		assertEquals(reply, actualResult);
	}

	@Test
	public void getPost_NoMetadataImplicit_success() throws Exception {
		forumService.getPostById(POST_ID);

		verify(postDAO, times(1)).get(POST_ID);
	}

	@Test
	public void getPost_Metadata_success() throws Exception {
		forumService.getPostById(POST_ID);
		verify(postDAO, times(1)).get(POST_ID);
	}

	@Test
	public void getPost_returnsExpected_success() throws Exception {
		ForumPost actualResult = forumService.getPostById(POST_ID);

		assertEquals(post, actualResult);
	}

	@Test
	public void getFollowersOnPost_hasFollowers_success() throws Exception {
		forumService.getFollowersOnPost(POST_ID);

		verify(followerDAO, times(1)).getPostFollowers(POST_ID);
	}

	@Test
	public void getFollowersOnPost_hasFollowersReturnsExpected_success() throws Exception {
		List<ForumPostFollower> actualResult = forumService.getFollowersOnPost(POST_ID);

		assertEquals(followerList, actualResult);
	}

	@Test
	public void getPostsUserIsFollowing_isFollowing_success() throws Exception {
		forumService.getPostsUserIsFollowing(USER_ID);

		verify(followerDAO, times(1)).getPostsFollowedByUser(USER_ID);
	}

	@Test
	public void getPostsUserIsFollowing_isFollowingReturnsExpected_success() throws Exception {
		List<ForumPostFollower> actualResult = forumService.getPostsUserIsFollowing(USER_ID);

		assertEquals(followerList, actualResult);
	}

	@Test
	public void toggleFollower_getUserAndPost_success() throws Exception {
		forumService.toggleFollower(POST_ID, USER_ID);

		verify(userDAO, times(1)).getUser(USER_ID);
		verify(postDAO, times(1)).get(POST_ID);
	}

	@Test
	public void toggleFollower_newFollower_success() throws Exception {
		when(followerDAO.getPostFollowerByUserAndPost(POST_ID, USER_ID)).thenReturn(null);
		forumService.toggleFollower(POST_ID, USER_ID);
		verify(followerDAO, times(1)).getPostFollowerByUserAndPost(POST_ID, USER_ID);
	}

	@Test
	public void toggleFollower_alreadyFollower_success() throws Exception {
		forumService.toggleFollower(POST_ID, USER_ID);
		verify(follower, times(1)).getDeleted();
	}

	@Test
	public void toggleFollower_returnsExpected_success() throws Exception {
		ForumPostFollower actualResult = forumService.toggleFollower(POST_ID, USER_ID);
		assertEquals(follower, actualResult);
	}

	@Test
	public void getFollowedPosts_isFollowing_success() throws Exception {
		forumService.getFollowedPost(USER_ID, forumPostPagination);
		verify(postDAO, times(1)).findAllFollowingPosts(USER_ID, forumPostPagination);
	}

	@Test
	public void getFollowedPosts_byCategory_success() throws Exception {
		forumService.getFollowedPost(USER_ID, CATEGORY_ID, forumPostPagination);
		verify(postDAO, times(1)).findAllFollowingPostsByCategory(USER_ID, CATEGORY_ID, forumPostPagination);
	}

	@Test
	public void getFollowerPosts_returnsExpected_success() throws Exception {
		ForumPostPagination actualResult = forumService.getFollowedPost(USER_ID, forumPostPagination);
		assertEquals(forumPostPagination, actualResult);
	}

	@Test
	public void isFollowerOnPost_success() throws Exception {
		forumService.isFollowerOnPost(POST_ID, USER_ID);
		verify(followerDAO, times(1)).getPostFollowerByUserAndPostNotDeleted(POST_ID, USER_ID);
	}
	@Test
	public void isFollowerOnPost_isFollower_success() throws Exception {
		boolean actualResult = forumService.isFollowerOnPost(POST_ID, USER_ID);
		assertEquals(true, actualResult);
	}

	@Test
	public void isFollowerOnPost_notFollower_success() throws Exception {
		when(followerDAO.getPostFollowerByUserAndPostNotDeleted(POST_ID, USER_ID)).thenReturn(null);
		boolean actualResult = forumService.isFollowerOnPost(POST_ID, USER_ID);
		assertEquals(false, actualResult);
	}

	@Test
	public void getAllFlaggedPosts_success() throws Exception {
		forumService.getAllFlaggedPostsStatistics(forumFlaggedPostPagination);
		verify(flaggedDAO, times(1)).getAllFlaggedPostStatistics(forumFlaggedPostPagination);
	}

	@Test
	public void getAllBannedUsers_success() throws Exception {
		forumService.getAllBannedUsers(userForumBanPagination);
		verify(banDAO, times(1)).getAllBannedUsers(userForumBanPagination);
	}

	@Test
	public void getAllBannedUsers_expectedResult_success() throws Exception {
		UserForumBanPagination actualResult = forumService.getAllBannedUsers(userForumBanPagination);
		assertEquals(userForumBanPagination, actualResult);
	}

	@Test
	public void isUserBanned_banned_success() throws Exception {
		when(banDAO.getBannedUser(USER_ID)).thenReturn(ban);
		boolean actualResult = forumService.isUserBanned(USER_ID);
		assertEquals(true, actualResult);
	}

	@Test
	public void banUser_withEmail_success() throws Exception {
		forumService.banUser(EMAIL, REASON);
		verify(userService, times(1)).findUserByEmail(EMAIL);
	}

	@Test
	public void banUser_withId_success() throws Exception {
		forumService.banUser(USER_ID, REASON);
		verify(userService, times(1)).findUserById(USER_ID);
	}

	@Test
	public void banUser_newBan_success() throws Exception {
		when(banDAO.getBannedUser(USER_ID)).thenReturn(null);
		forumService.banUser(USER_ID, REASON);
		verify(banDAO, never()).saveOrUpdate(ban);
	}

	@Test
	public void banUser_restoreBan_success() throws Exception {
		when(banDAO.getBannedUser(USER_ID)).thenReturn(ban);
		when(ban.getDeleted()).thenReturn(true);

		forumService.banUser(USER_ID, REASON);

		verify(ban, times(1)).setDeleted(false);
	}

	@Test
	public void banUser_nothingDone_success() throws Exception {
		forumService.banUser(USER_ID, REASON);
		verify(ban, times(0)).setReason(REASON);
	}

	@Test
	public void banUser_expectedResult_success() throws Exception {
		when(banDAO.getBannedUser(USER_ID)).thenReturn(ban);
		UserForumBan actualResult = forumService.banUser(USER_ID, REASON);
		assertEquals(ban, actualResult);
	}

	@Test
	public void unbanUser_expectedResult_success() throws Exception {
		when(banDAO.getBannedUser(USER_ID)).thenReturn(ban);
		UserForumBan actualResult = forumService.unbanUser(USER_ID);
		assertEquals(ban, actualResult);
	}

	@Test
	public void getBannedUsersInThread_getBannedUsers_success() throws Exception {
		forumService.getBannedUsersInThread(POST_ID);
		verify(banDAO, times(1)).getAllBannedUsersOnPost(POST_ID);
	}

	@Test
	public void toggleForumPostDelete_deletePost_success() throws Exception {
		forumService.toggleForumPostDelete(POST_ID);
		verify(flaggedDAO, times(1)).getAllFlagsByPostId(POST_ID);
	}

	@Test
	public void toggleForumPostDelete_restorePost_success() throws Exception {
		when(post.getDeleted()).thenReturn(true);
		forumService.toggleForumPostDelete(POST_ID);
		verify(post, times(1)).setDeleted(false);
	}

	@Test
	public void toggleFlaggedStatus_createNewFlag_success() throws Exception {
		when(flaggedDAO.getFlaggedPostByUser(POST_ID, USER_ID)).thenReturn(null);
		forumService.toggleFlaggedStatus(POST_ID, USER_ID);
		verify(flaggedDAO, never()).saveOrUpdate(flaggedAssociation);
	}

	@Test
	public void toggleFlaggedStatus_newFlag_success() throws Exception {
		forumService.toggleFlaggedStatus(POST_ID, USER_ID);
		verify(post, times(1)).setFlagged(Boolean.TRUE);
	}

	@Test
	public void toggleFlaggedStatus_sendWorkEvent_success() throws Exception {
		forumService.toggleFlaggedStatus(POST_ID, USER_ID);
		verify(eventService, times(1)).processEvent(any(CreateWorkFromFlaggedPostEvent.class));
	}

	@Test
	public void toggleFlaggedStatus_returnsExpected_success() throws Exception {
		ForumPost actualResult = forumService.toggleFlaggedStatus(POST_ID, USER_ID);
		assertEquals(post, actualResult);
	}

	@Test
	public void getFlaggedPostsInThreadByUser_success() throws Exception {
		forumService.getUserFlaggedPostsInThread(POST_ID, USER_ID);
		verify(flaggedDAO, times(1)).getUserFlaggedPostsInThread(POST_ID, USER_ID);
	}

	@Test
	public void notifyPostFollowers_success() throws Exception {
		forumService.notifyPostFollowersEvent(reply);
		verify(userNotificationService, times(1)).onForumCommentAdded(reply, post);
	}

	@Test
	public void isUserAdmin_userIsWMUser_success() throws Exception {
		when(user.getCompany().getId()).thenReturn(Constants.WM_COMPANY_ID);
		assertTrue(forumService.isUserAdmin(user));
	}

	@Test
	public void isUserAdmin_userIsWMServiceUser_success() throws Exception {
		when(user.getCompany().getId()).thenReturn(Constants.WM_SUPPORT_COMPANY_ID);
		assertTrue(forumService.isUserAdmin(user));
	}

	@Test
	public void isUserAdmin_userIsNotAdmin_failure() throws Exception {
		when(user.getCompany().getId()).thenReturn(0L);
		assertFalse(forumService.isUserAdmin(user));
	}
}
