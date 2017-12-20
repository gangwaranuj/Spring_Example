package com.workmarket.domains.work.service;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.workmarket.domains.groups.dao.WorkGroupAssociationDAO;
import com.workmarket.domains.groups.model.UserGroup;
import com.workmarket.domains.groups.model.WorkGroupAssociation;
import com.workmarket.domains.model.Address;
import com.workmarket.domains.model.AddressType;
import com.workmarket.domains.model.Company;
import com.workmarket.domains.model.DateRange;
import com.workmarket.domains.model.User;
import com.workmarket.domains.model.WorkResource;
import com.workmarket.domains.model.WorkResourcePagination;
import com.workmarket.domains.model.WorkResourceStatusType;
import com.workmarket.domains.model.WorkStatusType;
import com.workmarket.domains.model.audit.ViewType;
import com.workmarket.domains.model.changelog.work.WorkNotifyChangeLog;
import com.workmarket.domains.model.crm.ClientContact;
import com.workmarket.domains.model.lane.LaneAssociation;
import com.workmarket.domains.model.pricing.FlatPricePricingStrategy;
import com.workmarket.domains.model.pricing.FullPricingStrategy;
import com.workmarket.domains.model.pricing.PricingStrategy;
import com.workmarket.domains.model.user.PersonaPreference;
import com.workmarket.domains.model.validation.ConstraintViolation;
import com.workmarket.domains.velvetrope.service.AdmissionService;
import com.workmarket.domains.work.facade.service.WorkFacadeService;
import com.workmarket.domains.work.model.CancellationReasonType;
import com.workmarket.domains.work.model.Work;
import com.workmarket.domains.work.model.WorkBundle;
import com.workmarket.domains.work.model.WorkRecurrenceAssociation;
import com.workmarket.domains.work.model.WorkResourceLabelType;
import com.workmarket.domains.work.model.WorkResourceTimeTracking;
import com.workmarket.domains.work.model.negotiation.WorkBonusNegotiation;
import com.workmarket.domains.work.model.negotiation.WorkNegotiation;
import com.workmarket.domains.work.model.negotiation.WorkRescheduleNegotiation;
import com.workmarket.domains.work.model.state.WorkSubStatusType;
import com.workmarket.domains.work.model.state.WorkSubStatusTypeAssociation;
import com.workmarket.domains.work.service.audit.WorkActionRequest;
import com.workmarket.domains.work.service.resource.WorkAuthorizationResponse;
import com.workmarket.domains.work.service.route.WorkBundleRouting;
import com.workmarket.domains.work.service.workresource.WorkResourceService;
import com.workmarket.dto.AddressDTO;
import com.workmarket.dto.CompanyResource;
import com.workmarket.dto.CompanyResourcePagination;
import com.workmarket.helpers.WMCallable;
import com.workmarket.service.business.BaseServiceIT;
import com.workmarket.service.business.dto.CancelWorkDTO;
import com.workmarket.service.business.dto.CloseWorkDTO;
import com.workmarket.service.business.dto.CompleteWorkDTO;
import com.workmarket.service.business.dto.ManageMyWorkMarketDTO;
import com.workmarket.service.business.dto.RatingDTO;
import com.workmarket.service.business.dto.UnassignDTO;
import com.workmarket.service.business.dto.WorkDTO;
import com.workmarket.service.business.dto.WorkNegotiationDTO;
import com.workmarket.service.business.dto.WorkResourceDetailPagination;
import com.workmarket.service.business.dto.WorkResourceLabelDTO;
import com.workmarket.service.business.wrapper.AcceptWorkResponse;
import com.workmarket.service.business.wrapper.WorkNegotiationResponse;
import com.workmarket.test.IntegrationTest;
import com.workmarket.thrift.work.TimeTrackingRequest;
import com.workmarket.thrift.work.TimeTrackingResponse;
import com.workmarket.utility.CollectionUtilities;
import com.workmarket.utility.DateUtilities;
import com.workmarket.utility.HibernateUtilities;
import com.workmarket.utility.RandomUtilities;
import com.workmarket.velvetrope.Venue;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import static com.jayway.awaitility.Awaitility.await;
import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

@RunWith(SpringJUnit4ClassRunner.class)
@Category(IntegrationTest.class)
public class WorkServiceIT extends BaseServiceIT {

	@Autowired private WorkNegotiationService workNegotiationService;
	@Autowired private WorkService workService;
	@Autowired private WorkFacadeService workFacadeService;
	@Autowired private WorkResourceService workResourceService;
	@Autowired private WorkGroupAssociationDAO workGroupAssociationDAO;
	@Autowired private WorkChangeLogService workChangeLogService;
	@Autowired private AdmissionService admissionService;
	@Autowired private WorkBundleRouting workBundleRouting;

	private User employee;
	private User contractor;
	private Work work;
	private static final boolean assignToFirstToAccept = false;

	private void initializeVars() throws Exception {
		employee = newFirstEmployeeWithCashBalance();
		contractor = newContractor();
		work = newWork(employee.getId());
		assertNotNull(work);

		laneService.addUserToCompanyLane2(contractor.getId(), employee.getCompany().getId());

		workRoutingService.openWork(work.getWorkNumber());
		routingStrategyService.addUserNumbersRoutingStrategy(work.getId(), Sets.newHashSet(contractor.getUserNumber()), 0, assignToFirstToAccept);

		AcceptWorkResponse response = workService.acceptWork(contractor.getId(), work.getId());
		assertTrue(response.isSuccessful());
	}

	@Test
	public void shouldAllowWorkNotifyIfNoChangeLog() throws Exception {
		initializeVars();
		work = getSentWork();
		assertTrue(workService.isWorkNotifyAllowed(work.getId()));
	}

	@Test
	public void shouldNotAllowWorkNotifyIfChangeLogInsideWindow() throws Exception {
		initializeVars();
		work = getSentWork();
		workChangeLogService.saveWorkChangeLog(new WorkNotifyChangeLog(work.getId(), 1L, 1L, 1L));
		assertFalse(workService.isWorkNotifyAllowed(work.getId()));
	}

	@Test
	public void checkoutWithNote_pass() throws Exception {
		initializeVars();

		TimeTrackingResponse checkinResponse= createCheckin(work.getId());
		assertNotNull(checkinResponse);
		assertNotNull(checkinResponse.getTimeTracking());

		String noteText = "This is the note";

		TimeTrackingRequest timeTrackingRequest = new TimeTrackingRequest().setWorkId(work.getId())
				.setDate(new DateTime().toGregorianCalendar())
				.setDistance(10.0)
				.setTimeTrackingId(checkinResponse.getTimeTracking().getId())
				.setNoteOnCheckOut(noteText);

		TimeTrackingResponse checkoutResponse = workService.checkOutActiveResource(timeTrackingRequest);
		assertNotNull(checkoutResponse);

		assertTrue(checkoutResponse.isSuccessful());
		assertEquals(checkoutResponse.getTimeTracking().getNote().getContent(), noteText);
	}

	@Test
	public void checkoutWithEmptyNote_pass() throws Exception {
		initializeVars();

		TimeTrackingResponse checkinResponse= createCheckin(work.getId());
		assertNotNull(checkinResponse);
		assertNotNull(checkinResponse.getTimeTracking());

		String noteText = "   \t \n";

		TimeTrackingResponse checkoutResponse = createCheckout(work.getId(), checkinResponse.getTimeTracking().getId(), noteText);
		assertNotNull(checkoutResponse);

		assertTrue(checkoutResponse.isSuccessful());
		assertNotNull(checkoutResponse.getTimeTracking());
		assertNull(checkoutResponse.getTimeTracking().getNote());
	}

	@Test
	public void checkoutWithoutNote_pass() throws Exception {
		initializeVars();

		TimeTrackingResponse checkinResponse= createCheckin(work.getId());
		assertNotNull(checkinResponse);
		assertNotNull(checkinResponse.getTimeTracking());

		TimeTrackingResponse checkoutResponse = createCheckout(work.getId(), checkinResponse.getTimeTracking().getId(), null);
		assertNotNull(checkoutResponse);

		assertTrue(checkoutResponse.isSuccessful());
		assertNotNull(checkoutResponse.getTimeTracking());
		assertNull(checkoutResponse.getTimeTracking().getNote());
	}

	@Test
	public void checkoutWithoutNote_fail() throws Exception {
		initializeVars();

		WorkDTO workDTO = new WorkDTO();
		workDTO.setCheckoutNoteRequired(true);
		Work work1 = newWork(employee.getId(), workDTO);

		assertNotNull(work1);
		workRoutingService.openWork(work1.getWorkNumber());
		routingStrategyService.addUserNumbersRoutingStrategy(work1.getId(), Sets.newHashSet(contractor.getUserNumber()), 0, assignToFirstToAccept);
		workService.acceptWork(contractor.getId(), work1.getId());

		TimeTrackingResponse checkinResponse= createCheckin(work1.getId());
		assertNotNull(checkinResponse);
		assertNotNull(checkinResponse.getTimeTracking());

		TimeTrackingResponse checkoutResponse = createCheckout(work1.getId(), checkinResponse.getTimeTracking().getId(), null);
		assertNotNull(checkoutResponse);

		assertFalse(checkoutResponse.isSuccessful());
	}


	@Test
	public void checkoutWithEmptyNote_fail() throws Exception {
		initializeVars();

		WorkDTO workDTO = new WorkDTO();
		workDTO.setCheckoutNoteRequired(true);
		Work work1 = newWork(employee.getId(), workDTO);
		assertNotNull(work1);

		workRoutingService.openWork(work1.getWorkNumber());
		routingStrategyService.addUserNumbersRoutingStrategy(work1.getId(), Sets.newHashSet(contractor.getUserNumber()), 0, assignToFirstToAccept);
		workService.acceptWork(contractor.getId(), work1.getId());

		TimeTrackingResponse checkinResponse = createCheckin(work1.getId());
		assertNotNull(checkinResponse);
		assertNotNull(checkinResponse.getTimeTracking());

		String noteText = "   \t \n";

		TimeTrackingResponse checkoutResponse = createCheckout(work1.getId(), checkinResponse.getTimeTracking().getId(), noteText);
		assertNotNull(checkoutResponse);

		assertFalse(checkoutResponse.isSuccessful());
	}

	@Test
	public void checkinTwiceWithoutCheckout_notAllowed() throws Exception {
		initializeVars();

		TimeTrackingResponse checkinResponse= createCheckin(work.getId());
		assertNotNull(checkinResponse);
		assertNotNull(checkinResponse.getTimeTracking());
		assertTrue(checkinResponse.isSuccessful());

		TimeTrackingResponse checkinResponseAgain= createCheckin(work.getId());
		assertNotNull(checkinResponseAgain);
		assertFalse(checkinResponseAgain.isSuccessful());
	}

	@Test
	public void findWork() throws Exception {
		User employee = newFirstEmployeeWithCashBalance();
		Work testWork = newWork(employee.getId());
		Work work = workService.findWork(testWork.getId());

		assertNotNull(work);
	}

