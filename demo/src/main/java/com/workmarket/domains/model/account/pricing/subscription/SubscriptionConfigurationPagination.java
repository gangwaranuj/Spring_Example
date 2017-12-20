package com.workmarket.domains.model.account.pricing.subscription;

import com.workmarket.domains.model.AbstractPagination;
import com.workmarket.domains.model.Pagination;

public class SubscriptionConfigurationPagination extends AbstractPagination<SubscriptionConfiguration> implements Pagination<SubscriptionConfiguration> {

	public SubscriptionConfigurationPagination(boolean returnAllRows) {
		super(returnAllRows);
	}

	public enum FILTER_KEYS {
		COMPANY_ID,
		APPROVAL_STATUS,
		EFFECTIVE_DATE_FROM,
		EFFECTIVE_DATE_TO,
		SUBSCRIPTION_STATUS;
	}

	public enum SORTS {
		EFFECTIVE_DATE("effectiveDate");

		private final String column;

		private SORTS(String column) {
			this.column = column;
		}

		public String getColumn() {
			return column;
		}

	}
}
