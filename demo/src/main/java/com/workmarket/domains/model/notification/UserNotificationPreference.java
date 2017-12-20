package com.workmarket.domains.model.notification;

import com.workmarket.domains.model.AbstractEntity;
import com.workmarket.domains.model.User;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Transient;

import static org.apache.commons.lang3.BooleanUtils.isTrue;

@Entity(name="userNotificationPreference")
@Table(name="user_notification_preference")
@NamedQueries({
	@NamedQuery(name="userNotificationPreference.byUser", query="from userNotificationPreference where user.id = :user_id"),
	@NamedQuery(name="userNotificationPreference.byUserAndType", query="from userNotificationPreference where user.id = :user_id and notificationType.code = :notification_type_code")
})
public class UserNotificationPreference extends AbstractEntity {

	private static final long serialVersionUID = 1L;
	
	private User user;
	private NotificationType notificationType;
	private Boolean emailFlag;
	private Boolean followFlag;
	private Boolean bullhornFlag;
	private Boolean smsFlag;
	private Boolean voiceFlag;
	private Boolean pushFlag;
	private Boolean dispatchEmailFlag;
	private Boolean dispatchBullhornFlag;
	private Boolean dispatchSmsFlag;
	private Boolean dispatchVoiceFlag;
	private Boolean dispatchPushFlag;

	@ManyToOne(fetch=FetchType.LAZY, optional=false)
	@JoinColumn(name="user_id")
	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}
	
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="notification_type_code", referencedColumnName="code", nullable=false)
	public NotificationType getNotificationType() {
		return notificationType;
	}
	
	public void setNotificationType(NotificationType notificationType) {
		this.notificationType = notificationType;
	}
	
	@Column(name="email_flag", nullable=false, length=1)
	public Boolean getEmailFlag() {
		return emailFlag;
	}
	
	public void setEmailFlag(Boolean emailFlag) {
		this.emailFlag = emailFlag;
	}

	@Column(name="follow_flag", nullable=false, length=1)
	public Boolean getFollowFlag() {
		return followFlag;
	}

	public void setFollowFlag(Boolean followFlag) {
		this.followFlag = followFlag;
	}

	@Column(name="bullhorn_flag", nullable=false, length=1)
	public Boolean getBullhornFlag() {
		return bullhornFlag;
	}

	public void setBullhornFlag(Boolean bullhornFlag) {
		this.bullhornFlag = bullhornFlag;
	}

	@Column(name="sms_flag", nullable=false, length=1)
	public Boolean getSmsFlag() {
		return smsFlag;
	}
	
	public void setSmsFlag(Boolean smsFlag) {
		this.smsFlag = smsFlag;
	}
	
	@Column(name="voice_flag", nullable=false, length=1)
	public Boolean getVoiceFlag() {
		return voiceFlag;
	}
	
	public void setVoiceFlag(Boolean voiceFlag) {
		this.voiceFlag = voiceFlag;
	}

	@Column(name="push_flag", nullable=false, length=1)
	public Boolean getPushFlag() {
		return pushFlag;
	}

	public void setPushFlag(Boolean pushFlag) {
		this.pushFlag = pushFlag;
	}

	@Column(name="dispatch_email_flag", nullable=false, length=1)
	public Boolean getDispatchEmailFlag() {
		return dispatchEmailFlag;
	}

	public void setDispatchEmailFlag(Boolean dispatchEmailFlag) {
		this.dispatchEmailFlag = dispatchEmailFlag;
	}

	@Column(name="dispatch_bullhorn_flag", nullable=false, length=1)
	public Boolean getDispatchBullhornFlag() {
		return dispatchBullhornFlag;
	}

	public void setDispatchBullhornFlag(Boolean dispatchBullhornFlag) {
		this.dispatchBullhornFlag = dispatchBullhornFlag;
	}

	@Column(name="dispatch_push_flag", nullable=false, length=1)
	public Boolean getDispatchPushFlag() {
		return dispatchPushFlag;
	}

	public void setDispatchPushFlag(Boolean dispatchPushFlag) {
		this.dispatchPushFlag = dispatchPushFlag;
	}

	@Column(name="dispatch_sms_flag", nullable=false, length=1)
	public Boolean getDispatchSmsFlag() {
		return dispatchSmsFlag;
	}

	public void setDispatchSmsFlag(Boolean dispatchSmsFlag) {
		this.dispatchSmsFlag = dispatchSmsFlag;
	}

	@Column(name="dispatch_voice_flag", nullable=false, length=1)
	public Boolean getDispatchVoiceFlag() {
		return dispatchVoiceFlag;
	}

	public void setDispatchVoiceFlag(Boolean dispatchVoiceFlag) {
		this.dispatchVoiceFlag = dispatchVoiceFlag;
	}

	@Override
	public String toString() {
		return "UserNotificationPreference{" +
			"user=" + user +
			", notificationType=" + notificationType +
			", emailFlag=" + emailFlag +
			", followFlag=" + followFlag +
			", bullhornFlag=" + bullhornFlag +
			", smsFlag=" + smsFlag +
			", voiceFlag=" + voiceFlag +
			", pushFlag=" + pushFlag +
			", dispatchEmailFlag=" + dispatchEmailFlag +
			", dispatchBullhornFlag=" + dispatchBullhornFlag +
			", dispatchSmsFlag=" + dispatchSmsFlag +
			", dispatchVoiceFlag=" + dispatchVoiceFlag +
			", dispatchPushFlag=" + dispatchPushFlag +
			'}';
	}

	@Transient
	public boolean isCacheable() {
		return isTrue(getBullhornFlag());
	}
}
