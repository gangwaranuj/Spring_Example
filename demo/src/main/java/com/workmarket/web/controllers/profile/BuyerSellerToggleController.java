package com.workmarket.web.controllers.profile;

import com.workmarket.domains.model.user.PersonaPreference;
import com.workmarket.service.business.UserService;
import com.workmarket.service.infra.business.AuthenticationService;
import com.workmarket.velvetrope.VelvetRope;
import com.workmarket.velvetrope.Venue;
import com.workmarket.web.controllers.BaseController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;

import static org.springframework.web.bind.annotation.RequestMethod.POST;

@Controller
@RequestMapping("/profile-edit/persona-toggle")
public class BuyerSellerToggleController extends BaseController {

	public static final String UPGRADE_MESSAGE =
		"If you want to learn more about our pricing plans, call us at 1-877-654-WORK.";
	@Autowired private UserService userService;
	@Autowired private AuthenticationService authenticationService;


	@VelvetRope(
		venue = Venue.COMPANY,
		message = UPGRADE_MESSAGE,
		redirectPath = "/home"
	)
	@RequestMapping(
		value = "/create_work",
		method = POST)
	public String toggleCreate(@ModelAttribute("personaForm") PersonaPreference form) {
		return toggle(form);
	}

	@RequestMapping(
		value = "/perform_work",
		method = POST)
	public String togglePerform(@ModelAttribute("personaForm") PersonaPreference form) {
		return toggle(form);
	}

	@RequestMapping(
		value = "/dispatch_work",
		method = POST)
	public String toggleDispatch(@ModelAttribute("personaForm") PersonaPreference form) {
		return toggle(form);
	}

	private String toggle(PersonaPreference form) {

		form.setUserId(getCurrentUser().getId());
		userService.saveOrUpdatePersonaPreference(form);
		authenticationService.refreshSessionForUser(getCurrentUser().getId());

		return "redirect:/home";
	}

	@VelvetRope(
		venue = Venue.COMPANY,
		message = UPGRADE_MESSAGE,
		redirectPath = "/home"
	)
	@RequestMapping("/pricing")
	public void noop() {
		// TODO[Jim]: Remove this when we have a real pricing page
		//   It's a do nothing hack to work around the absence of
		//   a real pricing page.
	}
}
