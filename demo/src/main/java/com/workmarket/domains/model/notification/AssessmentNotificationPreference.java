package com.workmarket.domains.model.notification;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

import com.workmarket.domains.model.DeletableEntity;
import com.workmarket.domains.model.assessment.AbstractAssessment;
import com.workmarket.domains.model.audit.AuditChanges;

@Entity(name="assessmentNotificationPreference")
@Table(name="assessment_notification_preference")
@NamedQueries({
	@NamedQuery(name="assessmentNotificationPreference.byAssessmentAndType", query="from assessmentNotificationPreference where assessment.id = :assessment_id and notificationType.code = :notification_type_code")
})
@AuditChanges
public class AssessmentNotificationPreference extends DeletableEntity {
private static final long serialVersionUID = 1L;

	private AbstractAssessment assessment;
	private NotificationType notificationType;
	private Integer days;

	public AssessmentNotificationPreference() {}
	public AssessmentNotificationPreference(String type) {
		this.notificationType = new NotificationType(type);
	}
	public AssessmentNotificationPreference(NotificationType type) {
		this.notificationType = type;
	}

	@ManyToOne(fetch=FetchType.LAZY, optional=false)
	@JoinColumn(name="assessment_id")
	public AbstractAssessment getAssessment() {
		return assessment;
	}
	public void setAssessment(AbstractAssessment assessment) {
		this.assessment = assessment;
	}

	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="notification_type_code", referencedColumnName="code", nullable=false)
	public NotificationType getNotificationType() {
		return notificationType;
	}
	public void setNotificationType(NotificationType notificationType) {
		this.notificationType = notificationType;
	}

	@Column(name="days")
	public Integer getDays() {
		return days;
	}
	public void setDays(Integer days) {
		this.days = days;
	}
}
