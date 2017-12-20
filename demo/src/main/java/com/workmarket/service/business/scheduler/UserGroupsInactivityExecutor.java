package com.workmarket.service.business.scheduler;

import com.workmarket.domains.groups.dao.UserGroupDAO;
import com.workmarket.domains.groups.service.UserGroupValidationService;
import com.workmarket.service.business.UserGroupInactivityService;
import com.workmarket.service.business.UserGroupService;
import com.workmarket.service.business.UserNotificationService;
import com.workmarket.service.business.UserUserGroupDocumentReferenceService;
import com.workmarket.service.infra.business.AuthenticationService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jmx.export.annotation.ManagedOperation;
import org.springframework.jmx.export.annotation.ManagedResource;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
@ManagedResource(objectName = "bean:name=userGroupsInactivityExecutor", description = "inactive group expiration tasks")
public class UserGroupsInactivityExecutor {

	@Resource private UserGroupInactivityService userGroupInactivityService;

	@ManagedOperation(description = "deactivateInactiveGroups")
	public void deactivateInactiveGroups() {
		userGroupInactivityService.deactivateInactiveGroups();
	}
}
