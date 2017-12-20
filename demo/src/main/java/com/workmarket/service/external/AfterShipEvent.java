package com.workmarket.service.external;

/**
 * Created by alejandrosilva on 3/11/15.
 */
public enum AfterShipEvent {
	TRACKING_UPDATE("tracking_update");

	private String code;

	AfterShipEvent(String code) {
		this.code = code;
	}

	public String getCode() {
		return code;
	}
}
