package com.workmarket.service.business.scheduler;

import com.workmarket.common.template.email.EmailTemplate;
import com.workmarket.common.template.email.EmailTemplateFactory;
import com.workmarket.domains.model.Company;
import com.workmarket.domains.model.directory.Email;
import com.workmarket.service.business.CompanyService;
import com.workmarket.configuration.Constants;
import com.workmarket.service.infra.business.AuthenticationService;
import com.workmarket.service.infra.notification.NotificationService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jmx.export.annotation.ManagedOperation;
import org.springframework.jmx.export.annotation.ManagedResource;
import org.springframework.stereotype.Service;

import static org.apache.commons.collections.CollectionUtils.isNotEmpty;

@Service
@ManagedResource(objectName = "bean:name=agingAlert", description = "aging alert")
public class AgingAlertExecutor implements ScheduledExecutor {

	private static final Log logger = LogFactory.getLog(AgingAlertExecutor.class);

	@Autowired private AuthenticationService authenticationService;
	@Autowired private CompanyService companyService;
	@Autowired private EmailTemplateFactory emailTemplateFactory;
	@Autowired private NotificationService notificationService;

	@ManagedOperation(description = "aging alert")
	public void execute() {
		logger.debug("[alert] AgingAlertExecutor: start");
		authenticationService.setCurrentUser(Constants.WORKMARKET_SYSTEM_USER_ID);

		for (Long companyId : companyService.findCompanyIdsWithAgingAlert()) {
			Company company = companyService.findCompanyById(companyId);
			if (isNotEmpty(company.getAgingAlertEmails())) {
				for (Email email : company.getAgingAlertEmails()) {
					try {
						EmailTemplate template = emailTemplateFactory.buildAssignmentAgingNotificationTemplate(company.getCreatedBy().getId(), companyId, email);
						if (template != null) {
							notificationService.sendNotification(template);
						}
					} catch (Exception e) {
						logger.error("Error aging alert to Email: " + email.getEmail(), e);
					}
				}
			}
		}
		logger.debug("[alert] AgingAlertExecutor: done");
	}


}
