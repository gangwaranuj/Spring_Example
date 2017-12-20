package com.workmarket.vault.services;

import com.google.common.base.Joiner;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.workmarket.common.exceptions.BadRequestException;
import com.workmarket.common.exceptions.ServiceUnavailableException;
import com.workmarket.service.infra.http.HttpClientService;
import com.workmarket.service.infra.http.HttpRequestCommand;
import com.workmarket.service.infra.http.HttpRequestCommandFactory;
import com.workmarket.service.infra.http.server.Host;
import com.workmarket.service.infra.http.server.LoadBalanceStrategy;
import com.workmarket.vault.models.VaultKeyValuePair;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.impl.client.CloseableHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class VaultServerServiceImpl implements VaultServerService {
	private static final Log logger = LogFactory.getLog(VaultServerServiceImpl.class);

	@Autowired private LoadBalanceStrategy loadBalanceStrategy;
	@Autowired private HttpRequestCommandFactory httpRequestCommandFactory;
	@Autowired private HttpClientService httpClientService;


	@Override
	public void remove(String key) throws ServiceUnavailableException {
		execute(getPathForKey(key), null, httpRequestCommandFactory.createDeleteCommand());
	}

	@Override
	public void remove(List<VaultKeyValuePair> pairs) throws ServiceUnavailableException {
		for (VaultKeyValuePair pair : pairs) {
			remove(pair.getId());
		}
	}

	@Override
	public void post(VaultKeyValuePair pair) throws ServiceUnavailableException, BadRequestException {
		String body;

		try {
			ObjectMapper mapper = new ObjectMapper();
			body = mapper.writeValueAsString(pair);
		} catch (JsonProcessingException e) {
			logger.error("Error deserializing post request", e);
			throw new BadRequestException("Could not parse body");
		}

		execute("/", body, httpRequestCommandFactory.createPostCommand());
	}

	@Override
	public void post(List<VaultKeyValuePair> pairs) throws ServiceUnavailableException, BadRequestException {
		if (CollectionUtils.isEmpty(pairs)) {
			return;
		}

		String body;

		try {
			ObjectMapper mapper = new ObjectMapper();
			body = mapper.writeValueAsString(pairs);
		} catch (JsonProcessingException e) {
			logger.error("Error deserializing post request", e);
			throw new BadRequestException("Could not parse body");
		}

		HttpResponse<JsonNode> response = execute("/", body, httpRequestCommandFactory.createPostCommand());

		if (response.getStatus() == 500) {
			throw new ServiceUnavailableException("Error posting vault values " + response.getBody());
		} else if (response.getStatus() >= 400) {
			throw new BadRequestException("Bad request posting vault values " + response.getBody());
		}

		logger.info("Succesfully committed to Vault");
	}

	@Override
	public VaultKeyValuePair get(String key) throws ServiceUnavailableException {
		List<VaultKeyValuePair> result = get(com.google.common.collect.Lists.newArrayList(key));

		if (CollectionUtils.isNotEmpty(result)) {
			return result.get(0);
		}

		return new VaultKeyValuePair(key, "");
	}

	@Override
	public List<VaultKeyValuePair> get(List<String> keys) throws ServiceUnavailableException {
		HttpResponse<JsonNode> response =  execute(getPathForKeys(keys), null,
			httpRequestCommandFactory.createGetCommand());

		List<VaultKeyValuePair> results = new ArrayList<>();

		if (response.getStatus() == HttpStatus.SC_OK) {
			try {
				JSONArray array = (JSONArray) response.getBody().getObject().get("results");
				// Vault will return values of null for ids not found
				for (int i = 0; i < array.length(); i++) {
					JSONObject pairAsObject = (JSONObject) array.get(i);
					String id = (String) pairAsObject.get("id");
					String value = pairAsObject.get("value").equals(null) ? "" : (String) pairAsObject.get("value");
					results.add(new VaultKeyValuePair(id, StringUtils.isEmpty(value) ? "" : value));
				}
				return results;
			} catch (JSONException e) {
				results.clear();
				logger.error("Could not deserialize object");
			}
		}

		// initialize empty
		for (String k : keys) {
			results.add(new VaultKeyValuePair());
		}

		return results;
	}

	private String getPathForKey(String key) {
		return "/" + key;
	}

	private String getPathForKeys(List<String> keys) {
		return "/" + Joiner.on(",").join(keys);
	}

	private HttpResponse<JsonNode> execute(String path, String body, HttpRequestCommand command) throws ServiceUnavailableException {
		int numHosts = loadBalanceStrategy.getNumHosts();

		for (int i = 0; i < numHosts; i++) {
			String host = loadBalanceStrategy.getNextHost();
			try {
				CloseableHttpClient httpClient = httpClientService.getClient();
				return command.setUrl(host + path).setBody(body).execute(httpClient);
			} catch (Exception e) {
				logger.error("Error fetching host", e);
				loadBalanceStrategy.incrementFailure(new Host(host));
			}
		}

		throw new ServiceUnavailableException("");
	}
}
