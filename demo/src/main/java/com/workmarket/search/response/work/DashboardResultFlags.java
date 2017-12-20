package com.workmarket.search.response.work;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

import java.io.Serializable;

public class DashboardResultFlags implements Serializable {
	private static final long serialVersionUID = 1L;

	private boolean confirmed;
	private boolean resourceConfirmationRequired;
	private boolean internal;
	private boolean scheduleRangeFlag;
	private boolean addressOnsiteFlag;
	private boolean autoPayEnabled;
	private boolean autoSendInvoiceEmail;
	private boolean paymentTermsEnabled;
	private boolean assignToFirstResource;
	private boolean applied;
	private boolean applicationsPending;

	public DashboardResultFlags() {
		this.confirmed = true;
		this.resourceConfirmationRequired = true;
		this.internal = true;
		this.scheduleRangeFlag = true;
		this.addressOnsiteFlag = true;
		this.autoPayEnabled = true;
		this.autoSendInvoiceEmail = true;
		this.paymentTermsEnabled = true;
		this.assignToFirstResource = false;
		this.applied = false;
		this.applicationsPending = false;
	}

	public DashboardResultFlags(
			boolean confirmed,
			boolean resourceConfirmationRequired,
			boolean internal,
			boolean scheduleRangeFlag,
			boolean addressOnsiteFlag,
			boolean autoPayEnabled,
			boolean autoSendInvoiceEmail,
			boolean paymentTermsEnabled,
			boolean assignToFirstResource,
			boolean applied,
			boolean applicationsPending) {
		this();
		this.confirmed = confirmed;
		this.resourceConfirmationRequired = resourceConfirmationRequired;
		this.internal = internal;
		this.scheduleRangeFlag = scheduleRangeFlag;
		this.addressOnsiteFlag = addressOnsiteFlag;
		this.autoPayEnabled = autoPayEnabled;
		this.autoSendInvoiceEmail = autoSendInvoiceEmail;
		this.paymentTermsEnabled = paymentTermsEnabled;
		this.assignToFirstResource = assignToFirstResource;
		this.applied = applied;
		this.applicationsPending = applicationsPending;
	}

	public boolean isConfirmed() {
		return this.confirmed;
	}

	public DashboardResultFlags setConfirmed(boolean confirmed) {
		this.confirmed = confirmed;
		return this;
	}

	public boolean isResourceConfirmationRequired() {
		return this.resourceConfirmationRequired;
	}

	public DashboardResultFlags setResourceConfirmationRequired(boolean resourceConfirmationRequired) {
		this.resourceConfirmationRequired = resourceConfirmationRequired;
		return this;
	}

	public boolean isInternal() {
		return this.internal;
	}

	public DashboardResultFlags setInternal(boolean internal) {
		this.internal = internal;
		return this;
	}

	public boolean isScheduleRangeFlag() {
		return this.scheduleRangeFlag;
	}

	public DashboardResultFlags setScheduleRangeFlag(boolean scheduleRangeFlag) {
		this.scheduleRangeFlag = scheduleRangeFlag;
		return this;
	}

	public boolean isAddressOnsiteFlag() {
		return this.addressOnsiteFlag;
	}

	public DashboardResultFlags setAddressOnsiteFlag(boolean addressOnsiteFlag) {
		this.addressOnsiteFlag = addressOnsiteFlag;
		return this;
	}

	public boolean isAutoPayEnabled() {
		return this.autoPayEnabled;
	}

	public DashboardResultFlags setAutoPayEnabled(boolean autoPayEnabled) {
		this.autoPayEnabled = autoPayEnabled;
		return this;
	}

	public boolean isAutoSendInvoiceEmail() {
		return this.autoSendInvoiceEmail;
	}

	public DashboardResultFlags setAutoSendInvoiceEmail(boolean autoSendInvoiceEmail) {
		this.autoSendInvoiceEmail = autoSendInvoiceEmail;
		return this;
	}

	public boolean isPaymentTermsEnabled() {
		return this.paymentTermsEnabled;
	}

	public DashboardResultFlags setPaymentTermsEnabled(boolean paymentTermsEnabled) {
		this.paymentTermsEnabled = paymentTermsEnabled;
		return this;
	}

	public boolean isAssignToFirstResource() {
		return assignToFirstResource;
	}

	public DashboardResultFlags setAssignToFirstResource(boolean assignToFirstResource) {
		this.assignToFirstResource = assignToFirstResource;
		return this;
	}

	public boolean isApplied() {
		return applied;
	}

	public DashboardResultFlags setApplied(boolean applied) {
		this.applied = applied;
		return this;
	}

	public boolean isApplicationsPending() {
		return applicationsPending;
	}

	public DashboardResultFlags setApplicationsPending(boolean applicationsPending) {
		this.applicationsPending = applicationsPending;
		return this;
	}

	@Override
	public boolean equals(Object that) {
		return EqualsBuilder.reflectionEquals(this, that);
	}

	@Override
	public int hashCode() {
		return HashCodeBuilder.reflectionHashCode(this);
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}
}

