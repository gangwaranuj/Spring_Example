package com.workmarket.domains.work.service.dashboard;

import com.workmarket.web.forms.feed.FeedRequestParams;
import com.workmarket.domains.model.WorkStatusType;
import com.workmarket.search.request.work.WorkSearchRequest;
import com.workmarket.search.response.work.DashboardResponse;
import com.workmarket.search.response.work.DashboardResponseSidebar;
import com.workmarket.search.response.work.DashboardResultList;
import com.workmarket.search.response.work.WorkSearchResponse;
import com.workmarket.service.business.feed.Feed;
import com.workmarket.domains.authentication.model.ExtendedUserDetails;

import java.util.List;
import java.util.Map;

public interface MobileDashboardService {

    public static final Integer DEFAULT_ASSIGNMENT_LIST_PAGE_SIZE = 10;
	public static final Integer DEFAULT_ASSIGNMENT_FEED_PAGE_SIZE = 25;

    DashboardResponseSidebar
		getMobileHomeCounts
		(
			Long userId,
			String userNumber
		);

	/*
    DashboardResponse
		getAssignmentListByStatus
		(
			ExtendedUserDetails user,
			WorkStatusType statusType,
			Integer page
		);
	*/

    DashboardResponse
		getAssignmentListByStatus
		(
			ExtendedUserDetails user,
			WorkStatusType statusType,
			Integer page,
			Integer pageSize,
			String sort
		);

    Feed
		getWorkFeed
		(
			Long userId,
			String latitude,
			String longitude,
			Integer page
		);

    Feed
		getWorkFeed
		(
			Long userId,
			String latitude,
			String longitude,
			Integer page,
			Integer pageSize
		);

    Feed
		getWorkFeed
		(
			Long userId,
			FeedRequestParams params
		);

    WorkSearchResponse
		executeSearch
		(
			Long userId,
			WorkSearchRequest request
		);

    List<Map<String, Object>>
		parseResults
		(
			ExtendedUserDetails user,
			DashboardResultList dashboardResultList
		);

    List<Map<String, Object>>
		parseResults
		(
			ExtendedUserDetails user,
			Feed feed
		);

    WorkSearchRequest
		buildSearchRequest
		(
			String userNumber,
			WorkStatusType workStatusType,
			Integer page,
			Integer pageSize
		);
}
