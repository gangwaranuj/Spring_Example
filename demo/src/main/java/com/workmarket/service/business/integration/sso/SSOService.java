package com.workmarket.service.business.integration.sso;


import com.workmarket.sso.dto.SSOMetadataDTO;

public interface SSOService {

    SSOMetadataDTO getMetadataForCompany(String uuid);

    SSOMetadataDTO getSPMetadata();
}
