package com.workmarket.domains.work.service.resource;

public class WorkResourceAddOptions {
	private final boolean enforceMaxResource;
	private final boolean notifyUsers;


	public WorkResourceAddOptions(boolean enforceMaxResource) {
		this.enforceMaxResource = enforceMaxResource;
		this.notifyUsers = true;
	}
	
	public boolean isEnforceMaxResource() {
		return enforceMaxResource;
	}

	public boolean isNotifyUsers() {
		return notifyUsers;
	}
	
	
	@Override
	public String toString() {
		return "WorkResourceAddOptions ["
			+ "enforceMaxResource=" + enforceMaxResource
			+ ", notifyUsers=" + notifyUsers
			+ "]";
	}
}
