package com.workmarket.domains.work.service;

import com.google.common.collect.Sets;
import com.workmarket.configuration.Constants;
import com.workmarket.domains.model.ApprovalStatus;
import com.workmarket.domains.model.User;
import com.workmarket.domains.model.WorkResource;
import com.workmarket.domains.model.acl.AclRole;
import com.workmarket.domains.model.pricing.FlatPricePricingStrategy;
import com.workmarket.domains.work.model.Work;
import com.workmarket.domains.work.model.negotiation.AbstractWorkNegotiation;
import com.workmarket.domains.work.model.negotiation.SpendLimitNegotiationType;
import com.workmarket.domains.work.model.negotiation.WorkBudgetNegotiation;
import com.workmarket.domains.work.model.negotiation.WorkExpenseNegotiation;
import com.workmarket.domains.work.model.negotiation.WorkNegotiation;
import com.workmarket.domains.work.model.negotiation.WorkNegotiationPagination;
import com.workmarket.domains.work.model.negotiation.WorkRescheduleNegotiation;
import com.workmarket.service.business.BaseServiceIT;
import com.workmarket.service.business.LaneService;
import com.workmarket.service.business.dto.CompleteWorkDTO;
import com.workmarket.service.business.dto.UserDTO;
import com.workmarket.service.business.dto.WorkDTO;
import com.workmarket.service.business.dto.WorkNegotiationDTO;
import com.workmarket.service.business.status.WorkNegotiationResponseStatus;
import com.workmarket.service.business.wrapper.WorkNegotiationResponse;
import com.workmarket.service.infra.business.AuthenticationService;
import com.workmarket.test.IntegrationTest;
import com.workmarket.thrift.work.TimeTrackingRequest;
import com.workmarket.utility.CollectionUtilities;
import com.workmarket.utility.DateUtilities;
import com.workmarket.utility.RandomUtilities;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

@RunWith(SpringJUnit4ClassRunner.class)
@Category(IntegrationTest.class)
public class WorkNegotiationServiceIT extends BaseServiceIT {

	@Autowired private WorkNegotiationService workNegotiationService;
	@Autowired private AuthenticationService authenticationService;
	@Autowired private LaneService laneService;
	@Autowired private WorkService workService;

	User employee;
	User contractor;

	private static final String DATE_STRING = "2011-09-02T09:00:00.000Z";

	@Before
	public void setup() throws Exception{
		employee = newEmployeeWithCashBalance();
		contractor = newContractorIndependentLane4ReadyWithCashBalanceWithTaxEntity(true);
	}

	@Test
	public void test_negotiationCreatedWithNote() throws Exception {
		Long contractorId = contractor.getId();
		laneService.addUserToCompanyLane3(contractor.getId(), employee.getCompany().getId());
		authenticationService.setCurrentUser(employee);
		Work work = newWork(employee.getId());
		Long workId = work.getId();

		workRoutingService.addToWorkResources(workId, contractorId);

		WorkNegotiationDTO dto = new WorkNegotiationDTO();
		dto.setPriceNegotiation(true);
		dto.setPricingStrategyId(FLAT_PRICING_STRATEGY);
		dto.setFlatPrice(500.00);
		dto.setScheduleNegotiation(true);
		dto.setIsScheduleRange(false);
		dto.setScheduleFromString(DATE_STRING);
		dto.setNote("Need more!");

		authenticationService.setCurrentUser(contractor);

		WorkNegotiationResponse negotiation = workNegotiationService.createNegotiation(workId, dto);
		assertNotNull(negotiation);
		assertTrue(negotiation.isSuccessful());
	}

	@Test
	public void test_negotiationApproved() throws Exception {
		Long contractorId = contractor.getId();
		laneService.addUserToCompanyLane3(contractorId, employee.getCompany().getId());
		authenticationService.setCurrentUser(employee);
		Work work = newWork(employee.getId());
		Long workId = work.getId();

		workRoutingService.addToWorkResources(workId, contractorId);

		Calendar reschedule = DateUtilities.newCalendar(2013, 8, 2, 9, 0, 0);

		WorkNegotiationDTO dto = new WorkNegotiationDTO();
		dto.setPriceNegotiation(true);
		dto.setPricingStrategyId(FLAT_PRICING_STRATEGY);
		dto.setFlatPrice(500.00);
		dto.setScheduleNegotiation(true);
		dto.setIsScheduleRange(false);
		dto.setScheduleFromString(DateUtilities.getISO8601(reschedule));
		dto.setNote("note comment");

		authenticationService.setCurrentUser(contractor);

		WorkNegotiationResponse negotiationResponse = workNegotiationService.createNegotiation(workId, dto);
		assertNotNull(negotiationResponse);
		assertTrue(negotiationResponse.isSuccessful());

		assertTrue(workService.isUserWorkResourceForWork(contractorId, workId));
		assertFalse(workService.isUserActiveResourceForWork(contractorId, workId));
		assertNotNull(negotiationResponse);

		WorkNegotiation negotiation = (WorkNegotiation)workNegotiationService.findById(negotiationResponse.getWorkNegotiationId());
		assertTrue(negotiation.getPricingStrategy() instanceof FlatPricePricingStrategy);
		assertEquals(BigDecimal.valueOf(500.00).setScale(2), ((FlatPricePricingStrategy) negotiation.getPricingStrategy()).getFlatPrice().setScale(2));

		authenticationService.setCurrentUser(employee);

		workNegotiationService.approveNegotiation(negotiation.getId());

		work = workService.findWork(work.getId());

		assertTrue(workService.isUserActiveResourceForWork(contractor.getId(), work.getId()));
		assertTrue(work.getPricingStrategy() instanceof FlatPricePricingStrategy);
		assertEquals(BigDecimal.valueOf(500.00).setScale(2), ((FlatPricePricingStrategy) work.getPricingStrategy()).getFlatPrice().setScale(2));
		assertTrue(reschedule.getTime().equals(work.getScheduleFrom().getTime()));
	}

	@Test
	public void test_negotiationDeclined() throws Exception {
		Long contractorId = contractor.getId();
		laneService.addUserToCompanyLane3(contractor.getId(), employee.getCompany().getId());
		authenticationService.setCurrentUser(employee);
		Work work = newWork(employee.getId());
		Long workId = work.getId();

		workRoutingService.addToWorkResources(work.getId(), contractorId);

		WorkNegotiationDTO dto = new WorkNegotiationDTO();
		dto.setPriceNegotiation(true);
		dto.setPricingStrategyId(FLAT_PRICING_STRATEGY);
		dto.setFlatPrice(1000.00);
		dto.setNote("note comment");

		authenticationService.setCurrentUser(contractor);

		WorkNegotiationResponse negotiationResponse = workNegotiationService.createNegotiation(workId, dto);
		assertNotNull(negotiationResponse);
		assertTrue(negotiationResponse.isSuccessful());

		assertTrue(workService.isUserWorkResourceForWork(contractorId, workId));
		assertFalse(workService.isUserActiveResourceForWork(contractorId, workId));
		assertNotNull(negotiationResponse);

		authenticationService.setCurrentUser(employee);

		WorkNegotiation negotiation = (WorkNegotiation)workNegotiationService.findById(negotiationResponse.getWorkNegotiationId());
		workNegotiationService.declineNegotiation(negotiation.getId(), "Declining", null);

		work = workService.findWork(workId);

		assertTrue(workService.isUserWorkResourceForWork(contractorId, workId));
		assertFalse(workService.isUserActiveResourceForWork(contractorId, workId));
		assertTrue(work.getPricingStrategy() instanceof FlatPricePricingStrategy);
	}

