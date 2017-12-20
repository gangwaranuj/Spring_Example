package com.workmarket.service.business.upload.parser;

import com.google.common.collect.Lists;
import com.workmarket.configuration.Constants;
import com.workmarket.domains.work.service.upload.WorkUploadColumn;
import com.workmarket.service.business.ProfileService;
import com.workmarket.thrift.work.Schedule;
import com.workmarket.thrift.work.Work;
import com.workmarket.thrift.work.exception.WorkRowParseError;
import com.workmarket.thrift.work.exception.WorkRowParseErrorType;
import com.workmarket.utility.DateUtilities;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Calendar;
import java.util.List;
import java.util.Map;

import static org.springframework.util.CollectionUtils.isEmpty;

@Component
public class ScheduleParserImpl implements ScheduleParser {
	@Autowired ProfileService profileService;

	private void overrideFromTemplate(WorkUploaderBuildResponse response, WorkUploaderBuildData buildData) {
		Work work = response.getWork();
		Work templateWork = buildData.getWork();

		if (!work.isSetSchedule()) { return; }

		Map<String, String> types = buildData.getTypes();

		// set defaults from template, if any
		Schedule templateSchedule = work.getSchedule();
		// check to see what's overridden - upload file takes precedence
		if (WorkUploadColumn.isEmpty(types, WorkUploadColumn.START_TIME)) {
			String timeStr = DateUtilities.formatMillis("hh:mmaa", templateSchedule.getFrom(), templateWork.getTimeZone());
			types.put(WorkUploadColumn.START_TIME.getUploadColumnName(), timeStr);
		}

		if (WorkUploadColumn.isEmpty(types, WorkUploadColumn.START_DATE)) {
			String dateStr = DateUtilities.formatMillis("MM/dd/yyyy", templateSchedule.getFrom(), templateWork.getTimeZone());
			types.put(WorkUploadColumn.START_DATE.getUploadColumnName(), dateStr);
		}

		if (templateSchedule.isSetThrough() && WorkUploadColumn.isEmpty(types, WorkUploadColumn.END_TIME)) {
			String timeStr = DateUtilities.formatMillis("hh:mmaa", templateSchedule.getThrough(), templateWork.getTimeZone());
			types.put(WorkUploadColumn.END_TIME.getUploadColumnName(), timeStr);
		}

		if (templateSchedule.isSetThrough() && WorkUploadColumn.isEmpty(types, WorkUploadColumn.END_DATE)) {
			String dateStr = DateUtilities.formatMillis("MM/dd/yyyy", templateSchedule.getThrough(), templateWork.getTimeZone());
			types.put(WorkUploadColumn.END_DATE.getUploadColumnName(), dateStr);
		}
	}