	@Test
	public void findWorkIdsForBuyer() throws Exception {
		User employee = newFirstEmployeeWithCashBalance();
		newWork(employee.getId());
		List<Long> workIds = workService.findWorkIdsByBuyerAndStatus(employee.getId(), "draft", "active");

		assertNotNull(workIds);
		assertFalse(workIds.isEmpty());
	}

	@Test
	public void createWork() throws Exception {
		User user = newFirstEmployeeWithCashBalance();

		WorkDTO workDTO = new WorkDTO();
		workDTO.setTitle("Do Work!");
		workDTO.setDescription("Description of work.");
		workDTO.setIsOnsiteAddress(false);
		workDTO.setPricingStrategyId(FLAT_PRICING_STRATEGY);
		workDTO.setFlatPrice(100.00);
		workDTO.setIsScheduleRange(false);
		workDTO.setScheduleFromString("2010-09-02T09:00:00Z");
		workDTO.setIvrActive(true);
		workDTO.setCheckinCallRequired(true);

		Work work = workFacadeService.saveOrUpdateWork(user.getId(), workDTO);

		assertNotNull(work);
		assertTrue(work.getId() > 0);
		assertTrue(work.isCheckinCallRequired());

		assertNull(work.getAddress());

		Calendar from = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
		from.set(2010, 8, 2, 9, 0, 0);
		from.set(Calendar.MILLISECOND, 0);

		assertEquals(from.getTime(), work.getScheduleFrom().getTime());

		assertTrue(work.isIvrActive());
	}

	@Test
	public void addToWorkResources_withOneResource_success() throws Exception {
		User user = newFirstEmployeeWithCashBalance();
		User resource = newContractor();
		laneService.addUserToCompanyLane2(resource.getId(), user.getCompany().getId());

		WorkDTO workDTO = new WorkDTO();
		workDTO.setTitle("Do Work!");
		workDTO.setDescription("Description of work.");
		workDTO.setIsOnsiteAddress(false);
		workDTO.setPricingStrategyId(FLAT_PRICING_STRATEGY);
		workDTO.setFlatPrice(100.00);
		workDTO.setIsScheduleRange(false);
		workDTO.setScheduleFromString("2010-09-02T09:00:00Z");

		Work work = workFacadeService.saveOrUpdateWork(user.getId(), workDTO);
		workRoutingService.addToWorkResources(work.getId(), resource.getId());
		assertNotNull(work);
		assertTrue(work.getId() > 0);

		assertNull(work.getAddress());

		assertTrue(workService.isUserWorkResourceForWork(resource.getId(), work.getId()));
	}

	@Test
	public void createOnsiteWork() throws Exception {

		User user = newFirstEmployeeWithCashBalance();

		WorkDTO workDTO = new WorkDTO();
		workDTO.setTitle("Do Work!");
		workDTO.setDescription("Description of work.");
		workDTO.setIsOnsiteAddress(true);
		workDTO.setPricingStrategyId(FLAT_PRICING_STRATEGY);
		workDTO.setFlatPrice(100.00);
		workDTO.setIsScheduleRange(false);
		workDTO.setScheduleFromString("2010-09-02T09:00:00Z");
		workDTO.setBuyerSupportUserId(ANONYMOUS_USER_ID);
		workDTO.setServiceClientContactId(CLIENT_CONTACT_ID);
		workDTO.setRequireTimetracking(true);

		workDTO.setAddressId(ADDRESS_ID);

		Work work = workFacadeService.saveOrUpdateWork(user.getId(), workDTO);

		ClientContact c = crmService.findClientContactById(workDTO.getServiceClientContactId());
		assertNotNull(c);

		assertNotNull(work);
		assertTrue(work.getId() > 0);

		assertNotNull(work.getAddress());
		assertTrue(work.getAddress().getId() == 1);

		assertTrue(work.getBuyerSupportUser().getId().equals(ANONYMOUS_USER_ID));
		assertTrue(work.isRequireTimetracking());

		assertFalse(work.isClientLocationAddress());

		assertNull(work.getLocation());
		assertNull(work.getClientCompany());

		Calendar from = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
		from.set(2010, 8, 2, 9, 0, 0);
		from.set(Calendar.MILLISECOND, 0);

		assertEquals(from.getTime(), work.getScheduleFrom().getTime());
		assertNotNull(work.getCompany());

		// change the strategy to offsiteWork
		workDTO.setId(work.getId());
		workDTO.setTitle("Do Work!");
		workDTO.setDescription("Description of work.");
		workDTO.setIsOnsiteAddress(false);
		workDTO.setPricingStrategyId(FLAT_PRICING_STRATEGY);
		workDTO.setFlatPrice(100.00);
		workDTO.setIsScheduleRange(false);
		workDTO.setScheduleFromString("2010-09-02T09:00:00Z");

		work = workFacadeService.saveOrUpdateWork(user.getId(), workDTO);

		assertNotNull(work);
		assertNull(work.getAddress());
		assertFalse(work.isClientLocationAddress());
		assertNull(work.getLocation());
	}

	@Test
	public void repriceWork() throws Exception {
		User user = newFirstEmployeeWithCashBalance();
		Work work = newWork(user.getId());

		WorkDTO workDTO = new WorkDTO();
		workDTO.setPricingStrategyId(FLAT_PRICING_STRATEGY);
		workDTO.setFlatPrice(200.00);
		workService.repriceWork(work.getId(), workDTO);

		work = workService.findWork(work.getId());
		assertEquals(1, work.getPriceHistory().size());
	}

	// Work resources

	@Test
	public void workResourceAcceptsWork() throws Exception {
		User employee = newFirstEmployeeWithCashBalance();
		Work work = newWork(employee.getId());
		User user1 = newContractorIndependentlane4Ready();

		assertNotNull(user1);
		assertEquals(WorkStatusType.DRAFT, work.getWorkStatusType().getCode());

		laneService.addUserToCompanyLane2(user1.getId(), work.getCompany().getId());

		workRoutingService.addToWorkResources(work.getWorkNumber(), Sets.newHashSet(user1.getUserNumber()));
		workService.acceptWork(user1.getId(), work.getId());

		work = workService.findWork(work.getId());
		WorkResource workResource = workService.findWorkResource(user1.getId(), work.getId());

		assertEquals(WorkStatusType.ACTIVE, work.getWorkStatusType().getCode());
		assertEquals(WorkResourceStatusType.ACTIVE, workResource.getWorkResourceStatusType().getCode());
	}

	@Test
	public void workResourceDeclinesWork() throws Exception {
		User employee = newFirstEmployeeWithCashBalance();
		Work work = newWork(employee.getId());

		User user1 = newContractorIndependentlane4Ready();
		assertNotNull(user1);
		User user2 = newContractorIndependentlane4Ready();
		assertNotNull(user2);
		User user3 = newContractorIndependentlane4Ready();
		assertNotNull(user3);

		assertEquals(WorkStatusType.DRAFT, work.getWorkStatusType().getCode());

		laneService.addUserToCompanyLane2(user1.getId(), work.getCompany().getId());
		laneService.addUserToCompanyLane2(user2.getId(), work.getCompany().getId());
		laneService.addUserToCompanyLane2(user3.getId(), work.getCompany().getId());

		Set<LaneAssociation> associations = laneService.findAllAssociationsWhereUserIdIn(work.getCompany().getId(), Sets.newHashSet(user1.getId(), user2.getId(), user3.getId()));
		assertNotNull(associations);
		assertTrue(associations.size() == 3);
		workRoutingService.addToWorkResources(work.getId(), user1.getId());
		workRoutingService.addToWorkResources(work.getId(), user2.getId());
		workRoutingService.addToWorkResources(work.getId(), user3.getId());
		workService.declineWork(user1.getId(), work.getId());

		work = workService.findWork(work.getId());
		WorkResource workResource = workService.findWorkResource(user1.getId(), work.getId());

		assertEquals(WorkStatusType.SENT, work.getWorkStatusType().getCode());
		assertEquals(WorkResourceStatusType.DECLINED, workResource.getWorkResourceStatusType().getCode());
	}

	@Test
	public void workResourceDeclinesWorkUndecline() throws Exception {
		User employee = newFirstEmployeeWithCashBalance();
		Work work = newWork(employee.getId());

		User user1 = newContractorIndependentlane4Ready();
		assertNotNull(user1);
		User user2 = newContractorIndependentlane4Ready();
		assertNotNull(user2);
		User user3 = newContractorIndependentlane4Ready();
		assertNotNull(user3);

		assertEquals(WorkStatusType.DRAFT, work.getWorkStatusType().getCode());

		laneService.addUserToCompanyLane2(user1.getId(), work.getCompany().getId());
		laneService.addUserToCompanyLane2(user2.getId(), work.getCompany().getId());
		laneService.addUserToCompanyLane2(user3.getId(), work.getCompany().getId());

		workRoutingService.addToWorkResources(work.getId(), user1.getId());
		workRoutingService.addToWorkResources(work.getId(), user2.getId());
		workRoutingService.addToWorkResources(work.getId(), user3.getId());
		WorkResource workResource = workService.findWorkResource(user1.getId(), work.getId());
		workService.declineWork(user1.getId(), work.getId());
		workService.undeclineWork(workResource);

		work = workService.findWork(work.getId());

		assertEquals(WorkStatusType.SENT, work.getWorkStatusType().getCode());
		assertEquals(WorkResourceStatusType.OPEN, workResource.getWorkResourceStatusType().getCode());
		assertFalse(workResource.isAssignedToWork());
	}

	@Test
	public void allWorkResourcesDeclineWork() throws Exception {
		User employee = newFirstEmployeeWithCashBalance();
		Work work = newWork(employee.getId());

		User user1 = newContractorIndependentlane4Ready();
		User user2 = newContractorIndependentlane4Ready();
		User user3 = newContractorIndependentlane4Ready();

		laneService.addUsersToCompanyLane2(Lists.newArrayList(user1.getId(), user2.getId(), user3.getId()), work.getCompany().getId());

		assertEquals(WorkStatusType.DRAFT, work.getWorkStatusType().getCode());

		workRoutingService.addToWorkResources(work.getId(), user1.getId());
		workRoutingService.addToWorkResources(work.getId(), user2.getId());
		workRoutingService.addToWorkResources(work.getId(), user3.getId());

		workService.declineWork(user1.getId(), work.getId());
		workService.declineWork(user2.getId(), work.getId());
		workService.declineWork(user3.getId(), work.getId());

		work = workService.findWork(work.getId());
		WorkResource workResource1 = workService.findWorkResource(user1.getId(), work.getId());
		WorkResource workResource2 = workService.findWorkResource(user2.getId(), work.getId());
		WorkResource workResource3 = workService.findWorkResource(user3.getId(), work.getId());

		assertEquals(WorkStatusType.DECLINED, work.getWorkStatusType().getCode());
		assertEquals(WorkResourceStatusType.DECLINED, workResource1.getWorkResourceStatusType().getCode());
		assertEquals(WorkResourceStatusType.DECLINED, workResource2.getWorkResourceStatusType().getCode());
		assertEquals(WorkResourceStatusType.DECLINED, workResource3.getWorkResourceStatusType().getCode());
	}

