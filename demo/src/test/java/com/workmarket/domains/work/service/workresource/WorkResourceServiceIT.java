package com.workmarket.domains.work.service.workresource;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.workmarket.domains.model.User;
import com.workmarket.domains.model.WorkResource;
import com.workmarket.domains.model.WorkStatusType;
import com.workmarket.domains.model.acl.AclRole;
import com.workmarket.domains.velvetrope.service.AdmissionService;
import com.workmarket.domains.work.model.Work;
import com.workmarket.domains.work.model.WorkResourceAggregateFilter;
import com.workmarket.domains.work.service.WorkNegotiationService;
import com.workmarket.service.business.BaseServiceIT;
import com.workmarket.service.business.dto.CompleteWorkDTO;
import com.workmarket.service.business.dto.WorkDTO;
import com.workmarket.service.business.dto.WorkNegotiationDTO;
import com.workmarket.service.business.dto.WorkResourceDetailPagination;
import com.workmarket.test.IntegrationTest;
import com.workmarket.utility.CollectionUtilities;
import com.workmarket.utility.DateUtilities;
import com.workmarket.velvetrope.Venue;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Calendar;
import java.util.List;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

@SuppressWarnings("unchecked")
@RunWith(SpringJUnit4ClassRunner.class)
@Category(IntegrationTest.class)
public class WorkResourceServiceIT extends BaseServiceIT {

	@Autowired private WorkResourceService workResourceService;
	@Autowired private WorkNegotiationService workNegotiationService;
	@Autowired AdmissionService admissionService;

	private final static double MAXIMUM_HOURS = 3;
	private final static double HOURLY_RATE = 10;
	@Test
	public void countAssignmentsByResourceUserIdAndStatus() throws Exception {

		User employee = newFirstEmployeeWithCashBalanceAndPaymentTermsEnabled();
		authenticationService.setCurrentUser(employee);

		User contractor = newContractor();
		laneService.addUserToCompanyLane2(contractor.getId(), employee.getCompany().getId());

		Work work = newWorkWithPaymentTerms(employee.getId(), 30);

		workRoutingService.addToWorkResources(work.getId(), contractor.getId());
		workService.acceptWork(contractor.getId(), work.getId());
		workService.updateWorkProperties(work.getId(),
				CollectionUtilities.newStringMap("resolution", "Complete the assignment"));

		workService.completeWork(work.getId(), new CompleteWorkDTO());
		workService.closeWork(work.getId());

		billingService.payAssignment(work.getId());

		work = workService.findWork(work.getId());

		WorkResourceAggregateFilter filter = new WorkResourceAggregateFilter()
				.setFromDate(DateUtilities.getMidnight6MonthsAgo())
				.setWorkStatusTypeCode(WorkStatusType.PAID);

		assertTrue(work.isPaid());
		assertEquals(workResourceService.countAssignmentsByResourceUserIdAndStatus(Lists.newArrayList(contractor.getId()), filter), Integer.valueOf(1));
	}

	@Test
	public void findResourcesInFromWorkNotInToWork() throws Exception {
		User employee = newFirstEmployeeWithCashBalanceAndPaymentTermsEnabled();
		authenticationService.setCurrentUser(employee);

		User contractor = newContractor();
		laneService.addUserToCompanyLane2(contractor.getId(), employee.getCompany().getId());
		User contractor2 = newContractor();
		laneService.addUserToCompanyLane2(contractor2.getId(), employee.getCompany().getId());

		Work work = newWorkWithPaymentTerms(employee.getId(), 30);
		Work work2 = newWorkWithPaymentTerms(employee.getId(), 30);

		workRoutingService.addToWorkResources(work.getId(), contractor.getId());
		workRoutingService.addToWorkResources(work.getId(), contractor2.getId());

		workRoutingService.addToWorkResources(work2.getId(), contractor.getId());

		List<WorkResource> resourceList = workResourceService.findResourcesInFromWorkNotInToWork(work.getId(), work2.getId());
		assertTrue(resourceList.size() == 1);
		for (WorkResource workResource : resourceList) {
			assertEquals(workResource.getUser().getId(), contractor2.getId());
		}
	}

