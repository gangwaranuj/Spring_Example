package com.workmarket.domains.work.service;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

import com.workmarket.common.service.helpers.ServiceMessageHelper;
import com.workmarket.dao.LocationDAO;
import com.workmarket.dao.datetime.TimeZoneDAO;
import com.workmarket.dao.random.WorkRandomIdentifierDAO;
import com.workmarket.data.solr.indexer.work.WorkIndexer;
import com.workmarket.domains.model.Address;
import com.workmarket.domains.model.AddressType;
import com.workmarket.domains.model.Company;
import com.workmarket.domains.model.DressCode;
import com.workmarket.domains.model.Industry;
import com.workmarket.domains.model.Location;
import com.workmarket.domains.model.LocationType;
import com.workmarket.domains.model.ManageMyWorkMarket;
import com.workmarket.domains.model.User;
import com.workmarket.domains.model.WorkStatusType;
import com.workmarket.domains.model.account.pricing.AccountPricingType;
import com.workmarket.domains.model.account.pricing.AccountServiceType;
import com.workmarket.domains.model.datetime.TimeZone;
import com.workmarket.domains.model.geocoding.Coordinate;
import com.workmarket.domains.model.postalcode.State;
import com.workmarket.domains.model.pricing.FlatPricePricingStrategy;
import com.workmarket.domains.model.pricing.PricingStrategyType;
import com.workmarket.domains.model.tax.AbstractTaxEntity;
import com.workmarket.domains.model.validation.ConstraintViolation;
import com.workmarket.domains.payments.service.AccountRegisterAuthorizationService;
import com.workmarket.domains.work.dao.WorkBundleDAO;
import com.workmarket.domains.work.model.AbstractWork;
import com.workmarket.domains.work.model.Work;
import com.workmarket.domains.work.model.WorkBundle;
import com.workmarket.domains.work.service.audit.WorkAuditService;
import com.workmarket.domains.work.service.route.WorkBundleRouting;
import com.workmarket.domains.work.service.route.WorkRoutingService;
import com.workmarket.domains.work.service.state.WorkStatusService;
import com.workmarket.domains.work.service.validator.WorkSaveRequestValidator;
import com.workmarket.dto.AddressDTO;
import com.workmarket.service.business.AddressService;
import com.workmarket.service.business.LocationService;
import com.workmarket.service.business.PricingService;
import com.workmarket.service.business.UserService;
import com.workmarket.service.business.account.AccountPricingService;
import com.workmarket.service.business.dto.AddressVerificationDTO;
import com.workmarket.service.business.dto.CompleteWorkDTO;
import com.workmarket.service.business.dto.UnassignDTO;
import com.workmarket.service.business.dto.WorkBundleDTO;
import com.workmarket.service.business.dto.WorkCostDTO;
import com.workmarket.service.business.wrapper.ValidateWorkResponse;
import com.workmarket.service.infra.business.AuthenticationService;
import com.workmarket.service.infra.business.InvariantDataService;
import com.workmarket.service.infra.event.EventRouter;
import com.workmarket.service.infra.security.WorkContext;
import com.workmarket.service.thrift.TWorkFacadeService;
import com.workmarket.thrift.core.ValidationException;
import com.workmarket.thrift.work.WorkActionException;
import com.workmarket.thrift.work.WorkRequest;
import com.workmarket.thrift.work.WorkResponse;
import com.workmarket.web.models.MessageBundle;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import ch.lambdaj.group.Group;

