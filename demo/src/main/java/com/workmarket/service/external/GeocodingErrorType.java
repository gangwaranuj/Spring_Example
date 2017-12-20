package com.workmarket.service.external;

/**
 * Enumeration that shows the geocoding error type based on the 
 * google response
 * 
 * @author kristian
 *
 */
public enum GeocodingErrorType {
	ZERO_RESULTS("ZERO_RESULTS"),
	OVER_QUERY_LIMIT("OVER_QUERY_LIMIT"),
	REQUEST_DENIED("REQUEST_DENIED"),
	INVALID_REQUEST("INVALID_REQUEST");
	
	private final String googleCode;
	
	private GeocodingErrorType(String googleCode) {
		this.googleCode = googleCode;
	}
	
	public String getGoogleCode() {
		return googleCode;
	}
	
	public static GeocodingErrorType findErrorCode(String errorCode) {
		if (errorCode == null) {
			return null;
		}
		for (GeocodingErrorType error : GeocodingErrorType.values()) {
			if (error.getGoogleCode().equals(errorCode)) {
				return error;
			}
		}
		return null;
	}
	
}
