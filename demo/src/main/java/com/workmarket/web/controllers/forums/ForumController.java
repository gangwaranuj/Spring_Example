package com.workmarket.web.controllers.forums;

import com.google.common.collect.Lists;
import com.workmarket.configuration.Constants;
import com.workmarket.domains.forums.model.ForumCategory;
import com.workmarket.domains.forums.model.ForumCategoryStatistics;
import com.workmarket.domains.forums.model.ForumPost;
import com.workmarket.domains.forums.model.ForumPostFollower;
import com.workmarket.domains.forums.model.ForumPostPagination;
import com.workmarket.domains.forums.model.ForumTag;
import com.workmarket.domains.forums.model.ForumTagStatistics;
import com.workmarket.domains.forums.service.ForumService;
import com.workmarket.domains.model.Pagination;
import com.workmarket.service.business.UserService;
import com.workmarket.service.infra.business.AuthenticationService;
import com.workmarket.utility.CollectionUtilities;
import com.workmarket.utility.DateUtilities;
import com.workmarket.utility.WebUtilities;
import com.workmarket.web.controllers.BaseController;
import com.workmarket.web.exceptions.HttpException404;
import com.workmarket.web.forms.forums.ForumPostForm;
import com.workmarket.web.forms.forums.ForumPostSearchForm;
import com.workmarket.web.helpers.AjaxResponseBuilder;
import com.workmarket.web.models.DataTablesRequest;
import com.workmarket.web.models.DataTablesResponse;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

@Controller
@RequestMapping(value = {"/forums"})
public class ForumController extends BaseController {

	@Autowired private ForumService forumService;
	@Autowired private UserService userService;
	@Autowired private AuthenticationService authenticationService;

	private static final Logger logger = LoggerFactory.getLogger(ForumController.class);

	@ModelAttribute("tagStats")
	protected List<ForumTagStatistics> getForumTagStatistics(HttpServletRequest request) {
		return null;
	}

	@ModelAttribute("tags")
	protected List<String> getTagNames(HttpServletRequest request) {
		return WebUtilities.isAjax(request) ? null : ForumTag.getForumListTags();
	}

	@ModelAttribute("isUserBanned")
	protected boolean isUserBanned(HttpServletRequest request) {
		return !WebUtilities.isAjax(request) && isUserBanned();
	}

	protected boolean isUserBanned() {
		return forumService.isUserBanned(getCurrentUser().getId());
	}

	@ModelAttribute("isInternal")
	protected boolean isUserInternal(HttpServletRequest request) {
		return !WebUtilities.isAjax(request) && isUserInternal();
	}

	protected boolean isUserInternal() {
		return forumService.isUserAdmin(authenticationService.getCurrentUser());
	}

	@RequestMapping(method = GET)
	public String index(Model model, @ModelAttribute("search-form") ForumPostSearchForm searchForm) {

		List<ForumCategory> categories = forumService.getCategoryList();
		List<ForumCategoryStatistics> categoryStats = Lists.newArrayList();

		model.addAttribute("categories", categories);
		model.addAttribute("categoryStats", categoryStats);

		return "web/pages/forums/index";
	}

	@RequestMapping(value = "/{categoryId}")
	public String topicPage(Model model, @PathVariable Long categoryId, @ModelAttribute("search-form") ForumPostSearchForm searchForm) {

		List<ForumCategory> categories = forumService.getCategoryList();
		List<ForumCategoryStatistics> categoryStats = Lists.newArrayList();

		model.addAttribute("categories", categories);
		model.addAttribute("categoryStats", categoryStats);
		model.addAttribute("categoryId", categoryId);

		return "web/pages/forums/community_page";
	}

	@RequestMapping(
		value = "/get_posts",
		method = GET,
		produces = APPLICATION_JSON_VALUE)
	private void getPosts(Model model, HttpServletRequest httpRequest, @RequestParam("categoryId") String category, @RequestParam boolean following) {

		DataTablesRequest request = DataTablesRequest.newInstance(httpRequest);

		ForumPostPagination pagination = null;
		Map<Long, String> avatarURIs = null;
		Map<Long, String> creatorNames = null;
		Long categoryId = null;
		try {
			pagination = request.newPagination(ForumPostPagination.class);
			pagination.setSortDirection(Pagination.SORT_DIRECTION.DESC);
			if (StringUtils.isNotEmpty(category) && !category.equals("undefined") && StringUtils.isNumeric(category)) {
				categoryId = Long.parseLong(category);
			}
			if (following && categoryId != null) {
				pagination = forumService.getFollowedPost(getCurrentUser().getId(), categoryId, pagination);
			} else if (following) {
				pagination = forumService.getFollowedPost(getCurrentUser().getId(), pagination);
			} else {
				pagination = forumService.getCategoryPosts(categoryId, pagination);
			}
			avatarURIs = forumService.getAvatarURIs(pagination.getResults());
			creatorNames = forumService.getPostCreatorNames(pagination.getResults());
		} catch (Exception e) {
			logger.error("", e);
		}

		DataTablesResponse<List<String>, Map<String, Object>> response = DataTablesResponse.newInstance(request, pagination);
		if (pagination.getResults() != null) {
			for (ForumPost post : pagination.getResults()) {
				List<String> row = Lists.newArrayList();
				Map<String, Object> rowMap = CollectionUtilities.newObjectMap(
					"postId", post.getId(),
					"title", post.getTitle(),
					"commentCount", 0,
					"lastComment", 0,
					"lastCommentId", 0,
					"lastCommentDate", 0,
					"name", creatorNames.get(post.getCreatorId()),
					"avatarURI", avatarURIs.get(post.getCreatorId()),
					"categoryName", forumService.getForumCategory(post.getCategoryId()).getCategoryName(),
					"categoryId", post.getCategoryId()
				);
				response.addRow(row, rowMap);
			}
		}
		model.addAttribute("response", response);
	}

