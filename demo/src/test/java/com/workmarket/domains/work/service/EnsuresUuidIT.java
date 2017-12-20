package com.workmarket.domains.work.service;

import com.workmarket.domains.model.User;
import com.workmarket.domains.work.model.Work;
import com.workmarket.service.business.BaseServiceIT;
import com.workmarket.test.IntegrationTest;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.junit.Assert.assertNotNull;

/**
 * Test that EnsuresUuid works.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@Category(IntegrationTest.class)
public class EnsuresUuidIT extends BaseServiceIT {

	@Test public void createAndCheck() throws Exception {
		final User employee = newFirstEmployeeWithCashBalance();
		final Work work = newWork(employee.getId());
		assertNotNull(work.getUuid()); // after save, existing instance has it
		assertNotNull(workService.findWork(work.getId()).getUuid()); // yup, it's in the db too
	}
}