	// Negotiation by a non-resource

	@Test(expected = IllegalStateException.class)
	public void test_negotiationCreatedByNonWorkResource() throws Exception {

		Long contractorId = contractor.getId();
		laneService.addUserToCompanyLane3(contractorId, employee.getCompany().getId());
		authenticationService.setCurrentUser(employee);
		Work work = newWork(employee.getId());
		Long workId = work.getId();

		WorkNegotiationDTO dto = new WorkNegotiationDTO();
		dto.setPriceNegotiation(true);
		dto.setPricingStrategyId(FLAT_PRICING_STRATEGY);
		dto.setFlatPrice(1000.00);
		dto.setNote("note comment");

		authenticationService.setCurrentUser(contractor);

		WorkNegotiationResponse negotiationResponse = workNegotiationService.createNegotiation(workId, dto);
		assertNotNull(negotiationResponse);
		assertTrue(negotiationResponse.isSuccessful());
	}

	// Negotiation on non-negotiable work

	@Test(expected = IllegalStateException.class)
	public void test_negotiationOnNonNegotiableWork() throws Exception {

		laneService.addUserToCompanyLane3(contractor.getId(), employee.getCompany().getId());
		authenticationService.setCurrentUser(employee);
		Work work = newWork(employee.getId());

		workRoutingService.addToWorkResources(work.getId(), contractor.getId());
		workService.acceptWork(contractor.getId(), work.getId());

		WorkNegotiationDTO dto = new WorkNegotiationDTO();
		dto.setPriceNegotiation(true);
		dto.setPricingStrategyId(FLAT_PRICING_STRATEGY);
		dto.setFlatPrice(1000.00);
		dto.setNote("note comment");

		authenticationService.setCurrentUser(contractor);

		WorkNegotiationResponse negotiationResponse = workNegotiationService.createNegotiation(work.getId(), dto);
		assertNotNull(negotiationResponse);
		assertTrue(negotiationResponse.isSuccessful());
	}

	// Approval by a non-buyer

	@Test
	public void test_negotiationApprovedByNonBuyer() throws Exception {

		laneService.addUserToCompanyLane3(contractor.getId(), employee.getCompany().getId());
		authenticationService.setCurrentUser(employee);
		Work work = newWork(employee.getId());

		workRoutingService.addToWorkResources(work.getId(), contractor.getId());

		WorkNegotiationDTO dto = new WorkNegotiationDTO();
		dto.setPriceNegotiation(true);
		dto.setPricingStrategyId(FLAT_PRICING_STRATEGY);
		dto.setFlatPrice(1000.00);
		dto.setNote("note comment");

		authenticationService.setCurrentUser(contractor);

		WorkNegotiationResponse negotiationResponse = workNegotiationService.createNegotiation(work.getId(), dto);
		assertNotNull(negotiationResponse);
		assertTrue(negotiationResponse.isSuccessful());
		assertTrue(workService.isUserWorkResourceForWork(contractor.getId(), work.getId()));
		assertFalse(workService.isUserActiveResourceForWork(contractor.getId(), work.getId()));
		assertNotNull(negotiationResponse);

		WorkNegotiation negotiation = (WorkNegotiation)workNegotiationService.findById(negotiationResponse.getWorkNegotiationId());
		assertTrue(negotiation.getPricingStrategy() instanceof FlatPricePricingStrategy);
		assertEquals(BigDecimal.valueOf(1000.00).setScale(2), ((FlatPricePricingStrategy) negotiation.getPricingStrategy()).getFlatPrice().setScale(2));

		assertFalse(workNegotiationService.approveNegotiation(negotiation.getId()).isSuccessful());
	}

	// Decline by a non-buyer

	@Test
	public void test_negotiationDeclinedByNonBuyer() throws Exception {

		laneService.addUserToCompanyLane3(contractor.getId(), employee.getCompany().getId());
		authenticationService.setCurrentUser(employee);
		Work work = newWork(employee.getId());

		workRoutingService.addToWorkResources(work.getId(), contractor.getId());

		WorkNegotiationDTO dto = new WorkNegotiationDTO();
		dto.setPriceNegotiation(true);
		dto.setPricingStrategyId(FLAT_PRICING_STRATEGY);
		dto.setFlatPrice(1000.00);
		dto.setNote("note comment");

		authenticationService.setCurrentUser(contractor);

		WorkNegotiationResponse negotiationResponse = workNegotiationService.createNegotiation(work.getId(), dto);
		assertNotNull(negotiationResponse);
		assertTrue(negotiationResponse.isSuccessful());
		assertTrue(workService.isUserWorkResourceForWork(contractor.getId(), work.getId()));
		assertFalse(workService.isUserActiveResourceForWork(contractor.getId(), work.getId()));
		assertNotNull(negotiationResponse);

		WorkNegotiation negotiation = (WorkNegotiation)workNegotiationService.findById(negotiationResponse.getWorkNegotiationId());
		assertTrue(negotiation.getPricingStrategy() instanceof FlatPricePricingStrategy);
		org.junit.Assert.assertEquals(BigDecimal.valueOf(1000.00).setScale(2), ((FlatPricePricingStrategy) negotiation.getPricingStrategy()).getFlatPrice().setScale(2));

		assertFalse(workNegotiationService.declineNegotiation(negotiation.getId(), "Declining", null).isSuccessful());
	}

	// Approval of expired

	@Test(expected = IllegalStateException.class)
	public void test_negotiationApprovedButExpired() throws Exception {

		laneService.addUserToCompanyLane3(contractor.getId(), employee.getCompany().getId());
		authenticationService.setCurrentUser(employee);
		Work work = newWork(employee.getId());

		workRoutingService.addToWorkResources(work.getId(), contractor.getId());

		WorkNegotiationDTO dto = new WorkNegotiationDTO();
		dto.setExpiresOn(DateUtilities.getISO8601(DateUtilities.subtractTime(Calendar.getInstance(), 1, Constants.HOUR)));
		dto.setPriceNegotiation(true);
		dto.setPricingStrategyId(FLAT_PRICING_STRATEGY);
		dto.setFlatPrice(1000.00);
		dto.setNote("note comment");

		authenticationService.setCurrentUser(contractor);

		WorkNegotiationResponse negotiationResponse = workNegotiationService.createNegotiation(work.getId(), dto);
		assertNotNull(negotiationResponse);
		assertTrue(negotiationResponse.isSuccessful());
		assertTrue(workService.isUserWorkResourceForWork(contractor.getId(), work.getId()));
		assertFalse(workService.isUserActiveResourceForWork(contractor.getId(), work.getId()));
		assertNotNull(negotiationResponse);
		WorkNegotiation negotiation = (WorkNegotiation)workNegotiationService.findById(negotiationResponse.getWorkNegotiationId());
		assertTrue(negotiation.getPricingStrategy() instanceof FlatPricePricingStrategy);
		assertEquals(BigDecimal.valueOf(1000.00).setScale(2), ((FlatPricePricingStrategy) negotiation.getPricingStrategy()).getFlatPrice().setScale(2));

		authenticationService.setCurrentUser(employee);

		assertTrue(workNegotiationService.approveNegotiation(negotiation.getId()).isSuccessful());
	}

	// Decline of expired

