package com.workmarket.domains.model.integration.autotask;

import com.workmarket.domains.model.AbstractEntity;
import com.workmarket.domains.model.notification.NotificationType;

import javax.persistence.*;


@Entity(name="autotaskUserPreference")
@Table(name="autotask_user_preference")
@NamedQueries({
		@NamedQuery(name="autotaskUserPreference.byAutotaskUserAndType", query="from autotaskUserPreference where autotaskUser.id = :autotask_user_id and notificationType.code = :notification_type_code")

})
public class AutotaskUserPreference extends AbstractEntity {

	private static final long serialVersionUID = 1L;

	private AutotaskUser autotaskUser;
	private NotificationType notificationType;
	private Boolean flag;

	@ManyToOne(fetch= FetchType.LAZY, optional=false)
	@JoinColumn(name="autotask_user_id")
	public AutotaskUser getAutotaskUser() {
		return autotaskUser;
	}

	public void setAutotaskUser(AutotaskUser autotaskUser) {
		this.autotaskUser = autotaskUser;
	}

	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="notification_type_code", referencedColumnName="code", nullable=false)
	public NotificationType getNotificationType() {
		return notificationType;
	}

	public void setNotificationType(NotificationType notificationType) {
		this.notificationType = notificationType;
	}

	@Column(name="flag", nullable=false, length=1)
	public Boolean getFlag() {
		return flag;
	}

	public void setFlag(Boolean flag) {
		this.flag = flag;
	}
}
