package com.workmarket.service.external;

import com.mashape.unirest.http.exceptions.UnirestException;
import com.workmarket.domains.work.model.part.ShippingProvider;
import org.json.JSONException;
import org.json.JSONObject;

public class AfterShipGatewayImplMock implements AfterShipGateway {

	public static final ShippingProvider SHIPPING_PROVIDER =  ShippingProvider.UPS;
	public static final String
		VALID_TRACKING_NUMBER = "1Z45783E0398560742",
		DELIVERED_STATUS = "Delivered",
		NOT_SHIPPED_STATUS = "InfoReceived",
		SHIPPING_PROVIDER_CODE = "ups",
		BAD_REQUEST_RESPONSE_JSON = "{\"status\": 400,\"meta\":{\"code\":4005,\"message\":\"The value of `tracking_number` is invalid.\",\"type\":\"BadRequest\"},\"data\":{}}",
		NOT_FOUND_RESPONSE_JSON = "{\"status\": 404,\"meta\":{\"code\":4004,\"message\":\"Tracking does not exist.\",\"type\":\"NotFound\"},\"data\":{}}",
		SUCCESSFUL_RESPONSE_JSON =
			"{" +
				"	\"status\": 200," +
				"	\"meta\": {" +
				"		\"code\": 200" +
				"	}," +
				"	\"data\": {" +
				"		\"tracking\": {" +
				"			\"tracking_number\": \"%s\"," +
				"			\"slug\": \"%s\"," +
				"			\"tag\": \"%s\"," +
				"		}" +
				"	}" +
				"}",
		SUCCESSFUL_DETECT_COURIER_RESPONSE =
			"{" +
				"\"status\": 200," +
				"\"meta\":{" +
					"\"code\":200" +
				"}," +
				"\"data\":{" +
					"\"total\":1," +
					"\"couriers\":[{" +
								"\"slug\":\"usps\"," +
								"\"name\":\"USPS\"," +
								"\"phone\":\"+1 800-275-8777\"," +
								"\"other_name\":\"United States Postal Service\"," +
								"\"web_url\":\"https://www.usps.com\"," +
								"\"required_fields\":[]," +
								"\"default_language\":null," +
								"\"support_languages\":null" +
							"}]" +
				"}" +
			"}";

	@Override
	public JSONObject detectCourier(final String trackingNumber) throws JSONException, UnirestException {
		return new JSONObject(String.format(SUCCESSFUL_DETECT_COURIER_RESPONSE));
	}

	@Override
	public JSONObject track(final String trackingNumber) throws JSONException, UnirestException {
		return buildSuccessfulJsonResponse(trackingNumber, SHIPPING_PROVIDER_CODE, NOT_SHIPPED_STATUS);
	}

	@Override
	public JSONObject get(final String trackingNumber, final String provider) throws JSONException, UnirestException {
		return buildSuccessfulJsonResponse(trackingNumber, provider, DELIVERED_STATUS);
	}

	@Override
	public JSONObject delete(final String trackingNumber, final String provider) throws JSONException, UnirestException {
		return buildSuccessfulJsonResponse(trackingNumber, provider, DELIVERED_STATUS);
	}

	public static JSONObject buildSuccessfulJsonResponse(String trackingNumber, String shippingProvider, String deliveryStatus) throws JSONException {
		return new JSONObject(String.format(SUCCESSFUL_RESPONSE_JSON, trackingNumber, shippingProvider, deliveryStatus));
	}
}
