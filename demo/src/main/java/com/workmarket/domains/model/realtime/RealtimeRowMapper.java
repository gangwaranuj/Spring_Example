package com.workmarket.domains.model.realtime;

import com.workmarket.dao.realtime.RealtimeSQLFactoryImpl.SORTS;
import com.workmarket.thrift.core.TimeRange;
import com.workmarket.thrift.services.realtime.RealtimeCompany;
import com.workmarket.thrift.services.realtime.RealtimeProjectData;
import com.workmarket.thrift.services.realtime.RealtimeRowFact;
import com.workmarket.thrift.services.realtime.RealtimeUser;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;


public class RealtimeRowMapper implements RowMapper<IRealtimeRow> {

	@Override
	public IRealtimeRow mapRow(ResultSet rs, int rowNum) throws SQLException {
		RealtimeRowDecorator row = new RealtimeRowDecorator();
		row.setWorkNumber(rs.getString(SORTS.WORK_NUMBER.getColumn()));
		row.setOrderCreationDate(rs.getTimestamp(SORTS.CREATED_ON.getColumn()).getTime());
		Timestamp sentOn = rs.getTimestamp(SORTS.ORDER_AGE.getColumn());
		if (sentOn != null) {
			row.setOrderSentOn(sentOn.getTime());
		}

		row.setTimeZoneId(rs.getString("timeZoneId"));
		row.setModifiedOn(rs.getTimestamp(SORTS.MODIFIED_TIME.getColumn()).getTime());
		row.setModifierFirstName(rs.getString("modifier.first_name"));
		row.setModifierLastName(rs.getString("modifier.last_name"));

		TimeRange schedule = extractAssignmentTimeRange(rs);
		row.setAssignmentTimeRange(schedule);
		row.setDetailsText(rs.getString(SORTS.DETAILS.getColumn()));
		Double spendLimit = rs.getDouble(SORTS.SPEND_LIMIT.getColumn());
		if (spendLimit != null && spendLimit > 0) {
			row.setSpendLimit(spendLimit);
		}
		row.setQuestionCount(rs.getInt(SORTS.QUESTIONS.getColumn()));
		row.setOffers(rs.getInt(SORTS.OFFERS.getColumn()));
		row.setDeclines(rs.getInt(SORTS.DECLINES.getColumn()));
		row.setWorkId(rs.getLong("w.id"));
		RealtimeCompany company = new RealtimeCompany();
		company.setCompanyId(rs.getLong("company_id"));
		company.setCompanyName(rs.getString("company_name"));
		row.setLatitude(rs.getDouble("a.latitude"));
		row.setLongitude(rs.getDouble("a.longitude"));

		row.setCompany(company);
		RealtimeUser owner = new RealtimeUser();
		owner.setUserId(rs.getLong("creator_id"));
		owner.setFirstName(rs.getString("creator.first_name"));
		owner.setLastName(rs.getString("creator.last_name"));
		owner.setUserNumber("creator.user_number");
		row.setOwner(owner);
		Timestamp dueTime = rs.getTimestamp("due_time");
		if (dueTime != null && !rs.wasNull()) {
			row.setDueOn(dueTime.getTime());
		}
		Long projectId = rs.getLong("project_id");
		if (projectId != null && !rs.wasNull()) {
			RealtimeProjectData projectData = new RealtimeProjectData();
			projectData.setId(projectId);
			projectData.setName(rs.getString("project_name"));
			row.setProjectData(projectData);
		}
		row.setPercentWithOffers(rs.getInt("percent_of_open_offers"));
		row.setPercentWithRejections(rs.getInt("percent_of_rejections"));
		row.setNumberOfUnansweredQuestions(rs.getInt("number_of_unanswered_questions"));
		row.setPercentResourcesWhoViewedAssignment(rs.getInt("percent_resources_who_viewed_assignment"));
		row.setGroupSent(rs.getBoolean("isSentToGroup"));

		Long workingOnItId = rs.getLong("working_on_it_id");
		if (!rs.wasNull()) {
			RealtimeUser user = new RealtimeUser();
			user.setUserId(workingOnItId);
			user.setFirstName(rs.getString("working_on_it_first_name"));
			user.setLastName(rs.getString("working_on_it_last_name"));
			user.setUserNumber(rs.getString("working_on_it_user_number"));
			row.setUserWorkingOnIt(user);
		}

		return row;
	}

	private TimeRange extractAssignmentTimeRange(ResultSet rs) throws SQLException {
		Timestamp scheduleFrom = rs.getTimestamp("schedule_from");
		Timestamp scheduleThrough = rs.getTimestamp("schedule_through");
		TimeRange schedule = new TimeRange();
		if (scheduleFrom != null) {
			schedule.setFrom(scheduleFrom.getTime());
		}
		if (scheduleThrough != null) {
			schedule.setTo(scheduleThrough.getTime());
		}
		return schedule;
	}

}