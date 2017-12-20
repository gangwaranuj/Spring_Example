package com.workmarket.domains.compliance.service;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.workmarket.domains.compliance.model.AssignmentCountComplianceRule;
import com.workmarket.domains.compliance.model.PeriodicComplianceRule;
import com.workmarket.domains.compliance.model.WorkBundleComplianceCriterion;
import com.workmarket.domains.compliance.model.WorkComplianceCriterion;
import com.workmarket.domains.model.Company;
import com.workmarket.domains.model.DateRange;
import com.workmarket.domains.model.User;
import com.workmarket.domains.model.WorkStatusType;
import com.workmarket.domains.work.model.AbstractWork;
import com.workmarket.domains.work.model.Work;
import com.workmarket.domains.work.model.WorkBundle;
import com.workmarket.domains.work.service.WorkBundleService;
import com.workmarket.domains.work.service.WorkService;
import com.workmarket.utility.DateUtilities;
import com.workmarket.utility.StringUtilities;
import com.workmarket.web.helpers.MessageBundleHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.TimeZone;

@Component
public class CompliantVisitorImpl implements CompliantVisitor {
	@Autowired private WorkService workService;
	@Autowired private WorkBundleService workBundleService;
	@Autowired private MessageBundleHelper messageHelper;

	private boolean isWorkInPeriod(DateRange workRange, DateRange period) {
		if (!workRange.isRange()) {
			return period.contains(workRange);
		}
		return period.contains(workRange.getFrom()) || period.contains(workRange.getThrough()) || workRange.contains(period.getFrom());
	}

