package com.workmarket.domains.authentication.saml.metadata;


import com.workmarket.common.core.RequestContext;
import com.workmarket.service.web.WebRequestContextProvider;
import com.workmarket.sso.SSOServiceClient;
import com.workmarket.sso.dto.ValidationClientRequest;
import com.workmarket.sso.dto.ValidationClientResponse;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.opensaml.common.SAMLException;
import org.opensaml.saml2.core.Response;
import org.opensaml.saml2.core.Status;
import org.opensaml.saml2.core.StatusCode;
import org.opensaml.ws.transport.http.HttpServletRequestAdapter;
import org.opensaml.xml.security.SecurityException;
import org.opensaml.xml.validation.ValidationException;

import java.util.UUID;

import javax.servlet.http.HttpServletRequest;

import rx.Observable;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class WMWebSSOProfileConsumerImplTest {

	@Mock SSOServiceClient ssoServiceClient;
	@InjectMocks WMWebSSOProfileConsumerImpl webSSSOProfileConsumer;

	@Mock WMSAMLMessageContext context;
	@Mock Status status;
	@Mock StatusCode statusCode;
	@Mock Response response;
	@Mock HttpServletRequest httpServletRequest;
	@Mock HttpServletRequestAdapter httpServletRequestAdapter;
	@Mock WebRequestContextProvider webRequestContextProvider;

	@Before
	public void setUp() throws Exception {
		when(httpServletRequest.getParameter("SAMLResponse")).thenReturn("Zm9v%0A"); // foo
		when(context.getRequest()).thenReturn(httpServletRequest);
		when(context.getInboundSAMLMessage()).thenReturn(response);
		when(response.getStatus()).thenReturn(status);
		when(status.getStatusCode()).thenReturn(statusCode);
		when(httpServletRequest.getRequestURL()).thenReturn(new StringBuffer("http://localhost:8080"));
		when(httpServletRequestAdapter.getWrappedRequest()).thenReturn(httpServletRequest);
		when(context.getInboundMessageTransport()).thenReturn(httpServletRequestAdapter);
		final RequestContext requestContext = new RequestContext(UUID.randomUUID().toString(), "DUMMY_TENANT_ID");
		requestContext.setUserId("workmarket");
		when(webRequestContextProvider.getRequestContext()).thenReturn(requestContext);
	}

	@Test(expected = SAMLException.class)
	public void testProcessAuthenticationResponse_invalidStatusCode() throws SecurityException, SAMLException, ValidationException {

		when(statusCode.getValue()).thenReturn(StatusCode.AUTHN_FAILED_URI);
		webSSSOProfileConsumer.processAuthenticationResponse(context);
	}

	@Test
	public void testProcessAuthenticationResponse() throws SecurityException, SAMLException, ValidationException {
		ValidationClientResponse validationClientResponse = new ValidationClientResponse();
		when(ssoServiceClient.validate(any(ValidationClientRequest.class), any(RequestContext.class)))
			.thenReturn(Observable.just(validationClientResponse));
		when(statusCode.getValue()).thenReturn(StatusCode.SUCCESS_URI);

		webSSSOProfileConsumer.processAuthenticationResponse(context);
		verify(ssoServiceClient, times(1)).validate(any(ValidationClientRequest.class), any(RequestContext.class));
	}

	@Test(expected = SAMLException.class)
	public void testProcessAuthenticationResponse_withClientError_throwsSAMLException()
		throws SecurityException, SAMLException, ValidationException {

		ValidationClientResponse validationClientResponse = new ValidationClientResponse();
		validationClientResponse.setError("Validate error");
		when(ssoServiceClient.validate(any(ValidationClientRequest.class), any(RequestContext.class)))
			.thenReturn(Observable.just(validationClientResponse));
		when(statusCode.getValue()).thenReturn(StatusCode.SUCCESS_URI);

		webSSSOProfileConsumer.processAuthenticationResponse(context);
	}
}
