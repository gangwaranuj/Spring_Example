
package com.workmarket.web.controllers;

import com.workmarket.domains.model.VisitedResource;
import com.workmarket.service.tracking.ViewedResourceTrackingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class ViewedResourceTrackingController extends BaseController {

	@Autowired private ViewedResourceTrackingService viewedResourceTrackingService;

	@RequestMapping(value={"/tracking",}, method=RequestMethod.GET,produces="application/json")
	public @ResponseBody Map<String,List<String>> trackingJoyrides() {
		Long userId = getCurrentUser().getId();
		Map<String,List<String>> visitedList = new HashMap<>();
		visitedList.put("visitedList", viewedResourceTrackingService.getViewedResourcesListByUserId(userId));
		return visitedList;
	}

	@RequestMapping(value={"/tracking/merge"}, method=RequestMethod.GET)
	public @ResponseBody Map<String,List<String>> trackingJoyridesMerge(@RequestParam(value="resourceName") String resourceName) {
		viewedResourceTrackingService.merge(new VisitedResource(getCurrentUser().getId(), resourceName));
		return trackingJoyrides();
	}

}
