package com.workmarket.domains.groups.facade;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Optional;
import com.workmarket.domains.groups.model.ScheduledRun;
import com.workmarket.domains.groups.service.UserGroupValidationService;
import com.workmarket.service.business.ScheduledRunService;
import com.workmarket.service.business.UserGroupEvaluationScheduledRunService;
import com.workmarket.service.business.UserGroupService;
import com.workmarket.service.business.event.UserGroupValidationEvent;
import com.workmarket.service.infra.event.EventRouter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class UserGroupValidationFacadeImpl implements UserGroupValidationFacade {

	@Autowired private ScheduledRunService scheduledRunService;
	@Autowired private UserGroupValidationService userGroupValidationService;
	@Autowired private UserGroupEvaluationScheduledRunService userGroupEvaluationScheduleService;
	@Autowired private EventRouter eventRouter;
	@Autowired private UserGroupService userGroupService;

	@Override
	public void revalidateUserGroups() {
		List<Long> dueForValidationUserGroupIds = userGroupService.getDueForValidationUserGroupIds();
		for (Long userGroupId : dueForValidationUserGroupIds) {
			eventRouter.sendEvent(new UserGroupValidationEvent(userGroupId));
		}
	}

	@Override
	@VisibleForTesting
	public void revalidateUserGroup(long userGroupId) {
		Optional<ScheduledRun> scheduledRun = userGroupEvaluationScheduleService.startScheduledRun(userGroupId);
		if (!scheduledRun.isPresent()) {
			return;
		}
		userGroupValidationService.revalidateAllAssociations(userGroupId);
		scheduledRunService.completeScheduledRun(scheduledRun.get());
	}

}
