package com.workmarket.domains.authentication.saml.metadata;

import com.google.common.base.Charsets;
import com.google.common.base.Joiner;

import com.workmarket.common.core.RequestContext;
import com.workmarket.service.web.WebRequestContextProvider;
import com.workmarket.sso.SSOServiceClient;
import com.workmarket.sso.dto.ValidationClientRequest;
import com.workmarket.sso.dto.ValidationClientResponse;

import org.opensaml.common.SAMLException;
import org.opensaml.common.SAMLObject;
import org.opensaml.saml2.core.AuthnRequest;
import org.opensaml.saml2.core.RequestedAuthnContext;
import org.opensaml.saml2.core.Response;
import org.opensaml.saml2.core.StatusCode;
import org.opensaml.saml2.core.StatusMessage;
import org.opensaml.saml2.metadata.AssertionConsumerService;
import org.opensaml.ws.transport.http.HttpServletRequestAdapter;
import org.opensaml.xml.XMLObject;
import org.opensaml.xml.security.SecurityException;
import org.opensaml.xml.util.DatatypeHelper;
import org.opensaml.xml.validation.ValidationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.saml.SAMLCredential;
import org.springframework.security.saml.context.SAMLMessageContext;
import org.springframework.security.saml.storage.SAMLMessageStorage;
import org.springframework.security.saml.websso.WebSSOProfileConsumerImpl;
import org.springframework.stereotype.Component;

import java.nio.ByteBuffer;
import java.nio.CharBuffer;

import javax.servlet.http.HttpServletRequest;
import javax.xml.bind.DatatypeConverter;

/**
 * Extending WebSSOProfileConsumerImpl in order to override processAuthenticationResponse so that we use SSO
 * microservice to perform assertion validation
 */
@Component
public class WMWebSSOProfileConsumerImpl extends WebSSOProfileConsumerImpl {

	private static final Logger logger = LoggerFactory.getLogger(WMWebSSOProfileConsumerImpl.class);

	@Autowired private SSOServiceClient ssoServiceClient;
	@Autowired private WebRequestContextProvider webRequestContextProvider;

	/**
	 * The input context object must have set the properties related to the returned Response, which is validated
	 * and in case no errors are found the SAMLCredential is returned
	 */
	public SAMLCredential processAuthenticationResponse(SAMLMessageContext ctx)
		throws SAMLException, ValidationException, SecurityException {


		if (!(ctx instanceof WMSAMLMessageContext)) {
			throw new SAMLException("Context is not of correct type");
		}
		final WMSAMLMessageContext context = (WMSAMLMessageContext) ctx;

		AuthnRequest request = null;
		SAMLObject message = context.getInboundSAMLMessage();

		// Verify type
		if (!(message instanceof Response)) {
			throw new SAMLException("Message is not of a Response object type");
		}
		Response response = (Response) message;

		verifyStatus(response);

		// Verify response to field if present, set request if correct
		SAMLMessageStorage messageStorage = context.getMessageStorage();
		if (messageStorage != null && response.getInResponseTo() != null) {
			XMLObject xmlObject = messageStorage.retrieveMessage(response.getInResponseTo());
			if (xmlObject == null) {
				throw new SAMLException("InResponseToField of the Response doesn't correspond to sent message " +
					response.getInResponseTo());
			} else if (xmlObject instanceof AuthnRequest) {
				request = (AuthnRequest) xmlObject;
			} else {
				throw new SAMLException("Sent request was of different type than the expected AuthnRequest " +
					response.getInResponseTo());
			}
		}

		// Verify endpoint requested in the original request
		verifyEndpoint(context, request);

		RequestedAuthnContext requestedAuthnContext = null;
		String requestId = null;
		if (request != null) {
			requestId = request.getID();
			requestedAuthnContext = request.getRequestedAuthnContext();
		}

		HttpServletRequest httpRequest = ((HttpServletRequestAdapter) context.getInboundMessageTransport()).getWrappedRequest();
		String requestURL = DatatypeHelper.safeTrimOrNullString(httpRequest.getRequestURL().toString());

		final ValidationClientRequest clientRequest = new ValidationClientRequest.Builder()
			.setRequestedAuthnContext(requestedAuthnContext)
			.setEncryptedAssertions(response.getEncryptedAssertions())
			.setAssertions(response.getAssertions())
			.setIdpEntityId(context.getInboundMessageIssuer())
			.setRelayState(context.getRelayState())
			.setRequestId(requestId)
			.setPeerSSLCredential(context.getPeerSSLCredential())
			.setPeerEntityRole(context.getPeerEntityRole())
			.setInboundSAMLBinding(context.getInboundSAMLBinding())
			.setRequestURL(requestURL)
			.setOriginalRequest(getOriginalRequest(context.getRequest()))
			.build();

		final ValidationClientResponse clientResponse = ssoServiceClient
			.validate(clientRequest, webRequestContextProvider.getRequestContext())
			.toBlocking()
			.first();

		logger.debug("WMWebSSOProfileConsumerImpl(): SAML Response validated \n\n {}", 
			clientRequest.getOriginalRequest());

		if (clientResponse.getError() != null) {
			throw new SAMLException(clientResponse.getError());
		}

		return clientResponse.getSamlCredential();
	}

	private String getOriginalRequest(final HttpServletRequest request) {
		final String encoded = request.getParameter("SAMLResponse");
		logger.debug("encoded request thing is {}", encoded);
		logger.debug("parameters are", Joiner.on(",").join(request.getParameterMap().keySet()));
		final CharBuffer xml = Charsets.UTF_8.decode(ByteBuffer.wrap(DatatypeConverter.parseBase64Binary(encoded)));
		logger.debug("decoded -> {}", xml.toString());
		return xml.toString();
	}

	private void verifyEndpoint(SAMLMessageContext context, AuthnRequest request) {
		if (request != null) {
			AssertionConsumerService assertionConsumerService = (AssertionConsumerService) context.getLocalEntityEndpoint();
			if (request.getAssertionConsumerServiceIndex() != null) {
				if (!request.getAssertionConsumerServiceIndex().equals(assertionConsumerService.getIndex())) {
					logger.info("Response was received at a different endpoint index than was requested");
				}
			} else {
				String requestedResponseURL = request.getAssertionConsumerServiceURL();
				String requestedBinding = request.getProtocolBinding();
				if (requestedResponseURL != null) {
					String responseLocation;
					if (assertionConsumerService.getResponseLocation() != null) {
						responseLocation = assertionConsumerService.getResponseLocation();
					} else {
						responseLocation = assertionConsumerService.getLocation();
					}
					if (!requestedResponseURL.equals(responseLocation)) {
						logger.info("Response was received at a different endpoint URL {} than was requested {}", responseLocation, requestedResponseURL);
					}
				}
				if (requestedBinding != null) {
					if (!requestedBinding.equals(context.getInboundSAMLBinding())) {
						logger.info("Response was received using a different binding {} than was requested {}", context.getInboundSAMLBinding(), requestedBinding);
					}
				}
			}
		}
	}

	private void verifyStatus(Response response) throws SAMLException {
		// Verify status
		String statusCode = response.getStatus().getStatusCode().getValue();
		if (!StatusCode.SUCCESS_URI.equals(statusCode)) {
			StatusMessage statusMessage = response.getStatus().getStatusMessage();
			String statusMessageText = null;
			if (statusMessage != null) {
				statusMessageText = statusMessage.getMessage();
			}
			throw new SAMLException(
				String.format("Response has invalid status code %s, status message is %s",
					statusCode, statusMessageText));
		}
	}
}
