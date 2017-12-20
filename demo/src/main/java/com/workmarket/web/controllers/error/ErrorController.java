package com.workmarket.web.controllers.error;

import com.workmarket.web.controllers.BaseController;
import org.springframework.mobile.device.site.SitePreference;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;

import static org.springframework.web.bind.annotation.RequestMethod.GET;

@Controller
@RequestMapping("/error")
public class ErrorController extends BaseController {

	@RequestMapping(
		value = "/no_access",
		method = GET)
	public String noAccess() {

		return "web/pages/error/no_access";
	}

	@RequestMapping(
		value = "/not_confirmed",
		method = GET)
	public String notConfirmed() {

		return "web/pages/error/not_confirmed";
	}

	@RequestMapping(
		value = "/404",
		method = GET)
	public String show404(
		HttpServletRequest request, SitePreference site) {

		if (isMobile(request, site)) {
			return "mobile/pages/v2/error/404";
		}

		return "web/pages/error/404";
	}

	@RequestMapping(
		value = "/405",
		method = GET)
	public String show405() {

		return "web/pages/error/general-error";
	}

	@RequestMapping(
		value = "/500",
		method = GET)
	public String show500(HttpServletRequest request, SitePreference site) {
		if (isMobile(request, site)) {
			return "mobile/pages/error/500";
		}

		return "web/pages/error/500";
	}
}