package com.workmarket.data.solr.model;

import com.google.common.collect.Lists;
import com.workmarket.data.report.work.PaymentTotals;
import com.workmarket.domains.model.AbstractPagination;
import com.workmarket.domains.model.Pagination;
import com.workmarket.utility.sql.SQLOperator;
import org.apache.commons.lang.StringUtils;

import java.util.Collections;
import java.util.List;

public class WorkSearchDataPagination extends AbstractPagination<SolrWorkData> implements Pagination<SolrWorkData> {
	private boolean showAllCompanyAssignments = false;
	public static final int MAX_ROWS = 2000;
	private PaymentTotals paymentTotals = new PaymentTotals();

	public enum FILTER_KEYS {
		CLIENT_ID("work.client_company_id", SQLOperator.IN),
		COMPANY_ID("work.company_id", SQLOperator.EQUALS),
		COMPANY_IDS("work.company_id", SQLOperator.IN),
		PROJECT_ID("project.id", SQLOperator.IN),
		PARENT_ID("work.parent_id", SQLOperator.EQUALS),
		BUYER_ID("work.buyer_user_id", SQLOperator.IN),
		INDUSTRY_ID("work.industry_id", SQLOperator.EQUALS),
		WORK_STATUS("work_status_type_code", SQLOperator.EQUALS),
		START_DATE("work.schedule_from", SQLOperator.GREATER_THAN_OR_EQUAL),
		END_DATE("work.schedule_from", SQLOperator.LESS_THAN_OR_EQUAL),
		LANE_TYPE_ID("lane.lane_type_id", SQLOperator.IN),
		RESOURCE_ID("assignedUser.id", SQLOperator.EQUALS),
		RESOURCE_COMPANY_ID("assignedUser.company_id", SQLOperator.EQUALS),
		INVITED_RESOURCE_ID("resource.user_id", SQLOperator.EQUALS),
		ASSIGNED_TO_WORK("resource.assigned_to_work", SQLOperator.EQUALS),
		WORK_RESOURCE_STATUS_CODE("resource.work_resource_status_type_code", SQLOperator.EQUALS),
		WORK_SUB_STATUS_TYPE_ID("work_sub_status_type.id", SQLOperator.EQUALS),
		WORK_NUMBER("work.work_number", SQLOperator.EQUALS),
		INVOICE_ID("invoice.id", SQLOperator.EQUALS),
		HAS_OPEN_NEGOTIATION(StringUtils.EMPTY, StringUtils.EMPTY),
		HAS_UNANSWERED_QUESTION(StringUtils.EMPTY, StringUtils.EMPTY),
		WORK_TITLE("work.title", SQLOperator.LIKE),
		NEGOTIATION_TYPE("negotiation.type", SQLOperator.EQUALS),
		NEGOTIATION_INITIATED_BY_RESOURCE("negotiation.requestor_is_resource", SQLOperator.EQUALS),
		NEGOTIATION_APPROVAL_STATUS("negotiation.approval_status", SQLOperator.EQUALS),
		ADDRESS_COUNTRY("address.country", SQLOperator.EQUALS),
		// date range filters
		DUE_DATE_FROM("work.due_on", SQLOperator.GREATER_THAN_OR_EQUAL),
		DUE_DATE_TO("work.due_on", SQLOperator.LESS_THAN_OR_EQUAL),
		MODIFIED_DATE_FROM("waa.last_action_on", SQLOperator.GREATER_THAN_OR_EQUAL),
		MODIFIED_DATE_TO("waa.last_action_on", SQLOperator.LESS_THAN_OR_EQUAL),
		ACCEPTED_DATE_FROM("milestones.accepted_on", SQLOperator.GREATER_THAN_OR_EQUAL),
		ACCEPTED_DATE_TO("milestones.accepted_on", SQLOperator.LESS_THAN_OR_EQUAL),
		SENT_DATE_FROM("milestones.sent_on", SQLOperator.GREATER_THAN_OR_EQUAL),
		SENT_DATE_TO("milestones.sent_on", SQLOperator.LESS_THAN_OR_EQUAL),
		CREATED_DATE_FROM("work.created_on", SQLOperator.GREATER_THAN_OR_EQUAL),
		CREATED_DATE_TO("work.created_on", SQLOperator.LESS_THAN_OR_EQUAL),
		SCHEDULED_DATE_FROM("work.schedule_from", SQLOperator.GREATER_THAN_OR_EQUAL),
		SCHEDULED_DATE_TO("work.schedule_from", SQLOperator.LESS_THAN_OR_EQUAL),
		COMPLETED_DATE_FROM("milestones.complete_on", SQLOperator.GREATER_THAN_OR_EQUAL),
		COMPLETED_DATE_TO("milestones.complete_on", SQLOperator.LESS_THAN_OR_EQUAL),
		APPROVED_DATE_FROM("milestones.closed_on", SQLOperator.GREATER_THAN_OR_EQUAL),
		APPROVED_DATE_TO("milestones.closed_on", SQLOperator.LESS_THAN_OR_EQUAL),
		PAID_DATE_FROM("milestones.paid_on", SQLOperator.GREATER_THAN_OR_EQUAL),
		PAID_DATE_TO("milestones.paid_on", SQLOperator.LESS_THAN_OR_EQUAL);

