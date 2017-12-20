package com.workmarket.service.business.event.assessment;

import com.workmarket.domains.model.assessment.Attempt;
import com.workmarket.service.business.event.ScheduledEvent;

public class TimedAssessmentAttemptAutoCompleteScheduledEvent extends ScheduledEvent {
	private static final long serialVersionUID = 1L;

	// TODO: Alex - replace reference to entity with id or code
	@Deprecated
	private Attempt attempt;

	@Deprecated
	public Attempt getAttempt() {
		return attempt;
	}
	public void setAttempt(Attempt attempt) {
		this.attempt = attempt;
	}
}
