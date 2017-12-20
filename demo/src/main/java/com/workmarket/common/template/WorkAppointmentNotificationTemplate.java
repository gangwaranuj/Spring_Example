package com.workmarket.common.template;

import com.workmarket.domains.model.DateRange;
import com.workmarket.domains.model.notification.NotificationType;
import com.workmarket.domains.work.model.Work;
import com.workmarket.configuration.Constants;
import com.workmarket.service.infra.communication.ReplyToType;
import org.springframework.util.Assert;

public class WorkAppointmentNotificationTemplate extends AbstractWorkNotificationTemplate {

	private static final long serialVersionUID = 8534290097264060082L;

    private DateRange appointment;

	protected WorkAppointmentNotificationTemplate(Long toId, Work work, DateRange appointment, Long activeWorkerId) {
		super(Constants.EMAIL_USER_ID_TRANSACTIONAL, toId, new NotificationType(NotificationType.WORK_APPOINTMENT_SET), ReplyToType.TRANSACTIONAL, work, activeWorkerId);

		if (isToActiveWorker()) {
			setNotificationType(new NotificationType(NotificationType.RESOURCE_WORK_APPOINTMENT_SET));
		}

		Assert.notNull(appointment);
		this.appointment = appointment;
	}

    public DateRange getAppointment() {
        return this.appointment;
    }

    @Override
    public String getDate() {
        return getAppointmentString(this.appointment);
    }
}
