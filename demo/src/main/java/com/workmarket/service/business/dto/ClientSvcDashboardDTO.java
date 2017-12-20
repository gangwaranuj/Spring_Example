package com.workmarket.service.business.dto;

public class ClientSvcDashboardDTO {

	private Integer newUsers;
	private Integer pendingProfileUpdates;
	private Integer activeWork;
	private Integer concerns;

	public void setNewUsers(Integer newUsers) {
		this.newUsers = newUsers;
	}

	public Integer getNewUsers() {
		return newUsers;
	}

	public void setPendingProfileUpdates(Integer pendingProfileUpdates) {
		this.pendingProfileUpdates = pendingProfileUpdates;
	}

	public Integer getPendingProfileUpdates() {
		return pendingProfileUpdates;
	}

	public void setActiveWork(Integer activeWork) {
		this.activeWork = activeWork;
	}

	public Integer getActiveWork() {
		return activeWork;
	}

	public void setConcerns(Integer concerns) {
		this.concerns = concerns;
	}

	public Integer getConcerns() {
		return concerns;
	}

}
