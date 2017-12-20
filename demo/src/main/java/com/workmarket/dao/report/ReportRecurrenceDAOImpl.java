package com.workmarket.dao.report;

import com.google.common.collect.Lists;
import com.workmarket.dao.AbstractDAO;
import com.workmarket.domains.model.directory.Email;
import com.workmarket.domains.model.reporting.ReportRecurrence;
import com.workmarket.utility.DateUtilities;
import com.workmarket.utility.NumberUtilities;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Criteria;
import org.hibernate.FetchMode;
import org.hibernate.Hibernate;
import org.hibernate.Query;
import org.hibernate.criterion.Restrictions;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by nick on 8/8/12 11:42 AM
 */
@Repository
public class ReportRecurrenceDAOImpl extends AbstractDAO<ReportRecurrence> implements ReportRecurrenceDAO {

	private static final Log logger = LogFactory.getLog(ReportRecurrenceDAOImpl.class);

	@Qualifier("jdbcTemplate") @Autowired private NamedParameterJdbcTemplate jdbcTemplate;

	@Override
	public ReportRecurrence findByReportKey(long reportKey) {
		Query query = getFactory().getCurrentSession().getNamedQuery("reportRecurrence.byReportKey");
		query.setLong("reportKey", reportKey);

		ReportRecurrence recurrence = (ReportRecurrence) query.uniqueResult();

		if (recurrence != null) {
			Hibernate.initialize(recurrence.getRecipients());
		}

		return recurrence;
	}

	@Override public void deleteAllRecipients(long recurrenceId) {
		// need to use native SQL as Hibernate does not allow HQL access to @JoinTable entities
		String deleteQuery = String.format("DELETE FROM report_recurrence_email WHERE report_recurrence_id = %d", recurrenceId);
		getFactory().getCurrentSession().createSQLQuery(deleteQuery).executeUpdate();
	}

	@Override public void saveRecurrence(ReportRecurrence recurrence) {
		if (recurrence.getId() != null)
			deleteAllRecipients(recurrence.getId());
		saveOrUpdate(recurrence);
	}

	/**
	 * Selects recurrences that meet the following criteria
	 * - for TODAY with time T (in UTC), and a recurrence R
	 * - R.report_hour = T.hour
	 * - all daily reports having
	 * - R.daily_weekdays_only_flag == false, if T is a weekend
	 * - all weekly reports having
	 * - T.weekday in R.weekly_days
	 * - all monthly reports having
	 * - R.monthly_frequency_day == T.day
	 * - all monthly reports having
	 * - monthly_frequency_weekday == T.weekday AND monthly_frequency_weekday_ordinal == getWeekdayOrdinal(T))
	 * where "getWeekdayOrdinal" returns n for the "nth T.weekday of the month" for T
	 *
	 * @param date
	 * @return
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<Long> findReportIdsByRecurringDateTime(DateTime date) {

		List<Map<String, Object>> reports = jdbcTemplate.queryForList(buildScheduledJobQuery(date), new HashMap<String, Object>());
		List<Long> results = Lists.newArrayList();

		for (Map<String, Object> mapParams : reports) {
			String tz = (String) mapParams.get("time_zone_id");
			DateTime userDate = new DateTime(date, DateTimeZone.forID(tz));

			/**
			 * In production, date logic is in UTC, so recurrence criteria can become invalid during conversion from a user's timezone.
			 * 		e.g. a report for Tuesdays sent at 10 PM EST will be stored as 3 AM UTC (+5), which puts it in a different "day",
			 * 		thus it should not be sent on Tuesday but rather on Wednesday UTC.
			 * So we need to filter that stuff here rather than in the query itself.
			 */

			String recurrenceType = (String) mapParams.get("recurrence_type");

			if (ReportRecurrence.DAILY.equals(recurrenceType)) {

				if ((Boolean) mapParams.get("daily_weekdays_only_flag") && DateUtilities.isWeekend(userDate))
					continue;

			} else if (ReportRecurrence.WEEKLY.equals(recurrenceType)) {

				// logical AND weekly_days with bit mask representing current day of week (monday = 1, sunday = 64)
				if (((Integer) mapParams.get("weekly_days") & NumberUtilities.ipow(2, userDate.getDayOfWeek() - 1)) == 0)
					continue;

			} else if (ReportRecurrence.MONTHLY.equals(recurrenceType)) {

				if (BooleanUtils.toBoolean((Integer) mapParams.get("monthly_use_day_of_month_flag"))) {
					if ((Integer) mapParams.get("monthly_frequency_day") != userDate.getDayOfMonth())
						continue;

				} else {
					if ((Integer) mapParams.get("monthly_frequency_weekday") != userDate.getDayOfWeek()
							|| (Integer) mapParams.get("monthly_frequency_weekday_ordinal") != DateUtilities.getWeekdayOrdinalByMonth(userDate))
						continue;
				}
			}
			results.add((long) (Integer) mapParams.get("report_id"));
		}
		logger.info(String.format("%d of %d reports met recurrence criteria", results.size(), reports.size()));
		return results;
	}

	@Override
	public Set<Email> findRecurringReportRecipientsByReportId(Long reportId) {
		Criteria criteria = getFactory().getCurrentSession().createCriteria(getEntityClass());
		criteria.setFetchMode("recipients", FetchMode.JOIN);
		criteria.add(Restrictions.eq("reportKey", reportId));
		ReportRecurrence recurrence = (ReportRecurrence) criteria.uniqueResult();
		return (recurrence == null) ? new HashSet<Email>() : recurrence.getRecipients();
	}

	@Override
	@SuppressWarnings("unchecked")
	public List<ReportRecurrence> findReportRecurrencesByCompanyId(long companyId) {
		Criteria criteria = getFactory().getCurrentSession().createCriteria(getEntityClass());
		criteria.setFetchMode("recipients", FetchMode.JOIN);
		criteria.add(Restrictions.eq("companyId", companyId));
		return (List<ReportRecurrence>) criteria.list();
	}

	@Override protected Class<?> getEntityClass() {
		return ReportRecurrence.class;
	}

	public void setJdbcTemplate(NamedParameterJdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}

	private String buildScheduledJobQuery(DateTime date) {

		StringBuilder sql = new StringBuilder();
		sql.append(String.format(
				"SELECT rr.*, tz.time_zone_id " +
						"FROM report_recurrence rr " +
						"INNER JOIN time_zone tz on rr.time_zone_id = tz.id " +
						"WHERE rr.enabled = 1 and rr.deleted = 0 AND report_hour = '%s'", date.getHourOfDay()));

		return sql.toString();
	}

}
