package com.workmarket.domains.forums.service;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.workmarket.common.service.helpers.ServiceMessageHelper;
import com.workmarket.configuration.Constants;
import com.workmarket.dao.UserDAO;
import com.workmarket.dao.asset.UserAssetAssociationDAO;
import com.workmarket.domains.forums.dao.ForumCategoryDAO;
import com.workmarket.domains.forums.dao.ForumPostDAO;
import com.workmarket.domains.forums.dao.ForumPostEditHistoryDAO;
import com.workmarket.domains.forums.dao.ForumPostFollowerDAO;
import com.workmarket.domains.forums.dao.ForumPostTagAssociationDAO;
import com.workmarket.domains.forums.dao.ForumPostUserAssociationDAO;
import com.workmarket.domains.forums.dao.UserForumBanDAO;
import com.workmarket.domains.forums.model.FlaggedPostStatistics;
import com.workmarket.domains.forums.model.ForumCategory;
import com.workmarket.domains.forums.model.ForumFlaggedPostPagination;
import com.workmarket.domains.forums.model.ForumPost;
import com.workmarket.domains.forums.model.ForumPostEditHistory;
import com.workmarket.domains.forums.model.ForumPostFollower;
import com.workmarket.domains.forums.model.ForumPostPagination;
import com.workmarket.domains.forums.model.ForumPostTagAssociation;
import com.workmarket.domains.forums.model.ForumPostUserAssociation;
import com.workmarket.domains.forums.model.ForumTag;
import com.workmarket.domains.forums.model.UserForumBan;
import com.workmarket.domains.forums.model.UserForumBanPagination;
import com.workmarket.domains.forums.service.event.NotifyPostFollowerEvent;
import com.workmarket.domains.model.User;
import com.workmarket.domains.model.asset.UserAssetAssociation;
import com.workmarket.domains.model.pricing.PricingStrategyType;
import com.workmarket.domains.work.facade.service.WorkFacadeService;
import com.workmarket.domains.work.model.Work;
import com.workmarket.domains.work.service.WorkService;
import com.workmarket.service.business.UserNotificationService;
import com.workmarket.service.business.UserService;
import com.workmarket.service.business.dto.WorkDTO;
import com.workmarket.service.business.event.forums.CreateWorkFromFlaggedPostEvent;
import com.workmarket.service.business.event.user.UserSearchIndexEvent;
import com.workmarket.service.business.event.work.WorkUpdateSearchIndexEvent;
import com.workmarket.service.infra.EnvironmentDetectionService;
import com.workmarket.service.infra.business.AuthenticationService;
import com.workmarket.service.infra.event.EventRouter;
import com.workmarket.service.infra.event.transactional.EventService;
import com.workmarket.web.forms.forums.ForumPostForm;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.util.Calendar;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
public class ForumServiceImpl implements ForumService {

	@Autowired private ForumCategoryDAO categoryDAO;
	@Autowired private ForumPostDAO postDAO;
	@Autowired private ForumPostEditHistoryDAO editDAO;
	@Autowired private ForumPostTagAssociationDAO tagDAO;
	@Autowired private ForumPostUserAssociationDAO flaggedDAO;
	@Autowired private UserForumBanDAO banDAO;
	@Autowired private ForumPostFollowerDAO followerDAO;
	@Autowired private UserAssetAssociationDAO userAssetAssociationDAO;

	@Autowired private UserDAO userDAO;
	@Autowired private UserService userService;
	@Autowired private AuthenticationService authenticationService;
	@Autowired private EventRouter eventRouter;
	@Autowired private WorkService workService;
	@Autowired private WorkFacadeService workFacadeService;
	@Autowired private UserNotificationService userNotificationService;
	@Autowired private EnvironmentDetectionService environmentDetectionService;
	@Autowired private EventService eventService;
	@Autowired private ServiceMessageHelper serviceMessageHelper;

	@Override
	public List<ForumCategory> getCategoryList() {
		return categoryDAO.getAll();
	}

	@Override
	public ForumCategory getForumCategory(Long categoryId) {
		Assert.notNull(categoryId);

		return categoryDAO.get(categoryId);
	}

	@Override
	public ForumPostPagination getCategoryPosts(Long categoryId, ForumPostPagination pagination) {
		Assert.notNull(categoryId);
		Assert.notNull(pagination);

		return postDAO.findAllCategoryPosts(categoryId, pagination);
	}

	@Override
	public ForumPostPagination getPostReplies(Long rootId, ForumPostPagination pagination) {
		Assert.notNull(rootId);
		Assert.notNull(pagination);

		return postDAO.findAllPostReplies(rootId, pagination);
	}

