package com.workmarket.domains.work.dao;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.workmarket.dao.AbstractDAO;
import com.workmarket.domains.model.ApprovalStatus;
import com.workmarket.domains.model.DateRange;
import com.workmarket.domains.model.DateRangeUtilities;
import com.workmarket.domains.model.User;
import com.workmarket.domains.model.WorkResource;
import com.workmarket.service.business.dto.DispatcherDTO;
import com.workmarket.service.business.dto.WorkResourceDetail;
import com.workmarket.service.business.dto.WorkResourceDetailPagination;
import com.workmarket.domains.model.WorkResourcePagination;
import com.workmarket.domains.model.WorkResourceStatusType;
import com.workmarket.domains.model.WorkStatusType;
import com.workmarket.domains.model.acl.AclRole;
import com.workmarket.domains.model.lane.LaneType;
import com.workmarket.domains.model.pricing.FullPricingStrategy;
import com.workmarket.domains.model.pricing.PricingStrategyType;
import com.workmarket.domains.model.resource.LiteResource;
import com.workmarket.domains.work.model.Work;
import com.workmarket.domains.work.model.WorkResourceFeedbackPagination;
import com.workmarket.domains.work.model.WorkResourceFeedbackRow;
import com.workmarket.domains.work.model.WorkSchedule;
import com.workmarket.domains.work.model.negotiation.WorkNegotiation;
import com.workmarket.integration.autotask.util.StringUtil;
import com.workmarket.dto.AddressDTO;
import com.workmarket.service.business.dto.WorkResourceDTO;
import com.workmarket.service.business.event.EventFactory;
import com.workmarket.service.infra.event.EventRouter;
import com.workmarket.utility.DateUtilities;
import com.workmarket.utility.GeoUtilities;
import com.workmarket.utility.StringUtilities;
import com.workmarket.utility.sql.SQLBuilder;
import com.workmarket.utility.sql.SQLOperator;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.math.util.MathUtils;
import org.hibernate.Criteria;
import org.hibernate.FetchMode;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.criterion.Subqueries;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.workmarket.utility.CollectionUtilities.*;

@Repository
public class WorkResourceDAOImpl extends AbstractDAO<WorkResource> implements WorkResourceDAO {

	private static final Log logger = LogFactory.getLog(WorkResourceDAOImpl.class);

	@Resource(name = "readOnlyJdbcTemplate")
	@Autowired private NamedParameterJdbcTemplate jdbcTemplate;
	@Autowired private WorkResourceDecorator resourceDecorator;
	@Autowired private WorkResourceDetailSQLFactory resourceDetailSQLFactory;
	@Autowired private EventRouter eventRouter;
	@Autowired private EventFactory eventFactory;

	protected Class<WorkResource> getEntityClass() {
		return WorkResource.class;
	}

	public static class WorkResourceDetailMapper implements RowMapper<WorkResourceDetail> {
		// The following lists are in order with each other
		private List<Double> distance;
		private List<String> fromPostalCode;
		private List<String> toPostalCode;

		private Double rlat;
		private Double rlng;
		private WorkResourceDetailPagination pagination;

		public void addDistance(Double d) {
			if (distance == null) {
				distance = new ArrayList<>();
			}

			if (d != null) {
				distance.add(d);
			}
		}

		public List<Double> getDistance() {
			return distance;
		}

		public void addFromPostalCode(String code) {
			if (fromPostalCode == null) {
				fromPostalCode = new ArrayList<>();
			}

			if (code != null) {
				fromPostalCode.add(code);
			}
		}

		public List<String> getFromPostalCode() {
			return fromPostalCode;
		}

		public void addToPostalCode(String code) {
			if (toPostalCode == null) {
				toPostalCode = new ArrayList<>();
			}

			if (code != null) {
				toPostalCode.add(code);
			}
		}

		public List<String> getToPostalCode() {
			return toPostalCode;
		}

		public Double getRlat() {
			return rlat;
		}

		public void setReferenceLatitude(Double rlat) {
			this.rlat = rlat;
		}

		public Double getRlng() {
			return rlng;
		}

		public void setReferenceLongitude(Double rlng) {
			this.rlng = rlng;
		}

		public void setPagination(WorkResourceDetailPagination pagination) {
			this.pagination = pagination;
		}

		public WorkResourceDetailPagination getPagination() {
			return pagination;
		}

		// Extracted here for testing purposes
		public WorkResourceDetail getWorkResourceDetail() {
			return new WorkResourceDetail();
		}