	@Test
	public void findAllResourcesForWork_filterByResourceCompanyId() throws Exception {
		User me = newFirstEmployee();
		User myWorker = newCompanyEmployeeSharedWorkerConfirmed(me.getCompany().getId());
		User otherWorker = newContractorIndependentlane4Ready();
		User buyer = newFirstEmployeeWithCashBalanceAndPaymentTermsEnabled();
		authenticationService.setCurrentUser(buyer);
		Work work = newWork(buyer.getId());

		laneService.addUserToCompanyLane2(myWorker.getId(), buyer.getCompany().getId());
		laneService.addUserToCompanyLane2(otherWorker.getId(), buyer.getCompany().getId());
		workRoutingService.addToWorkResources(
			work.getWorkNumber(), Sets.newHashSet(
				myWorker.getUserNumber(), otherWorker.getUserNumber()
			)
		);

		WorkResourceDetailPagination pagination = new WorkResourceDetailPagination();
		pagination.addFilter(
			WorkResourceDetailPagination.FILTER_KEYS.WORK_RESOURCE_COMPANY_ID, me.getCompany().getId()
		);
		pagination = workResourceService.findAllResourcesForWork(work.getId(), pagination);
		assertEquals(1, pagination.getResults().size());
		assertEquals(myWorker.getId(), pagination.getResults().get(0).getUserId());
		assertEquals(me.getCompany().getId(), pagination.getResults().get(0).getCompanyId());
	}

	@Test
	public void isAtLeastOneWorkerFromCompanyInvitedToWork_noWorkersInvited_returnFalse() throws Exception {
		User worker = newContractor();
		User buyer = newFirstEmployeeWithCashBalanceAndPaymentTermsEnabled();
		Work work = newWork(buyer.getId());

		assertFalse(workResourceService.isAtLeastOneWorkerFromCompanyInvitedToWork(worker.getCompany().getId(), work.getId()));
	}

	@Test
	public void isAtLeastOneWorkerFromCompanyInvitedToWork_oneWorkerInvited_returnTrue() throws Exception {
		User worker = newContractor();
		User buyer = newFirstEmployeeWithCashBalanceAndPaymentTermsEnabled();
		authenticationService.setCurrentUser(buyer);
		Work work = newWork(buyer.getId());

		laneService.addUserToCompanyLane2(worker.getId(), buyer.getCompany().getId());
		workRoutingService.addToWorkResources(work.getId(), worker.getId());

		assertTrue(workResourceService.isAtLeastOneWorkerFromCompanyInvitedToWork(worker.getCompany().getId(), work.getId()));
	}

	@Test
	public void getAllDispatcherIdsForWorker_noDispatchers_returnEmptyList() throws Exception {
		User worker = newContractor();
		assertEquals(0, workResourceService.getAllDispatcherIdsForWorker(worker.getId()).size());
	}

	@Test
	public void getAllDispatcherIdsForWorker_oneDispatcher_returnOneDispatcher() throws Exception {
		User me = newFirstEmployee();
		authenticationService.updateUserAclRoles(me.getId(), Lists.newArrayList(AclRole.ACL_ADMIN, AclRole.ACL_DISPATCHER));
		User myWorker = newCompanyEmployeeSharedWorkerConfirmed(me.getCompany().getId());

		assertEquals(1, workResourceService.getAllDispatcherIdsForWorker(myWorker.getId()).size());
	}

	@Test
	public void getAllDispatcherIdsInCompany_noDispatchers_returnEmptyList() throws Exception {
		User worker = newContractor();
		assertEquals(0, workResourceService.getAllDispatcherIdsInCompany(worker.getCompany().getId()).size());
	}

	@Test
	public void getAllDispatcherIdsInCompany_oneDispatcher_returnOneDispatcher() throws Exception {
		User me = newFirstEmployee();
		authenticationService.updateUserAclRoles(me.getId(), Lists.newArrayList(AclRole.ACL_ADMIN, AclRole.ACL_DISPATCHER));

		assertEquals(1, workResourceService.getAllDispatcherIdsInCompany(me.getCompany().getId()).size());
	}

	@Test
	public void getAllDispatcherIdsForWorker_inputDispatcherId_returnEmptyList() throws Exception {
		User me = newFirstEmployee();
		authenticationService.updateUserAclRoles(me.getId(), Lists.newArrayList(AclRole.ACL_ADMIN, AclRole.ACL_DISPATCHER));

		assertEquals(0, workResourceService.getAllDispatcherIdsForWorker(me.getId()).size());
	}

