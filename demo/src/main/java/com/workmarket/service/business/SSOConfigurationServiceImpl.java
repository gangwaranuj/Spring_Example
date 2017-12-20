package com.workmarket.service.business;

import com.google.api.client.util.Lists;
import com.workmarket.common.core.RequestContext;
import com.workmarket.dao.company.SSOConfigurationDAO;
import com.workmarket.domains.model.Company;
import com.workmarket.domains.model.acl.AclRole;
import com.workmarket.domains.model.company.SSOConfiguration;
import com.workmarket.service.web.WebRequestContextProvider;
import com.workmarket.sso.SSOServiceClient;
import com.workmarket.sso.dto.SSOMetadataDTO;
import com.workmarket.sso.dto.SaveMetadataResponse;
import com.workmarket.web.forms.mmw.SSOConfigurationForm;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SSOConfigurationServiceImpl implements SSOConfigurationService {

	protected static final Log logger = LogFactory.getLog(SSOConfigurationServiceImpl.class);

	@Autowired SSOConfigurationDAO ssoConfigurationDAO;
	@Autowired CompanyService companyService;
	@Autowired SSOServiceClient ssoServiceClient;
	@Autowired AclRoleService aclRoleService;
	@Autowired private WebRequestContextProvider webRequestContextProvider;


	@Override
	public SSOConfiguration findByCompanyId(Long companyId) {
		return ssoConfigurationDAO.findBy("company.id", companyId);
	}

	@Override
	public List<Error> saveSSOConfiguration(SSOConfigurationForm form, Long companyId) {
		List<Error> errors = Lists.newArrayList();
		Company company = companyService.findById(companyId);

		logger.info("Saving IDP Metadata: " + form.getEntityId());
		SSOMetadataDTO ssoMetadata = new SSOMetadataDTO.Builder()
			.setMetadata(form.getIdpMetadata())
			.setEntityId(form.getEntityId())
			.setCompanyUuid(company.getUuid())
			.build();
		SaveMetadataResponse response =
				ssoServiceClient.saveSSOMetadata(ssoMetadata, webRequestContextProvider.getRequestContext())
				.toBlocking().first();
		if (response.getErrorMessage() != null) {
			logger.error("Error saving IDP metadata: " + response.getErrorMessage());
			errors.add(new Error(response.getErrorMessage()));
		}
		else {
			SSOConfiguration configuration = findByCompanyId(companyId);
			if (configuration == null) {
				configuration = new SSOConfiguration();
			}
			configuration.setCompany(company);
			AclRole aclRole = aclRoleService.findAclRoleById(form.getDefaultRoleId());
			configuration.setDefaultRole(aclRole);
			ssoConfigurationDAO.saveOrUpdate(configuration);
		}
		return errors;
	}
}
