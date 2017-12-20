package com.workmarket.domains.work.service.route;

import com.google.common.base.Optional;
import com.google.common.collect.Sets;
import com.workmarket.domains.model.Address;
import com.workmarket.domains.model.Company;
import com.workmarket.domains.model.DeliveryStatusType;
import com.workmarket.domains.model.Industry;
import com.workmarket.domains.model.ManageMyWorkMarket;
import com.workmarket.domains.model.User;
import com.workmarket.domains.model.postalcode.Country;
import com.workmarket.domains.model.postalcode.State;
import com.workmarket.domains.work.model.Work;
import com.workmarket.domains.work.model.route.GroupRoutingStrategy;
import com.workmarket.search.request.user.AssignmentResourceSearchRequest;
import com.workmarket.search.worker.query.model.FindWorkerCriteria;
import com.workmarket.search.worker.query.model.LocationCriteria;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.runners.MockitoJUnitRunner;

import java.math.BigDecimal;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Author: rocio
 */
@RunWith(MockitoJUnitRunner.class)
public class WorkRoutingSearchRequestBuilderTest {

	@InjectMocks WorkRoutingSearchRequestBuilderImpl workRoutingSearchRequestBuilder;

	private static final BigDecimal LATITUDE = new BigDecimal(40.7534195);
	private static final BigDecimal LONGITUDE = new BigDecimal(-73.9931652);
	private static final double RADIUS_IN_KILO = 96.0;

	private Work work;
	private Work onsiteWork;
	private Company workCompany;
	private Industry industry;
	private User user;
	private Address addressWithLatLon;
	private Address addressWithStateOnly;
	private Address addressWithCountryOnly;
	private State state;
	private Set<Long> groupIds;
	private GroupRoutingStrategy routingStrategy;

	@Before
	public void setUp() throws Exception {
		ManageMyWorkMarket manageMyWorkMarket = new ManageMyWorkMarket();
		manageMyWorkMarket.setPaymentTermsDays(15);
		manageMyWorkMarket.setPaymentTermsEnabled(true);
		groupIds = Sets.newHashSet(1L, 2L, 4L);

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

		onsiteWork = new Work();
		onsiteWork.setId(2L);
		onsiteWork.setCompany(workCompany);
		onsiteWork.setIndustry(industry);
		onsiteWork.setIsOnsiteAddress(true);
		onsiteWork.setWorkNumber("121212");

		state = new State();
		state.setName("NY");
		state.setCountry(Country.USA_COUNTRY);
		state.setShortName("NY");

		addressWithLatLon = new Address();
		addressWithLatLon.setAddress1("20 West 20th Street");
		addressWithLatLon.setCity("New York");
		addressWithLatLon.setState(state);
		addressWithLatLon.setPostalCode("10011");
		addressWithLatLon.setCountry(Country.USA_COUNTRY);
		addressWithLatLon.setLatitude(LATITUDE);
		addressWithLatLon.setLongitude(LONGITUDE);

		addressWithStateOnly = new Address();
		addressWithStateOnly.setState(state);

		addressWithCountryOnly = new Address();
		addressWithCountryOnly.setCountry(Country.USA_COUNTRY);

		routingStrategy = new GroupRoutingStrategy();
		routingStrategy.setDeliveryStatus(new DeliveryStatusType(DeliveryStatusType.SCHEDULED));
		routingStrategy.setCreatorId(user.getId());
		routingStrategy.setUserGroups(groupIds);
	}

	@Test
	public void build_WithGroupRoutingStrategy_success() throws Exception {
		routingStrategy.setWork(work);
		AssignmentResourceSearchRequest request = workRoutingSearchRequestBuilder.build(routingStrategy);
		assertNotNull(request);
		assertNotNull(request.getRequest());
		assertNotNull(request.getRequest().getGroupFilter());
		assertFalse(request.getRequest().getGroupFilter().isEmpty());
		assertTrue(request.getRequest().getGroupFilter().size() == 3);
		assertNotNull(request.getWorkNumber());
		assertEquals(request.getBoostIndustryId(), 1L);
	}

