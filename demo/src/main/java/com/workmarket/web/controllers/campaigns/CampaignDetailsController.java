package com.workmarket.web.controllers.campaigns;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.workmarket.dto.RecruitingCampaignUser;
import com.workmarket.dto.RecruitingCampaignUserPagination;
import com.workmarket.domains.model.recruiting.RecruitingCampaign;
import com.workmarket.service.business.RecruitingService;
import com.workmarket.service.business.UserService;
import com.workmarket.utility.CollectionUtilities;
import com.workmarket.utility.DateUtilities;
import com.workmarket.utility.StringUtilities;
import com.workmarket.web.helpers.AjaxResponseBuilder;
import com.workmarket.web.helpers.MessageBundleHelper;
import com.workmarket.web.models.DataTablesRequest;
import com.workmarket.web.models.DataTablesResponse;
import com.workmarket.web.models.MessageBundle;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.text.WordUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.http.MediaType.TEXT_HTML_VALUE;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

@Controller
@RequestMapping("/campaigns")
public class CampaignDetailsController extends BaseCampaignController {

	@Autowired private RecruitingService recruitingService;
	@Autowired private MessageBundleHelper messageHelper;
	@Autowired private UserService userService;

	private static final Map<Integer, String> recruitSortMap;

	static {
		recruitSortMap = new ImmutableMap.Builder<Integer, String>()
			.put(1, RecruitingCampaignUserPagination.SORTS.LAST_NAME.toString())
			.put(3, RecruitingCampaignUserPagination.SORTS.CITY.toString())
			.put(6, RecruitingCampaignUserPagination.SORTS.GROUP_APPROVAL_STATUS.toString())
			.put(7, RecruitingCampaignUserPagination.SORTS.REGISTRATION_DATE.toString())
			.build();
	}

	@ModelAttribute("campaign")
	protected RecruitingCampaign getCampaign(
		@PathVariable("id") Long id) {
		return super.getCampaign(getCurrentUser().getCompanyId(), id);
	}

	@RequestMapping(
		value = "/details/{id}",
		method = GET)
	public String details(
		@ModelAttribute("campaign")
		RecruitingCampaign campaign,
		HttpServletRequest httpServletRequest,
		Model model) throws Exception {
		model.addAttribute("currentView", "campaigns");
		model.addAttribute("showStats", getCurrentUser().getCompanyId().equals(campaign.getCompany().getId()));

		String referenceEmail = httpServletRequest.getParameter("ref");

		return "web/pages/campaigns/details";
	}

	@RequestMapping(
		value = "/{id}/recruits",
		method = GET,
		produces = APPLICATION_JSON_VALUE)
	public void recruits(
		@ModelAttribute("campaign")
		RecruitingCampaign campaign,
		HttpServletRequest httpRequest,
		Model model) throws Exception {

		DataTablesRequest request = DataTablesRequest.newInstance(httpRequest);
		request.setSortableColumnMapping(recruitSortMap);

		RecruitingCampaignUserPagination pagination = request.newPagination(RecruitingCampaignUserPagination.class);
		pagination.addFilter(RecruitingCampaignUserPagination.FILTER_KEYS.CAMPAIGN_ID, campaign.getId());

		pagination = recruitingService.findAllRecruitingCampaignUsers(pagination);

		DataTablesResponse<List<String>, Map<String, Object>> response = DataTablesResponse.newInstance(request, pagination);

		for (RecruitingCampaignUser user : pagination.getResults()) {
			List<String> data = Lists.newArrayList(
				"",
				StringUtilities.fullName(user.getFirstName(), user.getLastName()),
				user.getCompanyName(),
				BooleanUtils.toStringYesNo(user.isEmailConfirmed()),
				user.getGroupApprovalStatus() == null ? "-" : WordUtils.capitalize(user.getGroupApprovalStatus().name().toLowerCase()),
				DateUtilities.format("MMM d, yyyy", user.getRegistrationDate(), getCurrentUser().getTimeZoneId())
			);

			Map<String, Object> meta = CollectionUtilities.newObjectMap(
				"user_number", user.getUserNumber(),
				"lane2_status", user.getLaneApprovalStatus() == null ? "-" : user.getLaneApprovalStatus().name().toLowerCase(),
				"confirmed", user.isEmailConfirmed()
			);

			response.addRow(data, meta);
		}

		model.addAttribute("response", response);
	}

