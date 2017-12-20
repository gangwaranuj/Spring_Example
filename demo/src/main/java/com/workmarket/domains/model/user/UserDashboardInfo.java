package com.workmarket.domains.model.user;

public class UserDashboardInfo {
	private Long id;
	private Long companyId;
	private boolean canManageWork;
	private boolean canApproveWork;

	public UserDashboardInfo setId(Long id) {
		this.id = id;
		return this;
	}

	public Long getId() {
		return id;
	}

	public UserDashboardInfo setCompanyId(Long companyId) {
		this.companyId = companyId;
		return this;
	}

	public Long getCompanyId() {
		return companyId;
	}

	public UserDashboardInfo setCanManageWork(boolean canManageWork) {
		this.canManageWork = canManageWork;
		return this;
	}

	public boolean isCanManageWork() {
		return canManageWork;
	}

	public UserDashboardInfo setCanApproveWork(boolean canApproveWork) {
		this.canApproveWork = canApproveWork;
		return this;
	}

	public boolean isCanApproveWork() {
		return canApproveWork;
	}
}
