package com.workmarket.service.infra.http;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import org.apache.http.client.HttpClient;

public class HttpRequestDeleteCommand extends HttpRequestCommand {
	@Override
	public HttpResponse<JsonNode> execute(HttpClient httpClient) throws UnirestException {
		if (httpClient != null) {
			Unirest.setHttpClient(httpClient);
		}
		return Unirest.delete(getPath()).asJson();
	}
}
