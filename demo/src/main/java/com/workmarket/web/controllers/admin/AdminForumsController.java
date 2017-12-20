package com.workmarket.web.controllers.admin;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.workmarket.domains.forums.model.FlaggedPostStatistics;
import com.workmarket.domains.forums.model.ForumFlaggedPostPagination;
import com.workmarket.domains.forums.model.ForumPost;
import com.workmarket.domains.forums.model.UserForumBan;
import com.workmarket.domains.forums.model.UserForumBanPagination;
import com.workmarket.domains.forums.service.ForumService;
import com.workmarket.domains.model.User;
import com.workmarket.service.infra.business.AuthenticationService;
import com.workmarket.utility.CollectionUtilities;
import com.workmarket.web.controllers.BaseController;
import com.workmarket.web.forms.admin.BanUserForm;
import com.workmarket.web.forms.forums.ForumPostSearchForm;
import com.workmarket.web.helpers.AjaxResponseBuilder;
import com.workmarket.web.models.DataTablesRequest;
import com.workmarket.web.models.DataTablesResponse;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

@Controller
@RequestMapping("/admin/forums")
public class AdminForumsController extends BaseController {

	@Autowired private ForumService forumService;
	@Autowired private AuthenticationService authenticationService;

	private static final Logger logger = LoggerFactory.getLogger(AdminForumsController.class);
	private static final int POSTS_ROW_LIMIT = 20;

	@RequestMapping("/flagged")
	public String getFlaggedPostPage(Model model) throws Exception {
		model.addAttribute("timezone", getCurrentUser().getTimeZoneId());
		model.addAttribute("labels", ForumFlaggedPostPagination.COLUMNS);
		return "web/pages/admin/forums/flagged";
	}

	@RequestMapping(
		value = "/flagged_posts",
		method = GET,
		produces = APPLICATION_JSON_VALUE)
	public void getFlaggedPosts(
		Model model,
		HttpServletRequest httpRequest) {

		DataTablesRequest request = DataTablesRequest.newInstance(httpRequest);
		ForumFlaggedPostPagination pagination;
		try {
			pagination = request.newPagination(ForumFlaggedPostPagination.class);
		} catch(Exception e) {
			logger.error("[forums] Error fetching flagged posts", e);
			return;
		}

		if (request.getSortColumnIndex() != null) {
			if (ForumFlaggedPostPagination.SORTABLE_COLUMNS.containsKey(request.getSortColumnIndex())) {
				pagination.setSortColumn(ForumFlaggedPostPagination.SORTABLE_COLUMNS.get(request.getSortColumnIndex()));
			}
		}

		forumService.getAllFlaggedPostsStatistics(pagination);

		DataTablesResponse<List<String>, Map<String, Object>> response = DataTablesResponse.newInstance(request, pagination);
		SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy hh:mm a z");
		sdf.setTimeZone(TimeZone.getTimeZone(getCurrentUser().getTimeZoneId()));
		for (FlaggedPostStatistics stat: pagination.getResults()) {
			List<String> row = Lists.newArrayList();
			row.add(stat.getComment());
			if (stat.isCreatorBanned()) {
				row.add(stat.getCreatorName().concat(" - banned"));
			}
			else {
				row.add(stat.getCreatorName());
			}
			row.add(sdf.format(stat.getDateReported().getTime()));
			row.add(stat.getCount().toString());
			row.add(stat.getCreatorId().toString());
			response.addRow(row, CollectionUtilities.newObjectMap("postId", stat.getPostId(), "rootId", stat.getRootId()));
		}
		model.addAttribute("response", response);
	}

	@RequestMapping(value = "/banned", method = GET)
	public String getBannedUsers(Model model, @ModelAttribute("form") BanUserForm form) {
		model.addAttribute("timezone", getCurrentUser().getTimeZoneId());
		model.addAttribute("labels", UserForumBanPagination.COLUMNS);
		return "web/pages/admin/forums/banned";
	}

