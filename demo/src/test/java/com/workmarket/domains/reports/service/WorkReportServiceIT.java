package com.workmarket.domains.reports.service;

import com.google.common.collect.Lists;
import com.workmarket.data.report.work.WorkReportPagination;
import com.workmarket.data.report.work.WorkReportRow;
import com.workmarket.domains.model.User;
import com.workmarket.domains.model.WorkStatusType;
import com.workmarket.domains.work.model.Work;
import com.workmarket.domains.work.service.WorkService;
import com.workmarket.service.business.BaseServiceIT;
import com.workmarket.service.business.dto.CompleteWorkDTO;
import com.workmarket.test.IntegrationTest;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.math.BigDecimal;

import static org.junit.Assert.assertEquals;

@RunWith(SpringJUnit4ClassRunner.class)
@Category(IntegrationTest.class)
public class WorkReportServiceIT extends BaseServiceIT {

	@Autowired private WorkService workService;
	@Autowired private WorkReportService workReportService;

	@Test
	public void findAllWorkByWorkNumber_compareWorkReportPriceAgainstWorkPrice() throws Exception {
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

		WorkReportPagination pagination = new WorkReportPagination();
		pagination.setReturnAllRows();
		pagination = workReportService.findAllWorkByWorkNumber(
			employee.getCompany().getId(), employee.getId(), Lists.newArrayList(work.getWorkNumber()), pagination
		);

		work = workService.findWork(work.getId());

		WorkReportRow workReport = pagination.getResults().get(0);
		assertEquals(WorkStatusType.COMPLETE, work.getWorkStatusType().getCode());
		assertEquals(WorkStatusType.COMPLETE, workReport.getStatus());
		assertEquals(workReport.getPrice(), pagination.getPriceTotal(), .001);
		assertEquals(
			WorkReportRow.calculateSpendLimitWithFee(
				work.getPricingStrategy().getFullPricingStrategy().getFlatPrice(),
				new BigDecimal(10)
			),
			workReport.getPrice(),
			.001
		);
	}
}
