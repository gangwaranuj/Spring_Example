package com.workmarket.dao.realtime;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.workmarket.domains.model.realtime.*;
import com.workmarket.domains.model.realtime.TotalAssignmentCountMapper.AssignmentCount;
import com.workmarket.thrift.services.realtime.RealtimeDropDownOption;
import com.workmarket.thrift.services.realtime.RealtimeFilter;
import com.workmarket.thrift.services.realtime.RealtimeUser;
import com.workmarket.thrift.services.realtime.TotalAssignmentCount;
import com.workmarket.utility.DateUtilities;
import com.workmarket.utility.sql.SQLBuilder;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import static com.google.common.collect.Maps.newHashMapWithExpectedSize;

@Repository
public class RealtimeWorkReportDAOImpl implements RealtimeWorkReportDAO {

	private static final Log logger = LogFactory.getLog(RealtimeWorkReportDAOImpl.class);
	@Resource(name = "readOnlyJdbcTemplate")
	private NamedParameterJdbcTemplate jdbcTemplate;
	@Autowired private RealtimeSQLFactory sqlFactory;

	@Override
	public IRealtimeStatusPage generateRealtimeStatusPage(
			Long companyId,
			RealtimeReportType reportType,
			RealtimeFilter filters,
			RealtimeServicePagination pagination) {

		RealtimeStatusPageDecorator page = new RealtimeStatusPageDecorator();
		fillInNumberOfResults(companyId, reportType, filters, page, pagination);
		Collection<IRealtimeRow> rows = queryForRows(companyId, reportType, filters, pagination);

		Map<Long, IRealtimeRow> workPageMap = Maps.newHashMapWithExpectedSize(rows.size());
		for (IRealtimeRow row : rows) {
			page.addToRows(row);
			workPageMap.put(row.getWorkId(), row);
		}

		Collection<Long> workIds = findAllWorkIdsInResponse(rows);
		if (workIds.size() == 0) {
			return page;
		}
		RealtimeResourceMapper resourceMapper = new RealtimeResourceMapper();
		Map<String, Object> resourceParameters = newHashMapWithExpectedSize(3);
		resourceParameters.put("workIds", workIds);
		//We use this format of date to avoid the use of sysdate() or now() which invalidate the use of the query cache
		resourceParameters.put("today", DateUtilities.formatTodayForSQL());

		Collection<IRealtimeResource> results = jdbcTemplate.query(sqlFactory.getResourceSQL(), resourceParameters, resourceMapper);
		if (results == null || results.size() == 0) {
			logger.error("no realtime resources for assignment - is that possible to have in sent status? Company Id: " + companyId + " report Type: " + reportType);
			return page;
		}

		Collection<Long> realtimeResourcesCaptured = Lists.newArrayListWithCapacity(results.size());
		for (IRealtimeResource resource : results) {
			// get the row...
			IRealtimeRow row = workPageMap.get(resource.getWorkId());
			resource.setRelativeDistance(row.getLatitude(), row.getLongitude());
			row.addToInvitedResources(resource);
			realtimeResourcesCaptured.add(resource.getWorkResourceId());
		}
		final SQLBuilder rtSQLOwner = sqlFactory.getOwnerDropDownSQL(reportType, companyId);
		final Collection<IRealtimeUser> ownerDropDown = jdbcTemplate.query(rtSQLOwner.build(), rtSQLOwner.getParams(), new RealtimeUserMapper());
		if (ownerDropDown != null && ownerDropDown.size() > 0) {
			for (IRealtimeUser owner : ownerDropDown) {
				page.addToOwnerFilterOptions((RealtimeUser) owner);
			}
		}
		hydrateNumberOfQuestions(companyId, reportType, page);
		hydrateClientNames(companyId, reportType, page);
		hydrateProjectNames(companyId, reportType, page);
		return page;
	}

	@Override
	public TotalAssignmentCount calculateTotalOpenAssignments(long companyId, String timeZone) {
		SQLBuilder builder = sqlFactory.getAssignmentTotalCountSQL(companyId);
		SQLBuilder todayBuilder = sqlFactory.getAssignmentTodayTotalSQL(companyId, timeZone).addParam("companyId", companyId);
		SQLBuilder todaySentCount = sqlFactory.getAssignmentSentTodayTotalSQL(timeZone, companyId);
		return performAssignmentCount(builder, todayBuilder, todaySentCount);
	}

	@Override
	public TotalAssignmentCount calculateTotalOpenAssignments(String timeZone) {
		SQLBuilder builder = sqlFactory.getAssignmentTotalCountSQL();
		SQLBuilder todayBuilder = sqlFactory.getAssignmentTodayTotalSQL(timeZone);
		SQLBuilder todaySentCount = sqlFactory.getAssignmentSentTodayTotalSQL(timeZone, null);
		return performAssignmentCount(builder, todayBuilder, todaySentCount);
	}

