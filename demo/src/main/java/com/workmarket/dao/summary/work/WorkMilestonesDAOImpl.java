package com.workmarket.dao.summary.work;

import com.workmarket.dao.AbstractDAO;
import com.workmarket.domains.model.summary.work.WorkMilestones;
import org.springframework.stereotype.Repository;
import org.springframework.util.Assert;

@Repository
public class WorkMilestonesDAOImpl extends AbstractDAO<WorkMilestones> implements WorkMilestonesDAO {
	
	@Override
	protected Class<WorkMilestones> getEntityClass() {
		return WorkMilestones.class;
	}

	@Override
	public WorkMilestones findWorkMilestonesByWorkId(Long workId) {
		Assert.notNull(workId, "Work id is required");

		WorkMilestones milestones = get(workId);

		if (milestones == null) {
			milestones = new WorkMilestones();
			milestones.setWorkId(workId);
			saveOrUpdate(milestones);
		}

		return milestones;
	}
}