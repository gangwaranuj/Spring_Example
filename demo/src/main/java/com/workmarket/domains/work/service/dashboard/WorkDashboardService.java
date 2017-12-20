package com.workmarket.domains.work.service.dashboard;

import com.workmarket.search.request.work.WorkSearchRequest;
import com.workmarket.search.response.work.DashboardResponse;
import com.workmarket.search.response.work.DashboardResponseSidebar;

public interface WorkDashboardService {

	DashboardResponseSidebar generateWorkDashboardSidebar(WorkSearchRequest request);

	DashboardResponse getDashboard(WorkSearchRequest request);
}