package com.workmarket.api.v2.employer.search.worker.services;

import com.workmarket.api.v2.employer.search.common.services.SearchUseCase;
import com.workmarket.api.v2.employer.search.worker.model.Worker;
import com.workmarket.api.v2.employer.search.worker.model.WorkerDetailsRequestDTO;
import com.workmarket.api.v2.employer.search.worker.model.WorkerDetailsResponseDTO;
import com.workmarket.domains.authentication.model.ExtendedUserDetails;
import com.workmarket.service.web.WebRequestContextProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import java.util.List;

/**
 * Use case for getting the details of a set of workers.
 */
@Component
@Scope("prototype")
public class WorkerDetailsUseCase extends SearchUseCase<WorkerDetailsUseCase, WorkerDetailsResponseDTO> {
	private static final Logger logger = LoggerFactory.getLogger(WorkerDetailsUseCase.class);

	private final WorkerHydrator workerHydrator;
	private final WorkerDetailsRequestDTO criteria;
	private final WebRequestContextProvider requestContext;
	private final ExtendedUserDetails userDetails;

	private List<Worker> workersList;
	private WorkerDetailsResponseDTO workerDetailsResponseDTO;

	/**
	 * Constructor.
	 * @param workerHydrator The hydrator for converting our worker id's in to responses
	 * @param criteria The criteria for what workers to hydrate.
	 * @param userDetails The user details
	 * @param requestContext The context for the request
	 */
	public WorkerDetailsUseCase(final WorkerHydrator workerHydrator,
	                            final ExtendedUserDetails userDetails,
	                            final WebRequestContextProvider requestContext,
	                            final WorkerDetailsRequestDTO criteria) {
		this.workerHydrator = workerHydrator;
		this.userDetails = userDetails;
		this.requestContext = requestContext;
		this.criteria = criteria;

	}

	@Override
	public WorkerDetailsResponseDTO andReturn() throws Exception {
		return workerDetailsResponseDTO;
	}

	@Override
	protected WorkerDetailsUseCase me() {
		return this;
	}

	@Override
	protected void failFast() {
		Assert.notNull(criteria);
		Assert.isTrue(criteria.getUuids().size() > 0);
	}

	@Override
	protected void init() {
	}

	@Override
	protected void prepare() {
	}

	@Override
	protected void process() {
		workersList = workerHydrator.hydrateWorkersByUuids(userDetails, criteria.getUuids());
	}

	@Override
	protected void finish() {
		WorkerDetailsResponseDTO.Builder workerDetailsResponseBuilder = new WorkerDetailsResponseDTO.Builder();
		workerDetailsResponseBuilder.setWorkers(workersList);
		workerDetailsResponseDTO = workerDetailsResponseBuilder.build();
	}

	@Override
	protected WorkerDetailsUseCase handleExceptions() throws Exception {
		if (exception != null) {
			logger.error("Failed executing " + this.getClass().getSimpleName() +
				" for Request Id: " + requestContext.getWebRequestContext().getRequestId(), exception);
			throw exception;
		}
		return this;
	}
}
