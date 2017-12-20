package com.workmarket.api.v2.employer.settings.services;

import com.workmarket.api.v2.employer.assignments.services.UseCaseFactory;
import com.workmarket.api.v2.employer.settings.models.CompanyWorkersDTO;
import com.workmarket.common.core.RequestContext;
import com.workmarket.service.web.WebRequestContextProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Company workers service implementation.
 */
@Service
public class CompanyWorkersServiceImpl implements CompanyWorkersService {

    @Autowired UseCaseFactory useCaseFactory;
    @Autowired WebRequestContextProvider webRequestContextProvider;

    @Override
    public CompanyWorkersDTO getWorkers(Integer offset, Integer limit) {
        return useCaseFactory
            .getUseCase(GetCompanyWorkersUseCase.class, webRequestContextProvider, offset, limit)
            .execute()
            .andReturn();
    }

    @Override
    public CompanyWorkersDTO getWorkers(Integer offset, Integer limit, String companyNumber) {
        return useCaseFactory
            .getUseCase(GetCompanyWorkersUseCase.class, webRequestContextProvider, offset, limit, companyNumber)
            .execute()
            .andReturn();
    }
}
