package com.workmarket.domains.velvetrope.rope;

import com.workmarket.domains.model.DateRange;
import com.workmarket.domains.work.dao.WorkResourceDAO;
import com.workmarket.domains.work.model.AbstractWork;
import com.workmarket.domains.work.model.WorkSchedule;
import com.workmarket.domains.work.service.WorkService;
import com.workmarket.thrift.work.Work;
import com.workmarket.utility.DateUtilities;
import com.workmarket.velvetrope.Rope;

import java.util.Calendar;
import java.util.List;

public class AvoidScheduleConflictsThriftRope implements Rope {

	private final WorkResourceDAO workResourceDAO;
	private final WorkService workService;
	private final Work work;
	private final long userId;
	List<AbstractWork> conflicts;

	public AvoidScheduleConflictsThriftRope(
		WorkResourceDAO workResourceDAO,
		WorkService workService,
		Work work,
		long userId,
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

		Calendar from = DateUtilities.getCalendarFromMillis(work.getSchedule().getFrom());
		Calendar through =
			work.getSchedule().getThrough() == 0 ?
				null :
				DateUtilities.getCalendarFromMillis(work.getSchedule().getThrough());

		WorkSchedule thisWorkSchedule =
			workService.augmentWorkSchedule(
				new WorkSchedule(new DateRange(from, through))
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
