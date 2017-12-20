package com.workmarket.service.business.scheduler;

import com.workmarket.common.template.NotificationTemplate;
import com.workmarket.common.template.NotificationTemplateFactory;
import com.workmarket.configuration.Constants;
import com.workmarket.domains.model.tax.AbstractTaxReport;
import com.workmarket.domains.model.tax.TaxForm1099;
import com.workmarket.domains.model.tax.TaxForm1099Set;
import com.workmarket.service.business.tax.report.TaxReportService;
import com.workmarket.service.infra.business.AuthenticationService;
import com.workmarket.service.infra.notification.NotificationDispatcher;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

@Service
public class TaxForm1099ReminderExecutor implements ScheduledExecutor {

	private static final Log logger = LogFactory.getLog("taxForm1099ReminderExecutor");

	@Autowired private AuthenticationService authenticationService;
	@Autowired private TaxReportService taxReportService;
	@Autowired private NotificationTemplateFactory notificationTemplateFactory;
	@Autowired private NotificationDispatcher notificationDispatcher;

	static final Calendar limitToSendReminders;

	static {
		limitToSendReminders = Calendar.getInstance();
		limitToSendReminders.set(Calendar.MONTH, Calendar.APRIL);
		limitToSendReminders.set(Calendar.DAY_OF_MONTH, 15);
	}

	@Override
	public void execute() {
		logger.info("****** Running TaxForm1099ReminderExecutor at " + new Date());
		authenticationService.setCurrentUser(Constants.WORKMARKET_SYSTEM_USER_ID);

		if (Calendar.getInstance().after(limitToSendReminders)) {
			return;
		}


		int currentYear = Calendar.getInstance().get(Calendar.YEAR);
		TaxForm1099Set taxForm1099Set = taxReportService.findPublishedTaxForm1099ReportForYear(currentYear - 1);
		logger.info("****** Sending reminders for TaxReport year " + String.valueOf(currentYear - 1));

		if (taxForm1099Set != null) {
			List<TaxForm1099> taxForm1099List = taxReportService.findAllUndownloadedTaxForm1099ByTaxForm1099SetId(taxForm1099Set.getId());

			for (AbstractTaxReport report : taxForm1099List) {
				logger.info("****** Emailing company id  " + report.getCompanyId());
				List<NotificationTemplate> templates = notificationTemplateFactory.buildTaxReportAvailableNotificationTemplates(report);
				try {
					notificationDispatcher.dispatchNotifications(templates);
				} catch (Exception e) {
					logger.error("Error sending email for report " + report, e);
				}
			}

		}
	}
}