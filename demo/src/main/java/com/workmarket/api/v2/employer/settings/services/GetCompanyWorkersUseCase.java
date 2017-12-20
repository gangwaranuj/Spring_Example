package com.workmarket.api.v2.employer.settings.services;

import com.google.common.collect.Lists;
import com.workmarket.api.v2.employer.assignments.services.UseCase;
import com.workmarket.api.v2.employer.search.worker.model.Worker;
import com.workmarket.api.v2.employer.search.worker.services.WorkerHydrator;
import com.workmarket.api.v2.employer.settings.models.CompanyWorkersDTO;
import com.workmarket.domains.authentication.model.ExtendedUserDetails;
import com.workmarket.domains.authentication.services.SecurityContextFacade;
import com.workmarket.service.business.CompanyService;
import com.workmarket.service.web.WebRequestContextProvider;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import java.util.List;

@Component
@Scope("prototype")
public class GetCompanyWorkersUseCase implements UseCase<GetCompanyWorkersUseCase, CompanyWorkersDTO> {

    private static final List<Worker> EMPTY_LIST = Lists.newArrayList();
    private static final int DEFAULT_OFFSET = 0;
    private static final int DEFAULT_LIMIT = 50;

    @Autowired private WebRequestContextProvider webRequestContextProvider;
    @Autowired private CompanyService companyService;
    @Autowired private SecurityContextFacade securityContextFacade;
    @Autowired @Qualifier("apiWorkerHydrator") private WorkerHydrator workerHydrator;

    private CompanyWorkersDTO.Builder companyWorkersDTOBuilder = new CompanyWorkersDTO.Builder();
    private Integer offset;
    private Integer limit;
    private String companyNumber;

    public GetCompanyWorkersUseCase(final WebRequestContextProvider webRequestContextProvider, final Integer offset, final Integer limit) {
        this.webRequestContextProvider = webRequestContextProvider;
        this.offset = offset;
        this.limit = limit;
    }

    public GetCompanyWorkersUseCase(
        final WebRequestContextProvider webRequestContextProvider, final Integer offset, final Integer limit, final String companyNumber) {
        this.webRequestContextProvider = webRequestContextProvider;
        this.offset = offset;
        this.limit = limit;
        this.companyNumber = companyNumber;
    }

    @Override
    public GetCompanyWorkersUseCase execute() {
        ExtendedUserDetails userDetails = securityContextFacade.getCurrentUser();
        List<String> workerNumbers;
        String companyNumber = this.companyNumber;
        if (companyNumber == null) {
            companyNumber = userDetails.getCompanyNumber();
        }
        Assert.notNull(companyNumber);
        int offset = this.offset == null ? DEFAULT_OFFSET : this.offset;
        int limit = this.limit == null ? DEFAULT_LIMIT : this.limit;
        workerNumbers = companyService.findWorkerNumbers(companyNumber);

        if (CollectionUtils.isEmpty(workerNumbers) || offset * limit > workerNumbers.size()) {
            companyWorkersDTOBuilder.setWorkers(EMPTY_LIST);
        } else {
            int fromIndex = offset * limit;
            int toIndex = (offset + 1) * limit > workerNumbers.size() ? workerNumbers.size() : (offset + 1) * limit;
            List<String> requestedRange = workerNumbers.subList(fromIndex, toIndex);
            companyWorkersDTOBuilder.setWorkers(workerHydrator.hydrateWorkersByUserNumbers(userDetails, requestedRange));
        }

        return this;
    }

    @Override
    public CompanyWorkersDTO andReturn() {
        return companyWorkersDTOBuilder.build();
    }
}
