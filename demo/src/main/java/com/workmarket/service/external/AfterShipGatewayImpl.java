package com.workmarket.service.external;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import com.mashape.unirest.request.BaseRequest;
import com.mashape.unirest.request.HttpRequest;
import com.mashape.unirest.request.HttpRequestWithBody;
import com.mashape.unirest.request.body.RequestBodyEntity;
import com.typesafe.config.Config;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AfterShipGatewayImpl implements AfterShipGateway {

	private final String aftershipUrl;
	private final String aftershipKey;

	private static String
		POST_URL,
		GET_URL,
		COURIER_DETECT_URL;

	private static final String POST_BODY = "{ tracking : { tracking_number : '%s' } }";

	@Autowired
	public AfterShipGatewayImpl(final Config configuration) {
		aftershipUrl = configuration.getString("aftership.url");
		aftershipKey = configuration.getString("aftership.key");
	}

	@Override
	public JSONObject detectCourier(final String trackingNumber) throws JSONException, UnirestException {
		return getResponse(
			addTrackingNumberToRequestBody(
				(HttpRequestWithBody) addHeaders(Unirest.post(getCourierDetectURL())),
				trackingNumber
			)
		);
	}

	@Override
	public JSONObject track(final String trackingNumber) throws JSONException, UnirestException {
		return getResponse(
			addTrackingNumberToRequestBody(
				(HttpRequestWithBody) addHeaders(Unirest.post(getPostURL())),
				trackingNumber
			)
		);
	}

	@Override
	public JSONObject get(final String trackingNumber, final String provider) throws JSONException, UnirestException {
		return getResponse(
			addHeaders(
				Unirest.get(String.format(getGetURL(), provider, trackingNumber))
			)
		);
	}

	@Override
	public JSONObject delete(final String trackingNumber, final String provider) throws JSONException, UnirestException {
		return getResponse(
			addHeaders(
				Unirest.delete(String.format(getGetURL(), provider, trackingNumber))
			)
		);
	}

	private HttpRequest addHeaders(HttpRequest httpRequest) {
		return httpRequest
			.header("aftership-api-key", aftershipKey)
			.header("accept", "application/json");
	}

	private RequestBodyEntity addTrackingNumberToRequestBody(HttpRequestWithBody httpRequestWithBody, final String trackingNumber) {
		return httpRequestWithBody.body(new JsonNode(String.format(POST_BODY, trackingNumber)));
	}

	private String getCourierDetectURL() {
		if (COURIER_DETECT_URL == null) {
			COURIER_DETECT_URL = aftershipUrl + "/couriers/detect";
		}
		return COURIER_DETECT_URL;
	}

	private String getPostURL() {
		if (POST_URL == null) {
			POST_URL = aftershipUrl + "/trackings";
		}
		return POST_URL;
	}

	private String getGetURL() {
		if (GET_URL == null) {
			GET_URL = getPostURL() + "/%s/%s";
		}
		return GET_URL;
	}

	private JSONObject getResponse(final BaseRequest request) throws JSONException, UnirestException {
		HttpResponse<JsonNode> response = request.asJson();
		return response.getBody().getObject().put("status", response.getStatus());
	}
}
