package com.workmarket.domains.model.filter;

import java.util.List;

public class WorkSubStatusTypeFilter {

	private boolean clientVisible = true;
	private boolean resourceVisible = false;
	private boolean showDeactivated = false;
	private boolean showCustomSubStatus = false;
	private boolean showSystemSubStatus = true;
	private boolean showRequiresRescheduleSubStatus = true;
	private Long workId;
	private List<String> triggeredBy;

	public boolean isClientVisible() {
		return clientVisible;
	}

	public WorkSubStatusTypeFilter setClientVisible(boolean clientVisible) {
		this.clientVisible = clientVisible;
		return this;
	}

	public boolean isResourceVisible() {
		return resourceVisible;
	}

	public WorkSubStatusTypeFilter setResourceVisible(boolean resourceVisible) {
		this.resourceVisible = resourceVisible;
		return this;
	}

	public boolean isShowDeactivated() {
		return showDeactivated;
	}

	public WorkSubStatusTypeFilter setShowDeactivated(boolean showDeactivated) {
		this.showDeactivated = showDeactivated;
		return this;
	}

	public boolean isShowCustomSubStatus() {
		return showCustomSubStatus;
	}

	public WorkSubStatusTypeFilter setShowCustomSubStatus(boolean showCustomSubStatus) {
		this.showCustomSubStatus = showCustomSubStatus;
		return this;
	}

	public boolean isShowSystemSubStatus() {
		return showSystemSubStatus;
	}

	public WorkSubStatusTypeFilter setShowSystemSubStatus(boolean showSystemSubStatus) {
		this.showSystemSubStatus = showSystemSubStatus;
		return this;
	}

	public WorkSubStatusTypeFilter setTriggeredBy(List<String> triggeredBy) {
		this.triggeredBy = triggeredBy;
		return this;
	}

	public List<String> getTriggeredBy() {
		return triggeredBy;
	}

	public WorkSubStatusTypeFilter setShowRequiresRescheduleSubStatus(boolean showRequiresRescheduleSubStatus) {
		this.showRequiresRescheduleSubStatus = showRequiresRescheduleSubStatus;
		return this;
	}

	public boolean isShowRequiresRescheduleSubStatus() {
		return showRequiresRescheduleSubStatus;
	}
	
	@Override
	public String toString() {
		return "WorkSubStatusTypeFilter [clientVisible=" + clientVisible + ", resourceVisible=" + resourceVisible + ", showDeactivated=" + showDeactivated + ", showCustomSubStatus="
				+ showCustomSubStatus + ", showSystemSubStatus=" + showSystemSubStatus + ", showRequiresRescheduleSubStatus=" + showRequiresRescheduleSubStatus + ", triggeredBy=" + triggeredBy + "]";
	}

	public Long getWorkId() {
		return workId;
	}

	public WorkSubStatusTypeFilter setWorkId(Long workId) {
		this.workId = workId;
		return this;
	}

}
