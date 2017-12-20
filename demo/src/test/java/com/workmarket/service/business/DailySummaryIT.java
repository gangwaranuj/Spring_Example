package com.workmarket.service.business;

import com.workmarket.domains.model.reporting.DailySummary;
import com.workmarket.domains.model.reporting.DailySummaryPagination;
import com.workmarket.test.IntegrationTest;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.junit.Assert.assertNotNull;

@RunWith(SpringJUnit4ClassRunner.class)
@Category(IntegrationTest.class)
public class DailySummaryIT extends BaseServiceIT {

	@Autowired
	private DailySummaryService dailySummaryService;
		
	@Test
	public void findAllSummaries() throws Exception {
		DailySummaryPagination pagination = new DailySummaryPagination();
		pagination = dailySummaryService.findAllSummaries(pagination);
		assertNotNull(pagination);

		for (DailySummary summary : pagination.getResults()) {
			assertNotNull(summary.getRouted());
			assertNotNull(summary.getAssignments());
			assertNotNull(summary.getAssignmentsOnTerms());
			assertNotNull(summary.getBackgroundChecks());
			assertNotNull(summary.getDraftsCreated());
			assertNotNull(summary.getDrugTests());
			assertNotNull(summary.getPublicGroups());
			assertNotNull(summary.getInvitations());
			assertNotNull(summary.getAveragePriceCreatedAssignments());
			assertNotNull(summary.getPrivateGroups());
			assertNotNull(summary.getUniqueRouters());
			assertNotNull(summary.getUniqueBuyers());
		}
	}
}
