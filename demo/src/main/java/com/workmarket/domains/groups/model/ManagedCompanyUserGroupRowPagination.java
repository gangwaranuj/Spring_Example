package com.workmarket.domains.groups.model;

import com.workmarket.domains.model.AbstractPagination;
import com.workmarket.domains.model.Pagination;

public class ManagedCompanyUserGroupRowPagination extends AbstractPagination<ManagedCompanyUserGroupRow> implements Pagination<ManagedCompanyUserGroupRow> {

	public ManagedCompanyUserGroupRowPagination() {
		super();
	}

	public ManagedCompanyUserGroupRowPagination(boolean returnAllRows) {
		super(returnAllRows);
	}

	private boolean showPrivateGroups = false;
	private boolean showOnlyActiveGroups = true;
	private boolean showSharedGroups = false;

	private boolean isCurrentUserAnAdmin = true;
	private Long currentUserCompanyId;

	public enum FILTER_KEYS {
		OWNER("ug.creator_id"),
		COMPANY("ug.company_id"),
		PUBLISHED("pg.id"),
		ACTIVATED("ug.active_flag");

		private String column;

		FILTER_KEYS(String column) {
			this.column = column;
		}

		public String getColumn() {
			return column;
		}
	}

	public enum SORTS {
		GROUP_NAME("ug.name"),
		MEMBERS("memberCount"),
		MESSAGES("messageCount"),
		PENDING_APPLICANT_COUNT("pendingApplicantCount"),
		COMPANY_NAME("companyName"),
		INVITED_ON("date_invited"),
		APPLIED_ON("date_applied"),
		APPROVED_ON("date_approved"),
		OWNER_LAST_NAME("u.last_name"),
		PUBLISHED_ON("pg.published_on"),
		CREATED_ON("ug.created_on"),
		INDUSTRY("ug.industry_id");

		private String column;

		SORTS(String column) {
			this.column = column;
		}

		public String getColumn() {
			return column;
		}
	}

	public void setIsCurrentUserAnAdmin(boolean currentUserAnAdmin) { this.isCurrentUserAnAdmin = currentUserAnAdmin; }

	public boolean isCurrentUserAnAdmin() { return isCurrentUserAnAdmin; }

	public boolean isShowPrivateGroups() { return showPrivateGroups; }

	public void setShowPrivateGroups(boolean showPrivateGroups) {
		this.showPrivateGroups = showPrivateGroups;
	}

	public boolean isShowSharedGroups() { return showSharedGroups; }

	public void setShowSharedGroups(boolean showSharedGroups) { this.showSharedGroups = showSharedGroups; }

	public Long getCurrentUserCompanyId() { return currentUserCompanyId; }

	public void setCurrentUserCompanyId(Long currentUserCompanyId) { this.currentUserCompanyId = currentUserCompanyId; }

	public boolean isShowOnlyActiveGroups() { return showOnlyActiveGroups; }

	public void setShowOnlyActiveGroups(boolean showOnlyActiveGroups) { this.showOnlyActiveGroups = showOnlyActiveGroups; }

}