	@Test
	public void workDeclineThenResend() throws Exception {
		User employee = newFirstEmployeeWithCashBalance();
		Work work = newWork(employee.getId());

		User user1 = newContractorIndependentlane4Ready();
		User user2 = newContractorIndependentlane4Ready();
		User user3 = newContractorIndependentlane4Ready();

		assertTrue(work.isDraft());

		laneService.addUserToCompanyLane2(user1.getId(), work.getCompany().getId());
		laneService.addUserToCompanyLane2(user2.getId(), work.getCompany().getId());
		laneService.addUserToCompanyLane2(user3.getId(), work.getCompany().getId());

		workRoutingService.addToWorkResources(work.getId(), user1.getId());
		workRoutingService.addToWorkResources(work.getId(), user2.getId());
		workRoutingService.addToWorkResources(work.getId(), user3.getId());
		workService.declineWork(user1.getId(), work.getId());
		List<Long> resourceIds = Lists.newArrayList(user1.getId(), user2.getId(), user3.getId());
		workService.resendInvitationsAsync(work.getId(), resourceIds);

		work = workService.findWork(work.getId());
		work = getWhenSent(work).get();
		WorkResource workResource = getWorkResourceWhenStatusTypeIsOpen(user1, work).get();

		assertTrue(work.isSent());
		assertTrue(workResource.isOpen());
	}

	@Test
	public void declineWork_GivenAllDeclined_WhenInviteNewResources_WorkIsSent() throws Exception {
		User employee = newFirstEmployeeWithCashBalance();
		Work work = newWork(employee.getId());
		User user1 = newContractorIndependentlane4Ready();
		User user2 = newContractorIndependentlane4Ready();
		User user3 = newContractorIndependentlane4Ready();

		assertTrue(work.isDraft());

		laneService.addUserToCompanyLane2(user1.getId(), work.getCompany().getId());
		laneService.addUserToCompanyLane2(user2.getId(), work.getCompany().getId());
		laneService.addUserToCompanyLane2(user3.getId(), work.getCompany().getId());

		workRoutingService.addToWorkResources(work.getId(), user1.getId());
		workRoutingService.addToWorkResources(work.getId(), user2.getId());
		workService.declineWork(user1.getId(), work.getId());

		work = workService.findWork(work.getId());
		assertTrue(work.isSent());

		workService.declineWork(user2.getId(), work.getId());
		work = workService.findWork(work.getId());
		assertTrue(work.isDeclined());

		workRoutingService.addToWorkResources(work.getId(), user3.getId());

		await().atMost(JMS_DELAY, MILLISECONDS).until(workIsReady(work.getId(), user3.getId()));

		work = workService.findWork(work.getId());
		WorkResource workResource = workService.findWorkResource(user3.getId(), work.getId());
		assertTrue(work.isSent());
		assertTrue(workResource.isOpen());
	}

	private Callable<Boolean> workIsReady(final Long workId, final Long userId) {
		return new WMCallable<Boolean>(webRequestContextProvider) {
			public Boolean apply() throws Exception {
				work = workService.findWork(workId);
				WorkResource workResource = workService.findWorkResource(userId, workId);
				return workResource != null;
			}
		};
	}

	@Test
	public void workNegotiateThenCancel() throws Exception {
		User employee = newFirstEmployeeWithCashBalance();
		Work work = newWork(employee.getId());
		PricingStrategy strategy = work.getPricingStrategy();
		FullPricingStrategy fullStrat = strategy.getFullPricingStrategy();
		BigDecimal oldPrice = fullStrat.getFlatPrice();

		User user1 = newContractorIndependentlane4Ready();
		assertNotNull(user1);
		User user2 = newContractorIndependentlane4Ready();
		assertNotNull(user2);
		User user3 = newContractorIndependentlane4Ready();
		assertNotNull(user3);

		assertEquals(WorkStatusType.DRAFT, work.getWorkStatusType().getCode());

		laneService.addUserToCompanyLane2(user1.getId(), work.getCompany().getId());
		laneService.addUserToCompanyLane2(user2.getId(), work.getCompany().getId());
		laneService.addUserToCompanyLane2(user3.getId(), work.getCompany().getId());

		workRoutingService.addToWorkResources(work.getId(), user1.getId());
		workRoutingService.addToWorkResources(work.getId(), user2.getId());
		workRoutingService.addToWorkResources(work.getId(), user3.getId());


		Calendar reschedule = DateUtilities.newCalendar(2012, 11, 28, 9, 0, 0);

		WorkNegotiationDTO dto = new WorkNegotiationDTO();
		dto.setPriceNegotiation(true);
		dto.setPricingStrategyId(FLAT_PRICING_STRATEGY);
		dto.setFlatPrice(500.00);
		dto.setScheduleNegotiation(true);
		dto.setIsScheduleRange(false);
		dto.setScheduleFromString(DateUtilities.getISO8601(reschedule));

		authenticationService.setCurrentUser(user1);
			WorkNegotiationResponse negotiationResponse = workNegotiationService.createNegotiation(work.getId(), dto);
		WorkNegotiation negotiation = (WorkNegotiation)workNegotiationService.findById(negotiationResponse.getWorkNegotiationId());

		assertTrue(workService.isUserWorkResourceForWork(user1.getId(), work.getId()));
		assertFalse(workService.isUserActiveResourceForWork(user1.getId(), work.getId()));
		assertNotNull(negotiation);
		assertTrue(negotiation.getPricingStrategy() instanceof FlatPricePricingStrategy);
		assertEquals(BigDecimal.valueOf(500.00).setScale(2), ((FlatPricePricingStrategy) negotiation.getPricingStrategy()).getFlatPrice().setScale(2));

		authenticationService.setCurrentUser(employee);
		workNegotiationService.approveNegotiation(negotiation.getId());
		authenticationService.setCurrentUser(user1);
		workService.abandonWork(user1.getId(), work.getId(), "blah");

		PricingStrategy newStrategy = work.getPricingStrategy();
		FullPricingStrategy newFullStrat = newStrategy.getFullPricingStrategy();
		BigDecimal newPrice = newFullStrat.getFlatPrice();
		assertEquals(oldPrice, newPrice);

		Collection<WorkNegotiation> negotiations = workNegotiationService.findAllNegotiationsByWorkId(work.getId());

		for (WorkNegotiation negIter : negotiations) {
			assertFalse(negIter.isApproved());
			assertTrue(negIter.getDeleted());
		}

		Work testWork = workService.findWork(work.getId());
		assertTrue(testWork.getPriceHistory().size() == 0);
	}

	@Test
	public void workCancelThenResend() throws Exception {
		User employee = newFirstEmployeeWithCashBalance();
		Work work = newWork(employee.getId());

		User user1 = newContractorIndependentlane4Ready();
		assertNotNull(user1);
		User user2 = newContractorIndependentlane4Ready();
		assertNotNull(user2);
		User user3 = newContractorIndependentlane4Ready();
		assertNotNull(user3);

		assertEquals(WorkStatusType.DRAFT, work.getWorkStatusType().getCode());

		laneService.addUserToCompanyLane2(user1.getId(), work.getCompany().getId());
		laneService.addUserToCompanyLane2(user2.getId(), work.getCompany().getId());
		laneService.addUserToCompanyLane2(user3.getId(), work.getCompany().getId());

		workRoutingService.addToWorkResources(work.getId(), user1.getId());
		workRoutingService.addToWorkResources(work.getId(), user2.getId());
		workRoutingService.addToWorkResources(work.getId(), user3.getId());

		authenticationService.setCurrentUser(user1.getId());
		AcceptWorkResponse acceptResponse = workService.acceptWork(user1.getId(), work.getId());
		assertTrue(acceptResponse.isSuccessful());

		List<ConstraintViolation> violations = workService.abandonWork(user1.getId(), work.getId(), "blah");
		assertTrue(violations.isEmpty());

		authenticationService.setCurrentUser(user1.getId());
		workService.acceptWork(user1.getId(), work.getId());
		workService.abandonWork(user1.getId(), work.getId(), "blah");
		workService.resendInvitationsAsync(work.getId(), Lists.newArrayList(user1.getId(), user2.getId(), user3.getId()));

		work = getWhenSent(work).get();
		WorkResource workResource = getWorkResourceWhenStatusTypeIsOpen(user1, work).get();

		assertEquals(WorkStatusType.SENT, work.getWorkStatusType().getCode());
		assertFalse(workResource.isCancelled());
		assertEquals(WorkResourceStatusType.OPEN, workResource.getWorkResourceStatusType().getCode());
	}

	@Async
	public Future<Work> getWhenSent(Work work) {
		while (!WorkStatusType.SENT.equals(work.getWorkStatusType().getCode())) {
			work = workService.findWork(work.getId());
		}
		return new AsyncResult<>(work);
	}

	@Async
	public Future<WorkResource> getWorkResourceWhenStatusTypeIsOpen(User user, Work work) {
		WorkResource workResource = workService.findWorkResource(user.getId(), work.getId());
		while (!WorkResourceStatusType.OPEN.equals(workResource.getWorkResourceStatusType().getCode())) {
			workResource = workService.findWorkResource(user.getId(), work.getId());
		}
		return new AsyncResult<>(workResource);
	}

	@Transactional
	@Test
	public void workConfirmAcceptCancelThenResendThenConfirm() throws Exception {
		User employee = newFirstEmployeeWithCashBalance();
		Work work = newWorkOnSiteWithLocationWithRequiredConfirmation(employee.getId(), "2012-11-16T23:00:00Z");

		User user1 = newContractorIndependentlane4Ready();
		assertNotNull(user1);
		User user2 = newContractorIndependentlane4Ready();
		assertNotNull(user2);
		User user3 = newContractorIndependentlane4Ready();
		assertNotNull(user3);

		assertEquals(WorkStatusType.DRAFT, work.getWorkStatusType().getCode());

		laneService.addUserToCompanyLane2(user1.getId(), work.getCompany().getId());
		laneService.addUserToCompanyLane2(user2.getId(), work.getCompany().getId());
		laneService.addUserToCompanyLane2(user3.getId(), work.getCompany().getId());

		workRoutingService.addToWorkResources(work.getId(), user1.getId());
		workRoutingService.addToWorkResources(work.getId(), user2.getId());
		workRoutingService.addToWorkResources(work.getId(), user3.getId());
		authenticationService.setCurrentUser(user1.getId());
		workService.acceptWork(user1.getId(), work.getId());
		workService.confirmWorkResource(user1.getId(), work.getId());
		workService.abandonWork(user1.getId(), work.getId(), "blah");
		List<Long> resourceIds = Lists.newArrayList(user1.getId(), user2.getId(), user3.getId());
		workService.resendInvitationsAsync(work.getId(), resourceIds);

		workService.acceptWork(user1.getId(), work.getId());
		workService.confirmWorkResource(user1.getId(), work.getId());


		work = workService.findWork(work.getId());
		WorkResource workResource = workService.findWorkResource(user1.getId(), work.getId());

		assertEquals(WorkStatusType.ACTIVE, work.getWorkStatusType().getCode());
		assertFalse(workResource.isCancelled());
		assertEquals(WorkResourceStatusType.ACTIVE, workResource.getWorkResourceStatusType().getCode());
	}

