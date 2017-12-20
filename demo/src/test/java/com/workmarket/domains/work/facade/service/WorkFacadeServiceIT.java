package com.workmarket.domains.work.facade.service;

import com.workmarket.domains.model.User;
import com.workmarket.domains.work.model.CancellationReasonType;
import com.workmarket.domains.work.model.Work;
import com.workmarket.service.business.BaseServiceIT;
import com.workmarket.service.business.dto.CancelWorkDTO;
import com.workmarket.test.IntegrationTest;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.junit.Assert.assertFalse;

@RunWith(SpringJUnit4ClassRunner.class)
@Category(IntegrationTest.class)
public class WorkFacadeServiceIT extends BaseServiceIT {

	@Autowired WorkFacadeService workFacadeService;

	@Test
	public void cancelWork_returnNoErrors() throws Exception {
		User employee = newEmployeeWithCashBalance();
		Work work = newWork(employee.getId());
		User worker = newContractorIndependentlane4Ready();

		laneService.addUserToCompanyLane2(worker.getId(), work.getBuyer().getCompany().getId());
		workRoutingService.addToWorkResources(work.getId(), worker.getId());
		authenticationService.setCurrentUser(work.getBuyer().getId());
		CancelWorkDTO cancelWorkDTO = new CancelWorkDTO();
		cancelWorkDTO.setWorkId(work.getId());
		cancelWorkDTO.setPrice(35.00);
		cancelWorkDTO.setCancellationReasonTypeCode(CancellationReasonType.OTHER);
		cancelWorkDTO.setNote("This is a cancellation note");

		assertFalse(workService.cancelWork(cancelWorkDTO).isEmpty());
	}
}