	private void hydrateClientNames(Long companyId, RealtimeReportType reportType, RealtimeStatusPageDecorator page) {
		final SQLBuilder rtSQLClient = sqlFactory.getClientDropdownSQL(reportType, companyId);
		final Collection<RealtimeDropDownOption> clientDropdowns = jdbcTemplate.query(rtSQLClient.build(), rtSQLClient.getParams(), new RealtimeDropdownMapper());
		for (RealtimeDropDownOption clientDropdown : clientDropdowns) {
			page.addToClients(clientDropdown);
		}
	}

	private void hydrateProjectNames(Long companyId, RealtimeReportType reportType, RealtimeStatusPageDecorator page) {
		final SQLBuilder rtSQLProject = sqlFactory.getProjectDropdownSQL(reportType, companyId);
		final Collection<RealtimeDropDownOption> projectDropdowns = jdbcTemplate.query(rtSQLProject.build(), rtSQLProject.getParams(), new RealtimeDropdownMapper());
		for (RealtimeDropDownOption projectDropdown : projectDropdowns) {
			page.addToProjects(projectDropdown);
		}
	}

	private void hydrateNumberOfQuestions(Long companyId, RealtimeReportType type, RealtimeStatusPageDecorator page) {
		final SQLBuilder rtSQL = sqlFactory.getMaxUnansweredQuestionsSQL(companyId, type);
		final int maxNumberOfQuestions = jdbcTemplate.queryForObject(rtSQL.build(), rtSQL.getParams(), Integer.class);
		page.setMaxUnansweredQuestions(maxNumberOfQuestions);
	}

	private void fillInNumberOfResults(Long companyId, RealtimeReportType reportType, RealtimeFilter filters, IRealtimeStatusPage page, RealtimeServicePagination pagination) {
		final SQLBuilder rtSql = sqlFactory.getNumberOfResultsSql(companyId, reportType, filters, pagination);
		final int numOfResults = jdbcTemplate.queryForObject(rtSql.build(), rtSql.getParams(), Integer.class);
		page.setNumberOfResults(numOfResults);
	}

	private Collection<IRealtimeRow> queryForRows(
			Long companyId,
			RealtimeReportType reportType,
			RealtimeFilter filters,
			RealtimeServicePagination pagination)
			throws DataAccessException {

		RealtimeRowMapper assignmentMapper = new RealtimeRowMapper();
		SQLBuilder sql = sqlFactory.createRealtimeAssignmentSQL(companyId, reportType, filters, pagination);
		return this.jdbcTemplate.query(sql.build(), sql.getParams(), assignmentMapper);
	}

	private Collection<Long> findAllWorkIdsInResponse(Collection<IRealtimeRow> rows) {
		List<Long> workIds = Lists.newArrayListWithCapacity(rows.size());
		for (IRealtimeRow row : rows) {
			workIds.add(row.getWorkId());
		}
		return workIds;
	}

	private TotalAssignmentCount performAssignmentCount(
			SQLBuilder builder,
			SQLBuilder todayBuilder,
			SQLBuilder todaySentCount) {

		int totalInRealtime = jdbcTemplate.queryForObject(builder.buildCount(), builder.getParams(), Integer.class);
		int totalSentToday = jdbcTemplate.queryForObject(todaySentCount.build(), todaySentCount.getParams(), Integer.class);
		List<AssignmentCount> assignmentCountList = jdbcTemplate.query(todayBuilder.build(), todayBuilder.getParams(), new TotalAssignmentCountMapper());
		final TotalAssignmentCount assignmentCount;
		if (CollectionUtils.isEmpty(assignmentCountList)) {
			assignmentCount = new TotalAssignmentCount();
		} else {
			assignmentCount = new TotalAssignmentCount();
			for (AssignmentCount count : assignmentCountList) {
				String status = count.getLeft();
				Integer statusCount = count.getRight();
				switch (status) {
					case "sent":
						assignmentCount.setTodaySentAssignments(statusCount);
						break;
					case "draft":
						assignmentCount.setTodayCreatedAssignments(statusCount);
						break;
					case "active":
						assignmentCount.setTodayAcceptedAssignments(statusCount);
						break;
					case "cancelled":
						assignmentCount.setTodayCancelledAssignments(statusCount);
						break;
					case "void":
						assignmentCount.setTodayVoidedAssignments(statusCount);
						break;
				}
			}
		}
		assignmentCount.setOpenAssignments(totalInRealtime);
		assignmentCount.setTodaySentAssignments(totalSentToday);
		return assignmentCount;
	}
}
