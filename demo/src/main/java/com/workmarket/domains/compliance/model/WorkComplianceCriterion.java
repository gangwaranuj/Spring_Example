package com.workmarket.domains.compliance.model;

import com.workmarket.domains.model.DateRange;
import com.workmarket.domains.model.User;
import com.workmarket.domains.work.model.Work;

public class WorkComplianceCriterion extends BaseComplianceCriterion {
	private final Work work;

	public WorkComplianceCriterion(User user, Work work, DateRange schedule) {
		super(user, schedule);
		this.work = work;
	}

	@Override
	public Work getWork() {
		return this.work;
	}
}

