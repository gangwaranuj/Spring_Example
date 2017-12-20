package com.workmarket.web.controllers;

import com.codahale.metrics.MetricRegistry;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.workmarket.common.metric.WMMetricRegistryFacade;
import com.workmarket.domains.model.notification.NotificationType;
import com.workmarket.domains.model.notification.UserNotification;
import com.workmarket.domains.model.notification.UserNotificationPagination;
import com.workmarket.dto.UnreadNotificationsDTO;
import com.workmarket.service.business.UserNotificationService;
import com.workmarket.utility.CollectionUtilities;
import com.workmarket.utility.DateUtilities;
import com.workmarket.web.helpers.AjaxResponseBuilder;
import com.workmarket.web.helpers.MessageBundleHelper;
import com.workmarket.web.models.DataTablesRequest;
import com.workmarket.web.models.DataTablesResponse;
import com.workmarket.web.models.MessageBundle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.http.MediaType.TEXT_HTML_VALUE;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

@Controller
@RequestMapping("/notifications")
public class NotificationsController extends BaseController {

	private final WMMetricRegistryFacade metricFacade;
	private final UserNotificationService notificationService;
	private final MessageBundleHelper messageHelper;

	@Autowired
	public NotificationsController(final UserNotificationService notificationService,
	                               final MessageBundleHelper messageHelper,
	                               final MetricRegistry metricRegistry) {
		this.notificationService = notificationService;
		this.messageHelper = messageHelper;
		this.metricFacade = new WMMetricRegistryFacade(metricRegistry, "notificationcontroller");
	}

	@RequestMapping(value={"", "/", "/active"}, method = GET, produces = TEXT_HTML_VALUE)
	public String active() {
		return "web/pages/notifications/active";
	}

	@RequestMapping(value={"/active.json"}, method = GET, produces = APPLICATION_JSON_VALUE)
	public ModelAndView activeList(HttpServletRequest httpRequest) throws Exception {
		metricFacade.meter("getactivejson").mark();
		DataTablesRequest request = DataTablesRequest.newInstance(httpRequest);
		request.setSortableColumnMapping(new HashMap<Integer, String>() {{
			put(0, UserNotificationPagination.SORTS.DESCRIPTION.toString());
			put(1, UserNotificationPagination.SORTS.CREATED_ON.toString());
		}});
		UserNotificationPagination pagination = request.newPagination(UserNotificationPagination.class);
		pagination.addFilter(UserNotificationPagination.FILTER_KEYS.ARCHIVED, Boolean.FALSE);
		pagination.addFilter(UserNotificationPagination.FILTER_KEYS.USER_FILTERS, Boolean.FALSE);
		pagination = notificationService.findAllUserNotifications(getCurrentUser().getId(), pagination);
		DataTablesResponse<List<String>,Map<String,Object>> response = DataTablesResponse.newInstance(request, pagination);
		for (UserNotification n : pagination.getResults()) {
			List<String> data = Lists.newArrayList(
					n.getDisplayMessage(),
					DateUtilities.format("MMM d, yyyy @ h:mma", n.getCreatedOn(), getCurrentUser().getTimeZoneId()),
					null
			);
			Map<String,Object> meta = CollectionUtilities.newObjectMap(
					"uuid", n.getUuid()
			);
			if (NotificationType.BULK_UPLOAD_COMPLETE.equals(n.getNotificationType().getCode()) ||
				NotificationType.BULK_UPLOAD_FAILED.equals(n.getNotificationType().getCode())) {
				meta.put("bulkUploadJS", n.getDisplayMessage());
			}
			response.addRow(data, meta);
		}
		ModelAndView m = new ModelAndView("web/pages/notifications/active");
		m.addObject("response", response);
		return m;
	}

	@RequestMapping(
			value = "/list",
			method = GET,
			produces = APPLICATION_JSON_VALUE)
	@ResponseBody
	public String getNotifications() {
		metricFacade.meter("list").mark();
		String dataString = notificationService.findAllUserNotificationsForBullhornJson(getCurrentUser().getId());
		return (dataString == "") ? "[]" : dataString;
	}

	@RequestMapping(value="/archive", method = GET, produces = TEXT_HTML_VALUE)
	public String archive() {
		return "web/pages/notifications/archive";
	}

	@RequestMapping(value="/archive.json", method = GET, produces = APPLICATION_JSON_VALUE)
	public ModelAndView archiveList(HttpServletRequest httpRequest) throws Exception {
		metricFacade.meter("getarchivejson").mark();
		DataTablesRequest request = DataTablesRequest.newInstance(httpRequest);
		request.setSortableColumnMapping(new HashMap<Integer, String>() {{
			put(0, UserNotificationPagination.SORTS.DESCRIPTION.toString());
			put(1, UserNotificationPagination.SORTS.CREATED_ON.toString());
		}});

		UserNotificationPagination pagination = request.newPagination(UserNotificationPagination.class);
		pagination.addFilter(UserNotificationPagination.FILTER_KEYS.ARCHIVED, Boolean.TRUE);
		pagination = notificationService.findAllUserNotifications(getCurrentUser().getId(), pagination);

		DataTablesResponse<List<String>,Map<String,Object>> response = DataTablesResponse.newInstance(request, pagination);

		for (UserNotification n : pagination.getResults()) {
			List<String> data = Lists.newArrayList(
				n.getDisplayMessage(),
				DateUtilities.format("MMM d, yyyy @ h:mma", n.getCreatedOn(), getCurrentUser().getTimeZoneId()),
				null
			);

			Map<String,Object> meta = CollectionUtilities.newObjectMap(
				"uuid", n.getUuid()
			);
			response.addRow(data, meta);
		}

		ModelAndView m = new ModelAndView("web/pages/notifications/archive");
		m.addObject("response", response);
		return m;
	}

	@RequestMapping(value="/{uuid}/archive", method = POST)
	@ResponseBody
	public AjaxResponseBuilder archiveNotification(@PathVariable("uuid") String uuid, RedirectAttributes redirectAttributes) {
		metricFacade.meter("archive").mark();
		notificationService.archiveUserNotification(uuid, getCurrentUser().getId());

		MessageBundle messages = messageHelper.newFlashBundle(redirectAttributes);
		messageHelper.addSuccess(messages, "notifications.archive.success");

		return new AjaxResponseBuilder()
			.setRedirect("/notifications")
			.setSuccessful(true);
	}

	@RequestMapping(
		value = "/all_viewed",
		method = POST,
		produces = APPLICATION_JSON_VALUE)
	@ResponseBody
	public AjaxResponseBuilder setViewedAt(@RequestParam(required = false) String startUuid, @RequestParam(required = false) String endUuid) {
		metricFacade.meter("setallviewed").mark();
		notificationService.setViewedAtNotification(getCurrentUser().getId(), new UnreadNotificationsDTO(startUuid, endUuid));
		return new AjaxResponseBuilder().setSuccessful(true);
	}

	@RequestMapping(
		value="/unread_notifications",
		method = GET,
		produces = APPLICATION_JSON_VALUE)
	@ResponseBody
	public AjaxResponseBuilder bullhornCounter() throws Exception {
		metricFacade.meter("getunread").mark();
		return new AjaxResponseBuilder()
			.setSuccessful(true)
			.setData(ImmutableMap.<String, Object>of(
				"notifications", notificationService.getUnreadNotificationsInfoByUser(getCurrentUser().getId())));
	}
}