	@Test
	public void workResourceCompletesFixedPriceWork() throws Exception {
		User employee = newEmployeeWithCashBalance();
		Work work = newWork(employee.getId());
		User contractor = newContractorIndependentlane4Ready();
		assertEquals(WorkStatusType.DRAFT, work.getWorkStatusType().getCode());

		laneService.addUserToCompanyLane2(contractor.getId(), employee.getCompany().getId());
		workRoutingService.addToWorkResources(work.getId(), contractor.getId());
		workService.acceptWork(contractor.getId(), work.getId());

		workService.updateWorkProperties(work.getId(),
				CollectionUtilities.newStringMap("resolution", "Complete the assignment"));
		authenticationService.setCurrentUser(contractor.getId());
		workService.completeWork(work.getId(), new CompleteWorkDTO());

		work = workService.findWork(work.getId());

		assertEquals(WorkStatusType.COMPLETE, work.getWorkStatusType().getCode());
	}

	@Test
	public void incompleteWork() throws Exception {
		User employee = newFirstEmployeeWithCashBalance();
		Work work = newWork(employee.getId());
		User resource = newContractorIndependentlane4Ready();
		String note = "Terrible job, dude. Do it again!";

		laneService.addUserToCompanyLane2(resource.getId(), work.getBuyer().getCompany().getId());

		workRoutingService.addToWorkResources(work.getId(), resource.getId());
		workService.acceptWork(resource.getId(), work.getId());

		workService.incompleteWork(work.getId(), note);

		work = workService.findWork(work.getId(), true);

		for (WorkSubStatusTypeAssociation a : work.getWorkSubStatusTypeAssociations()) {
			if (a.getWorkSubStatusType().getCode().equals(WorkSubStatusType.INCOMPLETE_WORK)) {
				assertTrue(StringUtils.contains(a.getTransitionNote().getContent(), note));
			}
		}

	}

	@Test
	public void abandonWork() throws Exception {
		User employee = newEmployeeWithCashBalance();
		Work work = newWork(employee.getId());
		User resource = newContractorIndependentlane4Ready();

		laneService.addUserToCompanyLane2(resource.getId(), work.getBuyer().getCompany().getId());

		workRoutingService.addToWorkResources(work.getId(), resource.getId());
		authenticationService.setCurrentUser(resource.getId());
		workService.acceptWork(resource.getId(), work.getId());
		workService.abandonWork(resource.getId(), work.getId(), "I thought about it and I really don't want to do this job.");

		work = workService.findWork(work.getId());

		WorkResource workResource = workService.findActiveWorkResource(work.getId());
		assertNull(workResource);
	}

	@Test
	public void findActiveWorkerId_addWorkerToWork_returnWorkerIdForWork() throws Exception {
		User employee = newEmployeeWithCashBalance();
		Work work = newWork(employee.getId());
		User worker = newContractorIndependentlane4Ready();

		laneService.addUserToCompanyLane2(worker.getId(), work.getBuyer().getCompany().getId());

		workRoutingService.addToWorkResources(work.getId(), worker.getId());
		authenticationService.setCurrentUser(worker.getId());
		workService.acceptWork(worker.getId(), work.getId());

		work = workService.findWork(work.getId());

		Long activeWorkerId = workService.findActiveWorkerId(work.getId());
		assertEquals(activeWorkerId, worker.getId());
	}

	@Test
	public void findActiveWorkerId_noActiveWorker_returnNull() throws Exception {
		User employee = newEmployeeWithCashBalance();
		Work work = newWork(employee.getId());

		work = workService.findWork(work.getId());

		assertNull(workService.findActiveWorkerId(work.getId()));
	}

	@Test
	public void closeWork() throws Exception {
		User employee = newEmployeeWithCashBalance();
		User contractor = newContractorIndependentlane4Ready();

		laneService.addUserToCompanyLane2(contractor.getId(), employee.getCompany().getId());
		Work work = newWork(employee.getId());

		assertEquals(WorkStatusType.DRAFT, work.getWorkStatusType().getCode());

		workRoutingService.addToWorkResources(work.getId(), contractor.getId());
		workService.acceptWork(contractor.getId(), work.getId());
		CompleteWorkDTO dto = new CompleteWorkDTO();
		dto.setResolution("Resolved");
		workService.completeWork(work.getId(), dto);

		authenticationService.setCurrentUser(employee.getId());
		CloseWorkDTO closeWorkDTO = new CloseWorkDTO();
		closeWorkDTO.setRating(new RatingDTO(80));
		workService.closeWork(work.getId(), closeWorkDTO);

		work = workService.findWork(work.getId());

		assertEquals(WorkStatusType.PAID, work.getWorkStatusType().getCode());
	}

	@Test
	public void closeWorkWithNoLaneRelationship() throws Exception {
		User employee = newEmployeeWithCashBalance();
		User contractor = newContractorIndependentlane4Ready();

		ManageMyWorkMarketDTO manageMyWorkMarketDTO = new ManageMyWorkMarketDTO();
		manageMyWorkMarketDTO.setInstantWorkerPoolEnabled(true);
		profileService.updateManageMyWorkMarket(employee.getCompany().getId(), manageMyWorkMarketDTO);

		Work work = newWork(employee.getId());

		assertEquals(WorkStatusType.DRAFT, work.getWorkStatusType().getCode());

		workRoutingService.addToWorkResources(work.getId(), contractor.getId());
		workService.acceptWork(contractor.getId(), work.getId());
		CompleteWorkDTO dto = new CompleteWorkDTO();
		dto.setResolution("Resolved");
		workService.completeWork(work.getId(), dto);

		authenticationService.removeAclRoleFromUser(contractor.getId(), ACL_ROLE_SHARED_WORKER);

		authenticationService.setCurrentUser(employee.getId());

		CloseWorkDTO closeWorkDTO = new CloseWorkDTO();
		closeWorkDTO.setRating(new RatingDTO(80));
		workService.closeWork(work.getId(), closeWorkDTO);

		work = workService.findWork(work.getId());

		assertEquals(WorkStatusType.PAID, work.getWorkStatusType().getCode());
	}


	@Test
	public void cancelDraftWork() throws Exception {
		User employee = newEmployeeWithCashBalance();
		Work work = newWork(employee.getId());
		authenticationService.setCurrentUser(work.getBuyer().getId());

		workService.voidWork(work.getId(), "Nah.");

		work = workService.findWork(work.getId());

		assertEquals(WorkStatusType.VOID, work.getWorkStatusType().getCode());
	}

	@Test
	public void cancelSentWork() throws Exception {
		User employee = newEmployeeWithCashBalance();
		Work work = newWork(employee.getId());
		User resource = newContractorIndependentlane4Ready();

		laneService.addUserToCompanyLane2(resource.getId(), work.getBuyer().getCompany().getId());
		workRoutingService.addToWorkResources(work.getId(), resource.getId());
		authenticationService.setCurrentUser(work.getBuyer().getId());
		CancelWorkDTO cancelWorkDTO = new CancelWorkDTO();
		cancelWorkDTO.setWorkId(work.getId());
		cancelWorkDTO.setPrice(35.00);
		cancelWorkDTO.setCancellationReasonTypeCode(CancellationReasonType.OTHER);
		cancelWorkDTO.setNote("This is a cancellation note");

		assertFalse(workService.cancelWork(cancelWorkDTO).isEmpty());
	}

	@Test
	public void workShortUrl() throws Exception {
		User employee = newEmployeeWithCashBalance();
		Work work = newWork(employee.getId());
		assertNotNull(work.getShortUrl());
	}

	@Test(expected = IllegalStateException.class)
	public void deleteDraftNotAllowed() throws Exception {
		User employee = newEmployeeWithCashBalance();
		Work work = newWorkOnSiteWithLocationWithRequiredCheckin(employee.getId());
		User resource = newContractorIndependentlane4Ready();
		laneService.addUserToCompanyLane2(resource.getId(), work.getBuyer().getCompany().getId());

		workRoutingService.addToWorkResources(work.getId(), resource.getId());
		workService.acceptWork(resource.getId(), work.getId());

		work = workService.findWork(work.getId());

		assertEquals(work.getWorkStatusType().getCode(), WorkStatusType.ACTIVE);
		assertFalse(work.getDeleted());

		workService.deleteDraft(employee.getId(), work.getId());
	}

	@Test(expected = IllegalStateException.class)
	public void deleteDraftNotOwner() throws Exception {
		Work work = newWorkOnSiteWithLocationWithRequiredCheckin(ANONYMOUS_USER_ID);
		User resource = newContractorIndependentlane4Ready();
		work = workService.findWork(work.getId());

		assertEquals(work.getWorkStatusType().getCode(), WorkStatusType.DRAFT);
		assertFalse(work.getDeleted());

		workService.deleteDraft(resource.getId(), work.getId());
	}

	@Test
	public void checkInCheckOut() throws Exception {
		User employeeUser = newEmployeeWithCashBalance();
		Work work = newWorkOnSiteWithLocationWithRequiredCheckin(employeeUser.getId());
		User resource = newContractorIndependentlane4Ready();

		laneService.addUserToCompanyLane2(resource.getId(), work.getBuyer().getCompany().getId());
		workRoutingService.addToWorkResources(work.getId(), resource.getId());
		workService.acceptWork(resource.getId(), work.getId());
		workService.checkInActiveResource(new TimeTrackingRequest()
			.setWorkId(work.getId())
			.setDate(DateUtilities.getCalendarNow())
			.setLatitude(40.7401299)
			.setLongitude(-73.9924149)
		);

		workService.checkOutActiveResource(new TimeTrackingRequest()
			.setWorkId(work.getId())
			.setDate(DateUtilities.getCalendarNow())
			.setLatitude(41.7401299)
			.setLongitude(-72.9924149)
		);

		WorkResource activeResource = workService.findWorkResource(resource.getId(), work.getId());
		WorkResourceTimeTracking checkInOut = workService.findLatestTimeTrackRecordByWorkResource(activeResource.getId());

		assertNotNull(activeResource);
		assertNotNull(checkInOut.getCheckedInOn());
		assertNotNull(checkInOut.getCheckedOutOn());
		assertTrue(activeResource.isCheckedIn());
		assertTrue(activeResource.getTimeTracking().get(0).getDistanceIn() > 0.00 && activeResource.getTimeTracking().get(0).getDistanceIn() < 1.0);
		assertTrue(activeResource.getTimeTracking().get(0).getDistanceOut() > 75.00 && activeResource.getTimeTracking().get(0).getDistanceOut() < 95.0);

		workService.checkInActiveResource(new TimeTrackingRequest().setWorkId(work.getId()).setDate(DateUtilities.getCalendarNow()));
		workService.checkOutActiveResource(new TimeTrackingRequest().setWorkId(work.getId()).setDate(DateUtilities.getCalendarNow()));

		checkInOut = workService.findLatestTimeTrackRecordByWorkResource(activeResource.getId());

		assertNotNull(checkInOut.getCheckedInOn());
		assertNotNull(checkInOut.getCheckedOutOn());
		assertTrue(activeResource.isCheckedIn());

		List<WorkResourceTimeTracking> timeTracking = workService.findTimeTrackingByWorkResource(activeResource.getId());
		assertNotNull(timeTracking);
		assertTrue(timeTracking.size() == 2);
		assertNull(timeTracking.get(1).getDistanceIn());
		assertNull(timeTracking.get(1).getDistanceOut());
	}