	@Test(expected = IllegalStateException.class)
	public void test_negotiationDeclinedButExpired() throws Exception {

		laneService.addUserToCompanyLane3(contractor.getId(), employee.getCompany().getId());
		authenticationService.setCurrentUser(employee);
		Work work = newWork(employee.getId());

		workRoutingService.addToWorkResources(work.getId(), contractor.getId());

		WorkNegotiationDTO dto = new WorkNegotiationDTO();
		dto.setExpiresOn(DateUtilities.getISO8601(DateUtilities.subtractTime(Calendar.getInstance(), 1, Constants.HOUR)));
		dto.setPriceNegotiation(true);
		dto.setPricingStrategyId(FLAT_PRICING_STRATEGY);
		dto.setFlatPrice(1000.00);
		dto.setNote("note comment");

		authenticationService.setCurrentUser(contractor);

		WorkNegotiationResponse negotiationResponse = workNegotiationService.createNegotiation(work.getId(), dto);
		assertNotNull(negotiationResponse);
		assertTrue(negotiationResponse.isSuccessful());
		assertTrue(workService.isUserWorkResourceForWork(contractor.getId(), work.getId()));
		assertFalse(workService.isUserActiveResourceForWork(contractor.getId(), work.getId()));
		assertNotNull(negotiationResponse);

		WorkNegotiation negotiation = (WorkNegotiation)workNegotiationService.findById(negotiationResponse.getWorkNegotiationId());
		assertTrue(negotiation.getPricingStrategy() instanceof FlatPricePricingStrategy);
		assertEquals(BigDecimal.valueOf(1000.00).setScale(2), ((FlatPricePricingStrategy) negotiation.getPricingStrategy()).getFlatPrice().setScale(2));

		authenticationService.setCurrentUser(employee);

		assertFalse(workNegotiationService.declineNegotiation(negotiation.getId(), "Declining", null).isSuccessful());
	}

	// Expiration extension

	@Test
	public void test_negotiationExpirationExtension() throws Exception {

		laneService.addUserToCompanyLane3(contractor.getId(), employee.getCompany().getId());
		authenticationService.setCurrentUser(employee);
		Work work = newWork(employee.getId());

		workRoutingService.addToWorkResources(work.getId(), contractor.getId());

		WorkNegotiationDTO dto = new WorkNegotiationDTO();
		dto.setExpiresOn(DateUtilities.getISO8601(DateUtilities.subtractTime(Calendar.getInstance(), 6, Constants.HOUR)));
		dto.setPriceNegotiation(true);
		dto.setPricingStrategyId(FLAT_PRICING_STRATEGY);
		dto.setFlatPrice(1000.00);
		dto.setNote("note comment");

		authenticationService.setCurrentUser(contractor);

		WorkNegotiationResponse negotiationResponse = workNegotiationService.createNegotiation(work.getId(), dto);
		assertNotNull(negotiationResponse);
		assertTrue(negotiationResponse.isSuccessful());
		WorkNegotiation negotiation = (WorkNegotiation)workNegotiationService.findById(negotiationResponse.getWorkNegotiationId());
		Calendar originalExpiration = negotiation.getExpiresOn();

		workNegotiationService.extendNegotiationExpiration(negotiation.getId(), 5, Constants.HOUR);

		negotiation = (WorkNegotiation) workNegotiationService.findById(negotiation.getId());

		assertTrue(negotiation.getExpiresOn().compareTo(originalExpiration) > 0);
	}

	// Negotiation via work resource

	@Test
	public void test_getNegotiations() throws Exception {
		int negotiationCount = 2;
		int contractorCount = 2;

		authenticationService.setCurrentUser(employee);
		Work work = newWork(employee.getId());

		for (int i = 0; i < contractorCount; i++) {
			User contractor = newContractorIndependentLane4ReadyWithCashBalanceWithTaxEntity(true);

			laneService.addUserToCompanyLane3(contractor.getId(), employee.getCompany().getId());

			workRoutingService.addToWorkResources(work.getId(), contractor.getId());

			authenticationService.setCurrentUser(contractor);

			WorkNegotiationDTO dto = new WorkNegotiationDTO();
			dto.setPriceNegotiation(true);
			dto.setPricingStrategyId(FLAT_PRICING_STRATEGY);
			dto.setNote("note comment");
			for (int j = 0; j < negotiationCount; j++) {
				dto.setFlatPrice((double) RandomUtilities.nextIntInRange(100, 1000));

				WorkNegotiationResponse negotiationResponse = workNegotiationService.createNegotiation(work.getId(), dto);
				assertNotNull(negotiationResponse);
				assertTrue(negotiationResponse.isSuccessful());
			}
		}

		authenticationService.setCurrentUser(employee);

		WorkNegotiationPagination pagination = new WorkNegotiationPagination(true);
		pagination.getFilters().put(WorkNegotiationPagination.FILTER_KEYS.APPROVAL_STATUS.toString(), ApprovalStatus.PENDING.toString());

		pagination = workNegotiationService.findByWork(work.getId(), pagination);

		assertEquals(contractorCount, pagination.getRowCount().intValue());
		for (AbstractWorkNegotiation negotiation : pagination.getResults()) {
			assertTrue(((WorkNegotiation) negotiation).isPriceNegotiation());
			assertEquals(work.getId(), negotiation.getWork().getId());
			assertEquals(ApprovalStatus.PENDING, negotiation.getApprovalStatus());
		}
	}

	@Test
	public void test_cancelPendingNegotiations() throws Exception {
		int negotiationCount = 2;
		authenticationService.setCurrentUser(employee);
		Work work = newWork(employee.getId());
		laneService.addUserToCompanyLane3(contractor.getId(), employee.getCompany().getId());

		workRoutingService.addToWorkResources(work.getId(), contractor.getId());

		authenticationService.setCurrentUser(contractor);

		WorkNegotiationDTO dto = new WorkNegotiationDTO();
		dto.setPriceNegotiation(true);
		dto.setPricingStrategyId(FLAT_PRICING_STRATEGY);
		dto.setNote("note comment");

		for (int j = 0; j < negotiationCount; j++) {
			dto.setFlatPrice((double) RandomUtilities.nextIntInRange(100, 1000));

			WorkNegotiationResponse negotiationResponse = workNegotiationService.createNegotiation(work.getId(), dto);
			assertNotNull(negotiationResponse);
			assertTrue(negotiationResponse.isSuccessful());
		}

		workNegotiationService.cancelPendingNegotiationsByCompanyForWork(contractor.getCompany().getId(), work.getId());

		WorkNegotiationPagination pagination = workNegotiationService.findByCompanyForWork(contractor.getCompany().getId(), work.getId(), new WorkNegotiationPagination(true));

		assertEquals(negotiationCount, pagination.getRowCount().intValue());
		for (AbstractWorkNegotiation negotiation : pagination.getResults()) {
			assertEquals(ApprovalStatus.REMOVED, negotiation.getApprovalStatus());
		}
	}


	@Test
	public void test_cancelAllNegotiations() throws Exception {
		int negotiationCount = 2;
		authenticationService.setCurrentUser(employee);
		Work work = newWork(employee.getId());
		laneService.addUserToCompanyLane3(contractor.getId(), employee.getCompany().getId());

		workRoutingService.addToWorkResources(work.getId(), contractor.getId());

		authenticationService.setCurrentUser(contractor);

		WorkNegotiationDTO dto = new WorkNegotiationDTO();
		dto.setPriceNegotiation(true);
		dto.setPricingStrategyId(FLAT_PRICING_STRATEGY);
		dto.setNote("note comment");

		for (int j = 0; j < negotiationCount; j++) {
			dto.setFlatPrice((double) RandomUtilities.nextIntInRange(100, 1000));

			WorkNegotiationResponse negotiationResponse = workNegotiationService.createNegotiation(work.getId(), dto);
			assertNotNull(negotiationResponse);
			assertTrue(negotiationResponse.isSuccessful());
		}

			workNegotiationService.cancelAllNegotiationsByCompanyForWork(contractor.getCompany().getId(), work.getId());

			WorkNegotiationPagination pagination = workNegotiationService.findByCompanyForWork(contractor.getCompany().getId(), work.getId(), new WorkNegotiationPagination(true));

			assertEquals(0, pagination.getRowCount().intValue());
		}

