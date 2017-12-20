package com.workmarket.web.controllers.campaigns;

import com.google.common.base.Strings;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.workmarket.domains.model.recruiting.RecruitingCampaign;
import com.workmarket.domains.model.recruiting.RecruitingCampaignPagination;
import com.workmarket.service.business.RecruitingService;
import com.workmarket.utility.CollectionUtilities;
import com.workmarket.utility.DateUtilities;
import com.workmarket.web.controllers.BaseController;
import com.workmarket.web.models.DataTablesRequest;
import com.workmarket.web.models.DataTablesResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.http.MediaType.TEXT_HTML_VALUE;
import static org.springframework.web.bind.annotation.RequestMethod.GET;

@Controller
@RequestMapping("/campaigns")
public class CampaignsController extends BaseController {

	@Autowired private RecruitingService recruitingService;

	private static Map<Integer, String> CAMPAIGNS_SORT_MAP;

	static {
		CAMPAIGNS_SORT_MAP = new ImmutableMap.Builder<Integer, String>()
			.put(0, RecruitingCampaignPagination.SORTS.CAMPAIGN_TITLE.toString())
			.put(1, RecruitingCampaignPagination.SORTS.CAMPAIGN_DATE.toString())
			.put(2, RecruitingCampaignPagination.SORTS.CLICKS.toString())
			.put(3, RecruitingCampaignPagination.SORTS.SIGNUPS.toString())
			.build();
	}

	@RequestMapping(
		method = GET,
		produces = TEXT_HTML_VALUE)
	public String index(Model model) {
		model.addAttribute("currentView", "campaigns");
		return "web/pages/campaigns/index";
	}

	@RequestMapping(
		value = "/list",
		method = GET,
		produces = APPLICATION_JSON_VALUE)
	public void index(
		HttpServletRequest httpRequest,
		Model model) throws Exception {

		String status = Strings.nullToEmpty(httpRequest.getParameter("filters[status]"));

		DataTablesRequest request = DataTablesRequest.newInstance(httpRequest);
		request.setSortableColumnMapping(CAMPAIGNS_SORT_MAP);

		RecruitingCampaignPagination pagination = request.newPagination(RecruitingCampaignPagination.class);

		if (status.equals("active")) {
			pagination.addFilter(RecruitingCampaignPagination.FILTER_KEYS.ACTIVE, Boolean.TRUE);
		} else if (status.equals("inactive")) {
			pagination.addFilter(RecruitingCampaignPagination.FILTER_KEYS.ACTIVE, Boolean.FALSE);
		}

		pagination = recruitingService.findAllCampaignsByCompanyId(getCurrentUser().getCompanyId(), pagination);

		DataTablesResponse<List<String>, Map<String, Object>> response = DataTablesResponse.newInstance(request, pagination);
		response.addFilter("status", status);

		for (RecruitingCampaign campaign : pagination.getResults()) {
			List<String> data = Lists.newArrayList(
				campaign.getTitle(),
				DateUtilities.format("MMM d, yyyy", campaign.getCreatedOn(), getCurrentUser().getTimeZoneId()),
				campaign.getClicks().toString(),
				campaign.getUsers().toString(),
				""
			);

			response.addRow(data, CollectionUtilities.newObjectMap(
					"id", campaign.getId(),
					"active", campaign.isActive())
			);
		}

		model.addAttribute("response", response);
	}
}
