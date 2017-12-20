package com.workmarket.web.controllers;

import com.workmarket.service.business.PricingService;
import com.workmarket.velvetrope.VelvetRope;
import com.workmarket.velvetrope.Venue;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
@RequestMapping("/screening")
@VelvetRope(
	venue = Venue.HIDE_SCREENINGS,
	bypass = true,
	redirectPath = "/home",
	message = "You do not have access to this feature."
)
public class ScreeningController extends BaseController {

	@Autowired PricingService pricingService;

	@RequestMapping(method = RequestMethod.GET)
	public String index(Model model) throws Exception {

		model.addAttribute("drug_test_price", pricingService.findDrugTestPrice(getCurrentUser().getCompanyId()));
		model.addAttribute("background_check_price", pricingService.findBackgroundCheckPrice(getCurrentUser().getCompanyId()));

		return "web/pages/screening/index";
	}
}
