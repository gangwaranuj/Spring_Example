package com.workmarket.web.controllers.mobile;

import com.codahale.metrics.MetricRegistry;
import com.google.common.collect.ImmutableMap;
import com.workmarket.common.metric.WMMetricRegistryFacade;
import com.workmarket.domains.model.notification.UserNotificationPagination;
import com.workmarket.service.business.UserNotificationService;
import com.workmarket.web.controllers.BaseController;
import com.workmarket.web.helpers.AjaxResponseBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Map;

import static org.springframework.web.bind.annotation.RequestMethod.GET;

@Controller
@RequestMapping("/mobile/notifications")
public class MobileNotificationController extends BaseController {

	private final WMMetricRegistryFacade metricFacade;
	private final UserNotificationService userNotificationService;

	@Autowired
	public MobileNotificationController(final UserNotificationService userNotificationService,
	                                    final MetricRegistry metricRegistry) {
		this.userNotificationService = userNotificationService;
		this.metricFacade = new WMMetricRegistryFacade(metricRegistry, "mobilenotificationcontroller");
	}

	@RequestMapping(value = "/{page}", method = GET)
    public String notifications(Model model, @PathVariable("page") int page) {
		metricFacade.meter("getpage").mark();
		UserNotificationPagination pagination = getNotificationPaginationForPage(page);
		addDetails(model, pagination);
		model.addAttribute("page", page);
		return "mobile/pages/v2/notifications/index";
	}

	@RequestMapping(value = "/list", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody AjaxResponseBuilder list() {
		return list(1);
	}

	@RequestMapping(value = "/list/{page}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody AjaxResponseBuilder list(@PathVariable("page") Integer page) {
		metricFacade.meter("listpage").mark();
		AjaxResponseBuilder response = new AjaxResponseBuilder().setSuccessful(false);

		UserNotificationPagination notificationPagination = getNotificationPaginationForPage(page);

		Map<String, Object> pagination = ImmutableMap.<String, Object>of(
				"page", page,
				"hasMore", page < notificationPagination.getNumberOfPages()
		);

		response.setData(ImmutableMap.of(
				"notifications", notificationPagination.getResults(),
				"pagination", pagination
		));

		response.setSuccessful(true);

		return response;
	}

	@RequestMapping(method = GET)
	public String notificationFirstPage(Model model) {
		metricFacade.meter("firstpage").mark();
		UserNotificationPagination pagination = getNotificationPaginationForPage(1);
		addDetails(model, pagination);
		final Long userId = getCurrentUser().getId();
		userNotificationService.setViewedAtNotificationAsync(userId, userNotificationService.getUnreadNotificationsInfoByUser(userId));
		model.addAttribute("page", 1);
		return "mobile/pages/v2/notifications/index";
	}

	private UserNotificationPagination getNotificationPaginationForPage(int page) {
		UserNotificationPagination pagination = UserNotificationPagination.newBullhornPagination();
		pagination.setPage(page);
		pagination = userNotificationService.findAllUserNotifications(getCurrentUser().getId(), pagination);
		return pagination;
	}

	private void addDetails(Model model, UserNotificationPagination pagination) {
		model.addAttribute("notifications", pagination.getResults());
		model.addAttribute("hasMore", pagination.getCurrentPage() < pagination.getNumberOfPages());
		model.addAttribute("title", "Notifications");
	}
}
