package com.workmarket.domains.work.service.actions;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.workmarket.domains.model.User;
import com.workmarket.domains.work.model.Work;
import com.workmarket.search.request.work.WorkSearchRequest;
import com.workmarket.service.business.BaseServiceIT;
import com.workmarket.service.infra.security.WorkContext;
import com.workmarket.test.IntegrationTest;
import com.workmarket.web.helpers.AjaxResponseBuilder;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;


@RunWith(SpringJUnit4ClassRunner.class)
@Category(IntegrationTest.class)
public class WorkEventAuthServiceIT extends BaseServiceIT {

	@Autowired WorkEventFactory workEventFactory;
	@Autowired WorkEventAuthService workEventAuthService;

	DoNothingEvent event;
	User buyer;
	User contractor;

	Work work1;
	Work work2;

	WorkSearchRequest workSearchRequest;
	List<Work> works;
	List<String> workNumbers;
	AjaxResponseBuilder response;
	String messageKey = "doNothing";
	ImmutableSet<WorkContext> authz = ImmutableSet.of(
			WorkContext.OWNER,
			WorkContext.COMPANY_OWNED,
			WorkContext.ACTIVE_RESOURCE);

	@Before
	public void setup() throws Exception{
		buyer = newEmployeeWithCashBalance();
		contractor = newContractorIndependentlane4Ready();

		work1 = newWork(buyer.getId());

		workNumbers = Lists.newArrayList();
		workNumbers.add(work1.getWorkNumber());

		works = Lists.newArrayList();
		response = new AjaxResponseBuilder().setSuccessful(true);

	}

	@Test
	public void test_validateandauth_owner(){
		logger.info("eventAuthServiceIntegrationTest");
		works.add(work1);
		List<Work> validWork = workEventAuthService.validateAndAuthorizeWork(buyer,works,response,messageKey,authz);
		Assert.assertTrue(validWork.size() == 1);
	}

	@Test
	public void test_validateAndAuth_notRelated(){
		works.add(work1);
		List<Work> validWork = workEventAuthService.validateAndAuthorizeWork(contractor,works,response,messageKey,authz);
		Assert.assertTrue(validWork.size()==0);
	}


	@Test
	public void test_validateAndAuth_activeResource(){
		laneService.addUserToCompanyLane2(contractor.getId(), buyer.getCompany().getId());
		Work work = newWorkForEmployeeWithActiveResource (buyer.getId(),contractor);
		works.add(work);
		List<Work> validWork = workEventAuthService.validateAndAuthorizeWork(contractor,works,response,messageKey,authz);
		Assert.assertTrue(validWork.size()==1);
	}

	@Test
	public void test_validateAndAuth_companyOwned(){
		User coworker = null;
		try{
			coworker = newCompanyEmployee(buyer.getCompany().getId());
		}catch(Exception e){
		}
		works.add(work1);
		List<Work> validWork = workEventAuthService.validateAndAuthorizeWork(coworker,works,response,messageKey,authz);
		Assert.assertTrue(validWork.size()==1);
	}

}
