package com.workmarket.api.v2.employer.search.worker.services;

import com.workmarket.api.v2.employer.search.worker.model.WorkerSearchRequestDTO;
import com.workmarket.data.solr.query.location.LocationQueryCreationService;
import com.workmarket.domains.authentication.model.ExtendedUserDetails;
import com.workmarket.domains.work.service.WorkService;
import com.workmarket.search.cache.StateLookupCache;
import com.workmarket.service.business.VendorService;

/**
 * Criteria builder that is specific to our worker search.
 */
public class WorkerSearchCriteriaBuilder extends BaseWorkerSearchCriteriaBuilder {

	/**
	 * Constructor.
	 * @param workService The work service used to resolve assignments
	 * @param vendorService The vendor service
	 * @param locationQueryCreationService The location service
	 * @param stateLookupCache The state lookup
	 * @param userDetails The user details for the user making the request
	 * @param workerSearchRequestDTO The incoming criteria
	 */
	public WorkerSearchCriteriaBuilder(final WorkService workService,
	                                   final VendorService vendorService,
	                                   final LocationQueryCreationService locationQueryCreationService,
	                                   final StateLookupCache stateLookupCache,
	                                   final ExtendedUserDetails userDetails,
	                                   final WorkerSearchRequestDTO workerSearchRequestDTO) {
		super(workService, vendorService, locationQueryCreationService, stateLookupCache,
			userDetails, workerSearchRequestDTO);
	}

	@Override
	protected void createCustomCriteria() {
		findWorkerCriteriaBuilder.setExcludeFacets(Boolean.TRUE);
	}
}