	@Test
	public void test_cancelPendingNegotiationsForDeclinedWork() throws Exception {
		int negotiationCount = 2;
		authenticationService.setCurrentUser(employee);
		Work work = newWork(employee.getId());
		laneService.addUserToCompanyLane3(contractor.getId(), employee.getCompany().getId());

		workRoutingService.addToWorkResources(work.getId(), contractor.getId());

		authenticationService.setCurrentUser(contractor);

		WorkNegotiationDTO dto = new WorkNegotiationDTO();
		dto.setPriceNegotiation(true);
		dto.setPricingStrategyId(FLAT_PRICING_STRATEGY);
		dto.setNote("note comment");

		for (int j = 0; j < negotiationCount; j++) {
			dto.setFlatPrice((double) RandomUtilities.nextIntInRange(100, 1000));

			WorkNegotiationResponse negotiation = workNegotiationService.createNegotiation(work.getId(), dto);
			assertNotNull(negotiation);
		}

		workService.declineWork(contractor.getId(), work.getId());

		WorkNegotiationPagination pagination = workNegotiationService.findByCompanyForWork(contractor.getCompany().getId(), work.getId(), new WorkNegotiationPagination(true));

		assertEquals(negotiationCount, pagination.getRowCount().intValue());
		for (AbstractWorkNegotiation negotiation : pagination.getResults()) {
			assertEquals(ApprovalStatus.REMOVED, negotiation.getApprovalStatus());
		}
	}


	// RE-SCHEDULING

	@Test
	public void test_rescheduleCreatedWithNote() throws Exception {

		laneService.addUserToCompanyLane3(contractor.getId(), employee.getCompany().getId());
		authenticationService.setCurrentUser(employee);
		Work work = newWork(employee.getId());

		workRoutingService.addToWorkResources(work.getId(), contractor.getId());
		workService.acceptWork(contractor.getId(), work.getId());

		WorkNegotiationDTO dto = new WorkNegotiationDTO();
		dto.setIsScheduleRange(false);
		dto.setScheduleFromString(DATE_STRING);
		dto.setNote("Need more!");

		authenticationService.setCurrentUser(contractor);

		WorkRescheduleNegotiation negotiation = workNegotiationService.createRescheduleNegotiation(work.getId(), dto);

		assertNotNull(negotiation.getNote());
		assertEquals("Need more!", negotiation.getNote().getContent());
	}

	@Test
	public void test_rescheduleApproved() throws Exception {

		laneService.addUserToCompanyLane3(contractor.getId(), employee.getCompany().getId());
		authenticationService.setCurrentUser(employee);
		Work work = newWork(employee.getId());

		workRoutingService.addToWorkResources(work.getId(), contractor.getId());
		workService.acceptWork(contractor.getId(), work.getId());
		workService.checkInActiveResource(new TimeTrackingRequest().setWorkId(work.getId()).setDate(DateUtilities.getCalendarNow()));

		Calendar reschedule = DateUtilities.newCalendar(2011, 8, 2, 9, 0, 0);
		WorkNegotiationDTO dto = new WorkNegotiationDTO();
		dto.setIsScheduleRange(false);
		dto.setScheduleFromString(DateUtilities.getISO8601(reschedule));
		dto.setNote("note comment");

		authenticationService.setCurrentUser(contractor);

		WorkRescheduleNegotiation negotiation = workNegotiationService.createRescheduleNegotiation(work.getId(), dto);

		assertNotNull(negotiation);

		authenticationService.setCurrentUser(employee);

		workNegotiationService.approveNegotiation(negotiation.getId());

		work = workService.findWork(work.getId());

		assertEquals(reschedule.getTime(), work.getScheduleFrom().getTime());

		WorkResource workResource = workService.findActiveWorkResource(work.getId());

		assertFalse(workResource.isCheckedIn());
	}

	@Test
	public void test_rescheduleDeclined() throws Exception {

		laneService.addUserToCompanyLane3(contractor.getId(), employee.getCompany().getId());
		authenticationService.setCurrentUser(employee);
		Work work = newWork(employee.getId());

		workRoutingService.addToWorkResources(work.getId(), contractor.getId());
		workService.acceptWork(contractor.getId(), work.getId());

		Calendar reschedule = DateUtilities.newCalendar(2011, 8, 2, 9, 0, 0);
		WorkNegotiationDTO dto = new WorkNegotiationDTO();
		dto.setIsScheduleRange(false);
		dto.setScheduleFromString(DateUtilities.getISO8601(reschedule));
		dto.setNote("note comment");
		dto.setDeclinedNote("declined note");

		authenticationService.setCurrentUser(contractor);

		WorkRescheduleNegotiation negotiation = workNegotiationService.createRescheduleNegotiation(work.getId(), dto);

		assertTrue(workService.isUserActiveResourceForWork(contractor.getId(), work.getId()));
		assertNotNull(negotiation);

		authenticationService.setCurrentUser(employee);

		assertTrue(workNegotiationService.declineNegotiation(negotiation.getId(), null, null).isSuccessful());

		work = workService.findWork(work.getId());

		assertFalse(workService.isUserActiveResourceForWork(contractor.getId(), work.getId()));
		assertFalse(reschedule.getTime().equals(work.getScheduleFrom().getTime()));
	}

	// Negotiation by buyer/admin

	@Test
	public void test_buyerRescheduleApproved() throws Exception {

		laneService.addUserToCompanyLane3(contractor.getId(), employee.getCompany().getId());
		authenticationService.setCurrentUser(employee);
		Work work = newWork(employee.getId());

		workRoutingService.addToWorkResources(work.getId(), contractor.getId());
		workService.acceptWork(contractor.getId(), work.getId());

		Calendar reschedule = DateUtilities.newCalendar(2011, 8, 2, 9, 0, 0);
		WorkNegotiationDTO dto = new WorkNegotiationDTO();
		dto.setIsScheduleRange(false);
		dto.setScheduleFromString(DateUtilities.getISO8601(reschedule));
		dto.setNote("note comment");

		authenticationService.setCurrentUser(employee);

		WorkRescheduleNegotiation negotiation = workNegotiationService.createRescheduleNegotiation(work.getId(), dto);

		assertNotNull(negotiation);
		assertFalse(negotiation.isInitiatedByResource());

		authenticationService.setCurrentUser(contractor);

		workNegotiationService.approveNegotiation(negotiation.getId());

		work = workService.findWork(work.getId());

		assertTrue(reschedule.getTime().equals(work.getScheduleFrom().getTime()));
		assertTrue(workService.isUserActiveResourceForWork(contractor.getId(), work.getId()));
		assertTrue(work.isActive());
	}

