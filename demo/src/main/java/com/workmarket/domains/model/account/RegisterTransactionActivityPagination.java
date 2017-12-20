package com.workmarket.domains.model.account;

import com.workmarket.domains.model.AbstractPagination;
import com.workmarket.domains.model.Pagination;

public class RegisterTransactionActivityPagination extends AbstractPagination<RegisterTransactionActivity> implements Pagination<RegisterTransactionActivity> {

	public enum FILTER_KEYS {
		TRANSACTION_DATE_FROM,
		TRANSACTION_DATE_TO,
		WORK_STATUS_TYPE_CODE,
		SUBSTATUS_TYPE_CODE,
		BUYER_ID,
		CLIENT_COMPANY_ID,
		PROJECT_ID,
		TRANSACTION_TYPE,
		ASSIGNMENT_SCHEDULED_DATE_FROM,
		ASSIGNMENT_SCHEDULED_DATE_TO,
		ASSIGNMENT_APPROVED_DATE_FROM,
		ASSIGNMENT_APPROVED_DATE_TO,
		ASSIGNMENT_PAID_DATE_FROM,
		ASSIGNMENT_PAID_DATE_TO;
	}

	public RegisterTransactionActivityPagination() {
		super(false);
	}

	public RegisterTransactionActivityPagination(boolean returnAllRows) {
		super(returnAllRows);
	}

	public enum SORTS {
		TRANSACTION_DATE("rt.effective_date"),
		WORK_NUMBER("work.work_number"),
		TRANSACTION_TYPE("rtType.code"),
		ASSIGNMENT_SCHEDULED_DATE("work.schedule_from"),
		ASSIGNMENT_APPROVED_DATE("work.closed_on"),
		ASSIGNMENT_PAID_DATE("milestones.paid_on");

		private String column;

		SORTS(String column) {
			this.column = column;
		}

		public String getColumn() {
			return column;
		}
	}

	public boolean hasTransactionDateFilter() {
		if (getFilters() != null) {
			return (getFilters().get(RegisterTransactionActivityPagination.FILTER_KEYS.TRANSACTION_DATE_FROM) != null || getFilters().get(
					RegisterTransactionActivityPagination.FILTER_KEYS.TRANSACTION_DATE_TO) != null);
		}
		return false;
	}

	public boolean hasAssignmentScheduledDateFilter() {
		if (getFilters() != null) {
			return (getFilters().get(RegisterTransactionActivityPagination.FILTER_KEYS.ASSIGNMENT_SCHEDULED_DATE_FROM) != null || getFilters().get(
					RegisterTransactionActivityPagination.FILTER_KEYS.ASSIGNMENT_SCHEDULED_DATE_TO) != null);
		}
		return false;
	}

	public boolean hasAssignmentApprovedDateFilter() {
		if (getFilters() != null) {
			return (getFilters().get(RegisterTransactionActivityPagination.FILTER_KEYS.ASSIGNMENT_APPROVED_DATE_FROM) != null || getFilters().get(
					RegisterTransactionActivityPagination.FILTER_KEYS.ASSIGNMENT_APPROVED_DATE_TO) != null);
		}
		return false;
	}

	public boolean hasAssignmentPaidDateFilter() {
		if (getFilters() != null) {
			return (getFilters().get(RegisterTransactionActivityPagination.FILTER_KEYS.ASSIGNMENT_PAID_DATE_FROM) != null || getFilters().get(
					RegisterTransactionActivityPagination.FILTER_KEYS.ASSIGNMENT_PAID_DATE_TO) != null);
		}
		return false;
	}

	public boolean hasBuyerFilter() {
		if (getFilters() != null) {
			return (getFilters().get(RegisterTransactionActivityPagination.FILTER_KEYS.BUYER_ID) != null);
		}
		return false;
	}

	public boolean hasWorkStatusTypeFilter() {
		if (getFilters() != null) {
			return (getFilters().get(RegisterTransactionActivityPagination.FILTER_KEYS.WORK_STATUS_TYPE_CODE) != null);
		}
		return false;
	}

	public boolean hasClientCompanyFilter() {
		if (getFilters() != null) {
			return (getFilters().get(RegisterTransactionActivityPagination.FILTER_KEYS.CLIENT_COMPANY_ID) != null);
		}
		return false;
	}

	public boolean hasProjectFilter() {
		if (getFilters() != null) {
			return (getFilters().get(RegisterTransactionActivityPagination.FILTER_KEYS.PROJECT_ID) != null);
		}
		return false;
	}

	public boolean hasSubStatusFilter() {
		if (getFilters() != null) {
			return (getFilters().get(RegisterTransactionActivityPagination.FILTER_KEYS.SUBSTATUS_TYPE_CODE) != null);
		}
		return false;
	}

	public boolean hasTransactionTypeFilter() {
		if (getFilters() != null) {
			return (getFilters().get(RegisterTransactionActivityPagination.FILTER_KEYS.TRANSACTION_TYPE) != null);
		}
		return false;
	}
}

