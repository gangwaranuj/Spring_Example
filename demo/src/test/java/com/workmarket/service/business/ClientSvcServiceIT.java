package com.workmarket.service.business;

import com.workmarket.domains.model.Pagination;
import com.workmarket.domains.model.assessment.AbstractAssessment;
import com.workmarket.domains.groups.model.UserGroup;
import com.workmarket.domains.model.note.concern.Concern;
import com.workmarket.domains.model.note.concern.ConcernPagination;
import com.workmarket.domains.model.recruiting.RecruitingCampaign;
import com.workmarket.domains.work.model.Work;
import com.workmarket.service.business.dto.ClientSvcDashboardDTO;
import com.workmarket.service.business.dto.RegistrationConcernDTO;
import com.workmarket.service.business.dto.UserDTO;
import com.workmarket.service.infra.business.AuthenticationService;
import com.workmarket.test.IntegrationTest;
import com.workmarket.utility.RandomUtilities;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

@RunWith(SpringJUnit4ClassRunner.class)
@Category(IntegrationTest.class)
public class ClientSvcServiceIT extends BaseServiceIT {

	@Autowired private ClientSvcService clientSvcService;
	@Autowired private RegistrationService registrationService;
	@Autowired private AuthenticationService authenticationService;

	@Test
	@Transactional
	public void test_getDashboard() throws Exception {

		UserDTO dto = new UserDTO();
		dto.setFirstName("Wilbur");
		dto.setLastName("Cobb");
		dto.setEmail("wilbur" + RandomUtilities.nextLong() + "@cobb.com");
		dto.setPassword("" + RandomUtilities.nextLong());

		registrationService.registerNewForCompany(dto, COMPANY_ID, new Long[] {
			ACL_ROLE_USER,
			ACL_ROLE_WORKER,
			ACL_ROLE_SHARED_WORKER
		});

		ClientSvcDashboardDTO dashboard = clientSvcService.getDashboard();

		Assert.assertNotNull(dashboard);

		Assert.assertTrue(dashboard.getNewUsers() >= 1);
	}

	@Test
	@Transactional
	public void test_reportUserGroup() throws Exception {
		UserGroup group = newCompanyUserGroup(COMPANY_ID);
		Concern concern = clientSvcService.reportUserGroup(group.getId(), "This group is innapropiate");
		Assert.assertTrue(concern.getContent().equals("This group is innapropiate"));
	}

	@Test
	public void test_reportWork() throws Exception {
		Work work = newWork(ANONYMOUS_USER_ID);
		Concern concern = clientSvcService.reportWork(work.getId(), "This work is innapropiate");
		Assert.assertTrue(concern.getContent().equals("This work is innapropiate"));
	}

	@Test
	public void test_reportCampaign() throws Exception {
		RecruitingCampaign campaign = newRecruitingCampaign(COMPANY_ID, null);
		RegistrationConcernDTO dto = new RegistrationConcernDTO();
		dto.setMessage("This campaign is inappropriate");
		dto.setUserName("John Mayer");
		dto.setEmail("email@gmail.com");
		Concern concern = clientSvcService.reportRecruitingCampaign(COMPANY_ID, campaign.getId(), dto);
		Assert.assertTrue(concern.getContent().equals("This campaign is inappropriate"));
	}

	@Test
	public void test_reportAssessment() throws Exception {
		AbstractAssessment assessment = newAssessment();
		Concern concern = clientSvcService.reportAssessment(assessment.getId(), "This assessment is wrong, I'm not wrong. :P");
		Assert.assertTrue(concern.getContent().equals("This assessment is wrong, I'm not wrong. :P"));
	}

	@Test
	public void test_reportProfile() throws Exception {
		Concern concern = clientSvcService.reportProfile(ANONYMOUS_USER_ID, "This profile is inappropriate");
		Assert.assertTrue(concern.getContent().equals("This profile is inappropriate"));
	}

	@Test
	public void test_findAllConcerns() throws Exception {
		ConcernPagination pagination = new ConcernPagination();
		pagination.setResultsLimit(10);
		pagination.setSortColumn(ConcernPagination.SORTS.CREATED_ON);
		pagination.setSortDirection(Pagination.SORT_DIRECTION.DESC);

		pagination = clientSvcService.findAllConcerns(pagination);
		Assert.assertNotNull(pagination.getResults());
		Assert.assertTrue(10 >= pagination.getResults().size());
	}

	@Test
	@Transactional
	public void test_resolveConcern() throws Exception {
		Work work = newWork(ANONYMOUS_USER_ID);
		Concern concern = clientSvcService.reportWork(work.getId(), "This work is inappropriate");
		Assert.assertTrue(concern.getContent().equals("This work is inappropriate"));
		Assert.assertFalse(concern.isResolved());
		clientSvcService.updateConcernResolvedStatus(concern.getId(), true);

		concern = clientSvcService.findConcernById(concern.getId());
		Assert.assertTrue(concern.isResolved());
		Assert.assertEquals(concern.getResolvedBy(), authenticationService.getCurrentUser());

		clientSvcService.updateConcernResolvedStatus(concern.getId(), false);

		concern = clientSvcService.findConcernById(concern.getId());
		Assert.assertFalse(concern.isResolved());
	}

	@Test
	@Transactional
	public void test_deleteConcern() throws Exception {
		Work work = newWork(ANONYMOUS_USER_ID);
		Concern concern = clientSvcService.reportWork(work.getId(), "This work is innapropiate");
		Assert.assertTrue(concern.getContent().equals("This work is innapropiate"));
		Assert.assertFalse(concern.getDeleted());
		Assert.assertFalse(concern.isResolved());
		clientSvcService.deleteConcern(concern.getId());

		concern = clientSvcService.findConcernById(concern.getId());
		Assert.assertTrue(concern.getDeleted());
		Assert.assertFalse(concern.isResolved());
	}
}
