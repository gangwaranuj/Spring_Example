package com.workmarket.common.template;

import com.workmarket.configuration.Constants;
import com.workmarket.domains.model.notification.NotificationType;
import com.workmarket.service.infra.communication.ReplyToType;

import java.math.BigDecimal;

public class LockedCompanyAccount24HrsWarningNotificationTemplate extends NotificationTemplate {

	private static final long serialVersionUID = 5606753615038573019L;

	private BigDecimal upcomingDuePayables;
	private boolean singleInvoice;

	public LockedCompanyAccount24HrsWarningNotificationTemplate(Long toId, BigDecimal upcomingDuePayables, boolean singleInvoice) {
		super(Constants.EMAIL_USER_ID_TRANSACTIONAL, toId, new NotificationType(NotificationType.WORKMARKET_MESSAGE), ReplyToType.TRANSACTIONAL);
		setCcEmail(Constants.EMAIL_CLIENT_SERVICES);
		this.upcomingDuePayables = upcomingDuePayables;
		this.singleInvoice = singleInvoice;
	}

	public BigDecimal getUpcomingDuePayables() {
		return upcomingDuePayables;
	}

	public void setUpcomingDuePayables(BigDecimal upcomingDuePayables) {
		this.upcomingDuePayables = upcomingDuePayables;
	}


	public boolean isSingleInvoice() {
		return singleInvoice;
	}

	public void setSingleInvoice(boolean singleInvoice) {
		this.singleInvoice = singleInvoice;
	}

}
