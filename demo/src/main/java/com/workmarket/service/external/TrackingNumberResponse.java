package com.workmarket.service.external;

import com.workmarket.domains.work.model.part.ShippingProvider;
import org.springframework.http.HttpStatus;

public class TrackingNumberResponse {

	private TrackingStatus trackingStatus;
	private HttpStatus responseCode;
	private boolean successful;
	private ShippingProvider shippingProvider;
	private String trackingNumber;
	private AfterShipError metaCode;

	public TrackingNumberResponse() {}

	public TrackingStatus getTrackingStatus() {
		return trackingStatus;
	}

	public TrackingNumberResponse setTrackingStatus(TrackingStatus trackingStatus) {
		this.trackingStatus = trackingStatus;
		return this;
	}

	public HttpStatus getResponseCode() {
		return responseCode;
	}

	public TrackingNumberResponse setResponseCode(HttpStatus responseCode) {
		this.responseCode = responseCode;
		return this;
	}

	public ShippingProvider getShippingProvider() {
		return shippingProvider;
	}

	public TrackingNumberResponse setShippingProvider(ShippingProvider shippingProvider) {
		this.shippingProvider = shippingProvider;
		return this;
	}

	public String getTrackingNumber() {
		return trackingNumber;
	}

	public TrackingNumberResponse setTrackingNumber(String trackingNumber) {
		this.trackingNumber = trackingNumber;
		return this;
	}

	public TrackingNumberResponse setSuccessful(boolean isSuccessful) {
		this.successful = isSuccessful;
		return this;
	}

	public boolean isSuccessful() {
		return this.successful;
	}

	public AfterShipError getMetaCode() {
		return metaCode;
	}

	public TrackingNumberResponse setMetaCode(AfterShipError metaCode) {
		this.metaCode = metaCode;
		return this;
	}

	public static TrackingNumberResponse success() {
		return new TrackingNumberResponse()
			.setSuccessful(true);
	}

	public static TrackingNumberResponse fail() {
		return new TrackingNumberResponse()
			.setSuccessful(false);
	}

	public static TrackingNumberResponse badRequest() {
		return new TrackingNumberResponse()
			.setResponseCode(HttpStatus.BAD_REQUEST)
			.setSuccessful(false);
	}

	public static TrackingNumberResponse notFound() {
		return new TrackingNumberResponse()
			.setResponseCode(HttpStatus.NOT_FOUND)
			.setSuccessful(false);
	}

	public static TrackingNumberResponse trackingExists() {
		return TrackingNumberResponse.badRequest().setMetaCode(AfterShipError.TRACKING_EXISTS);
	}
}
