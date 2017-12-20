package com.workmarket.service.business;

import com.workmarket.domains.groups.dao.ScheduledRunDAO;
import com.workmarket.domains.groups.model.ScheduledRun;
import com.workmarket.utility.DateUtilities;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class ScheduledRunServiceImpl implements ScheduledRunService {

	@Resource private ScheduledRunDAO scheduledRunDAO;

	@Override
	public void saveOrUpdate(ScheduledRun scheduledRun) {
		scheduledRunDAO.saveOrUpdate(scheduledRun);
	}

	@Override
	public void completeScheduledRun(ScheduledRun scheduledRun) {
		scheduledRun.setCompletedOn(DateUtilities.getCalendarNow());
		saveOrUpdate(scheduledRun);
	}
}
