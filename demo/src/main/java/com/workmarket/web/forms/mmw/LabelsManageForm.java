package com.workmarket.web.forms.mmw;

import java.io.Serializable;

import com.workmarket.domains.model.PrivacyType;
import org.apache.commons.lang3.StringUtils;

public class LabelsManageForm implements Serializable {
	private static final long serialVersionUID = 1L;

	public static final String VIEW_EDIT = "view_edit";
	public static final String VIEW = "view";
	public static final String IO = "io";
	public static final String IO_R = "io_r";
	public static final String R = "r";

	private Long workSubStatusTypeId;
	private boolean active;
	private Long companyId;
	private String code;
	private String description;
	private String resourceAccess;
	private boolean resourceEditable;
	private Boolean resourceVisible;
	private String notify;
	private boolean notifyClientEnabled;
	private boolean notifyResourceEnabled;
	private boolean alert;
	private String noteRequiredAccess;
	private boolean noteRequired;
	private boolean includeInstructions;
	private String instructions;
	private boolean scheduleRequired;
	private boolean removeAfterReschedule;
	private PrivacyType notePrivacyType = PrivacyType.PUBLIC;
	private boolean removeOnVoidOrCancelled;
	private boolean removeOnPaid;
	private Long[] workTemplateIds;
	private String[] workStatusCodes;
	private Integer workStatusTypeScopeRangeFrom;
	private Integer workStatusTypeScopeRangeTo;
	private String[] workSubStatusTypeRecipientIds;

	public boolean shouldBeResourceEditable() {
		return StringUtils.equals(resourceAccess, VIEW_EDIT);
	}

	public boolean shouldBeResourceVisible() {
		return StringUtils.equals(resourceAccess, VIEW) ||
			StringUtils.equals(resourceAccess, VIEW_EDIT);
	}

	public boolean shouldBeNotifyClientEnabled() {
		return StringUtils.equals(notify, IO) ||
			StringUtils.equals(notify, IO_R);
	}

	public boolean shouldBeNotifyResourceEnabled() {
		return StringUtils.equals(notify, R) ||
			StringUtils.equals(notify, IO_R);
	}

	public Long getWorkSubStatusTypeId() {
		return workSubStatusTypeId;
	}

	public void setWorkSubStatusTypeId(Long workSubStatusTypeId) {
		this.workSubStatusTypeId = workSubStatusTypeId;
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	public Long getCompanyId() {
		return companyId;
	}

	public void setCompanyId(Long companyId) {
		this.companyId = companyId;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getResourceAccess() {
		return resourceAccess;
	}

	public void setResourceAccess(String resourceAccess) {
		this.resourceAccess = resourceAccess;
	}

	public boolean isResourceEditable() {
		return resourceEditable;
	}

	public void setResourceEditable(boolean resourceEditable) {
		this.resourceEditable = resourceEditable;
	}

	public Boolean getResourceVisible() {
		return resourceVisible;
	}

	public void setResourceVisible(Boolean resourceVisible) {
		this.resourceVisible = resourceVisible;
	}

	public String getNotify() {
		return notify;
	}

	public void setNotify(String notify) {
		this.notify = notify;
	}

	public boolean isNotifyClientEnabled() {
		return notifyClientEnabled;
	}

	public void setNotifyClientEnabled(boolean notifyClientEnabled) {
		this.notifyClientEnabled = notifyClientEnabled;
	}

	public boolean isNotifyResourceEnabled() {
		return notifyResourceEnabled;
	}

	public void setNotifyResourceEnabled(boolean notifyResourceEnabled) {
		this.notifyResourceEnabled = notifyResourceEnabled;
	}

	public boolean isAlert() {
		return alert;
	}

	public void setAlert(boolean alert) {
		this.alert = alert;
	}

	public boolean isNoteRequired() {
		return noteRequired;
	}

	public void setNoteRequired(boolean noteRequired) {
		this.noteRequired = noteRequired;
	}

	public boolean isIncludeInstructions() {
		return includeInstructions;
	}

	public void setIncludeInstructions(boolean includeInstructions) {
		this.includeInstructions = includeInstructions;
	}

	public String getInstructions() {
		return instructions;
	}

	public void setInstructions(String instructions) {
		this.instructions = instructions;
	}

	public boolean isScheduleRequired() {
		return scheduleRequired;
	}

	public void setScheduleRequired(boolean scheduleRequired) {
		this.scheduleRequired = scheduleRequired;
	}

	public boolean isRemoveAfterReschedule() {
		return removeAfterReschedule;
	}

	public void setRemoveAfterReschedule(boolean removeAfterReschedule) {
		this.removeAfterReschedule = removeAfterReschedule;
	}
	
	public String getNoteRequiredAccess() {
		return noteRequiredAccess;
	}

	public void setNoteRequiredAccess(String noteRequiredAccess) {
		this.noteRequiredAccess = noteRequiredAccess;
	}

	public PrivacyType getNotePrivacyType() {
		return notePrivacyType;
	}

	public void setNotePrivacyType(PrivacyType notePrivacyType) {
		this.notePrivacyType = notePrivacyType;
	}

	public boolean isRemoveOnVoidOrCancelled() {
		return removeOnVoidOrCancelled;
	}

	public void setRemoveOnVoidOrCancelled(boolean removeOnVoidOrCancelled) {
		this.removeOnVoidOrCancelled = removeOnVoidOrCancelled;
	}

	public boolean isRemoveOnPaid() {
		return removeOnPaid;
	}

	public void setRemoveOnPaid(boolean removeOnPaid) {
		this.removeOnPaid = removeOnPaid;
	}

	public Long[] getWorkTemplateIds() {
		return workTemplateIds;
	}

	public void setWorkTemplateIds(Long[] workTemplateIds) {
		this.workTemplateIds = workTemplateIds;
	}

	public String[] getWorkStatusCodes() {
		return workStatusCodes;
	}

	public void setWorkStatusCodes(String[] workStatusCodes) {
		this.workStatusCodes = workStatusCodes;
	}

	public int getWorkStatusTypeScopeRangeFrom() {
		return workStatusTypeScopeRangeFrom;
	}

	public void setWorkStatusTypeScopeRangeFrom(int workStatusTypeScopeRangeFrom) {
		this.workStatusTypeScopeRangeFrom = workStatusTypeScopeRangeFrom;
	}

	public int getWorkStatusTypeScopeRangeTo() {
		return workStatusTypeScopeRangeTo;
	}

	public void setWorkStatusTypeScopeRangeTo(int workStatusTypeScopeRangeTo) {
		this.workStatusTypeScopeRangeTo = workStatusTypeScopeRangeTo;
	}

	public String[] getWorkSubStatusTypeRecipientIds() { return workSubStatusTypeRecipientIds; }

	public void setWorkSubStatusTypeRecipientIds(String[] workSubStatusTypeRecipientIds) {
		this.workSubStatusTypeRecipientIds = workSubStatusTypeRecipientIds;
	}

}
