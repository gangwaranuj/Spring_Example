package com.workmarket.domains.authentication.saml.metadata;

import com.workmarket.service.web.WebRequestContextProvider;
import com.workmarket.sso.SSOServiceClient;
import com.workmarket.sso.dto.SAMLContextClientResponse;

import org.opensaml.saml2.core.AuthnRequest;
import org.opensaml.saml2.metadata.IDPSSODescriptor;
import org.opensaml.saml2.metadata.provider.MetadataProviderException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.saml.context.SAMLContextProviderImpl;
import org.springframework.security.saml.context.SAMLMessageContext;
import org.springframework.security.saml.storage.SAMLMessageStorage;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


/**
 * This class is used to build the application's 'SAML context'. This information now resides in the SSO microservice
 * so we're extending SAMLContextProviderImpl so that we can fetch the context from the SSO microservice.
 */
public class WMSAMLContextProviderImpl extends SAMLContextProviderImpl {

	@Autowired private WebRequestContextProvider webRequestContextProvider;
	@Autowired private SSOServiceClient ssoServiceClient;

	protected final static Logger logger = LoggerFactory.getLogger(SAMLContextProviderImpl.class);

	/**
	 * Override SAMLContextProviderImpl#getLocalEntity to populate the SAMLMessageContext from the SSO microservice.
	 */
	@Override
	public SAMLMessageContext getLocalEntity(HttpServletRequest request, HttpServletResponse response)
		throws MetadataProviderException {

		SAMLMessageContext context = getSAMLContext(request, response, false);
		populateLocalEntityId(context, request.getRequestURI());
		populatePeerSSLCredential(context);
		context.setPeerEntityRole(IDPSSODescriptor.DEFAULT_ELEMENT_NAME);
		return context;
	}

	/**
	 * Override SAMLContextProviderImpl#getLocalAndPeerEntity to populate the SAMLMessageContext from the SSO microservice.
	 */
	@Override
	public SAMLMessageContext getLocalAndPeerEntity(HttpServletRequest request, HttpServletResponse response)
		throws MetadataProviderException {

		WMSAMLMessageContext context = getSAMLContext(request, response, true);
		return context;
	}

	/**
	 * Make call to SSO microservice to populate SAMLMessageContext
	 */
	private WMSAMLMessageContext getSAMLContext(
		HttpServletRequest request,
		HttpServletResponse response,
		boolean setPeerEntityId) throws MetadataProviderException {

		WMSAMLMessageContext context = new WMSAMLMessageContext();
		populateGenericContext(request, response, context);

		if (setPeerEntityId) {
			populatePeerEntityId(context);
		}

		final SAMLContextClientResponse contextResponse = ssoServiceClient
			.getSAMLContext(context.getPeerEntityId(), webRequestContextProvider.getRequestContext())
			.toBlocking().first();
		context.setRequest(request);
		context.setLocalEntityId(contextResponse.getLocalEntityId());
		context.setPeerExtendedMetadata(contextResponse.getPeerExtendedMetadata());
		context.setPeerEntityRoleMetadata(contextResponse.getPeerEntityRoleMetadata());
		context.setLocalExtendedMetadata(contextResponse.getLocalExtendedMetadata());
		context.setLocalEntityRoleMetadata(contextResponse.getLocalEntityRoleMetadata());
		context.setLocalEntityRole(contextResponse.getLocalEntityRoleMetadata().getElementQName());
		context.setLocalEntityMetadata(contextResponse.getLocalEntityDescriptor());
		context.setRedirectURL(contextResponse.getRedirectURL());
		SAMLMessageStorage messageStorage = context.getMessageStorage();
		if (messageStorage != null && contextResponse.getAuthRequest() != null) {
			AuthnRequest authnRequest = contextResponse.getAuthRequest();
			messageStorage.storeMessage(authnRequest.getID(), authnRequest);
		}
		return context;
	}
}

