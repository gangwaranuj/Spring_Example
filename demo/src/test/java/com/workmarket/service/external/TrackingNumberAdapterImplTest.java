package com.workmarket.service.external;

import com.google.common.collect.ImmutableList;
import com.workmarket.common.api.exception.NotFoundException;
import com.workmarket.common.core.RequestContext;
import com.workmarket.service.infra.sms.ExperimentPercentageEvaluator;
import com.workmarket.service.web.WebRequestContextProvider;
import com.workmarket.shipment.client.ShipmentClient;
import com.workmarket.shipment.vo.ShippingProvider;
import com.workmarket.shipment.vo.ShippingProviders;
import com.workmarket.shipment.vo.ShippingStatus;
import com.workmarket.shipment.vo.TrackingResponse;
import org.apache.commons.collections.CollectionUtils;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.http.HttpStatus;
import rx.Observable;

import static com.workmarket.service.external.AfterShipGatewayImplMock.BAD_REQUEST_RESPONSE_JSON;
import static com.workmarket.service.external.AfterShipGatewayImplMock.DELIVERED_STATUS;
import static com.workmarket.service.external.AfterShipGatewayImplMock.NOT_FOUND_RESPONSE_JSON;
import static com.workmarket.service.external.AfterShipGatewayImplMock.NOT_SHIPPED_STATUS;
import static com.workmarket.service.external.AfterShipGatewayImplMock.SHIPPING_PROVIDER;
import static com.workmarket.service.external.AfterShipGatewayImplMock.SHIPPING_PROVIDER_CODE;
import static com.workmarket.service.external.AfterShipGatewayImplMock.SUCCESSFUL_DETECT_COURIER_RESPONSE;
import static com.workmarket.service.external.AfterShipGatewayImplMock.VALID_TRACKING_NUMBER;
import static com.workmarket.service.external.AfterShipGatewayImplMock.buildSuccessfulJsonResponse;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class TrackingNumberAdapterImplTest {

	@Mock AfterShipGatewayImpl afterShipGateway;
	@Mock ExperimentPercentageEvaluator experiment;
	@Mock ShipmentClient shipmentClient;
	@Mock WebRequestContextProvider webRequestContextProvider;

	@InjectMocks TrackingNumberAdapterImpl trackingNumberAdapter;

	@Before
	public void setUp() {
		when(experiment.shouldRunExperiment("aftership")).thenReturn(false);
	}

	@Test
	public void detectCourier_exceptionThrown_returnsServerErrorResponse() throws Exception {
		when(afterShipGateway.detectCourier(anyString())).thenThrow(JSONException.class);

		ShippingProviderDetectResponse response = trackingNumberAdapter.detectShippingProvider(anyString());

		assertFalse(response.isSuccessful());
		assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getResponseCode());
	}

	@Test
	public void newDetectCourier_exceptionThrown_returnsServerErrorResponse() throws Exception {
		when(experiment.shouldRunExperiment("aftership")).thenReturn(true);
		when(shipmentClient.detectShippingProviders(anyString(), (RequestContext)anyObject()))
				.thenReturn(Observable.<ShippingProviders>error(new JSONException("AIEEE")));

		ShippingProviderDetectResponse response = trackingNumberAdapter.detectShippingProvider(anyString());

		assertFalse(response.isSuccessful());
		assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getResponseCode());
	}

	@Test
	public void detectCourier_validNumber_returnSuccessfulResponse() throws Exception {
		when(afterShipGateway.detectCourier(anyString())).thenReturn(new JSONObject(SUCCESSFUL_DETECT_COURIER_RESPONSE));

		ShippingProviderDetectResponse response = trackingNumberAdapter.detectShippingProvider(anyString());

		assertTrue(response.isSuccessful());
		assertEquals(HttpStatus.OK, response.getResponseCode());
		assertTrue(CollectionUtils.isNotEmpty(response.getShippingProviders()));
	}

	@Test
	public void newDetectCourier_validNumber_returnSuccessfulResponse() throws Exception {
		when(experiment.shouldRunExperiment("aftership")).thenReturn(true);
		when(shipmentClient.detectShippingProviders(anyString(), (RequestContext)anyObject()))
				.thenReturn(Observable.just(new ShippingProviders(
						ImmutableList.of(ShippingProvider.usps)
				)));
		ShippingProviderDetectResponse response = trackingNumberAdapter.detectShippingProvider(anyString());

		assertTrue(response.isSuccessful());
		assertEquals(HttpStatus.OK, response.getResponseCode());
		assertTrue(CollectionUtils.isNotEmpty(response.getShippingProviders()));
	}

	@Test
	public void detectCourier_invalidNumber_returnBadRequestCode() throws Exception {
		when(afterShipGateway.detectCourier(anyString())).thenReturn(new JSONObject(BAD_REQUEST_RESPONSE_JSON));

		ShippingProviderDetectResponse response = trackingNumberAdapter.detectShippingProvider(anyString());

		assertFalse(response.isSuccessful());
		assertEquals(HttpStatus.BAD_REQUEST, response.getResponseCode());
	}

	@Test
	public void detectCourier_nonBadRequestError_returnGenericFailResponse() throws Exception {
		when(afterShipGateway.detectCourier(anyString())).thenReturn(new JSONObject(NOT_FOUND_RESPONSE_JSON));

		ShippingProviderDetectResponse response = trackingNumberAdapter.detectShippingProvider(anyString());

		assertFalse(response.isSuccessful());
		assertNull(response.getResponseCode());
	}

	@Test
	public void track_exceptionThrown_returnsFailResponse() throws Exception {
		when(afterShipGateway.track(anyString())).thenThrow(JSONException.class);

		TrackingNumberResponse response = trackingNumberAdapter.track(anyString());

		assertFalse(response.isSuccessful());
	}

	@Test
	public void newTrack_exceptionThrown_returnsFailResponse() throws Exception {
		when(experiment.shouldRunExperiment("aftership")).thenReturn(true);
		when(shipmentClient.startTracking(anyString(), (RequestContext) anyObject()))
				.thenReturn(Observable.<TrackingResponse>error(new RuntimeException()));

		TrackingNumberResponse response = trackingNumberAdapter.track(anyString());

		assertFalse(response.isSuccessful());
	}

	@Test
	public void track_validNumber_returnSuccessfulResponse() throws Exception {
		when(afterShipGateway.track(anyString())).thenReturn(
			buildSuccessfulJsonResponse(VALID_TRACKING_NUMBER, SHIPPING_PROVIDER_CODE, NOT_SHIPPED_STATUS)
		);

		TrackingNumberResponse response = trackingNumberAdapter.track(anyString());

		assertTrue(response.isSuccessful());
		assertEquals(HttpStatus.OK, response.getResponseCode());
		assertEquals(VALID_TRACKING_NUMBER, response.getTrackingNumber());
		assertEquals(TrackingStatus.NOT_SHIPPED, response.getTrackingStatus());
		assertEquals(SHIPPING_PROVIDER, response.getShippingProvider());
	}

	@Test
	public void newTrack_validNumber_returnSuccessfulResponse() throws Exception {
		when(experiment.shouldRunExperiment("aftership")).thenReturn(true);
		when(shipmentClient.startTracking(anyString(), (RequestContext) anyObject()))
				.thenReturn(Observable.just(
						new TrackingResponse(ShippingProvider.ups, VALID_TRACKING_NUMBER, ShippingStatus.NOT_SHIPPED)
				));

		TrackingNumberResponse response = trackingNumberAdapter.track(anyString());

		assertTrue(response.isSuccessful());
		assertEquals(HttpStatus.OK, response.getResponseCode());
		assertEquals(VALID_TRACKING_NUMBER, response.getTrackingNumber());
		assertEquals(TrackingStatus.NOT_SHIPPED, response.getTrackingStatus());
		assertEquals(SHIPPING_PROVIDER, response.getShippingProvider());
	}

	@Test
	public void track_invalidNumber_returnBadRequestCode() throws Exception {
		when(afterShipGateway.track(anyString())).thenReturn(new JSONObject(BAD_REQUEST_RESPONSE_JSON));

		TrackingNumberResponse response = trackingNumberAdapter.track(anyString());

		assertFalse(response.isSuccessful());
		assertEquals(HttpStatus.BAD_REQUEST, response.getResponseCode());
	}

	@Test
	public void track_untrackedNumber_returnNotFoundCode() throws Exception {
		when(afterShipGateway.track(anyString())).thenReturn(new JSONObject(NOT_FOUND_RESPONSE_JSON));

		TrackingNumberResponse response = trackingNumberAdapter.track(anyString());

		assertFalse(response.isSuccessful());
		assertEquals(HttpStatus.NOT_FOUND, response.getResponseCode());
	}

	@Test
	public void newTrack_untrackedNumber_returnNotFoundCode() throws Exception {
		when(experiment.shouldRunExperiment("aftership")).thenReturn(true);
		when(shipmentClient.startTracking(anyString(), (RequestContext) anyObject()))
			.thenReturn(Observable.<TrackingResponse>error(new NotFoundException("blah")));

		TrackingNumberResponse response = trackingNumberAdapter.track(anyString());

		assertFalse(response.isSuccessful());
		assertEquals(HttpStatus.NOT_FOUND, response.getResponseCode());
	}

	@Test
	public void get_exceptionThrown_returnsFailResponse() throws Exception {
		when(afterShipGateway.get(anyString(), anyString())).thenThrow(JSONException.class);

		TrackingNumberResponse response = trackingNumberAdapter.get("someNumber", "usps");

		assertFalse(response.isSuccessful());
	}

	@Test
	public void newGet_exceptionThrown_returnsFailResponse() throws Exception {
		when(experiment.shouldRunExperiment("aftership")).thenReturn(true);
		when(shipmentClient.getTrackingInfo(anyString(), (ShippingProvider) anyObject(), (RequestContext) anyObject()))
				.thenReturn(Observable.<TrackingResponse>error(new RuntimeException()));

		TrackingNumberResponse response = trackingNumberAdapter.get("someNumber", "usps");

		assertFalse(response.isSuccessful());
	}

	@Test
	public void get_validNumber_returnSuccessfulResponse() throws Exception {
		when(afterShipGateway.get(anyString(), anyString())).thenReturn(
			buildSuccessfulJsonResponse(VALID_TRACKING_NUMBER, SHIPPING_PROVIDER_CODE, DELIVERED_STATUS)
		);

		TrackingNumberResponse response = trackingNumberAdapter.get("someNumber", "usps");

		assertTrue(response.isSuccessful());
		assertEquals(HttpStatus.OK, response.getResponseCode());
		assertEquals(VALID_TRACKING_NUMBER, response.getTrackingNumber());
		assertEquals(TrackingStatus.DELIVERED, response.getTrackingStatus());
		assertEquals(SHIPPING_PROVIDER, response.getShippingProvider());
	}

	@Test
	public void newGet_validNumber_returnSuccessfulResponse() throws Exception {
		when(experiment.shouldRunExperiment("aftership")).thenReturn(true);
		when(shipmentClient.getTrackingInfo(anyString(), (ShippingProvider) anyObject(), (RequestContext) anyObject()))
				.thenReturn(Observable.just(
						new TrackingResponse(ShippingProvider.ups, VALID_TRACKING_NUMBER, ShippingStatus.DELIVERED)));

		TrackingNumberResponse response = trackingNumberAdapter.get("someNumber", "usps");

		assertTrue(response.isSuccessful());
		assertEquals(HttpStatus.OK, response.getResponseCode());
		assertEquals(VALID_TRACKING_NUMBER, response.getTrackingNumber());
		assertEquals(TrackingStatus.DELIVERED, response.getTrackingStatus());
		assertEquals(SHIPPING_PROVIDER, response.getShippingProvider());
	}

	@Test
	public void get_invalidNumber_returnBadRequestCode() throws Exception {
		when(afterShipGateway.get(anyString(), anyString())).thenReturn(new JSONObject(BAD_REQUEST_RESPONSE_JSON));

		TrackingNumberResponse response = trackingNumberAdapter.get("someNumber", "usps");

		assertFalse(response.isSuccessful());
		assertEquals(HttpStatus.BAD_REQUEST, response.getResponseCode());
	}

	@Test
	public void get_untrackedNumber_returnNotFoundCode() throws Exception {
		when(afterShipGateway.get(anyString(), anyString())).thenReturn(new JSONObject(NOT_FOUND_RESPONSE_JSON));

		TrackingNumberResponse response = trackingNumberAdapter.get("someNumber", "usps");

		assertFalse(response.isSuccessful());
		assertEquals(HttpStatus.NOT_FOUND, response.getResponseCode());
	}

	@Test
	public void newGet_untrackedNumber_returnNotFoundCode() throws Exception {
		when(experiment.shouldRunExperiment("aftership")).thenReturn(true);
		when(shipmentClient.getTrackingInfo(anyString(), (ShippingProvider) anyObject(), (RequestContext) anyObject()))
				.thenReturn(Observable.<TrackingResponse>error(new NotFoundException("xxx")));

		TrackingNumberResponse response = trackingNumberAdapter.get("someNumber", "usps");

		assertFalse(response.isSuccessful());
		assertEquals(HttpStatus.NOT_FOUND, response.getResponseCode());
	}
}