		@Override
		public WorkResourceDetail mapRow(ResultSet rs, int rowNum) throws SQLException {
			WorkResourceDetail resource = getWorkResourceDetail();
			resource.setWorkResourceId(rs.getLong("resourceId"));
			resource.setWorkResourceStatusTypeCode(rs.getString("work_resource_status_type_code"));
			resource.setUserId(rs.getLong("userId"));
			resource.setUserNumber(rs.getString("user_number"));
			resource.setFirstName(rs.getString("first_name"));
			resource.setLastName(rs.getString("last_name"));
			resource.setCompanyId(rs.getLong("companyId"));
			resource.setCompanyName(rs.getString("companyName"));
			resource.setEmail(rs.getString("email"));
			Long laneType = rs.getLong("laneType");
			if (rs.wasNull()) {
				resource.setLaneType(LaneType.LANE_4);
			} else {
				resource.setLaneType(LaneType.values()[laneType.intValue()]);
			}
			resource.setWorkPhone(rs.getString("work_phone"));
			resource.setWorkPhoneExtension(rs.getString("work_phone_extension"));
			resource.setMobilePhone(rs.getString("mobile_phone"));

			resource.setRating(Double.valueOf(rs.getDouble("rating_average")).intValue());
			resource.setNumberOfRatings(rs.getInt("rating_count"));
			resource.setAvatarCdnUri(rs.getString("avatarCdnUri"));
			resource.setAvatarUUID(rs.getString("avatarUUID"));
			resource.setAvatarAvailabilityType(rs.getString("avatarAvailabilityType"));
			resource.setAssignToFirstToAccept(rs.getBoolean("assignToFirstToAccept"));

			if (getPagination().isIncludeApplyNegotiation()) {

				if (rs.getBoolean("hasNegotiation")) {
					WorkNegotiation negotiation = new WorkNegotiation();

					negotiation.setId(rs.getLong("negotiationId"));
					negotiation.setRequestedOn(DateUtilities.getCalendarFromDate(rs.getTimestamp("negotiationRequestedOn")));
					negotiation.setExpiresOn(DateUtilities.getCalendarFromDate(rs.getTimestamp("negotiationExpiresOn")));
					negotiation.setApprovalStatus(ApprovalStatus.lookupByCode(rs.getInt("negotiationApprovalStatus")));

					negotiation.setPriceNegotiation(rs.getBoolean("negotiationNegotiatePriceFlag"));

					if (negotiation.isPriceNegotiation()) {
						FullPricingStrategy pricing = new FullPricingStrategy();
						pricing.setPricingStrategyType(PricingStrategyType.valueOf(rs.getString("negotiationPricingStrategyType")));
						pricing.setFlatPrice(rs.getBigDecimal("negotiationFlatPrice"));
						pricing.setMaxFlatPrice(rs.getBigDecimal("negotiationMaxFlatPrice"));
						pricing.setPerHourPrice(rs.getBigDecimal("negotiationPerHourPrice"));
						pricing.setMaxNumberOfHours(rs.getBigDecimal("negotiationMaxNumberOfHours"));
						pricing.setPerUnitPrice(rs.getBigDecimal("negotiationPerUnitPrice"));
						pricing.setMaxNumberOfUnits(rs.getBigDecimal("negotiationMaxNumberOfUnits"));
						pricing.setInitialPerHourPrice(rs.getBigDecimal("negotiationInitialPerHourPrice"));
						pricing.setInitialNumberOfHours(rs.getBigDecimal("negotiationInitialNumberOfHours"));
						pricing.setAdditionalPerHourPrice(rs.getBigDecimal("negotiationAdditionalPerHourPrice"));
						pricing.setMaxBlendedNumberOfHours(rs.getBigDecimal("negotiationMaxBlendedNumberOfHours"));
						pricing.setInitialPerUnitPrice(rs.getBigDecimal("negotiationInitialPerUnitPrice"));
						pricing.setInitialNumberOfUnits(rs.getBigDecimal("negotiationInitialNumberOfUnits"));
						pricing.setAdditionalPerUnitPrice(rs.getBigDecimal("negotiationAdditionalPerUnitPrice"));
						pricing.setMaxBlendedNumberOfUnits(rs.getBigDecimal("negotiationMaxBlendedNumberOfUnits"));
						pricing.setAdditionalExpenses(rs.getBigDecimal("negotiationAdditionalExpenses"));
						pricing.setBonus(rs.getBigDecimal("negotiationBonus"));
						pricing.setOverridePrice(rs.getBigDecimal("negotiationOverridePrice"));

						negotiation.setFullPricingStrategy(pricing);

						BigDecimal spendLimit = rs.getBigDecimal("negotiationSpendLimit");
						BigDecimal feePercentage = rs.getBigDecimal("negotiationFeePercentage");

						// TODO Verify/move math
						BigDecimal fee = feePercentage.movePointLeft(2).multiply(spendLimit);
						BigDecimal total = spendLimit.add(fee);

						resource.setApplyNegotiationSpendLimit(spendLimit);
						resource.setApplyNegotiationFee(fee);
						resource.setApplyNegotiationTotalCost(total);
					}

					negotiation.setScheduleNegotiation(rs.getBoolean("negotiationNegotiateScheduleFlag"));

					if (negotiation.isScheduleNegotiation()) {
						negotiation.setScheduleRangeFlag(rs.getBoolean("negotiationScheduleIsRangeFlag"));
						negotiation.setScheduleFrom(DateUtilities.getCalendarFromDate(rs.getTimestamp("negotiationScheduleFrom")));
						negotiation.setScheduleThrough(DateUtilities.getCalendarFromDate(rs.getTimestamp("negotiationScheduleThrough")));
					}

					resource.setApplyNegotiation(negotiation);
					resource.setApplyNegotiationNote(rs.getString("negotiationNoteContent"));
				}

				resource.setOnTimePercentage(rs.getDouble("onTimePercentage"));
				resource.setDeliverableOnTimePercentage(rs.getDouble("deliverableOnTimePercentage"));
			} else {
				resource.setQuestionPending(rs.getBoolean("question_pending"));

				String negotiationStatus = rs.getString("negotiation_status");
				if (negotiationStatus != null) {
					resource.setLatestNegotiationPending(ApprovalStatus.lookupByCode(Integer.valueOf(negotiationStatus)).equals(ApprovalStatus.PENDING));
					resource.setLatestNegotiationDeclined(ApprovalStatus.lookupByCode(Integer.valueOf(negotiationStatus)).equals(ApprovalStatus.DECLINED));
					resource.setLatestNegotiationExpired(rs.getBoolean("negotiation_expired"));
				}
			}

			resource.setInvitedOn(DateUtilities.getCalendarFromDate(rs.getTimestamp("created_on")));
			resource.setModifiedOn(DateUtilities.getCalendarFromDate(rs.getTimestamp("modified_on")));

			AddressDTO address = new AddressDTO();
			address.setAddress1(rs.getString("line1"));
			address.setAddress2(rs.getString("line2"));
			address.setCity(rs.getString("city"));
			address.setState(rs.getString("state"));
			address.setPostalCode(rs.getString("postal_code"));
			address.setCountry(rs.getString("country"));

			resource.setAddress(address);
			resource.setLatitude(rs.getBigDecimal("latitude"));
			resource.setLongitude(rs.getBigDecimal("longitude"));

			Double distance = rs.getDouble("distance");
			String toPostalCode = rs.getString("work_postal_code");

			if (address.getPostalCode() != null && address.getPostalCode().equals(toPostalCode)) {
				resource.setDistance(0d);
			} else if (distance != 0d) { // distance has been calculated
				resource.setDistance(distance);
			} else { // no distance, so let's calculate it
				Double lat = rs.getDouble("latitude");
				Double lng = rs.getDouble("longitude");
				if (getRlat() != null && getRlng() != null && lat != null && lng != null &&
					(getRlat() != 0D || getRlng() != 0D) && (lat != 0D || lng != 0D)) {
					Double dist = GeoUtilities.distanceInMiles(getRlat(), getRlng(), lat, lng);
					Double roundedValue = MathUtils.round(dist, 1);
					resource.setDistance(roundedValue);

					if (!StringUtil.isNullOrEmpty(toPostalCode) && !StringUtil.isNullOrEmpty(address.getPostalCode())) {
						addFromPostalCode(address.getPostalCode());
						addToPostalCode(toPostalCode);
						addDistance(roundedValue);
					}
				}
			}

			resource.setTargeted(rs.getBoolean("targeted"));
			resource.setBlocked(rs.getBoolean("blocked"));
			resource.setJoinedOn(DateUtilities.getCalendarFromDate(rs.getTimestamp("joined_on")));

			return resource;
		}
	}

