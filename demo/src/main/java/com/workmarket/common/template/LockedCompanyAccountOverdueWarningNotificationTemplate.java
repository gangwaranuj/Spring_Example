package com.workmarket.common.template;

import java.math.BigDecimal;

import com.workmarket.domains.model.notification.NotificationType;
import com.workmarket.configuration.Constants;
import com.workmarket.service.infra.communication.ReplyToType;

public class LockedCompanyAccountOverdueWarningNotificationTemplate extends NotificationTemplate {

	private static final long serialVersionUID = 5606753615038573019L;

	private BigDecimal        pastDuePayables;
	private Integer           daysSinceOverdue;
	private Integer           daysTillSuspended;

	public LockedCompanyAccountOverdueWarningNotificationTemplate(Long toId, Integer daysSinceOverdue, Integer daysTillSuspended, BigDecimal pastDuePayables) {
		super(Constants.EMAIL_USER_ID_TRANSACTIONAL, toId, new NotificationType(NotificationType.WORKMARKET_MESSAGE), ReplyToType.TRANSACTIONAL);
		setCcEmail(Constants.EMAIL_CLIENT_SERVICES);
		this.setDaysSinceOverdue(daysSinceOverdue);
		this.setDaysTillSuspended(daysTillSuspended);
		this.setPastDuePayables(pastDuePayables);
	}

	public BigDecimal getPastDuePayables() {
	    return pastDuePayables;
    }

	public void setPastDuePayables(BigDecimal pastDuePayables) {
	    this.pastDuePayables = pastDuePayables;
    }

	public Integer getDaysSinceOverdue() {
	    return daysSinceOverdue;
    }

	public void setDaysSinceOverdue(Integer daysSinceOverdue) {
	    this.daysSinceOverdue = daysSinceOverdue;
    }

	public Integer getDaysTillSuspended() {
	    return daysTillSuspended;
    }

	public void setDaysTillSuspended(Integer daysTillSuspended) {
	    this.daysTillSuspended = daysTillSuspended;
    }

}