	@RequestMapping(
		value = "/{id}/recruit_actions",
		method = POST,
		produces = TEXT_HTML_VALUE)
	public String recruitActionsRedirect(
		@ModelAttribute("campaign") RecruitingCampaign campaign,
		@RequestParam(value = "recruits[]", required = false) List<String> recruits,
		@RequestParam(value = "action", required = false) String action,
		RedirectAttributes flash) throws Exception {

		MessageBundle bundle = messageHelper.newFlashBundle(flash);
		recruitActions(campaign, recruits, action, bundle);

		return "redirect:/campaigns/details/{id}";
	}

	@RequestMapping(
		value = "/{id}/recruit_actions",
		method = POST,
		produces = APPLICATION_JSON_VALUE)
	@ResponseBody
	public AjaxResponseBuilder recruitActionsAjax(
		@ModelAttribute("campaign") RecruitingCampaign campaign,
		@RequestParam(value = "recruits[]", required = false) List<String> recruits,
		@RequestParam(value = "action", required = false) String action) throws Exception {

		MessageBundle bundle = messageHelper.newBundle();
		recruitActions(campaign, recruits, action, bundle);

		return new AjaxResponseBuilder()
			.setSuccessful(true)
			.setMessages(bundle.getAllMessages())
			.setRedirect(String.format("/campaigns/details/%d", campaign.getId()));
	}

	@RequestMapping(
		value = "/{id}/delete",
		method = POST)
	public String delete(
		@ModelAttribute("campaign")
		RecruitingCampaign campaign,
		RedirectAttributes flash) throws Exception {

		recruitingService.deleteRecruitingCampaign(getCurrentUser().getCompanyId(), campaign.getId());

		MessageBundle bundle = messageHelper.newFlashBundle(flash);
		messageHelper.addSuccess(bundle, "campaigns.delete.success");

		return "redirect:/campaigns";
	}

	/**
	 * Toggle active/deactivated status
	 */
	@RequestMapping(
		value = "/{id}/activate",
		method = POST)
	public String toggleActivation(
		@ModelAttribute("campaign")
		RecruitingCampaign campaign,
		RedirectAttributes flash) throws Exception {

		MessageBundle bundle = messageHelper.newFlashBundle(flash);
		Long companyId = getCurrentUser().getCompanyId();

		if (campaign.isActive()) {
			recruitingService.deactivateRecruitingCampaign(companyId, campaign.getId());
			messageHelper.addSuccess(bundle, "campaigns.activate.success_deactivate");
		} else {
			recruitingService.activateRecruitingCampaign(companyId, campaign.getId());
			messageHelper.addSuccess(bundle, "campaigns.activate.success_activate");
		}

		return "redirect:/campaigns";
	}

	private void recruitActions(
		RecruitingCampaign campaign,
		List<String> recruits,
		String action,
		MessageBundle bundle) {

		if (StringUtils.isBlank(action)) {
			messageHelper.addError(bundle, "campaigns.recruit_actions.noaction");
			return;
		}
		if (CollectionUtilities.isEmpty(recruits)) {
			messageHelper.addError(bundle, "campaigns.recruit_actions.norecruits");
			return;
		}
		if (!StringUtilities.equalsAny(action, "remove", "add")) {
			messageHelper.addError(bundle, "campaigns.recruit_actions.noaction");
			return;
		}

		if (action.equals("remove")) {
			for (String recruit : recruits) {
				Long userId = userService.findUserId(recruit);
				if (userId != null) {
					recruitingService.declineRecruitingCampaignUser(getCurrentUser().getCompanyId(), campaign.getId(), userId);
				}
			}

			messageHelper.addSuccess(bundle, "campaigns.recruit_actions.success_remove");
		}

		if (action.equals("add")) {
			for (String recruit : recruits) {
				Long userId = userService.findUserId(recruit);
				if (userId != null) {
					recruitingService.approveRecruitingCampaignUser(getCurrentUser().getCompanyId(), campaign.getId(), userId);
				}
			}

			messageHelper.addSuccess(bundle, "campaigns.recruit_actions.success_add");
		}
	}
}
