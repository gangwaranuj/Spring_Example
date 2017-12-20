package com.workmarket.api.v2.employer.search.worker.services;

import com.workmarket.api.v2.employer.search.common.model.Highlights;
import com.workmarket.api.v2.employer.search.common.services.SearchUseCase;
import com.workmarket.api.v2.employer.search.worker.model.WorkerSearchRecord;
import com.workmarket.api.v2.employer.search.worker.model.WorkerSearchRequestDTO;
import com.workmarket.api.v2.employer.search.worker.model.WorkerSearchResponseDTO;
import com.workmarket.data.solr.model.SolrUserType;
import com.workmarket.data.solr.query.location.LocationQueryCreationService;

import com.workmarket.domains.authentication.model.ExtendedUserDetails;
import com.workmarket.domains.work.service.WorkService;
import com.workmarket.search.cache.StateLookupCache;
import com.workmarket.search.worker.FindWorkerClient;
import com.workmarket.search.worker.FindWorkerSearchResponse;
import com.workmarket.search.worker.query.model.FindWorkerCriteria;
import com.workmarket.search.worker.query.model.FindWorkerResponse;
import com.workmarket.search.worker.query.model.Worker;
import com.workmarket.service.business.VendorService;
import com.workmarket.service.web.WebRequestContextProvider;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.mutable.MutableObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import rx.functions.Action1;

import java.util.List;
import java.util.Map;

/**
 * Use case for doing a search for workers.
 */
@Component
@Scope("prototype")
public class WorkersSearchUseCase extends SearchUseCase<WorkersSearchUseCase, WorkerSearchResponseDTO> {
	private static final Logger logger = LoggerFactory.getLogger(WorkerDetailsUseCase.class);

	private final WorkService workService;
	private final VendorService vendorService;
	private final LocationQueryCreationService locationQueryCreationService;
	private final StateLookupCache stateLookupCache;
	private final FindWorkerClient findWorkerClient;

	private final ExtendedUserDetails userDetails;
	private final WebRequestContextProvider webRequestContextProvider;
	private final WorkerSearchRequestDTO criteria;

	private BaseWorkerSearchCriteriaBuilder workerSearchCriteriaBuilder;
	private FindWorkerCriteria findWorkerCriteria;
	private FindWorkerSearchResponse findWorkerSearchResponse;
	private WorkerSearchResponseDTO workerSearchResponseDTO;

	/**
	 * Constructor.
	 * @param workService The work service used to retrieve our work
	 * @param vendorService The vendor service
	 * @param locationQueryCreationService The location service
	 * @param stateLookupCache The state cache
	 * @param findWorkerClient The client for our worker-search-service
	 * @param extendedUserDetails The user details making the request
	 * @param requestContext The request context
	 * @param criteria The incoming criteria
	 */
	public WorkersSearchUseCase(final WorkService workService,
	                            final VendorService vendorService,
	                            final LocationQueryCreationService locationQueryCreationService,
	                            final StateLookupCache stateLookupCache,
	                            final FindWorkerClient findWorkerClient,
	                            final ExtendedUserDetails extendedUserDetails,
	                            final WebRequestContextProvider requestContext,
	                            final WorkerSearchRequestDTO criteria) {
		this.workService = workService;
		this.vendorService = vendorService;
		this.locationQueryCreationService = locationQueryCreationService;
		this.stateLookupCache = stateLookupCache;
		this.findWorkerClient = findWorkerClient;
		this.userDetails = extendedUserDetails;
		this.webRequestContextProvider = requestContext;
		this.criteria = criteria;
	}

	@Override
	public WorkerSearchResponseDTO andReturn() throws Exception {
		return workerSearchResponseDTO;
	}

	@Override
	protected WorkersSearchUseCase me() {
		return this;
	}

	@Override
	protected void failFast() {
		Assert.notNull(criteria);
	}

	@Override
	protected void init() {
		workerSearchCriteriaBuilder = new WorkerSearchCriteriaBuilder(workService, vendorService,
			locationQueryCreationService, stateLookupCache, userDetails, criteria);
	}

	@Override
	protected void prepare() {
		findWorkerCriteria = workerSearchCriteriaBuilder.build();
	}

	@Override
	protected void process() {
		final MutableObject<Throwable> errorCondition = new MutableObject<>();

		final long offset = criteria.getOffset() != null ? criteria.getOffset() : 0;
		final long limit = criteria.getLimit() != null ? criteria.getLimit() : 25;
		findWorkerClient.findWorkers(findWorkerCriteria, offset, limit, webRequestContextProvider.getRequestContext())
			.subscribe(
				new Action1<FindWorkerSearchResponse>() {
					@Override
				    public void call(FindWorkerSearchResponse searchResponse) {
						findWorkerSearchResponse = searchResponse;
				    }
				},
				new Action1<Throwable>() {
					@Override
					public void call(Throwable throwable) {
						errorCondition.setValue(throwable);
					}
				});

		if (errorCondition.getValue() != null) {
			throw new RuntimeException(errorCondition.getValue());
		}
	}

	@Override
	protected void finish() {
		// map our meta information first
		final WorkerSearchResponseDTO.Builder responseBuilder = new WorkerSearchResponseDTO.Builder();

		responseBuilder.setOffset(findWorkerSearchResponse.getOffset());
		responseBuilder.setSize(findWorkerSearchResponse.getLimit());
		responseBuilder.setResultCount(findWorkerSearchResponse.getTotalResults());
		responseBuilder.setQueryTimeMillis(findWorkerSearchResponse.getQueryTimeMillis());

		final FindWorkerResponse findWorkerResponse = findWorkerSearchResponse.getResults();
		for (Worker worker : findWorkerResponse.getWorkers()) {
			Map<String, List<String>> snippets = worker.getSnippets();

			Highlights highlights = null;
			if (snippets != null) {
				Highlights.Builder highlightsBuilder  = new Highlights.Builder();
				List<String> skillHighlights = snippets.get("skillNames");
				if (CollectionUtils.isNotEmpty(skillHighlights)) {
					highlightsBuilder.addSkills(skillHighlights);
				}

				List<String> licenseHighlights = snippets.get("licenseNames");
				if (CollectionUtils.isNotEmpty(licenseHighlights)) {
					highlightsBuilder.addLicenses(licenseHighlights);
				}

				List<String> certificationHighlights = snippets.get("certificationNames");
				if (CollectionUtils.isNotEmpty(certificationHighlights)) {
					highlightsBuilder.addCertifications(certificationHighlights);
				}
			}

			WorkerSearchRecord workerSearchRecord = new WorkerSearchRecord.Builder()
				.setUuid(worker.getUuid())
				.setScore(worker.getScore())
				.setHighlights(highlights)
				.setUserType(SolrUserType.getSolrUserTypeByCode(worker.getUserTypeCode()))
				.build();
			responseBuilder.addSearchResult(workerSearchRecord);
		}

		workerSearchResponseDTO = responseBuilder.build();
	}

	@Override
	protected WorkersSearchUseCase handleExceptions() throws Exception {
		if (exception != null) {
			logger.error("Failed executing " + this.getClass().getSimpleName() +
				" for Request Id: " + webRequestContextProvider.getWebRequestContext().getRequestId(), exception);
			throw exception;
		}
		return this;
	}
}
