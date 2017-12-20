package com.workmarket.domains.authentication.saml.metadata;


import org.opensaml.common.SAMLException;
import org.opensaml.saml2.metadata.provider.MetadataProviderException;
import org.opensaml.ws.message.encoder.MessageEncodingException;
import org.opensaml.ws.transport.http.HTTPOutTransport;
import org.opensaml.ws.transport.http.HTTPTransportUtils;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.saml.SAMLEntryPoint;
import org.springframework.security.saml.context.SAMLMessageContext;

/**
 * Extending SAMLEntryPoint to override initializeSSO. This method is responsible for redirecting authentication
 * request to SSO identity provider. The request was already constructed when we fetch the SAMLMessageContext from the
 * SSO microservice so all that is left to do is send the redirect
 */
public class WMSAMLEntryPoint extends SAMLEntryPoint {

	protected void initializeSSO(SAMLMessageContext context, AuthenticationException e) throws MetadataProviderException, SAMLException, MessageEncodingException {

		WMSAMLMessageContext wmMessageContext = (WMSAMLMessageContext) context;
		HTTPOutTransport out = (HTTPOutTransport) context.getOutboundMessageTransport();
		HTTPTransportUtils.addNoCacheHeaders(out);
		HTTPTransportUtils.setUTF8Encoding(out);
		out.sendRedirect(wmMessageContext.getRedirectURL());
	}
}
