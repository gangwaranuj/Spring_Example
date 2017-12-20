package com.workmarket.vault.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.workmarket.common.exceptions.ServiceUnavailableException;
import com.workmarket.service.infra.http.HttpClientService;
import com.workmarket.service.infra.http.HttpRequestCommandFactory;
import com.workmarket.service.infra.http.HttpRequestDeleteCommand;
import com.workmarket.service.infra.http.HttpRequestGetCommand;
import com.workmarket.service.infra.http.HttpRequestPostCommand;
import com.workmarket.service.infra.http.server.LoadBalanceStrategy;
import com.workmarket.vault.models.VaultKeyValuePair;
import org.apache.http.client.HttpClient;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class VaultServerServiceImplTest {
	private static final String HOST1 = "https://host1.wm.com";

	@Mock LoadBalanceStrategy loadBalanceStrategy;
	@Mock HttpRequestCommandFactory httpRequestCommandFactory;
	@Mock private HttpClientService httpClientService;
	@InjectMocks private VaultServerServiceImpl vaultService = spy(new VaultServerServiceImpl());

	@Mock HttpRequestDeleteCommand deleteCommand;
	@Mock HttpRequestGetCommand getCommand;
	@Mock HttpRequestPostCommand postCommand;
	@Mock HttpClient httpClient;
	@Mock HttpResponse<JsonNode> response;

	VaultKeyValuePair pair;
	List<VaultKeyValuePair> pairs;
	HttpResponse<JsonNode> getResponse;
	InputStream getResponseStream;
	JsonNode jsonNode;

	@Before
	public void setUp() throws Exception {
		pair = new VaultKeyValuePair("key", "value");
		pairs = new ArrayList<>();
		pairs.add(pair);

		when(response.getStatus()).thenReturn(200);

		when(deleteCommand.execute(null)).thenReturn(response);
		when(deleteCommand.setUrl(anyString())).thenReturn(deleteCommand);
		when(deleteCommand.setBody(anyString())).thenReturn(deleteCommand);
		when(httpRequestCommandFactory.createDeleteCommand()).thenReturn(deleteCommand);

		getResponse = mock(HttpResponse.class);
		getResponseStream = mock(ByteArrayInputStream.class);
		when(getResponseStream.toString()).thenReturn("{\"meta\":{},\"results\":[{\"id\":\"key\",\"value\":\"value\"}]}");
		when(getResponse.getStatus()).thenReturn(200);
		when(getResponse.getRawBody()).thenReturn(getResponseStream);

		jsonNode = mock(JsonNode.class);
		JSONObject jsonObject = mock(JSONObject.class);
		JSONArray jsonArray = mock(JSONArray.class);
		JSONObject pairAsObject = mock(JSONObject.class);
		when(jsonNode.getObject()).thenReturn(jsonObject);
		when(jsonObject.get("results")).thenReturn(jsonArray);
		when(jsonArray.length()).thenReturn(1);
		when(jsonArray.get(0)).thenReturn(pairAsObject);
		when(pairAsObject.get("id")).thenReturn("key");
		when(pairAsObject.get("value")).thenReturn("value");
		when(getResponse.getBody()).thenReturn(jsonNode);

		when(getCommand.execute(null)).thenReturn(getResponse);
		when(getCommand.setUrl(anyString())).thenReturn(getCommand);
		when(getCommand.setBody(anyString())).thenReturn(getCommand);
		when(httpRequestCommandFactory.createGetCommand()).thenReturn(getCommand);

		when(postCommand.execute(null)).thenReturn(response);
		when(postCommand.setUrl(anyString())).thenReturn(postCommand);
		when(postCommand.setBody(anyString())).thenReturn(postCommand);
		when(httpRequestCommandFactory.createPostCommand()).thenReturn(postCommand);

		when(loadBalanceStrategy.getNextHost()).thenReturn(HOST1);
		when(loadBalanceStrategy.getNumHosts()).thenReturn(1);

		when(httpClientService.getClient()).thenReturn(null);
	}

	@Test(expected = ServiceUnavailableException.class)
	public void shouldThrowExceptionIfNoHosts() throws Exception {
		when(loadBalanceStrategy.getNumHosts()).thenReturn(0);
		vaultService.remove("key");
	}

	@Test
	public void shouldExecuteDeleteCommand() throws Exception {
		vaultService.remove("key");
		verify(deleteCommand).execute(null);
	}

	@Test
	public void shouldRemoveCommandOnSetOfKeys() throws Exception {
		vaultService.remove("key");
		verify(deleteCommand).setUrl(HOST1 + "/key");
	}

	@Test
	public void shouldPostCommand() throws Exception {
		vaultService.post(pair);
		verify(postCommand).execute(null);
	}

	@Test
	public void shouldPostCommandWithJsonBody() throws Exception {
		ObjectMapper mapper = new ObjectMapper();
		String json = mapper.writeValueAsString(pair);

		vaultService.post(pair);
		verify(postCommand).setBody(json);
	}

	@Test
	public void shouldPostCommandWithJsonBodyWithMultipleKeys() throws Exception {
		ObjectMapper mapper = new ObjectMapper();
		String json = mapper.writeValueAsString(pairs);

		vaultService.post(pairs);
		verify(postCommand).setBody(json);
	}

	@Test
	public void shouldGetCommand() throws Exception {
		vaultService.get("key");
		verify(getCommand).execute(null);
	}

	@Test
	public void shouldGetCommandResponse() throws Exception {
		ObjectMapper mapper = new ObjectMapper();
		String json = mapper.writeValueAsString(pair);
		when(getResponseStream.toString()).thenReturn("{\"meta\":{},\"results\":[" + json + "]}");
		VaultKeyValuePair responsePair = vaultService.get("key");
		assertEquals(pair, responsePair);
	}

	@Test
	public void shouldGetCommandEmptyResponseIfNon200Response() throws Exception {
		when(getResponse.getStatus()).thenReturn(400);
		VaultKeyValuePair responsePair = vaultService.get("key");
		assertTrue(responsePair.isEmpty());
	}
}