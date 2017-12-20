package com.workmarket.service.business.queue;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.workmarket.data.solr.indexer.work.WorkIndexer;
import com.workmarket.domains.model.WorkResource;
import com.workmarket.domains.model.WorkStatusType;
import com.workmarket.domains.work.dao.WorkDAO;
import com.workmarket.domains.work.model.Work;
import com.workmarket.domains.work.service.WorkChangeLogService;
import com.workmarket.domains.work.service.WorkService;
import com.workmarket.domains.work.service.workresource.WorkResourceService;
import com.workmarket.service.business.UserNotificationService;
import com.workmarket.service.summary.SummaryService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class WorkEventQueueServiceImpl implements WorkEventQueueService {
	private static final Log logger = LogFactory.getLog(WorkEventQueueServiceImpl.class);

	@Autowired private UserNotificationService userNotificationService;
	@Autowired private WorkService workService;
	@Autowired private WorkResourceService workResourceService;
	@Autowired private SummaryService summaryService;
	@Autowired private WorkChangeLogService workChangeLogService;
	@Autowired private WorkIndexer workIndexer;
	@Autowired private WorkDAO workDAO;


	@Override
	public void onWorkPaid(WorkPaidDelayedEvent delay) {
		logger.debug(delay);
		Work work = workService.findWork(delay.getWorkId());
		if (work == null) {
			return;
		}

		WorkResource workResource = workService.findWorkResourceById(delay.getWorkResourceId());
		if (workResource == null) {
			return;
		}

		try {
			logger.info("****** Processing onWorkPaid for work id: " + delay.getWorkId());
			userNotificationService.onWorkClosedAndPaid(delay.getWorkResourceId());
			workService.updateMyPaidResourcesGroup(delay.getWorkId(), delay.getWorkResourceId(), delay.getActor());
		} catch (Exception e) {
			logger.error("Error while processing work id " + delay.getWorkId(), e);
		}
	}

	@Override
	public void onWorkUploaded(WorkUploadDelayedEvent delay) {
		logger.debug(delay);
		
		if (delay.getWorkIds() == null || delay.getWorkIds().isEmpty()) {
			return;
		}
		workIndexer.reindexById(Sets.newHashSet(delay.getWorkIds()));
		//Notify resources only after the work has been reindexed
		List<Work> workList = workDAO.findWorksByIds(delay.getWorkIds());
		for (Work work : workList) {
			List<Long> usersToNotify = Lists.newArrayList();
			for (WorkResource resource : workResourceService.findAllResourcesForWork(work.getId())) {
				usersToNotify.add(resource.getUser().getId());
			}
			if (!usersToNotify.isEmpty()) {
				Long id = work.getId();
				try {
					workChangeLogService.saveWorkCreatedChangeLog(id, delay.getWorkActionRequest().getModifierId(), delay.getWorkActionRequest().getMasqueradeId(), delay.getWorkActionRequest().getOnBehalfOfId());
					userNotificationService.onWorkCreated(id);
					userNotificationService.onWorkInvitation(work.getId(), usersToNotify, false);
					summaryService.saveWorkHistorySummary(id, WorkStatusType.DRAFT, null, delay.getWorkActionRequest().getLastActionOn());
					summaryService.saveWorkStatusTransitionHistorySummary(id, null, WorkStatusType.newWorkStatusType(WorkStatusType.DRAFT), 0, delay.getWorkActionRequest().getLastActionOn());
				} catch (Exception e) {
					logger.error("Error while processing work id " + id, e);
				}
			}
		}
	}
}
