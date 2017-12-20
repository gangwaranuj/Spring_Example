package com.workmarket.thrift.work;

import com.google.common.collect.ImmutableSet;
import com.workmarket.domains.model.User;
import com.workmarket.domains.model.WorkResourcePagination;
import com.workmarket.domains.model.changelog.work.WorkChangeLog;
import com.workmarket.domains.model.changelog.work.WorkChangeLogPagination;
import com.workmarket.domains.work.model.Work;
import com.workmarket.domains.work.model.negotiation.WorkNegotiation;
import com.workmarket.service.business.BaseServiceIT;
import com.workmarket.service.business.dto.WorkNegotiationDTO;
import com.workmarket.service.business.event.EventFactory;
import com.workmarket.domains.work.service.WorkChangeLogService;
import com.workmarket.domains.work.service.WorkNegotiationService;
import com.workmarket.domains.work.service.audit.WorkActionRequest;
import com.workmarket.service.business.wrapper.AcceptWorkResponse;
import com.workmarket.service.business.wrapper.WorkNegotiationResponse;
import com.workmarket.service.infra.event.EventRouter;
import com.workmarket.service.thrift.transactional.TWorkService;
import com.workmarket.service.thrift.transactional.work.WorkRequestInfo;
import com.workmarket.service.thrift.transactional.work.WorkResponseBuilder;
import com.workmarket.utility.DateUtilities;
import org.hamcrest.Matcher;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Calendar;
import java.util.HashSet;
import java.util.List;

import static org.apache.commons.collections.CollectionUtils.isEmpty;
import static org.apache.commons.collections.CollectionUtils.isNotEmpty;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

@RunWith(SpringJUnit4ClassRunner.class)
public class WorkResponseBuilderIT extends BaseServiceIT {

	@Autowired TWorkService tWorkService;
	@Autowired WorkResponseBuilder workResponseBuilder;
	@Autowired WorkNegotiationService workNegotiationService;
	@Autowired WorkChangeLogService workChangeLogService;
	@Autowired EventFactory eventFactory;
	@Autowired EventRouter eventRouter;

	private User employee;
	private Work work;
	private User activeResource;
	private User otherResource;

	@Before
	public void setUp() throws Exception {
		employee = newFirstEmployeeWithCashBalance();
		work = newWork(employee.getId());
		activeResource = newContractorIndependentlane4Ready();
		otherResource = newContractorIndependentlane4Ready();

		laneService.addUserToCompanyLane2(activeResource.getId(), work.getCompany().getId());
		workRoutingService.addToWorkResources(work.getId(), activeResource.getId());
		workRoutingService.addToWorkResources(work.getId(), otherResource.getId());
	}

	@Ignore
	@Test
	public void buildNotes_AcceptedOnBehalfWithNote_NoteNotVisibleToResource() throws Exception {
		AcceptWorkOfferRequest acceptWorkOfferRequest = new AcceptWorkOfferRequest()
				.setWorkAction(new WorkActionRequest(work.getWorkNumber())
						.setResourceUserNumber(activeResource.getUserNumber())
						.setOnBehalfOfUserNumber(employee.getUserNumber()))
				.setNote("test note");

		// this is the only way to add a resource note on accept-on-behalf
		WorkActionResponse response = workService.acceptWorkOnBehalf(acceptWorkOfferRequest);

		work = workService.findWork(work.getId());
		work.setRequirementSets(null);  // these steps are needed to avoid lazy load
		WorkResourcePagination pagination = new WorkResourcePagination(true);
		workService.findWorkResources(work.getId(), pagination);
		work.setWorkResources(new HashSet<>(pagination.getResults()));

		assertEquals(WorkActionResponseCodeType.SUCCESS, response.getResponseCode());
		assertTrue(work.isActive());
		ImmutableSet<WorkRequestInfo> includes = ImmutableSet.of(
				WorkRequestInfo.CONTEXT_INFO,
				WorkRequestInfo.RESOURCES_INFO,
				WorkRequestInfo.NOTES_INFO
		);

		WorkResponse activeResourceResponse = workResponseBuilder.buildWorkResponse(work, activeResource, includes);
		assertTrue(isEmpty(activeResourceResponse.getWork().getNotes()));

		WorkResponse otherResourceResponse = workResponseBuilder.buildWorkResponse(work, otherResource, includes);
		assertTrue(isEmpty(otherResourceResponse.getWork().getNotes()));

		WorkResponse employeeResponse = workResponseBuilder.buildWorkResponse(work, employee, includes);
		assertTrue(isNotEmpty(employeeResponse.getWork().getNotes()));
	}

	@Test
	public void buildChangeLog_whenWorkIsAssigned_dontShowNegotiationExpirysFromOtherWorkers() throws Exception {
		Calendar now = Calendar.getInstance();
		Calendar newScheduleFrom = Calendar.getInstance();
		newScheduleFrom.add(Calendar.DAY_OF_YEAR, 1);

		WorkNegotiationDTO dto = new WorkNegotiationDTO();
		dto.setPriceNegotiation(true);
		dto.setPricingStrategyId(FLAT_PRICING_STRATEGY);
		dto.setFlatPrice(500.00);
		dto.setScheduleNegotiation(true);
		dto.setIsScheduleRange(false);
		dto.setScheduleFromString((DateUtilities.getISO8601(newScheduleFrom)));
		dto.setExpiresOn(DateUtilities.getISO8601(now));
		dto.setNote("Need more!");

		authenticationService.setCurrentUser(otherResource);
		WorkNegotiationResponse negotiationResponse = workNegotiationService.createNegotiation(work.getId(), dto);
		assertNotNull(negotiationResponse);
		assertTrue(negotiationResponse.isSuccessful());

		AcceptWorkResponse workResponse = workService.acceptWork(activeResource.getId(), work.getId());
		assertTrue(workResponse.isSuccessful());
		assertEquals(activeResource, workResponse.getActiveResource().getUser());

		/**
		 * Ensure the negotiation expiry log has been written to the DB
		 * Run the code synchronously that the consumers run asynchronously, so we don't have to wait.
		 * See UserNotificationServiceImpl::onWorkNegotiationRequested
		 */
		WorkNegotiation negotiation = (WorkNegotiation) workNegotiationService.findById(negotiationResponse.getWorkNegotiationId());
		eventRouter.onEvent(
			eventFactory.buildWorkNegotiationExpiredScheduledEvent(
				negotiation, negotiation.getExpiresOn()
			)
		);

		List<WorkChangeLog> changeLogs = workChangeLogService.findAllChangeLogsByWorkId(work.getId(), new WorkChangeLogPagination(true)).getResults();
		Matcher negotiationExpiredLog = hasProperty("type", is(WorkChangeLog.WORK_NEGOTIATION_EXPIRED));
		assertThat(changeLogs, hasItem(negotiationExpiredLog));

		WorkResponse employeeResponse = workResponseBuilder.buildWorkDetailResponse(work.getId(), employee.getId());
		negotiationExpiredLog = hasProperty("type", is(LogEntryType.WORK_NEGOTIATION_EXPIRED));
		assertThat(employeeResponse.getWork().getChangelog(), not(hasItem(negotiationExpiredLog)));
	}

}
