package com.workmarket.domains.work.service.dashboard;

import com.workmarket.domains.model.user.UserDashboardInfo;
import com.workmarket.search.request.work.WorkSearchRequest;
import com.workmarket.search.response.work.DashboardResult;
import com.workmarket.search.response.work.DashboardResultList;
import com.workmarket.search.response.work.WorkSearchResponse;
import com.workmarket.web.forms.work.WorkDashboardForm;

import java.util.Map;

public interface DashboardResultService {
	Map<String, Object> getMappedWorkItem(WorkDashboardForm form, DashboardResult item, UserDashboardInfo userDashboardInfo);

	void decorateDashBoardResultFlags(WorkSearchRequest request, WorkSearchResponse response, DashboardResultList results);

	int getPendingApprovalsCount(WorkSearchRequest request, WorkSearchResponse response);
}
