package com.workmarket.service.business;

import com.google.common.collect.Sets;
import com.workmarket.dto.RecruitingCampaignUserPagination;
import com.workmarket.domains.model.recruiting.RecruitingCampaign;
import com.workmarket.domains.model.Company;
import com.workmarket.domains.model.User;
import com.workmarket.domains.model.recruiting.RecruitingCampaignPagination;
import com.workmarket.test.IntegrationTest;
import com.workmarket.utility.RandomUtilities;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Set;

@RunWith(SpringJUnit4ClassRunner.class)
@Category(IntegrationTest.class)
public class RecruitingServiceIT extends BaseServiceIT {

	@Autowired private RecruitingService recruitingService;
	@Autowired private CompanyService companyService;

	private Company company;
	private RecruitingCampaign recruitingCampaign;

	@Before
	public void setup() throws Exception {
		company = companyService.createCompany("companyName" + RandomUtilities.generateNumericString(10), true,Company.TEST_CUSTOMER_TYPE);
		Assert.assertNotNull(company);

		recruitingCampaign = super.newRecruitingCampaign(company.getId(), null);
	}

	@Test
	public void test_findRecruitingCampaignById_pass() throws Exception {
		RecruitingCampaign campaign = recruitingService.findRecruitingCampaign(recruitingCampaign.getId());
		Assert.assertNotNull(campaign);
	}

	@Test
	public void test_findRecruitingCampaignByCompanyId_pass() throws Exception {
		RecruitingCampaign campaign = recruitingService.findRecruitingCampaign(company.getId(), recruitingCampaign.getId());
		Assert.assertNotNull(campaign);
	}

	@Test
	public void test_findRecruitingCampaignByCompanyEncryptedId_pass() throws Exception {
		RecruitingCampaign campaign = recruitingService.findRecruitingCampaign(company.getId(), recruitingCampaign.getEncryptedId());
		Assert.assertNotNull(campaign);
	}

	@Test
	public void test_findRecruitingCampaignByEncryptedId_pass() throws Exception {
		RecruitingCampaign campaign = recruitingService.findRecruitingCampaign(recruitingCampaign.getEncryptedId());
		Assert.assertNotNull(campaign);
	}

	@Test
	public void test_findRecruitingCampaignByCampaign_fail() {
		long invalidId = 0L;
		RecruitingCampaign campaign = recruitingService.findRecruitingCampaign(invalidId);
		Assert.assertNull(campaign);
	}

	@Test
	public void test_findRecruitingCampaignByCompanyId_fail() {
		long invalidId = 0L;
		RecruitingCampaign campaign = recruitingService.findRecruitingCampaign(invalidId, invalidId);
		Assert.assertNull(campaign);
	}

	@Test
	public void test_existsByCampaignTitle() {
		Assert.assertTrue(recruitingService.existRecruitingCampaignByCompanyAndTitle(company.getId(), recruitingCampaign.getTitle()));
		Assert.assertFalse(recruitingService.existRecruitingCampaignByCompanyAndTitle(company.getId(), "NOT A TITLE"));
	}

	@Test
	public void test_findAllCampaignsByCompanyId_pass() throws Exception {
		RecruitingCampaignPagination pagination = new RecruitingCampaignPagination();
		pagination.setReturnAllRows();
		pagination = recruitingService.findAllCampaignsByCompanyId(company.getId(), pagination);
		Assert.assertEquals(1, pagination.getResults().size());

		RecruitingCampaign newCampaign = super.newRecruitingCampaign(company.getId(), null);
		Assert.assertNotNull(newCampaign);

		Company otherCompany = companyService.createCompany("companyName" + RandomUtilities.generateNumericString(10), true, Company.TEST_CUSTOMER_TYPE);
		Assert.assertNotNull(otherCompany);
		RecruitingCampaign otherCampaign = super.newRecruitingCampaign(otherCompany.getId(), null);
		Assert.assertNotNull(otherCampaign);

		pagination = recruitingService.findAllCampaignsByCompanyId(company.getId(), pagination);
		Assert.assertEquals(2, pagination.getResults().size());
	}

