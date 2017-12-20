package com.workmarket.service.infra.notification;

import com.workmarket.common.template.AssessmentGradePendingNotificationTemplate;
import com.workmarket.common.template.InvoiceDueNotificationTemplate;
import com.workmarket.common.template.LockedCompanyAccount24HrsWarningNotificationTemplate;
import com.workmarket.common.template.LockedCompanyAccountNotificationTemplate;
import com.workmarket.common.template.LockedCompanyAccountOverdueWarningNotificationTemplate;
import com.workmarket.common.template.NotificationTemplate;
import com.workmarket.common.template.StatementReminderNotificationTemplate;
import com.workmarket.common.template.WorkResourceCheckInNotificationTemplate;
import com.workmarket.common.template.WorkResourceConfirmationNotificationTemplate;

public interface NotificationValidator {
	
	/**
	 * Validate whether it's still relevant to deliver the notification.
	 * 
	 * @param notification
	 * @return
	 * @throws Exception
	 */
	boolean validateNotification(NotificationTemplate notification);
	boolean validateNotification(WorkResourceConfirmationNotificationTemplate notification);
	boolean validateNotification(WorkResourceCheckInNotificationTemplate notification);
	boolean validateNotification(AssessmentGradePendingNotificationTemplate notification);
	boolean validateNotification(InvoiceDueNotificationTemplate notification);
	boolean validateNotification(StatementReminderNotificationTemplate notification);
	boolean validateNotification(LockedCompanyAccount24HrsWarningNotificationTemplate notification);
	boolean validateNotification(LockedCompanyAccountOverdueWarningNotificationTemplate notification);
	boolean validateNotification(LockedCompanyAccountNotificationTemplate notification);
}
