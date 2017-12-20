package com.workmarket.domains.model.notification;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.workmarket.domains.model.User;

import java.util.Calendar;

@JsonIgnoreProperties(value = { "encryptedId", "idHash" })
public class UserNotification {
	private NotificationType notificationType;
	private String displayMessage;
	private boolean sticky = false;
	private User fromUser;
	private User user;
	private boolean viewed = false;
	private boolean archived = false;
	private String uuid;
	private Calendar createdOn;
	private Calendar viewedAt;

	public UserNotification() {}

	public UserNotification(NotificationType notificationType) {
		this.notificationType = notificationType;
	}

	public UserNotification(NotificationType notificationType, String displayMessage) {
		this(notificationType);
		this.displayMessage = displayMessage;
	}

	public UserNotification(NotificationType notificationType, String displayMessage, boolean sticky) {
		this(notificationType, displayMessage);
		this.sticky = sticky;
	}

	public UserNotification(NotificationType notificationType, String displayMessage, boolean sticky, User fromUser) {
		this(notificationType, displayMessage, sticky);
		this.fromUser = fromUser;
	}

	public UserNotification(NotificationType notificationType, String displayMessage, boolean sticky, User fromUser, User toUser) {
		this(notificationType, displayMessage, sticky, fromUser);
		this.user = toUser;
	}

	public UserNotification(final NotificationType notificationType, final String displayMessage, final boolean sticky,
							final User fromUser, final User toUser, final String uuid) {
		this(notificationType, displayMessage, sticky, fromUser, toUser);
		this.uuid = uuid;
	}

	public NotificationType getNotificationType() {
		return notificationType;
	}

	public void setViewedAt(final Calendar when) {
		this.viewedAt = when;
	}

	public Calendar getViewedAt() {
		return viewedAt;
	}

	public void setCreatedOn(final Calendar when) {
		this.createdOn = when;
	}

	public Calendar getCreatedOn() {
		return createdOn;
	}
	public void setNotificationType(NotificationType notificationType) {
		this.notificationType = notificationType;
	}

	public String getDisplayMessage() {
		return displayMessage;
	}

	public void setDisplayMessage(String displayMessage) {
		this.displayMessage = displayMessage;
	}

	public boolean isSticky() {
		return sticky;
	}

	public void setSticky(boolean sticky) {
		this.sticky = sticky;
	}

	public User getFromUser() {
		return fromUser;
	}

	public void setFromUser(User fromUser) {
		this.fromUser = fromUser;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public boolean isViewed() {
		return viewed;
	}

	public void setViewed(boolean viewed) {
		this.viewed = viewed;
	}

	public boolean isArchived() {
		return archived;
	}

	public void setArchived(final boolean archived) {
		this.archived = archived;
	}

	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}
}
