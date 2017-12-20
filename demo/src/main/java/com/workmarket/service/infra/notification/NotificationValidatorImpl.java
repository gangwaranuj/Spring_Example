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
import com.workmarket.service.infra.business.UserRoleService;
import com.workmarket.dao.assessment.AttemptDAO;
import com.workmarket.domains.model.User;
import com.workmarket.domains.model.assessment.Attempt;
import com.workmarket.service.business.UserService;
import com.workmarket.domains.work.service.WorkService;
import com.workmarket.utility.DateUtilities;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.util.Calendar;

@Service
public class NotificationValidatorImpl implements NotificationValidator {

	private static final Log logger = LogFactory.getLog(NotificationValidatorImpl.class);
	
	@Autowired private AttemptDAO attemptDAO;
	@Autowired private UserService userService;
	@Autowired private WorkService workService;
	@Autowired private UserRoleService userRoleService;
	
	@Override
	public boolean validateNotification(NotificationTemplate notification) {
		try {
			Method method = getClass().getMethod("validateNotification", new Class[] { notification.getClass() });
			return (Boolean)method.invoke(this, notification);
		} catch (Exception ex) {
			return true;
		}
	}
	
	@Override
	public boolean validateNotification(WorkResourceConfirmationNotificationTemplate notification) {
		Calendar confirmationDate = workService.calculateRequiredConfirmationNotificationDate(notification.getWork().getId());
		if (!DateUtilities.withinIntervalWindow(Calendar.MINUTE, 5, confirmationDate)) {
			logger.debug("Discarding notification [WorkResourceConfirmationNotificationTemplate]");
			return false;
		}
		return workService.isWorkResourceConfirmationValid(notification.getToId(), notification.getWork().getId());
	}

	@Override
	public boolean validateNotification(WorkResourceCheckInNotificationTemplate notification) {
		Calendar reminderDate = workService.calculateRequiredCheckinReminderDate(notification.getWork().getId());
		if (!DateUtilities.withinIntervalWindow(Calendar.MINUTE, 5, reminderDate)) {
			logger.debug("Discarding notification [WorkResourceCheckInNotificationTemplate]");
			return false;
		}
		return workService.isWorkResourceCheckInValid(notification.getToId(), notification.getWork().getId());
	}
	
	@Override
	public boolean validateNotification(AssessmentGradePendingNotificationTemplate notification) {
		Attempt attempt = attemptDAO.get(notification.getAttempt().getId());
		Assert.notNull(attempt);
		return attempt.getStatus().isGradePending();
	}
	
	@Override
	public boolean validateNotification(InvoiceDueNotificationTemplate notification) {
		User user = userService.getUser(notification.getToId());
		Assert.notNull(user);
		return (userRoleService.isAdminOrManager(user) || userRoleService.isController(user));
	}
	
	@Override
	public boolean validateNotification(StatementReminderNotificationTemplate notification) {
		User user = userService.getUser(notification.getToId());
		Assert.notNull(user);
		return (userRoleService.isAdminOrManager(user) || userRoleService.isController(user));
	}

	@Override
	public boolean validateNotification(LockedCompanyAccount24HrsWarningNotificationTemplate notification) {
		User user = userService.getUser(notification.getToId());
		Assert.notNull(user);

		Calendar now = DateUtilities.getCalendarNow();
		Calendar dayAfterTomorrow = (Calendar) now.clone();
		dayAfterTomorrow.add(Calendar.DAY_OF_MONTH, 2);

		return !user.getCompany().isLocked() && !user.getCompany().hasOverdueWarning() && user.getCompany().hasLockWarning() && workService.countAllDueWorkByCompany(now, dayAfterTomorrow, user.getCompany().getId()) > 0;
	}

	@Override
	public boolean validateNotification(LockedCompanyAccountOverdueWarningNotificationTemplate notification) {
		User user = userService.getUser(notification.getToId());
		Assert.notNull(user);
		return !user.getCompany().isLocked() && user.getCompany().hasOverdueWarning() && notification.getPastDuePayables().compareTo(BigDecimal.ZERO) == 1;
	}

	@Override
	public boolean validateNotification(LockedCompanyAccountNotificationTemplate notification) {
		User user = userService.getUser(notification.getToId());
		Assert.notNull(user);
		return user.getCompany().isLocked() && notification.getPastDuePayables().compareTo(BigDecimal.ZERO) == 1;
	}
	
}