	@Override
	public void visit(WorkBundleComplianceCriterion complianceCriterion, AssignmentCountComplianceRule assignmentCountComplianceRule) {
		WorkBundle bundle = workBundleService.findById(complianceCriterion.getWork().getId(), true);
		Long maximum = assignmentCountComplianceRule.getMaxAssignments();
		User user = complianceCriterion.getUser();
		Company company = bundle.getCompany();
		PeriodicComplianceRule.PeriodType type = assignmentCountComplianceRule.getPeriodType();

		complianceCriterion.setName(type.getColumn());
		complianceCriterion.setTypeName(assignmentCountComplianceRule.getHumanTypeName());

		Set<Work> bundledWork = Sets.newHashSet(bundle.getBundle());
		Set<DateRange> evaluatedRanges = Sets.newHashSet();
		Iterator<Work> iterator = bundledWork.iterator();
		while (!bundledWork.isEmpty()) {
			Work work = iterator.next();
			iterator.remove();
			Calendar fromDate = work.getScheduleFrom();
			Calendar throughDate = work.getSchedule().isRange() ? work.getScheduleThrough() : work.getScheduleFrom();
			TimeZone tz = fromDate.getTimeZone();

			// null dateRange means a Lifetime check
			DateRange dateRange = new DateRange(null, null);

			for (Calendar workDate = fromDate; workDate.compareTo(throughDate) <= 0; workDate = DateUtilities.getMidnightNextDayRelativeToTimezone(dateRange.getThrough(), tz)) {
				switch (type) {
					case WEEK:
						dateRange.setFrom(DateUtilities.getMidnightRelativeToTimezone(DateUtilities.getCalendarWithFirstDayOfWeek(workDate, tz), tz));
						dateRange.setThrough(DateUtilities.getCalendarWithLastMinuteOfDay(DateUtilities.getCalendarWithLastDayOfWeek(dateRange.getFrom(), tz), tz));
						break;
					case MONTH:
						dateRange.setFrom(DateUtilities.getMidnightRelativeToTimezone(DateUtilities.getCalendarWithFirstDayOfTheMonth(workDate, tz), tz));
						dateRange.setThrough(DateUtilities.getCalendarWithLastMinuteOfDay(DateUtilities.getCalendarWithLastDayOfTheMonth(dateRange.getFrom(), tz), tz));
						break;
					case QUARTER:
						dateRange.setFrom(DateUtilities.getMidnightRelativeToTimezone(DateUtilities.getCalendarWithBeginningOfQuarter(workDate, tz), tz));
						dateRange.setThrough(DateUtilities.getCalendarWithLastMinuteOfDay(DateUtilities.getCalendarWithLastDayOfQuarter(dateRange.getFrom(), tz), tz));
						break;
					case YEAR:
						dateRange.setFrom(DateUtilities.getMidnightRelativeToTimezone(DateUtilities.getCalendarWithFirstDayOfYear(workDate, tz), tz));
						dateRange.setThrough(DateUtilities.getCalendarWithLastMinuteOfDay(DateUtilities.getCalendarWithLastDayOfYear(dateRange.getFrom(), tz), tz));
						break;
					case LIFETIME:
					default:
				}

				// only process this range if it has not already been processed
				if (!evaluatedRanges.add(new DateRange(dateRange))) {
					continue;
				}

				// count assignments in bundle in this date range and add to exclude list
				List<Long> excludeIds = Lists.newArrayList(work.getId());
				int bundledCount = 1;
				Iterator<Work> innerIterator = bundledWork.iterator();
				while (innerIterator.hasNext()) {
					Work innerWork = innerIterator.next();
					if (dateRange.getFrom() == null || isWorkInPeriod(innerWork.getSchedule(), dateRange)) {
						bundledCount++;
						excludeIds.add(innerWork.getId());
					}
				}

				// statuses to count for compliance
				List<String> statuses = ImmutableList.of(WorkStatusType.ACTIVE, WorkStatusType.CANCELLED_PAYMENT_PENDING,
						WorkStatusType.CANCELLED_WITH_PAY, WorkStatusType.PAID, WorkStatusType.PAYMENT_PENDING, WorkStatusType.COMPLETE);

				int count = workService.countWorkByCompanyUserRangeAndStatus(company.getId(), user.getId(), excludeIds, dateRange.getFrom(), dateRange.getThrough(), statuses);

				boolean met = count + bundledCount <= maximum;

				if (complianceCriterion.isMet()) {
					complianceCriterion.setMet(met);
				}

				if (!met) {
					boolean exceeded = count < maximum && count + bundledCount > maximum;
					Integer exceededBy = (int)(count + bundledCount - maximum);
					String msgTail = exceeded ? String.format(" by %d %s.", exceededBy, StringUtilities.pluralize("assignment", exceededBy)) : ".";
					String msgVerb = exceeded ? "would exceed" : "has met";

					if (type == PeriodicComplianceRule.PeriodType.LIFETIME) {
						complianceCriterion.addMessage(messageHelper.getMessage("assignment.compliance.periodic.lifetime_count_exceeded",
							user.getFullName(), msgVerb, maximum, StringUtilities.pluralize("assignment", maximum.intValue()), msgTail));
					} else {
						SimpleDateFormat dateFormat = new SimpleDateFormat("M/d/yyyy");
						complianceCriterion.addMessage(messageHelper.getMessage("assignment.compliance.periodic.count_exceeded",
							user.getFullName(), msgVerb, maximum, StringUtilities.pluralize("assignment", maximum.intValue()), type.getColumn(),
							dateFormat.format(dateRange.getFrom().getTime()), dateFormat.format(dateRange.getThrough().getTime()), msgTail));
					}
				}
				if (type == PeriodicComplianceRule.PeriodType.LIFETIME) {
					return;
				}
			}
		}
	}

