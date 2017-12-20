package com.workmarket.service.business.account;

import com.workmarket.domains.model.reporting.subscriptions.SubscriptionReportPagination;

public interface SubscriptionReportService {

	SubscriptionReportPagination getStandardReport(SubscriptionReportPagination pagination);

	SubscriptionReportPagination getUsageReport(SubscriptionReportPagination pagination);

}