	@Test
	public void test_buyerRescheduleDeclined() throws Exception {

		laneService.addUserToCompanyLane3(contractor.getId(), employee.getCompany().getId());
		authenticationService.setCurrentUser(employee);
		Work work = newWork(employee.getId());

		workRoutingService.addToWorkResources(work.getId(), contractor.getId());
		workService.acceptWork(contractor.getId(), work.getId());

		Calendar reschedule = DateUtilities.newCalendar(2011, 8, 2, 9, 0, 0);
		WorkNegotiationDTO dto = new WorkNegotiationDTO();
		dto.setIsScheduleRange(false);
		dto.setScheduleFromString(DateUtilities.getISO8601(reschedule));
		dto.setNote("note comment");
		dto.setDeclinedNote("declined note");

		authenticationService.setCurrentUser(employee);

		WorkRescheduleNegotiation negotiation = workNegotiationService.createRescheduleNegotiation(work.getId(), dto);

		assertNotNull(negotiation);
		assertFalse(negotiation.isInitiatedByResource());

		authenticationService.setCurrentUser(contractor);

		assertTrue(workService.isUserActiveResourceForWork(contractor.getId(), work.getId()));

		assertTrue(workNegotiationService.declineNegotiation(negotiation.getId(), null, null).isSuccessful());

		work = workService.findWork(work.getId());

		assertTrue(reschedule.getTime().equals(work.getScheduleFrom().getTime()));
		assertFalse(workService.isUserActiveResourceForWork(contractor.getId(), work.getId()));
		assertTrue(work.isSent());
	}

	// Negotiation by a non-resource

	@Test(expected = IllegalStateException.class)
	public void test_rescheduleCreatedByNonActiveWorkResource() throws Exception {
		User contractor1 = newContractorIndependentLane4ReadyWithCashBalanceWithTaxEntity(true);
		User contractor2 = newContractorIndependentLane4ReadyWithCashBalanceWithTaxEntity(true);

		laneService.addUserToCompanyLane3(contractor1.getId(), employee.getCompany().getId());
		laneService.addUserToCompanyLane3(contractor2.getId(), employee.getCompany().getId());

		Work work = newWork(employee.getId());

		workRoutingService.addToWorkResources(work.getWorkNumber(), Sets.newHashSet(contractor1.getUserNumber(), contractor2.getUserNumber()));
		workService.acceptWork(contractor1.getId(), work.getId());

		WorkNegotiationDTO dto = new WorkNegotiationDTO();
		dto.setIsScheduleRange(false);
		dto.setScheduleFromString(DATE_STRING);
		dto.setNote("note comment");

		authenticationService.setCurrentUser(contractor2);

		workNegotiationService.createRescheduleNegotiation(work.getId(), dto);
	}

	// Negotiation on non-negotiable work

	@Test(expected = IllegalStateException.class)
	public void test_rescheduleOnNonReschedulableWork() throws Exception {

		laneService.addUserToCompanyLane3(contractor.getId(), employee.getCompany().getId());
		authenticationService.setCurrentUser(employee);
		Work work = newWork(employee.getId());

		workRoutingService.addToWorkResources(work.getId(), contractor.getId());

		WorkNegotiationDTO dto = new WorkNegotiationDTO();
		dto.setIsScheduleRange(false);
		dto.setScheduleFromString(DATE_STRING);
		dto.setNote("note comment");

		authenticationService.setCurrentUser(contractor);

		workNegotiationService.createRescheduleNegotiation(work.getId(), dto);
	}

	// Approval by a non-buyer

	@Test(expected = IllegalStateException.class)
	public void test_rescheduleApprovedByNonBuyer() throws Exception {

		laneService.addUserToCompanyLane3(contractor.getId(), employee.getCompany().getId());
		authenticationService.setCurrentUser(employee);
		Work work = newWork(employee.getId());

		workRoutingService.addToWorkResources(work.getId(), contractor.getId());

		WorkNegotiationDTO dto = new WorkNegotiationDTO();
		dto.setIsScheduleRange(false);
		dto.setScheduleFromString(DATE_STRING);
		dto.setNote("note comment");

		authenticationService.setCurrentUser(contractor);

		WorkRescheduleNegotiation negotiation = workNegotiationService.createRescheduleNegotiation(work.getId(), dto);

		workNegotiationService.approveNegotiation(negotiation.getId());
	}

	// Decline by a non-buyer

	@Test(expected = IllegalStateException.class)
	public void rescheduleDeclinedByNonBuyer() throws Exception {

		laneService.addUserToCompanyLane3(contractor.getId(), employee.getCompany().getId());
		authenticationService.setCurrentUser(employee);
		Work work = newWork(employee.getId());

		workRoutingService.addToWorkResources(work.getId(), contractor.getId());

		WorkNegotiationDTO dto = new WorkNegotiationDTO();
		dto.setIsScheduleRange(false);
		dto.setScheduleFromString(DATE_STRING);
		dto.setNote("note comment");

		authenticationService.setCurrentUser(contractor);

		WorkRescheduleNegotiation negotiation = workNegotiationService.createRescheduleNegotiation(work.getId(), dto);

		assertFalse(workNegotiationService.declineNegotiation(negotiation.getId(), null, null).isSuccessful());
	}


	@Test
	public void test_spendLimitIncreaseCreatedWithNote() throws Exception {

		laneService.addUserToCompanyLane3(contractor.getId(), employee.getCompany().getId());
		authenticationService.setCurrentUser(employee);
		Work work = newWork(employee.getId());

		workRoutingService.addToWorkResources(work.getId(), contractor.getId());
		workService.acceptWork(contractor.getId(), work.getId());

		WorkNegotiationDTO dto = new WorkNegotiationDTO();
		dto.setPriceNegotiation(true);
		dto.setPricingStrategyId(FLAT_PRICING_STRATEGY);
		dto.setFlatPrice(500.00);
		dto.setNote("Need more!");

		authenticationService.setCurrentUser(contractor);

		WorkNegotiationResponse negotiationResponse = workNegotiationService.createExpenseIncreaseNegotiation(work.getId(), dto);
		WorkExpenseNegotiation negotiation = (WorkExpenseNegotiation)workNegotiationService.findById(negotiationResponse.getWorkNegotiationId());
		assertNotNull(negotiation.getNote());
		assertEquals("Need more!", negotiation.getNote().getContent());
	}


	@Test
	public void test_spendLimitIncreaseApproved() throws Exception {

		laneService.addUserToCompanyLane3(contractor.getId(), employee.getCompany().getId());
		authenticationService.setCurrentUser(employee);
		Work work = newWork(employee.getId());

		workRoutingService.addToWorkResources(work.getId(), contractor.getId());
		workService.acceptWork(contractor.getId(), work.getId());

		WorkNegotiationDTO dto = new WorkNegotiationDTO();
		dto.setPriceNegotiation(true);
		dto.setPricingStrategyId(FLAT_PRICING_STRATEGY);
		dto.setAdditionalExpenses(500.00);
		dto.setNote("note comment");

		authenticationService.setCurrentUser(contractor);

		WorkNegotiationResponse negotiationResponse = workNegotiationService.createExpenseIncreaseNegotiation(work.getId(), dto);

		WorkExpenseNegotiation negotiation = (WorkExpenseNegotiation)workNegotiationService.findById(negotiationResponse.getWorkNegotiationId());
		assertTrue(negotiation.getPricingStrategy() instanceof FlatPricePricingStrategy);
		assertEquals(BigDecimal.valueOf(90.91).setScale(2), ((FlatPricePricingStrategy) negotiation.getPricingStrategy()).getFlatPrice().setScale(2,BigDecimal.ROUND_UP));
		assertEquals(BigDecimal.valueOf(500).setScale(2),
				(negotiation.getPricingStrategy()).getFullPricingStrategy().
						getAdditionalExpenses().setScale(2,BigDecimal.ROUND_UP));

		authenticationService.setCurrentUser(employee);

		workNegotiationService.approveNegotiation(negotiation.getId());

		work = workService.findWork(work.getId());

		assertTrue(work.getPricingStrategy() instanceof FlatPricePricingStrategy);
		assertEquals(BigDecimal.valueOf(500).setScale(2),
				(work.getPricingStrategy()).getFullPricingStrategy().
						getAdditionalExpenses().setScale(2, BigDecimal.ROUND_UP));
	}


