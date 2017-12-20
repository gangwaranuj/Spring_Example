package com.workmarket.domains.work.service.actions;

import com.google.common.collect.Lists;
import com.workmarket.domains.model.User;
import com.workmarket.domains.work.model.Work;
import com.workmarket.service.business.BaseServiceIT;
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
public class WorkEventServiceIT extends BaseServiceIT {

	@Autowired WorkEventFactory workEventFactory;
	@Autowired WorkEventService workEventService;

	DoNothingEvent event;
	User buyer;
	User contractor;
	List<String> workNumbers;

	@Before
	public void setup() {

		try {
			buyer = newEmployeeWithCashBalance();
			contractor = newContractorIndependent();
		} catch (Exception e) {
		}
		Work work1 = newWork(buyer.getId());


		workNumbers = Lists.newArrayList();
		workNumbers.add(work1.getWorkNumber());
	}


	@Test
	public void test_event_valid() {
		event = workEventFactory.createDoNothingEvent(workNumbers, buyer, "do_nothing", "");
		AjaxResponseBuilder responseBuilder = workEventService.doAction(event);
		Assert.assertTrue(responseBuilder.isSuccessful());
	}


}
