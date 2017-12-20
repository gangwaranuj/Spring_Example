package com.workmarket.domains.velvetrope.rope;

import com.google.common.collect.Sets;
import com.workmarket.domains.model.DateRange;
import com.workmarket.domains.model.User;
import com.workmarket.domains.model.WorkResource;
import com.workmarket.domains.work.dao.WorkResourceDAO;
import com.workmarket.domains.work.model.Work;
import com.workmarket.domains.work.model.WorkSchedule;
import com.workmarket.domains.work.service.WorkService;
import com.workmarket.domains.work.service.resource.WorkAuthorizationResponse;
import com.workmarket.service.business.wrapper.WorkRoutingResponseSummary;
import com.workmarket.velvetrope.Rope;

import java.util.List;
import java.util.Map;
import java.util.Set;

public class AvoidScheduleConflictsRoutingRope implements Rope {
	private final Work work;
	private final WorkRoutingResponseSummary responseSummary;
	private final Set<WorkResource> invitedWorkResources;
	private WorkResourceDAO workResourceDAO;
	private final WorkService workService;

	public AvoidScheduleConflictsRoutingRope(
		Work work,
		WorkRoutingResponseSummary responseSummary,
		Set<WorkResource> invitedWorkResources,
		WorkResourceDAO workResourceDAO,
		WorkService workService
	) {
		this.work = work;
		this.responseSummary = responseSummary;
		this.invitedWorkResources = invitedWorkResources;
		this.workResourceDAO = workResourceDAO;
		this.workService = workService;
	}

	@Override
	public void enter() {
		WorkSchedule workSchedule =
			workService.augmentWorkSchedule(
				new WorkSchedule(new DateRange(work.getScheduleFrom(), work.getScheduleThrough()))
					.setWorkId(work.getId())
					.setWorkNumber(work.getWorkNumber())
					.setCompanyId(work.getCompany().getId())
			);

		Set<Long> invitedUserIds = Sets.newHashSet();
		for (WorkResource invitedWorkResource : invitedWorkResources) {
			invitedUserIds.add(invitedWorkResource.getUser().getId());
		}

		Map<Long, List<WorkSchedule>> userSchedules =
			workResourceDAO.findActiveSchedulesExcludingWorkByUserIds(work.getId(), invitedUserIds);

		Set<WorkResource> conflictResources = Sets.newHashSet();
		for (WorkResource resourceToSkip : invitedWorkResources) {
			User user = resourceToSkip.getUser();
			if (workSchedule.overlaps(workService.augmentWorkSchedules(userSchedules.get(user.getId())))) {
				conflictResources.add(resourceToSkip);
				responseSummary.getResponse().get(WorkAuthorizationResponse.SUCCEEDED).remove(user.getUserNumber());
				responseSummary.addToWorkAuthorizationResponse(WorkAuthorizationResponse.FAILED, user.getUserNumber());
			}
		}
		if (!conflictResources.isEmpty()) {
			invitedWorkResources.removeAll(conflictResources);
		}
	}
}
