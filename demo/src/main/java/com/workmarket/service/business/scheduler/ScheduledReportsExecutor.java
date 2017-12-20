package com.workmarket.service.business.scheduler;

import com.workmarket.domains.model.directory.Email;
import com.workmarket.domains.model.reporting.ReportRequestData;
import com.workmarket.domains.model.reporting.ReportingCriteria;
import com.workmarket.service.business.CustomReportService;
import com.workmarket.service.business.event.reporting.WorkReportGenerateEvent;
import com.workmarket.service.infra.event.EventRouter;
import com.workmarket.thrift.work.display.WorkDisplay;
import com.workmarket.utility.CollectionUtilities;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.joda.time.DateTime;
import org.joda.time.DateTimeFieldType;
import org.joda.time.DateTimeZone;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jmx.export.annotation.ManagedOperation;
import org.springframework.jmx.export.annotation.ManagedResource;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

/**
 * Created by nick on 8/14/12 12:09 PM
 */
@Service
@ManagedResource(objectName="bean:name=scheduledReports", description="Run scheduled reports")
public class ScheduledReportsExecutor implements ScheduledExecutor {

	@Autowired private EventRouter eventRouter;
	@Autowired private WorkDisplay.Iface workDisplayHandler;
	@Autowired private CustomReportService customReportService;

	private static final Log logger = LogFactory.getLog(ScheduledReportsExecutor.class);

	@Value("${reporting.csv.file.location}")
	String fileLocation;

	@SuppressWarnings("unchecked")
	@ManagedOperation(description = "Scheduled reports")
	@Override public void execute() {
		DateTime now = new DateTime(DateTimeZone.UTC);
		Integer hour = now.get(DateTimeFieldType.hourOfDay());

		logger.info(String.format("******  Running ScheduledReportsExecutor for time slot %d (UTC)", hour));

		List<ReportingCriteria> scheduledReports = workDisplayHandler.findRecurringReportsByDateTime(now);

		if (CollectionUtils.isNotEmpty(scheduledReports)) {
			logger.info(String.format("[reports] Processing %d scheduled reports for time slot %d (UTC)",
					scheduledReports.size(), hour));

			for (ReportingCriteria report : scheduledReports) {
				try {

					ReportRequestData request = customReportService.getReportRequestData(report);

					Set<Email> recipientEmails = workDisplayHandler.findRecurringReportRecipientsByReportId(report.getId());

					logger.info(String.format("[reports] Generating and sending report %d (%s) to %d recipients",
							report.getId(), report.getReportName(), recipientEmails.size()));

					WorkReportGenerateEvent workReportGenerateEvent = new WorkReportGenerateEvent(request,
							fileLocation, CollectionUtilities.newSetPropertyProjection(recipientEmails, "email"),
							report.getId());
					eventRouter.sendEvent(workReportGenerateEvent);

					Thread.sleep(10000L); // protect against overlapping report generation

				} catch (Exception e) {
					logger.error(String.format("[reports] Unable to build entity request for scheduled report %d", report.getId()), e);
				}
			}
		} else {
			logger.info(String.format("No reports found for time slot %d (UTC)", hour));
		}
	}
}
