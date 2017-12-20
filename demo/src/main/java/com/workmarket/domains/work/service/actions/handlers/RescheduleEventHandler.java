package com.workmarket.domains.work.service.actions.handlers;

import com.google.api.client.repackaged.com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Optional;
import com.workmarket.domains.model.DateRange;
import com.workmarket.domains.work.model.Work;
import com.workmarket.domains.work.service.WorkService;
import com.workmarket.domains.work.service.actions.AbstractWorkEvent;
import com.workmarket.domains.work.service.actions.RescheduleEvent;
import com.workmarket.service.thrift.transactional.TWorkService;
import com.workmarket.utility.DateUtilities;
import com.workmarket.web.helpers.AjaxResponseBuilder;
import com.workmarket.web.helpers.MessageBundleHelper;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.util.StopWatch;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;


@Service
public class RescheduleEventHandler implements WorkEventHandler {

	private static final Logger logger = LoggerFactory.getLogger(RescheduleEventHandler.class);

	@Autowired MessageBundleHelper messageBundleHelper;
	@Autowired TWorkService thriftWorkService;
	@Autowired WorkService workService;

	@Override
	public AjaxResponseBuilder handleEvent(AbstractWorkEvent event) {
		StopWatch stopWatch = new StopWatch();
		stopWatch.start();

		Assert.notNull(event);
		Assert.isTrue(event instanceof RescheduleEvent);
		@SuppressWarnings("ConstantConditions") RescheduleEvent rescheduleEvent = (RescheduleEvent) event;
		List<Work> works = rescheduleEvent.getWorks();

		if (!event.isValid()) {
			messageBundleHelper.addMessage(event.getResponse().setSuccessful(false), event.getMessageKey() + ".empty");
			return event.getResponse();
		}
		for (Work work : works) {
			try {
				final DateRange dateRange = getDateRange(rescheduleEvent, work);

				if (work.isVoidable()) {
					workService.updateWorkSchedule(work, dateRange, rescheduleEvent.getNote());
				} else {
					workService.setAppointmentTime(work.getId(), dateRange, rescheduleEvent.getNote());
				}
			} catch (Exception e) {
				logger.error(String.format("[mass_reschedule_feature] worknumber: %s produced exception \n", work.getWorkNumber()), e);
			}
		}
		messageBundleHelper.addMessage(event.getResponse().setSuccessful(true), event.getMessageKey() + ".success");
		stopWatch.stop();

		long time = stopWatch.getTotalTimeMillis();
		logger.info(String.format("[mass_reschedule_feature] rescheduled %d assignments in %d ms", CollectionUtils.size(works), time));

		return event.getResponse();
	}

	private DateRange getDateRange(final RescheduleEvent rescheduleEvent, final Work work) {
		final TimeZone timeZone = TimeZone.getTimeZone(work.getTimeZone().getTimeZoneId());
		final Calendar start = parseDateTime(rescheduleEvent.getStartDateTime(), rescheduleEvent.getStartDateTimeFormat(), timeZone);
		final Calendar end = parseDateTime(rescheduleEvent.getEndDateTime(), rescheduleEvent.getEndDateTimeFormat(), timeZone);
		return new DateRange(start, end);
	}

	@VisibleForTesting
	protected Calendar parseDateTime(final Optional<String> dateTime, final Optional<String> format, final TimeZone timeZone) {
		if (!dateTime.isPresent() || !format.isPresent()) {
			return null;
		}

		final SimpleDateFormat startDateFormat = new SimpleDateFormat(format.get());
		startDateFormat.setTimeZone(timeZone);

		try {
			final Date startDate = startDateFormat.parse(dateTime.get());
			return DateUtilities.getCalendarFromDate(startDate);
		} catch (ParseException e) {
			return null;
		}
	}
}