	@Test
	public void getDispatcherIdForWorkAndWorker_dispatcherApplies_returnDispatcherId() throws Exception {

		User dispatcher = createDispatcher();
		User worker = newCompanyEmployeeSharedWorkerConfirmed(dispatcher.getCompany().getId());

		User buyer = newFirstEmployeeWithCashBalanceAndPaymentTermsEnabled();
		authenticationService.setCurrentUser(buyer);

		WorkDTO dto = new WorkDTO();
		dto.setAssignToFirstResource(false);

		Work work = newWork(buyer.getId(), dto);

		// You can only apply to a sent assignment
		laneService.addUserToCompanyLane3(worker.getId(), buyer.getCompany().getId());
		workRoutingService.addToWorkResources(work.getId(), worker.getId());

		authenticationService.setCurrentUser(dispatcher);
		workNegotiationService.createApplyNegotiation(work.getId(), worker.getId(), new WorkNegotiationDTO());

		assertEquals(dispatcher.getId(), workResourceService.getDispatcherIdForWorkAndWorker(work.getId(), worker.getId()));
	}

	@Test
	public void getDispatcherIdForWorkAndWorker_dispatcherAccepts_returnDispatcherId() throws Exception {
		User dispatcher = createDispatcher();
		User worker = newCompanyEmployeeSharedWorkerConfirmed(dispatcher.getCompany().getId());

		User buyer = newFirstEmployeeWithCashBalanceAndPaymentTermsEnabled();
		authenticationService.setCurrentUser(buyer);
		WorkDTO dto = new WorkDTO();
		dto.setAssignToFirstResource(false);
		Work work = newWork(buyer.getId(), dto);
		laneService.addUserToCompanyLane2(worker.getId(), buyer.getCompany().getId());
		workRoutingService.addToWorkResources(work.getId(), worker.getId());

		authenticationService.setCurrentUser(dispatcher);
		workService.acceptWorkOnBehalf(
			createAcceptWorkOfferRequest(work, worker, dispatcher.getUserNumber())
		);

		assertEquals(dispatcher.getId(), workResourceService.getDispatcherIdForWorkAndWorker(work.getId(), worker.getId()));
	}

	@Test
	public void getDispatcherIdForWorkAndWorker_noDispatcherSet_returnNull() throws Exception {
		User buyer = newFirstEmployeeWithCashBalanceAndPaymentTermsEnabled();
		authenticationService.setCurrentUser(buyer);
		Work work = newWork(buyer.getId());

		assertNull(workResourceService.getDispatcherIdForWorkAndWorker(work.getId(), buyer.getId()));
	}

	@Test
	public void getDispatcherIdsForWorkAndWorkers_dispatchersApply_returnDispatcherIds() throws Exception {
		User dispatcher1 = createDispatcher();
		User worker1 = newCompanyEmployeeSharedWorkerConfirmed(dispatcher1.getCompany().getId());

		User dispatcher2 = createDispatcher();
		User worker2 = newCompanyEmployeeSharedWorkerConfirmed(dispatcher2.getCompany().getId());

		User buyer = newFirstEmployeeWithCashBalanceAndPaymentTermsEnabled();
		authenticationService.setCurrentUser(buyer);

		WorkDTO dto = new WorkDTO();

		dto.setAssignToFirstResource(false);

		Work work = newWork(buyer.getId(), dto);

		// You can only apply to a sent assignment
		laneService.addUserToCompanyLane3(worker1.getId(), buyer.getCompany().getId());
		laneService.addUserToCompanyLane3(worker2.getId(), buyer.getCompany().getId());
		workRoutingService.addToWorkResources(work.getId(), worker1.getId());
		workRoutingService.addToWorkResources(work.getId(), worker2.getId());

		authenticationService.setCurrentUser(dispatcher1);
		workNegotiationService.createApplyNegotiation(work.getId(), worker1.getId(), new WorkNegotiationDTO());

		authenticationService.setCurrentUser(dispatcher2);
		workNegotiationService.createApplyNegotiation(work.getId(), worker2.getId(), new WorkNegotiationDTO());

		assertEquals(2,
			workResourceService.getDispatcherIdsForWorkAndWorkers(
				work.getId(), Lists.newArrayList(worker1.getId(), worker2.getId())
			).size()
		);
	}

