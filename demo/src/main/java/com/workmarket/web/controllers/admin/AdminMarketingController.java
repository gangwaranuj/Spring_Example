package com.workmarket.web.controllers.admin;

import com.workmarket.service.business.feed.FeedService;
import com.workmarket.service.business.scheduler.PublicFeedToXmlExecutor;
import com.workmarket.web.controllers.BaseController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
@RequestMapping("/admin/marketing")
public class AdminMarketingController extends BaseController {

	@Autowired private FeedService feedService;
	@RequestMapping(
			value={"", "/"},
			method= RequestMethod.GET)
	public String index() throws Exception {

		return "web/pages/admin/marketing/index";
	}


	@RequestMapping(value = "/indeed")
	public String getIndeedPage() {
		return "web/pages/admin/marketing/indeed";
	}

	@RequestMapping(value = "/indeed/refresh")
	public String refreshIndeed() {
		feedService.pushFeedToRedis();
		return "redirect:/admin/marketing/indeed";
	}
}

