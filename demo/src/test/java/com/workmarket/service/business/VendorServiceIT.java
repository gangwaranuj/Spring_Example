package com.workmarket.service.business;

import com.google.common.collect.Sets;
import com.workmarket.domains.model.User;
import com.workmarket.domains.work.dao.WorkVendorInvitationDAO;
import com.workmarket.domains.work.model.Work;
import com.workmarket.test.IntegrationTest;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Collections;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@RunWith(SpringJUnit4ClassRunner.class)
@Category(IntegrationTest.class)
public class VendorServiceIT extends BaseServiceIT {

	@Autowired VendorService vendorService;
	@Autowired WorkVendorInvitationDAO workVendorInvitationDAO;

	@Test
	public void getVendorIdsByWork_noVendorsInvited_returnEmptyList() throws Exception {
		User user = newEmployeeWithCashBalance();
		Work work = newWork(user.getId());

		assertTrue(vendorService.getNotDeclinedVendorIdsByWork(work.getId()).isEmpty());
	}

	@Test
	public void getNotDeclinedVendorNumbersByWork_noVendorsInvited_returnEmptyList() throws Exception {
		User user = newEmployeeWithCashBalance();
		Work work = newWork(user.getId());

		assertTrue(vendorService.getNotDeclinedVendorNumbersByWork(work.getId()).isEmpty());
	}

	@Test
	public void getVendorIdsByWork_withInvitedVendor_returnVendorId() throws Exception {
		User user = newEmployeeWithCashBalance();
		Work work = newWork(user.getId());
		User worker = newContractor();

		vendorService.inviteVendorsToWork(Sets.newHashSet(worker.getCompany().getCompanyNumber()), work.getId(), false, Collections.<Long>emptySet());

		assertEquals(worker.getCompany().getId(), vendorService.getNotDeclinedVendorIdsByWork(work.getId()).get(0));
	}

	@Test
	public void getNotDeclinedVendorNumbersByWork_withInvitedVendor_returnVendorNumber() throws Exception {
		User user = newEmployeeWithCashBalance();
		Work work = newWork(user.getId());
		User worker = newContractor();

		vendorService.inviteVendorsToWork(Sets.newHashSet(worker.getCompany().getCompanyNumber()), work.getId(), false, Collections.<Long>emptySet());

		assertEquals(worker.getCompany().getCompanyNumber(), vendorService.getNotDeclinedVendorNumbersByWork(work.getId()).get(0));
	}

	@Test
	public void getVendorNumbersByWork_noVendorsInvited_returnEmptyList() throws Exception {
		User user = newEmployeeWithCashBalance();
		Work work = newWork(user.getId());

		assertTrue(vendorService.getVendorNumbersByWork(work.getId()).isEmpty());
	}

	@Test
	public void getVendorNumbersByWork_withInvitedVendor_returnVendorNumber() throws Exception {
		User user = newEmployeeWithCashBalance();
		Work work = newWork(user.getId());
		User worker = newContractor();

		vendorService.inviteVendorsToWork(Sets.newHashSet(worker.getCompany().getCompanyNumber()), work.getId(), false, Collections.<Long>emptySet());

		assertEquals(worker.getCompany().getCompanyNumber(), vendorService.getVendorNumbersByWork(work.getId()).get(0));
	}

	@Test
	public void getVendorIdsByWork_withDeclinedVendor_returnEmptyList() throws Exception {
		User user = newEmployeeWithCashBalance();
		Work work = newWork(user.getId());
		User worker = newContractor();

		vendorService.inviteVendorsToWork(Sets.newHashSet(worker.getCompany().getCompanyNumber()), work.getId(), false, Collections.<Long>emptySet());
		vendorService.declineWork(work.getId(), worker.getCompany().getId(), worker.getId());

		assertTrue(vendorService.getNotDeclinedVendorIdsByWork(work.getId()).isEmpty());
		assertEquals(worker.getCompany().getId(), vendorService.getDeclinedVendorIdsByWork(work.getId()).get(0));
	}

	@Test
	public void getDeclinedVendorNumbersByWork_withDeclinedVendor_returnDeclinedVendorNumber() throws Exception {
		User user = newEmployeeWithCashBalance();
		Work work = newWork(user.getId());
		User worker = newContractor();

		vendorService.inviteVendorsToWork(Sets.newHashSet(worker.getCompany().getCompanyNumber()), work.getId(), false, Collections.<Long>emptySet());
		vendorService.declineWork(work.getId(), worker.getCompany().getId(), worker.getId());

		assertTrue(vendorService.getNotDeclinedVendorNumbersByWork(work.getId()).isEmpty());
		assertEquals(worker.getCompany().getCompanyNumber(), vendorService.getDeclinedVendorNumbersByWork(work.getId()).get(0));
	}
}
