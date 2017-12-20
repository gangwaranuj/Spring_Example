package com.workmarket.domains.work.service.workresource;

import com.google.common.collect.Lists;
import com.workmarket.service.business.UserService;
import com.workmarket.service.business.dto.WorkResourceDetail;
import com.workmarket.domains.model.analytics.ResourceScoreCard;
import com.workmarket.domains.work.dao.WorkResourceDAO;
import com.workmarket.domains.work.dao.WorkResourceLabelDAO;
import com.workmarket.domains.work.model.WorkResourceLabel;
import com.workmarket.domains.work.model.WorkResourceLabelType;
import com.workmarket.domains.work.model.WorkSchedule;
import com.workmarket.domains.work.service.resource.WorkResourceChangeLogService;
import com.workmarket.service.analytics.AnalyticsService;
import com.workmarket.thrift.work.ResourceLabel;
import com.workmarket.thrift.work.ResourceNote;
import com.workmarket.utility.CollectionUtilities;
import org.apache.commons.collections.MapUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import java.util.List;
import java.util.Map;

import static org.apache.commons.collections.CollectionUtils.isNotEmpty;

@Component
public class WorkResourceDetailDecoratorImpl implements WorkResourceDetailDecorator {

	@Autowired private WorkResourceChangeLogService workResourceChangeLogService;
	@Autowired private WorkResourceLabelDAO workResourceLabelDAO;
	@Autowired private WorkResourceDAO workResourceDAO;
	@Autowired private AnalyticsService analyticsService;
	@Autowired private UserService userService;


	@Override
	public List<WorkResourceDetail> decorateNotes(long workId, List<WorkResourceDetail> workResources) {
		if (isNotEmpty(workResources)) {
			Map<Long, List<ResourceNote>> notesLookup = workResourceChangeLogService.findResourceNotesByWorkId(workId);
			if (MapUtils.isNotEmpty(notesLookup)) {
				for (WorkResourceDetail workResource : workResources) {
					workResource.setNotes((List<ResourceNote>) MapUtils.getObject(notesLookup, workResource.getUserId(), Lists.newArrayListWithExpectedSize(0)));
				}
			}
		}
		return workResources;
	}

	@Override
	public List<WorkResourceDetail> decorateLabels(long workId, List<WorkResourceDetail> workResources) {
		if (isNotEmpty(workResources)) {
			Map<Long, List<WorkResourceLabel>> labelsLookup = workResourceLabelDAO.findVisibleForWork(workId);
			if (MapUtils.isNotEmpty(labelsLookup)) {
				for (WorkResourceDetail workResource : workResources) {

					List<ResourceLabel> resourceLabels = Lists.newArrayList();
					List<WorkResourceLabel> workResourceLabels = labelsLookup.get(workResource.getUserId());
					if (isNotEmpty(workResourceLabels)) {
						for (WorkResourceLabel workResourceLabel : workResourceLabels) {
							WorkResourceLabelType workResourceLabelType = workResourceLabel.getWorkResourceLabelType();
							if (!workResourceLabelType.isVisible()) {
								continue;
							}
							resourceLabels.add(new ResourceLabel()
									.setId(workResourceLabel.getId())
									.setCode(workResourceLabelType.getCode())
									.setDescription(workResourceLabelType.getDescription())
									.setIgnored(workResourceLabel.isIgnored())
									.setConfirmed(workResourceLabel.isConfirmed())
									.setEncryptedId(workResourceLabel.getEncryptedId()));
						}
					}
					workResource.setLabels(resourceLabels);
				}
			}
		}
		return workResources;
	}

	@Override
	public List<WorkResourceDetail> decorateScheduleConflicts(WorkSchedule workSchedule, List<WorkResourceDetail> workResources) {
		Assert.notNull(workSchedule);

		if (isNotEmpty(workResources)) {
			Map<Long, List<WorkSchedule>> conflictedWorkByWorkResource = workResourceDAO.findActiveWorkScheduleByWorkResourceExcludingCurrentWork(workSchedule.getWorkId());
			for (WorkResourceDetail workResource : workResources) {
				List<WorkSchedule> workScheduleList = (List<WorkSchedule>) MapUtils.getObject(conflictedWorkByWorkResource, workResource.getUserId(), Lists.newArrayList());
				for (WorkSchedule schedule : workScheduleList) {
					if (workSchedule.contains(schedule)) {
						workResource.setScheduleConflict(true);
						break;
					}
				}
			}
		}

		return workResources;
	}

	@Override
	public List<WorkResourceDetail> decorateScoreCards(long companyId, List<WorkResourceDetail> workResources) {
		if (isNotEmpty(workResources)) {
			List<Long> userIds = CollectionUtilities.newListPropertyProjection(workResources, "userId");
			Map<Long, ResourceScoreCard> scoreCards = analyticsService.getResourceScoreCards(userIds);
			Map<Long, ResourceScoreCard> scoreCardsForCompany = analyticsService.getResourceScoreCardsForCompany(companyId, userIds);
			for (WorkResourceDetail workResource : workResources) {
				ResourceScoreCard resourceScoreCard = (ResourceScoreCard)MapUtils.getObject(scoreCards, workResource.getUserId(), new ResourceScoreCard());
				ResourceScoreCard resourceScoreCardForCompany = (ResourceScoreCard)MapUtils.getObject(scoreCardsForCompany, workResource.getUserId(), new ResourceScoreCard());
				workResource.setResourceScoreCard(resourceScoreCard);
				workResource.setResourceCompanyScoreCard(resourceScoreCardForCompany);
			}
		}
		return workResources;
	}

	@Override
	public List<WorkResourceDetail> decorateDispatcher(long workId, List<WorkResourceDetail> workResources) {

		if (isNotEmpty(workResources)) {
			for (WorkResourceDetail workResource : workResources) {
				workResource.setDispatcher(workResourceDAO.getDispatcherForWorkAndWorker(workId, workResource.getUserId()));
			}
		}

		return workResources;
	}

	@Override
	public List<WorkResourceDetail> decorateBlockedWorker(
		Long blockingCompanyId,
		List<WorkResourceDetail> workResources) {
		if (isNotEmpty(workResources)) {
			for (WorkResourceDetail workResource : workResources) {
				workResource.setBlocked(
					userService.isUserBlockedByCompany(workResource.getUserId(), workResource.getCompanyId(), blockingCompanyId));
			}
		}
		return workResources;
	}
}
