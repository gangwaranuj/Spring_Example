package com.workmarket.api.v2.employer.search.worker.services;

import com.workmarket.api.v2.employer.search.worker.model.WorkerDetailsRequestDTO;
import com.workmarket.api.v2.employer.search.worker.model.WorkerFiltersResponseDTO;
import com.workmarket.api.v2.employer.search.worker.model.WorkerSearchRequestDTO;
import com.workmarket.api.v2.employer.search.worker.model.WorkerSearchResponseDTO;
import com.workmarket.api.v2.employer.search.worker.model.WorkerDetailsResponseDTO;
import com.workmarket.domains.authentication.model.ExtendedUserDetails;

/**
 * Service layer used to handle our find workers behavior.
 */
public interface WorkerSearchService {

	/**
	 * Searches for workers that meet our specified criteria.
	 *
	 * @param criteria The criteria used to identify the workers of interest
	 * @param userDetails The user making the request
	 * @return WorkerSearchResponseDTO The search response
	 * @throws Exception Thrown if there is a problem finding workers
	 */
	WorkerSearchResponseDTO searchWorkers(final WorkerSearchRequestDTO criteria,
	                                      final ExtendedUserDetails userDetails) throws Exception;

	/**
	 * Gets our filter data/counts for the workers who meet our criteria.
	 *
	 * @param criteria The criteria we are using to filter our results by
	 * @param userDetails The user making the request
	 * @return SearchResponseDTO The search response
	 * @throws Exception Thrown if there is a problem finding workers
	 */
	WorkerFiltersResponseDTO searchWorkerFilters(final WorkerSearchRequestDTO criteria,
	                                             final ExtendedUserDetails userDetails) throws Exception;


	/**
	 * Gets the details for a set of workers based on the given criteria.
	 *
	 * @param criteria The criteria for the details request
	 * @param userDetails The user making the request
	 * @return WorkerDetailsResponseDTO The set of worker details
	 * @throws Exception Thrown if there is a problem finding workers
	 */
	WorkerDetailsResponseDTO getWorkers(final WorkerDetailsRequestDTO criteria,
	                                    final ExtendedUserDetails userDetails) throws Exception;

}
