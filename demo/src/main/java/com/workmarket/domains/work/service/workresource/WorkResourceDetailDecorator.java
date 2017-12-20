package com.workmarket.domains.work.service.workresource;

import com.workmarket.service.business.dto.WorkResourceDetail;
import com.workmarket.domains.work.model.WorkSchedule;

import java.util.List;

public interface WorkResourceDetailDecorator {

	List<WorkResourceDetail> decorateNotes(long workId, List<WorkResourceDetail> workResources);

	List<WorkResourceDetail> decorateLabels(long workId, List<WorkResourceDetail> workResources);

	List<WorkResourceDetail> decorateScheduleConflicts(WorkSchedule workSchedule, List<WorkResourceDetail> workResources);

	List<WorkResourceDetail> decorateScoreCards(long companyId, List<WorkResourceDetail> workResources);

	List<WorkResourceDetail> decorateDispatcher(long workId, List<WorkResourceDetail> workResources);

	List<WorkResourceDetail> decorateBlockedWorker(
		Long blockingCompanyId,
		List<WorkResourceDetail> workResources);
}
