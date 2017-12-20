package com.workmarket.common.template;

import com.workmarket.domains.model.DateRange;
import com.workmarket.domains.model.notification.NotificationType;
import com.workmarket.domains.work.model.Work;
import com.workmarket.configuration.Constants;
import com.workmarket.service.infra.communication.ReplyToType;
import org.springframework.util.Assert;

public class WorkResourceConfirmationNotificationTemplate extends AbstractWorkNotificationTemplate {

	private static final long serialVersionUID = 2582666051550912761L;

    private DateRange appointment;

	public WorkResourceConfirmationNotificationTemplate(Long toId, DateRange appointment, Work work) {
		super(Constants.EMAIL_USER_ID_TRANSACTIONAL, toId, new NotificationType(NotificationType.RESOURCE_WORK_CONFIRM), ReplyToType.TRANSACTIONAL, work);
        Assert.notNull(appointment);
        this.appointment = appointment;
	}

    @Override
    public String getDate() {
        return getAppointmentString(this.appointment);
    }
}
