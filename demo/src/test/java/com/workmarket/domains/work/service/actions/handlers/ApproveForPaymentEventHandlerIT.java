package com.workmarket.domains.work.service.actions.handlers;

import com.google.common.collect.Lists;
import com.workmarket.domains.model.User;
import com.workmarket.domains.model.WorkStatusType;
import com.workmarket.domains.work.model.Work;
import com.workmarket.service.business.BaseServiceIT;
import com.workmarket.service.business.dto.CompleteWorkDTO;
import com.workmarket.domains.work.service.actions.ApproveForPaymentWorkEvent;
import com.workmarket.test.IntegrationTest;
import com.workmarket.web.helpers.AjaxResponseBuilder;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;

import static org.junit.Assert.assertEquals;

@RunWith(SpringJUnit4ClassRunner.class)
@Category(IntegrationTest.class)
public class ApproveForPaymentEventHandlerIT extends BaseServiceIT {

	@Autowired ApproveForPaymentEventHandler approveForPaymentEventHandler;

	Work work1;
	Work work2;
	List<Work> works;
	User user;
	User contractor;
	ApproveForPaymentWorkEvent event;
	AjaxResponseBuilder response = new AjaxResponseBuilder().setSuccessful(true);
	List<String> workNumbers = Lists.newArrayList();

	@Before
	public void setup() throws Exception{
		user = newEmployeeWithCashBalance();
		contractor = newContractorIndependentlane4Ready();

		work1 = newWork(user.getId());
		work2 = newWork(user.getId());
		works = Lists.newArrayList();
		works.add(work1);

		workRoutingService.addToWorkResources(work1.getId(), contractor.getId());
		workService.acceptWork(contractor.getId(), work1.getId());
		CompleteWorkDTO dto = new CompleteWorkDTO();
		dto.setResolution("Resolved");
		workService.completeWork(work1.getId(),dto);

		event = (ApproveForPaymentWorkEvent) new ApproveForPaymentWorkEvent.Builder(workNumbers,user,"test","bulk_approve")
				.work(works)
				.workEventHandler(approveForPaymentEventHandler)
				.response(response)
				.build();
		authenticationService.setCurrentUser(user);
	}

	@Test
	public void handleEvent_validWork_approvedPayment(){
		approveForPaymentEventHandler.handleEvent(event);
		Work testWork = workService.findWork(work1.getId());
		assertEquals(WorkStatusType.PAID, testWork.getWorkStatusType().getCode());

	}

	@Test
	public void handleEvent_oneWorkNotCompleted_oneApprovedPayment() throws Exception{

		workRoutingService.addToWorkResources(work2.getId(), contractor.getId());
		workService.acceptWork(contractor.getId(), work2.getId());
		works.add(work2);
		event = (ApproveForPaymentWorkEvent) new ApproveForPaymentWorkEvent.Builder(workNumbers,user,"test","bulk_approve")
				.work(works)
				.workEventHandler(approveForPaymentEventHandler)
				.response(response)
				.build();

		approveForPaymentEventHandler.handleEvent(event);
		Work testWork = workService.findWork(work1.getId());
		assertEquals(WorkStatusType.PAID, testWork.getWorkStatusType().getCode());
		testWork = workService.findWork(work2.getId());
		assertEquals(WorkStatusType.ACTIVE, testWork.getWorkStatusType().getCode());
	}

	@Test
	public void handleEvent_twoValidWork_bothApprovedPayment() throws Exception{

		workRoutingService.addToWorkResources(work2.getId(), contractor.getId());
		workService.acceptWork(contractor.getId(), work2.getId());
		CompleteWorkDTO dto = new CompleteWorkDTO();
		dto.setResolution("Resolved");
		workService.completeWork(work2.getId(),dto);
		works.add(work2);

		event = (ApproveForPaymentWorkEvent) new ApproveForPaymentWorkEvent.Builder(workNumbers,user,"test","bulk_approve")
				.work(works)
				.workEventHandler(approveForPaymentEventHandler)
				.response(response)
				.build();

		approveForPaymentEventHandler.handleEvent(event);
		Work testWork = workService.findWork(work1.getId());
		assertEquals(WorkStatusType.PAID, testWork.getWorkStatusType().getCode());
		testWork = workService.findWork(work2.getId());
		assertEquals(WorkStatusType.PAID, testWork.getWorkStatusType().getCode());
	}

}
