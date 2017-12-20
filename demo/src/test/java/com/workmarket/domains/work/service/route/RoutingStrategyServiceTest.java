package com.workmarket.domains.work.service.route;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.workmarket.configuration.Constants;
import com.workmarket.service.business.dto.CompanyIdentityDTO;
import com.workmarket.domains.model.Company;
import com.workmarket.domains.model.DeliveryStatusType;
import com.workmarket.domains.model.Industry;
import com.workmarket.domains.model.ManageMyWorkMarket;
import com.workmarket.domains.model.account.AccountRegister;
import com.workmarket.domains.work.dao.RoutingStrategyDAO;
import com.workmarket.domains.work.dao.RoutingStrategyGroupDAO;
import com.workmarket.domains.work.dao.WorkDAO;
import com.workmarket.domains.work.model.Work;
import com.workmarket.domains.work.model.route.AbstractRoutingStrategy;
import com.workmarket.domains.work.model.route.PeopleSearchRoutingStrategy;
import com.workmarket.domains.work.model.route.RoutingVisitor;
import com.workmarket.domains.work.model.route.UserRoutingStrategy;
import com.workmarket.domains.work.model.route.VendorSearchRoutingStrategy;
import com.workmarket.search.request.user.PeopleSearchRequest;
import com.workmarket.search.response.user.PeopleSearchResponse;
import com.workmarket.search.response.user.PeopleSearchResult;
import com.workmarket.service.business.CompanyService;
import com.workmarket.service.business.PricingService;
import com.workmarket.service.business.event.EventFactory;
import com.workmarket.service.business.event.work.ExecuteRoutingStrategyGroupEvent;
import com.workmarket.service.business.event.work.RoutingStrategyScheduledEvent;
import com.workmarket.service.business.event.work.WorkUpdateSearchIndexEvent;
import com.workmarket.service.infra.event.EventRouter;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Matchers.anySetOf;
import static org.mockito.Mockito.anyInt;
import static org.mockito.Mockito.atLeast;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class RoutingStrategyServiceTest {

	@Mock private WorkDAO workDAO;
	@Mock private RoutingStrategyDAO routingStrategyDAO;
	@Mock private EventFactory eventFactory;
	@Mock private EventRouter eventRouter;
	@Mock private RoutingStrategyGroupDAO routingStrategyGroupDAO;
	@Mock private RoutingVisitor workRoutingVisitor;
	@Mock private PricingService pricingService;
	@Mock private CompanyService companyService;
	@InjectMocks RoutingStrategyServiceImpl routingStrategyService;

	private static final long ACCOUNT_REGISTER_ID = 999999L;

	private Work work;
	private Company workCompany;
	private Company userCompany;
	private Industry industry;
	private PeopleSearchRequest peopleSearchRequest;
	private static final boolean assignToFirstToAccept = false;

	private PeopleSearchResponse peopleSearchResponse;
	ManageMyWorkMarket companyManageMyWorkMarket = new ManageMyWorkMarket();
	ManageMyWorkMarket manageMyWorkMarket = mock(ManageMyWorkMarket.class);
	ExecuteRoutingStrategyGroupEvent event;
	private RoutingStrategyScheduledEvent routingStrategyScheduledEvent;
	AccountRegister accountRegister;

	@Before
	public void setUp() throws Exception {
		peopleSearchResponse = new PeopleSearchResponse();
		PeopleSearchResult peopleSearchResult = new PeopleSearchResult().setUserId(1L);
		PeopleSearchResult peopleSearchResult2 = new PeopleSearchResult().setUserId(2L);
		PeopleSearchResult peopleSearchResult3 = new PeopleSearchResult().setUserId(3L);
		peopleSearchResponse.addToResults(peopleSearchResult);
		peopleSearchResponse.addToResults(peopleSearchResult2);
		peopleSearchResponse.addToResults(peopleSearchResult3);

		peopleSearchRequest = new PeopleSearchRequest();
		peopleSearchRequest.addToGroupFilter(1L);

		industry = new Industry();
		industry.setId(1L);
		industry.setName("Technology");

		workCompany = new Company();
		workCompany.setId(1L);

		userCompany = new Company();
		userCompany.setId(1000L);


		manageMyWorkMarket.setPaymentTermsDays(15);
		manageMyWorkMarket.setPaymentTermsEnabled(true);

		work = new Work();
		work.setId(1L);
		work.setManageMyWorkMarket(manageMyWorkMarket);
		work.setCompany(workCompany);
		work.setIndustry(industry);
		workCompany.setManageMyWorkMarket(companyManageMyWorkMarket);
		when(workDAO.get(anyLong())).thenReturn(work);
		when(workDAO.findWorkById(anyLong())).thenReturn(work);

		accountRegister = mock(AccountRegister.class);
		when(accountRegister.getId()).thenReturn(ACCOUNT_REGISTER_ID);
		when(pricingService.findDefaultRegisterForCompany(anyLong())).thenReturn(accountRegister);

		event = mock(ExecuteRoutingStrategyGroupEvent.class);
		when(eventFactory.buildExecuteRoutingStrategyGroupEvent(anyLong(), anyInt())).thenReturn(event);

		routingStrategyScheduledEvent = mock(RoutingStrategyScheduledEvent.class);
		when(eventFactory.buildRoutingStrategyScheduledEvent(any(AbstractRoutingStrategy.class))).thenReturn(routingStrategyScheduledEvent);

		CompanyIdentityDTO identity = mock(CompanyIdentityDTO.class);
		when(companyService.findCompanyIdentitiesByCompanyNumbers(anySetOf(String.class))).thenReturn(Lists.newArrayList(identity));
	}

	@Test
	public void addRoutingStrategy_WithPeopleSearchRequest_success() throws Exception {
		assertNotNull(routingStrategyService.addPeopleSearchRequestRoutingStrategy(1L, peopleSearchRequest, 1, assignToFirstToAccept));
		verify(routingStrategyDAO, atLeast(1)).saveOrUpdate(any(AbstractRoutingStrategy.class));
	}

	@Test
	public void addRoutingStrategy_WithGroupIds_success() throws Exception {
		assertNotNull(routingStrategyService.addGroupIdsRoutingStrategy(1L, Sets.newHashSet(1L, 2L), 1, assignToFirstToAccept));
		verify(routingStrategyDAO, atLeast(1)).saveOrUpdate(any(AbstractRoutingStrategy.class));
	}

	@Test
	public void addRoutingStrategy_WithPeopleSearchRequest_returnsNull() throws Exception {
		assertNull(routingStrategyService.addPeopleSearchRequestRoutingStrategy(1L, new PeopleSearchRequest(), 1, assignToFirstToAccept));
	}

	@Test
	public void addAutoRoutingStrategy_success() throws Exception {
		assertNotNull(routingStrategyService.addAutoRoutingStrategy(1L, assignToFirstToAccept));
		verify(routingStrategyDAO, atLeast(1)).saveOrUpdate(any(AbstractRoutingStrategy.class));
		verify(eventRouter, times(1)).sendEvent(any(RoutingStrategyScheduledEvent.class));
	}

	@Test
	public void addLikeGroupsAutoRoutingStrategy_success() throws Exception {
		assertNotNull(routingStrategyService.addLikeGroupsAutoRoutingStrategy(1L));
		verify(routingStrategyDAO, atLeast(1)).saveOrUpdate(any(AbstractRoutingStrategy.class));
		verify(eventRouter, times(1)).sendEvent(any(RoutingStrategyScheduledEvent.class));
	}

	@Test
	public void addLikeGroupVendorRoutingStrategy_success() throws Exception {
		assertNotNull(routingStrategyService.addLikeGroupVendorRoutingStrategy(1L));
		verify(routingStrategyDAO, atLeast(1)).saveOrUpdate(any(AbstractRoutingStrategy.class));
		verify(eventRouter, times(0)).sendEvent(any(RoutingStrategyScheduledEvent.class));
	}

	@Test
	public void addLikeWorkAutoRoutingStrategy_success() throws Exception {
		assertNotNull(routingStrategyService.addLikeWorkAutoRoutingStrategy(1L));
		verify(routingStrategyDAO, atLeast(1)).saveOrUpdate(any(AbstractRoutingStrategy.class));
		verify(eventRouter, times(1)).sendEvent(any(RoutingStrategyScheduledEvent.class));
	}

	@Test
	public void addLikeWorkVendorRoutingStrategy_success() throws Exception {
		assertNotNull(routingStrategyService.addLikeWorkVendorRoutingStrategy(1L));
		verify(routingStrategyDAO, atLeast(1)).saveOrUpdate(any(AbstractRoutingStrategy.class));
		verify(eventRouter, times(0)).sendEvent(any(RoutingStrategyScheduledEvent.class));
	}

	@Test
	public void addPolymathAutoRoutingStrategy_success() throws Exception {
		assertNotNull(routingStrategyService.addPolymathAutoRoutingStrategy(1L));
		verify(routingStrategyDAO, atLeast(1)).saveOrUpdate(any(AbstractRoutingStrategy.class));
		verify(eventRouter, times(1)).sendEvent(any(RoutingStrategyScheduledEvent.class));
	}

	@Test
	public void addPolymathVendorRoutingStrategy_success() throws Exception {
		assertNotNull(routingStrategyService.addPolymathVendorRoutingStrategy(1L));
		verify(routingStrategyDAO, atLeast(1)).saveOrUpdate(any(AbstractRoutingStrategy.class));
		verify(eventRouter, times(0)).sendEvent(any(RoutingStrategyScheduledEvent.class));
	}

	@Test
	public void addPeopleSearchRoutingStrategy_success() throws Exception {
		assertNotNull(routingStrategyService.addPeopleSearchRoutingStrategy(1L, Sets.newHashSet("1"), 1L, true));
		verify(routingStrategyDAO, atLeast(1)).saveOrUpdate(any(AbstractRoutingStrategy.class));
		verify(workRoutingVisitor, times(1)).visit(any(PeopleSearchRoutingStrategy.class));
		verify(eventRouter, times(1)).sendEvent(any(WorkUpdateSearchIndexEvent.class));
	}

	@Test
	public void addVendorSearchRoutingStrategy_success() throws Exception {
		assertNotNull(routingStrategyService.addVendorSearchRoutingStrategy(1L, Sets.newHashSet("1"), 1L, true));
		verify(routingStrategyDAO, atLeast(1)).saveOrUpdate(any(AbstractRoutingStrategy.class));
		verify(workRoutingVisitor, times(1)).visit(any(VendorSearchRoutingStrategy.class));
		verify(eventRouter, times(1)).sendEvent(any(WorkUpdateSearchIndexEvent.class));
	}

	@Test
	public void addVendorRoutingStrategyByCompanyNumbers_success() throws Exception {
		assertNotNull(routingStrategyService.addVendorRoutingStrategyByCompanyNumbers(1L, 1L, Sets.newHashSet("1"), 0, true));
		verify(routingStrategyDAO, atLeast(1)).saveOrUpdate(any(AbstractRoutingStrategy.class));
		verify(eventRouter, times(0)).sendEvent(any(WorkUpdateSearchIndexEvent.class));
	}

	@Test
	public void executeRoutingStrategy_WithUserRoutingStrategy_success() throws Exception {
		UserRoutingStrategy routingStrategy = new UserRoutingStrategy();
		routingStrategy.setDeliveryStatus(new DeliveryStatusType(DeliveryStatusType.SCHEDULED));
		routingStrategy.setWork(work);
		routingStrategy.setUserNumbers(Sets.newHashSet("10383949", "6583930"));
		when(routingStrategyDAO.get(anyLong())).thenReturn(routingStrategy);
		routingStrategyService.executeRoutingStrategy(1L);
		assertFalse(routingStrategy.getSummary().hasErrors());
	}

	@Test
	public void scheduleExecuteRoutingStrategies_FindsACompanysAccountRegister() throws Exception {
		routingStrategyService.scheduleExecuteRoutingStrategyGroup(1L, 1, 0);
		verify(pricingService).findDefaultRegisterForCompany(1L);
	}

	@Test
	public void addRoutingStrategy_SetsAMessageGroupID() throws Exception {
		routingStrategyService.scheduleExecuteRoutingStrategyGroup(1L, 1L, 0);
		verify(event).setMessageGroupId(
			String.format(Constants.ACCOUNT_REGISTER_MESSAGE_GROUP_ID, ACCOUNT_REGISTER_ID));
	}
}
