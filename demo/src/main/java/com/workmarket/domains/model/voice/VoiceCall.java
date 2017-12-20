package com.workmarket.domains.model.voice;

import javax.persistence.Column;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.DiscriminatorType;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.workmarket.domains.model.User;
import com.workmarket.domains.model.audit.AuditedEntity;
import com.workmarket.domains.model.notification.NotificationType;
import com.workmarket.domains.work.model.Work;
import com.workmarket.configuration.Constants;
import com.workmarket.domains.model.audit.AuditChanges;

@Entity(name="voiceCall")
@Table(name="voice_call")
@DiscriminatorColumn(name="type", discriminatorType= DiscriminatorType.STRING)
@DiscriminatorValue("base")
@NamedQueries({
	@NamedQuery(name="voiceCall.byCallId", query="from voiceCall where callId = :call_id"),
	@NamedQuery(name="voiceCall.latestByUserForWork", query="from voiceCall c where c.user.id = :user_id and c.work.id = :work_id order by c.id DESC limit 1"),
	@NamedQuery(name="voiceCall.countByWorkSinceDate", query="select count(c) from voiceCall c where c.createdOn > :date and c.work.id = :work_id")
})
@AuditChanges
public abstract class VoiceCall extends AuditedEntity {

	private static final long serialVersionUID = 1L;

	private User user;
	private Work work;

	private NotificationType notificationType;

	private String fromNumber;
	private String toNumber;

	private String callId;
	private String callStatus;
	private String callSubStatus = "start";
	private String callDuration;
	private Integer failedPrompts = 0;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id", referencedColumnName = "id")
	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "work_id", referencedColumnName = "id")
	public Work getWork() {
		return work;
	}

	public void setWork(Work work) {
		this.work = work;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "notification_type_code", referencedColumnName = "code")
	public NotificationType getNotificationType() {
		return notificationType;
	}

	public void setNotificationType(NotificationType notificationType) {
		this.notificationType = notificationType;
	}

	@Column(name = "from_number")
	public String getFromNumber() {
		return fromNumber;
	}

	public void setFromNumber(String fromNumber) {
		this.fromNumber = fromNumber;
	}

	@Column(name = "to_number")
	public String getToNumber() {
		return toNumber;
	}

	public void setToNumber(String toNumber) {
		this.toNumber = toNumber;
	}

	@Column(name = "call_id", nullable = false)
	public String getCallId() {
		return callId;
	}

	public void setCallId(String callId) {
		this.callId = callId;
	}

	@Column(name = "call_status")
	public String getCallStatus() {
		return callStatus;
	}

	public void setCallStatus(String callStatus) {
		this.callStatus = callStatus;
	}

	@Column(name = "call_sub_status")
	public String getCallSubStatus() {
		return callSubStatus;
	}

	public void setCallSubStatus(String callSubStatus) {
		this.callSubStatus = callSubStatus;
	}

	@Column(name = "call_duration")
	public String getCallDuration() {
		return callDuration;
	}

	public void setCallDuration(String callDuration) {
		this.callDuration = callDuration;
	}

	@Column(name = "call_failed_prompts")
	public Integer getFailedPrompts() {
		return failedPrompts;
	}

	public void setFailedPrompts(Integer failedPrompts) {
		this.failedPrompts = failedPrompts;
	}

	@Transient
	public boolean didExceedFailedPrompts() {
		return failedPrompts > Constants.VOICE_CALL_FAILED_PROMPTS_CUTOFF;
	}
}
