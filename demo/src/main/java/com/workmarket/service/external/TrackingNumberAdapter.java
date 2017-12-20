package com.workmarket.service.external;

public interface TrackingNumberAdapter {

	ShippingProviderDetectResponse detectShippingProvider(final String trackingNumber);

	TrackingNumberResponse track(final String trackingNumber);

	TrackingNumberResponse get(final String trackingNumber, final String provider);

	// called by the AfterShipController.
	TrackingStatus translateAftershipTrackingStatus(final String aftershipTrackingStatus);

}
