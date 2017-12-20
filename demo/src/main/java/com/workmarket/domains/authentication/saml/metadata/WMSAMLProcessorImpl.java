package com.workmarket.domains.authentication.saml.metadata;

import org.opensaml.common.xml.SAMLConstants;
import org.opensaml.saml2.metadata.IDPSSODescriptor;
import org.opensaml.ws.message.decoder.MessageDecoder;
import org.opensaml.ws.message.decoder.MessageDecodingException;
import org.opensaml.xml.security.SecurityException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.saml.context.SAMLMessageContext;
import org.springframework.security.saml.processor.SAMLBinding;
import org.springframework.security.saml.processor.SAMLProcessorImpl;
import org.springframework.util.Assert;

import javax.xml.namespace.QName;
import java.util.Collection;

/**
 * Extending SAMLProcessorImpl to integrate with SSO microservice. SAMLProcessorImpl contains steps to populate/verify
 * security information that is now stored in the microservice. This will need to be revisted if want to handle SP (Work
 * Market) initiated single sign on and the IDP requires requests to be digitally signed. The problem with this is that
 * currently the signing credentials are stored in the microservice and cannot be used directly in this application.
 */
public class WMSAMLProcessorImpl extends SAMLProcessorImpl {

	private static final Logger logger = LoggerFactory.getLogger(WMSAMLProcessorImpl.class);

	public WMSAMLProcessorImpl(Collection<SAMLBinding> bindings) {
		super(bindings);
	}

	/**
	 * Overriding retrieveMessage to skip populateSecurityPolicy step which is now handled in the SSO microservice
	 */
	@Override
	public SAMLMessageContext retrieveMessage(SAMLMessageContext samlContext, SAMLBinding binding)
		throws SecurityException, MessageDecodingException {

		verifyContext(samlContext);

		QName peerEntityRole = samlContext.getPeerEntityRole();
		if (peerEntityRole == null) {
			peerEntityRole = IDPSSODescriptor.DEFAULT_ELEMENT_NAME;
		}
		samlContext.setPeerEntityRole(peerEntityRole);
		samlContext.setInboundSAMLProtocol(SAMLConstants.SAML20P_NS);
		samlContext.setInboundSAMLBinding(binding.getBindingURI());

		// Decode the message
		MessageDecoder decoder = binding.getMessageDecoder();
		decoder.decode(samlContext);
		logger.debug("retrieveMessage(): SAMLContext decoded as of binding {}", binding.getClass().getName());
		return samlContext;
	}

	/**
	 * Verifies that context contains all the required information related to the local entity.
	 *
	 * @param context context to populate
	 */
	protected void verifyContext(SAMLMessageContext context) {

		Assert.isInstanceOf(WMSAMLMessageContext.class, context);
		WMSAMLMessageContext wmContext = (WMSAMLMessageContext) context;
		Assert.notNull(wmContext.getMetadataProvider(), "Metadata provider must be set in the context");
		Assert.notNull(wmContext.getLocalEntityId(), "Local entity id must be set in the context");
		Assert.notNull(wmContext.getLocalEntityRole(), "Local entity role must be set in the context");
		Assert.notNull(wmContext.getLocalEntityMetadata(), "Local entity metadata must be set in the context");
		Assert.notNull(wmContext.getLocalEntityRoleMetadata(), "Local entity role metadata must be set in the context");
		Assert.notNull(wmContext.getLocalExtendedMetadata(), "Local extended metadata must be set in the context");
		logger.debug("verifyContext(): SAMLMessageContext verified.");
	}
}
