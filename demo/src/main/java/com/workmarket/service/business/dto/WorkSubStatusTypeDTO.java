package com.workmarket.service.business.dto;

import com.workmarket.domains.model.PrivacyType;


public class WorkSubStatusTypeDTO {

	private Long workSubStatusTypeId;
	private String code;
	private String description;
	private Long companyId;
	private boolean resourceVisible = false;
	private boolean alert = false;
	private boolean notifyClientEnabled = true;
	private boolean notifyResourceEnabled = false;
	private boolean noteRequired = false;
	private boolean includeInstructions = false;
	private String instructions;
	private boolean scheduleRequired = false;
	private boolean removeAfterReschedule = false;
	private boolean resourceEditable = false;
	private boolean active = true;
	private PrivacyType notePrivacyType = PrivacyType.PUBLIC;
	private boolean removeOnVoidOrCancelled = false;
	private boolean removeOnPaid = false;
	private Long[] workTemplateIds;
	private String[] workStatusCodes;
	private String[] workSubStatusTypeRecipientIds;
	

	public Long getWorkSubStatusTypeId() {
		return workSubStatusTypeId;
	}

	public void setWorkSubStatusTypeId(Long workSubStatusTypeId) {
		this.workSubStatusTypeId = workSubStatusTypeId;
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

	public Long getCompanyId() {
		return companyId;
	}

	public void setCompanyId(Long companyId) {
		this.companyId = companyId;
	}

	public boolean isResourceVisible() {
		return resourceVisible;
	}

	public void setResourceVisible(boolean resourceVisible) {
		this.resourceVisible = resourceVisible;
	}

	public boolean isAlert() {
		return alert;
	}

	public void setAlert(boolean alert) {
		this.alert = alert;
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

	public void setRemoveAfterReschedule(boolean removeAfterReschedule) {
		this.removeAfterReschedule = removeAfterReschedule;
	}

	public boolean isRemoveAfterReschedule() {
		return removeAfterReschedule;
	}

	public void setResourceEditable(boolean resourceEditable) {
		this.resourceEditable = resourceEditable;
	}

	public boolean isResourceEditable() {
		return resourceEditable;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	public boolean isActive() {
		return active;
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

	public String[] getWorkSubStatusTypeRecipientIds() { return workSubStatusTypeRecipientIds; }

	public void setWorkSubStatusTypeRecipientIds(String[] workSubStatusTypeRecipientIds) {
		this.workSubStatusTypeRecipientIds = workSubStatusTypeRecipientIds;
	}

}