	@Test
	@Ignore("Probably flickering because of something async")
	public void dueDate() throws Exception {
		User employeeUser = newFirstEmployeeWithCashBalanceAndPaymentTermsEnabled();
		Work work = newWorkWithPaymentTerms(employeeUser.getId(), 30);

		User resource = newContractorIndependentlane4Ready();

		laneService.addUserToCompanyLane2(resource.getId(), work.getBuyer().getCompany().getId());
		workRoutingService.addToWorkResources(work.getId(), resource.getId());
		workService.acceptWork(resource.getId(), work.getId());
		workService.updateWorkProperties(work.getId(), CollectionUtilities.newStringMap("resolution", "Complete the assignment"));

		workService.completeWork(work.getId(), new CompleteWorkDTO());

		authenticationService.setCurrentUser(employeeUser.getId());
		workService.closeWork(work.getId());

		work = workService.findWork(work.getId());
		assertNotNull(work);
		assertNotNull(work.getDueOn());
		assertNotNull(work.getPaymentTermsDays());
		assertNotNull(work.getClosedOn());

		Calendar closedOn = work.getClosedOn();
		closedOn.add(Calendar.DATE, work.getPaymentTermsDays());

		assertEquals(closedOn.get(Calendar.MONTH), work.getDueOn().get(Calendar.MONTH));
		assertEquals(closedOn.get(Calendar.DAY_OF_MONTH), work.getDueOn().get(Calendar.DAY_OF_MONTH));
	}

	@Test
	public void lockedCompanySaveLane2Resource() throws Exception {
		User employeeUser = newFirstEmployeeWithCashBalanceAndPaymentTermsEnabled();
		Work work = newWorkWithPaymentTerms(employeeUser.getId(), 30);

		User resource = newContractorIndependentlane4Ready();

		laneService.addUserToCompanyLane2(resource.getId(), employeeUser.getCompany().getId());
		companyService.lockCompanyAccount(employeeUser.getCompany().getId());
		workRoutingService.addToWorkResources(work.getId(), resource.getId());
		assertTrue(workService.findWork(work.getId()).isDraft());
	}

	@Test
	public void lockedCompanyRepriceWork() throws Exception {
		User employeeUser = newFirstEmployeeWithCashBalanceAndPaymentTermsEnabled();
		Work work = newWorkWithPaymentTerms(employeeUser.getId(), 30);

		User resource = newContractorIndependentlane4Ready();

		laneService.addUserToCompanyLane2(resource.getId(), employeeUser.getCompany().getId());
		workRoutingService.addToWorkResources(work.getId(), resource.getId());
		companyService.lockCompanyAccount(employeeUser.getCompany().getId());
		WorkDTO workDTO = new WorkDTO();
		workDTO.setPricingStrategyId(FLAT_PRICING_STRATEGY);
		workDTO.setFlatPrice(200.00);

		assertFalse(workService.repriceWork(work.getId(), workDTO).isEmpty());

	}

	@Test
	public void lockedCompanySaveLane1ResourceAndReprice() throws Exception {
		User employeeUser = newFirstEmployeeWithCashBalanceAndPaymentTermsEnabled();
		Work work = newWorkWithPaymentTerms(employeeUser.getId(), 30);

		User employee = newCompanyEmployee(employeeUser.getCompany().getId());

		laneService.addUserToCompanyLane1(employee.getId(), employeeUser.getCompany().getId());

		companyService.lockCompanyAccount(employeeUser.getCompany().getId());

		workRoutingService.addToWorkResources(work.getId(), employee.getId());

		WorkDTO workDTO = new WorkDTO();
		workDTO.setPricingStrategyId(FLAT_PRICING_STRATEGY);
		workDTO.setFlatPrice(200.00);

		workService.repriceWork(work.getId(), workDTO);
	}

	@Test
	public void userWithEnoughSpendLimitCreatesAssignment() throws Exception {
		User employee = newEmployeeWithCashBalance();
		assertEquals(employee.getSpendLimit().intValue(), 750);

		WorkDTO workDTO = new WorkDTO();
		workDTO.setTitle("Do Work!" + RandomUtilities.nextLong());
		workDTO.setDescription("Description of work. " + RandomUtilities.generateAlphaString(10));
		workDTO.setIsOnsiteAddress(false);
		workDTO.setPricingStrategyId((new FlatPricePricingStrategy()).getId());
		workDTO.setFlatPrice(600.00);
		workDTO.setIsScheduleRange(false);
		workDTO.setIndustryId(INDUSTRY_ID);
		workDTO.setScheduleFromString("2012-09-02T09:00:00Z");

		Work work = workFacadeService.saveOrUpdateWork(employee.getId(), workDTO);

		User resource = newContractorIndependentlane4Ready();

		laneService.addUserToCompanyLane2(resource.getId(), employee.getCompany().getId());
		workRoutingService.addToWorkResources(work.getId(), resource.getId());

		assertNotNull(workService.findWorkResource(resource.getId(), work.getId()));
	}

	@Test
	public void userWithoutEnoughSpendLimitCreatesAssignment() throws Exception {
		User employee = newEmployeeWithCashBalance();
		accountRegisterService.addFundsToRegisterFromWire(employee.getCompany().getId(), "1000.00");

		assertEquals(employee.getSpendLimit().intValue(), 750);

		WorkDTO workDTO = new WorkDTO();
		workDTO.setTitle("Do Work!" + RandomUtilities.nextLong());
		workDTO.setDescription("Description of work. " + RandomUtilities.generateAlphaString(10));
		workDTO.setIsOnsiteAddress(false);
		workDTO.setPricingStrategyId((new FlatPricePricingStrategy()).getId());
		workDTO.setFlatPrice(800.00);
		workDTO.setIsScheduleRange(false);
		workDTO.setIndustryId(INDUSTRY_ID);
		workDTO.setScheduleFromString("2012-09-02T09:00:00Z");

		Work work = workFacadeService.saveOrUpdateWork(employee.getId(), workDTO);

		User resource = newContractorIndependentlane4Ready();

		laneService.addUserToCompanyLane2(resource.getId(), employee.getCompany().getId());
		workRoutingService.addToWorkResources(work.getId(), resource.getId());
		assertTrue(workService.findWork(work.getId()).isDraft());
	}

	@Test
	public void userWithoutFundsCreatesAssignment() throws Exception {
		User employee = newFirstEmployeeWithAPLimit();
		accountRegisterService.addFundsToRegisterFromWire(employee.getCompany().getId(), "50.00");
		User resource = newContractorIndependentlane4Ready();

		assertEquals(employee.getSpendLimit().intValue(), 750);

		WorkDTO workDTO = new WorkDTO();
		workDTO.setTitle("Do Work!" + RandomUtilities.nextLong());
		workDTO.setDescription("Description of work. " + RandomUtilities.generateAlphaString(10));
		workDTO.setIsOnsiteAddress(false);
		workDTO.setPricingStrategyId((new FlatPricePricingStrategy()).getId());
		workDTO.setFlatPrice(200.00);
		workDTO.setIsScheduleRange(false);
		workDTO.setIndustryId(INDUSTRY_ID);
		Calendar tomorrow = Calendar.getInstance();
		tomorrow.add(Calendar.DAY_OF_YEAR, 1);
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		workDTO.setScheduleFromString(String.format("%sT09:00:00Z", sdf.format(tomorrow.getTime())));

		Work work = workFacadeService.saveOrUpdateWork(employee.getId(), workDTO);

		laneService.addUserToCompanyLane2(resource.getId(), employee.getCompany().getId());
		workRoutingService.addToWorkResources(work.getId(), resource.getId());
		assertTrue(workService.findWork(work.getId()).isDraft());
	}

	@Test
	public void isWorkResourceCheckInValid() throws Exception {
		User employee = newEmployeeWithCashBalance();
		accountRegisterService.addFundsToRegisterFromWire(employee.getCompany().getId(), "1000.00");

		WorkDTO workDTO = new WorkDTO();
		workDTO.setTitle("Do Work!" + RandomUtilities.nextLong());
		workDTO.setDescription("Description of work. " + RandomUtilities.generateAlphaString(10));
		workDTO.setIsOnsiteAddress(false);
		workDTO.setPricingStrategyId((new FlatPricePricingStrategy()).getId());
		workDTO.setFlatPrice(100.00);
		workDTO.setIsScheduleRange(false);
		Calendar scheduleFrom = DateUtilities.getCalendarNow();
		scheduleFrom.add(Calendar.HOUR_OF_DAY, 1);
		scheduleFrom.add(Calendar.MINUTE, 15);
		workDTO.setIndustryId(INDUSTRY_ID);
		workDTO.setScheduleFromString(DateUtilities.getISO8601(scheduleFrom));
		workDTO.setCheckinRequired(true);
		Work work = workFacadeService.saveOrUpdateWork(employee.getId(), workDTO);

		User resource = newContractorIndependentlane4Ready();

		laneService.addUserToCompanyLane2(resource.getId(), employee.getCompany().getId());
		workRoutingService.addToWorkResources(work.getId(), resource.getId());
		workService.acceptWork(resource.getId(), work.getId());

		assertTrue(workService.isWorkResourceCheckInValid(resource.getId(), work.getId()));

		WorkActionRequest request = new WorkActionRequest();
		request.setWorkId(work.getId());
		request.setModifierId(work.getBuyer().getId());


	}

	@Test
	public void isWorkResourceConfirmationValid() throws Exception {
		User employee = newEmployeeWithCashBalance();
		accountRegisterService.addFundsToRegisterFromWire(employee.getCompany().getId(), "1000.00");

		User resource = newContractorIndependentlane4Ready();

		userService.updateUserProperties(resource.getId(), CollectionUtilities.newStringMap("secondaryEmail", "ccemail@yahoo.com"));

		laneService.addUserToCompanyLane2(resource.getId(), employee.getCompany().getId());

		WorkDTO workDTO = new WorkDTO();
		workDTO.setTitle("Do Work!" + RandomUtilities.nextLong());
		workDTO.setDescription("Description of work. " + RandomUtilities.generateAlphaString(10));
		workDTO.setIsOnsiteAddress(false);
		workDTO.setPricingStrategyId((new FlatPricePricingStrategy()).getId());
		workDTO.setFlatPrice(100.00);
		workDTO.setIndustryId(INDUSTRY_ID);
		workDTO.setIsScheduleRange(false);
		Calendar scheduleFrom = DateUtilities.getCalendarNow();
		scheduleFrom.add(Calendar.HOUR_OF_DAY, 1);
		scheduleFrom.add(Calendar.MINUTE, 15);
		workDTO.setScheduleFromString(DateUtilities.getISO8601(scheduleFrom));
		workDTO.setResourceConfirmationRequired(true);
		workDTO.setResourceConfirmationHours(1d);

		Work work = workFacadeService.saveOrUpdateWork(employee.getId(), workDTO);

		workRoutingService.addToWorkResources(work.getId(), resource.getId());
		workService.acceptWork(resource.getId(), work.getId());

		assertTrue(workService.isWorkResourceConfirmationValid(resource.getId(), work.getId()));
		WorkActionRequest request = new WorkActionRequest();
		request.setWorkId(work.getId());
		request.setModifierId(work.getBuyer().getId());

	}

