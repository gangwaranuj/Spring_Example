package com.workmarket.domains.work.dao;

import com.google.common.base.Optional;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

import com.workmarket.configuration.Constants;
import com.workmarket.dao.PaginationAbstractDAO;
import com.workmarket.domains.model.EntityIdPagination;
import com.workmarket.domains.model.Pagination;
import com.workmarket.domains.model.User;
import com.workmarket.domains.model.WorkResource;
import com.workmarket.domains.model.WorkResourceStatusType;
import com.workmarket.domains.model.WorkStatusType;
import com.workmarket.domains.model.invoice.InvoiceStatusType;
import com.workmarket.domains.model.invoice.PaymentFulfillmentStatusType;
import com.workmarket.domains.model.rating.Rating;
import com.workmarket.domains.work.model.Work;
import com.workmarket.domains.work.model.WorkDue;
import com.workmarket.domains.work.model.WorkPagination;
import com.workmarket.domains.work.model.WorkWorkResourceAccountRegister;
import com.workmarket.service.business.dto.BuyerIdentityDTO;
import com.workmarket.service.exception.account.DuplicateWorkNumberException;
import com.workmarket.utility.CollectionUtilities;
import com.workmarket.utility.DateUtilities;
import com.workmarket.utility.HibernateUtilities;
import com.workmarket.utility.StringUtilities;
import com.workmarket.utility.sql.SQLBuilder;
import com.workmarket.utility.sql.SQLOperator;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Criteria;
import org.hibernate.FetchMode;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.criterion.Subqueries;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;

import static com.workmarket.utility.CollectionUtilities.newObjectMap;
import static org.apache.commons.collections.CollectionUtils.isEmpty;
import static org.apache.commons.collections.CollectionUtils.isNotEmpty;

@Repository
public class WorkDAOImpl extends PaginationAbstractDAO<Work> implements WorkDAO {

	private static final int WORK_DELIVERABLE_DUE_THRESHOLD = Constants.WORK_DELIVERABLE_DUE_REMINDER_THRESHOLD_HOURS - Constants.WORK_DELIVERABLE_DUE_GRACE_PERIOD_HOURS;

	private static final Log logger = LogFactory.getLog(WorkDAOImpl.class);

	@Resource(name = "jdbcTemplate")
	private NamedParameterJdbcTemplate jdbcTemplate;

	@Autowired
	@Resource(name = "readOnlyJdbcTemplate")
	private NamedParameterJdbcTemplate readOnlyJdbcTemplate;

	@Override
	protected Class<Work> getEntityClass() {
		return Work.class;
	}

	@SuppressWarnings("unchecked")
	@Override
	public WorkPagination findByWorkResource(Long userId, WorkPagination pagination) {
		// Because we're joining on work resources, we end up getting duplicate
		// rows for a single work item.
		// We could use the following:
		// criteria.setResultTransformer(CriteriaSpecification.DISTINCT_ROOT_ENTITY);
		// But it would kill our desire for pagination.
		// Use a sub-select to get IDs and then pull in associations with outer
		// select.
		// @see
		// http://blog.xebia.com/2008/12/11/sorting-and-pagination-with-hibernate-criteria-how-it-can-go-wrong-with-joins/

		Criteria criteria = getFactory().getCurrentSession().createCriteria(WorkResource.class)
				.setProjection(Projections.distinct(Projections.property("work"))).createAlias("work", "work", Criteria.INNER_JOIN)
				.setFetchMode("work.buyer", FetchMode.JOIN).createAlias("workResourceStatusType", "workResourceStatusType")
				.setFirstResult(pagination.getStartRow()).setMaxResults(pagination.getResultsLimit());

		Criteria count = getFactory().getCurrentSession().createCriteria(WorkResource.class)
				.setProjection(Projections.distinct(Projections.property("work"))).createAlias("work", "work", Criteria.INNER_JOIN)
				.createAlias("workResourceStatusType", "workResourceStatusType").setProjection(Projections.rowCount());

		criteria.add(Restrictions.eq("user.id", userId));
		count.add(Restrictions.eq("user.id", userId));

		criteria.add(Restrictions.eq("work.deleted", false));
		count.add(Restrictions.eq("work.deleted", false));

		applySorts(pagination, criteria, count);

		applyFilters(pagination, criteria, count, true);

		pagination.setResults(criteria.list());
		if (count.list().size() > 0)
			pagination.setRowCount(((Long) count.list().get(0)).intValue());
		else
			pagination.setRowCount(0);
		return pagination;
	}


	@Override
	@SuppressWarnings("unchecked")
	public WorkPagination findWorkByBuyerAndWorkResource(Long buyerId, Long resourceUserId, WorkPagination pagination) {
		Criteria criteria = getFactory().getCurrentSession().createCriteria(getEntityClass())
				.setFetchMode("workResources", FetchMode.JOIN)
				.createAlias("workResources", "workResources", Criteria.INNER_JOIN, Restrictions.eq("assignedToWork", true))
				.setFirstResult(pagination.getStartRow())
				.setMaxResults(pagination.getResultsLimit());

		Criteria count = getFactory().getCurrentSession().createCriteria(getEntityClass())
				.setProjection(Projections.distinct(Projections.property("id")))
				.setFetchMode("workResources", FetchMode.JOIN)
				.createAlias("workResources", "workResources", Criteria.INNER_JOIN, Restrictions.eq("assignedToWork", true))
				.setProjection(Projections.rowCount());

		criteria.add(Restrictions.eq("buyer.id", buyerId));
		count.add(Restrictions.eq("buyer.id", buyerId));

		criteria.add(Restrictions.eq("workResources.user.id", resourceUserId));
		count.add(Restrictions.eq("workResources.user.id", resourceUserId));

		criteria.add(Restrictions.eq("deleted", false));
		count.add(Restrictions.eq("deleted", false));

		applySorts(pagination, criteria, count);
		applyFilters(pagination, criteria, count);

		Long rowCount = HibernateUtilities.getRowCount(count);

		pagination.setRowCount(rowCount);
		pagination.setResults(criteria.list());

		return pagination;
	}

	@Override
	public List<Work> findAllWorkByProject(Long projectId) {
		Assert.notNull(projectId, "Project id is required");
		Criteria criteria = getFactory().getCurrentSession().createCriteria(getEntityClass());
		criteria.setFetchMode("project", FetchMode.JOIN);
		criteria.add(Restrictions.eq("project.id", projectId));
		return criteria.list();
	}

	@Override
	public List<Work> findAllWorkByProjectByStatus(Long projectId, String... status) {
		Assert.notNull(projectId, "Project id is required");
		Assert.notNull(status, "Status is required");
		Criteria criteria = getFactory().getCurrentSession().createCriteria(getEntityClass());
		criteria.setFetchMode("project", FetchMode.JOIN);
		criteria.add(Restrictions.in("workStatusType.code", status));
		criteria.add(Restrictions.eq("project.id", projectId));
		return criteria.list();

	}

