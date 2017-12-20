package com.workmarket.domains.work.service;

import com.google.common.collect.Sets;
import com.workmarket.domains.work.dao.BaseWorkDAO;
import com.workmarket.domains.work.dao.RoutingStrategyGroupDAO;
import com.workmarket.domains.work.model.route.RoutingStrategyGroup;
import com.workmarket.domains.work.service.route.RoutingStrategyService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import javax.annotation.Resource;
import java.util.List;

@Service
public class WorkBatchSendServiceImpl implements WorkBatchSendService {

	private static int ZERO_MINUTE_DELAY = 0;
	private static boolean ASSIGN_TO_FIRST_TO_ACCEPT = true;
	private static boolean NEED_TO_APPLY = false;

	private static final Log logger = LogFactory.getLog(WorkBatchSendServiceImpl.class);

	@Autowired private BaseWorkDAO abstractWorkDAO;

	@Resource private RoutingStrategyGroupDAO routingStrategyGroupDAO;
	@Resource private RoutingStrategyService routingStrategyService;

	@Override
	public void sendWorkBatchViaWorkSend(List<String> workNumbers) {
		Assert.notNull(workNumbers);

		for (String workNumber : workNumbers) {
			long workId = abstractWorkDAO.findWorkId(workNumber);
			routingStrategyService.addAutoRoutingStrategy(workId, NEED_TO_APPLY);
		}
	}

	@Override
	public void sendWork(WorkBatchSendRequest workBatchSendRequest) {
		Assert.notNull(workBatchSendRequest);

		for (String workNumber : workBatchSendRequest.getWorkNumbers()) {
			RoutingStrategyGroup routingStrategyGroup = new RoutingStrategyGroup();
			routingStrategyGroupDAO.saveOrUpdate(routingStrategyGroup);

			long workId = abstractWorkDAO.findWorkId(workNumber);
			sendWorkToUsers(workId, routingStrategyGroup.getId(), workBatchSendRequest.getAssignToFirstToAcceptUserNumbers(), ASSIGN_TO_FIRST_TO_ACCEPT);
			sendWorkToVendors(workId, routingStrategyGroup.getId(), workBatchSendRequest.getAssignToFirstToAcceptVendorCompanyNumbers(), ASSIGN_TO_FIRST_TO_ACCEPT);
			sendWorkToGroups(workId, routingStrategyGroup.getId(), workBatchSendRequest.getAssignToFirstToAcceptGroupIds(), ASSIGN_TO_FIRST_TO_ACCEPT);

			sendWorkToUsers(workId, routingStrategyGroup.getId(), workBatchSendRequest.getNeedToApplyUserNumbers(), NEED_TO_APPLY);
			sendWorkToVendors(workId, routingStrategyGroup.getId(), workBatchSendRequest.getNeedToApplyVendorCompanyNumbers(), NEED_TO_APPLY);
			sendWorkToGroups(workId, routingStrategyGroup.getId(), workBatchSendRequest.getNeedToApplyGroupIds(), NEED_TO_APPLY);

			routingStrategyService.scheduleExecuteRoutingStrategyGroup(workId, routingStrategyGroup.getId(), ZERO_MINUTE_DELAY);
		}
	}

	private void sendWorkToUsers(long workId, long routingStrategyGroupId, List<String> userNumbers, boolean assignToFirstToAccept) {
		if (CollectionUtils.isEmpty(userNumbers)) {
			return;
		}
		routingStrategyService.addUserNumbersRoutingStrategy(workId, routingStrategyGroupId, Sets.newHashSet(userNumbers), ZERO_MINUTE_DELAY, assignToFirstToAccept);
	}

	private void sendWorkToGroups(long workId, long routingStrategyGroupId, List<Long> groupIds, boolean assignToFirstToAccept) {
		if (CollectionUtils.isEmpty(groupIds)) {
			return;
			}
		routingStrategyService.addGroupIdsRoutingStrategy(workId, routingStrategyGroupId, Sets.newHashSet(groupIds), ZERO_MINUTE_DELAY, assignToFirstToAccept);
	}

	private void sendWorkToVendors(long workId, long routingStrategyGroupId, List<String> vendorCompanyIds, boolean assignToFirstToAccept) {
		if (CollectionUtils.isEmpty(vendorCompanyIds)) {
			return;
		}
		routingStrategyService.addVendorRoutingStrategyByCompanyNumbers(workId, routingStrategyGroupId, Sets.newHashSet(vendorCompanyIds), ZERO_MINUTE_DELAY, assignToFirstToAccept);
	}
}
