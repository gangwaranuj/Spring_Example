package com.workmarket.integration.webhook;

import org.springframework.http.ResponseEntity;

import java.net.URI;
import java.util.Map;

/**
 * Created by nick on 5/29/13 9:32 AM
 */
public interface RestClient {

	ResponseEntity<String> httpGet(URI uri, Map<String, String> headers);

	ResponseEntity<String> httpPost(URI uri, String body, Map<String, String> headers);

	ResponseEntity<String> httpPatch(URI uri, String body, Map<String, String> headers);

	ResponseEntity<String> httpPut(URI uri, String body, Map<String, String> headers);

	ResponseEntity<String> httpDelete(URI uri, String body, Map<String, String> headers);
}
