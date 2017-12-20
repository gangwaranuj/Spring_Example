package com.workmarket.service.business.integration.hooks.webhook;

import com.google.common.base.Optional;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Sets;

import com.codahale.metrics.Meter;
import com.codahale.metrics.MetricRegistry;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.workmarket.domains.model.Company;
import com.workmarket.domains.model.User;
import com.workmarket.domains.model.customfield.SavedWorkCustomField;
import com.workmarket.domains.model.customfield.WorkCustomField;
import com.workmarket.domains.model.customfield.WorkCustomFieldGroupAssociation;
import com.workmarket.domains.model.integration.IntegrationEventType;
import com.workmarket.domains.model.integration.webhook.AbstractWebHookClient;
import com.workmarket.domains.model.integration.webhook.WebHook;
import com.workmarket.domains.model.integration.webhook.WebHookHeader;
import com.workmarket.domains.work.model.Work;
import com.workmarket.domains.work.service.WorkService;
import com.workmarket.feature.gen.Messages.FeatureToggle;
import com.workmarket.feature.gen.Messages.Status;
import com.workmarket.feature.vo.FeatureToggleAndStatus;
import com.workmarket.helpers.WMCallable;
import com.workmarket.integration.webhook.RestClient;
import com.workmarket.service.business.CustomFieldService;
import com.workmarket.service.business.dto.integration.ParsedWebHookDTO;
import com.workmarket.service.featuretoggle.FeatureEntitlementService;
import com.workmarket.service.infra.security.SecurityContext;
import com.workmarket.service.web.WebRequestContext;
import com.workmarket.service.web.WebRequestContextProvider;

import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.fluent.Request;
import org.apache.http.client.fluent.Response;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.http.HttpStatus;
import org.springframework.util.Assert;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLDecoder;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashSet;
import java.util.Map;
import java.util.TimeZone;

import rx.Observable;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doCallRealMethod;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Created by nick on 5/29/13 9:27 AM
 */
@RunWith(MockitoJUnitRunner.class)
public class WebHookHTTPPoolingFactoryImplTest {

	@Mock RestClient restClient;
	@Mock CustomFieldService customFieldService;
	@Mock WorkService workService;
	@Mock private MetricRegistry metricRegistry;
	@Mock SecurityContext securityContext;
	@Mock private WebHookIntegrationService webHookIntegrationService;
	@Mock FeatureEntitlementService featureEntitlementService;
	@InjectMocks WebHookHTTPPoolingFactoryImpl webHookHTTPPoolingFactory = spy(new WebHookHTTPPoolingFactoryImpl());

	@Mock Work work;
	@Mock WebHook webHook;
	@Mock Map contextVariables;
	@Mock Request request;
	@Mock Response response;
	@Mock HttpResponse httpResponse;
	@Mock StatusLine statusLine;
	@Mock User buyerSupportUser;

	@Mock
	WebRequestContextProvider webRequestContextProvider;

	@Before
	public void setup() throws IOException {
		when(webRequestContextProvider.getWebRequestContext()).thenCallRealMethod();
		when(webRequestContextProvider.getWebRequestContext(any(String.class), any(String.class))).thenCallRealMethod();
		doCallRealMethod().when(webRequestContextProvider).setWebRequestContext(any(WebRequestContext.class));

		when(workService.findActiveWorkResource(anyLong())).thenReturn(null);

		try {
			when(webHook.getUrl()).thenReturn(new URL("http://www.workmarket.com"));
		} catch (MalformedURLException e) {
			// should not happen
		}

		when(webHook.getWebHookHeaders()).thenReturn(new HashSet<WebHookHeader>());
		when(webHook.getContentType()).thenReturn(WebHook.ContentType.JSON);

		when(metricRegistry.meter(anyString())).thenReturn(new Meter());
		webHookHTTPPoolingFactory.init();
		when(webHook.getMethodType()).thenReturn(WebHook.MethodType.POST);
		when(webHook.getIntegrationEventType()).thenReturn(IntegrationEventType.newInstance("some-code"));
		when(statusLine.getStatusCode()).thenReturn(200);
		when(httpResponse.getStatusLine()).thenReturn(statusLine);
		when(response.returnResponse()).thenReturn(httpResponse);
		when(request.execute()).thenReturn(response);
		doReturn(request)
			.when(webHookHTTPPoolingFactory).buildHttpRequest(any(WebHook.MethodType.class), any(ParsedWebHookDTO.class));
		when(securityContext.getCurrentUserCompanyId()).thenReturn(1L);

		when(buyerSupportUser.getFirstName()).thenReturn("Bob");
		when(buyerSupportUser.getLastName()).thenReturn("Smith");
		when(buyerSupportUser.getEmail()).thenReturn("bob@smith.com");
		when(work.getBuyerSupportUser()).thenReturn(buyerSupportUser);

		when(webHookIntegrationService.getWebhookClientCompanyId(any(Long.class))).thenReturn(Optional.of(222L));

		when(featureEntitlementService.getFeatureToggleForCurrentUser(any(String.class))).thenReturn(
				Observable.just(new FeatureToggleAndStatus(Status.getDefaultInstance(), FeatureToggle.getDefaultInstance())));
	}