	@Override
	public void visit(WorkComplianceCriterion complianceCriterion, AssignmentCountComplianceRule assignmentCountComplianceRule) {
		AbstractWork work = complianceCriterion.getWork();

		Long maximum = assignmentCountComplianceRule.getMaxAssignments();
		User user = complianceCriterion.getUser();
		Calendar fromDate = complianceCriterion.getSchedule().getFrom();
		Calendar throughDate = complianceCriterion.getSchedule().getThrough() != null ? complianceCriterion.getSchedule().getThrough() : complianceCriterion.getSchedule().getFrom();
		TimeZone tz = fromDate.getTimeZone();
		Company company = work.getCompany();
		PeriodicComplianceRule.PeriodType type = assignmentCountComplianceRule.getPeriodType();

		complianceCriterion.setName(type.getColumn());
		complianceCriterion.setTypeName(assignmentCountComplianceRule.getHumanTypeName());

		// null dateRange means a Lifetime check
		DateRange dateRange = new DateRange(null, null);
		boolean met = false;

		for (Calendar workDate = fromDate; workDate.compareTo(throughDate) <= 0; workDate = DateUtilities.getMidnightNextDayRelativeToTimezone(dateRange.getThrough(), tz)) {
			switch (type) {
				case WEEK:
					dateRange.setFrom(DateUtilities.getMidnightRelativeToTimezone(DateUtilities.getCalendarWithFirstDayOfWeek(workDate, tz), tz));
					dateRange.setThrough(DateUtilities.getCalendarWithLastMinuteOfDay(DateUtilities.getCalendarWithLastDayOfWeek(dateRange.getFrom(), tz), tz));
					break;
				case MONTH:
					dateRange.setFrom(DateUtilities.getMidnightRelativeToTimezone(DateUtilities.getCalendarWithFirstDayOfTheMonth(workDate, tz), tz));
					dateRange.setThrough(DateUtilities.getCalendarWithLastMinuteOfDay(DateUtilities.getCalendarWithLastDayOfTheMonth(dateRange.getFrom(), tz), tz));
					break;
				case QUARTER:
					dateRange.setFrom(DateUtilities.getMidnightRelativeToTimezone(DateUtilities.getCalendarWithBeginningOfQuarter(workDate, tz), tz));
					dateRange.setThrough(DateUtilities.getCalendarWithLastMinuteOfDay(DateUtilities.getCalendarWithLastDayOfQuarter(dateRange.getFrom(), tz), tz));
					break;
				case YEAR:
					dateRange.setFrom(DateUtilities.getMidnightRelativeToTimezone(DateUtilities.getCalendarWithFirstDayOfYear(workDate, tz), tz));
					dateRange.setThrough(DateUtilities.getCalendarWithLastMinuteOfDay(DateUtilities.getCalendarWithLastDayOfYear(dateRange.getFrom(), tz), tz));
					break;
				case LIFETIME:
				default:
			}

			// statuses to count for compliance
			List<String> statuses = ImmutableList.of(WorkStatusType.ACTIVE, WorkStatusType.CANCELLED_PAYMENT_PENDING,
					WorkStatusType.CANCELLED_WITH_PAY, WorkStatusType.PAID, WorkStatusType.PAYMENT_PENDING, WorkStatusType.COMPLETE);

			int count = workService.countWorkByCompanyUserRangeAndStatus(company.getId(), user.getId(), Lists.newArrayList(work.getId()), dateRange.getFrom(), dateRange.getThrough(), statuses);

			met = count + 1 <= maximum;

			if (complianceCriterion.isMet()) {
				complianceCriterion.setMet(met);
			}

			if (!met) {
				if (type == PeriodicComplianceRule.PeriodType.LIFETIME) {
					complianceCriterion.addMessage(messageHelper.getMessage("assignment.compliance.periodic.lifetime_count_exceeded",
						user.getFullName(), "has met", maximum, StringUtilities.pluralize("assignment", maximum.intValue()), "."));
				} else {
					SimpleDateFormat dateFormat = new SimpleDateFormat("M/d/yyyy");
					complianceCriterion.addMessage(messageHelper.getMessage("assignment.compliance.periodic.count_exceeded",
						user.getFullName(), "has met", maximum, StringUtilities.pluralize("assignment", maximum.intValue()), type.getColumn(),
						dateFormat.format(dateRange.getFrom().getTime()), dateFormat.format(dateRange.getThrough().getTime()), "."));
				}
			}
			if (type == PeriodicComplianceRule.PeriodType.LIFETIME) {
				return;
			}
		}
	}
}