	@Override
	@SuppressWarnings("unchecked")
	public Work findWorkByWorkNumber(String workNumber) {
		Criteria criteria = getFactory().getCurrentSession().createCriteria(getEntityClass());
		criteria.add(Restrictions.eq("workNumber", workNumber))
				.setMaxResults(1);

		return (Work) criteria.uniqueResult();
	}

	@Override
	public boolean isWorkStatusForWorkByWorkNumber(String workNumber, String status) {
		Criteria criteria = getFactory().getCurrentSession().createCriteria(getEntityClass());
		criteria.add(Restrictions.eq("workNumber", workNumber));
		criteria.add(Restrictions.eq("workStatusType.code", status));
		criteria.add(Restrictions.eq("deleted", false));
		criteria.setProjection(Projections.rowCount());

		return (((Long) criteria.uniqueResult()).intValue() == 1);
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Work> findWorkByWorkNumber(Collection<String> workNumbers) {
		if(CollectionUtils.isEmpty(workNumbers)) {
			return Collections.EMPTY_LIST;
		}
		return getFactory().getCurrentSession().createCriteria(getEntityClass())
				.add(Restrictions.in("workNumber", workNumbers))
				.list();
	}

	@Override
	public Integer countAllActiveWork() {
		Query query = getFactory().getCurrentSession().getNamedQuery("work.countActive");
		return ((Long) query.uniqueResult()).intValue();
	}

	@Override
	public Work findWorkById(Long workId) {
		Assert.notNull(workId, "Work id is required");
		return (Work) getFactory().getCurrentSession().createCriteria(getEntityClass())
				.setFetchMode("company", FetchMode.JOIN)
				.add(Restrictions.eq("id", workId)).uniqueResult();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Work> findWorksByIds(List<Long> workIds) {
		return (List<Work>) getFactory().getCurrentSession().createCriteria(getEntityClass())
				.setFetchMode("company", FetchMode.JOIN)
				.add(Restrictions.in("id", workIds))
				.list();
	}

	@SuppressWarnings("unchecked")
	@Override
	public boolean doesWorkerHaveWorkWithCompany(Long companyId, Long contractorUserId, List<String> statusCodes) {
		Assert.notNull(companyId);
		Assert.notNull(contractorUserId);

		final String sql =
				  " SELECT if(count(*) > 0, 1, 0) "
				+ " FROM work_resource wr "
				+ " INNER JOIN work w "
				+ " ON w.id = wr.work_id "
				+ " WHERE w.company_id = :companyId and w.type = :type "
				+ " AND w.work_status_type_code not in (:status) "
				+ " AND wr.user_id = :workerId and wr.assigned_to_work = true "
				+ " AND wr.work_resource_status_type_code = :workResourceStatusType ";

		MapSqlParameterSource params = new MapSqlParameterSource();
		params.addValue("companyId", companyId);
		params.addValue("workerId", contractorUserId);
		params.addValue("status", statusCodes);
		params.addValue("workResourceStatusType", WorkResourceStatusType.ACTIVE);
		params.addValue("type", "W"); // only count work, exclude bundle parent.

		return jdbcTemplate.queryForObject(sql, params, Boolean.class);

	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Work> findAllWorkWhereWorkStatusTypeNotInAndWorkSubStatusTypeIn(long companyId, long subStatusId, String[] statusCodes) {
		return getFactory().getCurrentSession()
			.getNamedQuery("work.findAllWorkWhereWorkStatusTypeNotInAndWorkSubStatusTypeIn")
			.setParameterList("statusCodes", statusCodes)
			.setParameter("subStatusId", subStatusId)
			.setParameter("companyId", companyId)
			.list();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Work> findAllWorkWhereTemplatesNotInAndWorkSubStatusTypeIn(long companyId, long subStatusId, Long[] templateIds) {
		return getFactory().getCurrentSession()
			.getNamedQuery("work.findAllWorkWhereTemplatesNotInAndWorkSubStatusTypeIn")
			.setParameterList("templateIds", templateIds)
			.setParameter("subStatusId", subStatusId)
			.setParameter("companyId", companyId)
			.list();
	}

	private String getSortColumn(String sortColumn) {
		String sort = "id";

		if (sortColumn.equals(WorkPagination.SORTS.TITLE.toString())) {
			sort = "title";
		} else if (sortColumn.equals(WorkPagination.SORTS.CITY.toString())) {
			sort = "address.city";
		} else if (sortColumn.equals(WorkPagination.SORTS.CLIENT_COMPANY_NAME.toString())) {
			sort = "clientCompany.name";
		} else if (sortColumn.equals(WorkPagination.SORTS.CREATED_DATE.toString())) {
			sort = "createdOn";
		} else if (sortColumn.equals(WorkPagination.SORTS.PRICE_FEE.toString())) {
			sort = "priceFee";
		} else if (sortColumn.equals(WorkPagination.SORTS.RESOURCE_NAME.toString())) {
			sort = "createdOn";
		} else if (sortColumn.equals(WorkPagination.SORTS.SCHEDULE_FROM.toString())) {
			sort = "scheduleFrom";
		} else if (sortColumn.equals(WorkPagination.SORTS.SCHEDULE_THROUGH.toString())) {
			sort = "scheduleThrough";
		} else if (sortColumn.equals(WorkPagination.SORTS.WORK_STATUS.toString())) {
			sort = "workStatusType.code";
		} else if (sortColumn.equals(WorkPagination.SORTS.RESOURCE_NAME.toString())) {
			sort = "resources.name";
		}

		return sort;
	}

	@SuppressWarnings("unchecked")
	@Override
	public WorkPagination findAllWorkPendingRatingByResource(final Long userId, WorkPagination pagination) {
		DetachedCriteria subcriteria = DetachedCriteria.forClass(Rating.class)
				.setProjection(Projections.distinct(Projections.property("work.id"))).add(Restrictions.eq("ratingUser.id", userId))
				.add(Restrictions.eq("deleted", false));

		Criteria criteria = getFactory().getCurrentSession().createCriteria(getEntityClass())
				.setFetchMode("buyer", FetchMode.JOIN)
				.setFetchMode("company", FetchMode.JOIN)
				.setFetchMode("invoice", FetchMode.JOIN)
				.createAlias("invoice", "invoice")
				.createAlias("workResources", "resource", Criteria.INNER_JOIN, Restrictions.eq("resource.assignedToWork", true))
				.add(Restrictions.eq("resource.user.id", userId))
				.add(Restrictions.eq("deleted", false))
				.add(Restrictions.in("workStatusType.code", WorkStatusType.CLOSED_WORK_STATUS_FOR_PENDING_RATING))
				.setFirstResult(pagination.getStartRow()).setMaxResults(pagination.getResultsLimit());

		Criteria count = getFactory().getCurrentSession().createCriteria(getEntityClass())
				.setProjection(Projections.distinct(Projections.property("id"))).setProjection(Projections.rowCount())
				.setFetchMode("invoice", FetchMode.JOIN)
				.createAlias("invoice", "invoice")
				.createAlias("workResources", "resource", Criteria.INNER_JOIN, Restrictions.eq("resource.assignedToWork", true))
				.add(Restrictions.eq("resource.user.id", userId))
				.add(Restrictions.eq("deleted", false))
				.add(Restrictions.in("workStatusType.code", WorkStatusType.CLOSED_WORK_STATUS_FOR_PENDING_RATING));

		criteria.add(Subqueries.propertyNotIn("id", subcriteria));
		count.add(Subqueries.propertyNotIn("id", subcriteria));

		if (pagination.hasFilter(WorkPagination.FILTER_KEYS.WORK_STATUS)) {
			String workStatus = pagination.getFilter(WorkPagination.FILTER_KEYS.WORK_STATUS);
			criteria.add(Restrictions.eq("workStatusType.code", workStatus));
			count.add(Restrictions.eq("workStatusType.code", workStatus));
		}

		if (pagination.hasFilter(WorkPagination.FILTER_KEYS.PAYMENT_DATE)) {
			Calendar paymentDate = DateUtilities.getCalendarFromISO8601(
					pagination.getFilter(WorkPagination.FILTER_KEYS.PAYMENT_DATE));
			criteria.add(Restrictions.ge("invoice.paymentDate", paymentDate));
			count.add(Restrictions.ge("invoice.paymentDate", paymentDate));
		}

		if (pagination.hasFilter(WorkPagination.FILTER_KEYS.FROM_DATE)) {
			Calendar fromDate = DateUtilities.getCalendarFromMillis(new Long(pagination.getFilter(WorkPagination.FILTER_KEYS.FROM_DATE)));
			criteria.add(Restrictions.ge("schedule.from", fromDate));
			count.add(Restrictions.ge("schedule.from", fromDate));
		}


		pagination.setResults(criteria.list());
		pagination.setRowCount(isNotEmpty(count.list()) ? ((Long) CollectionUtilities.first(count.list())).intValue() : 0);

		return pagination;
	}

	@SuppressWarnings("unchecked")
	@Override
	public Set<WorkDue> findAllDueAssignmentsByDueDate(Calendar dueDateFrom, Calendar dueDateThrough) {
		Assert.notNull(dueDateFrom);
		Assert.notNull(dueDateThrough);
		return Sets.newHashSet(findAllDueAssignments(dueDateFrom, dueDateThrough));
	}

	@Override
	public Set<WorkDue> findAllAssignmentsPastDue(Calendar dueDate) {
		Assert.notNull(dueDate);
		return Sets.newHashSet(findAllDueAssignments(null, dueDate));
	}

	@Override
	public int countWorkByCompanyUserRangeAndStatus(Long companyId, Long userId, List<Long> excludeIds, Calendar fromDate, Calendar toDate, List<String> statuses) {
		Assert.notNull(companyId);
		Assert.notNull(userId);
		Assert.notNull(statuses);
		Criteria criteria = getFactory().getCurrentSession().createCriteria(getEntityClass())
				.setFetchMode("workResources", FetchMode.JOIN)
				.createAlias("workResources", "workResources", Criteria.INNER_JOIN, Restrictions.eq("assignedToWork", true))
				.add(Restrictions.in("workStatusType.code", statuses))
				.add(Restrictions.eq("company.id", companyId))
				.add(Restrictions.eq("workResources.user.id", userId))
				.setProjection(Projections.rowCount());

		if (fromDate != null) {
			criteria.add(Restrictions.ge("schedule.from", fromDate));
		}
		if (toDate != null) {
			criteria.add(Restrictions.le("schedule.from", toDate));
		}
		if(excludeIds != null) {
			criteria.add(Restrictions.not(Restrictions.in("id", excludeIds)));
		}

		return ((Long) criteria.uniqueResult()).intValue();
	}

	@Override
	public int countWorkByCompanyByStatus(Long companyId, List<String> statuses) {
		Assert.notNull(companyId);
		Criteria criteria = getFactory().getCurrentSession().createCriteria(getEntityClass())
				.add(Restrictions.in("workStatusType.code", statuses))
				.add(Restrictions.eq("company.id", companyId))
				.setProjection(Projections.rowCount());

		return ((Long) criteria.uniqueResult()).intValue();
	}

	private List<WorkDue> findAllDueAssignments(Calendar dueDateFrom, Calendar dueDateThrough) {
		SQLBuilder builder = new SQLBuilder();
		builder.addColumns("work.id as workId", "work.work_number as workNumber", "work.title as workTitle",
				"work.due_on", "company.id as companyId", "work.invoice_id", "company.statements_enabled", "work.buyer_user_id",
				"company.company_status_type_code", "work.buyer_total_cost")
				.addTable("work")
				.addJoin("INNER JOIN company on company.id = work.company_id")
				.addWhereClause("work.type='W'")
				.addWhereClause("work.work_status_type_code in ('paymentPending' , 'cancelledPayPending')");

		if (dueDateFrom != null) {
			builder.addWhereClause("work.due_on >= :dueDateFrom")
					.addParam("dueDateFrom", dueDateFrom);
		}
		if (dueDateThrough != null) {
			builder.addWhereClause("work.due_on <= :dueDateThrough")
					.addParam("dueDateThrough", dueDateThrough);
		}

		return jdbcTemplate.query(builder.build(), builder.getParams(), new RowMapper<WorkDue>() {

			@Override
			public WorkDue mapRow(ResultSet rs, int rowNum) throws SQLException {
				WorkDue row = new WorkDue();
				row.setCompanyId(rs.getLong("companyId"));
				row.setWorkNumber(rs.getString("workNumber"));
				row.setWorkTitle(rs.getString("workTitle"));
				row.setDueOn(DateUtilities.getCalendarFromDate(rs.getTimestamp("due_on")));
				row.setWorkId(rs.getLong("workId"));
				row.setStatementsEnabled(rs.getBoolean("statements_enabled"));
				row.setInvoiceId(rs.getLong("invoice_id"));
				row.setBuyerUserId(rs.getLong("buyer_user_id"));
				row.setCompanyStatusTypeCode(rs.getString("company_status_type_code"));
				row.setBuyerTotalCost(rs.getBigDecimal("buyer_total_cost"));
				return row;
			}
		});
	}

	@Override
	public Integer countAllAssignmentsPaymentPendingByCompany(Long companyId) {
		Assert.notNull(companyId);
		Criteria criteria = getFactory().getCurrentSession().createCriteria(getEntityClass())
				.add(Restrictions.in("workStatusType.code", WorkStatusType.PAYMENT_PENDING_STATUS_TYPES))
				.add(Restrictions.eq("company.id", companyId))
				.setProjection(Projections.rowCount());

		return ((Long) criteria.uniqueResult()).intValue();
	}

	@Override
	public Integer countAllDueWorkByCompany(Long companyId) {
		Calendar date = DateUtilities.getCalendarNow();

		Criteria criteria = getFactory().getCurrentSession().createCriteria(getEntityClass())
				.add(Restrictions.in("workStatusType.code", WorkStatusType.PAYMENT_PENDING_STATUS_TYPES))
				.add(Restrictions.le("dueOn", date))
				.add(Restrictions.eq("company.id", companyId))
				.setProjection(Projections.rowCount());

		return ((Long) criteria.uniqueResult()).intValue();
	}

	@Override
	public Integer countAllDueWorkByCompany(Calendar dueDateFrom, Calendar dueDateThrough, Long companyId) {

		Criteria criteria = getFactory().getCurrentSession().createCriteria(getEntityClass())
				.add(Restrictions.in("workStatusType.code", WorkStatusType.PAYMENT_PENDING_STATUS_TYPES))
				.add(Restrictions.le("dueOn", dueDateThrough))
				.add(Restrictions.ge("dueOn", dueDateFrom))
				.add(Restrictions.eq("company.id", companyId))
				.setProjection(Projections.rowCount());

		return ((Long) criteria.uniqueResult()).intValue();
	}

	@Override
	public void applySorts(Pagination<Work> pagination, Criteria query, Criteria count) {
		String sort;
		if (pagination.getSortColumn() != null) {
			sort = getSortColumn(pagination.getSortColumn());

			if (pagination.getSortDirection().equals(Pagination.SORT_DIRECTION.DESC)) {
				query.addOrder(Order.desc(sort));
			} else {
				query.addOrder(Order.asc(sort));
			}
		}
	}

	public void applyFilters(Pagination<Work> pagination, Criteria query, Criteria count, boolean resource) {
		if (pagination.getFilters() == null)
			return;

		String workScheduleFrom = resource ? "work.scheduleFrom" : "scheduleFrom";
		String workTitle = resource ? "work.title" : "title";
		String workDescription = resource ? "work.description" : "description";
		String workStatus = resource ? "work.workStatusType.code" : "workStatusType.code";

		if (pagination.getFilter(WorkPagination.FILTER_KEYS.WORK_STATUS) != null) {

			String statusCode = pagination.getFilter(WorkPagination.FILTER_KEYS.WORK_STATUS);

			switch (statusCode) {
				case WorkStatusType.ACTIVE:
					query.add(Restrictions.ge(workScheduleFrom, DateUtilities.getCalendarNow())).add(
							Restrictions.eq(workStatus, WorkStatusType.ACTIVE));
					count.add(Restrictions.ge(workScheduleFrom, DateUtilities.getCalendarNow())).add(
							Restrictions.eq(workStatus, WorkStatusType.ACTIVE));

					break;
				case WorkStatusType.INPROGRESS:
					query.add(Restrictions.le(workScheduleFrom, DateUtilities.getCalendarNow())).add(
							Restrictions.eq(workStatus, WorkStatusType.ACTIVE));
					count.add(Restrictions.le(workScheduleFrom, DateUtilities.getCalendarNow())).add(
							Restrictions.eq(workStatus, WorkStatusType.ACTIVE));

					break;
				case WorkStatusType.CLOSED:
					query.add(Restrictions.in(workStatus, WorkStatusType.CLOSED_WORK_STATUS_TYPES));
					count.add(Restrictions.in(workStatus, WorkStatusType.CLOSED_WORK_STATUS_TYPES));

					break;
				default:
					query.add(Restrictions.eq(workStatus, statusCode));
					count.add(Restrictions.eq(workStatus, statusCode));
					break;
			}
		}

		if (pagination.getFilter(WorkPagination.FILTER_KEYS.WORK_RESOURCE_STATUS) != null) {

			String statusCode = pagination.getFilter(WorkPagination.FILTER_KEYS.WORK_RESOURCE_STATUS);

			query.add(Restrictions.eq("workResourceStatusType.code", statusCode));
			count.add(Restrictions.eq("workResourceStatusType.code", statusCode));
		}

		if (pagination.getFilter(WorkPagination.FILTER_KEYS.KEYWORD) != null) {

			String keyword = pagination.getFilter(WorkPagination.FILTER_KEYS.KEYWORD);

			query.add(Restrictions.or(Restrictions.ilike(workTitle, keyword, MatchMode.ANYWHERE),
					Restrictions.ilike(workDescription, keyword, MatchMode.ANYWHERE)));
			query.add(Restrictions.or(Restrictions.ilike(workTitle, keyword, MatchMode.ANYWHERE),
					Restrictions.ilike(workDescription, keyword, MatchMode.ANYWHERE)));
		}

		if (pagination.getFilter(WorkPagination.FILTER_KEYS.FROM_DATE) != null) {

			String from_date = pagination.getFilter(WorkPagination.FILTER_KEYS.FROM_DATE);

			query.add(Restrictions.ge(workScheduleFrom, DateUtilities.getCalendarFromISO8601(from_date)));
			count.add(Restrictions.ge(workScheduleFrom, DateUtilities.getCalendarFromISO8601(from_date)));
		}

		if (pagination.getFilter(WorkPagination.FILTER_KEYS.THROUGH_DATE) != null) {

			String through_date = pagination.getFilter(WorkPagination.FILTER_KEYS.THROUGH_DATE);

			query.add(Restrictions.le(workScheduleFrom, DateUtilities.getCalendarFromISO8601(through_date)));
			count.add(Restrictions.le(workScheduleFrom, DateUtilities.getCalendarFromISO8601(through_date)));
		}

	}

	@Override
	public void applyFilters(Pagination<Work> pagination, Criteria query, Criteria count) {
		applyFilters(pagination, query, count, false);
	}

	@Override
	public void buildWhereClause(Criteria query, Criteria count, Map<String, Object> params) {
	}

	@Override
	@SuppressWarnings("unchecked")
	public List<Integer> getAutoPayWorkIds(Date dueOn, List<String> workStatusTypes) {
		if (workStatusTypes.size() == 0) {
			return Lists.newArrayList();
		}
		String sql = "SELECT w.id FROM company c, work w " +
				" WHERE c.id = w.company_id " +
				" AND c.auto_pay_enabled = true " +
				" AND w.type = 'W' AND w.deleted = 0" +
				" AND w.work_status_type_code IN (" + StringUtils.join(StringUtilities.surround(workStatusTypes, "'"), ",") + ")" +
				" AND w.due_on <= :dueOn " +
				" AND NOT EXISTS (SELECT invoice.id FROM invoice " +
				" INNER JOIN invoice_summary_detail ON invoice.id = invoice_summary_detail.invoice_id " +
				" INNER JOIN invoice bundle ON bundle.id = invoice_summary_detail.invoice_summary_id " +
				" WHERE invoice.deleted = 0 AND bundle.deleted = 0 AND w.invoice_id = invoice.id)";
		SQLQuery sqlQuery = getFactory().getCurrentSession().createSQLQuery(sql);
		sqlQuery.setTimestamp("dueOn", dueOn);
		return (List<Integer>) sqlQuery.list();
	}

	@Override
	public Long getBuyerIdByWorkId(Long workId) throws DuplicateWorkNumberException {
		return this.findWorkById(workId).getBuyer().getId();
	}

	@Override
	public Long getBuyerIdByWorkNumber(String workNumber) throws DuplicateWorkNumberException {
		return this.findWorkByWorkNumber(workNumber).getBuyer().getId();
	}

	@Override
	@SuppressWarnings("unchecked")
	public List<BuyerIdentityDTO> findBuyerIdentitiesByWorkIds(Collection<Long> workIds) {
		if (isEmpty(workIds)) {
			return Lists.newArrayList();
		}

		SQLBuilder builder = new SQLBuilder()
				.addColumns("work.id", "work.company_id", "company.uuid", "company.company_number")
				.addTable("work")
				.addJoin("JOIN company on company.id = work.company_id")
				.addWhereClause("work.id IN (:workIds)")
				.addParam("workIds", workIds);

		return jdbcTemplate.query(builder.build(), builder.getParams(), new WorkCompanyRowMapper());
	}

	@Override
	public Long findBuyerCompanyIdByWorkId(final long workId) {
		final SQLBuilder sqlBuilder = new SQLBuilder();

		sqlBuilder
			.addColumn("company_id")
			.addTable("work")
			.addWhereClause("id = :workId")
			.addParam("workId", workId);

		return readOnlyJdbcTemplate.queryForObject(sqlBuilder.build(), sqlBuilder.getParams(), Long.class);
	}

	@Override
	public List<Long> findAllWorkIdsByUUIDs(final List<String> workUUIDs) {
		if (isEmpty(workUUIDs)) {
			return Lists.newArrayList();
		}

		final SQLBuilder sqlBuilder = new SQLBuilder();

		sqlBuilder
			.addColumn("id")
			.addTable("work")
			.addWhereClause("uuid IN (:workUUIDs)")
			.addParam("workUUIDs", workUUIDs);

		return readOnlyJdbcTemplate.queryForList(sqlBuilder.build(), sqlBuilder.getParams(), Long.class);
	}

	@SuppressWarnings("unchecked")
	public List<Integer> findAssignmentsMissingResourceNoShow() {

		String sql = "SELECT id from (SELECT " +
				"work.id, (work.checkin_required_flag OR work.checkin_call_required) checkingRequired, " +
				"DATE_ADD(IF (work.schedule_through is null, work.schedule_from, " +
				"work.schedule_through), INTERVAL " + Constants.WORK_RESOURCE_CHECKIN_GRACE_PERIOD_MINUTES + " MINUTE) AS requiredCheckinTime " +
				"FROM 	work " +
				"WHERE 	work.type = 'W' " +
				"AND 	work.work_status_type_code = 'active' " +
				"HAVING checkingRequired = true " +
				"AND    requiredCheckinTime > date_sub(now(), INTERVAL 6 HOUR) " +
				"AND    requiredCheckinTime < date_sub(now(), INTERVAL 2 MINUTE) " +
				"AND NOT EXISTS " +
				"      		(SELECT work_id FROM work_sub_status_type_association " +
				" 	   		INNER 	JOIN work_sub_status_type  " +
						"			ON 		work_sub_status_type.id = work_sub_status_type_association.work_sub_status_type_id " +
						"			WHERE 	work_id = work.id " +
				"					AND 	work_sub_status_type.code = 'resource_no_show') " +
				"AND NOT EXISTS " +
				"			(SELECT work_resource_time_tracking.id FROM work_resource_time_tracking \n" +
				" 			INNER JOIN work_resource ON work_resource_time_tracking.work_resource_id = work_resource.id \n" +
				"				WHERE work_resource.work_resource_status_type_code = 'active' AND work_resource.work_id = work.id)) as inner_query ";

		return readOnlyJdbcTemplate.queryForList(sql, new MapSqlParameterSource(), Integer.class);
	}

	@Override
	@SuppressWarnings("unchecked")
	public List<String> findAssignmentsWithDeliverablesDue() {

		Calendar now = DateUtilities.getCalendarNow();

		SQLBuilder subQuery = new SQLBuilder();
		subQuery
				.addColumns("w.work_number", "DATE_ADD(IF (w.schedule_through is null, w.schedule_from, w.schedule_through), INTERVAL (drg.hours_to_complete + " + Constants.WORK_DELIVERABLE_DUE_GRACE_PERIOD_HOURS + ") HOUR) AS deliverableDeadline")
				.addTable("work w")
				.addJoin("INNER JOIN deliverable_requirement_group drg ON w.deliverable_requirement_group_id = drg.id")
				.addWhereClause("w.type = 'W'")
				.addWhereClause("w.work_status_type_code = 'active'")
				.addWhereClause("drg.deadline_active = 1");

		SQLBuilder builder = new SQLBuilder();

		builder
				.addColumn("work_number")
				.addTable("(" + subQuery.build() + ") as inner_query")
				.addWhereClause("inner_query.deliverableDeadline >= date_sub(:nowTimestamp, INTERVAL 6 HOUR)")
				.addWhereClause("inner_query.deliverableDeadline <= :nowTimestamp")
				.addParam("nowTimestamp", now);

		return jdbcTemplate.queryForList(builder.build(), builder.getParams(), String.class);
	}

	@Override
	@SuppressWarnings("unchecked")
	public List<Work> findAssignmentsRequiringDeliverableDueReminder() {

		Calendar now = DateUtilities.getCalendarNow();


		SQLBuilder subQuery = new SQLBuilder();
		subQuery
				.addColumns("w.work_number", "w.id", "DATE_ADD(IF (w.schedule_through is null, w.schedule_from, w.schedule_through), INTERVAL (drg.hours_to_complete - " + WORK_DELIVERABLE_DUE_THRESHOLD + ") HOUR) AS reminderTime")
				.addTable("work w")
				.addJoin("INNER JOIN deliverable_requirement_group drg ON w.deliverable_requirement_group_id = drg.id")
				.addWhereClause("w.type = 'W'")
				.addWhereClause("w.work_status_type_code = 'active'")
				.addWhereClause("drg.deadline_active = 1")
				.addWhereClause("drg.reminder_sent = 0");

		SQLBuilder builder = new SQLBuilder();

		builder
				.addColumns("work_number", "id")
				.addTable("(" + subQuery.build() + ") as inner_query")
				.addWhereClause("inner_query.reminderTime >= date_sub(:nowTimestamp, INTERVAL " + WORK_DELIVERABLE_DUE_THRESHOLD + " HOUR)")
				.addWhereClause("inner_query.reminderTime <= :nowTimestamp")
				.addParam("nowTimestamp", now);

		return jdbcTemplate.query(builder.build(), builder.getParams(), new RowMapper<Work>() {
			@Override
			public Work mapRow(ResultSet rs, int rowNum) throws SQLException {
				Work w = new Work();
				w.setWorkNumber(rs.getString("work_number"));
				w.setId(rs.getLong("id"));
				return w;
			}
		});
	}

	@Override
	public List<Long> findWorkIdsByInvoiceId(Long... invoiceIds) {
		List<Long> ids = Lists.newArrayList();
		if (invoiceIds == null || invoiceIds.length == 0) {
			return ids;
		}
		String sql = "SELECT work.id FROM work " +
				" INNER JOIN invoice ON invoice.id = work.invoice_id " +
				" WHERE work.deleted = 0 AND work.type = 'W' " +
				" AND invoice.deleted = 0 AND invoice.id IN (" + StringUtils.join(invoiceIds, ",") + ") ";
		ids = this.jdbcTemplate.query(sql, new MapSqlParameterSource(), new RowMapper<Long>() {
			public Long mapRow(ResultSet rs, int rowNum) throws SQLException {
				return rs.getLong("id");
			}
		});
		return ids;
	}

	@Override
	public List<Long> findOpenWorkIdsBetweenUserAndCompany(Long userId, Long companyId) {
		List<Long> workIds = Lists.newArrayList();

		SQLBuilder builder = new SQLBuilder()
				.addColumn("w.id")
				.addTable("work w")
				.addJoin("INNER JOIN work_resource wr ON w.id=wr.work_id")
				.addWhereClause("w.company_id", SQLOperator.EQUALS, "company_id", companyId)
				.addWhereClause("w.work_status_type_code", SQLOperator.EQUALS, "workStatusCode", WorkStatusType.SENT)
				.addWhereClause("wr.work_resource_status_type_code", SQLOperator.EQUALS, "work_resource_status_type_code", "open")
				.addWhereClause("wr.user_id", SQLOperator.EQUALS, "user_id", userId);

		List<Map<String, Object>> results = jdbcTemplate.queryForList(builder.build(), builder.getParams());
		for (Map<String, Object> row : results) {
			workIds.add(((Integer) row.get("id")).longValue());
		}
		return workIds;
	}

	@Override
	public List<WorkWorkResourceAccountRegister> findWorkAndWorkResourceForPayment(List<Long> assignmentIds) {
		SQLBuilder builder = new SQLBuilder();
		builder.addColumns("work.id AS workId", "work.company_id", "work_resource.user_id",
				"work_resource.id AS workResourceId", "account_register.id AS buyerAccountRegisterId",
				"resourceAccountRegister.id AS resourceAccountRegisterId", "work_resource.delegator_id",
				"delegator.id AS delegatorUserId", "work_resource.hours_worked", "work_resource.units_processed",
				"work.override_price", "work_resource.additional_expenses", "work_resource.bonus", "work.pricing_strategy_type")
				.addTable("work")
				.addJoin("inner join work_resource on work.id = work_resource.work_id ")
				.addJoin("inner join user on user.id = work_resource.user_id ")
				.addJoin("inner join account_register on account_register.company_id = work.company_id ")
				.addJoin("inner join account_register resourceAccountRegister on resourceAccountRegister.company_id = user.company_id ")
				.addJoin("left 	join user delegator on work_resource.delegator_id = delegator.id ")
				.addWhereClause("work_resource.work_resource_status_type_code = 'active'")
				.addWhereClause("work_resource.assigned_to_work = true")
				.addWhereClause("work.deleted = 0 ")
				.addWhereClause("work.type = 'W'")
				.addWhereClause("work.work_status_type_code IN ('paymentPending', 'closed', 'cancelledPayPending') ")
				.addWhereClause("work.id in (" + StringUtils.join(assignmentIds, ",") + ")");

		logger.debug(builder.build());
		return this.jdbcTemplate.query(builder.build(), new MapSqlParameterSource(), new RowMapper<WorkWorkResourceAccountRegister>() {
			@Override
			public WorkWorkResourceAccountRegister mapRow(ResultSet rs, int rowNum) throws SQLException {
				WorkWorkResourceAccountRegister ww = new WorkWorkResourceAccountRegister();
				ww.setWorkId(rs.getLong("workId"));
				ww.setCompanyId(rs.getLong("company_id"));
				ww.setBuyerAccountRegisterId(rs.getLong("buyerAccountRegisterId"));
				ww.setResourceAccountRegisterId(rs.getLong("resourceAccountRegisterId"));
				ww.setWorkResourceDelegatorId(rs.getLong("delegator_id"));
				ww.setWorkResourceId(rs.getLong("workResourceId"));
				ww.setWorkResourceUserId(rs.getLong("user_id"));
				ww.setWorkResourceDelegatorUserId(rs.getLong("delegatorUserId"));
				ww.setAdditionalExpenses(rs.getBigDecimal("additional_expenses"));
				ww.setBonus(rs.getBigDecimal("bonus"));
				ww.setHoursWorked(rs.getBigDecimal("hours_worked"));
				ww.setOverridePrice(rs.getBigDecimal("override_price"));
				ww.setPricingStrategyType(rs.getString("pricing_strategy_type"));
				return ww;
			}
		});
	}

	@Override
	public boolean isWorkPendingFulfillment(Long workId) {
		SQLBuilder builder = new SQLBuilder();
		builder.addColumn("IF( (invoice.invoice_status_type_code = :paid AND invoice.payment_fulfillment_status_type_code = :pendingFulfillment) " +
				" OR (bundle.invoice_status_type_code = :paid AND bundle.payment_fulfillment_status_type_code = :pendingFulfillment), 1, 0) AS isPendingFulfillment")
				.addTable("work")
				.addJoin("inner join invoice on work.invoice_id = invoice.id and invoice.deleted = false")
				.addJoin("left join invoice_summary_detail on invoice_summary_detail.invoice_id = invoice.id")
				.addJoin("left join invoice bundle on invoice_summary_detail.invoice_summary_id = bundle.id and bundle.deleted = false")
				.addWhereClause("work.id = :workId")
				.addWhereClause("work.deleted = false")
				.addParam("workId", workId)
				.addParam("paid", InvoiceStatusType.PAID)
				.addParam("pendingFulfillment", PaymentFulfillmentStatusType.PENDING_FULFILLMENT);
		logger.debug(builder.toString());

		try {

			return jdbcTemplate.queryForObject(
				"select sum(x.isPendingFulfillment) > 0 from ( " + builder.build() + ") x",
				builder.getParams(),
				Boolean.class
			);
		} catch (EmptyResultDataAccessException e) {
			return false;
		}
	}

	@Override
	public boolean doesClientCompanyHaveActiveAssignments(long clientCompanyId) {
		SQLBuilder builder = new SQLBuilder();
		builder.addColumn("IF (count(*) > 0, 1, 0) as hasActiveAssignments")
				.addTable("work")
				.addWhereClause("client_company_id = :clientCompanyId")
				.addWhereClause("deleted = false")
				.addWhereClause("work_status_type_code IN (:status1, :status2, :status3, :status4)")
				.addParam("clientCompanyId", clientCompanyId)
				.addParam("status1", WorkStatusType.SENT)
				.addParam("status2", WorkStatusType.ACTIVE)
				.addParam("status3", WorkStatusType.COMPLETE)
				.addParam("status4", WorkStatusType.PAYMENT_PENDING);

		logger.debug(builder.toString());
		try {
			return jdbcTemplate.queryForObject(builder.build(), builder.getParams(), Boolean.class);
		} catch (EmptyResultDataAccessException e) {
			return false;
		}
	}

	@Override
	public Integer getMaxAssignmentId() {
		SQLBuilder builder = new SQLBuilder();
		builder.addColumn("MAX(work.id) as workId")
				.addTable("work");
		try {
			return jdbcTemplate.queryForObject(builder.build(), builder.getParams(), Long.class).intValue();
		} catch (EmptyResultDataAccessException e) {
			return 0;
		}
	}

	@Override
	public List<Long> findWorkIdsForBuyer(final Long buyerId, final String... workStatusTypes) {
		SQLBuilder builder = new SQLBuilder();

		builder.addColumn("id")
				.addTable("work")
				.addWhereClause("buyer_user_id = :buyerId").addParam("buyerId", buyerId)
				.addWhereInClause("work_status_type_code", "workStatusType", Arrays.asList(workStatusTypes));

		Map<String, Object> paramMap = newObjectMap(
				"buyerId", buyerId,
				"workStatusType", Arrays.asList(workStatusTypes));

		return jdbcTemplate.queryForList(builder.build(), paramMap, Long.class);
	}

	public int updateWorkBuyerUserId(final Long newBuyerUserId, final List<Long> workIds, final List<String> workStatusCodes) {
		Assert.notNull(newBuyerUserId);

		if (CollectionUtils.isEmpty(workIds)) return 0;

		String sql = "update work set buyer_user_id = :buyerUserId where id in (:workIds) and work_status_type_code in (:workStatusCodes)";

		Map<String, Object> paramMap = newObjectMap(
				"buyerUserId", newBuyerUserId,
				"workIds", workIds,
				"workStatusCodes", workStatusCodes);

		return jdbcTemplate.update(sql, paramMap);
	}

	@Override
	public boolean hasWorkPendingRatingByResource(Long userId) {
		DetachedCriteria subcriteria = DetachedCriteria.forClass(Rating.class)
				.setProjection(Projections.distinct(Projections.property("work.id")))
				.add(Restrictions.eq("ratingUser.id", userId))
				.add(Restrictions.eq("deleted", false));

		Criteria count = getFactory().getCurrentSession().createCriteria(getEntityClass())
				.setProjection(Projections.distinct(Projections.property("id"))).setProjection(Projections.rowCount())
				.setFetchMode("invoice", FetchMode.JOIN)
				.createAlias("invoice", "invoice")
				.createAlias("workResources", "resource", Criteria.INNER_JOIN, Restrictions.eq("resource.assignedToWork", true))
				.add(Restrictions.eq("resource.user.id", userId))
				.add(Restrictions.eq("deleted", false))
				.add(Restrictions.in("workStatusType.code", WorkStatusType.CLOSED_WORK_STATUS_FOR_PENDING_RATING))
				.add(Subqueries.propertyNotIn("id", subcriteria))
				.add(Restrictions.ge("invoice.paymentDate", DateUtilities.getMidnightNMonthsAgo(3)));

		return ((Long) count.list().get(0)).intValue() > 0;
	}

	@Override
	public Optional<User> findSupportContactUserByWorkId(Long workId) {
		Work work = (Work) getFactory().getCurrentSession().getNamedQuery("work.getWorkWithSupportContactByWorkId")
				.setParameter("workId", workId).uniqueResult();

		return work == null ? Optional.<User>absent() : Optional.of(work.getBuyerSupportUser());
	}

	@Override
	public Map<Long, Calendar> findLastModifiedDate(int limit) {
		String sql =
				" SELECT 	waa.work_id AS workId, waa.last_action_on " +
						" FROM		work_action_audit waa " +
						" ORDER 	BY waa.last_action_on DESC limit :limit ";

		Map<Long, Calendar> results = Maps.newHashMap();
		MapSqlParameterSource params = new MapSqlParameterSource();
		params.addValue("limit", limit);

		List<Map<String, Object>> workList = readOnlyJdbcTemplate.queryForList(sql, params);
		for (Map<String, Object> row : workList) {
			Long workId = (Long) row.get("workId");
			Calendar time = DateUtilities.getCalendarFromDate((Timestamp) row.get("last_action_on"));
			results.put(workId, time);
		}
		return results;
	}

	@Override
	public EntityIdPagination findAllWorkIdsByCompanyId(long companyId, EntityIdPagination pagination) {
		Integer rowCount = 0;
		Calendar indexFrom = Calendar.getInstance();
		indexFrom.add(Calendar.DAY_OF_YEAR, -7);
		SQLBuilder builder = new SQLBuilder();
		builder.addColumn("work.id")
				.addTable("work")
				.addJoin("INNER JOIN work_action_audit ON work_action_audit.work_id = work.id")
				.addWhereClause("work.type = 'W'")
				.addWhereClause("work.deleted = false")
				.addWhereClause("work.company_id = :companyId")
				.addWhereClause("work_action_audit.last_action_on >= :lastModifiedFrom")
				.addParam("lastModifiedFrom", indexFrom)
				.addParam("companyId", companyId)
				.setStartRow(pagination.getStartRow())
				.setPageSize(pagination.getResultsLimit());

		List<Long> results = readOnlyJdbcTemplate.queryForList(builder.build(), builder.getParams(), Long.class);

		if (!pagination.isSkipTotalCount()) {
			if (isNotEmpty(results)) {
				rowCount = readOnlyJdbcTemplate.queryForObject(builder.buildCount("work.id"), builder.getParams(), Integer.class);
			}
		}
		pagination.setResults(results);
		pagination.setRowCount(rowCount);
		return pagination;
	}

	@Override
	public EntityIdPagination findAllWorkIdsByLastModifiedDate(Calendar lastModifiedFrom, EntityIdPagination pagination) {
		Integer rowCount = 0;
		SQLBuilder builder = new SQLBuilder();
		builder.addColumn("work.id")
				.addTable("work")
				.addJoin("INNER JOIN work_action_audit ON work_action_audit.work_id = work.id")
				.addWhereClause("work.type = 'W'")
				.addWhereClause("work.deleted = false")
				.addWhereClause("work_action_audit.last_action_on >= :lastModifiedFrom")
				.addParam("lastModifiedFrom", lastModifiedFrom)
				.setStartRow(pagination.getStartRow())
				.setPageSize(pagination.getResultsLimit());

		List<Long> results = readOnlyJdbcTemplate.queryForList(builder.build(), builder.getParams(), Long.class);

		if (!pagination.isSkipTotalCount()) {
			if (isNotEmpty(results)) {
				rowCount = readOnlyJdbcTemplate.queryForObject(builder.buildCount("work.id"), builder.getParams(), Integer.class);
			}
		}
		pagination.setResults(results);
		pagination.setRowCount(rowCount);
		return pagination;
	}

	@Override
	public void saveOrUpdate(final Work entity) {
		super.saveOrUpdate(entity);
	}

	@Override
	public void persist(final Work t) {
		super.persist(t);
	}

	@Override
	public void saveAll(final Collection<Work> entities) {
		super.saveAll(entities);
	}

	@Override
	public Map<String, Object> getAssignmentDataOne(final Map<String, Object> params) {
		final String selectCount = " SELECT count(distinct work.id) ";
		final String select =
				" SELECT DISTINCT " +
						"                work.company_id                       AS company_id, " +
						"                work.id                               AS work_id, " +
						"                workState.short_name                  AS location_state, " +
						"                buyer.last_name                       AS internal_owner_last_name, " +
						"                assignedWorkResource.hours_worked     AS hours_worked, " +
						"                CASE work.pricing_strategy_type " +
						"                  WHEN 'PER_HOUR' THEN work.per_hour_price " +
						"                  WHEN 'BLENDED_PER_HOUR' THEN work.initial_per_hour_price " +
						"                  ELSE NULL " +
						"                end                                                    AS per_hour_price_initial, " +
						"                IF (work.pricing_strategy_type = 'BLENDED_PER_HOUR', " +
						"                work.additional_per_hour_price, NULL)                  AS per_hour_price_additional, " +
						"                IF (work.work_status_type_code IN ( " +
						"                    'paymentPending', 'paid', 'cancelledPayPending', " +
						"                    'cancelledWithPay' ), work.buyer_total_cost, NULL) AS final_cost, " +

						"                work_milestones.created_on             AS create_date, " +
						"                work.closed_on              AS close_date, " +
						"                work_milestones.sent_on     AS sent_date, " +
						"                work_milestones.complete_on AS complete_date ";

			 String fromAndWhere =
						" FROM   work " +
						"       LEFT JOIN work_milestones " +
						"              ON work.id = work_milestones.work_id " +
						"       LEFT JOIN address " +
						"              ON work.address_id = address.id " +
						"       LEFT JOIN state AS workState " +
						"              ON workState.id = address.state " +
						"       LEFT JOIN user AS buyer " +
						"              ON buyer.id = work.buyer_user_id " +
						"       LEFT JOIN work_resource assignedWorkResource " +
						"              ON work.id = assignedWorkResource.work_id " +
						"                 AND assignedWorkResource.assigned_to_work = true " +
						" WHERE  1 = 1 " +
						"       AND work.deleted = false " +
						"       AND work.type = 'W' " +
						"       AND work.company_id = :companyId ";

		if (params.containsKey("created_date_from")) {
			fromAndWhere += " AND work_milestones.created_on >= :created_date_from ";
		}
		if (params.containsKey("created_date_to")) {
			fromAndWhere += " AND work_milestones.created_on <= :created_date_to ";
		}

		if (params.containsKey("close_date_from")) {
			fromAndWhere += " AND work.closed_on >= :close_date_from ";
		}
		if (params.containsKey("close_date_to")) {
			fromAndWhere += " AND work.closed_on <= :close_date_to ";
		}

		if (params.containsKey("sent_date_from")) {
			fromAndWhere += " AND work_milestones.sent_on >= :sent_date_from ";
		}
		if (params.containsKey("sent_date_to")) {
			fromAndWhere += " AND work_milestones.sent_on <= :sent_date_to ";
		}

		if (params.containsKey("complete_date_from")) {
			fromAndWhere += " AND work_milestones.complete_on >= :complete_date_from ";
		}
		if (params.containsKey("complete_date_to")) {
			fromAndWhere += " AND work_milestones.complete_on <= :complete_date_to ";
		}

		final String limitAndOrder =
				" ORDER BY sent_date DESC " +
				" LIMIT  :offset, :limit ";

		final List<Map<String, Object>> results = readOnlyJdbcTemplate.query(select + fromAndWhere + limitAndOrder, new MapSqlParameterSource(params), new
				RowMapper<Map<String, Object>>() {
			@Override
			public Map<String, Object> mapRow(ResultSet resultSet, int i) throws SQLException {
				final Map<String, Object> row = Maps.newLinkedHashMap();
				row.put("company_id", resultSet.getLong(1));
				row.put("work_id", resultSet.getLong(2));
				row.put("location_state", resultSet.getString(3));
				row.put("internal_owner_last_name", resultSet.getString(4));
				row.put("hours_worked", resultSet.getInt(5));
				row.put("per_hour_price_initial", resultSet.getBigDecimal(6));
				row.put("per_hour_price_additional", resultSet.getBigDecimal(7));
				row.put("final_cost", resultSet.getBigDecimal(8));
				row.put("create_date", resultSet.getTimestamp(9));
				row.put("close_date", resultSet.getTimestamp(10));
				row.put("sent_date", resultSet.getTimestamp(11));
				row.put("complete_date", resultSet.getTimestamp(12));

				return row;
			}
		});

		final Integer totalRows = readOnlyJdbcTemplate.queryForObject(selectCount + fromAndWhere, new MapSqlParameterSource(params), Integer.class);

		if (results.isEmpty()) {
			return ImmutableMap.of();
		}

		return Maps.newHashMap(ImmutableMap.of(
				"results", results,
				"totalRows", totalRows));
	}

	private static final class WorkCompanyRowMapper implements RowMapper<BuyerIdentityDTO> {
		WorkCompanyRowMapper() { }

		@Override public BuyerIdentityDTO mapRow(ResultSet rs, int i) throws SQLException {
			return new BuyerIdentityDTO(rs.getLong("id"), rs.getLong("company_id"), rs.getString("company.uuid"), rs.getString("company.company_number"));
		}
	}
}
