package com.workmarket.domains.model.notification;

import com.workmarket.domains.model.LookupEntity;

import javax.persistence.Entity;
import javax.persistence.Table;

@Entity(name="notificationStatusType")
@Table(name="notification_status_type")
public class NotificationStatusType extends LookupEntity {

	private static final long serialVersionUID = 6974286145336375864L;

	public static final String PUBLISHED = "published";
	public static final String DRAFT = "draft";
	public static final String ARCHIVED = "archived";

	public NotificationStatusType() {}

	public NotificationStatusType(String code){
		super(code);
	}

	public static NotificationStatusType newNotificationStatus(String code) {
		return new NotificationStatusType(code);
	}
}
