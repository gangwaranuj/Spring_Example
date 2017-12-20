package com.workmarket.web.helpers;

import com.workmarket.domains.work.model.WorkBundle;

/**
 * User: micah
 * Date: 9/3/13
 * Time: 5:23 PM
 */
public class WorkBundleValidationResult {
	public enum STATUS {
		SUCCESS, FAILURE, PARTIAL_SUCCESS
	}

	private WorkBundle bundle;
	private STATUS status = STATUS.FAILURE;

	public WorkBundle getBundle() {
		return bundle;
	}

	public void setBundle(WorkBundle bundle) {
		this.bundle = bundle;
	}

	public STATUS getStatus() {
		return status;
	}

	public void setStatus(STATUS status) {
		this.status = status;
	}

	public boolean isSuccessful() {
		return STATUS.PARTIAL_SUCCESS.equals(status) || STATUS.SUCCESS.equals(status);
	}
}