	@Test
	public void secondaryClientLocation() throws Exception {
		User employee = userService.findUserById(ANONYMOUS_USER_ID);
		accountRegisterService.addFundsToRegisterFromWire(employee.getCompany().getId(), "1000.00");

		WorkDTO workDTO = new WorkDTO();
		workDTO.setTitle("Do Work!" + RandomUtilities.nextLong());
		workDTO.setDescription("Description of work. " + RandomUtilities.generateAlphaString(10));
		workDTO.setIsOnsiteAddress(false);
		workDTO.setPricingStrategyId((new FlatPricePricingStrategy()).getId());
		workDTO.setFlatPrice(100.00);
		workDTO.setIsScheduleRange(false);
		workDTO.setScheduleFromString(DateUtilities.getISO8601(Calendar.getInstance()));
		workDTO.setSecondaryClientContactId(CLIENT_CONTACT_ID);

		Work work = workFacadeService.saveOrUpdateWork(ANONYMOUS_USER_ID, workDTO);

		assertNotNull(work);
	}

	@Test
	public void findAssignmentTimeZone() throws Exception {
		WorkDTO workDTO = new WorkDTO();
		workDTO.setTitle("Do Work!" + RandomUtilities.nextLong());
		workDTO.setDescription("Description of work. " + RandomUtilities.generateAlphaString(10));
		workDTO.setIsOnsiteAddress(true);
		workDTO.setPricingStrategyId((new FlatPricePricingStrategy()).getId());
		workDTO.setFlatPrice(100.00);
		workDTO.setIsScheduleRange(false);
		workDTO.setScheduleFromString(DateUtilities.getISO8601(Calendar.getInstance()));

		AddressDTO addressDTO = new AddressDTO();
		addressDTO.setAddress1("1301 Burlingame Ave.");
		addressDTO.setPostalCode("94010");
		addressDTO.setCountry("USA");
		addressDTO.setState("CA");
		addressDTO.setCity("Burlingame");
		addressDTO.setAddressTypeCode(AddressType.ASSIGNMENT);

		Address address = addressService.saveOrUpdate(addressDTO);

		workDTO.setAddressId(address.getId());

		Work work = workFacadeService.saveOrUpdateWork(ANONYMOUS_USER_ID, workDTO);

		assertNotNull(work);
		assertEquals("America/Los_Angeles", workService.findAssignmentTimeZone(work.getId()).getTimeZoneId());
		assertEquals("America/Los_Angeles", work.getTimeZone().getTimeZoneId());

		User resource = newContractorIndependentlane4Ready();

		laneService.addUserToCompanyLane2(resource.getId(), COMPANY_ID);
		workRoutingService.addToWorkResources(work.getId(), resource.getId());
	}

	@Test(expected = IllegalArgumentException.class)
	public void appointmentTimeNoTimeRange() throws Exception {
		WorkDTO workDTO = new WorkDTO();
		workDTO.setTitle("Do Work!" + RandomUtilities.nextLong());
		workDTO.setDescription("Description of work. " + RandomUtilities.generateAlphaString(10));
		workDTO.setIsOnsiteAddress(true);
		workDTO.setPricingStrategyId((new FlatPricePricingStrategy()).getId());
		workDTO.setFlatPrice(100.00);
		workDTO.setIsScheduleRange(false);
		workDTO.setScheduleFromString(DateUtilities.getISO8601(Calendar.getInstance()));

		Work work = workFacadeService.saveOrUpdateWork(ANONYMOUS_USER_ID, workDTO);

		assertNotNull(work);
		Calendar apptTime = Calendar.getInstance();
		apptTime.add(Calendar.MINUTE, 5);
		workService.setAppointmentTime(work.getId(), apptTime, "It is a haiku. An appointment test message. Refrigerator.");
	}

	@Test
	public void appointmentTime() throws Exception {
		User employee = newEmployeeWithCashBalance();
		User contractor = newContractorIndependentlane4Ready();

		laneService.addUserToCompanyLane2(contractor.getId(), employee.getCompany().getId());

		WorkDTO workDTO = new WorkDTO();
		workDTO.setTitle("Do Work!" + RandomUtilities.nextLong());
		workDTO.setDescription("Description of work. " + RandomUtilities.generateAlphaString(10));
		workDTO.setIsOnsiteAddress(false);
		workDTO.setPricingStrategyId((new FlatPricePricingStrategy()).getId());
		workDTO.setFlatPrice(100.00);
		workDTO.setIsScheduleRange(true);
		workDTO.setIndustryId(INDUSTRY_ID);
		workDTO.setScheduleFromString(DateUtilities.getISO8601(Calendar.getInstance()));

		Calendar through = Calendar.getInstance();
		through.add(Calendar.DATE, 2);
		workDTO.setScheduleThroughString(DateUtilities.getISO8601(through));

		Work work = workFacadeService.saveOrUpdateWork(employee.getId(), workDTO);
		workRoutingService.addToWorkResources(work.getId(), contractor.getId());
		workService.acceptWork(contractor.getId(), work.getId());

		assertNotNull(work);
		Calendar apptTime = Calendar.getInstance();
		apptTime.add(Calendar.MINUTE, 5);
		authenticationService.setCurrentUser(employee.getId());
		workService.setAppointmentTime(work.getId(), apptTime, "Appointment message.");

		CompleteWorkDTO dto = new CompleteWorkDTO();
		dto.setResolution("resolution");
		workService.completeWork(work.getId(), dto);
	}

	@Test
	public void inviteUserWhoBlocktheInvitingCompany() throws Exception {
		User buyer = newEmployeeWithCashBalance();
		User resource = newContractorIndependentlane4Ready();

		laneService.addUserToCompanyLane2(resource.getId(), buyer.getCompany().getId());

		userService.blockCompanyFromCompany(resource.getId(), buyer.getCompany().getId());
		Work work = newWork(buyer.getId());
		workRoutingService.addToWorkResources(work.getId(), resource.getId());
		assertTrue(workService.findWork(work.getId()).isDraft());
	}

	@Test
	public void inviteBlockedUser() throws Exception {
		User buyer = newEmployeeWithCashBalance();
		User resource = newContractorIndependentlane4Ready();

		laneService.addUserToCompanyLane2(resource.getId(), buyer.getCompany().getId());

		userService.blockUserFromCompany(buyer.getId(), resource.getId(), buyer.getCompany().getId());
		Work work = newWork(buyer.getId());
		workRoutingService.addToWorkResources(work.getId(), resource.getId());
		assertTrue(workService.findWork(work.getId()).isDraft());

	}

	@Test
	public void saveBlockedWorkResource() throws Exception {
		User buyer = userService.findUserById(ANONYMOUS_USER_ID);
		Work work = newWork(buyer.getId());
		Set<String> users = Sets.newHashSet();

		CompanyResourcePagination pagination = new CompanyResourcePagination(false);
		pagination.setStartRow(ZERO);
		pagination.setResultsLimit(1);

		pagination = laneService.findAllContractorsByCompany(buyer.getCompany().getId(), pagination);

		for (CompanyResource r : pagination.getResults()) {
			users.add(r.getUserNumber());
			userService.blockUser(buyer.getId(), r.getId());
			assertTrue(userService.isUserBlockedForCompany(r.getId(), r.getCompanyId(), COMPANY_ID));
		}

		workRoutingService.addToWorkResources(work.getWorkNumber(), users);
		assertTrue(workService.findWorkResources(work.getId(), new WorkResourcePagination(true)).getResults().isEmpty());
		assertTrue(workResourceService.findAllResourcesForWork(work.getId(), new WorkResourceDetailPagination(true)).getResults().isEmpty());
	}

	@Test
	public void addToWorkResources_withSameUserTwice_fails() throws Exception {
		User buyer = newEmployeeWithCashBalance();
		Company company = companyService.findCompanyById(buyer.getCompany().getId());

		User contractor = newContractorIndependentlane4Ready();
		laneService.addUserToCompanyLane3(contractor.getId(), company.getId());
		Work work = newWork(buyer.getId());

		Map<WorkAuthorizationResponse, Set<String>> response = workRoutingService.addToWorkResources(work.getWorkNumber(), Sets.newHashSet(contractor.getUserNumber())).getResponse();
		assertNotNull(response);
		assertTrue(response.keySet().size() == 1);
		assertEquals(response.keySet().iterator().next(), WorkAuthorizationResponse.SUCCEEDED);

		response = workRoutingService.addToWorkResources(work.getWorkNumber(), Sets.newHashSet(contractor.getUserNumber())).getResponse();
		assertNotNull(response);
		assertTrue(response.keySet().size() == 1);
		assertEquals(response.keySet().iterator().next(), WorkAuthorizationResponse.ALREADY_ADDED);
	}

	@Test
	public void markWorkViewed_success() throws Exception {
		User buyer = newEmployeeWithCashBalance();
		Company company = companyService.findCompanyById(buyer.getCompany().getId());

		User contractor = newContractorIndependentlane4Ready();
		laneService.addUserToCompanyLane3(contractor.getId(), company.getId());
		Work work = newWork(buyer.getId());

		Map<WorkAuthorizationResponse, Set<String>> response = workRoutingService.addToWorkResources(work.getWorkNumber(), Sets.newHashSet(contractor.getUserNumber())).getResponse();
		assertNotNull(response);
		workService.markWorkViewed(work.getId(), contractor.getId(), ViewType.MOBILE);
		WorkResource workResource = workService.findWorkResource(contractor.getId(), work.getId());
		assertNotNull(workResource);
		assertNotNull(workResource.getViewedOn());
		assertEquals(workResource.getViewType(), ViewType.MOBILE.getTypeString());
		workService.markWorkViewed(work.getId(), contractor.getId(), ViewType.MOBILE);
	}

	@Test
	public void findResourceWithLabels_success() throws Exception {
		User employee = newFirstEmployeeWithCashBalanceAndPaymentTermsEnabled();
		authenticationService.setCurrentUser(employee);

		User contractor = newContractor();
		Work work = createWorkAndSendToResourceWithPaymentTerms(employee, contractor);

		workService.acceptWork(contractor.getId(), work.getId());
		workService.updateWorkProperties(work.getId(), CollectionUtilities.newStringMap("resolution", "Complete the assignment"));
		authenticationService.setCurrentUser(contractor);
		workService.completeWork(work.getId(), new CompleteWorkDTO());
		authenticationService.setCurrentUser(employee);
		workService.closeWork(work.getId());

		work = workService.findWork(work.getId());
		WorkResourceDetailPagination resources = workResourceService.findAllResourcesForWork(work.getId(), new WorkResourceDetailPagination().setIncludeLabels(true) );
		assertTrue(resources.getResults().size() == 1);
		assertTrue(work.isPaymentPending());

		WorkResourceLabelDTO dto = new WorkResourceLabelDTO()
			.setConfirmed(true)
			.setWorkResourceId(CollectionUtilities.first(resources.getResults()).getWorkResourceId())
			.setWorkResourceLabelTypeCode(WorkResourceLabelType.ABANDONED);
		assertNotNull(workResourceService.addLabelToWorkResource(dto));

		resources = workResourceService.findAllResourcesForWork(work.getId(), new WorkResourceDetailPagination().setIncludeLabels(true));
		assertTrue(CollectionUtilities.first(resources.getResults()).getLabels().size() == 1);
	}

