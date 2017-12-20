package com.workmarket.domains.work.service;

import com.workmarket.domains.model.User;
import com.workmarket.domains.model.summary.work.WorkMilestones;
import com.workmarket.domains.work.model.Work;
import com.workmarket.service.business.BaseServiceIT;
import com.workmarket.service.business.LaneService;
import com.workmarket.test.BrokenTest;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;


/**
 * Copyright (c) 2011 WorkMarket, Inc., All rights reserved.
 *
 * @author Chris Benskey
 */
@RunWith(SpringJUnit4ClassRunner.class)
@Category(BrokenTest.class)
@Ignore
public class WorkMilestoneServiceIT extends BaseServiceIT {

	@Autowired private LaneService laneService;
	@Autowired
	WorkService workService;
	@Autowired
	WorkMilestonesService workMilestonesService;

	@Test
	@SuppressWarnings("unchecked")
	public void testMilestoneGeneration() throws Exception {
		User employee = newFirstEmployeeWithCashBalance();

		User contractor = newContractorIndependentlane4Ready();

		laneService.addUserToCompanyLane3(contractor.getId(), employee.getCompany().getId());

		Work work = newWork(employee.getId());

		workRoutingService.addToWorkResources(work.getId(), contractor.getId());
		workService.acceptWork(contractor.getId(), work.getId());
		workService.closeWork(work.getId());

		WorkMilestones milestones = workMilestonesService.findWorkMilestonesByWorkId(work.getId());

		Assert.assertNotNull(milestones);
		Assert.assertNotNull(milestones.getCreatedOn());
		Assert.assertNotNull(milestones.getDraftOn());
		Assert.assertNotNull(milestones.getSentOn());
		Assert.assertNotNull(milestones.getAcceptedOn());
	}

}
