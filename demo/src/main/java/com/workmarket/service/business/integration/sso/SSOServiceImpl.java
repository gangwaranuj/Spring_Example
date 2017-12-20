package com.workmarket.service.business.integration.sso;

import com.workmarket.service.web.WebRequestContextProvider;
import com.workmarket.sso.SSOServiceClient;
import com.workmarket.sso.dto.SSOMetadataDTO;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SSOServiceImpl implements SSOService {

	@Autowired private SSOServiceClient ssoServiceClient;
	@Autowired private WebRequestContextProvider webRequestContextProvider;

	@Override
	public SSOMetadataDTO getMetadataForCompany(String uuid) {
		Iterable<SSOMetadataDTO> metadata = ssoServiceClient
			.getCompanyMetadata(uuid, webRequestContextProvider.getRequestContext())
			.toBlocking()
			.toIterable();
		return metadata.iterator().next();
	}

	@Override
	public SSOMetadataDTO getSPMetadata() {
		return ssoServiceClient.getSPMetadata(webRequestContextProvider.getRequestContext())
				.toBlocking().first();
	}
}
