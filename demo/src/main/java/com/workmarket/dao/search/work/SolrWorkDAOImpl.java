package com.workmarket.dao.search.work;

import com.google.common.collect.Lists;
import com.workmarket.data.solr.model.SolrWorkData;
import com.workmarket.domains.work.model.PublicWork;
import com.workmarket.configuration.Constants;
import com.workmarket.utility.DateUtilities;
import com.workmarket.utility.NumberUtilities;
import com.workmarket.utility.SearchUtilities;
import com.workmarket.utility.StringUtilities;
import com.workmarket.utility.sql.SQLBuilder;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;

import static com.workmarket.utility.CollectionUtilities.isEmpty;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

@Repository
public class SolrWorkDAOImpl implements SolrWorkDAO {

	@Resource(name = "jdbcTemplate")
	private NamedParameterJdbcTemplate jdbcTemplate;

	private static final class SolrWorkDataMapper implements RowMapper<SolrWorkData> {

		@Override
		public SolrWorkData mapRow(ResultSet rs, int rowNum) throws SQLException {
			SolrWorkData row = new SolrWorkData();
			List<String> externalUniqueIds = Lists.newArrayList();
			row.setWorkId(rs.getLong("workId"));
			row.setUuid(rs.getString("uuid"));
			row.setWorkNumber(rs.getString("work_number"));
			row.setParentId(rs.getLong("parent_id"));
			row.setParentTitle(rs.getString("parentTitle"));
			row.setParentDescription(rs.getString("parentDescription"));
			row.setTitle(rs.getString("title"));
			row.setDescription(rs.getString("description"));
			row.setInstructions(rs.getString("instructions"));
			row.setSkills(StringUtilities.stripHTML(rs.getString("skills")));
			row.setBuyerUserId(SearchUtilities.encodeId(rs.getLong("buyerId")));
			row.setBuyerFullName(StringUtilities.fullName(rs.getString("buyerFirstName"), rs.getString("buyerLastName")));
			row.setCompanyId(rs.getLong("company_id"));
			row.setCompanyName(rs.getString("companyName"));
			row.setClientLocationId(rs.getLong("locationId"));
			row.setClientLocationName(rs.getString("locationName"));
			row.setClientLocationNumber(rs.getString("location_number"));
			row.setClientCompanyId(rs.getLong("clientCompanyId"));
			row.setClientCompanyName(rs.getString("clientCompanyName"));
			row.setSupportName(StringUtilities.fullName(rs.getString("buyerSupportFirstName"), rs.getString("buyerSupportLastName")));
			row.setSupportEmail(rs.getString("buyerSupportEmail"));
			row.setCreatorUserId(SearchUtilities.encodeId(rs.getLong("creator_id")));
			row.setDispatcherId(rs.getLong("dispatcherId"));
			row.setShowInFeed(rs.getBoolean("showInFeed"));
			row.setOffSite(rs.getBoolean("offSite"));
			row.setTimeZoneId(rs.getString("timeZoneId"));
			row.setOpenNegotiations(rs.getBoolean("openNegotiations"));
			row.setOpenQuestions(rs.getBoolean("openQuestions"));
			row.setAssignToFirstResource(rs.getBoolean("assign_to_first_resource"));
			row.setApplicationsPending(rs.getBoolean("applicationsPending"));

			row.setConfirmed(rs.getBoolean("confirmed_flag"));
			row.setResourceConfirmationRequired(rs.getBoolean("resource_confirmation_flag"));

			row.setPricingType(rs.getString("pricing_strategy_type"));
			BigDecimal buyerFee = NumberUtilities.defaultValue(rs.getBigDecimal("buyerFee"));
			if (buyerFee.compareTo(Constants.MAX_WORK_FEE) > 0) {
				buyerFee = Constants.MAX_WORK_FEE;
			}
			row.setBuyerFee(buyerFee.doubleValue());
			row.setBuyerTotalCost(NumberUtilities.defaultValue(rs.getBigDecimal("buyerTotalCost")).doubleValue());
			row.setWorkPrice(NumberUtilities.defaultValue(rs.getBigDecimal("work_price")).doubleValue());
			row.setAmountEarned(rs.getDouble("amount_earned"));

			String title = rs.getString("title");
			if (title != null && title.length() > PublicWork.MAX_TITLE_LENGTH) {
				row.setPublicTitle(title.substring(0, PublicWork.MAX_TITLE_LENGTH));
			} else {
				row.setPublicTitle(rs.getString("title"));
			}

			BigDecimal spendLimit = rs.getBigDecimal("spendLimit");
			row.setSpendLimit(NumberUtilities.defaultValue(spendLimit).doubleValue());
			row.setSpendLimitWithFee(
				SolrWorkData.calculateSpendLimitWithFee(spendLimit, rs.getBigDecimal("workFeePercentage"))
			);

			row.setInvoiceId(rs.getLong("invoiceId"));
			row.setInvoiceNumber(rs.getString("invoiceNumber"));
			row.setAutoPayEnabled(rs.getBoolean("auto_pay_enabled"));

			String latitude = rs.getString("latitude");
			String longitude = rs.getString("longitude");

			if (isNotBlank(latitude) && isNotBlank(longitude)) {
				row.setLatitude(Double.valueOf(latitude));
				row.setLongitude(Double.valueOf(longitude));
				row.setLocation(latitude + "," + longitude);
			}

			row.setWorkStatusTypeCode(rs.getString("workStatusCode"));
			row.setWorkStatusTypeDescription(rs.getString("workStatusDescription"));

			row.setIndustryId(rs.getLong("industry_id"));
			row.setCity(rs.getString("city"));
			row.setState(rs.getString("state"));
			row.setPostalCode(rs.getString("postalCode"));
			row.setCountry(rs.getString("country"));

			Calendar createdOn = DateUtilities.getCalendarFromDate(rs.getTimestamp("created_on"));
			if (createdOn != null) {
				row.setCreatedDate(createdOn.getTime());
			}

			Calendar scheduleFrom = DateUtilities.getCalendarFromDate(rs.getTimestamp("schedule_from"));
			if (scheduleFrom != null) {
				row.setScheduleFromDate(scheduleFrom.getTime());
			}

			Calendar scheduleThrough = DateUtilities.getCalendarFromDate(rs.getTimestamp("schedule_through"));
			if (scheduleThrough != null) {
				row.setScheduleThroughDate(scheduleThrough.getTime());
			}

			Calendar closedOn = DateUtilities.getCalendarFromDate(rs.getTimestamp("closed_on"));
			if (closedOn != null) {
				row.setApprovedDate(closedOn.getTime());
			}

			Calendar sentOn = DateUtilities.getCalendarFromDate(rs.getTimestamp("sent_on"));
			if (sentOn != null) {
				row.setSendDate(sentOn.getTime());
			}

			Calendar paidOn = DateUtilities.getCalendarFromDate(rs.getTimestamp("paid_on"));
			if (paidOn != null) {
				row.setPaidDate(paidOn.getTime());
			}

			Calendar completedOn = DateUtilities.getCalendarFromDate(rs.getTimestamp("complete_on"));
			if (completedOn != null) {
				row.setCompletedDate(completedOn.getTime());
			}

			Calendar dueOn = DateUtilities.getCalendarFromDate(rs.getTimestamp("dueDate"));
			if (dueOn != null) {
				row.setDueDate(dueOn.getTime());
			}

			row.setProjectId(rs.getLong("projectId"));
			row.setProjectName(rs.getString("projectName"));

			Calendar lastModifiedOn = DateUtilities.getCalendarFromDate(rs.getTimestamp("lastModifiedOn"));
			if (lastModifiedOn != null) {
				row.setLastModifiedDate(lastModifiedOn.getTime());
			}
			row.setModifierFirstName(rs.getString("modifierFirstName"));
			row.setModifierLastName(rs.getString("modifierLastName"));

			String countyId = rs.getString("countyId");
			if (isNotBlank(countyId)) {
				row.setCountyId(Long.valueOf(countyId));
				row.setCountyName(rs.getString("countyName"));
			}
			row.setIndexDate(Calendar.getInstance().getTime());

			row.setUniqueExternalId(rs.getString("uniqueExternalId"));
			row.setRecurrenceUUID(rs.getString("recurrenceUUID"));
			if (row.getRecurrenceUUID() != null) {
				externalUniqueIds.add(row.getRecurrenceUUID());
			}
			final String decisionFlowUuid = rs.getString("decisionFlowUuid");
			if (decisionFlowUuid != null) {
				externalUniqueIds.add(decisionFlowUuid);
			}
			row.setExternalUniqueIds(externalUniqueIds);
			return row;
		}
	}

