package com.workmarket.common.template;

import com.workmarket.domains.model.DateRange;
import com.workmarket.domains.model.WorkResource;
import com.workmarket.domains.model.notification.NotificationType;
import com.workmarket.domains.work.model.Work;
import com.workmarket.configuration.Constants;
import com.workmarket.service.infra.communication.ReplyToType;
import org.springframework.util.Assert;

public class WorkSubStatusFailedConfirmationNotificationTemplate extends WorkResourceNotificationTemplate {
	private static final long serialVersionUID = -6921700362536273286L;
	private DateRange appointment;

	public WorkSubStatusFailedConfirmationNotificationTemplate(Work work, WorkResource workResource, Long toId, DateRange appointment) {
		super(Constants.EMAIL_USER_ID_TRANSACTIONAL, toId, new NotificationType(NotificationType.WORK_SUBSTATUS_NOT_CONFIRMED), ReplyToType.TRANSACTIONAL, work, workResource);
		Assert.notNull(appointment);
		this.appointment = appointment;
	}

	public WorkSubStatusFailedConfirmationNotificationTemplate(Work work, Long toId, DateRange appointment) {
		super(Constants.EMAIL_USER_ID_TRANSACTIONAL, toId, new NotificationType(NotificationType.WORK_SUBSTATUS_NOT_CONFIRMED), ReplyToType.TRANSACTIONAL, work);
		Assert.notNull(appointment);
		this.appointment = appointment;
	}

	@Override
	public String getDate() {
		return getAppointmentString(this.appointment);
	}
}
