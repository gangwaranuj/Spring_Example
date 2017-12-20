package com.workmarket.service.business;

import com.workmarket.domains.groups.model.ScheduledRun;

public interface ScheduledRunService {

	void saveOrUpdate(ScheduledRun scheduledRun);

	void completeScheduledRun(ScheduledRun scheduledRun);

}
