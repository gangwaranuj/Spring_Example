package com.workmarket.data.report.work;

import java.util.List;

import static org.apache.commons.collections.CollectionUtils.isNotEmpty;

public class CustomFieldReportFilters {

	private Boolean showOnInvoice;
	private Boolean visibleToResource;
	private Boolean visibleToBuyer;
	private Boolean showOnDashboard;
	private Boolean showOnSentStatus;
	private Boolean showOnEmail;
	private List<Long> workIds;
	private List<Long> workCustomFieldIds; // NULL means fetch all

	public List<Long> getWorkCustomFieldIds() {
		return workCustomFieldIds;
	}

	public void setWorkCustomFieldIds(List<Long> workCustomFieldIds) {
		this.workCustomFieldIds = workCustomFieldIds;
	}

	public Boolean getShowOnInvoice() {
		return showOnInvoice;
	}

	public void setShowOnInvoice(Boolean showOnInvoice) {
		this.showOnInvoice = showOnInvoice;
	}

	public Boolean getVisibleToResource() {
		return visibleToResource;
	}

	public void setVisibleToResource(Boolean visibleToResource) {
		this.visibleToResource = visibleToResource;
	}

	public Boolean getVisibleToBuyer() {
		return visibleToBuyer;
	}

	public void setVisibleToBuyer(Boolean visibleToBuyer) {
		this.visibleToBuyer = visibleToBuyer;
	}

	public Boolean getShowOnDashboard() {
		return showOnDashboard;
	}

	public void setShowOnDashboard(Boolean showOnDashboard) {
		this.showOnDashboard = showOnDashboard;
	}

	public void setShowOnSentStatus(Boolean showOnSentStatus) {
		this.showOnSentStatus = showOnSentStatus;
	}

	public Boolean getShowOnSentStatus() {
		return showOnSentStatus;
	}

	public List<Long> getWorkIds() {
		return workIds;
	}

	public void setWorkIds(List<Long> workIds) {
		this.workIds = workIds;
	}

	public Boolean getShowOnEmail() {
		return showOnEmail;
	}

	public void setShowOnEmail(Boolean showOnEmail) {
		this.showOnEmail = showOnEmail;
	}

	public boolean hasShowOnDashboardFilter() {
		return getShowOnDashboard() != null;
	}
	
	public boolean hasShowOnInvoiceFilter() {
		return getShowOnInvoice() != null;
	}
	
	public boolean hasVisibleToBuyerFilter() {
		return getVisibleToBuyer() != null;
	}
	
	public boolean hasVisibleToResourceFilter() {
		return getVisibleToResource() != null;
	}
	
	public boolean hasShowOnSentStatusFilter() {
		return getShowOnSentStatus() != null;
	}

	public boolean hasWorkFilter() {
		return isNotEmpty(workIds);
	}

	public boolean hasShowOnEmailFilter() {
		return getShowOnEmail() != null;
	}

	public boolean hasCustomFieldIdFilter() {
		return isNotEmpty(workCustomFieldIds);
	}
}