	@Override
	public ForumPost toggleForumPostDelete(Long postId) {
		Assert.notNull(postId);

		ForumPost post = postDAO.get(postId);
		Assert.notNull(post);

		post.setFlagged(false);
		if (!post.getDeleted()) {
			List<ForumPostUserAssociation> associations = flaggedDAO.getAllFlagsByPostId(postId);
			for (ForumPostUserAssociation association : associations) {
				association.setDeleted(true);
			}
		}
		post.setDeleted(!post.getDeleted());

		return post;
	}

	@Override
	public ForumPost getPostById(Long id) {
		Assert.notNull(id);

		return postDAO.get(id);
	}

	@Override
	public ForumPost savePost(ForumPostForm form) {
		Assert.notNull(form);
		if (!isUserBanned(authenticationService.getCurrentUserId())) {
			ForumPost post = form.toPost();
			post.setLastPostOn(Calendar.getInstance());
			post.setAdmin(isUserAdmin(authenticationService.getCurrentUser()));
			postDAO.saveOrUpdate(post);
			if (form.getTags() != null) {
				Set<ForumPostTagAssociation> tags = Sets.newHashSet();
				for (String tag : form.getTags()) {
					if (ForumTag.getTagByName(tag) != null) {
						ForumPostTagAssociation a = new ForumPostTagAssociation(post, ForumTag.getTagByName(tag));
						tags.add(a);
						tagDAO.saveOrUpdate(a);
					}
				}
				post.setForumPostTags(tags);
			}
			toggleFollower(post, post.getCreatorId());
			return post;
		}

		return null;
	}

	public ForumPost savePost(Long postId, String newTitle, String newPost) {
		Assert.notNull(postId);

		if (isUserBanned(authenticationService.getCurrentUserId())) {
			return null;
		}

		ForumPost post = this.getPostById(postId);

		if (newPost.equals(post.getComment()) && newTitle.equals(post.getTitle())) {
			return null;
		}

		ForumPostEditHistory history = new ForumPostEditHistory(post, post.getTitle(), post.getComment());

		post.setTitle(newTitle);
		post.setComment(newPost);
		post.setEdited(true);

		postDAO.saveOrUpdate(post);
		editDAO.saveOrUpdate(history);

		return post;
	}


	public ForumPost savePost(Long postId, String newPost) {
		Assert.notNull(postId);

		if (!isUserBanned(authenticationService.getCurrentUserId())) {
			ForumPost post = this.getPostById(postId);

			if (post.getComment().equals(newPost)) {
				return null;
			}

			ForumPostEditHistory history = new ForumPostEditHistory(post, null, post.getComment());

			post.setComment(newPost);
			post.setEdited(true);

			postDAO.saveOrUpdate(post);
			editDAO.saveOrUpdate(history);

			return post;
		}

		return null;
	}

	@Override
	public ForumPost saveReply(ForumPostForm form) {
		Assert.notNull(form);

		if (!isUserBanned(authenticationService.getCurrentUserId())) {
			ForumPost post = form.toPost();
			post.setAdmin(isUserAdmin(authenticationService.getCurrentUser()));
			postDAO.saveOrUpdate(post);
			eventService.processEvent(new NotifyPostFollowerEvent(post));
			ForumPost parent = postDAO.get(post.getRootId());
			parent.setLastPostOn(Calendar.getInstance());
			postDAO.saveOrUpdate(parent);
			boolean following = false;
			Long currentUserId = authenticationService.getCurrentUserId();
			List<ForumPostFollower> followers = this.getFollowersOnPost(parent.getId());
			if (followers != null) {
				for (ForumPostFollower follower : followers) {
					if (follower.getFollowerUser().getId().equals(currentUserId)) {
						following = true;
						break;
					}
				}
				if (!following) {
					this.toggleFollower(parent, authenticationService.getCurrentUserId());
				}
			}
			return post;
		}

		return null;
	}

	@Override
	public ForumPost toggleFlaggedStatus(Long postId, Long userId) {
		Assert.notNull(postId);
		Assert.notNull(userId);

		ForumPostUserAssociation flaggedAssociation = flaggedDAO.getFlaggedPostByUser(postId, userId);
		ForumPost post = postDAO.get(postId);
		if (flaggedAssociation != null) {
			flaggedAssociation.setDeleted(false);
		} else {
			flaggedAssociation = new ForumPostUserAssociation();
			flaggedAssociation.setPost(post);
			flaggedAssociation.setUserId(userId);
		}

		flaggedDAO.saveOrUpdate(flaggedAssociation);

		if (!flaggedAssociation.getDeleted() && !post.getFlagged()) {
			post.setFlagged(true);
			eventService.processEvent(new CreateWorkFromFlaggedPostEvent(post));
			postDAO.saveOrUpdate(post);
		}

		return post;
	}

