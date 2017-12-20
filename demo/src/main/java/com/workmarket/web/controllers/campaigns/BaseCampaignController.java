package com.workmarket.web.controllers.campaigns;

import com.workmarket.domains.model.recruiting.RecruitingCampaign;
import com.workmarket.service.business.RecruitingService;
import com.workmarket.service.infra.security.RequestContext;
import com.workmarket.utility.CollectionUtilities;
import com.workmarket.web.controllers.BaseController;
import com.workmarket.web.exceptions.HttpException401;
import com.workmarket.web.exceptions.HttpException404;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

public class BaseCampaignController extends BaseController {

	@Autowired private RecruitingService recruitingService;

	protected RecruitingCampaign getCampaign(Long companyId, Long id) {
		RecruitingCampaign campaign = recruitingService.findRecruitingCampaign(companyId, id);

		if (campaign == null) {
			throw new HttpException404()
				.setMessageKey("campaigns.notfound")
				.setRedirectUri("redirect:/campaigns");
		}

		List<RequestContext> contexts = recruitingService.getRequestContext(campaign.getId());
		if (!CollectionUtilities.containsAny(contexts, RequestContext.COMPANY_OWNED, RequestContext.OWNER)) {
			throw new HttpException401()
				.setMessageKey("campaigns.notallowed")
				.setRedirectUri("redirect:/campaigns");
		}

		return campaign;
	}
}
