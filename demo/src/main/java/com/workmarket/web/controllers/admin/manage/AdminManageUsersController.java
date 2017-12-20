package com.workmarket.web.controllers.admin.manage;

import com.google.common.collect.Lists;
import com.workmarket.domains.model.ApprovalStatus;
import com.workmarket.domains.model.User;
import com.workmarket.domains.model.UserPagination;
import com.workmarket.domains.model.acl.AclRole;
import com.workmarket.domains.model.user.RecentUser;
import com.workmarket.domains.model.user.RecentUserPagination;
import com.workmarket.service.business.MessagingService;
import com.workmarket.service.business.ProfileService;
import com.workmarket.service.business.UserService;
import com.workmarket.service.business.dto.EMailDTO;
import com.workmarket.service.infra.business.AuthenticationService;
import com.workmarket.utility.CollectionUtilities;
import com.workmarket.utility.DateUtilities;
import com.workmarket.utility.StringUtilities;
import com.workmarket.web.controllers.BaseController;
import com.workmarket.web.forms.MessageForm;
import com.workmarket.web.helpers.MessageBundleHelper;
import com.workmarket.web.models.DataTablesRequest;
import com.workmarket.web.models.DataTablesResponse;
import com.workmarket.web.models.MessageBundle;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/admin/manage/users")
public class AdminManageUsersController extends BaseController {

	@Autowired private AuthenticationService authnService;
	@Autowired private ProfileService profileService;
	@Autowired private UserService userService;
	@Autowired private MessagingService messagingService;
	@Autowired private MessageBundleHelper messageHelper;
	@Autowired private MessageSource messageSource;

	@RequestMapping(value="/index", method=RequestMethod.GET)
	public String index() {
		return "redirect:/admin/manage/users/pending";
	}

	@RequestMapping(value="/pending", method=RequestMethod.GET, produces=MediaType.TEXT_HTML_VALUE)
	public String pending() {
		return "web/pages/admin/manage/users/pending";
	}

	@RequestMapping(value="/pending", method=RequestMethod.GET, produces=MediaType.APPLICATION_JSON_VALUE)
	public void pendingList(HttpServletRequest httpRequest, Model model) throws Exception {
		DataTablesRequest request = DataTablesRequest.newInstance(httpRequest);
		request.setSortableColumnMapping(CollectionUtilities.<Integer,String>newTypedObjectMap(
			1, UserPagination.SORTS.CREATION_DATE,
			2, UserPagination.SORTS.LAST_NAME,
			3, UserPagination.SORTS.COMPANY_NAME
		));
		request.setFilterMapping(CollectionUtilities.<String,Enum<?>>newTypedObjectMap(
			"sSearch", UserPagination.FILTER_KEYS.KEYWORDS
		));

		UserPagination pagination = request.newPagination(UserPagination.class);
		pagination = userService.findAllPendingLane3Users(pagination);

		DataTablesResponse<List<String>,Map<String,Object>> response = DataTablesResponse.newInstance(request, pagination);

		for (User u : pagination.getResults()) {
			List<String> data = Lists.newArrayList(
				u.getId().toString(),
				DateUtilities.format("MMMM d, yyyy", u.getCreatedOn()),
				u.getFullName(),
				u.getCompany().getName(),
				u.getUserNumber()
			);

			response.addRow(data, null);
		}

		model.addAttribute("response", response);
	}

	@RequestMapping(value="/approve", method=RequestMethod.POST)
	public String approve(
			RedirectAttributes redirectAttributes,
			MessageBundle messages,
			@RequestParam("user_ids[]") List<Long> userIds) {

		for (Long uid : userIds)
			authnService.approveUser(uid);

		messageHelper.addSuccess(messages, "admin.users.approved.success");
		redirectAttributes.addFlashAttribute("bundle", messages);

		return "redirect:/admin/manage/users/pending";
	}

