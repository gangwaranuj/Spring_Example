package com.workmarket.service.external;


public class GeocodingException extends Exception {

	private static final long serialVersionUID = 1586397511306830905L;

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