	@Test
	public void test_spendLimitWithMoreExpenses() throws Exception {

		laneService.addUserToCompanyLane3(contractor.getId(), employee.getCompany().getId());
		authenticationService.setCurrentUser(employee);
		Work work = newWork(employee.getId());

		workRoutingService.addToWorkResources(work.getId(), contractor.getId());
		workService.acceptWork(contractor.getId(), work.getId());

		WorkNegotiationDTO dto = new WorkNegotiationDTO();
		dto.setPriceNegotiation(true);
		dto.setPricingStrategyId(FLAT_PRICING_STRATEGY);
		dto.setFlatPrice(200.00);
		dto.setSpendLimitNegotiationTypeCode(SpendLimitNegotiationType.NEED_MORE_EXPENSES);
		dto.setAdditionalExpenses(100.00);
		dto.setNote("Need more!");

		authenticationService.setCurrentUser(contractor);
		workNegotiationService.createExpenseIncreaseNegotiation(work.getId(), dto);

	}

	@Test
	public void test_spendLimitWithMoreExpensesApprovedWithWorkCompletedForLessDoesNotOverrideApprovedValue() throws Exception {

		laneService.addUserToCompanyLane3(contractor.getId(), employee.getCompany().getId());
		authenticationService.setCurrentUser(employee);
		Work work = newWork(employee.getId());

		workRoutingService.addToWorkResources(work.getId(), contractor.getId());
		workService.acceptWork(contractor.getId(), work.getId());

		WorkNegotiationDTO dto = new WorkNegotiationDTO();
		dto.setPriceNegotiation(true);
		dto.setSpendLimitNegotiationTypeCode(SpendLimitNegotiationType.NEED_MORE_EXPENSES);
		dto.setAdditionalExpenses(100.00);
		dto.setNote("Need more!");

		authenticationService.setCurrentUser(contractor);
		WorkNegotiationResponse negotiationResponse = workNegotiationService.createExpenseIncreaseNegotiation(work.getId(), dto);

		authenticationService.setCurrentUser(employee);
		WorkExpenseNegotiation negotiation = (WorkExpenseNegotiation)workNegotiationService.findById(negotiationResponse.getWorkNegotiationId());

		workNegotiationService.approveNegotiation(negotiation.getId());

		CompleteWorkDTO complete = new CompleteWorkDTO();
		complete.setResolution("Finished!");
		complete.setAdditionalExpenses(50.00);

		authenticationService.setCurrentUser(employee);
		workService.completeWork(work.getId(), complete);

		work = workService.findWork(work.getId());
		WorkResource resource = workService.findActiveWorkResource(work.getId());

		assertEquals(100, work.getPricingStrategy().getFullPricingStrategy().getAdditionalExpenses().intValue());
		assertEquals(50, resource.getAdditionalExpenses().intValue());
	}

	@Test(expected = IllegalStateException.class)
	public void test_negotiationExpirationDate() throws Exception {

		laneService.addUserToCompanyLane3(contractor.getId(), employee.getCompany().getId());
		authenticationService.setCurrentUser(employee);
		Work work = newWork(employee.getId());

		workRoutingService.addToWorkResources(work.getId(), contractor.getId());

		WorkNegotiationDTO dto = new WorkNegotiationDTO();
		dto.setExpiresOn(DateUtilities.getISO8601(DateUtilities.addTime(Calendar.getInstance(), 1, Constants.HOUR)));
		dto.setScheduleNegotiation(true);
		dto.setScheduleFromString(DateUtilities.getISO8601(DateUtilities.addMinutes(Calendar.getInstance(), 30)));
		dto.setNote("note comment");

		authenticationService.setCurrentUser(contractor);
		workNegotiationService.createNegotiation(work.getId(), dto);
	}


	// resource can't approve on behalf of themselves
	@Test
	public void testOnBehalfOfInitiatedByResource() throws Exception {

		laneService.addUserToCompanyLane3(contractor.getId(), employee.getCompany().getId());
		authenticationService.setCurrentUser(employee);
		Work work = newWork(employee.getId());

		workRoutingService.addToWorkResources(work.getId(), contractor.getId());

		WorkNegotiationDTO dto = new WorkNegotiationDTO();
		dto.setScheduleNegotiation(true);
		dto.setScheduleFromString(DateUtilities.getISO8601(DateUtilities.addMinutes(Calendar.getInstance(), 30)));
		dto.setNote("note comment");

		authenticationService.setCurrentUser(contractor);
		WorkNegotiationResponse response = workNegotiationService.createNegotiation(work.getId(), dto);
		assertNotNull(response);

		assertFalse(workNegotiationService.approveNegotiation(response.getWorkNegotiationId(), contractor.getId()).isSuccessful());
	}

	@Test
	public void testDontAllowApprovalAfterWorkApproved() throws Exception {
		User employee = newFirstEmployeeWithCashBalanceAndPaymentTermsEnabled();
		authenticationService.setCurrentUser(employee);

		User contractor = newContractor();
		laneService.addUserToCompanyLane2(contractor.getId(), employee.getCompany().getId());

		Work work = newWorkWithPaymentTerms(employee.getId(), 30);

		assertTrue(work.hasPaymentTerms());

		workRoutingService.addToWorkResources(work.getId(), contractor.getId());
		workService.acceptWork(contractor.getId(), work.getId());
		workService.updateWorkProperties(work.getId(),
				CollectionUtilities.newStringMap("resolution", "Complete the assignment"));

		WorkNegotiationDTO dto = new WorkNegotiationDTO();
		dto.setAdditionalExpenses(300.00);
		dto.setNote("note comment");
		authenticationService.setCurrentUser(contractor);
		WorkNegotiationResponse response = workNegotiationService.createExpenseIncreaseNegotiation(work.getId(), dto);
		assertTrue(response.isSuccessful());
		workService.completeWork(work.getId(), new CompleteWorkDTO());

		authenticationService.setCurrentUser(employee);
		workService.closeWork(work.getId());
		assertFalse(workNegotiationService.approveNegotiation(response.getWorkNegotiationId()).isSuccessful());

	}

	@Test
	public void test_approvedNegotiation() throws Exception {

		laneService.addUserToCompanyLane3(contractor.getId(), employee.getCompany().getId());
		authenticationService.setCurrentUser(employee);
		Work work = newWork(employee.getId());

		workRoutingService.addToWorkResources(work.getId(), contractor.getId());
		workService.acceptWork(contractor.getId(), work.getId());

		WorkNegotiationDTO dto = new WorkNegotiationDTO();
		dto.setIsScheduleRange(false);
		dto.setScheduleFromString(DATE_STRING);
		dto.setNote("note comment");

		authenticationService.setCurrentUser(contractor);

		WorkRescheduleNegotiation negotiation = workNegotiationService.createRescheduleNegotiation(work.getId(), dto);

		assertTrue(workService.isUserActiveResourceForWork(contractor.getId(), work.getId()));
		assertNotNull(negotiation);

		authenticationService.setCurrentUser(employee);

		assertTrue(workNegotiationService.approveNegotiation(negotiation.getId(), null).isSuccessful());

		assertFalse(workNegotiationService.approveNegotiation(negotiation.getId(), null).isSuccessful());
		assertFalse(workNegotiationService.declineNegotiation(negotiation.getId(), null, null).isSuccessful());

		authenticationService.setCurrentUser(contractor);
		assertFalse(workNegotiationService.cancelNegotiation(negotiation.getId()).isSuccessful());
	}

