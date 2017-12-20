package com.workmarket.service.infra.http;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import org.apache.http.client.HttpClient;

public class HttpRequestPostCommand extends HttpRequestCommand {
	@Override
	public HttpResponse<JsonNode> execute(HttpClient httpClient) throws UnirestException {
		if (httpClient != null) {
			Unirest.setHttpClient(httpClient);
		}
		return Unirest
				.post(getPath())
				.headers(getHeaders())
				.body(getBody())
				.asJson();
	}
}