	@Override
	public SolrWorkData getSolrDataById(Long id) {
		SQLBuilder builder = SolrWorkSqlUtil.newWorkSolrIndexSQLBuilder();
		builder.addWhereClause("work.id = :workId")
				.addParam("workId", id);
		List<SolrWorkData> solrWorkData = jdbcTemplate.query(builder.build(), builder.getParams(), new SolrWorkDataMapper());
		if (CollectionUtils.isNotEmpty((solrWorkData))) {
			return solrWorkData.get(0);
		}
		return null;
	}

	@Override
	public List<SolrWorkData> getSolrDataById(List<Long> ids) {
		if (isEmpty(ids)) {
			return Collections.emptyList();
		}

		SQLBuilder builder = SolrWorkSqlUtil.newWorkSolrIndexSQLBuilder();
		builder.addWhereInClause("work.id", "workIds", ids);
		return jdbcTemplate.query(builder.build(), builder.getParams(), new SolrWorkDataMapper());
	}

	@Override
	public List<SolrWorkData> getSolrDataByWorkNumber(List<String> workNumbers) {
		if (isEmpty(workNumbers)) {
			return Collections.emptyList();
		}

		SQLBuilder builder = SolrWorkSqlUtil.newWorkSolrIndexSQLBuilder();
		builder.addWhereInClause("work.work_number", "workNumbers", workNumbers);
		return jdbcTemplate.query(builder.build(), builder.getParams(), new SolrWorkDataMapper());
	}

	@Override
	public List<SolrWorkData> getSolrDataBetweenIds(Long fromId, Long toId) {
		SQLBuilder builder = SolrWorkSqlUtil.newWorkSolrIndexSQLBuilder();

		if (fromId != null && toId != null) {
			builder.addWhereClause("work.id BETWEEN :fromId AND :toId")
					.addParam("fromId", fromId)
					.addParam("toId", toId);
		}
		return jdbcTemplate.query(builder.build(), builder.getParams(), new SolrWorkDataMapper());
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<SolrWorkData> getSolrDataChanged(Calendar from) {
		if (from == null) {
			return Collections.EMPTY_LIST;
		}
		SQLBuilder builder = SolrWorkSqlUtil.newWorkSolrIndexSQLBuilder();

		builder.addWhereClause("waa.last_action_on >= :from")
				.addParam("from", from);
		return jdbcTemplate.query(builder.build(), builder.getParams(), new SolrWorkDataMapper());
	}

	@Override
	public List<String> getSolrDataUuidsByIds(final List<Long> ids) {
		throw new UnsupportedOperationException("Uuids are not available for workcore");
	}
}
