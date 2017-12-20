package com.workmarket.service.external;

import com.mashape.unirest.http.exceptions.UnirestException;
import org.json.JSONException;
import org.json.JSONObject;

public interface AfterShipGateway {

	JSONObject detectCourier(final String trackingNumber) throws JSONException, UnirestException;

	JSONObject track(final String trackingNumber) throws JSONException, UnirestException;

	JSONObject get(final String trackingNumber, final String provider) throws JSONException, UnirestException;

	JSONObject delete(final String trackingNumber, final String provider) throws JSONException, UnirestException;

}
