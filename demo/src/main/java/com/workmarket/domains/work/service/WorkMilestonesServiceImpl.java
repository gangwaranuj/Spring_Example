package com.workmarket.domains.work.service;

import com.workmarket.dao.summary.work.WorkMilestonesDAO;
import com.workmarket.domains.model.summary.work.WorkMilestones;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class WorkMilestonesServiceImpl implements WorkMilestonesService {

	@Autowired
	private WorkMilestonesDAO workMilestonesDAO;

	@Override
	public WorkMilestones findWorkMilestonesByWorkId(Long workId) {
		return workMilestonesDAO.findWorkMilestonesByWorkId(workId);
	}

	@Override
	public void saveOrUpdate(WorkMilestones workMilestones) {
		workMilestonesDAO.saveOrUpdate(workMilestones);
	}
}
