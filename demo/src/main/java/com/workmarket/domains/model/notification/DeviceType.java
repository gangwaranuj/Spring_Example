package com.workmarket.domains.model.notification;

import com.workmarket.domains.model.LookupEntity;

import javax.persistence.Entity;
import javax.persistence.Table;

@Entity(name="DeviceType")
@Table(name="device_type")
public class DeviceType extends LookupEntity {

	private static final long serialVersionUID = 3270581048308578834L;

	public static DeviceType ANDROID = DeviceType.newInstance("android");
	public static DeviceType IOS = DeviceType.newInstance("ios");

	public DeviceType() {}

	private DeviceType (String code) {
		super(code);
	}

	private static DeviceType newInstance(String code) {
		return new DeviceType(code);
	}
}