	@Test
	public void approveScheduleNegotiation_withDateTimeSet_appointmentSet() throws Exception {

		laneService.addUserToCompanyLane3(contractor.getId(), employee.getCompany().getId());
		authenticationService.setCurrentUser(employee);
		Work work = newWork(employee.getId());

		workRoutingService.addToWorkResources(work.getId(), contractor.getId());
		workService.acceptWork(contractor.getId(), work.getId());

		WorkNegotiationDTO dto = new WorkNegotiationDTO();
		dto.setIsScheduleRange(false);
		dto.setScheduleFromString(DATE_STRING);
		dto.setNote("note comment");

		authenticationService.setCurrentUser(contractor);

		WorkRescheduleNegotiation negotiation = workNegotiationService.createRescheduleNegotiation(work.getId(), dto);

		authenticationService.setCurrentUser(employee);

		workNegotiationService.approveNegotiation(negotiation.getId(), null);

		WorkResource workResource = workService.findWorkResource(contractor.getId(), work.getId());

		assertNotNull(workResource.getAppointment().getFrom());
		assertEquals(workResource.getAppointment().getFrom().getTime(), negotiation.getScheduleFrom().getTime());
	}

	@Test
	public void approveScheduleNegotiation_withDateRange_appointmentSet() throws Exception {

		laneService.addUserToCompanyLane3(contractor.getId(), employee.getCompany().getId());
		authenticationService.setCurrentUser(employee);
		Calendar fiveDaysFromToday = Calendar.getInstance();
		fiveDaysFromToday.add(Calendar.DAY_OF_MONTH, 5);
		Calendar today = Calendar.getInstance();
		Work work = newWorkWithDateRange(employee.getId(), new WorkDTO(), fiveDaysFromToday, today);

		workRoutingService.addToWorkResources(work.getId(), contractor.getId());
		workService.acceptWork(contractor.getId(), work.getId());

		WorkNegotiationDTO dto = new WorkNegotiationDTO();
		dto.setIsScheduleRange(true);
		Calendar tomorrow = Calendar.getInstance();
		tomorrow.add(Calendar.DAY_OF_MONTH, 1);
		dto.setScheduleFromString(new SimpleDateFormat("yyyy-MM-dd'T'HH:mmZ").format(tomorrow.getTime()));
		dto.setNote("note comment");

		authenticationService.setCurrentUser(contractor);

		WorkRescheduleNegotiation negotiation = workNegotiationService.createRescheduleNegotiation(work.getId(), dto);

		authenticationService.setCurrentUser(employee);
		workNegotiationService.approveNegotiation(negotiation.getId(), null);
		WorkResource workResource = workService.findWorkResource(contractor.getId(), work.getId());

		assertNotNull(workResource.getAppointment().getFrom());
		assertEquals(workResource.getAppointment().getFrom().getTime(), negotiation.getScheduleFrom().getTime());
	}

	@Test
	public void test_declinedNegotiation() throws Exception {

		laneService.addUserToCompanyLane3(contractor.getId(), employee.getCompany().getId());
		authenticationService.setCurrentUser(employee);
		Work work = newWork(employee.getId());

		workRoutingService.addToWorkResources(work.getId(), contractor.getId());
		workService.acceptWork(contractor.getId(), work.getId());

		WorkNegotiationDTO dto = new WorkNegotiationDTO();
		dto.setIsScheduleRange(false);
		dto.setScheduleFromString(DATE_STRING);
		dto.setNote("note comment");
		dto.setDeclinedNote("declined note");

		authenticationService.setCurrentUser(contractor);

		WorkRescheduleNegotiation negotiation = workNegotiationService.createRescheduleNegotiation(work.getId(), dto);

		assertTrue(workService.isUserActiveResourceForWork(contractor.getId(), work.getId()));
		assertNotNull(negotiation);

		authenticationService.setCurrentUser(employee);

		assertTrue(workNegotiationService.declineNegotiation(negotiation.getId(), null, null).isSuccessful());

		assertFalse(workNegotiationService.approveNegotiation(negotiation.getId(), null).isSuccessful());
		assertFalse(workNegotiationService.declineNegotiation(negotiation.getId(), null, null).isSuccessful());

		authenticationService.setCurrentUser(contractor);
		assertFalse(workNegotiationService.cancelNegotiation(negotiation.getId()).isSuccessful());
	}

	@Test
	public void test_cancelledNegotiation() throws Exception {

		laneService.addUserToCompanyLane3(contractor.getId(), employee.getCompany().getId());
		authenticationService.setCurrentUser(employee);
		Work work = newWork(employee.getId());

		workRoutingService.addToWorkResources(work.getId(), contractor.getId());
		workService.acceptWork(contractor.getId(), work.getId());

		WorkNegotiationDTO dto = new WorkNegotiationDTO();
		dto.setIsScheduleRange(false);
		dto.setScheduleFromString(DATE_STRING);
		dto.setNote("note comment");

		authenticationService.setCurrentUser(contractor);

		WorkRescheduleNegotiation negotiation = workNegotiationService.createRescheduleNegotiation(work.getId(), dto);

		assertTrue(workService.isUserActiveResourceForWork(contractor.getId(), work.getId()));
		assertNotNull(negotiation);

		assertTrue(workNegotiationService.cancelNegotiation(negotiation.getId()).isSuccessful());

		authenticationService.setCurrentUser(employee);
		assertFalse(workNegotiationService.approveNegotiation(negotiation.getId(), null).isSuccessful());
		assertFalse(workNegotiationService.declineNegotiation(negotiation.getId(), null, null).isSuccessful());

		authenticationService.setCurrentUser(contractor);
		assertFalse(workNegotiationService.cancelNegotiation(negotiation.getId()).isSuccessful());
	}

	@Test
	public void cancelAllNegotiationsForWork_worker1CreatesNegotiationWorker2Applies_noActiveNegotiationsExistForWork() throws Exception  {
		User contractor2 = newContractorIndependentLane4ReadyWithCashBalanceWithTaxEntity(true);

		laneService.addUserToCompanyLane3(contractor.getId(), employee.getCompany().getId());
		laneService.addUserToCompanyLane3(contractor2.getId(), employee.getCompany().getId());

		authenticationService.setCurrentUser(employee);
		WorkDTO workDTO = new WorkDTO();
		workDTO.setAssignToFirstResource(false);
		Work work = newWork(employee.getId(), workDTO);

		workRoutingService.addToWorkResources(work.getId(), contractor.getId());
		workRoutingService.addToWorkResources(work.getId(), contractor2.getId());

		WorkNegotiationDTO dto = new WorkNegotiationDTO();
		dto.setPriceNegotiation(true);
		dto.setPricingStrategyId(FLAT_PRICING_STRATEGY);
		dto.setFlatPrice(500.00);
		dto.setScheduleNegotiation(true);
		dto.setIsScheduleRange(false);
		dto.setScheduleFromString(DATE_STRING);
		dto.setNote("Need more!");

		authenticationService.setCurrentUser(contractor);
		WorkNegotiationResponse negotiation = workNegotiationService.createNegotiation(work.getId(), dto);
		assertNotNull(negotiation);
		assertTrue(negotiation.isSuccessful());

		authenticationService.setCurrentUser(contractor2);
		WorkNegotiationResponse negotiation2 = workNegotiationService.createApplyNegotiation(work.getId(), new WorkNegotiationDTO());
		assertNotNull(negotiation2);
		assertTrue(negotiation2.isSuccessful());

		// Cancel all negotiations
		workNegotiationService.cancelAllNegotiationsForWork(work.getId());

		assertEquals(workNegotiationService.findAllNegotiationsByWorkId(work.getId()).size(), 0);
	}

