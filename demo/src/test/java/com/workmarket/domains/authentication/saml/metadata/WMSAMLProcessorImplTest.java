package com.workmarket.domains.authentication.saml.metadata;


import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.opensaml.common.xml.SAMLConstants;
import org.opensaml.saml2.metadata.EntityDescriptor;
import org.opensaml.saml2.metadata.IDPSSODescriptor;
import org.opensaml.saml2.metadata.RoleDescriptor;
import org.opensaml.saml2.metadata.provider.MetadataProvider;
import org.opensaml.ws.message.decoder.MessageDecoder;
import org.opensaml.ws.message.decoder.MessageDecodingException;
import org.opensaml.xml.security.SecurityException;
import org.springframework.security.saml.metadata.ExtendedMetadata;
import org.springframework.security.saml.processor.SAMLBinding;

import javax.xml.namespace.QName;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class WMSAMLProcessorImplTest {

	private String localEntityId = "workmarket";
	private String bindingURI = "bindingURI";

	@Mock SAMLBinding samlBinding;

	@InjectMocks WMSAMLProcessorImpl samlProcessor;

	@Mock MetadataProvider metadataProvider;
	@Mock QName localEntityRole;
	@Mock EntityDescriptor localEntityMetadata;
	@Mock RoleDescriptor localEntityRoleMetadata;
	@Mock ExtendedMetadata localExtendedMetadata;

	@Mock MessageDecoder messageDecoder;
	@Mock QName qname;

	@Before
	public void setUp() throws Exception {

		when(samlBinding.getMessageDecoder()).thenReturn(messageDecoder);
		when(samlBinding.getBindingURI()).thenReturn(bindingURI);
	}

	@Test
	public void testRetrieveMessage() throws MessageDecodingException, SecurityException {

		WMSAMLMessageContext context = new WMSAMLMessageContext();
		context.setMetadataProvider(metadataProvider);
		context.setLocalEntityId(localEntityId);
		context.setLocalEntityRole(localEntityRole);
		context.setLocalEntityMetadata(localEntityMetadata);
		context.setLocalEntityRoleMetadata(localEntityRoleMetadata);
		context.setLocalExtendedMetadata(localExtendedMetadata);

		samlProcessor.retrieveMessage(context, samlBinding);

		assertEquals(IDPSSODescriptor.DEFAULT_ELEMENT_NAME, context.getPeerEntityRole());
		assertEquals(SAMLConstants.SAML20P_NS, context.getInboundSAMLProtocol());
		assertEquals(bindingURI, context.getInboundSAMLBinding());
	}

	@Test(expected = IllegalArgumentException.class)
	public void testVerifyContext_missingMetadataProvider() throws MessageDecodingException, SecurityException {

		WMSAMLMessageContext context = new WMSAMLMessageContext();
		context.setLocalEntityId(localEntityId);
		context.setLocalEntityRole(localEntityRole);
		context.setLocalEntityMetadata(localEntityMetadata);
		context.setLocalEntityRoleMetadata(localEntityRoleMetadata);
		context.setLocalExtendedMetadata(localExtendedMetadata);

		samlProcessor.verifyContext(context);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testVerifyContext_missingLocalEntityId() throws MessageDecodingException, SecurityException {

		WMSAMLMessageContext context = new WMSAMLMessageContext();
		context.setMetadataProvider(metadataProvider);
		context.setLocalEntityRole(localEntityRole);
		context.setLocalEntityMetadata(localEntityMetadata);
		context.setLocalEntityRoleMetadata(localEntityRoleMetadata);
		context.setLocalExtendedMetadata(localExtendedMetadata);

		samlProcessor.verifyContext(context);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testVerifyContext_missingLocalEntityRole() throws MessageDecodingException, SecurityException {

		WMSAMLMessageContext context = new WMSAMLMessageContext();
		context.setMetadataProvider(metadataProvider);
		context.setLocalEntityId(localEntityId);
		context.setLocalEntityMetadata(localEntityMetadata);
		context.setLocalEntityRoleMetadata(localEntityRoleMetadata);
		context.setLocalExtendedMetadata(localExtendedMetadata);

		samlProcessor.verifyContext(context);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testVerifyContext_missingLocalEntityMetadata() throws MessageDecodingException, SecurityException {

		WMSAMLMessageContext context = new WMSAMLMessageContext();
		context.setMetadataProvider(metadataProvider);
		context.setLocalEntityId(localEntityId);
		context.setLocalEntityRole(localEntityRole);
		context.setLocalEntityRoleMetadata(localEntityRoleMetadata);
		context.setLocalExtendedMetadata(localExtendedMetadata);

		samlProcessor.verifyContext(context);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testVerifyContext_missingLocalEntityRoleMetadata() throws MessageDecodingException, SecurityException {

		WMSAMLMessageContext context = new WMSAMLMessageContext();
		context.setMetadataProvider(metadataProvider);
		context.setLocalEntityId(localEntityId);
		context.setLocalEntityRole(localEntityRole);
		context.setLocalEntityMetadata(localEntityMetadata);
		context.setLocalExtendedMetadata(localExtendedMetadata);

		samlProcessor.verifyContext(context);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testVerifyContext_missingLocalExtendedMetadata() throws MessageDecodingException, SecurityException {

		WMSAMLMessageContext context = new WMSAMLMessageContext();
		context.setMetadataProvider(metadataProvider);
		context.setLocalEntityId(localEntityId);
		context.setLocalEntityRole(localEntityRole);
		context.setLocalEntityMetadata(localEntityMetadata);
		context.setLocalEntityRoleMetadata(localEntityRoleMetadata);

		samlProcessor.verifyContext(context);
	}

	@Test
	public void testVerifyContext() throws MessageDecodingException, SecurityException {

		WMSAMLMessageContext context = new WMSAMLMessageContext();
		context.setMetadataProvider(metadataProvider);
		context.setLocalEntityId(localEntityId);
		context.setLocalEntityRole(localEntityRole);
		context.setLocalEntityMetadata(localEntityMetadata);
		context.setLocalEntityRoleMetadata(localEntityRoleMetadata);
		context.setLocalExtendedMetadata(localExtendedMetadata);

		samlProcessor.verifyContext(context);

		// No exception thrown = test passed
		assertTrue(true);

	}
}