	@Test
	@Transactional
	public void clearGroupsForWork_AfterGroupsAreAdded_success() throws Exception {
		User buyer = newEmployeeWithCashBalance();
		Work work = newWork(buyer.getId());

		User contractor = newContractorIndependentlane4Ready();
		User contractor2 = newContractorIndependentlane4Ready();
		UserGroup group = newPublicUserGroup(contractor);
		UserGroup group2 = newPublicUserGroup(contractor2);
		List<Long> groups = ImmutableList.of(group.getId(), group2.getId());
		workService.addGroupsForWork(groups, work.getId());
		Set<WorkGroupAssociation> associations = workGroupAssociationDAO.findAllByWork(work.getId());
		assertEquals(2, associations.size());

		for (WorkGroupAssociation association : associations) {
			assertTrue(association.getGroup().equals(group) || association.getGroup().equals(group2));
			assertTrue(association.getWork().equals(work));
		}

		workService.clearGroupsForWork(work.getId());
		associations = workGroupAssociationDAO.findAllByWork(work.getId());
		assertEquals(0, associations.size());
	}

	@Test
	@Transactional
	public void addGroupToWorkTwice_fail() throws Exception {
		User buyer = newEmployeeWithCashBalance();
		Work work = newWork(buyer.getId());

		User contractor = newContractorIndependentlane4Ready();
		UserGroup group = newPublicUserGroup(contractor);
		List<Long> groups = ImmutableList.of(group.getId());
		workService.addGroupsForWork(groups, work.getId());
		Set<WorkGroupAssociation> associations = workGroupAssociationDAO.findAllByWork(work.getId());
		assertEquals(1, associations.size());

		workService.addGroupsForWork(groups, work.getId());
		associations = workGroupAssociationDAO.findAllByWork(work.getId());
		assertEquals(1, associations.size());
	}

	private Work getSentWork() throws Exception {
		User employee = newFirstEmployeeWithCashBalance();
		Work work = newWork(employee.getId());

		User user1 = newContractorIndependentlane4Ready();
		assertNotNull(user1);
		User user2 = newContractorIndependentlane4Ready();
		assertNotNull(user2);
		User user3 = newContractorIndependentlane4Ready();
		assertNotNull(user3);

		assertEquals(WorkStatusType.DRAFT, work.getWorkStatusType().getCode());

		laneService.addUserToCompanyLane2(user1.getId(), work.getCompany().getId());
		laneService.addUserToCompanyLane2(user2.getId(), work.getCompany().getId());
		laneService.addUserToCompanyLane2(user3.getId(), work.getCompany().getId());

		Set<LaneAssociation> associations = laneService.findAllAssociationsWhereUserIdIn(work.getCompany().getId(), Sets.newHashSet(user1.getId(), user2.getId(), user3.getId()));
		assertNotNull(associations);
		assertTrue(associations.size() == 3);
		workRoutingService.addToWorkResources(work.getId(), user1.getId());
		workRoutingService.addToWorkResources(work.getId(), user2.getId());
		workRoutingService.addToWorkResources(work.getId(), user3.getId());
		workService.declineWork(user1.getId(), work.getId());

		return workService.findWork(work.getId());
	}

	/**
	 * Verify that WorkResource.appointment is set when an assignment is by the contractor and approved by the creator
	 * @throws Exception
     */
	@Test
	public void workerRescheduleWork_withinWindow_appointmentSet() throws Exception {

		User employee = newEmployeeWithCashBalance();
		User contractor = newContractorIndependentlane4Ready();

		authenticationService.setCurrentUser(employee.getId());

		laneService.addUserToCompanyLane2(contractor.getId(), employee.getCompany().getId());

		// Create work with date range
		Calendar scheduleStart = Calendar.getInstance();
		Calendar scheduleThrough = Calendar.getInstance();
		scheduleThrough.add(Calendar.DATE, 10);

		WorkDTO workDTO = new WorkDTO();
		workDTO.setTitle("Do Work!" + RandomUtilities.nextLong());
		workDTO.setDescription("Description of work. " + RandomUtilities.generateAlphaString(10));
		workDTO.setIsOnsiteAddress(false);
		workDTO.setPricingStrategyId((new FlatPricePricingStrategy()).getId());
		workDTO.setFlatPrice(100.00);
		workDTO.setIsScheduleRange(true);
		workDTO.setAssignToFirstResource(false);
		workDTO.setIndustryId(INDUSTRY_ID);
		workDTO.setScheduleFromString(DateUtilities.getISO8601(scheduleStart));
		workDTO.setScheduleThroughString(DateUtilities.getISO8601(scheduleThrough));

		Work work = workFacadeService.saveOrUpdateWork(employee.getId(), workDTO);

		// Assign work
		workRoutingService.addToWorkResources(work.getId(), contractor.getId());
		workService.acceptWork(contractor.getId(), work.getId());

		// Worker reschedules appointment
		authenticationService.setCurrentUser(contractor);
		Calendar apptStartTime = Calendar.getInstance();
		apptStartTime.add(Calendar.MINUTE, 5);
		workService.setAppointmentTime(work.getId(), new DateRange(apptStartTime), "");

		// Validate workResource.appointment has been set
		WorkResource workResource = workResourceService.findActiveWorkResource(work.getId());
		assertNotNull(workResource);
		assertNotNull(workResource.getAppointment());
	}

	/**
	 * Verify that WorkResource.appointment is set when an assignment is rescheduled by the creator and approved by the contractor
	 * @throws Exception
	 */
	@Test
	public void employeeRescheduleWork_withinWindow_appointmentSet() throws Exception {

		User employee = newEmployeeWithCashBalance();
		userService.saveOrUpdatePersonaPreference(new PersonaPreference()
				.setUserId(employee.getId())
				.setBuyer(false)
				.setSeller(true));

		User contractor = newContractorIndependentlane4Ready();

		authenticationService.setCurrentUser(employee.getId());

		// Create work with date range
		Calendar scheduleStart = Calendar.getInstance();
		Calendar scheduleThrough = Calendar.getInstance();
		scheduleThrough.add(Calendar.DATE, 10);

		WorkDTO workDTO = new WorkDTO();
		workDTO.setTitle("Do Work!" + RandomUtilities.nextLong());
		workDTO.setDescription("Description of work. " + RandomUtilities.generateAlphaString(10));
		workDTO.setIsOnsiteAddress(false);
		workDTO.setPricingStrategyId((new FlatPricePricingStrategy()).getId());
		workDTO.setFlatPrice(100.00);
		workDTO.setIsScheduleRange(true);
		workDTO.setAssignToFirstResource(false);
		workDTO.setIndustryId(INDUSTRY_ID);
		workDTO.setScheduleFromString(DateUtilities.getISO8601(scheduleStart));
		workDTO.setScheduleThroughString(DateUtilities.getISO8601(scheduleThrough));

		Work work = workFacadeService.saveOrUpdateWork(employee.getId(), workDTO);

		// Assign work
		workRoutingService.addToWorkResources(work.getId(), contractor.getId());
		workService.acceptWork(contractor.getId(), work.getId());

		// Employee reschedules appointment (Create WorkNegotiation)
		Calendar apptStartTime = Calendar.getInstance();
		apptStartTime.add(Calendar.MINUTE, 5);
		workService.setAppointmentTime(work.getId(), new DateRange(apptStartTime), "");

		// Contractor approves appointment
		WorkRescheduleNegotiation workNegotiation = workNegotiationService.findLatestActiveRescheduleRequestByCompanyForWork(false, employee.getCompany().getId(), work.getId());

		authenticationService.setCurrentUser(contractor.getId());

		workNegotiationService.approveNegotiation(workNegotiation.getId());

		// Validate workResource.appointment has been set
		WorkResource workResource = workResourceService.findActiveWorkResource(work.getId());
		assertNotNull(workResource.getAppointment());
	}

	@Test
	public void unassign_withOriginalPriceRollback_bonusAndPriceEditAreReset() throws Exception {

		employee = newEmployeeWithCashBalance();
		contractor = newContractorIndependentLane4ReadyWithCashBalanceWithTaxEntity(true);

		Long contractorId = contractor.getId();
		laneService.addUserToCompanyLane3(contractorId, employee.getCompany().getId());
		authenticationService.setCurrentUser(employee);
		Work work = newWork(employee.getId());
		Long workId = work.getId();
		BigDecimal originalPrice = work.getPricingStrategy().getFullPricingStrategy().getFlatPrice();

		// reprice work
		TimeUnit.SECONDS.sleep(1); // Sleeping so that WorkPrice records will have different times
		repriceWork(work, 200);
		work = workService.findWork(work.getId());
		BigDecimal editedPrice = work.getPricingStrategy().getFullPricingStrategy().getFlatPrice();

		// assign/accept work
		TimeUnit.SECONDS.sleep(1); // Sleeping so that WorkPrice records will have different times
		workRoutingService.addToWorkResources(workId, contractorId);
		authenticationService.setCurrentUser(contractor);
		workService.acceptWork(contractor.getId(), work.getId());

		// add bonus
		TimeUnit.SECONDS.sleep(1); // Sleeping so that WorkPrice records will have different times
		double bonusAmount = 50.0;
		addBonus(work, bonusAmount);
		work = workService.findWork(work.getId());
		Assert.assertEquals(BigDecimal.valueOf(bonusAmount).setScale(2), work.getPricingStrategy().getFullPricingStrategy().getBonus().setScale(2));

		// Unassign and rollback to original price
		UnassignDTO unassignDTO = new UnassignDTO();
		unassignDTO.setWorkId(workId);
		unassignDTO.setRollbackToOriginalPrice(true);
		unassignDTO.setCancellationReasonTypeCode(CancellationReasonType.RESOURCE_CANCELLED);
		unassignDTO.setNote("note");

		workService.unassignWork(unassignDTO);

		// test that bonus and price are both reset
		work = workService.findWork(work.getId());
		Assert.assertEquals(BigDecimal.valueOf(0).setScale(2), work.getPricingStrategy().getFullPricingStrategy().getBonus().setScale(2));
		Assert.assertEquals(originalPrice, work.getPricingStrategy().getFullPricingStrategy().getFlatPrice());
	}



