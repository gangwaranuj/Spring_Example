package com.workmarket.domains.model.notification;


import com.workmarket.domains.model.AbstractEntity;
import com.workmarket.domains.model.audit.AuditChanges;

import javax.persistence.*;


@Entity(name="company_alert")
@Table(name="company_alert")
@AuditChanges
public class CompanyAlert extends AbstractEntity {

	private Long companyId;
	private NotificationType notificationType;
	private boolean sentToday;



	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "notification_type_code", referencedColumnName = "code")
	public NotificationType getNotificationType() {
		return notificationType;
	}

	public void setNotificationType(NotificationType notificationType) {
		this.notificationType = notificationType;
	}

	@Column(name = "company_id")
	public Long getCompanyId() {
		return companyId;
	}

	public void setCompanyId(Long companyId) {
		this.companyId = companyId;
	}

	@Column(name = "sent_today", nullable = false)
	public boolean isSentToday() {
		return sentToday;
	}

	public void setSentToday(boolean sentToday) {
		this.sentToday = sentToday;
	}

}
