package com.workmarket.domains.work.service;

import com.workmarket.domains.model.summary.work.WorkMilestones;

public interface WorkMilestonesService {
	/**
	 * Finds a work milestones record. There is only one work milestones row per work. The row will be lazy created if it does not exists.
	 * 
	 * @param workId
	 * @return work milestones record
	 */
	WorkMilestones findWorkMilestonesByWorkId(Long workId);

	void saveOrUpdate(WorkMilestones workMilestones);
}