	@Test
	public void buildBody_GivenAssignmentId_ExpectWorkNumber() throws Exception {
		when(work.getWorkNumber()).thenReturn("52913");

		when(webHook.getBody()).thenReturn("assignment_id=${assignment_id}");
		when(webHook.getIntegrationEventType()).thenReturn(IntegrationEventType.newInstance(IntegrationEventType.WORK_ACCEPT));

		Optional<ParsedWebHookDTO> parsedWebHookDTO = webHookHTTPPoolingFactory.buildHook(work, webHook, contextVariables);
		Assert.isTrue(parsedWebHookDTO.get().getBody().contains("52913"));
	}

	@Test
	public void buildBody_GivenCustomField_ExpectCustomFieldValue() throws Exception {
		WorkCustomFieldGroupAssociation customFieldGroupAssociation = mock(WorkCustomFieldGroupAssociation.class);

		SavedWorkCustomField savedWorkCustomField = mock(SavedWorkCustomField.class);
		when(savedWorkCustomField.getValue()).thenReturn("blah");

		WorkCustomField workCustomField = mock(WorkCustomField.class);
		when(workCustomField.getId()).thenReturn(5L);

		when(savedWorkCustomField.getWorkCustomField()).thenReturn(workCustomField);

		when(customFieldGroupAssociation.getSavedWorkCustomFields()).thenReturn(Sets.newHashSet(savedWorkCustomField));

		when(customFieldService.findAllByWork(anyLong())).thenReturn(Sets.newHashSet(customFieldGroupAssociation));

		when(webHook.getBody()).thenReturn("my_custom_field=${custom_field_5}");
		when(webHook.getIntegrationEventType()).thenReturn(IntegrationEventType.newInstance(IntegrationEventType.WORK_ACCEPT));

		Optional<ParsedWebHookDTO> parsedWebHookDTO = webHookHTTPPoolingFactory.buildHook(work, webHook, contextVariables);

		Assert.isTrue(parsedWebHookDTO.get().getBody().contains("blah"));
	}

	@Test
	public void buildBody_GivenISO8601DateFormat_ExpectISO8601Time() throws Exception {
		Calendar calendar = new GregorianCalendar(2012, 11, 20, 12, 0, 0);
		calendar.setTimeZone(TimeZone.getTimeZone("GMT"));

		when(work.getScheduleFrom()).thenReturn(calendar);

		when(webHook.getBody()).thenReturn("end_of_world='${start_date_time}'");
		when(webHook.getIntegrationEventType()).thenReturn(IntegrationEventType.newInstance(IntegrationEventType
				.WORK_ACCEPT));

		AbstractWebHookClient abstractWebHookClient = new AbstractWebHookClient();
		abstractWebHookClient.setDateFormat(AbstractWebHookClient.DateFormat.ISO_8601);
		when(webHook.getWebHookClient()).thenReturn(abstractWebHookClient);

		Optional<ParsedWebHookDTO> parsedWebHookDTO = webHookHTTPPoolingFactory.buildHook(work, webHook, contextVariables);

		Assert.isTrue(parsedWebHookDTO.get().getBody().contains("2012-12-20T12:00:00.000Z"));
	}

	@Test
	public void buildBody_GivenUnixDateFormat_ExpectUnixTime() throws Exception {
		Calendar calendar = new GregorianCalendar(2012, 11, 20, 12, 0, 0);
		calendar.setTimeZone(TimeZone.getTimeZone("GMT"));

		when(work.getScheduleFrom()).thenReturn(calendar);

		when(webHook.getBody()).thenReturn("end_of_world='${start_date_time}'");
		when(webHook.getIntegrationEventType()).thenReturn(IntegrationEventType.newInstance(IntegrationEventType.WORK_ACCEPT));

		AbstractWebHookClient abstractWebHookClient = new AbstractWebHookClient();
		abstractWebHookClient.setDateFormat(AbstractWebHookClient.DateFormat.UNIX);
		when(webHook.getWebHookClient()).thenReturn(abstractWebHookClient);

		Optional<ParsedWebHookDTO> parsedWebHookDTO = webHookHTTPPoolingFactory.buildHook(work, webHook, contextVariables);

		Assert.isTrue(parsedWebHookDTO.get().getBody().contains("1356004800"));
	}

