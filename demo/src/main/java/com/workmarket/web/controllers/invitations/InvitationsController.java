package com.workmarket.web.controllers.invitations;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.workmarket.domains.model.Invitation;
import com.workmarket.domains.model.InvitationPagination;
import com.workmarket.domains.model.User;
import com.workmarket.domains.model.recruiting.RecruitingCampaign;
import com.workmarket.dto.SuggestionDTO;
import com.workmarket.service.business.InvitationService;
import com.workmarket.service.business.RecruitingService;
import com.workmarket.service.business.RegistrationService;
import com.workmarket.service.business.UserService;
import com.workmarket.service.infra.business.SuggestionService;
import com.workmarket.utility.CollectionUtilities;
import com.workmarket.utility.DateUtilities;
import com.workmarket.utility.StringUtilities;
import com.workmarket.web.controllers.BaseController;
import com.workmarket.web.helpers.MessageBundleHelper;
import com.workmarket.web.models.DataTablesRequest;
import com.workmarket.web.models.DataTablesResponse;
import com.workmarket.web.models.MessageBundle;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.springframework.web.bind.annotation.RequestMethod.GET;

@Controller
@RequestMapping("/invitations")
public class InvitationsController extends BaseController {

	private static final Log logger = LogFactory.getLog(InvitationsController.class);

	@Autowired RegistrationService registrationService;
	@Autowired SuggestionService suggestionService;
	@Autowired MessageBundleHelper messageHelper;
	@Autowired InvitationService invitationService;
	@Autowired RecruitingService recruitingService;
	@Autowired private UserService userService;

	private static Map<String, String> statusFilterOptions = ImmutableMap.of(
		"sent", "Invited",
		"registered", "Registered",
		"insystem", "In-System"
	);

	private static Map<Integer, String> invitationFilterSortMap = ImmutableMap.<Integer, String>builder()
			.put(1, InvitationPagination.SORTS.FIRST_NAME.toString())
			.put(2, InvitationPagination.SORTS.LAST_NAME.toString())
			.put(3, InvitationPagination.SORTS.EMAIL.toString())
			// 4 - there is no way to sort by Sent By
			.put(5, InvitationPagination.SORTS.LAST_REMINDER_DATE.toString())
			.build();


	@RequestMapping(method=RequestMethod.GET, produces=MediaType.TEXT_HTML_VALUE)
	public String showIndex(Model model) {

		model.addAttribute("status_filter_options", statusFilterOptions);
		model.addAttribute("currentView", "invitations");
		return "web/pages/invitations/index";
	}

	@RequestMapping(value="/populate", method=RequestMethod.GET, produces=MediaType.APPLICATION_JSON_VALUE)
	public void index(HttpServletRequest httpRequest, Model model) throws Exception {

		DataTablesRequest request = DataTablesRequest.newInstance(httpRequest);
		request.setSortableColumnMapping(invitationFilterSortMap);
		request.setFilterMapping(ImmutableMap.<String,Enum<?>>of(
			"filters[invitation_status]", InvitationPagination.FILTER_KEYS.USER_STATUS
		));

		InvitationPagination pagination = request.newPagination(InvitationPagination.class);

		pagination = registrationService.findInvitations(getCurrentUser().getId(), pagination);

		DataTablesResponse<List<String>, Map<String,Object>> response = DataTablesResponse.newInstance(request, pagination);

		for (final Invitation inv : pagination.getResults()) {
			RecruitingCampaign campaign = inv.getRecruitingCampaign() != null ?
				recruitingService.findRecruitingCampaign(inv.getRecruitingCampaign().getId()) : null;

			String name = StringUtilities.fullName(inv.getFirstName(), inv.getLastName());

			// If invite was sent to a company then display the company name instead of first and last names.
			if (inv.getInvitedUser() != null) {
				User invitedUser = userService.findUserById(inv.getInvitedUser().getId());
				if (invitedUser != null && invitedUser.getCompany() != null && !StringUtils.isEmpty(invitedUser.getCompany().getEffectiveName())) {
					name = invitedUser.getCompany().getEffectiveName();
				}
			}

			ArrayList<String> row = Lists.newArrayList(
				"",
				name,
				inv.getEmail(),
				campaign != null ? campaign.getTitle() : "",
				inv.getInvitingUser().getFullName(),
				inv.getLastReminderDate() != null ?
					DateUtilities.format("MMM d, Y", inv.getLastReminderDate()) :
					DateUtilities.format("MMM d, Y", inv.getInvitationDate())
			);

			User invUser = inv.getInvitedUser();
			Map<String, Object> meta = CollectionUtilities.newObjectMap(
				"id", inv.getId(),
				"invited_user_id", (invUser == null) ? null : invUser.getId(),
				"invited_user_number", (invUser == null) ? null : invUser.getUserNumber(),
				"is_reminder_blocked", inv.isReminderBlocked(),
				"campaign_id", campaign != null ? campaign.getId() : null,
				"status", inv.getInvitationStatusType().getCode()
			);

			response.addRow(row, meta);
		}

		model.addAttribute("response", response);
	}

	@RequestMapping(value = "/suggest_groups", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody List<Map<String,Object>> suggestGroups(@RequestParam(required = true) String term) {
		List<SuggestionDTO> suggestions = suggestionService.suggestGroup(term);
		return CollectionUtilities.extractKeyValues(suggestions, "name", "id", "value", "value");
	}

	@RequestMapping(value = "/remind", method = RequestMethod.POST)
	public String remind(@RequestParam(value="invitation_ids[]", required=false) List<Long> invitationIds, RedirectAttributes flash) {
		MessageBundle bundle = messageHelper.newFlashBundle(flash);

		try {
			if (CollectionUtils.isEmpty(invitationIds)) {
				messageHelper.addNotice(bundle, "invitations.remind.empty");
			} else {
				registrationService.remindInvitations(invitationIds);
				messageHelper.addSuccess(bundle, "invitations.remind.success");
			}
		} catch (Exception e) {
			messageHelper.addError(bundle, "invitations.remind.exception");
		}

		return "redirect:/invitations";
	}

	@RequestMapping(value = "/accept/{encryptedId}", method = GET)
	public String acceptContractor(RedirectAttributes flash, @PathVariable String encryptedId) {
		MessageBundle bundle = registrationService.acceptInvitation(encryptedId);
		flash.addFlashAttribute("bundle", bundle);
		return "redirect:/home";
	}

	@RequestMapping(value = "/decline/{encryptedId}", method = GET)
	public String declineContractor(RedirectAttributes flash, @PathVariable String encryptedId) {
		MessageBundle bundle = registrationService.declineInvitation(encryptedId);
		flash.addFlashAttribute("bundle", bundle);
		return "redirect:/home";
	}
}