import static ch.lambdaj.Lambda.by;
import static ch.lambdaj.Lambda.group;
import static ch.lambdaj.Lambda.on;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyDouble;
import static org.mockito.Mockito.anyBoolean;
import static org.mockito.Mockito.anyLong;
import static org.mockito.Mockito.anyObject;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.startsWith;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class WorkBundleServiceTest {
	@Mock private WorkBundleDAO workBundleDAO;
	@Mock private WorkService workService;
	@Mock private WorkStatusService workStatusService;
	@Mock private WorkAuditService workAuditService;
	@Mock private UserService userService;
	@Mock private AuthenticationService authenticationService;
	@Mock private WorkActionRequestFactory workActionRequestFactory;
	@Mock private WorkRandomIdentifierDAO workNumberGenerator;
	@Mock private AccountPricingService accountPricingService;
	@Mock private PricingService pricingService;
	@Mock private TimeZoneDAO timeZoneDAO;
	@Mock private WorkIndexer workIndexer;
	@Mock private LocationService locationService;
	@Mock private InvariantDataService invariantDataService;
	@Mock private LocationDAO locationDAO;
	@Mock private WorkRoutingService workRoutingService;
	@Mock private TWorkFacadeService tWorkFacadeService;
	@Mock private EventRouter eventRouter;
	@Mock private WorkSaveRequestValidator workSaveRequestValidator;
	@Mock AccountRegisterAuthorizationService accountRegisterAuthorizationService;
	@Mock private WorkBundleRouting workBundleRouting;
	@Spy private ServiceMessageHelper messageHelper = new ServiceMessageHelper() {
		@Override public List<String> getMessages(List<ConstraintViolation> violations) { return null;}
		@Override public String getMessage(String message, Object... arguments) { return null; }
		@Override public String getMessage(ObjectError error) { return null; }
		@Override public List<String> getAllErrors(BindingResult binding) {
			List<String> result = Lists.newArrayList();
			if (binding.hasErrors()) {
				for (ObjectError e : binding.getAllErrors()) { result.add(e.getDefaultMessage()); }
			}
			return result;
		}
	};
	@Spy private AddressService addressService = new AddressService() {
		@Override public Address findById(Long addressId) { return null; }
		@Override public List<Address> findByIds(List<Long> addressIds) { return null; }
		@Override public void saveOrUpdate(Address address) {}
		@Override public void saveOrUpdate(State state) {}
		@Override public Address saveOrUpdate(AddressDTO addressDTO) {
			Address address = new Address();
			address.setAddress1(addressDTO.getAddress1());
			address.setCity(addressDTO.getCity());
			address.setState(mock(State.class));
			address.setPostalCode(addressDTO.getPostalCode());
			return address;
		}
		@Override public void addNewStateToAddress(Address address, String country, String state) {}

		@Override public AddressVerificationDTO verify(String address) throws Exception { return null; }
		@Override public Address verifyAndSave(Address address, MessageBundle messages) throws Exception { return null; }
		@Override public Coordinate getCoordinatesForUser(Long userId) { return null; }

		@Override
		public Coordinate getCoordinatesByAddressId(Long addressId) {
			return null;
		}
	};

	@InjectMocks WorkBundleServiceImpl workBundleService;

	WorkBundle parent;
	WorkBundleDTO workBundleDTO;

	Work child1;
	Work child2;
	Work child3;
	Address address1;
	Address address2;
	WorkStatusType workStatusType;
	WorkStatusType workStatusTypeActive;
	WorkStatusType workStatusTypeComplete;
	WorkStatusType workStatusTypePaid;
	WorkStatusType workStatusTypeSent;
	UnassignDTO unassignDTO;

	User user;
	Company company;
	Set<Work> workSet;
	List<String> workNumbers;
	ManageMyWorkMarket parentMMW = new ManageMyWorkMarket();
	ManageMyWorkMarket childMMW = new ManageMyWorkMarket();

	@Before
	public void setup() {
		Industry industry = mock(Industry.class);
		company = mock(Company.class);
		parent = mock(WorkBundle.class);
		when(parent.getId()).thenReturn(10L);
		when(parent.getCompany()).thenReturn(company);
		when(company.getId()).thenReturn(20L);
		when(parent.getManageMyWorkMarket()).thenReturn(parentMMW);

		workStatusType = mock(WorkStatusType.class);
		workStatusTypeActive = mock(WorkStatusType.class);
		workStatusTypeComplete = mock(WorkStatusType.class);
		workStatusTypePaid = mock(WorkStatusType.class);
		workStatusTypeSent = mock(WorkStatusType.class);
		when(workStatusType.getCode()).thenReturn(WorkStatusType.SENT);
		when(workStatusTypeActive.getCode()).thenReturn(WorkStatusType.ACTIVE);
		when(workStatusTypeComplete.getCode()).thenReturn(WorkStatusType.COMPLETE);
		when(workStatusTypePaid.getCode()).thenReturn(WorkStatusType.PAID);
		when(workStatusTypeSent.getCode()).thenReturn(WorkStatusType.SENT);

		child1 = mock(Work.class);
		when(child1.getWorkNumber()).thenReturn("2");
		when(child1.getId()).thenReturn(200L);
		when(child1.getIndustry()).thenReturn(industry);
		when(industry.getId()).thenReturn(2000L);
		when(child1.getPricingStrategyType()).thenReturn(PricingStrategyType.PER_HOUR);
		when(child1.isDraft()).thenReturn(true);
		when(child1.isInBundle()).thenReturn(false);
		when(workService.findWorkByWorkNumber(child1.getWorkNumber())).thenReturn(child1);
		childMMW.setPaymentTermsDays(10);
		childMMW.setPaymentTermsEnabled(true);
		childMMW.setPaymentTermsOverride(true);
		childMMW.setAutoPayEnabled(true);
		when(child1.getManageMyWorkMarket()).thenReturn(childMMW);

		child2 = mock(Work.class);
		when(child2.getWorkNumber()).thenReturn("3");
		when(child2.getId()).thenReturn(300L);
		when(child2.getPricingStrategyType()).thenReturn(PricingStrategyType.PER_HOUR);
		when(child2.getManageMyWorkMarket()).thenReturn(childMMW);
		when(child2.isDraft()).thenReturn(true);
		when(child2.isInBundle()).thenReturn(false);
		when(workService.findWorkByWorkNumber(child2.getWorkNumber())).thenReturn(child2);

		child3 = mock(Work.class);
		when(child3.getWorkNumber()).thenReturn("4");
		when(child3.getId()).thenReturn(400L);
		when(child3.getPricingStrategyType()).thenReturn(PricingStrategyType.PER_HOUR);
		when(child3.getManageMyWorkMarket()).thenReturn(childMMW);
		when(child3.getWorkStatusType()).thenReturn(WorkStatusType.newWorkStatusType(WorkStatusType.SENT));
		when(child3.isDraft()).thenReturn(false);

		// NYC
		address1 = mock(Address.class);
		when(address1.getLatitude()).thenReturn(new BigDecimal(40.7143528));
		when(address1.getLongitude()).thenReturn(new BigDecimal(-74.0059731));

		// Chicago
		address2 = mock(Address.class);
		when(address2.getLatitude()).thenReturn(new BigDecimal(41.8781136));
		when(address2.getLongitude()).thenReturn(new BigDecimal(-87.6297982));

		user = mock(User.class);
		when(user.getId()).thenReturn(1L);
		when(user.getCompany()).thenReturn(company);

		TimeZone tz = mock(TimeZone.class);

		workNumbers = ImmutableList.of(child1.getWorkNumber(), child2.getWorkNumber());
		List<Work> workList = ImmutableList.of(child1, child2);
		workSet = new HashSet<Work>();
		workSet.add(child1);
		workSet.add(child2);

		WorkBundle retBundle = mock(WorkBundle.class);
		when(retBundle.getTitle()).thenReturn("A Title");
		when(retBundle.getBundle()).thenReturn(workSet);
		when(retBundle.getId()).thenReturn(30L);
		when(retBundle.getCompany()).thenReturn(company);
		when(retBundle.getManageMyWorkMarket()).thenReturn(parentMMW);

		when(company.getAddress()).thenReturn(new Address());
		when(company.getAccountPricingType()).thenReturn(new AccountPricingType(AccountPricingType.TRANSACTIONAL_PRICING_TYPE));
		when(userService.getUser(1L)).thenReturn(user);
		when(workNumberGenerator.generateUniqueNumber()).thenReturn("5");
		when(accountPricingService.findAccountServiceTypeConfiguration(any(Company.class), eq(AbstractTaxEntity.COUNTRY_USA))).thenReturn(new AccountServiceType(AccountServiceType.NONE));
		when(timeZoneDAO.get(any(Long.class))).thenReturn(tz);
		when(workBundleDAO.findAllInByWorkNumbers(workNumbers)).thenReturn(workList);
		when(workService.buildWork(any(Long.class), any(WorkBundleDTO.class), any(WorkBundle.class), anyBoolean())).thenReturn(retBundle);

		WorkBundleDTO workBundleDTOOrig = new WorkBundleDTO();
		workBundleDTOOrig.setTitle("A Title");
		workBundleDTOOrig.setDescription("Description");
		workBundleDTOOrig.setWorkNumbers(workNumbers);
		workBundleDTO = spy(workBundleDTOOrig);

		unassignDTO = mock(UnassignDTO.class);
		when(unassignDTO.getWorkId()).thenReturn(10L);
		when(unassignDTO.getCancellationReasonTypeCode()).thenReturn("");
		when(unassignDTO.getNote()).thenReturn("");

		geocodeAdapterResponse = new AddressDTO();
		geocodeAdapterResponse.setAddress1("1052-1098 Easton Rd");
		geocodeAdapterResponse.setCity("Easton");
		geocodeAdapterResponse.setState("OH");
		geocodeAdapterResponse.setPostalCode("44076");
		geocodeAdapterResponse.setCountry("USA");
		geocodeAdapterResponse.setAddressTypeCode(AddressType.COMPANY);

		// midpoint
		try {
			when(locationService.reverseLookup(anyDouble(), anyDouble())).thenReturn(geocodeAdapterResponse);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Test(expected = IllegalArgumentException.class)
	public void addToBundle_NullParent_Exception() {
		workBundleService.addToBundle(null, child1);
	}

	@Test(expected = IllegalArgumentException.class)
	public void addToBundle_NullChild_Exception() {
		workBundleService.addToBundle(parent, null);
	}

	@Test
	public void isBundle_True() {
		assertEquals(true, workBundleService.isAssignmentBundle(parent));
	}

	@Test
	public void isBundle_False() {
		assertEquals(false, workBundleService.isAssignmentBundle(child1));
	}

	@Test
	public void addToBundle_ParentAndChild_SetParentCalled() {
		workBundleService.addToBundle(parent, child1);
		verify(child1).setParent(parent);
	}

	@Test
	public void addToBundle_NullChildren() {
		WorkBundle parent = new WorkBundle();
		workBundleService.addToBundle(parent, child1);

		assertEquals(1, parent.getBundle().size());
		assertEquals(child1, parent.getBundle().iterator().next());
	}

	@Test
	public void addToBundle_Ids() {
		when(workService.findWork(1L)).thenReturn(parent);
		when(workService.findWork(2L)).thenReturn(child1);

		workBundleService.addToBundle(1L, 2L);

		verify(workService).findWork(1L);
		verify(workService).findWork(2L);
		verify(child1).setParent(parent);
	}

	@Test
	public void unassignBundle_AllActive() {
		when(workService.findWork(10L)).thenReturn(parent);
		when(parent.getBundle()).thenReturn(ImmutableSet.of(child1, child2));
		when(parent.getWorkStatusType()).thenReturn(workStatusTypeActive);
		when(child1.getWorkStatusType()).thenReturn(workStatusTypeActive);
		when(child2.getWorkStatusType()).thenReturn(workStatusTypeActive);

		workBundleService.unassignBundle(unassignDTO);

		verify(workService, times(3)).unassignWork((UnassignDTO) anyObject());
	}

	@Test
	public void unassignBundle_OneComplete() {
		when(workService.findWork(10L)).thenReturn(parent);
		when(parent.getBundle()).thenReturn(ImmutableSet.of(child1, child2));
		when(parent.getWorkStatusType()).thenReturn(workStatusTypeActive);
		when(child1.getWorkStatusType()).thenReturn(workStatusTypeActive);
		when(child2.getWorkStatusType()).thenReturn(workStatusTypeComplete);

		boolean result = workBundleService.unassignBundle(unassignDTO);
		assertEquals(false, result);

		verify(workService, never()).unassignWork((UnassignDTO) anyObject());
	}

	@Test
	public void unassignBundle_OneSent() {
		when(workService.findWork(10L)).thenReturn(parent);
		when(parent.getBundle()).thenReturn(ImmutableSet.of(child1, child2));
		when(parent.getWorkStatusType()).thenReturn(workStatusTypeActive);
		when(child1.getWorkStatusType()).thenReturn(workStatusTypeActive);
		when(child2.getWorkStatusType()).thenReturn(workStatusTypeSent);

		workBundleService.unassignBundle(unassignDTO);

		verify(workService, times(2)).unassignWork((UnassignDTO) anyObject());
	}

	@Test
	public void unassignBundle_OnePaid() {
		when(workService.findWork(10L)).thenReturn(parent);
		when(parent.getBundle()).thenReturn(ImmutableSet.of(child1, child2));
		when(parent.getWorkStatusType()).thenReturn(workStatusTypeActive);
		when(child1.getWorkStatusType()).thenReturn(workStatusTypeActive);
		when(child2.getWorkStatusType()).thenReturn(workStatusTypePaid);

		workBundleService.unassignBundle(unassignDTO);

		verify(workService, never()).unassignWork((UnassignDTO)anyObject());
	}

	@Test
	public void addToBundle_Numbers() {
		when(workService.findWorkByWorkNumber("1")).thenReturn(parent);
		when(workService.findWorkByWorkNumber("2")).thenReturn(child1);

		workBundleService.addToBundle("1", "2");

		verify(workService).findWorkByWorkNumber("1");
		verify(workService).findWorkByWorkNumber("2");
		verify(child1).setParent(parent);
	}

	@Test
	public void addToBundle_IdNumberMix() {
		when(workService.findWork(1L)).thenReturn(parent);
		when(workService.findWorkByWorkNumber("2")).thenReturn(child1);

		workBundleService.addToBundle(1L, "2");

		verify(workService).findWork(1L);
		verify(workService).findWorkByWorkNumber("2");
		verify(child1).setParent(parent);
	}

	@Test
	public void addToBundle_NumberIdMix() {
		when(workService.findWorkByWorkNumber("1")).thenReturn(parent);
		when(workService.findWork(2L)).thenReturn(child1);

		workBundleService.addToBundle("1", 2L);

		verify(workService).findWorkByWorkNumber("1");
		verify(workService).findWork(2L);
		verify(child1).setParent(parent);
	}

	@Test
	public void verifyDTODefaults() {
		assertEquals(false, workBundleDTO.isAssignToFirstResource());
		assertEquals(Boolean.FALSE, workBundleDTO.isShowInFeed());
		assertEquals(0.001, workBundleDTO.getFlatPrice(), .0001);
		assertEquals(new FlatPricePricingStrategy().getId(), workBundleDTO.getPricingStrategyId());
		assertEquals(Boolean.FALSE, workBundleDTO.getIsOnsiteAddress());
		assertEquals(Boolean.TRUE, workBundleDTO.getDisablePriceNegotiation());
	}

	@Test
	public void saveOrUpdateWorkBundle_DateRange_DTOSetScheduleMethodsCalled() {
		Calendar from = Calendar.getInstance();
		from.set(2013, Calendar.JUNE, 20, 15, 0, 0);

		Calendar to = Calendar.getInstance();
		to.set(2013, Calendar.JUNE, 25, 15, 0, 0);

		when(child1.getScheduleFrom()).thenReturn(from);
		when(child1.getIsScheduleRange()).thenReturn(false);

		when(child2.getScheduleFrom()).thenReturn(from);
		when(child2.getScheduleThrough()).thenReturn(to);
		when(child2.getIsScheduleRange()).thenReturn(true);

		workBundleService.saveOrUpdateWorkBundle(1L, workBundleDTO);

		verify(workBundleDTO).setScheduleFromString(startsWith("2013-06-20T15:00:00"));
		verify(workBundleDTO).setScheduleThroughString(startsWith("2013-06-25T15:00:00"));
		verify(workBundleDTO).setIsScheduleRange(true);
	}

	@Test
	public void saveOrUpdateWorkBundle_DateRangeSame_DTOSetScheduleMethodsCalled() {
		Calendar from = Calendar.getInstance();
		from.set(2013, Calendar.JUNE, 20, 15, 0, 0);

		when(child1.getScheduleFrom()).thenReturn(from);
		when(child1.getIsScheduleRange()).thenReturn(false);

		when(child2.getScheduleFrom()).thenReturn(from);
		when(child2.getIsScheduleRange()).thenReturn(false);

		workBundleService.saveOrUpdateWorkBundle(1L, workBundleDTO);
		verify(workBundleDTO).setScheduleFromString(startsWith("2013-06-20T15:00:00"));
		verify(workBundleDTO).setIsScheduleRange(false);
	}

	@Test
	public void saveOrUpdateWorkBundle_TitleAndDescriptionSet_DTOSetIndustryCalled() {
		workBundleService.saveOrUpdateWorkBundle(1L, workBundleDTO);

		verify(workBundleDTO).setIndustryId(2000L);
	}

	@Test
	public void removeFromBundle_BundleWithChildren_ConfirmBundleChange() {
		WorkBundle bundle = workBundleService.saveOrUpdateWorkBundle(1L, workBundleDTO);

		workBundleService.removeFromBundle(bundle, child2);

		assertEquals(1, bundle.getBundle().size());
	}

	@Test
	public void removeFromBundle_BundleWithChildren_RoutingInProgress_ConfirmBundleNotChanged() {
		WorkBundle bundle = workBundleService.saveOrUpdateWorkBundle(1L, workBundleDTO);
		when(workBundleRouting.isWorkBundlePendingRouting(anyLong())).thenReturn(true);

		workBundleService.removeFromBundle(bundle, child2);

		assertEquals(2, bundle.getBundle().size());
	}

	private void budgetTestsSetup() {
		when(parent.getBundle()).thenReturn(ImmutableSet.of(child1, child2));

		when(pricingService.calculateMaximumResourceCost(child1)).thenReturn(new BigDecimal(100));
		when(pricingService.calculateMaximumResourceCost(child2)).thenReturn(new BigDecimal(100));
	}

	@Test
	public void getBundleBudget_IsBuyer_NoBuyerFee() {
		budgetTestsSetup();
		when(workService.getWorkContext(parent, user)).thenReturn(ImmutableList.of(WorkContext.OWNER));
		when(accountRegisterAuthorizationService.calculateCostOnSentWork(any(Work.class))).thenReturn(new WorkCostDTO(new BigDecimal(100), new BigDecimal(0), new BigDecimal(100)));

		BigDecimal result = workBundleService.getBundleBudget(user, parent);

		assertEquals(new BigDecimal(200), result);
	}

	@Test
	public void getBundleBudget_IsBuyer_BuyerFee() {
		budgetTestsSetup();
		when(workService.getWorkContext(parent, user)).thenReturn(ImmutableList.of(WorkContext.OWNER));
		when(accountRegisterAuthorizationService.calculateCostOnSentWork(any(Work.class))).thenReturn(new WorkCostDTO(new BigDecimal(100), new BigDecimal(10), new BigDecimal(110)));

		BigDecimal result = workBundleService.getBundleBudget(user, parent);

		assertEquals(new BigDecimal(220), result);
	}

	@Test
	public void getBundleBudget_IsResource_NoBuyerFee() {
		budgetTestsSetup();
		when(workService.getWorkContext(parent, user)).thenReturn(ImmutableList.of(WorkContext.ACTIVE_RESOURCE));
		when(accountRegisterAuthorizationService.calculateCostOnSentWork(any(Work.class))).thenReturn(new WorkCostDTO(new BigDecimal(100), new BigDecimal(0), new BigDecimal(100)));

		BigDecimal result = workBundleService.getBundleBudget(user, parent);

		assertEquals(new BigDecimal(200), result);
	}

	@Test
	public void getBundleBudget_IsResource_BuyerFee() {
		budgetTestsSetup();
		when(workService.getWorkContext(parent, user)).thenReturn(ImmutableList.of(WorkContext.ACTIVE_RESOURCE));
		when(accountRegisterAuthorizationService.calculateCostOnSentWork(any(Work.class))).thenReturn(new WorkCostDTO(new BigDecimal(100), new BigDecimal(10), new BigDecimal(110)));

		BigDecimal result = workBundleService.getBundleBudget(user, parent);

		assertEquals(new BigDecimal(200), result);
	}

	@Test
	public void  addAllToBundleByWork_HappyPath() throws WorkActionException, ValidationException {
		ValidateWorkResponse validateWorkResponse = mock(ValidateWorkResponse.class);
		when (validateWorkResponse.isSuccessful()).thenReturn(true);
		when(authenticationService.getCurrentUser()).thenReturn(user);

		WorkResponse workResponse = mock(WorkResponse.class);
		when(tWorkFacadeService.findWork(any(WorkRequest.class))).thenReturn(workResponse);

		assertEquals(0, workBundleService.addAllToBundleByWork(parent, ImmutableList.of(child1, child2)).size());
	}

	@Test
	public void  addAllToBundleByWork_OneNotDraft() throws WorkActionException, ValidationException {
		ValidateWorkResponse validateWorkResponse = mock(ValidateWorkResponse.class);
		when (validateWorkResponse.isSuccessful()).thenReturn(true);
		when(authenticationService.getCurrentUser()).thenReturn(user);

		WorkResponse workResponse = mock(WorkResponse.class);
		when(tWorkFacadeService.findWork(any(WorkRequest.class))).thenReturn(workResponse);

		assertEquals(1, workBundleService.addAllToBundleByWork(parent, ImmutableList.of(child1, child2, child3)).size());
	}

	AddressDTO geocodeAdapterResponse;

	private void setupJSONPain() throws Exception {
		when(parent.getBundle()).thenReturn(Sets.newHashSet(child1, child2));
	}

	@Test
	public void setBundleAddress_AllLocations() throws Exception {
		setupJSONPain();
		when(child1.isSetOnsiteAddress()).thenReturn(true);
		when(child2.isSetOnsiteAddress()).thenReturn(true);
		when(child1.getAddress()).thenReturn(address1);
		when(child2.getAddress()).thenReturn(address2);

		workBundleService.updateBundleCalculatedValues(parent);

		// confirm address is set on bundle
		verify(locationDAO).saveOrUpdate(any(Location.class));
		verify(parent).setIsOnsiteAddress(true);
	}

	@Test
	public void setBundleAddress_MixLocationAndVirtual() throws Exception {
		setupJSONPain();
		when(child1.isSetOnsiteAddress()).thenReturn(true);
		when(child2.isSetOnsiteAddress()).thenReturn(false);
		when(child1.getAddress()).thenReturn(address1);
		when(child2.getAddress()).thenReturn(null);

		workBundleService.updateBundleCalculatedValues(parent);

		// confirm that only address1 used for lookup
		verify(locationService).reverseLookup(address1.getLatitude().doubleValue(), address1.getLongitude().doubleValue());
		verify(parent).setIsOnsiteAddress(true);
	}

	@Test
	public void setBundleAddress_AllVirtual() throws Exception {
		setupJSONPain();
		when(child1.isSetOnsiteAddress()).thenReturn(false);
		when(child2.isSetOnsiteAddress()).thenReturn(false);
		when(child1.getAddress()).thenReturn(null);
		when(child2.getAddress()).thenReturn(null);

		workBundleService.updateBundleCalculatedValues(parent);

		verify(parent).setIsOnsiteAddress(false);
	}

	/**
	 * Test that when we call workBundleService.updateBundleCalculatedValues,
	 * we save a DTO with an AddressTypeCode.
	 */
	@Test
	public void setBundleAddress_SetsAddressTypeCode() throws Exception {
		updateBundleCompleteSetup();
		when(child1.isSetOnsiteAddress()).thenReturn(true);
		when(child2.isSetOnsiteAddress()).thenReturn(true);
		when(child1.getAddress()).thenReturn(address1);
		when(child2.getAddress()).thenReturn(address2);

		workBundleService.updateBundleCalculatedValues(parent);

		ArgumentCaptor<AddressDTO> dtoCaptor = ArgumentCaptor.forClass(AddressDTO.class);
		verify(addressService).saveOrUpdate(dtoCaptor.capture());

		assertEquals(AddressType.ASSIGNMENT, dtoCaptor.getValue().getAddressTypeCode());
	}

	/**
	 * Test that when we call workBundleService.updateBundleCalculatedValues,
	 * we save a DTO with a DressCodeId.
	 */
	@Test
	public void setBundleAddress_SetsDressCodeId() throws Exception {
		updateBundleCompleteSetup();
		when(child1.isSetOnsiteAddress()).thenReturn(true);
		when(child2.isSetOnsiteAddress()).thenReturn(true);
		when(child1.getAddress()).thenReturn(address1);
		when(child2.getAddress()).thenReturn(address2);

		workBundleService.updateBundleCalculatedValues(parent);

		ArgumentCaptor<AddressDTO> dtoCaptor = ArgumentCaptor.forClass(AddressDTO.class);
		verify(addressService).saveOrUpdate(dtoCaptor.capture());

		assertEquals((Long)DressCode.BUSINESS_CASUAL, dtoCaptor.getValue().getDressCodeId());
	}

	/**
	 * Test that when we call workBundleService.updateBundleCalculatedValues,
	 * we save a DTO with a DressCodeId.
	 */
	@Test
	public void setBundleAddress_SetsLocationTypeId() throws Exception {
		updateBundleCompleteSetup();
		when(child1.isSetOnsiteAddress()).thenReturn(true);
		when(child2.isSetOnsiteAddress()).thenReturn(true);
		when(child1.getAddress()).thenReturn(address1);
		when(child2.getAddress()).thenReturn(address2);

		workBundleService.updateBundleCalculatedValues(parent);

		ArgumentCaptor<AddressDTO> dtoCaptor = ArgumentCaptor.forClass(AddressDTO.class);
		verify(addressService).saveOrUpdate(dtoCaptor.capture());

		assertEquals(LocationType.COMMERCIAL_CODE, dtoCaptor.getValue().getLocationTypeId());
	}

	@Test(expected = IllegalArgumentException.class)
	public void isAssignmentBundle_Null() {
		Long id = null;
		workBundleService.isAssignmentBundle(id);
	}

	@Test
	public void isAssignmentBundle_NullWork() {
		AbstractWork work = null;
		assertEquals(false, workBundleService.isAssignmentBundle(work));
	}

	@Test
	public void isAssignmentBundle_Work_False() {
		AbstractWork work = new Work();
		assertEquals(false, workBundleService.isAssignmentBundle(work));
	}

	@Test
	public void isAssignmentBundle_WorkBundle_True() {
		AbstractWork work = new WorkBundle();
		assertEquals(true, workBundleService.isAssignmentBundle(work));
	}

	@Test
	public void isAssignmentBundle_Long_True() {
		WorkBundle bundle = mock(WorkBundle.class);
		when(workService.findWork(1L, false)).thenReturn(bundle);
		assertEquals(true, workBundleService.isAssignmentBundle(1L));
	}

	@Test
	public void isAssignmentBundle_String_True() {
		WorkBundle bundle = mock(WorkBundle.class);
		when(workService.findWorkByWorkNumber("1", false)).thenReturn(bundle);
		assertEquals(true, workBundleService.isAssignmentBundle("1"));
	}

	@Test
	public void findById_Null() {
		when(workBundleDAO.get(any(Long.class))).thenReturn(null);
		WorkBundle workBundle = workBundleService.findById(1L);
		assertEquals(null, workBundle);
	}

	@Test
	public void findAllDraftBundles() {
		when(userService.findUserById(1L)).thenReturn(user);
		when(company.getId()).thenReturn(2L);
		List<WorkBundle> expected = Lists.newArrayList();
		when(workBundleDAO.findAllBy("workStatusType.code", WorkStatusType.DRAFT, "buyerCompany.id", 2L)).thenReturn(expected);

		List<WorkBundle> actual = workBundleService.findAllDraftBundles(1L);

		assertEquals(expected, actual);
	}

	@Test
	public void getBundleAddresses_NullChildren() {
		List<Address> address = workBundleService.getBundleAddresses(parent);
		assertEquals(null, address);
	}

	@Test(expected = Exception.class)
	public void validateBundledWorkForAdd_NullDrafts_Boom() {
		workBundleService.validateAllBundledWorkForAdd(null, null);
	}

	@Test
	public void validateBundledWorkForAdd_AllDrafts_AllNotInBundle_AllSuccess() {
		List<ValidateWorkResponse> results = workBundleService.validateAllBundledWorkForAdd(workNumbers, null);

		for (ValidateWorkResponse validateWorkResponse : results) {
			assertTrue(validateWorkResponse.isSuccessful());
		}
	}

	@Test
	public void validateBundledWorkForAdd_NotAllDrafts_AllNotInBundle_MixedSuccess() {
		when(child2.isDraft()).thenReturn(false);
		when(child2.getWorkStatusType()).thenReturn(workStatusType);

		List<ValidateWorkResponse> results = workBundleService.validateAllBundledWorkForAdd(workNumbers, null);

		Group<ValidateWorkResponse> validationStatusGroup = group(results, by(on(ValidateWorkResponse.class).isSuccessful()));
		Collection<ValidateWorkResponse> validationErrors = validationStatusGroup.find(false);

		assertEquals(1, validationErrors.size());
	}

	@Test
	public void validateBundledWorkForAdd_AllDrafts_NotAllNotInBundle_MixedSuccess() {
		when(child2.isInBundle()).thenReturn(true);
		when(child2.getParent()).thenReturn(parent);

		List<ValidateWorkResponse> results = workBundleService.validateAllBundledWorkForAdd(workNumbers, null);

		Group<ValidateWorkResponse> validationStatusGroup = group(results, by(on(ValidateWorkResponse.class).isSuccessful()));
		Collection<ValidateWorkResponse> validationErrors = validationStatusGroup.find(false);

		assertEquals(1, validationErrors.size());
	}

	@Test(expected = IllegalArgumentException.class)
	public void updateBundleComplete_No_Id_BOOM() {
		workBundleService.updateBundleComplete(null);
	}

	@Test(expected = IllegalArgumentException.class)
	public void updateBundleComplete_No_Bundle_BOOM() {
		when(workBundleService.findById(900L)).thenReturn(null);

		workBundleService.updateBundleComplete(900L);
	}

	@Test
	public void updateBundleComplete_All_Cancelled_COMPLETE() {
		updateBundleCompleteSetup();
		when(child1.getWorkStatusType()).thenReturn(new WorkStatusType(WorkStatusType.CANCELLED));
		when(child2.getWorkStatusType()).thenReturn(new WorkStatusType(WorkStatusType.CANCELLED));

		assertTrue(workBundleService.updateBundleComplete(parent.getId()));
	}

	@Test
	public void updateBundleComplete_All_Cancelled_With_Pay_COMPLETE() {
		updateBundleCompleteSetup();
		when(child1.getWorkStatusType()).thenReturn(new WorkStatusType(WorkStatusType.CANCELLED_WITH_PAY));
		when(child2.getWorkStatusType()).thenReturn(new WorkStatusType(WorkStatusType.CANCELLED_WITH_PAY));

		assertTrue(workBundleService.updateBundleComplete(parent.getId()));
	}

	@Test
	public void updateBundleComplete_All_Deleted_COMPLETE() {
		updateBundleCompleteSetup();
		when(child1.getWorkStatusType()).thenReturn(new WorkStatusType(WorkStatusType.DELETED));
		when(child2.getWorkStatusType()).thenReturn(new WorkStatusType(WorkStatusType.DELETED));

		assertTrue(workBundleService.updateBundleComplete(parent.getId()));
	}

	@Test
	public void updateBundleComplete_All_Paid_COMPLETE() {
		updateBundleCompleteSetup();
		when(child1.getWorkStatusType()).thenReturn(new WorkStatusType(WorkStatusType.PAID));
		when(child2.getWorkStatusType()).thenReturn(new WorkStatusType(WorkStatusType.PAID));

		assertTrue(workBundleService.updateBundleComplete(parent.getId()));
	}

	@Test
	public void updateBundleComplete_All_Void_COMPLETE() {
		updateBundleCompleteSetup();
		when(child1.getWorkStatusType()).thenReturn(new WorkStatusType(WorkStatusType.VOID));
		when(child2.getWorkStatusType()).thenReturn(new WorkStatusType(WorkStatusType.VOID));

		assertTrue(workBundleService.updateBundleComplete(parent.getId()));
	}

	@Test
	public void updateBundleComplete_Mixed_COMPLETE() {
		updateBundleCompleteSetup();
		when(child1.getWorkStatusType()).thenReturn(new WorkStatusType(WorkStatusType.VOID));
		when(child2.getWorkStatusType()).thenReturn(new WorkStatusType(WorkStatusType.PAID));

		assertTrue(workBundleService.updateBundleComplete(parent.getId()));
	}

	@Test
	public void updateBundleComplete_Mixed_NOT_COMPLETE() {
		updateBundleCompleteSetup();
		when(child1.getWorkStatusType()).thenReturn(new WorkStatusType(WorkStatusType.ACTIVE));
		when(child2.getWorkStatusType()).thenReturn(new WorkStatusType(WorkStatusType.PAID));

		assertFalse(workBundleService.updateBundleComplete(parent.getId()));
	}

	@Test
	public void updateBundleComplete_empty_violations_COMPLETE() {
		updateBundleCompleteSetup();
		when(child1.getWorkStatusType()).thenReturn(new WorkStatusType(WorkStatusType.PAID));
		when(child2.getWorkStatusType()).thenReturn(new WorkStatusType(WorkStatusType.PAID));

		List<ConstraintViolation> violations = Lists.newArrayList();
		when(workService.completeWork(eq(parent.getId()), any(CompleteWorkDTO.class))).thenReturn(violations);

		assertTrue(workBundleService.updateBundleComplete(parent.getId()));
	}

	@Test
	public void updateBundleComplete_null_violations_COMPLETE() {
		updateBundleCompleteSetup();
		when(child1.getWorkStatusType()).thenReturn(new WorkStatusType(WorkStatusType.PAID));
		when(child2.getWorkStatusType()).thenReturn(new WorkStatusType(WorkStatusType.PAID));

		when(workService.completeWork(eq(parent.getId()), any(CompleteWorkDTO.class))).thenReturn(null);

		assertTrue(workBundleService.updateBundleComplete(parent.getId()));
	}

	@Test
	public void updateBundleComplete_violations_NOT_COMPLETE() {
		updateBundleCompleteSetup();
		when(child1.getWorkStatusType()).thenReturn(new WorkStatusType(WorkStatusType.PAID));
		when(child2.getWorkStatusType()).thenReturn(new WorkStatusType(WorkStatusType.PAID));

		List<ConstraintViolation> violations = Lists.newArrayList();
		ConstraintViolation constraintViolation = mock(ConstraintViolation.class);
		violations.add(constraintViolation);
		when(workService.completeWork(eq(parent.getId()), any(CompleteWorkDTO.class))).thenReturn(violations);

		assertFalse(workBundleService.updateBundleComplete(parent.getId()));
	}

	@Test(expected = IllegalArgumentException.class)
	public void isBundleComplete_No_Id_BOOM() {
		workBundleService.isBundleComplete(null);
	}

	@Test(expected = IllegalArgumentException.class)
	public void isBundleComplete_No_Bundle_BOOM() {
		when(workBundleService.findById(900L)).thenReturn(null);

		workBundleService.isBundleComplete(900L);
	}

	@Test
	public void isBundleComplete_FALSE() {
		when(parent.getWorkStatusType()).thenReturn(new WorkStatusType(WorkStatusType.ACTIVE));
		when(workBundleService.findById(parent.getId())).thenReturn(parent);

		assertFalse(workBundleService.isBundleComplete(parent.getId()));
	}

	@Test
	public void isBundleComplete_TRUE() {
		when(parent.getWorkStatusType()).thenReturn(new WorkStatusType(WorkStatusType.COMPLETE));
		when(workBundleService.findById(parent.getId())).thenReturn(parent);

		assertTrue(workBundleService.isBundleComplete(parent.getId()));
	}

	@Test(expected = IllegalArgumentException.class)
	public void updateBundleVoid_No_Id_BOOM() {
		workBundleService.updateBundleVoid(null);
	}

	@Test(expected = IllegalArgumentException.class)
	public void updateBundleVoid_No_Bundle_BOOM() {
		when(workBundleService.findById(900L)).thenReturn(null);

		workBundleService.updateBundleVoid(900L);
	}

	@Test
	public void updateBundleVoid_Already_Void_TRUE() {
		when(workStatusType.getCode()).thenReturn(WorkStatusType.VOID);
		when(parent.getWorkStatusType()).thenReturn(workStatusType);
		when(workBundleService.findById(parent.getId())).thenReturn(parent);

		assertTrue(workBundleService.updateBundleVoid(parent.getId()));
	}

	@Test
	public void updateBundleVoid_Not_DRAFT_OR_SENT_FALSE() {
		updateBundleCompleteSetup();
		when(workStatusType.getCode()).thenReturn(WorkStatusType.ACTIVE);

		assertFalse(workBundleService.updateBundleVoid(parent.getId()));
	}

	@Test
	public void updateBundleVoid_Not_Empty_DRAFT_FALSE() {
		updateBundleCompleteSetup();
		when(workStatusType.getCode()).thenReturn(WorkStatusType.DRAFT);

		assertFalse(workBundleService.updateBundleVoid(parent.getId()));
	}

	@Test
	public void updateBundleVoid_Empty_DRAFT_Violations_FALSE() {
		when(workStatusType.getCode()).thenReturn(WorkStatusType.DRAFT);
		when(parent.getWorkStatusType()).thenReturn(workStatusType);
		when(workBundleService.findById(parent.getId())).thenReturn(parent);

		List<ConstraintViolation> violations = Lists.newArrayList();
		violations.add(new ConstraintViolation("blarg", "blarg"));
		when(workService.voidWork(parent.getId(), "Empty Bundle")).thenReturn(violations);

		assertFalse(workBundleService.updateBundleVoid(parent.getId()));
	}

	@Test
	public void updateBundleVoid_Empty_DRAFT_No_Violations_TRUE() {
		when(workStatusType.getCode()).thenReturn(WorkStatusType.DRAFT);
		when(parent.getWorkStatusType()).thenReturn(workStatusType);
		when(workBundleService.findById(parent.getId())).thenReturn(parent);

		assertTrue(workBundleService.updateBundleVoid(parent.getId()));
	}

	@Test
	public void updateBundleVoid_Not_Empty_SENT_FALSE() {
		updateBundleCompleteSetup();
		when(workStatusType.getCode()).thenReturn(WorkStatusType.SENT);

		assertFalse(workBundleService.updateBundleVoid(parent.getId()));
	}

	@Test
	public void updateBundleVoid_Empty_SENT_Violations_FALSE() {
		when(workStatusType.getCode()).thenReturn(WorkStatusType.SENT);
		when(parent.getWorkStatusType()).thenReturn(workStatusType);
		when(workBundleService.findById(parent.getId())).thenReturn(parent);

		List<ConstraintViolation> violations = Lists.newArrayList();
		violations.add(new ConstraintViolation("blarg", "blarg"));
		when(workService.voidWork(parent.getId(), "Empty Bundle")).thenReturn(violations);

		assertFalse(workBundleService.updateBundleVoid(parent.getId()));
	}

	@Test
	public void updateBundleVoid_Empty_SENT_No_Violations_TRUE() {
		when(workStatusType.getCode()).thenReturn(WorkStatusType.SENT);
		when(parent.getWorkStatusType()).thenReturn(workStatusType);
		when(workBundleService.findById(parent.getId())).thenReturn(parent);

		assertTrue(workBundleService.updateBundleVoid(parent.getId()));
	}

	@Test(expected = IllegalArgumentException.class)
	public void isBundleVoid_No_Id_BOOM() {
		workBundleService.isBundleVoid(null);
	}

	@Test(expected = IllegalArgumentException.class)
	public void isBundleVoid_No_Bundle_BOOM() {
		when(workBundleService.findById(900L)).thenReturn(null);

		workBundleService.isBundleVoid(900L);
	}

	@Test
	public void isBundleVoid_FALSE() {
		when(parent.getWorkStatusType()).thenReturn(new WorkStatusType(WorkStatusType.ACTIVE));
		when(workBundleService.findById(parent.getId())).thenReturn(parent);

		assertFalse(workBundleService.isBundleVoid(parent.getId()));
	}

	@Test
	public void isBundleVoid_TRUE() {
		when(parent.getWorkStatusType()).thenReturn(new WorkStatusType(WorkStatusType.VOID));
		when(workBundleService.findById(parent.getId())).thenReturn(parent);

		assertTrue(workBundleService.isBundleVoid(parent.getId()));
	}

	@Test
	public void updateBundleCalculatedValues_PaymentTermsEnabled_Matches() {
		updateBundleCompleteSetup();

		assertFalse(parent.getManageMyWorkMarket().getPaymentTermsEnabled() == child1.getManageMyWorkMarket().getPaymentTermsEnabled());
		workBundleService.updateBundleCalculatedValues(parent.getId());
		assertTrue(parent.getManageMyWorkMarket().getPaymentTermsEnabled() == child1.getManageMyWorkMarket().getPaymentTermsEnabled());
	}

	@Test
	public void updateBundleCalculatedValues_PaymentTermsOverride_Matches() {
		updateBundleCompleteSetup();

		assertFalse(parent.getManageMyWorkMarket().getPaymentTermsOverride() == child1.getManageMyWorkMarket().getPaymentTermsOverride());
		workBundleService.updateBundleCalculatedValues(parent.getId());
		assertTrue(parent.getManageMyWorkMarket().getPaymentTermsOverride() == child1.getManageMyWorkMarket().getPaymentTermsOverride());
	}

	@Test
	public void updateBundleCalculatedValues_PaymentTermsDays_Matches() {
		updateBundleCompleteSetup();

		assertFalse(parent.getManageMyWorkMarket().getPaymentTermsDays().equals(child1.getManageMyWorkMarket().getPaymentTermsDays()));
		workBundleService.updateBundleCalculatedValues(parent.getId());
		assertTrue(parent.getManageMyWorkMarket().getPaymentTermsDays().equals(child1.getManageMyWorkMarket().getPaymentTermsDays()));
	}

	@Test
	public void updateBundleCalculatedValues_AutoPayEnabled_Matches() {
		updateBundleCompleteSetup();

		assertFalse(parent.getManageMyWorkMarket().getAutoPayEnabled() == child1.getManageMyWorkMarket().getAutoPayEnabled());
		workBundleService.updateBundleCalculatedValues(parent.getId());
		assertTrue(parent.getManageMyWorkMarket().getAutoPayEnabled() == child1.getManageMyWorkMarket().getAutoPayEnabled());
	}

	@Test
	public void saveOrUpdateWorkBundle_PaymentTermsEnabled_Matches() {
		updateBundleCompleteSetup();

		assertFalse(parent.getManageMyWorkMarket().getPaymentTermsEnabled() == child1.getManageMyWorkMarket().getPaymentTermsEnabled());
		workBundleService.saveOrUpdateWorkBundle(1L, workBundleDTO);
		assertTrue(parent.getManageMyWorkMarket().getPaymentTermsEnabled() == child1.getManageMyWorkMarket().getPaymentTermsEnabled());
	}

	@Test
	public void saveOrUpdateWorkBundle_PaymentTermsOverride_Matches() {
		updateBundleCompleteSetup();

		assertFalse(parent.getManageMyWorkMarket().getPaymentTermsOverride() == child1.getManageMyWorkMarket().getPaymentTermsOverride());
		workBundleService.saveOrUpdateWorkBundle(1L, workBundleDTO);
		assertTrue(parent.getManageMyWorkMarket().getPaymentTermsOverride() == child1.getManageMyWorkMarket().getPaymentTermsOverride());
	}

	@Test
	public void saveOrUpdateWorkBundle_PaymentTermsDays_Matches() {
		updateBundleCompleteSetup();

		assertFalse(parent.getManageMyWorkMarket().getPaymentTermsDays().equals(child1.getManageMyWorkMarket().getPaymentTermsDays()));
		workBundleService.saveOrUpdateWorkBundle(1L, workBundleDTO);
		assertTrue(parent.getManageMyWorkMarket().getPaymentTermsDays().equals(child1.getManageMyWorkMarket().getPaymentTermsDays()));
	}

	@Test
	public void saveOrUpdateWorkBundle_AutoPayEnabled_Matches() {
		updateBundleCompleteSetup();

		assertFalse(parent.getManageMyWorkMarket().getAutoPayEnabled() == child1.getManageMyWorkMarket().getAutoPayEnabled());
		workBundleService.saveOrUpdateWorkBundle(1L, workBundleDTO);
		assertTrue(parent.getManageMyWorkMarket().getAutoPayEnabled() == child1.getManageMyWorkMarket().getAutoPayEnabled());
	}

	private void updateBundleCompleteSetup() {
		when(child2.isInBundle()).thenReturn(true);
		when(child2.getParent()).thenReturn(parent);

		when(child1.isInBundle()).thenReturn(true);
		when(child1.getParent()).thenReturn(parent);

		when(parent.getBundle()).thenReturn(Sets.newHashSet(child1, child2));
		when(parent.getWorkStatusType()).thenReturn(workStatusType);

		when(workBundleService.findById(parent.getId())).thenReturn(parent);
		when(workService.findWork(parent.getId())).thenReturn(parent);
	}
}