	@Test
	public void buildBody_GivenJSON_ExpectValidJSON() throws Exception {
		String ascii = "";

		for (int i = 0; i < 256; i++) {
			ascii += (char) i;
		}

		when(work.getWorkNumber()).thenReturn(ascii);

		String body = "{" +
			"\"blah\": \"${assignment_id}\"" +
			"}";

		when(webHook.getBody()).thenReturn(body);
		when(webHook.getIntegrationEventType()).thenReturn(IntegrationEventType.newInstance(IntegrationEventType.WORK_ACCEPT));

		Optional<ParsedWebHookDTO> parsedWebHookDTO = webHookHTTPPoolingFactory.buildHook(work, webHook, contextVariables);

		JSONObject result = new JSONObject(parsedWebHookDTO.get().getBody());
		Assert.isTrue(ascii.equals(result.get("blah")));
	}

	@Test
	public void buildBody_GivenFormEncoded_ExpectValidFormEncoding() throws Exception {
		String ascii = "";

		for (int i = 0; i < 256; i++) {
			ascii += (char) i;
		}

		when(work.getWorkNumber()).thenReturn(ascii);

		String body = "blah=${assignment_id}";

		when(webHook.getContentType()).thenReturn(WebHook.ContentType.FORM_ENCODED);
		when(webHook.getBody()).thenReturn(body);
		when(webHook.getIntegrationEventType()).thenReturn(IntegrationEventType.newInstance(IntegrationEventType.WORK_ACCEPT));

		Optional<ParsedWebHookDTO> parsedWebHookDTO = webHookHTTPPoolingFactory.buildHook(work, webHook, contextVariables);

		// a properly escaped form encoding should go on a URL
		try {
			URL url = new URL("http://www.blah.com?" + parsedWebHookDTO.get().getBody());
		} catch (MalformedURLException e) {
			Assert.isTrue(false, "Invalid form encoding");
		}

		Assert.isTrue(URLDecoder.decode(parsedWebHookDTO.get().getBody(), "UTF-8").contains(ascii));
	}

	@Test
	public void buildURL_GivenSpecialChars_ExpectValidURL() throws Exception {
		String ascii = "";

		for (int i = 0; i < 256; i++) {
			ascii += (char) i;
		}

		when(work.getWorkNumber()).thenReturn(ascii);

		when(webHook.getUrl()).thenReturn(new URL("http://www.blah.com?blah=${assignment_id}"));

		when(webHook.getBody()).thenReturn("");
		when(webHook.getIntegrationEventType()).thenReturn(IntegrationEventType.newInstance(IntegrationEventType.WORK_ACCEPT));

		Optional<ParsedWebHookDTO> parsedWebHookDTO = webHookHTTPPoolingFactory.buildHook(work, webHook, contextVariables);

		Assert.isTrue(parsedWebHookDTO.isPresent());
		Assert.isTrue(parsedWebHookDTO.get().getUri().toString().startsWith("http://www.blah.com?blah="));
	}

	@Test
	public void shouldReturnStatusCode200() throws IOException {
		final HttpStatus status = webHookHTTPPoolingFactory.launchHook(webHook, new ParsedWebHookDTO());

		assertEquals(200, status.value());
	}

	@Test
	public void shouldReturn500OnException() throws IOException {
		doThrow(new IOException("uh oh")).when(request).execute();

		final HttpStatus status = webHookHTTPPoolingFactory.launchHook(webHook, new ParsedWebHookDTO());

		assertEquals(HttpStatus.INTERNAL_SERVER_ERROR.value(), status.value());
	}

	@Test
	public void shouldReturnMethodNotAllowedOnUnsupportedMethod() throws IOException {
		doReturn(null)
			.when(webHookHTTPPoolingFactory).buildHttpRequest(any(WebHook.MethodType.class), any(ParsedWebHookDTO.class));

		final HttpStatus status = webHookHTTPPoolingFactory.launchHook(webHook, new ParsedWebHookDTO());

		assertEquals(HttpStatus.METHOD_NOT_ALLOWED.value(), status.value());
	}

	@Test
	public void shouldTimeoutAndReturnRequestTimeoutStatusCodeAndAbort() {
		final Request request = mock(Request.class);
		final HttpStatus status = webHookHTTPPoolingFactory.runHystrixCommandAndReturnStatus(
				request,
				webHook,
				1000,
				new WMCallable<HttpResponse>(webRequestContextProvider) {
					@Override
					public HttpResponse apply() throws Exception {
						Thread.sleep(2500);
						return null;
					}
				});

		assertEquals(HttpStatus.REQUEST_TIMEOUT.value(), status.value());
		verify(request).abort();
	}

