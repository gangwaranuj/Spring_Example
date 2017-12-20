package com.workmarket.service.external;

public enum TrackingStatus {
	PENDING("pending"),
	NOT_SHIPPED("not_shipped"),
	IN_TRANSIT("in_transit"),
	DELIVERED("delivered"),
	UNRECOGNIZED("unrecognized"),
	NOT_AVAILABLE("not_available");

	final private String code;

	TrackingStatus(final String code) {
		this.code = code;
	}

	public String getCode() {
		return code;
	}
}
