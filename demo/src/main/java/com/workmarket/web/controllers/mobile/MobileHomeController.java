package com.workmarket.web.controllers.mobile;

import com.workmarket.domains.work.service.dashboard.MobileDashboardService;
import com.workmarket.search.response.work.DashboardResponseSidebar;
import com.workmarket.search.response.work.DashboardStatus;
import com.workmarket.service.business.UserService;
import com.workmarket.service.infra.event.EventRouter;
import com.workmarket.domains.authentication.model.ExtendedUserDetails;
import com.workmarket.utility.ModelEnumUtilities;
import com.workmarket.web.controllers.BaseController;
import com.workmarket.web.helpers.AjaxResponseBuilder;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Map;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.http.MediaType.TEXT_HTML_VALUE;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

@Controller
@RequestMapping("/mobile")
public class MobileHomeController extends BaseController {
	private static final Log logger = LogFactory.getLog(MobileHomeController.class);
	@Autowired private MobileDashboardService mobileDashboardService;
	@Autowired private UserService userService;
	@Autowired private EventRouter eventRouter;

	private static final String NULL_DEVICE_TOKEN = "(null)";

	@ModelAttribute("WorkStatusType")
	private Map<String,Object> getWorkStatusTypes() {
		return ModelEnumUtilities.workStatusTypes;
	}

	@RequestMapping(method = GET)
	public String index() {
		return "mobile/pages/v2/home/index";
	}

	@RequestMapping(
		value = "/welcome",
		method = GET,
		produces = TEXT_HTML_VALUE)
	public String list() {
		return "mobile/pages/public/welcome";
	}

	@RequestMapping(
		value = "/help",
		method = GET,
		produces = TEXT_HTML_VALUE)
	public String help() {
		return "mobile/pages/v2/home/help";
	}

	@RequestMapping(
		value="/register_device",
		method = POST,
		produces = APPLICATION_JSON_VALUE)
	public @ResponseBody
	AjaxResponseBuilder register(
		@RequestParam("regid") String regid,
		@RequestParam("type") String type) {

		AjaxResponseBuilder response = new AjaxResponseBuilder().setSuccessful(false);

		ExtendedUserDetails user = getCurrentUser();
		if (regid != null && !regid.equals(NULL_DEVICE_TOKEN) && type != null) {
			userService.registerDevice(user.getId(), regid, type);
			return response.setSuccessful(true);
		}

		logger.error(String.format("Failed to register mobile device: regId: %s, type: %s, userId: %s", regid, type, user.getId()));

		return response;
	}

	@RequestMapping(
		value = "/counts",
		method = GET,
		produces = APPLICATION_JSON_VALUE)
	public @ResponseBody Map<String,DashboardStatus> counts() {

		DashboardResponseSidebar sidebar = mobileDashboardService.getMobileHomeCounts(getCurrentUser().getId(),getCurrentUser().getUserNumber());

		return sidebar.getDashboardStatuses();
	}
}
