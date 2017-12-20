package com.workmarket.domains.authentication.saml.metadata;


import com.workmarket.common.core.RequestContext;
import com.workmarket.service.web.WebRequestContextProvider;
import com.workmarket.sso.SSOServiceClient;
import com.workmarket.sso.dto.SAMLContextClientResponse;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.opensaml.saml2.metadata.EntityDescriptor;
import org.opensaml.saml2.metadata.RoleDescriptor;
import org.opensaml.saml2.metadata.provider.MetadataProviderException;
import org.springframework.security.saml.context.SAMLMessageContext;
import org.springframework.security.saml.metadata.ExtendedMetadata;
import org.springframework.security.saml.metadata.MetadataManager;
import rx.Observable;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.namespace.QName;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class WMSAMLContextProviderTest {

	private String localEntityId = "workmarket";
	private String peerEntityId = "okta";

	@Mock SSOServiceClient ssoServiceClient;
	@Mock MetadataManager metadataManager;
	@InjectMocks WMSAMLContextProviderImpl contextProvider;

	@Mock HttpServletRequest request;
	@Mock HttpServletResponse response;

	@Mock EntityDescriptor localEntityDescriptor;
	@Mock RoleDescriptor localEntityRoleMetadata;
	@Mock ExtendedMetadata localExtendedMetadata;
	@Mock ExtendedMetadata peerExtendedMetadata;
	@Mock RoleDescriptor peerEntityRoleMetadata;
	@Mock WebRequestContextProvider webRequestContextProvider;


	@Mock QName qname;

	@Before
	public void setUp() throws Exception {

		when(metadataManager.getHostedSPName()).thenReturn(localEntityId);
		when(localEntityRoleMetadata.getElementQName()).thenReturn(qname);

		when(request.getParameter("idp")).thenReturn(peerEntityId);

		SAMLContextClientResponse samlContextClientResponse = new SAMLContextClientResponse.Builder()
			.setLocalEntityId(localEntityId)
			.setLocalEntityDescriptor(localEntityDescriptor)
			.setLocalEntityRoleMetadata(localEntityRoleMetadata)
			.setLocalExtendedMetadata(localExtendedMetadata)
			.setPeerExtendedMetadata(peerExtendedMetadata)
			.setPeerEntityRoleMetadata(peerEntityRoleMetadata)
			.build();

		when(ssoServiceClient.getSAMLContext(anyString(), any(RequestContext.class)))
			.thenReturn(Observable.just(samlContextClientResponse));
		when(webRequestContextProvider.getRequestContext()).thenReturn(
				new RequestContext("TEST_REQUEST_ID", "TEST_TENANT"));
	}

	@Test
	public void testGetLocalEntity() throws MetadataProviderException {
		SAMLMessageContext context = contextProvider.getLocalEntity(request, response);

		assertEquals(context.getLocalEntityId(), localEntityId);
		assertEquals(context.getLocalExtendedMetadata(), localExtendedMetadata);
		assertEquals(context.getLocalEntityRoleMetadata(), localEntityRoleMetadata);
		assertEquals(context.getLocalEntityRoleMetadata(), localEntityRoleMetadata);
		assertNull(context.getPeerEntityId());

		verify(ssoServiceClient, times(1)).getSAMLContext(anyString(), any(RequestContext.class));
	}

	@Test
	public void testGetLocalAndPeerEntity() throws MetadataProviderException {
		SAMLMessageContext context = contextProvider.getLocalAndPeerEntity(request, response);

		assertEquals(context.getLocalEntityId(), localEntityId);
		assertEquals(context.getLocalExtendedMetadata(), localExtendedMetadata);
		assertEquals(context.getLocalEntityRoleMetadata(), localEntityRoleMetadata);
		assertEquals(context.getLocalEntityRoleMetadata(), localEntityRoleMetadata);
		assertEquals(peerEntityId, context.getPeerEntityId());
	}
}