	@Override
	public void build(WorkUploaderBuildResponse response, WorkUploaderBuildData buildData) {

		Map<String,String> types = buildData.getTypes();

		// Use the timezone already parsed on the assignment;
		// If none exists use the WM default.
		String defaultTimeZone = response.getWork().getTimeZone();
		if (StringUtils.isEmpty(defaultTimeZone)) {
			if (response.getWork().getBuyer() != null) {
				defaultTimeZone = profileService.getTimeZoneByUserId(response.getWork().getBuyer().getId()).getTimeZoneId();
			} else {
				defaultTimeZone = Constants.DEFAULT_TIMEZONE;
			}
			response.getWork().setTimeZone(defaultTimeZone);
		}

		if (!WorkUploadColumn.containsAny(types,
				WorkUploadColumn.START_DATE_TIME, WorkUploadColumn.START_DATE, WorkUploadColumn.START_TIME,
				WorkUploadColumn.END_DATE_TIME, WorkUploadColumn.END_DATE, WorkUploadColumn.END_TIME)) {

			return;
		}

		List<WorkRowParseError> errors = Lists.newLinkedList();

		if (WorkUploadColumn.containsAll(types, WorkUploadColumn.START_DATE_TIME, WorkUploadColumn.START_DATE, WorkUploadColumn.START_TIME)) {
			errors.add(ParseUtils.createErrorRow(types.get(WorkUploadColumn.START_DATE_TIME),
					"Multiple columns mapped for Start Date and Time! Select either \"Start Date\" and \"Start Time\", " +
					"or \"Start Date and Time\" in order to proceed.", WorkRowParseErrorType.MULTIPLE_STRATEGIES_INFERRED, WorkUploadColumn.START_DATE_TIME));
		}

		if (WorkUploadColumn.containsAll(types, WorkUploadColumn.END_DATE_TIME, WorkUploadColumn.END_DATE, WorkUploadColumn.END_TIME)) {
			errors.add(ParseUtils.createErrorRow(types.get(WorkUploadColumn.END_DATE_TIME),
					"Multiple columns mapped for End Date and Time! Select either \"End Date\" and \"End Time\", " +
					"or \"End Date and Time\" in order to proceed.", WorkRowParseErrorType.MULTIPLE_STRATEGIES_INFERRED, WorkUploadColumn.END_DATE_TIME));
		}

		if (!isEmpty(errors)) {
			response.addToRowParseErrors(errors);
			return;
		}

		overrideFromTemplate(response, buildData);

		Schedule s = new Schedule();
		if (WorkUploadColumn.containsAll(types, WorkUploadColumn.START_DATE_TIME)) {
			String startDateTimeString = WorkUploadColumn.get(types, WorkUploadColumn.START_DATE_TIME);
			Calendar c = DateUtilities.getCalendarFromDateTimeString(startDateTimeString, defaultTimeZone);
			if (c != null) {
				s.setFrom(c.getTimeInMillis());
			} else {
				WorkUploadColumn column = WorkUploadColumn.START_DATE_TIME;
				errors.add(ParseUtils.createErrorRow(startDateTimeString,
						"Unrecognized Start Date and Time format: " + startDateTimeString,
						WorkRowParseErrorType.INVALID_DATA,
						column));
			}
		} else if (WorkUploadColumn.containsAny(types, WorkUploadColumn.START_DATE, WorkUploadColumn.START_TIME)) {
			WorkUploadColumn dateColumn = WorkUploadColumn.START_DATE;
			WorkUploadColumn timeColumn = WorkUploadColumn.START_TIME;
			Calendar c = parseDateAndTime(types, defaultTimeZone, errors, dateColumn, timeColumn);
			if (c != null) {
				s.setFrom(c.getTimeInMillis());
			}
		}

		if (WorkUploadColumn.containsAll(types, WorkUploadColumn.END_DATE_TIME)) {
			String endDateTimeString = WorkUploadColumn.get(types, WorkUploadColumn.END_DATE_TIME);
			Calendar c = DateUtilities.getCalendarFromDateTimeString(endDateTimeString, defaultTimeZone);
			if (c != null) {
				s.setThrough(c.getTimeInMillis());
			} else {
				WorkUploadColumn column = WorkUploadColumn.END_DATE_TIME;
				errors.add(ParseUtils.createErrorRow(endDateTimeString,
						"Unrecognized End Date and Time format: " + endDateTimeString,
						WorkRowParseErrorType.INVALID_DATA,
						column));
			}
		} else if (WorkUploadColumn.containsAny(types, WorkUploadColumn.END_DATE, WorkUploadColumn.END_TIME)) {
			WorkUploadColumn endDate = WorkUploadColumn.END_DATE;
			WorkUploadColumn endTime = WorkUploadColumn.END_TIME;
			Calendar c = parseDateAndTime(types, defaultTimeZone, errors, endDate, endTime);
			if (c != null) {
				s.setThrough(c.getTimeInMillis());
			}
		}

		if (!isEmpty(errors)) {
			response.addToRowParseErrors(errors);
			return;
		}

		s.setRange(s.isSetThrough());

		response.getWork().setSchedule(s);
	}

	private static Calendar parseDateAndTime(Map<String, String> types,
			String defaultTimeZone, List<WorkRowParseError> errors,
			WorkUploadColumn endDate, WorkUploadColumn endTime) {

		String endDateInput = WorkUploadColumn.get(types, endDate);
		String endTimeInput = WorkUploadColumn.get(types, endTime);
		if (endDateInput == null) {
			errors.add(ParseUtils.createErrorRow(endDateInput, "Date is required.", WorkRowParseErrorType.INVALID_DATA, endDate));
			return null;
		}
		if (endTimeInput == null) {
			errors.add(ParseUtils.createErrorRow(endTimeInput, "Time is required.", WorkRowParseErrorType.INVALID_DATA, endTime));
			return null;
		}
		Calendar c = DateUtilities.getCalendarFromDateTimeString(endDateInput, endTimeInput, defaultTimeZone);
		if (c == null) {
			errors.add(ParseUtils.createErrorRow(endDateInput, "Unrecognized date format: " + endDateInput, WorkRowParseErrorType.INVALID_DATA, endDate));
			return null;
		}
		if (!c.isSet(Calendar.HOUR)) {
			errors.add(ParseUtils.createErrorRow(endTimeInput, "Unrecognized time format: " + endTimeInput, WorkRowParseErrorType.INVALID_DATA, endTime));
			return null;
		}
		return c;
	}
}