		private final String column;
		private final String operator;

		FILTER_KEYS(String column, String operator) {
			this.column = column;
			this.operator = operator;
		}

		public String getColumn() {
			return column;
		}

		public String getOperator() {
			return operator;
		}

		public String getOperationWithParam(String paramName) {
			return getColumn() + getOperator() + " " + paramName;
		}

		public static final List<String> DATE_RANGES = Collections.unmodifiableList(
				Lists.newArrayList(
						DUE_DATE_FROM.name(),
						DUE_DATE_TO.name(),
						MODIFIED_DATE_FROM.name(),
						MODIFIED_DATE_TO.name(),
						ACCEPTED_DATE_FROM.name(),
						ACCEPTED_DATE_TO.name(),
						SENT_DATE_FROM.name(),
						SENT_DATE_TO.name(),
						CREATED_DATE_FROM.name(),
						CREATED_DATE_TO.name(),
						SCHEDULED_DATE_FROM.name(),
						SCHEDULED_DATE_TO.name(),
						COMPLETED_DATE_FROM.name(),
						COMPLETED_DATE_TO.name(),
						APPROVED_DATE_FROM.name(),
						APPROVED_DATE_TO.name(),
						PAID_DATE_FROM.name(),
						PAID_DATE_TO.name()
				)
		);
	}

	public enum SORTS {
		WORK_ID("work.id"),
		WORK_NUMBER("work.work_number"),
		STATUS("statusTypeCode"),
		TITLE("work.title"),
		CLIENT("client_company.name"),
		CITY("address.city"),
		LOCATION("address.postal_code"),
		RESOURCE("assignedUser.last_name"),
		BUYER_LAST_NAME("buyer.last_name"),
		SPEND_LIMIT("spend_limit"),
		NEXT_WORKFLOW_STEP_NAME("next_workflow_status_name"),
		DUE_DATE("dueDate"),
		AMOUNT_EARNED("amount_earned"),
		LAST_MODIFIED_DATE("waa.last_action_on"),
		INVOICE_NUMBER("invoice.invoice_number"),
		CREATED_ON("work.created_on"),
		// + 0, seen below, is a performance optimization.
		// See item #6 for an explanation:
		// http://code.openark.org/blog/mysql/7-ways-to-convince-mysql-to-use-the-right-index
		SCHEDULE_FROM("work.schedule_from + 0"),
		SENT_DATE("milestones.sent_on"),
		COMPLETED_DATE("milestones.complete_on"),
		APPROVED_DATE("milestones.closed_on"),
		PAID_DATE("milestones.paid_on"),;

		private final String column;

		SORTS(String column) {
			this.column = column;
		}

		public String getColumn() {
			return column;
		}
	}

	public void setShowAllCompanyAssignments(boolean showAllCompanyAssignments) {
		this.showAllCompanyAssignments = showAllCompanyAssignments;
	}

	public boolean isShowAllCompanyAssignments() {
		return showAllCompanyAssignments;
	}

	public PaymentTotals getPaymentTotals() {
		return paymentTotals;
	}

	public void setPaymentTotals(PaymentTotals paymentTotals) {
		this.paymentTotals = paymentTotals;
	}

	@Override
	public void setReturnAllRows(boolean returnAllRows) {
		super.setReturnAllRows(returnAllRows);
		if (isLimitMaxRows()) {
			setResultsLimit(MAX_ROWS);
		}
		setStartRow(0);
	}
}