	@Test
	public void unassign_withNoPriceRollback_bonusAndPriceAreNotReset() throws Exception {

		employee = newEmployeeWithCashBalance();
		contractor = newContractorIndependentLane4ReadyWithCashBalanceWithTaxEntity(true);

		Long contractorId = contractor.getId();
		laneService.addUserToCompanyLane3(contractorId, employee.getCompany().getId());
		authenticationService.setCurrentUser(employee);
		Work work = newWork(employee.getId());
		Long workId = work.getId();

		// reprice work
		TimeUnit.SECONDS.sleep(1); // Sleeping so that WorkPrice records will have different times
		repriceWork(work, 200);
		work = workService.findWork(work.getId());
		BigDecimal editedPrice = work.getPricingStrategy().getFullPricingStrategy().getFlatPrice();

		// assign/accept work
		TimeUnit.SECONDS.sleep(1); // Sleeping so that WorkPrice records will have different times
		workRoutingService.addToWorkResources(workId, contractorId);
		authenticationService.setCurrentUser(contractor);
		workService.acceptWork(contractor.getId(), work.getId());

		// add bonus
		TimeUnit.SECONDS.sleep(1); // Sleeping so that WorkPrice records will have different times
		double bonusAmount = 50.0;
		addBonus(work, bonusAmount);
		work = workService.findWork(work.getId());
		Assert.assertEquals(BigDecimal.valueOf(bonusAmount).setScale(2), work.getPricingStrategy().getFullPricingStrategy().getBonus().setScale(2));

		UnassignDTO unassignDTO = new UnassignDTO();
		unassignDTO.setWorkId(workId);
		unassignDTO.setRollbackToOriginalPrice(false);
		unassignDTO.setCancellationReasonTypeCode(CancellationReasonType.RESOURCE_CANCELLED);
		unassignDTO.setNote("note");
		workService.unassignWork(unassignDTO);

		work = workService.findWork(work.getId());
		// Bonus is not reset, neither is price
		Assert.assertEquals(BigDecimal.valueOf(50).setScale(2), work.getPricingStrategy().getFullPricingStrategy().getBonus().setScale(2));
		Assert.assertEquals(editedPrice, work.getPricingStrategy().getFullPricingStrategy().getFlatPrice());
	}

	@Test
	public void unassign_withNoPriceChange_pricingNotUpdated() throws Exception {

		employee = newEmployeeWithCashBalance();
		contractor = newContractorIndependentLane4ReadyWithCashBalanceWithTaxEntity(true);

		Long contractorId = contractor.getId();
		laneService.addUserToCompanyLane3(contractorId, employee.getCompany().getId());
		authenticationService.setCurrentUser(employee);
		Work work = newWork(employee.getId());
		Long workId = work.getId();
		BigDecimal originalPrice = work.getPricingStrategy().getFullPricingStrategy().getFlatPrice();

		workRoutingService.addToWorkResources(workId, contractorId);
		authenticationService.setCurrentUser(contractor);
		workService.acceptWork(contractor.getId(), work.getId());

		// Unassign and rollback to original price
		UnassignDTO unassignDTO = new UnassignDTO();
		unassignDTO.setWorkId(workId);
		unassignDTO.setRollbackToOriginalPrice(true);
		unassignDTO.setCancellationReasonTypeCode(CancellationReasonType.RESOURCE_CANCELLED);
		unassignDTO.setNote("note");

		workService.unassignWork(unassignDTO);

		// test that bonus and price are both reset
		work = workService.findWork(work.getId());
		Assert.assertEquals(originalPrice, work.getPricingStrategy().getFullPricingStrategy().getFlatPrice());
	}

	@Test
	public void unassignWorker_invalidStatus_fails() throws Exception {
		employee = newEmployeeWithCashBalance();
		contractor = newContractorIndependentLane4ReadyWithCashBalanceWithTaxEntity(true);

		Long contractorId = contractor.getId();
		laneService.addUserToCompanyLane3(contractorId, employee.getCompany().getId());
		authenticationService.setCurrentUser(employee);
		Work work = newWork(employee.getId());
		Long workId = work.getId();
		BigDecimal originalPrice = work.getPricingStrategy().getFullPricingStrategy().getFlatPrice();

		// Unassign and rollback to original price
		UnassignDTO unassignDTO = new UnassignDTO();
		unassignDTO.setWorkId(workId);
		unassignDTO.setRollbackToOriginalPrice(true);
		unassignDTO.setCancellationReasonTypeCode(CancellationReasonType.RESOURCE_CANCELLED);
		unassignDTO.setNote("note");

		List<ConstraintViolation> constraintViolations = workService.unassignWorker(unassignDTO);

		assertEquals("Expect to see invalid status constraint violation",1, constraintViolations.size());
	}

	@Test
	public void unassignWorker_succeeds() throws Exception {
		employee = newEmployeeWithCashBalance();
		contractor = newContractorIndependentLane4ReadyWithCashBalanceWithTaxEntity(true);

		Long contractorId = contractor.getId();
		laneService.addUserToCompanyLane3(contractorId, employee.getCompany().getId());
		authenticationService.setCurrentUser(employee);
		Work work = newWork(employee.getId());
		Long workId = work.getId();
		BigDecimal originalPrice = work.getPricingStrategy().getFullPricingStrategy().getFlatPrice();

		workRoutingService.addToWorkResources(workId, contractorId);
		authenticationService.setCurrentUser(contractor);
		workService.acceptWork(contractor.getId(), work.getId());

		// Unassign and rollback to original price
		UnassignDTO unassignDTO = new UnassignDTO();
		unassignDTO.setWorkId(workId);
		unassignDTO.setRollbackToOriginalPrice(true);
		unassignDTO.setCancellationReasonTypeCode(CancellationReasonType.RESOURCE_CANCELLED);
		unassignDTO.setNote("note");

		workService.unassignWorker(unassignDTO);

		// test that bonus and price are both reset
		work = workService.findWork(work.getId());
		Assert.assertEquals("Unassigned assignment should now be in sent status", WorkStatusType.SENT, work.getWorkStatusType().getCode());
	}

	@Transactional
	@Test
	public void unassignWorker_fromAssignmentInBundle_succeeds() throws Exception {
		employee = newEmployeeWithCashBalance();
		contractor = newContractorIndependentLane4ReadyWithCashBalanceWithTaxEntity(true);

		laneService.addUserToCompanyLane3(contractor.getId(), employee.getCompany().getId());
		authenticationService.setCurrentUser(employee);
		Work work = newWork(employee.getId());
		Long workId = work.getId();

		WorkBundle workBundle = newWorkBundle(employee.getId());

		workBundleService.addAllToBundleByIds(workBundle.getWorkNumber(), ImmutableList.of(work.getId()));
		workRoutingService.addToWorkResources(workBundle.getId(), contractor.getId());
		workBundleRouting.routeWorkBundle(workBundle.getId());
		authenticationService.setCurrentUser(contractor);
		//workBundleService.acceptAllWorkInBundle(workBundle.getId(), contractor.getId()); // this is async, so we'll just manually accept the bundle assignment
		workService.acceptWork(contractor.getId(), work.getId());
		workService.acceptWork(contractor.getId(), workBundle.getId());

		// Unassign
		UnassignDTO unassignDTO = new UnassignDTO();
		unassignDTO.setWorkId(workId);
		unassignDTO.setRollbackToOriginalPrice(false);
		unassignDTO.setCancellationReasonTypeCode(CancellationReasonType.RESOURCE_CANCELLED);
		unassignDTO.setNote("note");

		List<ConstraintViolation> constraintViolations = workService.unassignWorker(unassignDTO);
		work = workService.findWork(workId);

		assertEquals("Unassigned assignment should now be in sent status", WorkStatusType.SENT, work.getWorkStatusType().getCode());
		assertFalse("Assignment was removed from bundle", work.isInBundle());
	}

	@Transactional
	@Test
	public void unassignWorker_fromBundle_succeeds() throws Exception {
		employee = newEmployeeWithCashBalance();
		contractor = newContractorIndependentLane4ReadyWithCashBalanceWithTaxEntity(true);

		Long contractorId = contractor.getId();
		laneService.addUserToCompanyLane3(contractorId, employee.getCompany().getId());
		authenticationService.setCurrentUser(employee);
		Work work = newWork(employee.getId());
		Long workId = work.getId();

		WorkBundle workBundle = newWorkBundle(employee.getId());

		workBundleService.addAllToBundleByIds(workBundle.getWorkNumber(), ImmutableList.of(work.getId()));
		workRoutingService.addToWorkResources(workBundle.getId(), contractorId);
		workBundleRouting.routeWorkBundle(workBundle.getId());
		authenticationService.setCurrentUser(contractor);
		//workBundleService.acceptAllWorkInBundle(workBundle.getId(), contractor.getId()); // this is async, so we'll just manually accept the bundle assignment
		workService.acceptWork(contractor.getId(), work.getId());
		workService.acceptWork(contractor.getId(), workBundle.getId());

		// Unassign
		UnassignDTO unassignDTO = new UnassignDTO();
		unassignDTO.setWorkId(workBundle.getId());
		unassignDTO.setRollbackToOriginalPrice(false);
		unassignDTO.setCancellationReasonTypeCode(CancellationReasonType.RESOURCE_CANCELLED);
		unassignDTO.setNote("note");

		List<ConstraintViolation> constraintViolations = workService.unassignWorker(unassignDTO);
		workBundle = workBundleService.findById(workBundle.getId());
		work = workService.findWork(workId);

		assertEquals("Bundle should now be in sent status", WorkStatusType.SENT, workBundle.getWorkStatusType().getCode());
		assertEquals("Assignment should be in sent status", WorkStatusType.SENT, work.getWorkStatusType().getCode());
	}

	@Test
	public void testRecurrence() throws Exception {

		employee = newEmployeeWithCashBalance();
		contractor = newContractorIndependentLane4ReadyWithCashBalanceWithTaxEntity(true);

		Long contractorId = contractor.getId();
		laneService.addUserToCompanyLane3(contractorId, employee.getCompany().getId());
		authenticationService.setCurrentUser(employee);
		Work work = newWork(employee.getId());
		Long workId = work.getId();

		String uuid = UUID.randomUUID().toString();
		workService.saveWorkRecurrence(workId, workId, uuid);
		String recurrenceUUID = workService.getRecurrenceUUID(workId);
		assertEquals(uuid, recurrenceUUID);
	}

	@Test
	public void createWork_withoutOPPAlways() throws Exception {
		initializeVars();
		Work work = newWork(employee.getId());
		assertFalse(workService.isOfflinePayment(work));
	}

	@Test
	public void createWork_withOPPAlways() throws Exception {
		initializeVars();
		admissionService.saveAdmissionForCompanyIdAndVenue(employee.getCompany().getId(), Venue.OFFLINE_PAY_ALL);
		Work work = newWork(employee.getId());
		assertTrue(workService.isOfflinePayment(work));
	}

	private void repriceWork(Work work, double newPrice){
		WorkDTO workDTO = new WorkDTO();
		workDTO.setPricingStrategyId(new FlatPricePricingStrategy().getId());
		workDTO.setFlatPrice(newPrice);
		workService.repriceWork(work.getId(), workDTO);
	}

	private void addBonus(Work work, double amount) throws Exception {
		WorkNegotiationDTO dto = new WorkNegotiationDTO();
		dto.setBonus(amount);
		dto.setNote("Need more!");
		WorkNegotiationResponse response = workNegotiationService.createBonusNegotiation(work.getId(), dto);
		Assert.assertNotNull(response);
		WorkBonusNegotiation negotiation = (WorkBonusNegotiation) workNegotiationService.findById(response.getWorkNegotiationId());
		authenticationService.setCurrentUser(employee);
		workNegotiationService.approveNegotiation(negotiation.getId());
	}
}
