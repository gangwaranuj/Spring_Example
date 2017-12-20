package com.workmarket.service.external;

public enum AfterShipError {
	TRACKING_EXISTS(4003);

	private int code;

	AfterShipError(int code) {
		this.code = code;
	}

	public int getCode() {
		return code;
	}
}
