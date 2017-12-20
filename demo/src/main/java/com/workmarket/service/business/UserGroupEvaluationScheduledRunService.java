package com.workmarket.service.business;

import com.google.common.base.Optional;
import com.workmarket.domains.groups.model.ScheduledRun;

public interface UserGroupEvaluationScheduledRunService {

	Optional<ScheduledRun> getNextFutureScheduledRunForActiveOrInactiveGroup(long userGroupId);

	Optional<ScheduledRun> getNextScheduledRun(long userGroupId);

	Optional<ScheduledRun> startScheduledRun(long userGroupId);

	void turnOnAutomaticEvaluation(long userGroupId, int validateDaysInterval);

	void turnOffAutomaticEvaluation(long userGroupId);

}
