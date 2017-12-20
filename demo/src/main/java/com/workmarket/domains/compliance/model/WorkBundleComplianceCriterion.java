package com.workmarket.domains.compliance.model;

import com.workmarket.domains.model.DateRange;
import com.workmarket.domains.model.User;
import com.workmarket.domains.work.model.WorkBundle;

public class WorkBundleComplianceCriterion extends BaseComplianceCriterion {
	private final WorkBundle workBundle;

	public WorkBundleComplianceCriterion(User user, WorkBundle workBundle, DateRange schedule) {
		super(user, schedule);
		this.workBundle = workBundle;
	}

	@Override
	public WorkBundle getWork() {
		return workBundle;
	}
}
