package com.workmarket.domains.work.dao;

import com.google.common.collect.Lists;
import com.workmarket.domains.groups.model.UserGroup;
import com.workmarket.domains.model.Company;
import com.workmarket.domains.model.User;
import com.workmarket.domains.model.WorkVendorInvitation;
import com.workmarket.domains.model.WorkVendorInvitationToGroupAssociation;
import com.workmarket.domains.work.model.Work;
import com.workmarket.service.business.BaseServiceIT;
import com.workmarket.test.IntegrationTest;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Integration tests for WorkVendorInvitationDAO.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@Category(IntegrationTest.class)
public class WorkVendorInvitationDAOIT extends BaseServiceIT {

	@Autowired WorkVendorInvitationDAO workVendorInvitationDAO;
	@Autowired WorkVendorInvitationToGroupAssociationDAO workVendorInvitationToGroupAssociationDAO;

	@Test
	@Transactional
	public void getVendorInvitationsByWork_success() throws Exception {
		User buyer = newFirstEmployee();
		Company vendor = companyService.createCompany("Test", false, "unknown");
		Work work = newWork(buyer.getId());

		WorkVendorInvitation workVendorInvitation = new WorkVendorInvitation(work.getId(), vendor.getId());
		workVendorInvitationDAO.saveAll(Lists.newArrayList(workVendorInvitation));

		List<WorkVendorInvitation> invitations = workVendorInvitationDAO.getVendorInvitationsByWork(work.getId());
		assertEquals(1, invitations.size());
		assertEquals(vendor.getId(), invitations.get(0).getCompanyId());
	}

	@Ignore
	@Test
	@Transactional
	public void getVendorGroupInvitationsByWork_success() throws Exception {
		User buyer = newFirstEmployee();
		UserGroup group = newPrivateUserGroup(buyer);
		Company vendor = companyService.createCompany("Test", false, "unknown");
		Work work = newWork(buyer.getId());

		WorkVendorInvitation workVendorInvitation = new WorkVendorInvitation(work.getId(), vendor.getId());
		workVendorInvitationDAO.saveAll(Lists.newArrayList(workVendorInvitation));
		WorkVendorInvitationToGroupAssociation workVendorInvitationToGroupAssociation =
			new WorkVendorInvitationToGroupAssociation(workVendorInvitation.getId(), group.getId());
		workVendorInvitationToGroupAssociationDAO.saveAll(Lists.newArrayList(workVendorInvitationToGroupAssociation));

		Map<Long, Set<Long>> vendorGroupAssociations = workVendorInvitationDAO.getVendorInvitationGroupAssociationsByWorkId(work.getId());
		assertTrue(vendorGroupAssociations.size() == 1);
		assertTrue(vendorGroupAssociations.containsKey(vendor.getId()));
		assertTrue(vendorGroupAssociations.get(vendor.getId()).size() == 1);
		assertTrue(vendorGroupAssociations.get(vendor.getId()).contains(group.getId()));
	}
}
