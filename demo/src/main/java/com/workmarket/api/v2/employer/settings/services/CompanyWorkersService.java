package com.workmarket.api.v2.employer.settings.services;

import com.workmarket.api.v2.employer.settings.models.CompanyWorkersDTO;
import com.workmarket.common.core.RequestContext;

public interface CompanyWorkersService {
    CompanyWorkersDTO getWorkers(Integer offset, Integer limit);
    CompanyWorkersDTO getWorkers(Integer offset, Integer limit, String companyNumber);
}
