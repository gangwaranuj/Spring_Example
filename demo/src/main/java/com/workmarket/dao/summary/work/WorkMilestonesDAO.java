package com.workmarket.dao.summary.work;

import com.workmarket.dao.DAOInterface;
import com.workmarket.domains.model.summary.work.WorkMilestones;

public interface WorkMilestonesDAO extends DAOInterface<WorkMilestones> {
	WorkMilestones findWorkMilestonesByWorkId(Long workId);
}
