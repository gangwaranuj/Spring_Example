package com.workmarket.web.forms;

import com.workmarket.service.business.dto.NotificationPreferenceDTO;

import java.io.Serializable;
import java.util.Map;

public class NotificationsForm implements Serializable {
	private static final long serialVersionUID = 1L;

	private String ivrPreference;
	private boolean selectAllEmail;
	private boolean selectAllSms;
	private boolean selectAllVoice;
	private boolean selectAllBullhorn;
	private boolean selectAllPush;

	private Map<String,NotificationPreferenceDTO> notifications;

	public String getIvrPreference() {
		return ivrPreference;
	}

	public void setIvrPreference(String ivrPreference) {
		this.ivrPreference = ivrPreference;
	}

	public boolean isSelectAllEmail() {
		return selectAllEmail;
	}

	public void setSelectAllEmail(boolean selectAllEmail) {
		this.selectAllEmail = selectAllEmail;
	}

	public boolean isSelectAllSms() {
		return selectAllSms;
	}

	public void setSelectAllSms(boolean selectAllSms) {
		this.selectAllSms = selectAllSms;
	}

	public boolean isSelectAllVoice() {
		return selectAllVoice;
	}

	public void setSelectAllVoice(boolean selectAllVoice) {
		this.selectAllVoice = selectAllVoice;
	}

	public boolean isSelectAllBullhorn() {
		return selectAllBullhorn;
	}

	public void setSelectAllBullhorn(boolean selectAllBullhorn) {
		this.selectAllBullhorn = selectAllBullhorn;
	}

	public boolean isSelectAllPush() {
		return selectAllPush;
	}

	public void setSelectAllPush(boolean selectAllPush) {
		this.selectAllPush = selectAllPush;
	}

	public Map<String, NotificationPreferenceDTO> getNotifications() {
		return notifications;
	}

	public void setNotifications(Map<String, NotificationPreferenceDTO> notifications) {
		this.notifications = notifications;
	}
}
