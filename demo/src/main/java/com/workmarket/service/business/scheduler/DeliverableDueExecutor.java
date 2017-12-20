package com.workmarket.service.business.scheduler;

import com.workmarket.domains.model.WorkResource;
import com.workmarket.domains.model.validation.ConstraintViolation;
import com.workmarket.domains.work.service.DeliverableService;
import com.workmarket.domains.work.service.WorkService;
import com.workmarket.domains.work.service.WorkValidationService;
import com.workmarket.service.business.UserNotificationService;
import com.workmarket.service.infra.business.AuthenticationService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jmx.export.annotation.ManagedOperation;
import org.springframework.jmx.export.annotation.ManagedResource;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by rahul on 4/18/14
 */
@Service
@ManagedResource(objectName="bean:name=DeliverableDueScheduler", description="Check which deliverables are due")
public class DeliverableDueExecutor {

	private static final Log logger = LogFactory.getLog(DeliverableDueExecutor.class);

	@Autowired private WorkService workService;
	@Autowired private UserNotificationService userNotificationService;
	@Autowired private AuthenticationService authenticationService;
	@Autowired private WorkValidationService workValidationService;
	@Autowired private DeliverableService deliverableService;

	@ManagedOperation(description = "checkDeliverables")
	public void execute() {
		logger.debug("[DeliverableDue] DeliverableDueExecutor: start");

		try {
			List<String> workNumbers = workService.findAssignmentsWithDeliverablesDue();
			logger.debug("[DeliverableDue] Found " + workNumbers.size() + " assignments with deliverables due");

			for (String workNumber : workNumbers) {
				Long workId = workService.findWorkId(workNumber);
				WorkResource workResource = workService.findActiveWorkResource(workId);
				authenticationService.setCurrentUser(workResource.getUser().getId());
				deliverableService.disableDeliverableDeadline(workNumber);

				List<ConstraintViolation> constraintViolations = workValidationService.validateDeliverableRequirements(false, workNumber);
				if (CollectionUtils.isEmpty(constraintViolations)) {
					continue;
				}

				if (workResource != null) {
					userNotificationService.onDeliverableLate(workResource);
				} else {
					logger.error("[DeliverableDue] No active resource found for work id:" + workId);
				}
			}
		} catch (Exception e) {
			logger.error("[DeliverableDue] Deliverable due processing error ", e);
		}
	}
}