	@RequestMapping(
		value = "/post/{postId}",
		method = GET)
	public String getDiscussionPage(
		Model model,
		@PathVariable Long postId,
		@ModelAttribute("form") ForumPostForm form,
		final @ModelAttribute("search-form") ForumPostSearchForm searchForm) {

		ForumPost post = forumService.getPostById(postId);
		if (post == null) {
			throw new HttpException404()
				.setRedirectUri("redirect:/error/404");
		}
		ForumPostPagination pagination = new ForumPostPagination(true);
		pagination = forumService.getPostReplies(postId, pagination);

		Long userId = getCurrentUser().getId();

		List<ForumCategory> categories = forumService.getCategoryList();

		form.setCategoryId(post.getCategoryId());
		form.setParentId(post.getId());
		form.setRootId(post.getId());

		model.addAttribute("post", post);
		model.addAttribute("postCreatorNames", forumService.getPostCreatorNames(pagination.getResults()));
		model.addAttribute("postCreatorName", forumService.getPostCreatorName(post));
		model.addAttribute("postReplies", pagination.getResults());
		try {
			model.addAttribute("postCreatorAvatar", forumService.getAvatarURI(post));
			model.addAttribute("postRepliesAvatars", forumService.getAvatarURIs(pagination.getResults()));
		} catch (Exception e) {
			logger.error("", e);
		}
		model.addAttribute("timezone", getCurrentUser().getTimeZoneId());
		model.addAttribute("categories", categories);
		model.addAttribute("categoryStats", Lists.newArrayList());
		model.addAttribute("isFollowing", forumService.isFollowerOnPost(post.getId(), getCurrentUser().getId()));
		model.addAttribute("userId", userId);
		model.addAttribute("flaggedPosts", forumService.getUserFlaggedPostsInThread(postId, userId));
		if (isUserInternal()) {
			model.addAttribute("bannedUsers", forumService.getBannedUsersInThread(post.getId()));
		}
		model.addAttribute("postTags", forumService.getTagsOnPost(post.getId()));

		return "web/pages/forums/discussion_page";
	}

	@RequestMapping(
		value = "/post/reply",
		method = POST,
		produces = APPLICATION_JSON_VALUE)
	@ResponseBody
	@PreAuthorize("!principal.isMasquerading()")
	public AjaxResponseBuilder addReply(final @ModelAttribute("form") ForumPostForm form) {
		if (!isUserBanned()) {
			try {
				if (!isUserInternal()) {
					if (form.getComment().length() > Constants.FORUM_MAX_POST_LENGTH) {
						form.setComment(form.getComment().substring(0, Constants.FORUM_MAX_POST_LENGTH));
					}
				}
				ForumPost post = forumService.saveReply(form);

				Map<String, Object> creatorPropertyMap = userService.getProjectionMapById(post.getCreatorId(), "firstName", "lastName");

				return AjaxResponseBuilder.success()
					.addData("creator", creatorPropertyMap.get("firstName") + " " + creatorPropertyMap.get("lastName"))
					.addData("createdOn", DateUtilities.format("MM/dd/yy h:mmaa z", post.getCreatedOn(), getCurrentUser().getTimeZoneId()))
					.addData("comment", post.getComment())
					.addData("id", post.getId())
					.addData("creatorId", post.getCreatorId())
					.addData("avatarURI", forumService.getAvatarURI(post))
					.addData("isInternal", isUserInternal());
			} catch (Exception e) {
				logger.error("", e);
				return AjaxResponseBuilder.fail();
			}
		}
		return AjaxResponseBuilder.fail();
	}

	@RequestMapping(
		value = "/post",
		method = GET)
	public String getPostPage(Model model, @ModelAttribute("form") ForumPostForm form, @ModelAttribute("search-form") ForumPostSearchForm searchForm) {
		List<ForumCategory> categories = forumService.getCategoryList();

		model.addAttribute("categories", categories);
		model.addAttribute("categoryStats", Lists.newArrayList());

		return "web/pages/forums/add_post";
	}