	@RequestMapping(
		value = "/approve_lane3",
		method = RequestMethod.POST,
		produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody Map approveLane3(
		@RequestBody Map<String, Long> user,
		HttpServletResponse response) {

		String message;

		try {
			profileService.approveUserProfileModifications(user.get("id"));
			message = messageSource.getMessage("admin.users.approve_lane3.success", null, null);
		} catch (Exception e) {
			message = messageSource.getMessage("admin.users.approve_lane3.failure", null, null);
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
		}

		return CollectionUtilities.newObjectMap(
			"message", message
		);
	}

	@RequestMapping(
		value = "/decline_lane3",
		method = RequestMethod.POST,
		produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody Map declineLane3(
		@RequestBody Map<String, Long> user,
		HttpServletResponse response) {

		// Decline steps:
		// 1) Decline all profile modifications.
		// 2) Set lane 3 approval status to DECLINED
		// 3) Set the user status back to APPROVED so it doesn't hang out in the profile queue.
		//    (NOTE: This call MUST happen AFTER declining lane 3)
		// 4) Remove the shared worker role

		Long userId = user.get("id");
		String message;

		try {
			profileService.declineUserProfileModifications(userId);
			userService.updateLane3ApprovalStatus(userId, ApprovalStatus.DECLINED);
			authnService.approveUser(userId);
			authnService.removeAclRoleFromUser(userId, AclRole.ACL_SHARED_WORKER);
			message = messageSource.getMessage("admin.users.decline_lane3.success", null, null);
		} catch (Exception e) {
			message = messageSource.getMessage("admin.users.decline_lane3.failure", null, null);
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
		}

		return CollectionUtilities.newObjectMap(
			"message", message
		);
	}

	@RequestMapping(value="/message", method=RequestMethod.GET)
	public String message(
			Model model,
			@ModelAttribute("messageForm") MessageForm form,
			@RequestParam(value="user_ids[]", required=false) List<Long> userIds,
			@RequestParam(value="user_names[]", required=false) List<String> userNames) {

		form.setUserIds(userIds);

		model.addAttribute("userNames", userNames);

		return "web/pages/admin/manage/users/message";
	}

	@RequestMapping(value="/message", method=RequestMethod.POST)
	public void doMessage(
			@Valid @ModelAttribute("messageForm") MessageForm form,
			BindingResult bindingResult,
			Model model,
			MessageBundle messages) {

		if (CollectionUtils.isEmpty(form.getUserIds())) {
			bindingResult.rejectValue("userIds", "NotEmpty");
		}

		if (bindingResult.hasErrors()) {
			messageHelper.setErrors(messages, bindingResult);
			model.addAttribute("successful", false);
			model.addAttribute("errors", messages.getErrors());
		}

		EMailDTO dto = new EMailDTO();
		dto.setFromId(getCurrentUser().getId());
		dto.setSubject(form.getTitle());
		dto.setText(form.getMessage());

		messagingService.sendEmailToUsers(getCurrentUser().getId(), form.getUserIds(), dto);

		messageHelper.addSuccess(messages, "admin.users.message.success");

		model.addAttribute("response", CollectionUtilities.newObjectMap(
			"successful", true,
			"message", messages.getSuccess().iterator().next()
		));
	}

	@RequestMapping(value="/suspended", method=RequestMethod.GET, produces=MediaType.TEXT_HTML_VALUE)
	public String suspended() {
		return "web/pages/admin/manage/users/suspended";
	}

	@RequestMapping(value="/suspended.json", method=RequestMethod.GET, produces=MediaType.APPLICATION_JSON_VALUE)
	public void suspendedList(HttpServletRequest httpRequest, Model model) throws Exception {
		DataTablesRequest request = DataTablesRequest.newInstance(httpRequest);
		UserPagination pagination = request.newPagination(UserPagination.class);
		pagination = userService.findAllSuspendedUsers(pagination);

		DataTablesResponse<List<String>,Map<String,Object>> response = DataTablesResponse.newInstance(request, pagination);

		for (User u : pagination.getResults()) {
			List<String> data = Lists.newArrayList(
				u.getFullName(),
				u.getCompany().getName(),
				DateUtilities.format("MMMM d, yyyy", authnService.getUserStatusTypeModifiedOn(u))
			);

			Map<String,Object> meta = CollectionUtilities.newObjectMap(
				"id", u.getUserNumber()
			);

			response.addRow(data, meta);
		}

		model.addAttribute("response", response);
	}

	@RequestMapping(value="/recent", method=RequestMethod.GET, produces=MediaType.TEXT_HTML_VALUE)
	public String recent(Model model) {
		model.addAttribute("registrationDateFilterOptions", CollectionUtilities.newStringMap(
			"", "All Users",
			"last_365_days", "Last 365 Days",
			"last_120_days", "Last 120 Days",
			"last_90_days", "Last 90 Days",
			"last_60_days", "Last 60 Days",
			"last_30_days", "Last 30 Days",
			"ytd", "Year to Date"
		));

		return "web/pages/admin/manage/users/recent";
	}

	@RequestMapping(value="/recent.json", method=RequestMethod.GET, produces=MediaType.APPLICATION_JSON_VALUE)
	public void recentList(
			HttpServletRequest httpRequest,
			Model model,
			@RequestParam(value="registrationDate", required=false) String registrationDate) throws Exception {
		DataTablesRequest request = DataTablesRequest.newInstance(httpRequest);
		request.setSortableColumnMapping(CollectionUtilities.<Integer,String>newTypedObjectMap(
			1, RecentUserPagination.SORTS.LAST_NAME.toString(),
			2, RecentUserPagination.SORTS.COMPANY_NAME.toString(),
			3, RecentUserPagination.SORTS.EMAIL.toString(),
			4, RecentUserPagination.SORTS.WORK_PHONE.toString(),
			6, RecentUserPagination.SORTS.REGISTRATION_DATE.toString()
		));
		request.setFilterMapping(CollectionUtilities.<String,Enum<?>>newTypedObjectMap(
			"sSearch", RecentUserPagination.FILTER_KEYS.USER_NAME
		));

		RecentUserPagination pagination = request.newPagination(RecentUserPagination.class);
		if (StringUtils.isNotBlank(registrationDate)) {
			RecentUserPagination.FILTER_KEYS registrationDateFilter = RecentUserPagination.FILTER_KEYS.valueOf(registrationDate.toUpperCase());
			pagination.addFilter(registrationDateFilter, true);
		}
		pagination = userService.findAllRecentUsers(pagination);

		DataTablesResponse<List<String>,Map<String,Object>> response = DataTablesResponse.newInstance(request, pagination);

		pagination.setReturnAllRows();
		for (RecentUser u : pagination.getResults()) {
			List<String> data = Lists.newArrayList(
				null,
				StringUtilities.fullName(u.getFirstName(), u.getLastName()),
				u.getCompanyName(),
				u.getEmail(),
				StringUtilities.formatPhoneNumber(u.getWorkPhone()),
				null,
				DateUtilities.format("MMMM d, yyyy", u.getRegisteredOn())
			);

			Map<String,Object> meta = CollectionUtilities.newObjectMap(
				"id", u.getId(),
				"user_number", u.getUserNumber(),
				"company_id", u.getCompanyId(),
				"lane1", u.getLane1Flag(),
				"lane2", u.getLane2Flag(),
				"lane3", u.getLane3Flag(),
				"lane4", u.getLane4Flag()
			);

			response.addRow(data, meta);
		}

		model.addAttribute("response", response);
	}
}