	@Test
	public void buildFindWorkerCriteriaForGroupRouting_success() throws Exception {
		routingStrategy.setWork(work);
		FindWorkerCriteria criteria = workRoutingSearchRequestBuilder.buildFindWorkerCriteriaForGroupRouting(routingStrategy);

		assertEquals(user.getId().toString(), criteria.getRequestingUserId());
		assertEquals(workCompany.getId().toString(), criteria.getRequestingCompanyId());
		assertEquals(3, criteria.getGroupMembershipCriteria().getTalentPoolMemberships().size());
		assertEquals(3, criteria.getGroupMembershipCriteria().getTalentPoolMembershipOverrides().size());
		for (Long talentPoolId : groupIds) {
			assertTrue(criteria.getGroupMembershipCriteria().getTalentPoolMemberships().contains(talentPoolId.toString()));
			assertTrue(criteria.getGroupMembershipCriteria().getTalentPoolMembershipOverrides().contains(talentPoolId.toString()));
		}
		assertEquals(work.getWorkNumber(), criteria.getWorkCriteria().getWorkNumber());
		assertNull(criteria.getLocationCriteria());

	}

	@Test
	public void buildFindWorkerCriteriaForGroupRoutingWithLocation_success() throws Exception {
		onsiteWork.setAddress(addressWithLatLon);
		routingStrategy.setWork(onsiteWork);
		FindWorkerCriteria criteria = workRoutingSearchRequestBuilder.buildFindWorkerCriteriaForGroupRouting(routingStrategy);

		assertEquals(user.getId().toString(), criteria.getRequestingUserId());
		assertEquals(workCompany.getId().toString(), criteria.getRequestingCompanyId());
		assertEquals(3, criteria.getGroupMembershipCriteria().getTalentPoolMemberships().size());
		assertEquals(3, criteria.getGroupMembershipCriteria().getTalentPoolMembershipOverrides().size());
		for (Long talentPoolId : groupIds) {
			assertTrue(criteria.getGroupMembershipCriteria().getTalentPoolMemberships().contains(talentPoolId.toString()));
			assertTrue(criteria.getGroupMembershipCriteria().getTalentPoolMembershipOverrides().contains(talentPoolId.toString()));
		}
		assertEquals(onsiteWork.getWorkNumber(), criteria.getWorkCriteria().getWorkNumber());
		assertNotNull(criteria.getLocationCriteria());
	}

	@Test
	public void buildLocationCriteriaForGroupRoutingWithLatLon_success() throws Exception {
		onsiteWork.setAddress(addressWithLatLon);
		Optional<LocationCriteria> locationCriteria = workRoutingSearchRequestBuilder.buildLocationCriteria(onsiteWork);

		assertTrue(locationCriteria.isPresent());
		assertEquals(LATITUDE.doubleValue(), locationCriteria.get().getGeoPoint().getLatitude(), 0.001);
		assertEquals(LONGITUDE.doubleValue(), locationCriteria.get().getGeoPoint().getLongitude(), 0.001);
		assertEquals(RADIUS_IN_KILO, locationCriteria.get().getRadiusKilometers(), 0.001);
		assertNull(locationCriteria.get().getState());
		assertEquals(0, locationCriteria.get().getCountries().size());
	}

	@Test
	public void buildLocationCriteriaForGroupRoutingWithState_success() throws Exception {
		onsiteWork.setAddress(addressWithStateOnly);
		Optional<LocationCriteria> locationCriteria = workRoutingSearchRequestBuilder.buildLocationCriteria(onsiteWork);

		assertTrue(locationCriteria.isPresent());
		assertEquals(state.getShortName(), locationCriteria.get().getState());
		assertEquals(0, locationCriteria.get().getCountries().size());
		assertNull(locationCriteria.get().getGeoPoint());
		assertNull(locationCriteria.get().getRadiusKilometers());
	}

	@Test
	public void buildLocationCriteriaForGroupRoutingWithCountry_success() throws Exception {
		onsiteWork.setAddress(addressWithCountryOnly);
		Optional<LocationCriteria> locationcriteria = workRoutingSearchRequestBuilder.buildLocationCriteria(onsiteWork);

		assertTrue(locationcriteria.isPresent());
		assertEquals(Country.USA_COUNTRY.getId(), locationcriteria.get().getCountries().get(0));
		assertNull(locationcriteria.get().getState());
		assertNull(locationcriteria.get().getGeoPoint());
		assertNull(locationcriteria.get().getRadiusKilometers());
	}

	@Test
	public void buildLocationCriteriaForGroupRoutingWithoutLocation_success() throws Exception {
		Optional<LocationCriteria> locationCriteria = workRoutingSearchRequestBuilder.buildLocationCriteria(work);
		assertFalse(locationCriteria.isPresent());
	}
}
