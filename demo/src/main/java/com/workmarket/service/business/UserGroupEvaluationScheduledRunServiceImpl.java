package com.workmarket.service.business;

import com.google.common.base.Optional;
import com.workmarket.domains.groups.dao.UserGroupEvaluationScheduledRunDAO;
import com.workmarket.domains.groups.model.ScheduledRun;
import com.workmarket.domains.groups.model.UserGroup;
import com.workmarket.domains.groups.model.UserGroupEvaluationScheduledRun;
import com.workmarket.utility.DateUtilities;
import org.joda.time.DateTime;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Calendar;
import java.util.Locale;

@Service
public class UserGroupEvaluationScheduledRunServiceImpl implements UserGroupEvaluationScheduledRunService {

	@Resource private ScheduledRunService scheduledRunService;
	@Resource private UserGroupEvaluationScheduledRunDAO userGroupEvaluationScheduledRunDAO;
	@Resource private UserGroupService userGroupService;

	@Override
	public Optional<ScheduledRun> getNextFutureScheduledRunForActiveOrInactiveGroup(long userGroupId) {
		ScheduledRun scheduledRun = userGroupEvaluationScheduledRunDAO.findNextFutureScheduledRunForActiveOrInactiveGroup(userGroupId);
		if (scheduledRun == null) {
			return Optional.absent();
		}
		return Optional.of(scheduledRun);
	}

	@Override
	public Optional<ScheduledRun> getNextScheduledRun(long userGroupId) {
		ScheduledRun scheduledRun = userGroupEvaluationScheduledRunDAO.findNextScheduledRunForActiveGroup(userGroupId);
		if (scheduledRun == null) {
			return Optional.absent();
		}
		return Optional.of(scheduledRun);
	}

	@Override
	public Optional<ScheduledRun> startScheduledRun(final long userGroupId) {
		final Optional<ScheduledRun> scheduledRun = getNextScheduledRun(userGroupId);
		if (!scheduledRun.isPresent()) {
			return scheduledRun;
		}

		startScheduledRun(userGroupId, scheduledRun.get());
		return scheduledRun;
	}

	private void startScheduledRun(final long userGroupId, final ScheduledRun scheduledRun) {
		scheduledRun.setStartedOn(DateUtilities.getCalendarNow());
		scheduledRunService.saveOrUpdate(scheduledRun);

		final UserGroup userGroup = userGroupService.findGroupById(userGroupId);
		scheduleNextRun(scheduledRun, userGroup);
	}

	private void scheduleNextRun(final ScheduledRun scheduledRun, final UserGroup userGroup) {
		final Calendar nextRun = getNextRun(scheduledRun);
		final ScheduledRun nextRunToSchedule = new ScheduledRun();
		nextRunToSchedule.setNextRun(nextRun);
		nextRunToSchedule.setInterval(scheduledRun.getInterval());
		scheduledRunService.saveOrUpdate(nextRunToSchedule);

		final UserGroupEvaluationScheduledRun evaluationScheduledRun =
			new UserGroupEvaluationScheduledRun(userGroup, nextRunToSchedule);

		userGroupEvaluationScheduledRunDAO.saveOrUpdate(evaluationScheduledRun);
	}

	private Calendar getNextRun(final ScheduledRun scheduledRun) {
		return new DateTime().withTimeAtStartOfDay()
			.plusDays(scheduledRun.getInterval())
			.withTimeAtStartOfDay()
			.toCalendar(Locale.ENGLISH);
	}

	@Override
	public void turnOnAutomaticEvaluation(long userGroupId, int validateDaysInterval) {
		Optional<ScheduledRun> nextScheduledRun = getNextFutureScheduledRunForActiveOrInactiveGroup(userGroupId);
		if (nextScheduledRun.isPresent()) {
			return;
		}

		UserGroup userGroup = userGroupService.findGroupById(userGroupId);
		if (userGroup == null) {
			return;
		}

		ScheduledRun scheduledRun = new ScheduledRun();
		scheduledRun.setNextRun(DateUtilities.getCalendarNow());
		scheduledRun.setInterval(validateDaysInterval);
		scheduledRunService.saveOrUpdate(scheduledRun);

		UserGroupEvaluationScheduledRun evaluationScheduledRun =
			new UserGroupEvaluationScheduledRun(userGroup, scheduledRun);

		userGroupEvaluationScheduledRunDAO.saveOrUpdate(evaluationScheduledRun);
	}

	@Override
	public void turnOffAutomaticEvaluation(long userGroupId) {
		Optional<ScheduledRun> nextScheduledRun = getNextFutureScheduledRunForActiveOrInactiveGroup(userGroupId);
		if (!nextScheduledRun.isPresent()) {
			return;
		}

		ScheduledRun scheduledRun = nextScheduledRun.get();
		scheduledRun.setDeleted(true);
		scheduledRunService.saveOrUpdate(scheduledRun);
	}
}