	@Override
	public WorkResource findByUserAndWork(Long userId, Long workId) {
		Criteria criteria = getFactory().getCurrentSession().createCriteria(getEntityClass())
				.add(Restrictions.eq("user.id", userId))
				.add(Restrictions.eq("work.id", workId))
				.setFetchMode("timeTracking", FetchMode.JOIN)
				.setFetchMode("user", FetchMode.JOIN)
				.createAlias("user", "user")
				.setFetchMode("user.company", FetchMode.JOIN);

		return (WorkResource) criteria.uniqueResult();
	}

	@Override
	public List<WorkResource> findByUserIdsAndWorkId(Collection<Long> userIds, Long workId) {
		if (isEmpty(userIds)) {
			return Collections.emptyList();
		}
		Criteria criteria = getFactory().getCurrentSession().createCriteria(getEntityClass())
			.add(Restrictions.in("user.id", userIds))
			.add(Restrictions.eq("work.id", workId))
			.setFetchMode("timeTracking", FetchMode.JOIN)
			.setFetchMode("user", FetchMode.JOIN)
			.createAlias("user", "user")
			.setFetchMode("user.company", FetchMode.JOIN);

		return criteria.list();
	}

	@Override
	public List<String> findAssignToFirstResourceWorkNumbersByWorkIds(Collection<Long> workIds) {
		if (isEmpty(workIds)) {
			return Collections.emptyList();
		}
		SQLBuilder builder = new SQLBuilder();

		builder.addColumn("w.work_number")
			.addTable("work_resource wr")
			.addJoin("INNER JOIN work w ON w.id = wr.work_id")
			.addWhereInClause("wr.work_id", "workId", workIds)
			.addWhereClause("wr.assign_to_first_resource = 1");

		return jdbcTemplate.queryForList(builder.build(), builder.getParams(), String.class);
	}

	@Override
	public List<String> findAssignToFirstResourceWorkNumbersByUserNumberAndWorkIds(String userNumber, Collection<Long> workIds) {
		if (isEmpty(workIds)) {
			return Collections.emptyList();
		}

		SQLBuilder builder = new SQLBuilder();
		builder.addColumn("w.work_number")
			.addTable("work_resource wr")
			.addJoin("INNER JOIN work w ON w.id = wr.work_id")
			.addJoin("INNER JOIN user u ON u.id = wr.user_id")
			.addWhereInClause("wr.work_id", "workId", workIds)
			.addWhereClause("wr.assign_to_first_resource = 1")
			.addWhereClause("u.user_number", "=", "user_number", userNumber);

		return jdbcTemplate.queryForList(builder.build(), builder.getParams(), String.class);
	}

	@Override
	@SuppressWarnings("unchecked")
	public WorkResourcePagination findByWork(Long workId, WorkResourcePagination pagination) {
		Criteria criteria = getFactory().getCurrentSession().createCriteria(getEntityClass())
				.setFirstResult(pagination.getStartRow())
				.setMaxResults(pagination.getResultsLimit())
				.setFetchMode("user", FetchMode.JOIN)
				.setFetchMode("workResourceStatusType", FetchMode.JOIN);
		Criteria count = getFactory().getCurrentSession().createCriteria(getEntityClass())
				.setProjection(Projections.rowCount());

		criteria.add(Restrictions.eq("work.id", workId));
		count.add(Restrictions.eq("work.id", workId));

		boolean doFilterByWorkResourceStatus = pagination.getFilters().containsKey(WorkResourcePagination.FILTER_KEYS.WORK_RESOURCE_STATUS.toString());
		if (pagination.getFilters() != null && doFilterByWorkResourceStatus) {
			String statusCode = pagination.getFilters().get(WorkResourcePagination.FILTER_KEYS.WORK_RESOURCE_STATUS.toString());
			criteria.add(Restrictions.eq("workResourceStatusType.code", statusCode));
			count.add(Restrictions.eq("workResourceStatusType.code", statusCode));
		}

		pagination.setResults(criteria.list());
		if (count.list().size() > 0) {
			pagination.setRowCount(((Long) count.list().get(0)).intValue());
		} else {
			pagination.setRowCount(0);
		}
		return pagination;
	}

