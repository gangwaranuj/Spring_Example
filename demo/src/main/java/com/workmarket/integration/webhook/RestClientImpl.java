package com.workmarket.integration.webhook;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.util.Map;

import static java.lang.String.format;

@Component
public class RestClientImpl implements RestClient {
	private static final Logger logger = LoggerFactory.getLogger(RestClientImpl.class);

	private WMRestTemplate restTemplate = new WMRestTemplate();

	@Override
	public ResponseEntity<String> httpGet(URI uri, Map<String, String> headers) {

		if (uri == null) {
			return new ResponseEntity<String>(HttpStatus.BAD_REQUEST);
		}

		RestTemplate template = new RestTemplate();
		HttpHeaders requestHeaders = new HttpHeaders();
		requestHeaders.setAll(headers);

		HttpEntity<String> requestEntity = new HttpEntity<String>(requestHeaders);

		try {
			return template.exchange(uri, HttpMethod.GET, requestEntity, String.class);
		} catch (HttpStatusCodeException ex) {
			logger.error(format("Error doing GET request to the uri: %s, status code %s, message %s", uri, ex.getStatusCode(), ex.getStatusCode()), ex);
			return new ResponseEntity<String>(ex.getResponseBodyAsString(), ex.getResponseHeaders(), ex.getStatusCode());
		} catch (RestClientException ex) {
			logger.error("General Error doing GET request to the uri: " + uri + ", message: " + ex.getMessage(), ex);
			return new ResponseEntity<String>(HttpStatus.BAD_REQUEST);
		}
	}

	@Override
	public ResponseEntity<String> httpPost(URI uri, String body, Map<String, String> headers) {

		if (logger.isDebugEnabled()) {
			logger.debug(format("processing post request to [%s], body: [%s], headers: [%s]", uri, body, headers));
		}

		HttpHeaders requestHeaders = new HttpHeaders();

		for (Map.Entry<String, String> entry : headers.entrySet()) {
			requestHeaders.set(entry.getKey(), entry.getValue());
		}

		HttpEntity<String> requestEntity = new HttpEntity<String>(body, requestHeaders);

		try {
			return restTemplate.exchange(uri, HttpMethod.POST, requestEntity, String.class);
		} catch (HttpStatusCodeException ex) {
			logger.error(format("Error doing POST request to the uri: %s, status code %s, message %s", uri, ex.getStatusCode(), ex.getStatusCode()), ex);
			return new ResponseEntity<String>(ex.getResponseBodyAsString(), ex.getResponseHeaders(), ex.getStatusCode());
		} catch (RestClientException ex) {
				logger.error("General Error doing POST request to the uri: " + uri + ", message: " + ex.getMessage(), ex);
			return new ResponseEntity<String>(HttpStatus.BAD_REQUEST);
		}
	}

	@Override
	public ResponseEntity<String> httpPatch(URI uri, String body, Map<String, String> headers) {
		HttpHeaders requestHeaders = new HttpHeaders();

		for (Map.Entry<String, String> entry : headers.entrySet()) {
			requestHeaders.set(entry.getKey(), entry.getValue());
		}

		HttpEntity<String> requestEntity = new HttpEntity<String>(body, requestHeaders);

		try {
			// TODO: get rid of this work around after upgrading to Spring 3.2
			// Due to HttpMethod.PATCH not being implemented in the current version of spring
			// we override the TRACE method to implement PATCH
			return restTemplate.exchange(uri, HttpMethod.TRACE, requestEntity, String.class);
		} catch (HttpStatusCodeException ex) {
			logger.debug("Error doing PATCH request to the uri: " + uri);
			return new ResponseEntity<String>(ex.getStatusCode());
		} catch (RestClientException ex) {
			logger.debug("Error doing POST request to the uri: " + uri);
			return new ResponseEntity<String>(HttpStatus.BAD_REQUEST);
		}
	}

	@Override
	public ResponseEntity<String> httpPut(URI uri, String body, Map<String, String> headers) {
		HttpHeaders requestHeaders = new HttpHeaders();

		for (Map.Entry<String, String> entry : headers.entrySet()) {
			requestHeaders.set(entry.getKey(), entry.getValue());
		}

		HttpEntity<String> requestEntity = new HttpEntity<String>(body, requestHeaders);

		try {
			return restTemplate.exchange(uri, HttpMethod.PUT, requestEntity, String.class);
		} catch (HttpStatusCodeException ex) {
			logger.debug("Error doing PUT request to the uri: " + uri);
			return new ResponseEntity<String>(ex.getStatusCode());
		} catch (RestClientException ex) {
			logger.debug("Error doing POST request to the uri: " + uri);
			return new ResponseEntity<String>(HttpStatus.BAD_REQUEST);
		}
	}

	@Override
	public ResponseEntity<String> httpDelete(URI uri, String body, Map<String, String> headers) {
		HttpHeaders requestHeaders = new HttpHeaders();

		for (Map.Entry<String, String> entry : headers.entrySet()) {
			requestHeaders.set(entry.getKey(), entry.getValue());
		}

		HttpEntity<String> requestEntity = new HttpEntity<String>(body, requestHeaders);

		try {
			return restTemplate.exchange(uri, HttpMethod.DELETE, requestEntity, String.class);
		} catch (HttpStatusCodeException ex) {
			logger.debug("Error doing DELETE request to the uri: " + uri);
			return new ResponseEntity<String>(ex.getStatusCode());
		} catch (RestClientException ex) {
			logger.debug("Error doing POST request to the uri: " + uri);
			return new ResponseEntity<String>(HttpStatus.BAD_REQUEST);
		}
	}
}