	@Test
	public void test_negotiationWithInsufficientFunds_fails() throws Exception {
		User companyAdmin = newFirstEmployeeWithCashBalanceAndPaymentTermsEnabled(); //logic changed that allows admins to approve budgets regardless of budget limits
		User employeeWithBudget = newEmployeeForCompanyWithSpendLimit(companyAdmin.getCompany().getId(), BigDecimal.valueOf(750.00));

		Long contractorId = contractor.getId();
		laneService.addUserToCompanyLane3(contractorId, employeeWithBudget.getCompany().getId());
		authenticationService.setCurrentUser(employeeWithBudget);
		Work work = newWorkWithPaymentTerms(employeeWithBudget.getId(), 30);

		// route work
		workRoutingService.addToWorkResources(work.getId(), contractorId);

		// accept work
		authenticationService.setCurrentUser(contractor);
		workService.acceptWork(contractor.getId(), work.getId());

		// request budget increase
		WorkNegotiationResponse negotiationResponse = createPriceNegotiation(800.00, work);
		WorkBudgetNegotiation negotiation = (WorkBudgetNegotiation)workNegotiationService.findById(negotiationResponse.getWorkNegotiationId());
		assertTrue(negotiation.getPricingStrategy() instanceof FlatPricePricingStrategy);
		assertEquals(BigDecimal.valueOf(800.00).setScale(2), ((FlatPricePricingStrategy) negotiation.getPricingStrategy()).getFlatPrice().setScale(2));

		// complete work
		workService.updateWorkProperties(work.getId(), CollectionUtilities.newStringMap("resolution", "Complete the assignment"));
		workService.completeWork(work.getId(), new CompleteWorkDTO());

		// approve negotiation
		authenticationService.setCurrentUser(employeeWithBudget);
		WorkNegotiationResponse approveResponse = workNegotiationService.approveNegotiation(negotiation.getId());
		assertEquals(WorkNegotiationResponseStatus.FAILURE, approveResponse.getStatus());
		assertEquals("You cannot accept this budget increase because the assignment cost would exceed your $750.00 spend limit.",
			approveResponse.getMessages().iterator().next());
	}

	/**
	 * This test reproduces the issue reported in APP-12568
	 * We have an assignment created by one user with a $750 spend limit
	 * The worker accepts, requests a budget increase to $1000, and then submits for approval
	 * Another employee with $1000 budget approves the budget increase
	 * This should be allowed but was throwing an InsufficientSpendLimitException
	 */
	@Test
	public void test_negotiationWithSufficientFunds() throws Exception {

		User employeeWithBudget = newFirstEmployeeWithCashBalanceAndPaymentTermsEnabled();

		User employeeWithHigherBudget = newEmployeeForCompanyWithSpendLimit(employeeWithBudget.getCompany().getId(),
			BigDecimal.valueOf(1000.00));

		Long contractorId = contractor.getId();
		laneService.addUserToCompanyLane3(contractorId, employeeWithBudget.getCompany().getId());

		// create work
		authenticationService.setCurrentUser(employeeWithBudget);
		Work work = newWorkWithPaymentTerms(employeeWithBudget.getId(), 30);
		Long workId = work.getId();

		// route work
		workRoutingService.addToWorkResources(workId, contractorId);

		// accept work
		authenticationService.setCurrentUser(contractor);
		workService.acceptWork(contractor.getId(), work.getId());

		// request budget increase
		WorkNegotiationResponse negotiationResponse = createPriceNegotiation(800.00, work);

		// complete work
		workService.updateWorkProperties(work.getId(), CollectionUtilities.newStringMap("resolution", "Complete the assignment"));
		workService.completeWork(work.getId(), new CompleteWorkDTO());

		// approve budget increase
		authenticationService.setCurrentUser(employeeWithHigherBudget);
		WorkNegotiationResponse approveResponse = workNegotiationService.approveNegotiation(negotiationResponse.getWorkNegotiationId());
		assertEquals(WorkNegotiationResponseStatus.SUCCESS, approveResponse.getStatus());
	}

	/**
	 * This test reproduces another issue discovered while working on APP-12568
	 * We have an assignment created by one user with a $750 spend limit
	 * The worker accepts, requests a budget increase to $750, and then submits for approval
	 * The total buyer cost becomes $825 (with fee applied) which is more than budget.
	 * Therefore the approval attempt should result in an error
	 */
	@Test
	public void test_negotiationWithInsufficientFundsToCoverFee() throws Exception {
		User companyAdmin = newFirstEmployeeWithCashBalanceAndPaymentTermsEnabled(); //logic changed that allows admins to approve budgets regardless of budget limits
		User employeeWithBudget = newEmployeeForCompanyWithSpendLimit(companyAdmin.getCompany().getId(), BigDecimal.valueOf(750.00));

		Long contractorId = contractor.getId();
		laneService.addUserToCompanyLane3(contractorId, employeeWithBudget.getCompany().getId());

		// create work
		authenticationService.setCurrentUser(employeeWithBudget);
		Work work = newWorkWithPaymentTerms(employeeWithBudget.getId(), 30);

		// route work
		workRoutingService.addToWorkResources(work.getId(), contractorId);

		// accept work
		authenticationService.setCurrentUser(contractor);
		workService.acceptWork(contractor.getId(), work.getId());

		// request budget increase
		WorkNegotiationResponse negotiationResponse = createPriceNegotiation(750.00, work);

		// complete work
		workService.updateWorkProperties(work.getId(), CollectionUtilities.newStringMap("resolution", "Complete the assignment"));
		workService.completeWork(work.getId(), new CompleteWorkDTO());

		// approve budget increase
		authenticationService.setCurrentUser(employeeWithBudget);
		WorkNegotiationResponse approveResponse = workNegotiationService.approveNegotiation(negotiationResponse.getWorkNegotiationId());
		assertEquals(WorkNegotiationResponseStatus.FAILURE, approveResponse.getStatus());
		assertEquals("You cannot accept this budget increase because the assignment cost would exceed your $750.00 spend limit.",
			approveResponse.getMessages().iterator().next());
	}

	public User newEmployeeForCompanyWithSpendLimit(Long companyId, BigDecimal spendLimit) throws Exception {

		UserDTO userDTO = new UserDTO();
		String userName = "contractorUser" + RandomUtilities.nextLong();
		userDTO.setEmail(userName + "@workmarket.com");
		userDTO.setFirstName(userName + "@workmarket.com");
		userDTO.setLastName(userName + "@workmarket.com");
		userDTO.setPassword("" + RandomUtilities.nextLong());
		userDTO.setSpendLimit(spendLimit);
		return registrationService.registerNewForCompany(userDTO, companyId,
			new Long[]{AclRole.ACL_STAFF, AclRole.ACL_WORKER, AclRole.ACL_MANAGER});
	}

	private WorkNegotiationResponse createPriceNegotiation (Double flatPrice, Work work) throws Exception {

		WorkNegotiationDTO dto = new WorkNegotiationDTO();
		dto.setPriceNegotiation(true);
		dto.setPricingStrategyId(FLAT_PRICING_STRATEGY);
		dto.setFlatPrice(flatPrice);
		dto.setScheduleNegotiation(false);
		dto.setIsScheduleRange(false);
		dto.setScheduleFromString(DATE_STRING);
		dto.setNote("Need more!");

		return workNegotiationService.createBudgetIncreaseNegotiation(work.getId(), dto);
	}
}