	@Override
	@SuppressWarnings("unchecked")
	public List<WorkResource> findNotDeclinedForWork(Long workId) {
		return getFactory().getCurrentSession().getNamedQuery("workResource.notDeclinedForWork")
			.setLong("work_id", workId)
			.list();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Long> findUserIdsNotDeclinedForWork(Long workId) {
		return getFactory().getCurrentSession().createCriteria(getEntityClass())
			.setProjection(Projections.property("user.id"))
			.add(Restrictions.ne("workResourceStatusType.code", "declined"))
			.add(Restrictions.eq("work.id", workId))
			.list();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Long> findUserIdsDeclinedForWork(Long workId) {
		return getFactory().getCurrentSession().createCriteria(getEntityClass())
			.setProjection(Projections.property("user.id"))
			.add(Restrictions.eq("workResourceStatusType.code", "declined"))
			.add(Restrictions.eq("work.id", workId))
			.list();
	}

	@Override
	@SuppressWarnings("unchecked")
	public List<Long> findWorkerIdsForWork(Long workId) {
		return getFactory().getCurrentSession().createCriteria(getEntityClass())
			.setProjection(Projections.property("user.id"))
			.add(Restrictions.eq("work.id", workId))
			.list();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<WorkResource> findAllResourcesForWork(long workId) {
		Criteria criteria = getFactory().getCurrentSession().createCriteria(getEntityClass())
			.setFetchMode("user", FetchMode.JOIN)
			.add(Restrictions.eq("work.id", workId));

		return criteria.list();
	}

	@Override
	public WorkResource findActiveWorkResource(Long workId) {
		return (WorkResource) getFactory().getCurrentSession().createCriteria(getEntityClass())
			.setFetchMode("user", FetchMode.JOIN)
			.add(Restrictions.eq("work.id", workId))
			.add(Restrictions.eq("workResourceStatusType.code", WorkResourceStatusType.ACTIVE))
			.add(Restrictions.eq("assignedToWork", Boolean.TRUE))
			.setMaxResults(1)
			.uniqueResult();
	}

	@Override
	public Long findActiveWorkerId(Long workId) {
		SQLBuilder builder = new SQLBuilder()
			.addColumn("r.user_id")
			.addTable("work_resource r")
			.addWhereClause("r.work_id", "=", "work_id", workId)
			.addWhereClause("r.assigned_to_work = true")
			.addWhereClause("r.work_resource_status_type_code", "=", "status_code", WorkResourceStatusType.ACTIVE);

		List<Long> results = jdbcTemplate.query(builder.build(), builder.getParams(), new RowMapper<Long>() {
			@Override
			public Long mapRow(ResultSet resultSet, int i) throws SQLException {
				return resultSet.getLong("user_id");
			}
		});

		return isEmpty(results) ? null : results.get(0);
	}

	@Override
	public WorkResource findById(Long workResourceId) {
		return (WorkResource)getFactory().getCurrentSession().createCriteria(getEntityClass())
			.setFetchMode("user", FetchMode.JOIN)
			.add(Restrictions.eq("id", workResourceId)).uniqueResult();
	}

	@Override
	public boolean isUserActiveResourceForWorkWithAssessment(Long userId, Long assessmentId) {
		SQLBuilder builder = new SQLBuilder()
			.addColumn("r.id")
			.addTable("work_resource r")
			.addJoin("INNER JOIN work w ON w.id = r.work_id")
			.addJoin("INNER JOIN work_assessment_association wa ON wa.work_id = w.id")
			.addJoin("INNER JOIN assessment a ON a.id = wa.assessment_id AND a.id = :assessment_id")
			.addWhereClause("r.assigned_to_work = true")
			.addWhereClause("r.user_id", "=", "user_id", userId)
			.addParam("assessment_id", assessmentId);

		int count = jdbcTemplate.queryForObject(builder.buildCount("*"), builder.getParams(), Integer.class);
		return count > 0;
	}

	@Override
	public boolean isUserResourceForWork(Long workId, Long userId) {
		SQLBuilder builder = new SQLBuilder()
			.addColumn("r.id")
			.addTable("work_resource r")
			.addWhereClause("r.user_id", SQLOperator.EQUALS, "user_id", userId)
			.addWhereClause("r.work_id", SQLOperator.EQUALS, "work_id", workId)
			.addLimitClause(0, 1, true);

		return jdbcTemplate.queryForObject(builder.buildCount("r.id"), builder.getParams(), Integer.class) > 0;
	}

	@Override
	public List<WorkResource> findResourcesInFromWorkNotInToWork(long fromWorkId, long toWorkId) {
		DetachedCriteria subCriteria = DetachedCriteria.forClass(getEntityClass())
				.setProjection(Projections.distinct(Projections.property("user.id")))
				.add(Restrictions.eq("work.id", toWorkId));

		Criteria criteria = getFactory().getCurrentSession().createCriteria(getEntityClass())
				.setFetchMode("user", FetchMode.JOIN)
				.add(Restrictions.eq("work.id", fromWorkId))
				.add(Subqueries.propertyNotIn("user.id", subCriteria));

		return criteria.list();
	}

	@Override
	public WorkResourceDetailPagination findAllResourcesForWork(Long workId, WorkResourceDetailPagination pagination) {
		SQLBuilder builder = resourceDetailSQLFactory.getResourceListBuilder(workId, pagination);

		if (pagination.getSortColumn() != null) {
			builder.addOrderBy("resource.assigned_to_work", WorkResourceDetailPagination.SORT_DIRECTION.DESC.toString());
			if (WorkResourceDetailPagination.SORTS.valueOf(pagination.getSortColumn()).equals(WorkResourceDetailPagination.SORTS.ACTIVE_AND_LAST_NAME)) {
				builder.addOrderBy(WorkResourceDetailPagination.SORTS.LAST_NAME.getColumn(), WorkResourceDetailPagination.SORT_DIRECTION.ASC.toString());
			} else {
				builder.addOrderBy(WorkResourceDetailPagination.SORTS.valueOf(pagination.getSortColumn()).getColumn(), pagination.getSortDirection().toString());
			}
		} else {
			builder.addOrderBy(WorkResourceDetailPagination.SORTS.LAST_NAME.getColumn(), WorkResourceDetailPagination.SORT_DIRECTION.ASC.toString());
		}

		if (pagination.getFilters() != null) {
			int i = 0;
			for (String filterKey : pagination.getFilters().keySet()) {
				WorkResourceDetailPagination.FILTER_KEYS f = WorkResourceDetailPagination.FILTER_KEYS.valueOf(filterKey);
				builder
					.addWhereClause(f.getColumn() + " = :param" + (++i))
					.addParam("param" + i, pagination.getFilters().get(filterKey));
			}
		}

		builder.setStartRow(pagination.getStartRow());
		builder.setPageSize(pagination.getResultsLimit());

		WorkResourceDetailMapper rowMapper = new WorkResourceDetailMapper();
		rowMapper.setPagination(pagination);

		// Determine distance. Have to query the assignment's address
		// in order to have a reference lat/long for the distance calculation.

		SQLBuilder referenceLatLongSql = new SQLBuilder()
			.addColumns(
				"COALESCE(address.latitude, postal_code.latitude) AS latitude",
				"COALESCE(address.longitude, postal_code.longitude) AS longitude")
			.addTable("address")
			.addJoin("INNER JOIN work ON work.address_id = address.id")
			.addJoin("INNER JOIN postal_code ON postal_code.postal_code = address.postal_code")
			.addWhereClause("work.id", "=", "wid", workId)
			.setStartRow(0)
			.setPageSize(1);

		try {
			Map<String, Object> latlng = jdbcTemplate.queryForMap(referenceLatLongSql.build(), referenceLatLongSql.getParams());
			Object lat = latlng.get("latitude");
			Object lng = latlng.get("longitude");
			if (lat != null) {
				rowMapper.setReferenceLatitude(Double.valueOf(lat.toString()));
			}
			if (lng != null) {
				rowMapper.setReferenceLongitude(Double.valueOf(lng.toString()));
			}
		} catch (IncorrectResultSizeDataAccessException e) {
			// No location, no problem.
		}

		pagination.setResults(jdbcTemplate.query(builder.build(), builder.getParams(), rowMapper));
		pagination.setRowCount(jdbcTemplate.queryForObject(builder.buildCount("*"), builder.getParams(), Integer.class));

		// Determine if any rows have best price. Spec:
		// Show "best price" label for the response that currently has the lowest total cost.
		// Important: do not show this at all if > 1 applicants share the lowest total cost.

		if (pagination.isIncludeApplyNegotiation() && pagination.getRowCount() > 0) {
			SQLBuilder bestPriceSql = resourceDetailSQLFactory.getResourceBestPriceBuilder(workId);
			// `queryForMap` will blow up if the result size == 0
			List<Map<String,Object>> bestPrices = jdbcTemplate.queryForList(bestPriceSql.build(), bestPriceSql.getParams());
			Map<String,Object> bestPrice = first(bestPrices);
			if (MapUtils.isNotEmpty(bestPrice)) {
				Integer count = MapUtils.getInteger(bestPrice, "count", 0);
				if (count == 1) {
					Long resourceId = MapUtils.getLong(bestPrice, "resourceId");
					for (WorkResourceDetail r : pagination.getResults()) {
						if (!r.getWorkResourceId().equals(resourceId)) continue;
						r.setBestPrice(true);
					}
				}
			}
		}

		return pagination;
	}

	@Override
	public List<WorkResourceDTO> findAllResourcesForWorkSolrReindexOnly(Long workId) {
		SQLBuilder builder = new SQLBuilder();
		List<WorkResourceDTO> resources = Lists.newArrayList();

		builder.addColumns("wr.id as workResourceId", "user.first_name", "user.last_name", "user.id as userId", "company.id As companyId",
				"wr.work_resource_status_type_code", "company.effective_name", "user.user_number", "wr.assigned_to_work",
				"wr.appointment_from", "wr.appointment_through", "profile.mobile_phone", "profile.work_phone", "profile.work_phone_extension")
				.addTable("work_resource wr")
				.addJoin("INNER JOIN user ON user.id = wr.user_id")
				.addJoin("INNER JOIN company ON company.id = user.company_id")
				.addJoin("INNER JOIN profile ON profile.user_id = user.id")
				.addWhereClause("wr.work_id = :workId")
				.addParam("workId", workId);

		List<Map<String, Object>> results = jdbcTemplate.queryForList(builder.build(), builder.getParams());
		for (Map<String, Object> row : results) {
			WorkResourceDTO dto = new WorkResourceDTO();
			dto.setFirstName((String)row.get("first_name"));
			dto.setLastName((String) row.get("last_name"));
			dto.setWorkResourceId(((Integer) row.get("workResourceId")).longValue());
			dto.setUserId(((Integer) row.get("userId")).longValue());
			dto.setCompanyId(((Integer) row.get("companyId")).longValue());
			dto.setWorkResourceStatusTypeCode((String) row.get("work_resource_status_type_code"));
			dto.setCompanyName((String) row.get("effective_name"));
			dto.setUserNumber((String) row.get("user_number"));
			dto.setAssignedToWork((Boolean) row.get("assigned_to_work"));
			dto.setAppointmentFrom(DateUtilities.getCalendarFromDate((Timestamp)row.get("appointment_from")));
			dto.setAppointmentThrough(DateUtilities.getCalendarFromDate((Timestamp)row.get("appointment_through")));
			dto.setMobilePhoneNumber((String)row.get("mobile_phone"));
			dto.setWorkPhoneNumber((String)row.get("work_phone"));
			dto.setWorkPhoneExtension((String)row.get("work_phone_extension"));
			resources.add(dto);
		}
		return resources;
	}

	@Override
	public WorkResourceFeedbackPagination findResourceFeedbackForUserVisibleToUserAtCompany(Long userId, Long viewingUserId, Long viewingCompanyId, WorkResourceFeedbackPagination pagination) {
		SQLBuilder ratingsExistSql = new SQLBuilder()
				.addColumn("1")
				.addTable("rating r")
				.addWhereClause("r.work_id = w.id")
				.addWhereClause("r.rated_user_id = :userId")
				.addWhereClause("(r.rating_shared_flag = 'Y' OR r.rater_company_id = :companyId)")
				.addWhereClause("r.deleted = false")
				.addWhereClause("r.is_pending = false");

		SQLBuilder labelsExistSql = new SQLBuilder()
				.addColumn("1")
				.addTable("work_resource_label l")
				.addJoin("INNER JOIN work_resource_label_type t ON t.code = l.work_resource_label_type_code")
				.addWhereClause("l.work_id = w.id")
				.addWhereClause("l.work_resource_user_id = :userId")
				.addWhereClause("l.confirmed = true")
				.addWhereClause("l.ignored = false")
				.addWhereClause("t.visible = true");

		if (!userId.equals(viewingUserId)) {
			labelsExistSql.addWhereClause("l.work_company_id = :companyId");
		}

		SQLBuilder sql = new SQLBuilder()
				.addColumns("DISTINCT w.id", "w.work_number", "w.title", "w.schedule_from AS schedule_from", "w.schedule_through")
				.addColumns("wr.id AS workResourceId", "wr.user_id", "wr.appointment_from", "wr.appointment_through")
				.addColumns("c.id AS companyId", "c.effective_name AS companyName")
				.addTable("work w")
				.addJoin("INNER JOIN company c ON c.id = w.company_id")
				.addJoin("INNER JOIN work_resource wr ON w.id = wr.work_id")
				.addWhereClause("w.type = 'W'")
				.addWhereClause("w.deleted = false");

		if (pagination.hasFilter(WorkResourceFeedbackPagination.FILTER_KEYS.COMPANY_SCOPE)) {
			boolean scopeToCompany = BooleanUtils.toBoolean(pagination.getFilter(WorkResourceFeedbackPagination.FILTER_KEYS.COMPANY_SCOPE));
			if (scopeToCompany) {
				sql.addWhereClause("w.company_id = :companyId");
			}
		}

		sql.addWhereClause("w.work_status_type_code IN (" + StringUtils.join(StringUtilities.surround(WorkStatusType.ALL_RESOURCE_ASSIGNED_STATUS, "'"), ",") + ")")
				.addWhereClause("wr.user_id = :userId")
				.addWhereClause("wr.assigned_to_work = true")
				.addWhereClause(String.format("(EXISTS (%s) OR EXISTS (%s))", ratingsExistSql.build(), labelsExistSql.build()));

		SQLBuilder wrapper = new SQLBuilder()
			.addColumns("*")
			.addTable(String.format("(%s) a", sql.build()))
			.addDescOrderBy("a.schedule_from")
			.addParam("userId", userId)
			.addParam("companyId", viewingCompanyId)
			.setStartRow(pagination.getStartRow())
			.setPageSize(pagination.getResultsLimit());

		List<WorkResourceFeedbackRow> rows = jdbcTemplate.query(wrapper.build(), wrapper.getParams(), new RowMapper<WorkResourceFeedbackRow>() {
			@Override
			public WorkResourceFeedbackRow mapRow(ResultSet rs, int i) throws SQLException {
				DateRange workSchedule = new DateRange(DateUtilities.getCalendarFromDate(rs.getTimestamp("schedule_from")), DateUtilities.getCalendarFromDate(rs.getTimestamp("schedule_through")));
				DateRange resourceSchedule = new DateRange(DateUtilities.getCalendarFromDate(rs.getTimestamp("appointment_from")), DateUtilities.getCalendarFromDate(rs.getTimestamp("appointment_through")));

				return new WorkResourceFeedbackRow()
						.setWorkId(rs.getLong("id"))
						.setWorkNumber(rs.getString("work_number"))
						.setWorkTitle(rs.getString("title"))
						.setWorkSchedule(DateRangeUtilities.getAppointmentTime(workSchedule, resourceSchedule))
						.setCompanyId(rs.getLong("companyId"))
						.setCompanyName(rs.getString("companyName"))
						.setWorkResourceId(rs.getLong("workResourceId"))
						.setWorkResourceUserId(rs.getLong("user_id"));
			}
		});

		Integer count = jdbcTemplate.queryForObject(wrapper.buildCount(), wrapper.getParams(), Integer.class);

		rows = resourceDecorator.addRating(userId, viewingCompanyId, rows);
		rows = resourceDecorator.addWorkResourceLabels(userId, viewingUserId, viewingCompanyId, rows);

		pagination.setResults(rows);
		pagination.setRowCount(count);

		return pagination;
	}

	@Override
	public WorkResource createOpenWorkResource(Work work, User user, boolean targeted, boolean isVendor) {
		WorkResource workResource = new WorkResource(work, user);
		workResource.setTargeted(targeted);

		saveOrUpdate(workResource);
		return workResource;
	}

	@Override
	public List<LiteResource> findLiteResourceByWorkNumber(String workNumber) {
		SQLBuilder sql = new SQLBuilder()
				.addColumns("u.id, u.user_number, addr.latitude, addr.longitude")
				.addTable("work w")
				.addJoin("INNER JOIN work_resource wr on wr.work_id = w.id")
				.addJoin("INNER JOIN user u on u.id = wr.user_id")
				.addJoin("INNER JOIN profile p on p.user_id = u.id")
				.addJoin("INNER JOIN address addr on addr.id = p.address_id")
				.addWhereClause("w.work_number = :workNumber")
				.addParam("workNumber", workNumber);

		return jdbcTemplate.query(sql.build(), sql.getParams(), new RowMapper<LiteResource>() {
			@Override
			public LiteResource mapRow(ResultSet rs, int i) throws SQLException {
				LiteResource liteResource = new LiteResource();
				liteResource.setId(rs.getLong("u.id"));
				liteResource.setUserNumber(rs.getString("u.user_number"));
				liteResource.setLatitude(rs.getBigDecimal("addr.latitude"));
				liteResource.setLongitude(rs.getBigDecimal("addr.longitude"));

				return  liteResource;

			}
		});

	}

	@Override
	public List<WorkSchedule> findWorkSchedulesByWorker(long userId) {
		MapSqlParameterSource params = new MapSqlParameterSource();

		String sql =
			"SELECT 	wr.id workerId, wr.user_id, wr.work_resource_status_type_code, \n " +
			" 			w.id workId, w.company_id, w.work_number, \n " +
			" 			w.work_status_type_code, w.schedule_from, w.schedule_through \n " +
			" FROM 		work_resource wr \n " +
			" INNER 	join work w on wr.work_id = w.id " +
			" WHERE 	work_resource_status_type_code = :active " +
			" AND 		wr.user_id = :userId " +
			" AND 		w.work_status_type_code = :active ";

		params.addValue("userId", userId).addValue("active", WorkStatusType.ACTIVE);

		final List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql, params);

		final List<WorkSchedule> workSchedules = Lists.newArrayList();

		for (Map<String, Object> row : rows) {
			Calendar from = DateUtilities.getCalendarFromDate((Timestamp)row.get("schedule_from"));
			Calendar through = DateUtilities.getCalendarFromDate((Timestamp)row.get("schedule_through"));
			WorkSchedule workSchedule = new WorkSchedule(new DateRange(from, through));
			workSchedule.setWorkId((Integer)row.get("workId"))
				.setWorkNumber((String)row.get("work_number"))
				.setCompanyId((Integer)row.get("company_id"));
			workSchedules.add(workSchedule);
		}

		return workSchedules;
	}

	@Override
	public Map<Long, List<WorkSchedule>> findActiveWorkScheduleByWorkResourceExcludingCurrentWork(long workId) {
		final Map<Long, List<WorkSchedule>> results = Maps.newHashMap();
		MapSqlParameterSource params = new MapSqlParameterSource();
		String sql = "SELECT 	wr.id workerId, wr.user_id, wr.work_resource_status_type_code, \n " +
			" 					otherWork.id workId, otherWork.company_id, otherWork.work_number, \n " +
			" 					otherWork.work_status_type_code, otherWork.schedule_from, otherWork.schedule_through \n " +
			" FROM 		work_resource wr \n " +
			" INNER 	join work otherWork on wr.work_id = otherWork.id " +
			" WHERE 	work_resource_status_type_code = :active " +
			" AND 		otherWork.work_status_type_code = :active " +
			" AND 		EXISTS (SELECT id FROM work_resource WHERE work_id = :workId AND user_id = wr.user_id) " +
			" AND 		otherWork.id != :workId ";
		params.addValue("workId", workId).addValue("active", WorkStatusType.ACTIVE);

		List<Map<String, Object>> workScheduleList = jdbcTemplate.queryForList(sql, params);
		for (Map<String, Object> row : workScheduleList) {
			Calendar from = DateUtilities.getCalendarFromDate((Timestamp)row.get("schedule_from"));
			Calendar through = DateUtilities.getCalendarFromDate((Timestamp)row.get("schedule_through"));
			WorkSchedule workSchedule = new WorkSchedule(new DateRange(from, through));
			workSchedule.setWorkId((Integer)row.get("workId"))
					.setWorkNumber((String)row.get("work_number"))
					.setCompanyId((Integer)row.get("company_id"));
			Long workerUserId = ((Integer) row.get("user_id")).longValue();
			if (results.containsKey(workerUserId)) {
				results.get(workerUserId).add(workSchedule);
			} else {
				results.put(workerUserId, Lists.newArrayList(workSchedule));
			}
		}
		return results;
	}

	@Override
	public Map<Long, List<WorkSchedule>> findActiveSchedulesExcludingWorkByUserIds(
		long workId,
		Set<Long> userIds
	) {
		final Map<Long, List<WorkSchedule>> results = Maps.newHashMap();

		MapSqlParameterSource params = new MapSqlParameterSource();
		String sql = "" +
			"SELECT\n" +
			"	wr.id workerId,\n" +
			"	wr.user_id,\n" +
			"	wr.work_resource_status_type_code,\n" +
			"	w.id workId,\n" +
			"	w.company_id,\n" +
			"	w.work_number,\n" +
			"	w.work_status_type_code,\n" +
			"	w.schedule_from,\n" +
			"	w.schedule_through\n" +
			"FROM\n" +
			"	work_resource wr\n" +
			"		INNER JOIN work w\n" +
			"			ON wr.work_id = w.id\n" +
			"WHERE wr.work_resource_status_type_code = :active\n" +
			"AND w.work_status_type_code = :active\n" +
			"AND w.id <> :workId\n" +
			"AND wr.user_id in (:userIds);";
		params
			.addValue("workId", workId)
			.addValue("active", WorkStatusType.ACTIVE)
			.addValue("userIds", userIds);

		List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql, params);
		for (Map<String, Object> row : rows) {
			Calendar from = DateUtilities.getCalendarFromDate((Timestamp)row.get("schedule_from"));
			Calendar through = DateUtilities.getCalendarFromDate((Timestamp)row.get("schedule_through"));
			WorkSchedule workSchedule = new WorkSchedule(new DateRange(from, through));
			workSchedule.setWorkId((Integer)row.get("workId"))
				.setWorkNumber((String)row.get("work_number"))
				.setCompanyId((Integer)row.get("company_id"));
			Long workerUserId = ((Integer) row.get("user_id")).longValue();
			if (results.containsKey(workerUserId)) {
				results.get(workerUserId).add(workSchedule);
			} else {
				results.put(workerUserId, Lists.newArrayList(workSchedule));
			}
		}
		return results;
	}

	@Override
	public List<Long> findAllResourcesUserIdsForWorkWithNotificationAllowed(Long workId, String notificationTypeCode, boolean openOnly) {
		SQLBuilder builder = new SQLBuilder();
		List<Long> resources = Lists.newArrayList();

		builder.addColumn("wr.user_id")
				.addTable("work_resource wr")
				.addJoin("INNER JOIN user_notification_preference np on np.user_id = wr.user_id")
				.addWhereClause("wr.work_id = :workId")
				.addWhereClause("np.notification_type_code = :notificationTypeCode")
				.addWhereClause("(np.sms_flag = true OR np.push_flag = true)")
				.addParam("workId", workId)
				.addParam("notificationTypeCode", notificationTypeCode);
		if (openOnly) {
			builder.addWhereClause("wr.work_resource_status_type_code = 'open'");
		}
		logger.debug(builder.build());
		List<Map<String, Object>> results = jdbcTemplate.queryForList(builder.build(), builder.getParams());
		for (Map<String, Object> row : results) {
			resources.add(((Integer) row.get("user_id")).longValue());
		}
		return resources;
	}

	@Override
	public boolean isWorkNotifyAvailable(Long workId, String notificationTypeCode, boolean openOnly) {
		SQLBuilder builder = new SQLBuilder();
		builder.addTable("work_resource wr")
				.addJoin("INNER JOIN user_notification_preference np on np.user_id = wr.user_id")
				.addWhereClause("wr.work_id = :workId")
				.addWhereClause("np.notification_type_code = :notificationTypeCode")
				.addWhereClause("(np.sms_flag = true OR np.push_flag = true)")
				.addParam("workId", workId)
				.addParam("notificationTypeCode", notificationTypeCode);
		if (openOnly) {
			builder.addWhereClause("wr.work_resource_status_type_code = 'open'");
		}

		return jdbcTemplate.queryForObject(builder.buildCount("*"), builder.getParams(), Integer.class) > 0;
	}

	@Override
	public List<Long> findAllResourceUserIdsForWorkWithSmsAllowed(Long workId, String notificationTypeCode) {
		return findAllResourcesUserIdsForWorkWithSmsAllowed(workId, notificationTypeCode, "sms_flag");
	}

	@Override
	public List<Long> findAllResourceUserIdsForWorkWithPushAllowed(Long workId, String notificationTypeCode) {
		return findAllResourcesUserIdsForWorkWithSmsAllowed(workId, notificationTypeCode, "push_flag");
	}

	private List<Long> findAllResourcesUserIdsForWorkWithSmsAllowed(Long workId, String notificationTypeCode, String columnFlag) {
		SQLBuilder builder = new SQLBuilder();
		List<Long> resources = Lists.newArrayList();

		builder.addColumn("wr.user_id")
				.addTable("work_resource wr")
				.addJoin("INNER JOIN user_notification_preference np on np.user_id = wr.user_id")
				.addWhereClause("wr.work_id = :workId")
				.addWhereClause("np.notification_type_code = :notificationTypeCode")
				.addWhereClause("np." + columnFlag + " = true")
				.addWhereClause("wr.work_resource_status_type_code = 'open'")
				.addParam("workId", workId)
				.addParam("notificationTypeCode", notificationTypeCode);

		logger.debug(builder.build());
		List<Map<String, Object>> results = jdbcTemplate.queryForList(builder.build(), builder.getParams());
		for (Map<String, Object> row : results) {
			resources.add(((Integer) row.get("user_id")).longValue());
		}
		return resources;
	}

	@Override
	public List<Long> getAllWorkersFromCompanyInvitedToWork(Long companyId, Long workId) {
		return getFactory().getCurrentSession().createCriteria(getEntityClass())
			.setProjection(Projections.property("user.id"))
			.setFetchMode("user", FetchMode.JOIN)
			.createAlias("user", "user")
			.add(Restrictions.ne("workResourceStatusType.code", "declined"))
			.add(Restrictions.eq("work.id", workId))
			.add(Restrictions.eq("user.company.id", companyId))
			.list();
	}

	@Override
	public boolean isAtLeastOneWorkerFromCompanyInvitedToWork(Long companyId, Long workId) {
		SQLBuilder builder = new SQLBuilder()
			.addTable("work_resource wr")
			.addJoin("INNER JOIN user u ON wr.user_id = u.id")
			.addWhereClause("u.company_id", SQLOperator.EQUALS, "company_id", companyId)
			.addWhereClause("wr.work_id", SQLOperator.EQUALS, "work_id", workId)
			.addLimitClause(0, 1, true);

		return jdbcTemplate.queryForObject(builder.buildCount("wr.id"), builder.getParams(), Integer.class) > 0;
	}

	@Override
	public List<Long> getAllDispatcherIdsForWorker(Long workerId) {
		SQLBuilder builder = new SQLBuilder()
			.addColumn("u.id")
			.addTable("user u")
			.addJoin("LEFT JOIN user_acl_role r ON u.id = r.user_id")
			.addWhereClause(
				"u.company_id IN " +
					"(" +
					"SELECT u.company_id " +
					"FROM user u " +
					"WHERE u.id = :workerId " +
					")"
			)
			.addWhereClause("r.acl_role_id", SQLOperator.EQUALS, "dispatcherRoleId", AclRole.ACL_DISPATCHER)
			.addWhereClause("u.id", SQLOperator.NOT_EQUALS, "workerId", Boolean.TRUE)
			.addWhereClause("r.deleted", SQLOperator.EQUALS, "isDeleted", Boolean.FALSE)
			.addParam("workerId", workerId);

		return jdbcTemplate.queryForList(builder.build(), builder.getParams(), Long.class);
	}

	@Override
	public List<Long> getAllDispatcherIdsInCompany(Long companyId) {
		SQLBuilder builder = buildSQLForDispatcherIds()
			.addWhereClause("u.company_id", SQLOperator.EQUALS, "companyId", Boolean.TRUE)
			.addParam("companyId", companyId);

		return jdbcTemplate.queryForList(builder.build(), builder.getParams(), Long.class);
	}

	@Override
	public Long getDispatcherIdForWorkAndWorker(Long workId, Long workerId) {
		SQLBuilder builder = buildSQLForDispatcherIds()
			.addJoin("INNER JOIN work_resource wr ON wr.dispatcher_id = u.id AND wr.work_id = :workId")
			.addWhereClause("wr.user_id", SQLOperator.EQUALS, "workerId", Boolean.TRUE)
			.addWhereClause("u.id", SQLOperator.NOT_EQUALS, "workerId", Boolean.TRUE)
			.addParam("workerId", workerId)
			.addParam("workId", workId);

		List<Long> results = jdbcTemplate.queryForList(builder.build(), builder.getParams(), Long.class);
		if (isEmpty(results)) {
			return null;
		}

		return results.get(0);
	}

	@Override
	public DispatcherDTO getDispatcherForWorkAndWorker(Long workId, Long workerId) {
		SQLBuilder builder = buildSQLForWorkerDispatchers()
			.addJoin("INNER JOIN work_resource wr ON wr.dispatcher_id = u.id AND wr.work_id = :workId")
			.addWhereClause("wr.user_id", SQLOperator.EQUALS, "workerId", Boolean.TRUE)
			.addWhereClause("u.id", SQLOperator.NOT_EQUALS, "workerId", Boolean.TRUE)
			.addParam("workerId", workerId)
			.addParam("workId", workId)
			.addLimitClause(0, 1, true);

		List<DispatcherDTO> results = jdbcTemplate.query(builder.build(), builder.getParams(), new RowMapper<DispatcherDTO>() {
			@Override
			public DispatcherDTO mapRow(ResultSet rs, int i) throws SQLException {
				return new DispatcherDTO()
					.setFirstName(rs.getString("u.first_name"))
					.setLastName(rs.getString("u.last_name"))
					.setWorkPhone(rs.getString("p.work_phone"))
					.setMobilePhone(rs.getString("p.mobile_phone"))
					.setEmail(rs.getString("u.email"));
			}
		});

		if (isEmpty(results)) {
			return null;
		}

		return results.get(0);
	}

	@Override
	public List<Long> getDispatcherIdsForWorkAndWorkers(Long workId, Collection<Long> workerIds) {
		if (isEmpty(workerIds)) {
			return Collections.emptyList();
		}
		SQLBuilder builder = buildSQLForDispatcherIds()
			.addJoin("INNER JOIN work_resource wr ON wr.dispatcher_id = u.id AND wr.work_id = :workId")
			.addWhereClause("wr.user_id IN (:workerIds)")
			.addWhereClause("u.id NOT IN (:workerIds)")
			.addParam("workerIds", workerIds)
			.addParam("workId", workId);

		return jdbcTemplate.queryForList(builder.build(), builder.getParams(), Long.class);
	}

	private SQLBuilder buildSQLForDispatcherIds() {
		return new SQLBuilder()
			.addColumn("DISTINCT u.id")
			.addTable("user u")
			.addJoin("INNER JOIN user_acl_role r ON u.id = r.user_id")
			.addWhereClause("r.acl_role_id", SQLOperator.EQUALS, "dispatcherRoleId", AclRole.ACL_DISPATCHER)
			.addWhereClause("r.deleted", SQLOperator.EQUALS, "isDeleted", Boolean.FALSE);
	}

	private SQLBuilder buildSQLForWorkerDispatchers() {
		return new SQLBuilder()
			.addColumn("DISTINCT u.first_name")
			.addColumn("u.last_name")
			.addColumn("u.email")
			.addColumn("p.work_phone")
			.addColumn("p.mobile_phone")
			.addTable("user u")
			.addJoin("INNER JOIN user_acl_role r ON u.id = r.user_id")
			.addJoin("INNER JOIN profile p ON u.id = p.user_id")
			.addWhereClause("r.acl_role_id", SQLOperator.EQUALS, "dispatcherRoleId", AclRole.ACL_DISPATCHER)
			.addWhereClause("r.deleted", SQLOperator.EQUALS, "isDeleted", Boolean.FALSE);
	}

	@Override
	public void setDispatcherForWorkAndWorker(Long dispatcherId, Long workId, Long workerId) {
		getFactory().getCurrentSession().getNamedQuery("workResource.setDispatcher")
			.setLong("dispatcherId", dispatcherId)
			.setLong("workId", workId)
			.setLong("userId", workerId)
			.executeUpdate();
	}
}
