package com.workmarket.service.exception.geo;

import com.workmarket.service.infra.geo.GeocodingErrorType;

public class GeocodingException extends RuntimeException {

	private static final long serialVersionUID = -7556808545908695184L;
	
	private GeocodingErrorType errorType;

	public GeocodingException(String error) {
		super(error);
	}
	
	public GeocodingException(String error, Exception cause) {
		super(error, cause);
	}
	
	public GeocodingException(String error, GeocodingErrorType errorType, Exception cause) {
		super(error, cause);
		this.errorType = errorType;
	}
	
	public GeocodingException(String error, GeocodingErrorType errorType) {
		super(error);
		this.errorType = errorType;
	}
	
	public GeocodingErrorType getErrorType() {
		return errorType;
	}

}
