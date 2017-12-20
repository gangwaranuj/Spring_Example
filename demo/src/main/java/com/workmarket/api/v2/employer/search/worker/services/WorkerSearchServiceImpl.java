package com.workmarket.api.v2.employer.search.worker.services;

import com.workmarket.api.v2.employer.assignments.services.UseCaseFactory;
import com.workmarket.api.v2.employer.search.worker.model.WorkerDetailsRequestDTO;
import com.workmarket.api.v2.employer.search.worker.model.WorkerFiltersResponseDTO;
import com.workmarket.api.v2.employer.search.worker.model.WorkerSearchRequestDTO;
import com.workmarket.api.v2.employer.search.worker.model.WorkerDetailsResponseDTO;
import com.workmarket.api.v2.employer.search.worker.model.WorkerSearchResponseDTO;
import com.workmarket.data.solr.query.location.LocationQueryCreationService;
import com.workmarket.domains.authentication.model.ExtendedUserDetails;
import com.workmarket.domains.work.service.WorkService;
import com.workmarket.search.cache.HydratorCache;
import com.workmarket.search.cache.StateLookupCache;
import com.workmarket.search.worker.FindWorkerClient;
import com.workmarket.service.business.UserGroupService;
import com.workmarket.service.business.VendorService;
import com.workmarket.service.web.WebRequestContextProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

/**
 * Service implementation containing the logic to actually handle our worker search calls.
 */
@Service
public class WorkerSearchServiceImpl implements WorkerSearchService {

	@Autowired private UseCaseFactory useCaseFactory;
	@Autowired private WorkService workService;
	@Autowired private VendorService vendorService;
	@Autowired private LocationQueryCreationService locationQueryCreationService;
	@Autowired private StateLookupCache stateLookupCache;
	@Autowired private FindWorkerClient findWorkerClient;
	@Autowired private HydratorCache hydratorCache;
	@Autowired private UserGroupService userGroupService;
	@Autowired @Qualifier("apiWorkerHydrator") private WorkerHydrator workerHydrator;
	@Autowired private WebRequestContextProvider webRequestContextProvider;



	/**
	 * Constructor.
	 */
	public WorkerSearchServiceImpl() {

	}

	@Override
	public WorkerSearchResponseDTO searchWorkers(final WorkerSearchRequestDTO criteria,
	                                             final ExtendedUserDetails userDetails) throws Exception {
		return useCaseFactory
			.getUseCase(WorkersSearchUseCase.class, workService, vendorService, locationQueryCreationService,
				stateLookupCache, findWorkerClient, userDetails, webRequestContextProvider, criteria)
			.execute()
			.handleExceptions()
			.andReturn();
	}

	@Override
	public WorkerFiltersResponseDTO searchWorkerFilters(final WorkerSearchRequestDTO criteria,
	                                                    final ExtendedUserDetails userDetails) throws Exception {
		return useCaseFactory
			.getUseCase(WorkerFiltersSearchUseCase.class, workService, vendorService, locationQueryCreationService,
				stateLookupCache, findWorkerClient, hydratorCache, userGroupService, userDetails, webRequestContextProvider,
				criteria)
			.execute()
			.handleExceptions()
			.andReturn();
	}

	@Override
	public WorkerDetailsResponseDTO getWorkers(final WorkerDetailsRequestDTO criteria,
	                                           final ExtendedUserDetails userDetails) throws Exception {
		return useCaseFactory
			.getUseCase(WorkerDetailsUseCase.class, workerHydrator, userDetails, webRequestContextProvider, criteria)
			.execute()
			.handleExceptions()
			.andReturn();
	}
}
