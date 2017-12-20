package com.workmarket.domains.model.notification;

import com.workmarket.domains.model.DeletableEntity;
import com.workmarket.domains.model.User;
import com.workmarket.domains.model.audit.AuditChanges;

import javax.persistence.*;

/**
 * User: andrew
 * Date: 11/19/13
 */
@Entity(name = "userDeviceAssociation")
@Table(name = "user_device_association")
@AuditChanges
public class UserDeviceAssociation extends DeletableEntity {

	private static final long serialVersionUID = 1L;

	private User user;
	private String deviceType;
	private String deviceUid;

	@ManyToOne(optional = false, fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id", updatable = false)
	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	@Column(name = "device_uid")
	public String getDeviceUid() {
		return deviceUid;
	}

	public void setDeviceUid(String deviceUid) {
		this.deviceUid = deviceUid;
	}

	@Column(name = "device_type")
	public String getDeviceType() {
		return deviceType;
	}

	public void setDeviceType(String deviceType) {
		this.deviceType = deviceType;
	}
}
