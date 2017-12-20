package com.workmarket.service.business.scheduler;

import com.workmarket.domains.model.WorkResource;
import com.workmarket.domains.work.model.Work;
import com.workmarket.domains.work.service.DeliverableService;
import com.workmarket.domains.work.service.WorkService;
import com.workmarket.service.business.UserNotificationService;
import com.workmarket.service.infra.business.AuthenticationService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jmx.export.annotation.ManagedOperation;
import org.springframework.jmx.export.annotation.ManagedResource;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@ManagedResource(objectName="bean:name=DeliverableDueReminderScheduler", description="Check which assignments need a deliverable due reminder")
public class DeliverableDueReminderAlertExecutor {

	private static final Log logger = LogFactory.getLog(DeliverableDueReminderAlertExecutor.class);

	@Autowired private WorkService workService;
	@Autowired private DeliverableService deliverableService;
	@Autowired private UserNotificationService userNotificationService;
	@Autowired private AuthenticationService authenticationService;

	@ManagedOperation(description = "checkDeliverableDueReminder")
	public void execute() {
		logger.debug("[DeliverableDueReminder] DeliverableDueReminderAlertExecutor: start");

		try {
			List<Work> workList = workService.findAssignmentsRequiringDeliverableDueReminder();
			logger.debug("[DeliverableDueReminder] Found " + workList.size() + " assignments that require a deliverable due reminder");

			for (Work work: workList) {
				WorkResource workResource = workService.findActiveWorkResource(work.getId());
				if (workResource != null) {
					authenticationService.setCurrentUser(workResource.getUser().getId());
					deliverableService.disableDeliverableReminder(work.getWorkNumber());
					userNotificationService.onDeliverableDueReminder(workResource);
				} else {
					logger.error("[DeliverableDueReminder] No active resource found for work id:" + work.getId());
				}
			}
		} catch (Exception e) {
			logger.error("[DeliverableDueReminder] Deliverable Due Reminder processing error ", e);
		}

	}
}
