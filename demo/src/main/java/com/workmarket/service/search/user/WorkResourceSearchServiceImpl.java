package com.workmarket.service.search.user;

import com.google.common.base.Optional;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.workmarket.common.core.RequestContext;
import com.workmarket.domains.model.lane.LaneType;
import com.workmarket.search.model.PeopleSearchTransientData;
import com.workmarket.search.model.SearchType;
import com.workmarket.search.request.user.AssignmentResourceSearchRequest;
import com.workmarket.search.response.user.PeopleSearchResponse;
import com.workmarket.search.worker.FindWorkerClient;
import com.workmarket.search.worker.FindWorkerSearchResponse;
import com.workmarket.search.worker.query.model.FindWorkerCriteria;
import com.workmarket.search.worker.query.model.Worker;
import com.workmarket.service.exception.search.SearchException;
import org.apache.commons.lang3.mutable.MutableObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import rx.functions.Action1;

import java.util.List;

@Component
public class WorkResourceSearchServiceImpl implements WorkResourceSearchService {
	private static final Logger logger = LoggerFactory.getLogger(WorkResourceSearchServiceImpl.class);

	@Autowired private PeopleSearchService peopleSearchService;
	@Autowired private FindWorkerClient findWorkerClient;


	@Override
	public PeopleSearchResponse searchInternalAssignmentWorkers(AssignmentResourceSearchRequest request) throws SearchException {
		Assert.notNull(request);
		Assert.notNull(request.getRequest());
		request.getRequest().setLaneFilter(Sets.newHashSet(LaneType.LANE_1));
		PeopleSearchResponse response = searchAssignmentWorkers(request);
		return response;
	}

	@Override
	public PeopleSearchResponse searchAssignmentWorkers(AssignmentResourceSearchRequest request) throws SearchException {
		Assert.notNull(request);
		return peopleSearchService.searchPeople(buildBasicPeopleSearchTransientData(request));
	}

	@Override
	public PeopleSearchResponse searchWorkersForAutoRouting(AssignmentResourceSearchRequest request) throws SearchException {
		Assert.notNull(request);
		PeopleSearchTransientData data = buildBasicPeopleSearchTransientData(request);
		data.setSkills(request.getSkills());
		data.setSearchType(SearchType.PEOPLE_SEARCH_ASSIGNMENT);
		return peopleSearchService.searchPeople(data);
	}

	@Override
	public List<Worker> searchWorkersForGroupRouting(
		final FindWorkerCriteria findWorkerCriteria,
		Long offset,
		Long limit,
		final RequestContext requestContext
	) throws SearchException {
		Assert.notNull(findWorkerCriteria);
		final List<Worker> workers = Lists.newArrayList();
		final MutableObject<Optional<SearchException>> exceptionMutableObject =
			new MutableObject<>(Optional.<SearchException>absent());
		findWorkerClient.findWorkers(findWorkerCriteria, offset, limit, requestContext)
			.subscribe(new Action1<FindWorkerSearchResponse>() {
						   @Override public void call(FindWorkerSearchResponse findWorkerSearchResponse) {
							   workers.addAll(findWorkerSearchResponse.getResults().getWorkers());
						   }
					   },
				new Action1<Throwable>() {
					@Override public void call(Throwable throwable) {
						logger.error("RequestId: {} Error: {}", requestContext.getRequestId(), throwable.getMessage(), throwable);
						exceptionMutableObject.setValue(
							Optional.of(new SearchException("Failed retrieving our search results", throwable.getCause())));
					}
				});

		if (exceptionMutableObject.getValue().isPresent()) {
			throw exceptionMutableObject.getValue().get();
		}

		return workers;
	}

	private PeopleSearchTransientData buildBasicPeopleSearchTransientData(AssignmentResourceSearchRequest request) {
		PeopleSearchTransientData data = new PeopleSearchTransientData();

		data.setOriginalRequest(request.getRequest());
		data.addToIgnoredLanes(LaneType.LANE_0);
		data.setSkills(request.getSkills());
		data.setSearchType(SearchType.PEOPLE_SEARCH_ASSIGNMENT);
		data.setWork(request.getWork());
		return data;
	}

}