	@RequestMapping(
		value = "/banned_users",
		method = GET,
		produces = APPLICATION_JSON_VALUE)
	public void getBannedUsers(
		Model model,
		HttpServletRequest httpRequest,
		@RequestParam(value = "sSearch", required = false) String searchQuery) {

		DataTablesRequest request = DataTablesRequest.newInstance(httpRequest);
		UserForumBanPagination pagination;

		try {
			pagination = request.newPagination(UserForumBanPagination.class);
		} catch (Exception e) {
			logger.error("[forums] Error fetching banned users", e);
			return;
		}

		if (request.getSortColumnIndex() != null) {
			if (UserForumBanPagination.SORTABLE_COLUMNS.containsKey(request.getSortColumnIndex())) {
				pagination.setSortColumn(UserForumBanPagination.SORTABLE_COLUMNS.get(request.getSortColumnIndex()));
			}
		}

		if (StringUtils.isNotEmpty(searchQuery)) {
			pagination.addFilter(UserForumBanPagination.FILTER_KEYS.NAME_REASON, searchQuery);
		}

		pagination = forumService.getAllBannedUsers(pagination);

		DataTablesResponse<List<String>, Map<String, Object>> response = DataTablesResponse.newInstance(request, pagination);
		SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy hh:mm a z");
		sdf.setTimeZone(TimeZone.getTimeZone(getCurrentUser().getTimeZoneId()));

		for (UserForumBan ban : pagination.getResults()) {
			User bannedUser = ban.getBannedUser();
			List<String> row = Lists.newArrayList();
			row.add(bannedUser.getFullName());
			row.add(ban.getReason());
			row.add(sdf.format(ban.getCreatedOn().getTime()));
			row.add(bannedUser.getId().toString());
			response.addRow(row, CollectionUtilities.newObjectMap("user_id", bannedUser.getId()));
		}

		model.addAttribute("response", response);
	}

	@RequestMapping(value = "/unflag/{postId}", method = POST)
	@ResponseBody
	public AjaxResponseBuilder unflagPost(@PathVariable Long postId) {
		AjaxResponseBuilder response = AjaxResponseBuilder.fail();
		if (forumService.isUserAdmin(authenticationService.getCurrentUser())) {
			ForumPost unflag = forumService.unflagPost(postId);
			if (unflag != null) {
				response.setSuccessful(true);
			}
		}
		return response;
	}

	@RequestMapping(value = "/ban", method = POST)
	public String banUser(final @ModelAttribute("form") BanUserForm form) {
		forumService.banUser(form.getBannedUserEmail(), form.getReason());
		return "redirect:/admin/forums/banned";
	}

	@RequestMapping(value = "/ban/{userId}", method = POST)
	@ResponseBody
	public AjaxResponseBuilder banUser(@PathVariable Long userId, @RequestParam String reason) {
		UserForumBan ban = forumService.banUser(userId, reason);
		if (ban != null) {
			return AjaxResponseBuilder.success()
				.addData("banned", !ban.getDeleted());
		}
		return AjaxResponseBuilder.fail();
	}

	@RequestMapping(value = "/delete/{postId}", method = POST, produces = APPLICATION_JSON_VALUE)
	@ResponseBody
	public AjaxResponseBuilder deleteToggleForumPost(@PathVariable Long postId) {
		ForumPost post = forumService.getPostById(postId);
		if (!post.getDeleted()){
			post = forumService.toggleForumPostDelete(postId);
		}
		if (post.getDeleted()) {
			return AjaxResponseBuilder.success();
		}
		else {
			return AjaxResponseBuilder.fail();
		}
	}

	@RequestMapping(value = "/unban/{userId}")
	@ResponseBody
	public AjaxResponseBuilder unbanUser(@PathVariable Long userId) {
		UserForumBan unban = forumService.unbanUser(userId);
		if (unban != null) {
			return AjaxResponseBuilder.success();
		}
		return AjaxResponseBuilder.fail();
	}

	@Deprecated
	@RequestMapping(
		value = "/posts",
		method = GET,
		produces = APPLICATION_JSON_VALUE)
	@ResponseBody
	public Map<String, Object> getPosts(final @ModelAttribute ForumPostSearchForm form) {
		List<Map<String, Object>> results = Lists.newArrayList();
		return ImmutableMap.of(
			"posts", results,
			"pageNumber", 0,
			"numberOfPages", 0
		);
	}

	@RequestMapping(
			value = "/posts",
			method = GET)
	public String posts(Model model) throws Exception {
		model.addAttribute("timezone", getCurrentUser().getTimeZoneId());
		return "web/pages/admin/forums/posts";
	}
}
