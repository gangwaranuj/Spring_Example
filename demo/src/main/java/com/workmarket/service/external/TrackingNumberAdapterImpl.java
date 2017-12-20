package com.workmarket.service.external;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.mashape.unirest.http.exceptions.UnirestException;
import com.workmarket.common.api.exception.ForbiddenException;
import com.workmarket.common.api.exception.NotFoundException;
import com.workmarket.domains.work.model.part.ShippingProvider;
import com.workmarket.service.infra.sms.ExperimentPercentageEvaluator;
import com.workmarket.service.web.WebRequestContextProvider;
import com.workmarket.shipment.client.ShipmentClient;
import com.workmarket.shipment.vo.ShippingProviders;
import com.workmarket.shipment.vo.TrackingResponse;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import rx.Observable;
import rx.functions.Func1;

import java.util.List;

@Service
public class TrackingNumberAdapterImpl implements TrackingNumberAdapter {
	private static final Log LOGGER = LogFactory.getLog(TrackingNumberAdapterImpl.class);
	private static final Func1<TrackingResponse, TrackingNumberResponse> CONVERT_TRACKING_RESPONSE = new Func1<TrackingResponse, TrackingNumberResponse>() {
		@Override
		public TrackingNumberResponse call(final TrackingResponse trackingResponse) {
			final TrackingNumberResponse response = new TrackingNumberResponse();
			response.setSuccessful(true);
			response.setResponseCode(HttpStatus.OK);
			response.setTrackingNumber(trackingResponse.getTrackingNumber());
			response.setShippingProvider(ShippingProvider.getShippingProvider(trackingResponse.getProvider().toString()));
			response.setTrackingStatus(TrackingStatus.valueOf(trackingResponse.getStatus().toString()));
			return response;
		}
	};

	@Autowired AfterShipGateway afterShipGateway;
	@Autowired ExperimentPercentageEvaluator experiment;
	@Autowired ShipmentClient shipmentClient;
	@Autowired WebRequestContextProvider webRequestContextProvider;

	private static final List<String>
		AFTERSHIP_PENDING_STATUSES = ImmutableList.<String>builder()
			.add("Pending")
			.build(),

		AFTERSHIP_NOT_SHIPPED_STATUSES = ImmutableList.<String>builder()
			.add("InfoReceived")
			.build(),

		AFTERSHIP_IN_TRANSIT_STATUSES = ImmutableList.<String>builder()
			.add("InTransit")
			.add("OutForDelivery")
			.add("AttemptFail")
			.build(),

		AFTERSHIP_DELIVERED_STATUSES = ImmutableList.<String>builder()
			.add("Delivered")
			.build();

	@Override
	public ShippingProviderDetectResponse detectShippingProvider(final String trackingNumber) {
		if (experiment.shouldRunExperiment("aftership")) {
			return newDetectShippingProvider(trackingNumber);
		}

		try {
			return parseCourierDetectResponse(afterShipGateway.detectCourier(trackingNumber), trackingNumber);
		} catch (Exception e) {
			LOGGER.error("[trackingNumberAdapter] Error detecting courier for tracking number: " + trackingNumber, e);
			return ShippingProviderDetectResponse.serverError();
		}
	}

	private ShippingProviderDetectResponse newDetectShippingProvider(final String trackingNumber) {
		return shipmentClient.detectShippingProviders(trackingNumber, webRequestContextProvider.getRequestContext())
			.map(convertShippingDetectResponse())
			.onErrorResumeNext(new Func1<Throwable, Observable<ShippingProviderDetectResponse>>() {
				@Override
				public Observable<ShippingProviderDetectResponse> call(Throwable throwable) {
					final ShippingProviderDetectResponse response = new ShippingProviderDetectResponse();
					response.setSuccessful(false);
					response.setResponseCode(HttpStatus.INTERNAL_SERVER_ERROR);
					return Observable.just(response);
				}
			}).toBlocking().single();
	}