	@Test (expected = IllegalArgumentException.class)
	public void test_no_additional_application_after_work_assigned() throws Exception {
		User dispatcher1 = createDispatcher();
		User worker1 = newCompanyEmployeeSharedWorkerConfirmed(dispatcher1.getCompany().getId());

		User dispatcher2 = createDispatcher();
		User worker2 = newCompanyEmployeeSharedWorkerConfirmed(dispatcher2.getCompany().getId());

		User buyer = newFirstEmployeeWithCashBalanceAndPaymentTermsEnabled();
		authenticationService.setCurrentUser(buyer);

		WorkDTO dto = new WorkDTO();

		dto.setAssignToFirstResource(false);

		Work work = newWork(buyer.getId(), dto);

		// You can only apply to a sent assignment
		laneService.addUserToCompanyLane3(worker1.getId(), buyer.getCompany().getId());
		laneService.addUserToCompanyLane3(worker2.getId(), buyer.getCompany().getId());
		workRoutingService.addToWorkResources(work.getId(), worker1.getId());

		authenticationService.setCurrentUser(dispatcher1);
		workNegotiationService.createApplyNegotiation(work.getId(), worker1.getId(), new WorkNegotiationDTO());
		workService.acceptWork(worker1.getId(), work.getId());

		authenticationService.setCurrentUser(dispatcher2);
		workNegotiationService.createApplyNegotiation(work.getId(), worker2.getId(), new WorkNegotiationDTO());
	}

	@Test (expected = IllegalStateException.class)
	public void test_ApplyNotAllowed_with_DateRangeScheduleConflict() throws Exception {
		User worker = newContractorIndependentlane4Ready();
		User buyer = newFirstEmployeeWithCashBalanceAndPaymentTermsEnabled();
		admissionService.saveAdmissionForCompanyIdAndVenue(buyer.getCompany().getId(), Venue.AVOID_SCHED_CONFLICT);

		authenticationService.setCurrentUser(buyer);
		WorkDTO dto = new WorkDTO();
		dto.setAssignToFirstResource(false);

		Calendar now = Calendar.getInstance();
		Calendar fourHoursFromNow = (Calendar)now.clone();
		fourHoursFromNow.add(Calendar.HOUR_OF_DAY, 4);

		Work work1 = newWorkWithDateRange(buyer.getId(), dto, now, fourHoursFromNow);
		Work work2 = newWorkWithDateRange(buyer.getId(), dto, now, fourHoursFromNow);

		laneService.addUserToCompanyLane3(worker.getId(), buyer.getCompany().getId());
		workRoutingService.addToWorkResources(work1.getId(), worker.getId());
		workRoutingService.addToWorkResources(work2.getId(), worker.getId());

		authenticationService.setCurrentUser(worker);
		workNegotiationService.createApplyNegotiation(work1.getId(), worker.getId(), new WorkNegotiationDTO());
		workService.acceptWork(worker.getId(), work1.getId());

		// can't apply due to scheduling conflict
		workNegotiationService.createApplyNegotiation(work2.getId(), worker.getId(), new WorkNegotiationDTO());
	}

	@Test (expected = IllegalStateException.class)
	public void test_ApplyNotAllowed_with_HourlyScheduleConflict() throws Exception {
		User worker = newContractorIndependentlane4Ready();
		User buyer = newFirstEmployeeWithCashBalanceAndPaymentTermsEnabled();
		admissionService.saveAdmissionForCompanyIdAndVenue(buyer.getCompany().getId(), Venue.AVOID_SCHED_CONFLICT);

		authenticationService.setCurrentUser(buyer);
		WorkDTO dto = new WorkDTO();
		dto.setAssignToFirstResource(false);

		Calendar now = Calendar.getInstance();
		Calendar twoHoursFromNow = (Calendar)now.clone();
		twoHoursFromNow.add(Calendar.HOUR_OF_DAY, 2);

		Work work1 = newHourlyWork(buyer.getId(), dto, now, HOURLY_RATE, MAXIMUM_HOURS);
		Work work2 = newHourlyWork(buyer.getId(), dto, twoHoursFromNow, HOURLY_RATE, MAXIMUM_HOURS);

		laneService.addUserToCompanyLane3(worker.getId(), buyer.getCompany().getId());
		workRoutingService.addToWorkResources(work1.getId(), worker.getId());
		workRoutingService.addToWorkResources(work2.getId(), worker.getId());

		authenticationService.setCurrentUser(worker);
		workNegotiationService.createApplyNegotiation(work1.getId(), worker.getId(), new WorkNegotiationDTO());
		workService.acceptWork(worker.getId(), work1.getId());

		// can't apply due to scheduling conflict
		workNegotiationService.createApplyNegotiation(work2.getId(), worker.getId(), new WorkNegotiationDTO());
	}

