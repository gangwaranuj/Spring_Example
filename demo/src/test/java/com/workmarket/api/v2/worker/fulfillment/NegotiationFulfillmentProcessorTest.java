package com.workmarket.api.v2.worker.fulfillment;

import com.workmarket.api.exceptions.MessageSourceApiException;
import com.workmarket.api.v2.worker.model.AssignmentApplicationDTO;
import com.workmarket.api.v2.worker.model.NegotiationDTO;
import com.workmarket.api.v2.worker.model.RescheduleDTO;
import com.workmarket.api.v2.worker.service.NegotiationService;
import com.workmarket.api.v2.worker.service.SearchService;
import com.workmarket.api.v2.worker.service.UserService;
import com.workmarket.api.v2.worker.validators.NegotiationValidator;
import com.workmarket.domains.model.User;
import com.workmarket.domains.work.service.WorkNegotiationService;
import com.workmarket.service.business.dto.WorkNegotiationDTO;
import com.workmarket.service.business.wrapper.WorkNegotiationResponse;
import com.workmarket.thrift.core.Company;
import com.workmarket.thrift.work.PricingStrategy;
import com.workmarket.thrift.work.Work;
import com.workmarket.thrift.work.WorkResponse;
import com.workmarket.velvetrope.Doorman;
import com.workmarket.web.exceptions.HttpException404;
import com.workmarket.web.helpers.MessageBundleHelper;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.Mock;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.anyObject;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class NegotiationFulfillmentProcessorTest {

	private NegotiationFulfillmentProcessor fulfillmentProcessor;
	@Mock private NegotiationService negotiationService;
	@Mock private WorkNegotiationService workNegotiationService;
	@Mock private UserService userService;
	@Mock private NegotiationValidator negotiationValidator;
	@Mock private SearchService searchService;
	@Mock private MessageBundleHelper messageHelper;
	@Mock private Doorman doorman;

	private WorkResponse workResponse;
	private WorkNegotiationResponse goodResponse;
	private WorkNegotiationResponse badResponse;
	private AssignmentApplicationDTO dto;
	private AssignmentApplicationDTO invalidScheduleDTO;
	private AssignmentApplicationDTO invalidExpirationDTO;
	private Work targetWork;
	private User user;

	@Before
	public void setup()
		throws Exception {

		fulfillmentProcessor = new NegotiationFulfillmentProcessor();

		final Company company = new Company();
		company.setName("name");

		final PricingStrategy pricingStrategy = new PricingStrategy();
		pricingStrategy.setId(1L);

		targetWork = new Work();
		targetWork.setId(1L);
		targetWork.setCompany(company);
		targetWork.setPricing(pricingStrategy);

		workResponse = new WorkResponse();
		workResponse.setWorkBundle(false);
		workResponse.setWork(targetWork);

		final WorkNegotiationResponse applyResponse = new WorkNegotiationResponse();
		final NegotiationDTO.Builder pricingBuilder = new NegotiationDTO.Builder().withFlatPrice(1000.0);
		final RescheduleDTO.Builder scheduleBuilder = new RescheduleDTO.Builder().withStart(2033333333333L);

		dto = new AssignmentApplicationDTO.Builder()
			.withPricing(pricingBuilder)
			.withSchedule(scheduleBuilder)
			.withMessage("message")
			.build();

		invalidScheduleDTO = new AssignmentApplicationDTO.Builder().build();
		invalidExpirationDTO = new AssignmentApplicationDTO.Builder().build();

		user = new User();
		user.setId(12345678L);

		negotiationService = mock(NegotiationService.class);
		when(negotiationService.getWorkForNegotiation(anyString(), anyString())).thenReturn(targetWork );
		when(negotiationService.getWorkForApply(anyString())).thenReturn(workResponse);
		when(negotiationService.applyForWork(eq(targetWork.getId()), eq(user.getId()), (WorkNegotiationDTO)anyObject()))
			.thenReturn(applyResponse);

		workNegotiationService = mock(WorkNegotiationService.class);
		when(workNegotiationService.createApplyNegotiation(
			eq(targetWork.getId()),
			eq(user.getId()),
			(WorkNegotiationDTO) anyObject()))
			.thenReturn(applyResponse);

		negotiationValidator = mock(NegotiationValidator.class);
		when(negotiationValidator.isUserValidForWork((User) anyObject(), (Work) anyObject())).thenReturn(true);
		when(negotiationValidator.isUserEligibleForWork((User) anyObject(), (Work) anyObject())).thenReturn(true);

		doorman = mock(Doorman.class);

		searchService = mock(SearchService.class);

		userService = mock(UserService.class);
		when(userService.getCurrentUser()).thenReturn(user);

		messageHelper = mock(MessageBundleHelper.class);
		when(messageHelper.getMessage("assignment.mobile.apply.success")).thenReturn("Good Success.");

		fulfillmentProcessor.setNegotiationService(negotiationService);
		fulfillmentProcessor.setWorkNegotiationService(workNegotiationService);
		fulfillmentProcessor.setUserService(userService);
		fulfillmentProcessor.setSearchService(searchService);
		fulfillmentProcessor.setNegotiationValidator(negotiationValidator);
		fulfillmentProcessor.setMessageHelper(messageHelper);
		fulfillmentProcessor.setDoorman(doorman);

		goodResponse = WorkNegotiationResponse.success();
		goodResponse.setWorkNegotiationId(433L);

		badResponse = WorkNegotiationResponse.fail();
		badResponse.addMessage("Didn't work");
		badResponse.addMessage("State not right");
	}

	@Test
	public void requestBudgetIncrease_goodData_goodResponse() throws Exception {
		final NegotiationDTO negotiationDTO = new NegotiationDTO.Builder()
			.withFlatPrice(1500.00)
			.withNote("This job is bigger than you think")
			.build();

		when(negotiationService.createBudgetIncreaseNegotiation(targetWork, negotiationDTO)).thenReturn(goodResponse);
		when(negotiationValidator.validateBudgetIncrease(negotiationDTO, targetWork)).thenReturn(new LinkedList());

		FulfillmentPayloadDTO response = fulfillmentProcessor.requestBudgetIncrease("82982", negotiationDTO);
		assertEquals(Boolean.TRUE, response.isSuccessful());
		assertEquals(1, ((Map) response.getPayload().get(0)).size());
		assertEquals(433L, ((Map) response.getPayload().get(0)).get("id"));
		assertNull(response.getPagination());
	}

	@Test
	public void requestBudgetIncrease_badServiceResponse_badResponse() throws Exception {
		final NegotiationDTO negotiationDTO = new NegotiationDTO.Builder()
			.withMaxHours(16.00)
			.withNote("This is a note")
			.build();

		when(negotiationService.createBudgetIncreaseNegotiation(targetWork, negotiationDTO)).thenReturn(badResponse);
		when(negotiationValidator.validateBudgetIncrease(negotiationDTO, targetWork)).thenReturn(new LinkedList());

		final FulfillmentPayloadDTO response = fulfillmentProcessor.requestBudgetIncrease("82982", negotiationDTO);

		assertEquals(Boolean.FALSE, response.isSuccessful());
		assertEquals(2, response.getPayload().size());
		assertEquals("Didn't work", response.getPayload().get(0));
		assertEquals("State not right", response.getPayload().get(1));
		assertNull(response.getPagination());
	}

	@Test
	public void requestBudgetIncrease_badData_badResponse() throws Exception {
		final NegotiationDTO negotiationDTO = new NegotiationDTO.Builder()
			.withFlatPrice(5.00)
			.withNote("This is a note")
			.build();

		final List errorList = new LinkedList();
		errorList.add("Budget increase must be greater than the existing work budget");
		errorList.add("Some other error");

		when(negotiationValidator.validateBudgetIncrease(negotiationDTO, targetWork)).thenReturn(errorList);

		final FulfillmentPayloadDTO response = fulfillmentProcessor.requestBudgetIncrease("82982", negotiationDTO);

		assertEquals(Boolean.FALSE, response.isSuccessful());
		assertEquals(2, response.getPayload().size());
		assertEquals("Budget increase must be greater than the existing work budget", response.getPayload().get(0));
		assertEquals("Some other error", response.getPayload().get(1));
		assertNull(response.getPagination());
	}

	@Test
	public void requestReimbursement_goodData_goodResponse() throws Exception {
		final NegotiationDTO negotiationDTO = new NegotiationDTO.Builder()
			.withReimbursement(500.00)
			.withNote("This is a note")
			.build();

		when(negotiationService.createReimbursementNegotiation(targetWork, negotiationDTO)).thenReturn(goodResponse);

		final FulfillmentPayloadDTO response = fulfillmentProcessor.requestReimbursement("82982", negotiationDTO);

		assertEquals(Boolean.TRUE, response.isSuccessful());
		assertEquals(1, ((Map) response.getPayload().get(0)).size());
		assertEquals(433L, ((Map) response.getPayload().get(0)).get("id"));
		assertNull(response.getPagination());
	}

	@Test
	public void requestReimbursement_badServiceResponse_badResponse() throws Exception {
		final NegotiationDTO negotiationDTO = new NegotiationDTO.Builder()
			.withReimbursement(500.00)
			.withNote("This is a note")
			.build();

		when(negotiationService.createReimbursementNegotiation(targetWork, negotiationDTO)).thenReturn(badResponse);

		final FulfillmentPayloadDTO response = fulfillmentProcessor.requestReimbursement("82982", negotiationDTO);

		assertEquals(Boolean.FALSE, response.isSuccessful());
		assertEquals(2, response.getPayload().size());
		assertEquals("Didn't work", response.getPayload().get(0));
		assertEquals("State not right", response.getPayload().get(1));
		assertNull(response.getPagination());
	}

	@Test
	public void requestBonus_goodData_goodResponse() throws Exception {
		final NegotiationDTO negotiationDTO = new NegotiationDTO.Builder()
			.withBonus(300.00)
			.withNote("Note my bonus!")
			.build();

		when(negotiationService.createBonusNegotiation(targetWork, negotiationDTO)).thenReturn(goodResponse);

		final FulfillmentPayloadDTO response = fulfillmentProcessor.requestBonus("82982", negotiationDTO);

		assertEquals(Boolean.TRUE, response.isSuccessful());
		assertEquals(1, ((Map) response.getPayload().get(0)).size());
		assertEquals(433L, ((Map) response.getPayload().get(0)).get("id"));
		assertNull(response.getPagination());
	}

	@Test
	public void requestBonus_badServiceResponse_badResponse() throws Exception {
		final NegotiationDTO negotiationDTO = new NegotiationDTO.Builder()
			.withBonus(132.00)
			.withNote("Gimme a bonus")
			.build();

		when(negotiationService.createBonusNegotiation(targetWork, negotiationDTO)).thenReturn(badResponse);

		final FulfillmentPayloadDTO response = fulfillmentProcessor.requestBonus("82982", negotiationDTO);

		assertEquals(Boolean.FALSE, response.isSuccessful());
		assertEquals(2, response.getPayload().size());
		assertEquals("Didn't work", response.getPayload().get(0));
		assertEquals("State not right", response.getPayload().get(1));
		assertNull(response.getPagination());
	}

	@Test
	public void apply_successfulFulfillment_SuccessfulResponse() throws Exception {

		final FulfillmentPayloadDTO response = fulfillmentProcessor.applyForAssignment("1", dto);

		verify(userService, times(1)).getCurrentUser();
		verify(negotiationService, times(1)).getWorkForApply("1");
		verify(negotiationService, never()).submitApplyToBundle((Work) anyObject(), (User) anyObject());
		verify(searchService, times(1)).reindexWork(targetWork.getId());

		assertTrue(response.isSuccessful());
		assertNull(response.getPagination());
		assertNull(response.getMessage());
		assertEquals("Good Success.", response.getPayload().get(0));
	}

	@Test
	public void apply_workNotFound_Throws404Exception() throws Exception {
		workResponse.setWork(null);

		try {
			final FulfillmentPayloadDTO response = fulfillmentProcessor.applyForAssignment("1", dto);
			fail("Expected an HttpException404 to be tossed");
		} catch (final HttpException404 he404) {
			verify(userService, times(1)).getCurrentUser();
			verify(negotiationService, times(1)).getWorkForApply("1");
			verify(negotiationService, never()).submitApplyToBundle((Work) anyObject(), (User) anyObject());
			verify(searchService, never()).reindexWork(targetWork.getId());
			assertEquals("assignment.notfound", he404.getMessage());
		} catch (final Exception e) {
			fail("Expected an HttpException404 to be tossed, instead was " + e.getClass());
		}
	}

	@Ignore @Test
	public void apply_validateThrowsMessageSourceApiException_throwsMessageSourceApiException() throws Exception {
		doThrow(new MessageSourceApiException("validate.failed"))
			.when(negotiationValidator).validateApplication(dto, targetWork, user);

		try {
			FulfillmentPayloadDTO response = fulfillmentProcessor.applyForAssignment("1", dto);
			fail("Expected a MessageSourceApiException to be tossed");
		} catch (MessageSourceApiException msae) {
			verify(userService, times(1)).getCurrentUser();
			verify(negotiationService, times(1)).getWorkForApply("1");
			verify(negotiationService, never()).submitApplyToBundle((Work) anyObject(), (User) anyObject());
			verify(negotiationService, never())
				.applyForWork(eq(targetWork.getId()), eq(user.getId()), (WorkNegotiationDTO) anyObject());
			verify(searchService, never()).reindexWork(targetWork.getId());
			assertEquals("validate.failed", msae.getMessage());
		} catch (Exception e) {
			fail("Expected an HttpException404 to be tossed, instead was " + e.getClass());
		}
	}

	@Test
	public void apply_workBundle_submitsApplyBundle()
		throws Exception {

		workResponse.setWorkBundle(true);

		FulfillmentPayloadDTO response = fulfillmentProcessor.applyForAssignment("1", dto);

		verify(userService, times(1)).getCurrentUser();
		verify(negotiationService, times(1)).getWorkForApply("1");
		verify(negotiationService, times(1)).submitApplyToBundle((Work) anyObject(), (User) anyObject());
		verify(searchService, times(1)).reindexWork(targetWork.getId());

		assertTrue(response.isSuccessful());
		assertNull(response.getPagination());
		assertNull(response.getMessage());
		assertEquals("Good Success.", response.getPayload().get(0));
	}

	@Test
	public void apply_applyThrowsIllegalStateException_throwsMessageSourceApiException() throws Exception {
		when(workNegotiationService.createApplyNegotiation(
			eq(targetWork.getId()),
			eq(user.getId()),
			(WorkNegotiationDTO) anyObject()))
			.thenThrow(new IllegalStateException("A test message"));

		try {
			FulfillmentPayloadDTO response = fulfillmentProcessor.applyForAssignment("1", dto);
			fail("Expected a MessageSourceApiException to be tossed.");
		} catch (MessageSourceApiException msae) {
			verify(userService, times(1)).getCurrentUser();
			verify(negotiationService, times(1)).getWorkForApply("1");
			verify(negotiationService, never()).submitApplyToBundle((Work) anyObject(), (User) anyObject());
			verify(searchService, never()).reindexWork(targetWork.getId());
			assertEquals("A test message", msae.getMessage());
		}
	}
}
