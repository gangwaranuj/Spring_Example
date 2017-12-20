package com.workmarket.web.controllers.feed;

import com.workmarket.domains.work.model.AbstractWork;
import com.workmarket.domains.work.model.PublicWork;
import com.workmarket.domains.work.service.WorkService;
import com.workmarket.utility.SeoUtilities;
import com.workmarket.utility.StringUtilities;
import com.workmarket.web.controllers.BaseController;
import com.workmarket.web.exceptions.HttpException404;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/work")
public class PublicWorkController extends BaseController {

	public static final int SEO_FRIENDLY_TITLE_LENGTH = 155;

	@Autowired private WorkService workService;

	@RequestMapping(value = {"/{workNumber}", "/{workNumber}/{seoTitle}"})
	public String getPublicWork(@PathVariable String workNumber, Model model) {

		if (isAuthenticated()) {
			return String.format("redirect:/assignments/details/%s", workNumber);
		}

		PublicWork publicWork = new PublicWork();

		AbstractWork work = workService.findWorkByWorkNumber(workNumber);
		if (work == null || !work.isShownInFeed()) {
			throw new HttpException404()
					.setMessageKey("work.notfound")
					.setRedirectUri("redirect:/error/404");
		}

		model.addAttribute("work", publicWork.copy(work));

		model.addAttribute("workNumber", workNumber);

		if (StringUtils.isNotEmpty(work.getTitle())) {
			String pageTitle = StringUtilities.stripHTML(work.getTitle());
			model.addAttribute("pageTitle", StringUtils.substring(pageTitle, SEO_FRIENDLY_TITLE_LENGTH));
			if (work.getIsOnsiteAddress() && work.getAddress().getState() != null) {
				model.addAttribute("pageKeywords", SeoUtilities.buildSEOFriendlyKeywords(pageTitle, work.getAddress().getCity(), work.getAddress().getState().getShortName()));
			} else {
				model.addAttribute("pageKeywords", SeoUtilities.buildSEOFriendlyKeywords(pageTitle));
			}
		}

		model.addAttribute("pageDescription", StringUtilities.stripHTML(work.getDescription()));

		return "web/pages/feed/work";
	}
}