	@Test
	public void getDispatcherIdsForWorkAndWorkers_noDispatcherSet_returnEmptyList() throws Exception {
		User buyer = newFirstEmployeeWithCashBalanceAndPaymentTermsEnabled();
		authenticationService.setCurrentUser(buyer);
		Work work = newWork(buyer.getId());

		assertEquals(0, workResourceService.getDispatcherIdsForWorkAndWorkers(work.getId(), Lists.newArrayList(buyer.getId())).size());
	}

	@Test
	public void findWorkResources_addTwoWorkers_getTwoWorkers() throws Exception {
		User me = newFirstEmployee();
		User myWorker = newCompanyEmployeeSharedWorkerConfirmed(me.getCompany().getId());
		User otherWorker = newContractorIndependentlane4Ready();
		User buyer = newFirstEmployeeWithCashBalanceAndPaymentTermsEnabled();
		authenticationService.setCurrentUser(buyer);
		Work work = newWork(buyer.getId());

		laneService.addUserToCompanyLane2(myWorker.getId(), buyer.getCompany().getId());
		laneService.addUserToCompanyLane2(otherWorker.getId(), buyer.getCompany().getId());
		workRoutingService.addToWorkResources(
			work.getWorkNumber(), Sets.newHashSet(
				myWorker.getUserNumber(), otherWorker.getUserNumber()
			)
		);

		List<WorkResource> workResources = workResourceService.findWorkResources(Sets.newHashSet(myWorker.getId(), otherWorker.getId()), work.getId());
		assertEquals(2, workResources.size());
		for (WorkResource workResource : workResources) {
			Long workerId = workResource.getUser().getId();
			Long workId = workResource.getWork().getId();
			assertTrue(myWorker.getId().equals(workerId) || otherWorker.getId().equals(workerId));
			assertEquals(work.getId(), workId);
		}
	}

	@Test
	public void findWorkResources_noWorkers_getEmptyList() throws Exception {
		User buyer = newFirstEmployeeWithCashBalanceAndPaymentTermsEnabled();
		Work work = newWork(buyer.getId());
		assertEquals(0, workResourceService.findWorkResources(Sets.<Long>newHashSet(), work.getId()).size());
	}

	@Test
	public void isUserResourceForWork_noRecord_returnFalse() {
		assertFalse(workResourceService.isUserResourceForWork(0L, 0L));
	}

	@Test
	public void isUserResourceForWork_withRecord_returnTrue() throws Exception {
		User worker = newContractor();
		User buyer = newFirstEmployeeWithCashBalanceAndPaymentTermsEnabled();
		authenticationService.setCurrentUser(buyer);
		Work work = newWork(buyer.getId());

		laneService.addUserToCompanyLane2(worker.getId(), buyer.getCompany().getId());
		workRoutingService.addToWorkResources(work.getId(), worker.getId());

		assertTrue(workResourceService.isUserResourceForWork(work.getId(), worker.getId()));
	}

	@Test
	public void workResourceAcceptsWorkOnBehalfOf_withDateTimeSet_appointmentSet() throws Exception {
		User dispatcher = createDispatcher();
		User worker = newCompanyEmployeeSharedWorkerConfirmed(dispatcher.getCompany().getId());

		User buyer = newFirstEmployeeWithCashBalanceAndPaymentTermsEnabled();
		authenticationService.setCurrentUser(buyer);
		WorkDTO dto = new WorkDTO();
		dto.setAssignToFirstResource(false);
		Work work = newWork(buyer.getId(), dto);
		laneService.addUserToCompanyLane2(worker.getId(), buyer.getCompany().getId());
		workRoutingService.addToWorkResources(work.getId(), worker.getId());

		authenticationService.setCurrentUser(dispatcher);

		workService.acceptWork(worker.getId(), work.getId());

		workResourceService.setWorkResourceAppointmentFromWork(work.getId());

		work = workService.findWork(work.getId());

		WorkResource workResource = workService.findWorkResource(worker.getId(), work.getId());

		assertNotNull(workResource.getAppointment().getFrom());
		Assert.assertEquals(workResource.getAppointment().getFrom(), work.getScheduleFrom());
	}
}
