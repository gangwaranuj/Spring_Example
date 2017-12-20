package com.workmarket.service.business.scheduler;

import com.workmarket.configuration.Constants;
import com.workmarket.service.business.event.UserGroupsValidationEvent;
import com.workmarket.service.infra.business.AuthenticationService;
import com.workmarket.service.infra.event.EventRouter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jmx.export.annotation.ManagedOperation;
import org.springframework.jmx.export.annotation.ManagedResource;
import org.springframework.stereotype.Service;


@Service
@ManagedResource(objectName = "bean:name=userGroupsReevaluationExecutor", description = "reevaluate talent pool membership requirements")
public class UserGroupsReevaluationExecutor {

	@Autowired EventRouter eventRouter;
	@Autowired AuthenticationService authenticationService;

	@ManagedOperation(description = "reevaluateUserGroupRequirementSets")
	public void reevaluateUserGroupRequirementSets() {
		authenticationService.setCurrentUser(Constants.WORKMARKET_SYSTEM_USER_ID);
		eventRouter.sendEvent(new UserGroupsValidationEvent());
	}
}
