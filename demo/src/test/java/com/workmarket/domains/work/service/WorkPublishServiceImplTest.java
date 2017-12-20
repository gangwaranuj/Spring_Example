package com.workmarket.domains.work.service;

import com.google.common.collect.ImmutableList;
import com.workmarket.domains.model.ManageMyWorkMarket;
import com.workmarket.domains.work.dao.WorkDAO;
import com.workmarket.domains.work.model.Work;
import com.workmarket.domains.work.service.route.WorkRoutingService;
import com.workmarket.service.exception.WorkMarketException;
import com.workmarket.thrift.work.WorkPublishRequest;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.anyList;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class WorkPublishServiceImplTest {

	@Mock private WorkDAO workDAO;
	@Mock private WorkRoutingService workRoutingService;

	@InjectMocks WorkPublishServiceImpl workPublishService;

	@Test
	public void removeFromFeed_setsShowInFeedFalse() throws WorkMarketException {
		ManageMyWorkMarket manageMyWorkMarket = new ManageMyWorkMarket();
		manageMyWorkMarket.setShowInFeed(true);
		Work work = new Work();
		work.setManageMyWorkMarket(manageMyWorkMarket);
		when(workDAO.findWorkByWorkNumber(anyList())).thenReturn(ImmutableList.of(work));
		WorkPublishRequest workPublishRequest = new WorkPublishRequest();
		workPublishRequest.setWorkNumbers(ImmutableList.of("12345"));

		workPublishService.removeFromFeed(workPublishRequest);

		assertFalse(work.getManageMyWorkMarket().getShowInFeed());
	}

	@Test
	public void publish_work_setsShowInFeedTrue() throws WorkMarketException {
		ManageMyWorkMarket manageMyWorkMarket = new ManageMyWorkMarket();
		manageMyWorkMarket.setShowInFeed(false);
		Work work = new Work();
		work.setManageMyWorkMarket(manageMyWorkMarket);
		when(workDAO.findWorkByWorkNumber(anyList())).thenReturn(ImmutableList.of(work));
		WorkPublishRequest workPublishRequest = new WorkPublishRequest();
		workPublishRequest.setWorkNumbers(ImmutableList.of("12345"));

		workPublishService.publish(workPublishRequest);

		assertTrue(work.getManageMyWorkMarket().getShowInFeed());
	}
}
