package com.workmarket.service.business.scheduler;

import com.workmarket.domains.model.WorkResource;
import com.workmarket.domains.work.service.WorkService;
import com.workmarket.service.business.UserNotificationService;
import com.workmarket.service.infra.business.AuthenticationService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ResourceNoShowAlertBackstopExecutor {

	private static final Log logger = LogFactory.getLog(ResourceNoShowAlertBackstopExecutor.class);

	@Autowired private UserNotificationService userNotificationService;
	@Autowired private WorkService workService;
	@Autowired private AuthenticationService authenticationService;

	public void execute() {
		logger.debug("[ResourceNoShow] ResourceNoShowAlertBackstopExecutor: start");

		try {
			List<Integer> workIds = workService.findAssignmentsMissingResourceNoShow();
			logger.debug("[ResourceNoShow] Found " + workIds.size() + " assignments missing no show labels");

			for (Integer workId : workIds) {
				WorkResource resource = workService.findActiveWorkResource(workId.longValue());
				if (resource != null) {
					authenticationService.setCurrentUser(resource.getUser().getId());
					try {
						userNotificationService.onWorkResourceNotCheckedIn(resource);
					} catch (Exception e) {
						logger.error("[ResourceNoShow] Resource No Show Backstop processing error for work id:" + workId, e);
					}
				} else {
					logger.error("[ResourceNoShow] No active resource found for work id:" + workId);
				}
			}
		} catch (Exception e) {
			logger.error("[ResourceNoShow] Resource No Show Backstop processing error ", e);
		}
	}
}