	private Func1<ShippingProviders, ShippingProviderDetectResponse> convertShippingDetectResponse() {
		return new Func1<ShippingProviders, ShippingProviderDetectResponse>() {
			@Override
			public ShippingProviderDetectResponse call(final ShippingProviders shippingProviders) {
				final ShippingProviderDetectResponse response = new ShippingProviderDetectResponse();
				response.setSuccessful(true);
				response.setResponseCode(HttpStatus.OK);
				final ImmutableList.Builder<ShippingProvider> providers = ImmutableList.builder();
				for (final com.workmarket.shipment.vo.ShippingProvider provider : shippingProviders.getProviders()) {
					providers.add(ShippingProvider.getShippingProvider(provider.toString()));
				}
				response.setShippingProviders(providers.build());
				return response;
			}
		};
	}

	@Override
	public TrackingNumberResponse track(final String trackingNumber) {
		if (experiment.shouldRunExperiment("aftership")) {
			return newTrack(trackingNumber);
		}
		try {
			return parseTrackingNumberResponse(afterShipGateway.track(trackingNumber), trackingNumber);
		} catch (Exception e) {
			LOGGER.error("[trackingNumberAdapter] Error registering tracking number: " + trackingNumber, e);
			return TrackingNumberResponse.fail();
		}
	}

	private TrackingNumberResponse newTrack(final String trackingNumber) {
		return shipmentClient.startTracking(trackingNumber, webRequestContextProvider.getRequestContext())
			.map(CONVERT_TRACKING_RESPONSE)
			.onErrorResumeNext(new Func1<Throwable, Observable<TrackingNumberResponse>>() {
				@Override
				public Observable<TrackingNumberResponse> call(final Throwable throwable) {
					if (throwable instanceof ForbiddenException) {
						return Observable.just(TrackingNumberResponse.trackingExists());
					} else if (throwable instanceof NotFoundException) {
						return Observable.just(TrackingNumberResponse.notFound());
					}
					return Observable.just(TrackingNumberResponse.fail());
				}
			})
			.toBlocking().single();
	}

	@Override
	public TrackingNumberResponse get(final String trackingNumber, final String provider) {
		if (experiment.shouldRunExperiment("aftership")) {
			return newGetTracking(trackingNumber, provider);
		}
		try {
			return parseTrackingNumberResponse(afterShipGateway.get(trackingNumber, provider), trackingNumber);
		} catch (Exception e) {
			LOGGER.error(String.format("[trackingNumberAdapter] Error fetching tracking number status. Number: %s, Carrier: %s", trackingNumber, provider), e);
			return TrackingNumberResponse.fail();
		}
	}

	private TrackingNumberResponse newGetTracking(final String trackingNumber, final String provider) {
		final com.workmarket.shipment.vo.ShippingProvider shippingProvider = com.workmarket.shipment.vo.ShippingProvider.valueOf(provider);
		return shipmentClient.getTrackingInfo(trackingNumber, shippingProvider, webRequestContextProvider.getRequestContext())
			.map(CONVERT_TRACKING_RESPONSE)
			.onErrorResumeNext(new Func1<Throwable, Observable<TrackingNumberResponse>>() {
				@Override
				public Observable<TrackingNumberResponse> call(final Throwable throwable) {
					if (throwable instanceof NotFoundException) {
						return Observable.just(TrackingNumberResponse.notFound());
					}
					return Observable.just(TrackingNumberResponse.fail());
				}
			})
			.toBlocking().single();
	}

	@Override
	public TrackingStatus translateAftershipTrackingStatus(final String aftershipTrackingStatus) {
		if (aftershipTrackingStatus == null) {
			return null;
		}
		if (AFTERSHIP_PENDING_STATUSES.contains(aftershipTrackingStatus)) {
			return TrackingStatus.PENDING;
		}
		if (AFTERSHIP_NOT_SHIPPED_STATUSES.contains(aftershipTrackingStatus)) {
			return TrackingStatus.NOT_SHIPPED;
		}
		if (AFTERSHIP_IN_TRANSIT_STATUSES.contains(aftershipTrackingStatus)) {
			return TrackingStatus.IN_TRANSIT;
		}
		if (AFTERSHIP_DELIVERED_STATUSES.contains(aftershipTrackingStatus)) {
			return TrackingStatus.DELIVERED;
		}

		LOGGER.error("[trackingNumberAdapter] Unrecognized tracking status code: " + aftershipTrackingStatus);
		return TrackingStatus.NOT_AVAILABLE;
	}

