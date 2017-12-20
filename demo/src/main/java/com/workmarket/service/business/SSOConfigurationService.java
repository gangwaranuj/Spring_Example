package com.workmarket.service.business;

import com.workmarket.domains.model.company.SSOConfiguration;
import com.workmarket.web.forms.mmw.SSOConfigurationForm;

import java.util.List;


public interface SSOConfigurationService {

    public SSOConfiguration findByCompanyId(Long companyId);

    public List<Error> saveSSOConfiguration(SSOConfigurationForm ssoConfiguration, Long companyId);
}
