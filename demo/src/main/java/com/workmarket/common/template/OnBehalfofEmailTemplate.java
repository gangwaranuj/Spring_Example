package com.workmarket.common.template;

import com.workmarket.domains.model.notification.NotificationType;
import com.workmarket.service.infra.communication.ReplyToType;

public class OnBehalfofEmailTemplate extends NotificationTemplate {
	
	private static final long serialVersionUID = 245645696313386367L;
	
	private String actionLabel;
	
	private String onBehalfOf;
	
	private String workTitle;
	
	private String workNumber;
	
	private String note;
	
	public OnBehalfofEmailTemplate(Long fromId, Long toId, String workTitle, String workNumber, String actionLabel, String onBehalfOf, String note) {
		super(fromId, toId, new NotificationType(NotificationType.RESOURCE_WORK_ON_BEHALF_OF), ReplyToType.TRANSACTIONAL_FROM_USER);
		this.workTitle = workTitle;
		this.workNumber = workNumber;
		this.onBehalfOf = onBehalfOf + " " + actionLabel;
		this.note = note;
	}

	public String getActionLabel() {
		return actionLabel;
	}

	public void setActionLabel(String actionLabel) {
		this.actionLabel = actionLabel;
	}

	public String getOnBehalfOf() {
		return onBehalfOf;
	}

	public void setOnBehalfOf(String onBehalfOf) {
		this.onBehalfOf = onBehalfOf;
	}

	public String getWorkTitle() {
		return workTitle;
	}

	public void setWorkTitle(String workTitle) {
		this.workTitle = workTitle;
	}

	public String getWorkNumber() {
		return workNumber;
	}

	public void setWorkNumber(String workNumber) {
		this.workNumber = workNumber;
	}

	public String getNote() {
		return note;
	}

	public void setNote(String note) {
		this.note = note;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}



}