	private ShippingProviderDetectResponse parseCourierDetectResponse(final JSONObject jsonResponse, final String trackingNumber) throws JSONException, UnirestException {
		final HttpStatus responseCode = HttpStatus.valueOf(jsonResponse.getInt("status"));
		final JSONObject meta = jsonResponse.getJSONObject("meta");
		final JSONObject data = jsonResponse.getJSONObject("data");

		if (!isSuccessfulResponse(responseCode)) {
			LOGGER.debug("Failed to detect courier for tracking number: " + trackingNumber +
				". Aftership message error: " + meta.getString("message"));
			if (HttpStatus.BAD_REQUEST.equals(responseCode)) {
				return ShippingProviderDetectResponse.badRequest();
			}
			return ShippingProviderDetectResponse.fail();
		}

		if (data.has("couriers")) {
			final JSONArray couriers = data.getJSONArray("couriers");
			final int numberOfCouriers = couriers.length();
			List<ShippingProvider> detectedShippingProviders = Lists.newArrayList();
			for (int ix = 0; ix < numberOfCouriers; ix++) {
				JSONObject courier = couriers.getJSONObject(ix);
				detectedShippingProviders.add(ShippingProvider.getShippingProvider(courier.getString("slug")));
			}
			return new ShippingProviderDetectResponse()
				.setSuccessful(true)
				.setResponseCode(responseCode)
				.setShippingProviders(detectedShippingProviders);
		}

		LOGGER.error("[trackingNumberAdapter] No couriers data in response");
		return ShippingProviderDetectResponse.fail();
	}

	private TrackingNumberResponse parseTrackingNumberResponse(final JSONObject jsonResponse, final String trackingNumber) throws JSONException, UnirestException {
		final HttpStatus responseCode = HttpStatus.valueOf(jsonResponse.getInt("status"));
		final JSONObject meta = jsonResponse.getJSONObject("meta");
		final JSONObject data = jsonResponse.getJSONObject("data");

		if (!isSuccessfulResponse(responseCode)) {
			LOGGER.debug("An error occurred while contacting Aftership API for tracking number: " + trackingNumber +
				". Aftership message error: " + meta.getString("message"));

			if (AfterShipError.TRACKING_EXISTS.getCode() == meta.getInt("code")) {
				return TrackingNumberResponse.trackingExists();
			}
			if (HttpStatus.BAD_REQUEST.equals(responseCode)) {
				return TrackingNumberResponse.badRequest();
			}
			if (HttpStatus.NOT_FOUND.equals(responseCode)) {
				return TrackingNumberResponse.notFound();
			}
		}

		if (data.has("tracking")) {
			final JSONObject trackingInfo = data.getJSONObject("tracking");
			final String
				responseTrackingNumber = trackingInfo.has("tracking_number") ? trackingInfo.getString("tracking_number") : null,
				deliveryStatus = trackingInfo.has("tag") ? trackingInfo.getString("tag") : null,
				provider = trackingInfo.has("slug") ? trackingInfo.getString("slug") : null;

			return TrackingNumberResponse.success()
				.setTrackingStatus(translateAftershipTrackingStatus(deliveryStatus))
				.setResponseCode(responseCode)
				.setShippingProvider(ShippingProvider.getShippingProvider(provider))
				.setTrackingNumber(responseTrackingNumber);
		}

		LOGGER.error("[trackingNumberAdapter] No tracking data in response");
		return TrackingNumberResponse.fail();
	}

	private boolean isSuccessfulResponse(final HttpStatus httpResponseCode) {
		try {
			return HttpStatus.Series.SUCCESSFUL.equals(httpResponseCode.series());
		} catch (Exception e) {
			return false;
		}
	}
}
