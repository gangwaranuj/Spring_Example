package com.workmarket.domains.velvetrope.rope;

import com.workmarket.domains.model.DateRange;
import com.workmarket.domains.work.dao.WorkResourceDAO;
import com.workmarket.domains.work.model.AbstractWork;
import com.workmarket.domains.work.model.WorkSchedule;
import com.workmarket.domains.work.service.WorkService;
import com.workmarket.velvetrope.Rope;

import java.util.List;

public class AvoidScheduleConflictsModelRope implements Rope {
	private final WorkResourceDAO workResourceDAO;
	private final WorkService workService;
	private final AbstractWork work;
	private final Long userId;
	private final List<AbstractWork> conflicts;

	public AvoidScheduleConflictsModelRope(
		WorkResourceDAO workResourceDAO,
		WorkService workService,
		AbstractWork work,
		Long userId,
		List<AbstractWork> conflicts) {
		this.workResourceDAO = workResourceDAO;
		this.workService = workService;
		this.work = work;
		this.userId = userId;
		this.conflicts = conflicts;
	}

	@Override
	public void enter() {
		List<WorkSchedule> workSchedules = workResourceDAO.findWorkSchedulesByWorker(userId);

		WorkSchedule thisWorkSchedule =
			workService.augmentWorkSchedule(
				new WorkSchedule(new DateRange(work.getScheduleFrom(), work.getScheduleThrough()))
					.setWorkId(work.getId())
					.setWorkNumber(work.getWorkNumber())
					.setCompanyId(work.getCompany().getId())
			);

		for (WorkSchedule thatWorkSchedule : workSchedules) {
			workService.augmentWorkSchedule(thatWorkSchedule);
			if (thisWorkSchedule.overlaps(thatWorkSchedule) || thatWorkSchedule.overlaps(thisWorkSchedule)) {
				conflicts.add(workService.findWork(thatWorkSchedule.getWorkId()));
			}
		}
	}
}