	@Test
	public void shouldReturnPostRequest() throws URISyntaxException {
		reset(webHookHTTPPoolingFactory);
		final ParsedWebHookDTO parsedWebHookDTO = new ParsedWebHookDTO();
		parsedWebHookDTO.setUri(new URI("http://www.google.com"));
		parsedWebHookDTO.setBody("{}");
		parsedWebHookDTO.setHeaders(ImmutableMap.of("Content-Type", "application/json"));
		final Request request = webHookHTTPPoolingFactory.buildHttpRequest(WebHook.MethodType.POST, parsedWebHookDTO);

		assertTrue(request.toString().startsWith("POST"));
	}

	@Test
	public void shouldReturnPutRequest() throws URISyntaxException {
		reset(webHookHTTPPoolingFactory);
		final ParsedWebHookDTO parsedWebHookDTO = new ParsedWebHookDTO();
		parsedWebHookDTO.setUri(new URI("http://www.google.com"));
		parsedWebHookDTO.setBody("{}");
		parsedWebHookDTO.setHeaders(ImmutableMap.of("Content-Type", "application/json"));
		final Request request = webHookHTTPPoolingFactory.buildHttpRequest(WebHook.MethodType.PUT, parsedWebHookDTO);

		assertTrue(request.toString().startsWith("PUT"));
	}

	@Test
	public void shouldReturnPatchRequest() throws URISyntaxException {
		reset(webHookHTTPPoolingFactory);
		final ParsedWebHookDTO parsedWebHookDTO = new ParsedWebHookDTO();
		parsedWebHookDTO.setUri(new URI("http://www.google.com"));
		parsedWebHookDTO.setBody("{}");
		parsedWebHookDTO.setHeaders(ImmutableMap.of("Content-Type", "application/json"));
		final Request request = webHookHTTPPoolingFactory.buildHttpRequest(WebHook.MethodType.PATCH, parsedWebHookDTO);

		assertTrue(request.toString().startsWith("PATCH"));
	}

	@Test
	public void shouldReturnDeleteRequest() throws URISyntaxException {
		reset(webHookHTTPPoolingFactory);
		final ParsedWebHookDTO parsedWebHookDTO = new ParsedWebHookDTO();
		parsedWebHookDTO.setUri(new URI("http://www.google.com"));
		parsedWebHookDTO.setBody("{}"); // this will be ignore by DELETEs and not be serialized
		parsedWebHookDTO.setHeaders(ImmutableMap.of("Content-Type", "application/json"));
		final Request request = webHookHTTPPoolingFactory.buildHttpRequest(WebHook.MethodType.DELETE, parsedWebHookDTO);

		assertTrue(request.toString().startsWith("DELETE"));
	}

	@Test
	public void buildBody_InsertVariables() throws Exception {
		Map<String, String> body = ImmutableMap.of(
			"support_contact_first_name", "${support_contact_first_name}",
			"support_contact_last_name", "${support_contact_last_name}",
			"support_contact_email", "${support_contact_email}"
		);

		when(webHook.getBody()).thenReturn(new ObjectMapper().writeValueAsString(body));
		when(webHook.getIntegrationEventType()).thenReturn(IntegrationEventType.newInstance(IntegrationEventType
				.WORK_ACCEPT));

		Optional<ParsedWebHookDTO> parsedWebHookDTO = webHookHTTPPoolingFactory.buildHook(work, webHook, contextVariables);

		JSONObject result = new JSONObject(parsedWebHookDTO.get().getBody());
		assertEquals(result.get("support_contact_first_name"), work.getBuyerSupportUser().getFirstName());
		assertEquals(result.get("support_contact_last_name"), work.getBuyerSupportUser().getLastName());
		assertEquals(result.get("support_contact_email"), work.getBuyerSupportUser().getEmail());
	}

	@Test
	public void shouldReturnWebhookClientCompanyId() {
		final WebHook wh = new WebHook();
		final AbstractWebHookClient client = new AbstractWebHookClient();
		final Company company = new Company();
		final Long companyId = 2L;
		final Long webhookId = 3L;
		company.setId(companyId);
		client.setCompany(company);
		wh.setId(webhookId);
		wh.setWebHookClient(client);

		final Long companyClientId = webHookHTTPPoolingFactory.getClientCompanyId(wh);

		assertEquals(companyId, companyClientId);
	}

	@Test
	public void shouldReturnWebhookClientCompanyIdFromDb() {
		final Long webhookId = 3L;
		final Long companyId = 4L;
		final WebHook wh = new WebHook();
		wh.setId(webhookId);
		when(webHookIntegrationService.getWebhookClientCompanyId(webhookId)).thenReturn(Optional.of(companyId));

		final Long companyClientId = webHookHTTPPoolingFactory.getClientCompanyId(wh);

		verify(webHookIntegrationService).getWebhookClientCompanyId(webhookId);
		assertEquals(companyId, companyClientId);
	}
}
