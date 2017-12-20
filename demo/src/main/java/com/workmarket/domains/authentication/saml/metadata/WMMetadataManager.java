package com.workmarket.domains.authentication.saml.metadata;

import org.opensaml.saml2.metadata.provider.MetadataProvider;
import org.opensaml.saml2.metadata.provider.MetadataProviderException;
import org.springframework.security.saml.metadata.MetadataManager;

import java.util.ArrayList;

/**
 * Metadata is managed in SSO microservice but we have a few Spring classes (SAMLEntryPoint,...) with autowire
 * MetadataManager dependencies.
 * This class was created to satisfy those dependencies.
 */
public class WMMetadataManager extends MetadataManager {

	public WMMetadataManager() throws MetadataProviderException {
		super(new ArrayList<MetadataProvider>());
		this.setRefreshCheckInterval(-1); // metadata is managed by microservice so no need to refresh
	}

	@Override
	public void refreshMetadata() {}
}