	@Override
	public ForumPost unflagPost(Long postId) {
		Assert.notNull(postId);

		ForumPost post = postDAO.get(postId);
		List<ForumPostUserAssociation> flags = flaggedDAO.getAllFlagsByPostId(postId);

		for (ForumPostUserAssociation flag : flags) {
			flag.setDeleted(true);
			flaggedDAO.saveOrUpdate(flag);
		}
		post.setFlagged(false);

		return post;
	}

	@Override
	public void createWorkFromFlaggedPostEvent(ForumPost post) {

		WorkDTO workDTO = new WorkDTO();

		if (post.isReply()) {
			workDTO.setDescription(serviceMessageHelper.getMessage("flaggedReply.description", post.getComment(), post.getId().toString(), post.getParentId().toString()));
		} else {
			workDTO.setDescription(serviceMessageHelper.getMessage("flaggedPost.description", post.getComment(), post.getId().toString()));
		}

		workDTO.setTitle(serviceMessageHelper.getMessage("flaggedPost.title", post.getId().toString()));
		workDTO.setIsOnsiteAddress(false);
		workDTO.setPricingStrategyId((long) (PricingStrategyType.INTERNAL.ordinal() + 1));
		workDTO.setIsScheduleRange(false);
		workDTO.setScheduleFromString(new DateTime().toString());
		workDTO.setIndustryId(Constants.WM_TIME_INDUSTRY_ID);
		workDTO.setUseMaxSpendPricingDisplayModeFlag(false);
		workDTO.setShowInFeed(false);

		if (environmentDetectionService.isProd()) {
			Work work = workFacadeService.saveOrUpdateWork(Constants.BRIDGET_QUINN_SUPPORT_USER_ID, workDTO);

			eventRouter.sendEvent(new WorkUpdateSearchIndexEvent(work.getId()));
			eventRouter.sendEvent(new UserSearchIndexEvent(Constants.BRIDGET_QUINN_SUPPORT_USER_ID));
		}

	}

	@Override
	public Set<Long> getUserFlaggedPostsInThread(Long postId, Long userId) {
		Assert.notNull(postId);
		Assert.notNull(userId);

		return Sets.newHashSet(flaggedDAO.getUserFlaggedPostsInThread(postId, userId));
	}

	@Override
	public void getAllFlaggedPostsStatistics(ForumFlaggedPostPagination pagination) {
		pagination = flaggedDAO.getAllFlaggedPostStatistics(pagination);

		if (pagination.getResults() != null) {
			for (FlaggedPostStatistics stat : pagination.getResults()) {
				ForumPost post = getPostById(stat.getPostId());
				stat.setRootId(post.getRootId());
				stat.setCreatorId(post.getCreatorId());
				stat.setCreatorName(userService.getFullName(stat.getCreatorId()));
				stat.setComment(post.getComment());
				stat.setIsCreatorBanned(isUserBanned(post.getCreatorId()));
			}
		}

	}

	@Override
	public UserForumBanPagination getAllBannedUsers(UserForumBanPagination pagination) {
		return banDAO.getAllBannedUsers(pagination);
	}

	@Override
	public boolean isUserBanned(Long userId) {
		UserForumBan ban = banDAO.getBannedUser(userId);
		return ban != null && !ban.getDeleted();
	}

	@Override
	public UserForumBan banUser(String email, String reason) {
		Assert.hasText(email);

		User user = userService.findUserByEmail(email);

		return banUser(user, reason);
	}

	@Override
	public UserForumBan banUser(Long userId, String reason) {
		Assert.notNull(userId);

		User user = userService.findUserById(userId);

		return banUser(user, reason);
	}

	@Override
	public UserForumBan unbanUser(Long userBanId) {
		Assert.notNull(userBanId);

		UserForumBan ban = banDAO.getBannedUser(userBanId);
		ban.setDeleted(true);

		return ban;
	}

	@Override
	public Set<Long> getBannedUsersInThread(Long postId) {
		Assert.notNull(postId);

		List<Long> userList = banDAO.getAllBannedUsersOnPost(postId);
		return new HashSet<>(userList);
	}

