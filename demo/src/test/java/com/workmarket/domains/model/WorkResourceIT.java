package com.workmarket.domains.model;

import com.google.common.collect.Sets;
import com.workmarket.domains.work.model.Work;
import com.workmarket.service.business.BaseServiceIT;
import com.workmarket.test.IntegrationTest;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Set;

import static org.junit.Assert.assertTrue;

@RunWith(SpringJUnit4ClassRunner.class)
@Category(IntegrationTest.class)
public class WorkResourceIT extends BaseServiceIT {

	@Test
	public void equals() throws Exception {
		User employee = newFirstEmployeeWithCashBalanceAndPaymentTermsEnabled();
		authenticationService.setCurrentUser(employee);

		User contractor = newContractor();
		Work work = newWork(employee.getId());

		User contractor2 = newContractor();
		WorkResource w1 = new WorkResource(work, contractor).setScore(7);
		WorkResource w2 = new WorkResource(work, contractor2).setScore(7);

		Set<WorkResource> resourceSet = Sets.newHashSetWithExpectedSize(2);
		resourceSet.add(w1);
		resourceSet.add(w2);

		assertTrue(resourceSet.size() == 2);
	}

	@Test
	public void equals_withSameResource() throws Exception {
		User employee = newFirstEmployeeWithCashBalanceAndPaymentTermsEnabled();
		authenticationService.setCurrentUser(employee);

		User contractor = newContractor();
		Work work = newWork(employee.getId());
		WorkResource w1 = new WorkResource(work, contractor).setScore(7);

		Set<WorkResource> resourceSet = Sets.newHashSetWithExpectedSize(2);
		resourceSet.add(w1);
		resourceSet.add(w1);

		assertTrue(resourceSet.size() == 1);
	}
}
