package com.workmarket.dao.report;

import com.workmarket.dao.DAOInterface;
import com.workmarket.domains.model.directory.Email;
import com.workmarket.domains.model.reporting.ReportRecurrence;
import org.joda.time.DateTime;

import java.util.List;
import java.util.Set;

/**
 * Created by nick on 8/8/12 11:42 AM
 */
public interface ReportRecurrenceDAO extends DAOInterface<ReportRecurrence> {

	ReportRecurrence findByReportKey(long reportKey);

	void deleteAllRecipients(long reportKey);

	void saveRecurrence(ReportRecurrence recurrence);

	List<Long> findReportIdsByRecurringDateTime(DateTime date);

	Set<Email> findRecurringReportRecipientsByReportId(Long reportId);

	List<ReportRecurrence> findReportRecurrencesByCompanyId(long companyId);
}