	private UserForumBan banUser(User user, String reason) {
		Assert.notNull(user);
		Assert.hasText(reason);

		UserForumBan ban = banDAO.getBannedUser(user.getId());
		if (ban == null) {
			ban = new UserForumBan();
			ban.setBannedUser(user);
		}
		ban.setDeleted(false);
		ban.setReason(reason);
		banDAO.saveOrUpdate(ban);
		return ban;
	}


	@Override
	public List<ForumPostFollower> getFollowersOnPost(Long postId) {
		Assert.notNull(postId);

		return followerDAO.getPostFollowers(postId);
	}

	@Override
	public List<ForumPostFollower> getPostsUserIsFollowing(Long userId) {
		Assert.notNull(userId);

		return followerDAO.getPostsFollowedByUser(userId);
	}

	@Override
	public ForumPostFollower toggleFollower(Long postId, Long userId) {
		Assert.notNull(postId);
		Assert.notNull(userId);

		ForumPost post = postDAO.get(postId);
		return toggleFollower(post, userId);
	}

	@Override
	public ForumPostPagination getFollowedPost(Long userId, ForumPostPagination pagination) {
		Assert.notNull(userId);
		Assert.notNull(pagination);

		return postDAO.findAllFollowingPosts(userId, pagination);
	}

	@Override
	public ForumPostPagination getFollowedPost(Long userId, Long categoryId, ForumPostPagination pagination) {
		Assert.notNull(userId);
		Assert.notNull(categoryId);
		Assert.notNull(pagination);

		return postDAO.findAllFollowingPostsByCategory(userId, categoryId, pagination);
	}

	@Override
	public boolean isFollowerOnPost(Long postId, Long userId) {
		Assert.notNull(postId);
		Assert.notNull(userId);

		ForumPostFollower follower = followerDAO.getPostFollowerByUserAndPostNotDeleted(postId, userId);

		return follower != null && follower.isFollowing();
	}

	@Override
	public void notifyPostFollowersEvent(ForumPost post) {
		Assert.notNull(post);
		Assert.notNull(post.getId());
		Assert.notNull(post.getParentId());

		ForumPost root = postDAO.get(post.getRootId());
		Assert.notNull(root);

		userNotificationService.onForumCommentAdded(post, root);
	}

	@Override
	public ForumCategory saveCategory(ForumCategory category) {
		Assert.notNull(category);

		categoryDAO.saveOrUpdate(category);

		return category;
	}

	public Set<ForumTag> getTagsOnPost(Long postId) {
		Set<ForumTag> results = Sets.newHashSet();
		for (ForumPostTagAssociation association : tagDAO.findAllPostTags(postId)) {
			results.add(association.getTag());
		}
		return results;
	}

	private ForumPostFollower toggleFollower(ForumPost post, Long userId) {
		Assert.notNull(post);
		Assert.notNull(userId);

		User user = userDAO.getUser(userId);
		Assert.notNull(user);

		ForumPostFollower follower = followerDAO.getPostFollowerByUserAndPost(post.getId(), user.getId());
		if (follower == null) {
			follower = new ForumPostFollower();
			follower.setFollowerUser(user);
			follower.setPost(post);
		} else {
			follower.setDeleted(!follower.getDeleted());
		}
		followerDAO.saveOrUpdate(follower);
		return follower;
	}

	@Override
	public Map<Long, String> getAvatarURIs(List<ForumPost> posts) throws Exception {
		if (posts == null) {
			return null;
		}
		Map<Long, String> uris = Maps.newHashMap();
		for (ForumPost post : posts) {
			if (!uris.containsKey(post.getCreatorId())) {
				uris.put(post.getCreatorId(), getAvatarURI(post));
			}
		}
		return uris;
	}

	@Override
	public String getAvatarURI(ForumPost post) throws Exception {
		UserAssetAssociation assets = userAssetAssociationDAO.findUserAvatars(post.getCreatorId());
		if (assets != null) {
			return assets.getTransformedSmallAsset().getCdnUri();
		} else {
			return null;
		}
	}

	@Override
	public Map<Long, String> getPostCreatorNames(List<ForumPost> posts) {
		if (posts == null) {
			return null;
		}
		Map<Long, String> names = Maps.newHashMap();
		for (ForumPost post : posts) {
			if (!names.containsKey(post.getCreatorId())) {
				names.put(post.getCreatorId(), getPostCreatorName(post));
			}
		}
		return names;
	}

	@Override
	public String getPostCreatorName(ForumPost post) {
		return userService.getFullName(post.getCreatorId());
	}

	@Override
	public boolean isUserAdmin(User user) {
		Assert.notNull(user);
		Long companyId = user.getCompany().getId();
		return Constants.FORUM_ADMINS_COMPANY_IDS.contains(companyId);
	}

}