	@Test
	public void test_countCampaignsForCompany() throws Exception {
		User u = super.newCompanyEmployee(company.getId());
		int count = recruitingService.countCampaignsForCompany(u.getId());
		Assert.assertEquals(1, count);
	}

	@Test
	public void test_countCampaignsForCompanyZero_pass() throws Exception {
		Company otherCompany = companyService.createCompany("companyName" + RandomUtilities.generateNumericString(10),true, Company.TEST_CUSTOMER_TYPE);
		Assert.assertNotNull(otherCompany);
		User u = super.newCompanyEmployee(otherCompany.getId());
		int count = recruitingService.countCampaignsForCompany(u.getId());
		Assert.assertEquals(0, count);
	}

	@Test
	public void test_toggleActiveRecruitingCampaign() throws Exception {
		RecruitingCampaign rc = recruitingService.activateRecruitingCampaign(company.getId(), recruitingCampaign.getId());
		Assert.assertTrue(rc.isActive());

		rc = recruitingService.deactivateRecruitingCampaign(company.getId(), recruitingCampaign.getId());
		Assert.assertFalse(rc.isActive());

		rc = recruitingService.activateRecruitingCampaign(company.getId(), recruitingCampaign.getId());
		Assert.assertTrue(rc.isActive());
	}

	@Test
	public void test_deleteRecruitingCampaign_pass() throws Exception {
		RecruitingCampaign rc = super.newRecruitingCampaign(company.getId(), null);
		Assert.assertFalse(rc.getDeleted());
		rc = recruitingService.deleteRecruitingCampaign(company.getId(), rc.getId());
		Assert.assertTrue(rc.getDeleted());
	}

	@Test
	public void test_deleteRecruitingCampaign_fail() throws Exception {
		RecruitingCampaign rc = super.newRecruitingCampaign(company.getId(), null);
		Assert.assertFalse(rc.getDeleted());
		rc = recruitingService.deleteRecruitingCampaign(company.getId(), rc.getId());
		Assert.assertTrue(rc.getDeleted());

		RecruitingCampaign deleted_rc1 = recruitingService.deleteRecruitingCampaign(company.getId(), rc.getId());
		Assert.assertNull(deleted_rc1);
		RecruitingCampaign deleted_rc2 = recruitingService.deleteRecruitingCampaign(company.getId(), RandomUtilities.nextLong());
		Assert.assertNull(deleted_rc2);
	}

	@Test
	public void test_findAllRecruitingCampaignUsers() throws Exception {
		RecruitingCampaignUserPagination pagination = new RecruitingCampaignUserPagination();
		pagination.setReturnAllRows();

		pagination = recruitingService.findAllRecruitingCampaignUsers(pagination);
		int count = pagination.getRowCount();

		super.newEmployeeWithCampaign(recruitingCampaign.getId());

		pagination = recruitingService.findAllRecruitingCampaignUsers(pagination);
		int newUserCount = pagination.getRowCount();

		Assert.assertEquals(count + 1, newUserCount);
	}

	@Test
	public void test_declineRecruitingCampaignUser() throws Exception {

		User u = super.newEmployeeWithCampaign(recruitingCampaign.getId());

		Set status = laneService.findAllAssociationsWhereUserIdIn(company.getId(), Sets.newHashSet(u.getId()));
		Assert.assertFalse(status.isEmpty());

		recruitingService.declineRecruitingCampaignUser(company.getId(), recruitingCampaign.getId(), u.getId());
		status = laneService.findAllAssociationsWhereUserIdIn(company.getId(), Sets.newHashSet(u.getId()));
		Assert.assertTrue(status.isEmpty());
	}

	@Test
	public void test_approveRecruitingCampaignUser() throws Exception {
		User u = super.newEmployeeWithCampaign(recruitingCampaign.getId());

		Set status = laneService.findAllAssociationsWhereUserIdIn(company.getId(), Sets.newHashSet(u.getId()));
		Assert.assertFalse(status.isEmpty());

		recruitingService.approveRecruitingCampaignUser(company.getId(), recruitingCampaign.getId(), u.getId());
		status = laneService.findAllAssociationsWhereUserIdIn(company.getId(), Sets.newHashSet(u.getId()));
		Assert.assertFalse(status.isEmpty());
	}
}
