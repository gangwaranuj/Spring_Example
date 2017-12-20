package com.workmarket.domains.model.reporting.subscriptions;

import com.workmarket.domains.model.AbstractPagination;
import com.workmarket.domains.model.Pagination;

public class SubscriptionReportPagination extends AbstractPagination<SubscriptionReportRow> implements Pagination<SubscriptionReportRow> {

	public SubscriptionReportPagination() {}

	public SubscriptionReportPagination(boolean returnAllRows) {
		super(returnAllRows);
	}

	private SubscriptionAggregate subscriptionAggregate;

	public enum FILTER_KEYS {}

	public enum SORTS {}

	public SubscriptionAggregate getSubscriptionAggregate() {
		return subscriptionAggregate;
	}

	public void setSubscriptionAggregate(SubscriptionAggregate subscriptionAggregate) {
		this.subscriptionAggregate = subscriptionAggregate;
	}
}