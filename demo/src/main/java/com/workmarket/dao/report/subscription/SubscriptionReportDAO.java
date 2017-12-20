package com.workmarket.dao.report.subscription;

import com.workmarket.domains.model.reporting.subscriptions.SubscriptionAggregate;
import com.workmarket.domains.model.reporting.subscriptions.SubscriptionReportPagination;

public interface SubscriptionReportDAO {

	SubscriptionReportPagination getStandardReport(SubscriptionReportPagination pagination);

	SubscriptionReportPagination getUsageReport(SubscriptionReportPagination pagination);

	SubscriptionAggregate getSubscriptionAggregateReport(SubscriptionReportPagination pagination);
}
