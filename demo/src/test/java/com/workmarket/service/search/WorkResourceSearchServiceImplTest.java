package com.workmarket.service.search;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.workmarket.common.core.RequestContext;
import com.workmarket.domains.model.Address;
import com.workmarket.domains.model.Company;
import com.workmarket.domains.model.Industry;
import com.workmarket.domains.model.ManageMyWorkMarket;
import com.workmarket.domains.model.User;
import com.workmarket.domains.model.postalcode.Country;
import com.workmarket.domains.model.postalcode.State;
import com.workmarket.domains.work.model.Work;
import com.workmarket.search.model.PeopleSearchTransientData;
import com.workmarket.search.request.user.AssignmentResourceSearchRequest;
import com.workmarket.search.response.user.PeopleSearchResponse;
import com.workmarket.search.response.user.PeopleSearchResult;
import com.workmarket.search.worker.FindWorkerClient;
import com.workmarket.search.worker.FindWorkerSearchResponse;
import com.workmarket.search.worker.query.model.FindWorkerCriteria;
import com.workmarket.search.worker.query.model.FindWorkerResponse;
import com.workmarket.search.worker.query.model.Worker;
import com.workmarket.service.exception.search.SearchException;
import com.workmarket.service.search.user.PeopleSearchService;
import com.workmarket.service.search.user.WorkResourceSearchServiceImpl;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import rx.Observable;

import java.util.List;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class WorkResourceSearchServiceImplTest {

	@InjectMocks WorkResourceSearchServiceImpl service;
	@Mock PeopleSearchService peopleSearchService;
	@Mock FindWorkerClient findWorkerClient;

	private Work work;
	private Company workCompany;
	private User user;
	private Industry industry;
	private Address address;

	@Before
	public void setUp() throws Exception {
		ManageMyWorkMarket manageMyWorkMarket = new ManageMyWorkMarket();
		manageMyWorkMarket.setPaymentTermsDays(15);
		manageMyWorkMarket.setPaymentTermsEnabled(true);

		industry = new Industry();
		industry.setId(1L);
		industry.setName("Technology");

		workCompany = mock(Company.class);
		when(workCompany.getId()).thenReturn(1L);
		when(workCompany.isLocked()).thenReturn(false);

		user = mock(User.class);
		when(user.getId()).thenReturn(1L);
		when(user.getCompany()).thenReturn(workCompany);

		work = new Work();
		work.setId(1L);
		work.setManageMyWorkMarket(manageMyWorkMarket);
		work.setCompany(workCompany);
		work.setIndustry(industry);
		work.setIsOnsiteAddress(false);
		work.setWorkNumber("343454");

		address = new Address();
		address.setAddress1("20 West 20th Street");
		address.setCity("New York");
		State state = new State();
		state.setName("NY");
		state.setCountry(Country.USA_COUNTRY);
		state.setShortName("NY");
		address.setState(state);
		address.setPostalCode("10011");
		address.setCountry(Country.USA_COUNTRY);
	}

	@Test(expected = IllegalArgumentException.class)
	public void searchAssignmentWorkers_noRequest_barf() throws Exception {
		service.searchAssignmentWorkers(null);
	}

	@Test
	public void searchAssignmentResources_withCorrectRequest_callsPeopleServiceService() throws SearchException {
		AssignmentResourceSearchRequest request = mock(AssignmentResourceSearchRequest.class);
		PeopleSearchResponse response = mock(PeopleSearchResponse.class);
		when(response.getResults()).thenReturn(Lists.newArrayList(new PeopleSearchResult()));

		when(peopleSearchService.searchPeople(any(PeopleSearchTransientData.class))).thenReturn(response);
		PeopleSearchResponse peopleSearchResponse = service.searchAssignmentWorkers(request);

		assertNotNull(peopleSearchResponse);
		Assert.assertFalse(peopleSearchResponse.getResults().isEmpty());
		verify(peopleSearchService, times(1)).searchPeople(any(PeopleSearchTransientData.class));
	}

	@Test
	public void searchWorkersForAutoRouting_withCorrectRequest_callsPeopleServiceService() throws Exception {
		AssignmentResourceSearchRequest request = mock(AssignmentResourceSearchRequest.class);
		PeopleSearchResponse response = mock(PeopleSearchResponse.class);
		when(response.getResults()).thenReturn(Lists.newArrayList(new PeopleSearchResult()));

		when(peopleSearchService.searchPeople(any(PeopleSearchTransientData.class))).thenReturn(response);
		PeopleSearchResponse peopleSearchResponse = service.searchWorkersForAutoRouting(request);

		assertNotNull(peopleSearchResponse);
		Assert.assertFalse(peopleSearchResponse.getResults().isEmpty());
		verify(peopleSearchService, times(1)).searchPeople(any(PeopleSearchTransientData.class));
	}

	@Test
	public void searchWorkResourcesForGroupRouting_withCorrectRequest_callsFindWorkerClient() throws Exception {
		FindWorkerCriteria findWorkerCriteria = mock(FindWorkerCriteria.class);
		RequestContext requestContext = mock(RequestContext.class);
		Long offset = 0L;
		Long limit = 50L;
		Worker worker = mock(Worker.class);
		FindWorkerSearchResponse findWorkerSearchResponse = mock(FindWorkerSearchResponse.class);
		FindWorkerResponse findWorkerResponse = mock(FindWorkerResponse.class);
		when(findWorkerSearchResponse.getResults()).thenReturn(findWorkerResponse);
		when(findWorkerResponse.getWorkers()).thenReturn(ImmutableList.of(worker));
		when(findWorkerClient.findWorkers(findWorkerCriteria, offset, limit, requestContext)).thenReturn(Observable.just(findWorkerSearchResponse));

		List<Worker> workers = service.searchWorkersForGroupRouting(findWorkerCriteria, offset, limit, requestContext);
		assertNotNull(workers);
		assertTrue(workers.size() > 0);
		verify(findWorkerClient, times(1)).findWorkers(any(FindWorkerCriteria.class), anyLong(), anyLong(), any(RequestContext.class));
	}
}
