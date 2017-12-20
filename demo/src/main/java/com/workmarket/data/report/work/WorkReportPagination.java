package com.workmarket.data.report.work;

import java.util.List;

import com.workmarket.domains.model.AbstractPagination;
import com.workmarket.domains.model.Pagination;

public class WorkReportPagination extends AbstractPagination<WorkReportRow> implements Pagination<WorkReportRow> {
	
	private List<String> workStatusTypeFilter;
	
	private List<String> workSubStatusTypeFilter;
	
    public WorkReportPagination() {}

    public WorkReportPagination(boolean returnAllRows) {
        super(returnAllRows);
    }

private boolean showAllCompanyAssignments = false; 
	
	public enum FILTER_KEYS {
	
		CLIENT_ID("client_company.id"),
		COMPANY_ID("work.company_id"),
		BUYER_ID("buyer.id"),
		INDUSTRY_ID("work.industry_id"),
		WORK_STATUS("work_status_type_code"),
		START_DATE("work.schedule_from"),
		END_DATE("work.schedule_from"),
		RESOURCE_ID("assignedResource.user_id"),
		RESOURCE_COMPANY_ID("assignedUser.company_id"),
		FROM_PRICE("spend_limit"),
		TO_PRICE("spend_limit"),
		PROJECT_ID("project.project_id"),
		LANE_TYPE_ID("lane.lane_type_id"),
		WORK_SUB_STATUS_CODE("work_sub_status_type.code"),
		ASSIGNMENT_APPROVED_DATE_FROM("work.closed_on"),
		ASSIGNMENT_APPROVED_DATE_TO("work.closed_on"),
		ASSIGNMENT_PAID_DATE_FROM("milestones.paid_on"),
		ASSIGNMENT_PAID_DATE_TO("milestones.paid_on");
		
		private String column;

		FILTER_KEYS(String column) {
			this.column = column;
		}

		public String getColumn() {
			return column;
		}
	}

	public enum SORTS {
		
		WORK_ID("work.id"),
		WORK_NUMBER("work.work_number"),
		STATUS("work_status_type_code"),
		TITLE("work.title"),
		CLIENT("client_company.name"),
		CITY("address.city"),
		LOCATION("address.postal_code"),
		SCHEDULE_FROM("work.schedule_from"),
		CREATED_ON("work.created_on"),
		RESOURCE("assignedUser.last_name"),
		BUYER_LAST_NAME("buyer.last_name"),
		SPEND_LIMIT("spend_limit"),
		COMPLETED_DATE("milestones.complete_on"),
		CLOSED_DATE("milestones.closed_on"),
		WORK_PRICE("work_price"),
		WM_FEE("buyer_fee"),
		HOURS_WORKED("hours_worked"),
		SENT_DATE("milestones.sent_on"),
		DUE_DATE("work.due_on"),
		PAYMENT_TERMS_DAYS("work.payment_terms_days"),
		STATE("state.short_name"),
		ADDRESS1("address.line1"),
		ADDRESS2("address.line2"),
		HOURS_BUDGETED("hoursBudgeted"), 
		INVOICE_NUMBER("invoice_number"),
		WORK_TOTAL_COST("totalCost"),
		NEGOTIATION_NOTE("note.note_content"),
		NEGOTIATION_STATUS("work_negotiation.approval_status"),
		NEGOTIATION_TYPE("work_negotiation.type"),
		NEGOTIATION_DATE("work_negotiation.requested_on"),
		NEGOTIATION_APPROVED_ON("work_negotiation.approved_on");

		
		private String column;

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
	
	public List<String> getWorkStatusTypeFilter() {
		return workStatusTypeFilter;
	}
	
	public void setWorkStatusTypeFilter(List<String> workStatusTypeFilter) {
		this.workStatusTypeFilter = workStatusTypeFilter;
	}

	public List<String> getWorkSubStatusTypeFilter() {
		return workSubStatusTypeFilter;
	}

	public void setWorkSubStatusTypeFilter(List<String> workSubStatusTypeFilter) {
		this.workSubStatusTypeFilter = workSubStatusTypeFilter;
	}
	
	public String getDefaultFilter(boolean isBuyer) {
		if (isBuyer) {
			if (isShowAllCompanyAssignments()) {
				return FILTER_KEYS.COMPANY_ID.getColumn() + " = :companyId ";
			} else {
				return FILTER_KEYS.BUYER_ID.getColumn() + " = :userId ";
			}
		}
		else {
			if (isShowAllCompanyAssignments()) {
				return FILTER_KEYS.RESOURCE_COMPANY_ID.getColumn() + " = :companyId ";
			} else {
				return FILTER_KEYS.RESOURCE_ID.getColumn() + " = :userId ";
			}
		}
	}

	public double getPriceTotal() {
		double totalDue = 0d;
		for (WorkReportRow item : getResults()) {
			if (item.getWorkOverridePrice() != null) {
				totalDue += WorkReportRow.calculateSpendLimitWithFee(item.getWorkOverridePrice(), item.getWorkFeePercentage());
			} else {
				totalDue += item.getPrice();
			}
		}
		return totalDue;
	}
}
