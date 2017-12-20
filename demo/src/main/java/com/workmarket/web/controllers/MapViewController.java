package com.workmarket.web.controllers;

import com.google.common.collect.Lists;
import com.workmarket.domains.model.DateRangeUtilities;
import com.workmarket.domains.model.WorkStatusType;
import com.workmarket.domains.work.service.dashboard.WorkDashboardService;
import com.workmarket.redis.repositories.WorkSearchRequestRepository;
import com.workmarket.search.request.work.WorkSearchRequest;
import com.workmarket.search.request.work.WorkSearchRequestUserType;
import com.workmarket.search.response.work.DashboardResponse;
import com.workmarket.search.response.work.DashboardResult;
import com.workmarket.service.search.work.WorkSearchService;
import com.workmarket.utility.CollectionUtilities;
import com.workmarket.web.helpers.WorkStatusFilterHelper;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Controller
@RequestMapping("map")
public class MapViewController extends BaseController {
	private static final Log logger = LogFactory.getLog(MapViewController.class);

	@Autowired WorkSearchService workSearchService;
	@Autowired WorkDashboardService workDashboardService;
	@Autowired WorkSearchRequestRepository workSearchRequestRepository;

	@RequestMapping(method = RequestMethod.GET)

	public String index() {
		return "web/pages/map/index";
	}

	@RequestMapping(value = {"/useDashboardData"}, method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody List<Map<String, Object>> useDashboardData(@RequestParam String status){

		final int PAGE_SIZE = 5000;
		WorkSearchRequest request;

		request = new WorkSearchRequest()
			.setUserNumber(getCurrentUser().getUserNumber())
			.setPageSize(PAGE_SIZE)
			.setShowAllAtCompany(true)
			.setWorkSearchRequestUserType(getCurrentUser().getWorkSearchRequestUserType());
		request.setIgnoreVirtual(true);

		Set<WorkStatusType> workStatusFilterList = WorkStatusFilterHelper.createWorkStatusFilter(status);
		request.setWorkStatusFilters(workStatusFilterList);

		workSearchRequestRepository.set(getCurrentUser().getId(), request);
		DashboardResponse response = workDashboardService.getDashboard(request);
		List<DashboardResult> workData = response.getDashboardResultList().getResults();

		if(CollectionUtils.isEmpty(workData)) {
			return Collections.emptyList();
		}

		List<Map<String, Object>> data = Lists.newArrayList();

		for (DashboardResult item : workData) {
			if (item.getAddress() != null && item.getAddress().getLatitude() != null && item.getAddress().getLongitude() != null) {
				Map<String, Object> workItem = CollectionUtilities.newObjectMap(
					"workNumber", item.getWorkNumber(),
					"rawStatus", item.getWorkStatusTypeCode(),
					"status", item.getFormattedWorkStatusType(getCurrentUser().getWorkSearchRequestUserType(), getCurrentUser().getCompanyId(), item.getWorkStatusTypeCode()),
					"title", item.getTitle() == null ? "No title": item.getTitle(),
					"scheduledDate", item.getAssignmentAppointment() != null ? DateRangeUtilities.format("MMM dd hh:mm aa", "MMM dd hh:mm aa z", " - ", item.getAssignmentAppointment(), item.getTimeZoneId()) : "-",
					"scheduleFrom", item.getScheduleFrom(),
					"price", item.getFormattedPrice(false),
					"lat", item.getAddress().getLatitude(),
					"lng", item.getAddress().getLongitude()
				);
				data.add(workItem);
			}
		}
		return data;
	}
}