	@RequestMapping(
		value = "/post",
		method = POST)
	@PreAuthorize("!principal.isMasquerading()")
	public String submitPost(final @ModelAttribute("form") ForumPostForm form) {
		if (!isUserBanned()) {
			try {
				if (!isUserInternal()) {
					if (form.getComment().length() > Constants.FORUM_MAX_POST_LENGTH) {
						form.setComment(form.getComment().substring(0, Constants.FORUM_MAX_POST_LENGTH));
					}
				}
				ForumPost post = forumService.savePost(form);
				return "redirect:/forums/post/" + post.getId();
			} catch (Exception e) {
				logger.error("", e);
			}

		}
		return "redirect:/forums";
	}

	@RequestMapping(
		value = "/edit/{postId}",
		method = POST,
		produces = APPLICATION_JSON_VALUE)
	@ResponseBody
	public AjaxResponseBuilder editPost(
		@RequestParam("newTitle") String newTitle,
		@RequestParam("newPost") String newPost,
		@PathVariable("postId") Long postId) {

		AjaxResponseBuilder response = AjaxResponseBuilder.fail();
		if (!isUserBanned()) {
			if (forumService.getPostById(postId).getCreatorId().equals(getCurrentUser().getId()) || isUserInternal()) {
				if (newPost.length() > Constants.FORUM_MAX_POST_LENGTH && !isUserInternal()) {
					newPost = newPost.substring(0, Constants.FORUM_MAX_POST_LENGTH);
				}
				forumService.savePost(postId, newTitle, newPost);
				response.setSuccessful(true);
			}
		}

		return response;
	}

	@RequestMapping(
		value = "/reply/edit/{commentId}",
		method = POST,
		produces = APPLICATION_JSON_VALUE)
	public @ResponseBody AjaxResponseBuilder editPost(
		@RequestParam("newComment") String newComment,
		@PathVariable("commentId") Long commentId) {

		AjaxResponseBuilder response = AjaxResponseBuilder.fail();

		if (!isUserBanned()) {
			if (forumService.getPostById(commentId).getCreatorId().equals(getCurrentUser().getId()) || isUserInternal()) {
				if (newComment.length() > Constants.FORUM_MAX_POST_LENGTH && !isUserInternal()) {
					newComment = newComment.substring(0, Constants.FORUM_MAX_POST_LENGTH);
				}
				forumService.savePost(commentId, newComment);
				response.setSuccessful(true);
			}
		}

		return response;
	}

	@RequestMapping(
		value = "/search",
		method = POST)
	public String submitSearch(Model model, final @ModelAttribute("search-form") ForumPostSearchForm form) {
		model.addAttribute("categories", forumService.getCategoryList());
		model.addAttribute("isSearch", true);

		return "web/pages/forums/search";
	}

	@RequestMapping(
		value = "/search",
		method = POST,
		produces = APPLICATION_JSON_VALUE)
	@ResponseBody
	public AjaxResponseBuilder submitSearch(final @ModelAttribute("searchForm") ForumPostSearchForm form, HttpServletRequest httpRequest) {

		DataTablesRequest request = DataTablesRequest.newInstance(httpRequest);

		DataTablesResponse<List<String>, Map<String, Object>> response = DataTablesResponse.newInstance(request, 0);
		return AjaxResponseBuilder.success().addData("json", response);
	}

	@RequestMapping(
		value = "/search",
		method = GET)
	public String getSearchPage(Model model, @ModelAttribute("search-form") ForumPostSearchForm form) {
		model.addAttribute("categories", forumService.getCategoryList());
		model.addAttribute("isSearch", true);

		return "web/pages/forums/search";
	}

	@RequestMapping(
		value = "/follow/{postId}",
		method = GET,
		produces = APPLICATION_JSON_VALUE)
	@ResponseBody
	public AjaxResponseBuilder followPost(@PathVariable Long postId) {
		try {
			ForumPostFollower follower = forumService.toggleFollower(postId, getCurrentUser().getId());
			return AjaxResponseBuilder.success()
				.addData("isfollower", follower.isFollowing());
		} catch (Exception e) {
			logger.error("", e);

			return AjaxResponseBuilder.fail();
		}
	}

	@RequestMapping(
		value = "/post/toggle/{postId}",
		method = GET,
		produces = APPLICATION_JSON_VALUE)
	@ResponseBody
	public AjaxResponseBuilder deleteToggleForumPost(@PathVariable Long postId) {
		ForumPost post = forumService.toggleForumPostDelete(postId);

		return AjaxResponseBuilder.success()
			.addData("id", postId)
			.addData("deleted", post.getDeleted())
			.addData("comment", post.getComment())
			.addData("isInternal", isUserInternal());
	}

	@RequestMapping(
		value = "/post/flag/{postId}",
		method = POST,
		produces = APPLICATION_JSON_VALUE)
	public @ResponseBody Map flaggedToggleForumPost(
		@PathVariable Long postId,
		HttpServletResponse response) {

		boolean flagged;

		try {
			ForumPost post = forumService.toggleFlaggedStatus(postId, getCurrentUser().getId());
			flagged = post.getFlagged();
		} catch (Exception e) {
			flagged = false;
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			logger.error(String.format("Failed to flag forum post with id: %s", postId), e);
		}

		return CollectionUtilities.newObjectMap(
			"flagged", flagged
		);
	}


}
