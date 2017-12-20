package com.workmarket.common.template;

import com.workmarket.domains.model.notification.NotificationType;
import com.workmarket.domains.work.model.Work;
import com.workmarket.domains.work.model.negotiation.WorkNegotiation;
import com.workmarket.configuration.Constants;
import com.workmarket.service.infra.communication.ReplyToType;
import com.workmarket.utility.DateUtilities;

public class AbstractWorkNegotiationNotificationTemplate extends AbstractWorkNotificationTemplate {

	private static final long serialVersionUID = -8395911064283172238L;
	protected WorkNegotiation negotiation;

	public AbstractWorkNegotiationNotificationTemplate(Long toId, Work work, WorkNegotiation negotiation, NotificationType notificationType) {
		super(Constants.EMAIL_USER_ID_TRANSACTIONAL, toId, notificationType, ReplyToType.TRANSACTIONAL, work, negotiation);
		this.negotiation = negotiation;
	}

	public AbstractWorkNegotiationNotificationTemplate(Long toId, Work work, WorkNegotiation negotiation, NotificationType notificationType, String noteCreatorFullName) {
		super(Constants.EMAIL_USER_ID_TRANSACTIONAL, toId, notificationType, ReplyToType.TRANSACTIONAL, work, negotiation, noteCreatorFullName);
		this.negotiation = negotiation;
	}

	public WorkNegotiation getNegotiation() {
		return negotiation;
	}

	public String getNegotiationDate() {
		if (!negotiation.isScheduleNegotiation()) return null;
		if (negotiation.getScheduleRangeFlag() && negotiation.getScheduleThrough() != null) {
			return String.format("%s to: %s", DateUtilities.formatDateForEmail(negotiation.getScheduleFrom(), getTimeZoneId()), DateUtilities.formatDateForEmail(negotiation.getScheduleThrough(), getTimeZoneId()));
		}
		return DateUtilities.formatDateForEmail(negotiation.getScheduleFrom(), getTimeZoneId());
	}

	public String getNegotiationExpirationDate() {
		if (negotiation.getExpiresOn() == null) return null;
		return DateUtilities.formatDateForEmail(negotiation.getExpiresOn(), getTimeZoneId());
	}
}
