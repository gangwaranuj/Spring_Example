package com.workmarket.domains.work.model.state;

import com.workmarket.domains.model.PrivacyType;
import com.workmarket.configuration.Constants;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * Created with IntelliJ IDEA.
 * User: rocio
 * Date: 4/13/12
 * Time: 10:04 AM
 * To change this template use File | Settings | File Templates.
 */
@Embeddable
@Access(AccessType.PROPERTY)
public class WorkSubStatusDescriptor implements Serializable {

	private static final long serialVersionUID = -107431363021897139L;

	@NotNull
	private Boolean clientVisible = Boolean.FALSE;
	@NotNull
	private Boolean resourceVisible = Boolean.FALSE;

	private boolean custom = false;
	private boolean alert = false;
	private boolean userResolvable = false;
	private boolean actionResolvable = false;
	private boolean notifyClientEnabled = true;
	private boolean notifyResourceEnabled = false;
	private boolean noteRequired = false;
	private boolean includeInstructions = false;
	private String instructions;
	private boolean scheduleRequired = false;
	private boolean removeAfterReschedule = false;
	private PrivacyType notePrivacy = PrivacyType.PUBLIC;
	private boolean removeOnVoidOrCancelled = false;
	private boolean removeOnPaid = false;

	@NotNull
	private WorkSubStatusType.SubStatusType subStatusType = WorkSubStatusType.SubStatusType.ASSIGNMENT;
	@NotNull
	private WorkSubStatusType.TriggeredBy triggeredBy = WorkSubStatusType.TriggeredBy.CLIENT;

	@Column(name = "client_visible", nullable = false)
	public Boolean getClientVisible() {
		return clientVisible;
	}

	public void setClientVisible(Boolean clientVisible) {
		this.clientVisible = clientVisible;
	}

	@Column(name = "resource_visible", nullable = false)
	public Boolean getResourceVisible() {
		return resourceVisible;
	}

	public void setResourceVisible(Boolean resourceVisible) {
		this.resourceVisible = resourceVisible;
	}

	@Column(name = "sub_status_type", nullable = false)
	@Enumerated(EnumType.STRING)
	public WorkSubStatusType.SubStatusType getSubStatusType() {
		return subStatusType;
	}

	public void setSubStatusType(WorkSubStatusType.SubStatusType subStatusType) {
		this.subStatusType = subStatusType;
	}

	@Column(name = "custom", nullable = false)
	public boolean isCustom() {
		return custom;
	}

	public void setCustom(boolean custom) {
		this.custom = custom;
	}

	@Column(name = "alert", nullable = false)
	public boolean isAlert() {
		return alert;
	}

	public void setAlert(boolean alert) {
		this.alert = alert;
	}

	@Column(name = "user_resolvable", nullable = false)
	public boolean isUserResolvable() {
		return userResolvable;
	}

	public void setUserResolvable(boolean userResolvable) {
		this.userResolvable = userResolvable;
	}

	@Column(name = "action_resolvable", nullable = false)
	public boolean isActionResolvable() {
		return actionResolvable;
	}

	public void setActionResolvable(boolean actionResolvable) {
		this.actionResolvable = actionResolvable;
	}

	@Column(name = "triggered_by", nullable = false)
	@Enumerated(EnumType.STRING)
	public WorkSubStatusType.TriggeredBy getTriggeredBy() {
		return triggeredBy;
	}

	public void setTriggeredBy(WorkSubStatusType.TriggeredBy triggeredBy) {
		this.triggeredBy = triggeredBy;
	}

	@Column(name = "notify_client_enabled", nullable = false)
	public boolean isNotifyClientEnabled() {
		return notifyClientEnabled;
	}

	public void setNotifyClientEnabled(boolean notifyClientEnabled) {
		this.notifyClientEnabled = notifyClientEnabled;
	}

	@Column(name = "notify_resource_enabled", nullable = false)
	public boolean isNotifyResourceEnabled() {
		return notifyResourceEnabled;
	}

	public void setNotifyResourceEnabled(boolean notifyResourceEnabled) {
		this.notifyResourceEnabled = notifyResourceEnabled;
	}

	@Column(name = "note_required", nullable = false)
	public boolean isNoteRequired() {
		return noteRequired;
	}

	public void setNoteRequired(boolean noteRequired) {
		this.noteRequired = noteRequired;
	}

	@Column(name = "include_instructions", nullable = false)
	public boolean isIncludeInstructions() {
		return includeInstructions;
	}

	public void setIncludeInstructions(boolean includeInstructions) {
		this.includeInstructions = includeInstructions;
	}

	@Column(name = "instructions", length = Constants.TEXT_MAX_LENGTH)
	public String getInstructions() {
		return instructions;
	}

	public void setInstructions(String instructions) {
		this.instructions = instructions;
	}

	@Column(name = "schedule_required", nullable = false)
	public boolean isScheduleRequired() {
		return scheduleRequired;
	}

	public void setScheduleRequired(boolean scheduleRequired) {
		this.scheduleRequired = scheduleRequired;
	}

	@Column(name = "remove_after_reschedule", nullable = false)
	public boolean isRemoveAfterReschedule() {
		return removeAfterReschedule;
	}

	public void setRemoveAfterReschedule(boolean removeAfterReschedule) {
		this.removeAfterReschedule = removeAfterReschedule;
	}

	@Enumerated(EnumType.STRING)
	@Column(name = "note_privacy_type", nullable = false)
	public PrivacyType getNotePrivacy() {
		return notePrivacy;
	}

	public void setNotePrivacy(PrivacyType notePrivacy) {
		this.notePrivacy = notePrivacy;
	}

	@Column(name = "remove_on_void_cancelled", nullable = false)
	public boolean isRemoveOnVoidOrCancelled() {
		return removeOnVoidOrCancelled;
	}

	public void setRemoveOnVoidOrCancelled(boolean removeOnVoidOrCancelled) {
		this.removeOnVoidOrCancelled = removeOnVoidOrCancelled;
	}

	@Column(name = "remove_on_paid", nullable = false)
	public boolean isRemoveOnPaid() {
		return removeOnPaid;
	}

	public void setRemoveOnPaid(boolean removeOnPaid) {
		this.removeOnPaid = removeOnPaid;
	}

	@Override
	public String toString() {
		return "WorkSubStatusDescriptor{" +
				"actionResolvable=" + actionResolvable +
				", clientVisible=" + clientVisible +
				", resourceVisible=" + resourceVisible +
				", custom=" + custom +
				", alert=" + alert +
				", userResolvable=" + userResolvable +
				", notifyClientEnabled=" + notifyClientEnabled +
				", notifyResourceEnabled=" + notifyResourceEnabled +
				", noteRequired=" + noteRequired +
				", includeInstructions=" + includeInstructions +
				", instructions='" + instructions + '\'' +
				", scheduleRequired=" + scheduleRequired +
				", removeAfterReschedule=" + removeAfterReschedule +
				", notePrivacy=" + notePrivacy +
				", removeOnVoidOrCancelled=" + removeOnVoidOrCancelled +
				", removeOnPaid=" + removeOnPaid +
				", subStatusType=" + subStatusType +
				", triggeredBy=" + triggeredBy +
				'}';
	}
}
