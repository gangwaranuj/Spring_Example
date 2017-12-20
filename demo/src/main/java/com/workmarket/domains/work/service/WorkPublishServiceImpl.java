package com.workmarket.domains.work.service;

import com.workmarket.domains.work.dao.WorkDAO;
import com.workmarket.domains.work.model.Work;
import com.workmarket.domains.work.service.route.WorkRoutingService;
import com.workmarket.service.exception.WorkMarketException;
import com.workmarket.service.exception.work.WorkNotFoundException;
import com.workmarket.thrift.work.WorkPublishRequest;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import javax.annotation.Resource;
import java.util.List;

@Service
public class WorkPublishServiceImpl implements WorkPublishService {

	private static final Log logger = LogFactory.getLog(WorkPublishServiceImpl.class);

	@Resource private WorkDAO workDAO;
	@Resource private WorkRoutingService workRoutingService;

	@Override
	public void publish(WorkPublishRequest workPublishRequest) throws WorkMarketException {
		List<Work> assignmentsToUpdate = workDAO.findWorkByWorkNumber(workPublishRequest.getWorkNumbers());
		if (assignmentsToUpdate.isEmpty() || assignmentsToUpdate.size() < workPublishRequest.getWorkNumbers().size()) {
			throw new WorkMarketException("Can't publish one or more invalid work numbers");
		}

		for (Work work : assignmentsToUpdate) {
			publish(work);
		}
	}

	@Override
	public void publish(Work work) {
		Assert.notNull(work);

		if (work.isWorkBundle()) {
			return;
		}

		if(!work.isBundleOrInBundle()) {
			work.getManageMyWorkMarket().setShowInFeed(true);
		}

		workRoutingService.openWork(work);
	}

	@Override
	public void removeFromFeed(WorkPublishRequest workPublishRequest) throws WorkMarketException {
		List<Work> assignmentsToUpdate = workDAO.findWorkByWorkNumber(workPublishRequest.getWorkNumbers());
		if (assignmentsToUpdate.isEmpty() || assignmentsToUpdate.size() < workPublishRequest.getWorkNumbers().size()) {
			throw new WorkMarketException("Can't remove one or more invalid work numbers from feed");
		}

		for (Work work : assignmentsToUpdate) {
			work.getManageMyWorkMarket().setShowInFeed(false);
		}
	}
}
