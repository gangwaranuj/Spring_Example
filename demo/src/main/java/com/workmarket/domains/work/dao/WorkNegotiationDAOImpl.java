package com.workmarket.domains.work.dao;

import com.google.common.base.Optional;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.workmarket.dao.PaginationAbstractDAO;
import com.workmarket.domains.model.ApprovalStatus;
import com.workmarket.domains.model.DateRange;
import com.workmarket.domains.model.Pagination;
import com.workmarket.domains.model.account.RegisterTransactionType;
import com.workmarket.domains.model.account.pricing.AccountServiceType;
import com.workmarket.domains.work.model.negotiation.AbstractWorkNegotiation;
import com.workmarket.domains.work.model.negotiation.WorkBonusNegotiation;
import com.workmarket.domains.work.model.negotiation.WorkBudgetNegotiation;
import com.workmarket.domains.work.model.negotiation.WorkExpenseNegotiation;
import com.workmarket.domains.work.model.negotiation.WorkNegotiation;
import com.workmarket.domains.work.model.negotiation.WorkNegotiationPagination;
import com.workmarket.domains.work.model.negotiation.WorkRescheduleNegotiation;
import com.workmarket.utility.DateUtilities;
import com.workmarket.utility.StringUtilities;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.criterion.CriteriaSpecification;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.List;
import java.util.Map;

@Repository
public class WorkNegotiationDAOImpl extends PaginationAbstractDAO<AbstractWorkNegotiation> implements WorkNegotiationDAO {

	@Qualifier("jdbcTemplate") @Autowired private NamedParameterJdbcTemplate jdbcTemplate;

	protected Class<AbstractWorkNegotiation> getEntityClass() {
		return AbstractWorkNegotiation.class;
	}

	@Override
	public <T extends AbstractWorkNegotiation> T findById(long id) {
		return (T) getFactory().getCurrentSession().createCriteria(AbstractWorkNegotiation.class)
				.add(Restrictions.eq("id", id))
				.createAlias("work", "work", CriteriaSpecification.INNER_JOIN)
				.uniqueResult();
	}

	@Override
	public WorkNegotiationPagination findByWork(final long workId, WorkNegotiationPagination pagination) {
		return (WorkNegotiationPagination) super.paginationQuery(WorkNegotiation.class, pagination, ImmutableMap.<String, Object>of("work.id", workId, "deleted", Boolean.FALSE));
	}

	@Override
	public WorkNegotiationPagination findByUserForWork(final long userId, final long workId, WorkNegotiationPagination pagination) {
		return (WorkNegotiationPagination) super.paginationQuery(WorkNegotiation.class, pagination, ImmutableMap.<String, Object>of("requestedBy.id", userId, "work.id", workId, "deleted", Boolean.FALSE));
	}

	@Override
	public WorkNegotiationPagination findByCompanyForWork(final long companyId, final long workId, WorkNegotiationPagination pagination) {
		Criteria criteria = getFactory().getCurrentSession().createCriteria(WorkNegotiation.class);
		Criteria count = getFactory().getCurrentSession().createCriteria(WorkNegotiation.class);
		count.setProjection(Projections.rowCount());
		count.createAlias("requestedBy", "rb");
		criteria.setFirstResult(pagination.getStartRow());
		criteria.createAlias("requestedBy", "rb");

		if (pagination.getResultsLimit() != null) {
			criteria.setMaxResults(pagination.getResultsLimit());
		}

		buildWhereClause(criteria, count, ImmutableMap.<String, Object>of("rb.company.id", companyId, "work.id", workId, "deleted", Boolean.FALSE));
		applySorts(pagination, criteria, count);
		applyFilters(pagination, criteria, count);

		pagination.setResults(criteria.list());
		if (count.list().size() > 0) {
			pagination.setRowCount(((Long) count.list().get(0)).intValue());
		} else {
			pagination.setRowCount(0);
		}

		return pagination;
	}

	@Override
	public WorkNegotiation findLatestByUserForWork(Long userId, Long workId) {
		return (WorkNegotiation) getFactory().getCurrentSession().createCriteria(WorkNegotiation.class)
				.add(Restrictions.eq("requestedBy.id", userId))
				.add(Restrictions.eq("work.id", workId))
				.add(Restrictions.ge("deleted", Boolean.FALSE))
				.addOrder(Order.desc("id"))
				.setMaxResults(1)
				.uniqueResult();
	}

	@Override
	public WorkNegotiation findLatestApprovedByUserForWork(Long userId, Long workId) {
		return findLatestNegotiationForWorkByUserAndStatus(workId, userId, ApprovalStatus.APPROVED);
	}

	@Override
	public WorkNegotiation findLatestApprovedByCompanyForWork(Long companyId, Long workId) {
		return findLatestNegotiationForWorkByCompanyAndStatus(workId, companyId, ApprovalStatus.APPROVED);
	}

	@Override
	public WorkNegotiation findLatestApprovedForWork(Long workId) {
		return findLatestNegotiationForWorkByUserAndStatus(workId, null, ApprovalStatus.APPROVED);
	}

	@Override
	public Optional<WorkExpenseNegotiation> findLatestActiveExpenseNegotiationByUserForWork(Long userId, Long workId) {
		return Optional.fromNullable(findLatestNegotiationByUserForWorkWithStatus(
				WorkExpenseNegotiation.class, userId, workId, ApprovalStatus.PENDING));
	}

	@Override
	public Optional<WorkExpenseNegotiation> findLatestActiveExpenseNegotiationByCompanyForWork(Long companyId, Long workId) {
		return Optional.fromNullable(findLatestNegotiationByCompanyForWorkWithStatus(
			WorkExpenseNegotiation.class, companyId, workId, ApprovalStatus.PENDING));
	}

	@Override
	public Optional<WorkExpenseNegotiation> findLatestActiveExpenseNegotiationForWork(Long workId) {
		return Optional.fromNullable(findLatestNegotiationByUserForWorkWithStatus(
			WorkExpenseNegotiation.class, null, workId, ApprovalStatus.PENDING));
	}

	@Override
	public List<WorkExpenseNegotiation> findAllActiveExpenseNegotiationsByUserAndWork(Long userId, Long workId) {
		return findAllNegotiationsByUserForWorkWithStatus(
			WorkExpenseNegotiation.class, userId, workId, ApprovalStatus.PENDING);
	}

	@Override
	public List<WorkExpenseNegotiation> findAllActiveExpenseNegotiationsByCompanyAndWork(Long companyId, Long workId) {
		return findAllNegotiationsByCompanyForWorkWithStatus(
			WorkExpenseNegotiation.class, companyId, workId, ApprovalStatus.PENDING);
	}

	@Override
	public Optional<WorkExpenseNegotiation> findLatestApprovedExpenseIncreaseForWork(Long workId) {
		return Optional.fromNullable(findLatestNegotiationByUserForWorkWithStatus(
				WorkExpenseNegotiation.class, null, workId, ApprovalStatus.APPROVED));
	}

	@Override
	public Optional<WorkBudgetNegotiation> findLatestActiveBudgetNegotiationForWork(Long workId) {
		return Optional.fromNullable(findLatestNegotiationByUserForWorkWithStatus(
			WorkBudgetNegotiation.class, null, workId, ApprovalStatus.PENDING));
	}

	@Override
	public Optional<WorkBudgetNegotiation> findLatestActiveBudgetNegotiationByUserForWork(Long userId, Long workId) {
		return Optional.fromNullable(findLatestNegotiationByUserForWorkWithStatus(
				WorkBudgetNegotiation.class, userId, workId, ApprovalStatus.PENDING));
	}

	@Override
	public Optional<WorkBudgetNegotiation> findLatestActiveBudgetNegotiationByCompanyForWork(Long companyId, Long workId) {
		return Optional.fromNullable(findLatestNegotiationByCompanyForWorkWithStatus(
			WorkBudgetNegotiation.class, companyId, workId, ApprovalStatus.PENDING));
	}

	@Override
	public List<WorkBudgetNegotiation> findAllActiveBudgetNegotiationsByUserAndWork(Long userId, Long workId) {
		return findAllNegotiationsByUserForWorkWithStatus(
			WorkBudgetNegotiation.class, userId, workId, ApprovalStatus.PENDING);
	}

	@Override
	public List<WorkBudgetNegotiation> findAllActiveBudgetNegotiationsByCompanyAndWork(Long companyId, Long workId) {
		return findAllNegotiationsByCompanyForWorkWithStatus(
			WorkBudgetNegotiation.class, companyId, workId, ApprovalStatus.PENDING);
	}

	@Override
	public Optional<WorkBonusNegotiation> findLatestActiveBonusNegotiationByUserForWork(Long userId, Long workId) {
		return Optional.fromNullable(findLatestNegotiationByUserForWorkWithStatus(
			WorkBonusNegotiation.class, userId, workId, ApprovalStatus.PENDING));
	}

	@Override
	public Optional<WorkBonusNegotiation> findLatestActiveBonusNegotiationByCompanyForWork(Long companyId, Long workId) {
		return Optional.fromNullable(findLatestNegotiationByCompanyForWorkWithStatus(
			WorkBonusNegotiation.class, companyId, workId, ApprovalStatus.PENDING));
	}

	@Override public Optional<WorkBonusNegotiation> findLatestActiveBonusNegotiationForWork(Long workId) {
		return Optional.fromNullable(findLatestNegotiationByUserForWorkWithStatus(
			WorkBonusNegotiation.class, null, workId, ApprovalStatus.PENDING));
	}

	@Override
	public List<WorkBonusNegotiation> findAllActiveBonusNegotiationsByUserAndWork(Long userId, Long workId) {
		return findAllNegotiationsByUserForWorkWithStatus(
			WorkBonusNegotiation.class, userId, workId, ApprovalStatus.PENDING);
	}

	@Override
	public List<WorkBonusNegotiation> findAllActiveBonusNegotiationsByCompanyAndWork(Long companyId, Long workId) {
		return findAllNegotiationsByCompanyForWorkWithStatus(
			WorkBonusNegotiation.class, companyId, workId, ApprovalStatus.PENDING);
	}

	@Override
	public Optional<WorkBonusNegotiation> findLatestApprovedBonusForWork(Long workId) {
		return Optional.fromNullable(findLatestNegotiationByUserForWorkWithStatus(
			WorkBonusNegotiation.class, null, workId, ApprovalStatus.APPROVED));
	}

	@Override
	public WorkRescheduleNegotiation findLatestApprovedRescheduleRequestForWork(Long workId) {
		return (WorkRescheduleNegotiation) getFactory().getCurrentSession().createCriteria(WorkRescheduleNegotiation.class)
				.add(Restrictions.eq("work.id", workId))
				.add(Restrictions.ge("deleted", Boolean.FALSE))
				.add(Restrictions.isNotNull("approvedBy"))
				.addOrder(Order.desc("approvedOn"))
				.setMaxResults(1)
				.uniqueResult();
	}

	@Override
	public WorkRescheduleNegotiation findLatestRescheduleRequestByUserForWork(Long userId, Long workId) {
		return (WorkRescheduleNegotiation) getFactory().getCurrentSession().createCriteria(WorkRescheduleNegotiation.class)
				.add(Restrictions.eq("requestedBy.id", userId))
				.add(Restrictions.eq("work.id", workId))
				.add(Restrictions.eq("initiatedByResource", Boolean.TRUE))
				.add(Restrictions.ge("deleted", Boolean.FALSE))
				.addOrder(Order.desc("id"))
				.setMaxResults(1)
				.uniqueResult();
	}

	@Override
	public WorkRescheduleNegotiation findLatestActiveRescheduleRequestByUserForWork(Long userId, Long workId) {
		return (WorkRescheduleNegotiation) getFactory().getCurrentSession().createCriteria(WorkRescheduleNegotiation.class)
				.add(Restrictions.eq("requestedBy.id", userId))
				.add(Restrictions.eq("work.id", workId))
				.add(Restrictions.eq("initiatedByResource", Boolean.TRUE))
				.add(Restrictions.eq("approvalStatus", ApprovalStatus.PENDING))
				.add(Restrictions.ge("deleted", Boolean.FALSE))
				.addOrder(Order.desc("id"))
				.setMaxResults(1)
				.uniqueResult();
	}

	@Override
	public WorkRescheduleNegotiation findLatestActiveRescheduleRequestByCompanyForWork(boolean isResource, Long companyId, Long workId) {
		return (WorkRescheduleNegotiation) getFactory().getCurrentSession().createCriteria(WorkRescheduleNegotiation.class)
				.createAlias("requestedBy", "requestedBy")
				.add(Restrictions.eq("requestedBy.company.id", companyId))
				.add(Restrictions.eq("work.id", workId))
				.add(Restrictions.eq("initiatedByResource", isResource))
				.add(Restrictions.eq("approvalStatus", ApprovalStatus.PENDING))
				.add(Restrictions.ge("deleted", Boolean.FALSE))
				.addOrder(Order.desc("id"))
				.setMaxResults(1)
				.uniqueResult();
	}

	@SuppressWarnings("unchecked")
	@Override
	public Collection<WorkNegotiation> findAllByWork(Long id) {

		List<?> results = getFactory().getCurrentSession().createCriteria(WorkNegotiation.class)
				.add(Restrictions.eq("work.id", id))
				.add(Restrictions.eq("deleted", Boolean.FALSE))
				.addOrder(Order.desc("id")).list();

		return (Collection<WorkNegotiation>) results;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<WorkExpenseNegotiation> findPreCompletionExpenseIncreasesForWork(Long workId) {

		List<?> results = getFactory().getCurrentSession().createCriteria(WorkExpenseNegotiation.class)
				.add(Restrictions.eq("work.id", workId))
				.add(Restrictions.eq("duringCompletion", Boolean.FALSE))
				.add(Restrictions.eq("priceNegotiation", Boolean.TRUE))
				.add(Restrictions.ge("deleted", Boolean.FALSE))
				.add(Restrictions.eq("approvalStatus", ApprovalStatus.APPROVED))
				.addOrder(Order.desc("fullPricingStrategy.additionalExpenses"))
				.list();

		return (List<WorkExpenseNegotiation>) results;
	}

	@Override
	@SuppressWarnings("unchecked")
	public List<WorkBonusNegotiation> findPreCompletionBonusesForWork(Long workId) {

		List<?> results = getFactory().getCurrentSession().createCriteria(WorkBonusNegotiation.class)
				.add(Restrictions.eq("work.id", workId))
				.add(Restrictions.eq("duringCompletion", Boolean.FALSE))
				.add(Restrictions.eq("priceNegotiation", Boolean.TRUE))
				.add(Restrictions.ge("deleted", Boolean.FALSE))
				.add(Restrictions.eq("approvalStatus", ApprovalStatus.APPROVED))
				.addOrder(Order.desc("fullPricingStrategy.bonus"))
				.list();

		return (List<WorkBonusNegotiation>) results;
	}


	@Override
	public Map<String, BigDecimal> findTotalAdditionalExpensesPaidToCompany(Long companyId, DateRange dateRange) {
		/*
			If there's an override price, additional expenses is 0.
			Else, if there are expenses on the work resource table, use that amount
			Else, default to the work expenses
		 */
		String sql = "SELECT COALESCE(SUM(IF(COALESCE(work.override_price, 0) > 0, 0, " +
				" 				 IF(COALESCE(work_resource.additional_expenses, 0) > 0, work_resource.additional_expenses, COALESCE(work.additional_expenses,0)))) ,0) " +
				"			AS expenses, \n" +
				" 			wrt.account_service_type_code AS accountServiceType " +
				" FROM 		work " +
				" INNER 	JOIN register_transaction rt ON rt.work_id = work.id \n" +
				" INNER 	JOIN account_register ON account_register.id = rt.account_register_id \n" +
				" INNER  	JOIN work_resource_transaction wrt ON wrt.id = rt.id \n" +
				" INNER  	JOIN work_resource ON work_resource.id = wrt.work_resource_id " +
				" WHERE 	rt.effective_date >= :fromDate AND rt.effective_date < :toDate \n" +
				" AND 		rt.register_transaction_type_code = :workPayment " +
				" AND 		rt.pending_flag = 'N' \n" +
				" AND 		rt.amount > 0 " +
				" AND 		account_register.company_id = :companyId " +
				" GROUP 	BY wrt.account_service_type_code";


		Map<String, BigDecimal> result = Maps.newHashMapWithExpectedSize(3);
		MapSqlParameterSource params = new MapSqlParameterSource();
		params.addValue("fromDate", dateRange.getFrom());
		params.addValue("toDate", dateRange.getThrough());
		params.addValue("workPayment", RegisterTransactionType.RESOURCE_WORK_PAYMENT);
		params.addValue("vor", AccountServiceType.VENDOR_OF_RECORD);
		params.addValue("companyId", companyId);

		List<Map<String, Object>> results = jdbcTemplate.queryForList(sql, params);
		for (Map<String, Object> row : results) {
			String accountServiceType = (String) row.get("accountServiceType");
			BigDecimal amount = (BigDecimal) row.get("expenses");
			result.put(accountServiceType, amount);
		}
		return result;
	}

	@Override
	public BigDecimal findTotalAdditionalExpensesPaidToCompanyByBuyer(Long resourceCompanyId, Long buyerCompanyId, DateRange dateRange, List<String> accountServiceType) {
		/*
			If there's an override price, additional expenses is 0.
			Else, if there are expenses on the work resource table, use that amount
			Else, default to the work expenses
		 */
		String sql = "SELECT COALESCE(SUM(IF(COALESCE(work.override_price, 0) > 0, 0, " +
				" 				 IF(COALESCE(work_resource.additional_expenses, 0) > 0, work_resource.additional_expenses, COALESCE(work.additional_expenses,0)))), 0) " +
				"			AS expenses " +
				" FROM 		work " +
				" INNER 	JOIN register_transaction rt ON rt.work_id = work.id \n" +
				" INNER   	JOIN work_resource_transaction wrt ON wrt.id = rt.id \n" +
				" INNER 	JOIN account_register ON account_register.id = rt.account_register_id \n" +
				" INNER  	JOIN work_resource ON work_resource.id = wrt.work_resource_id " +
				" WHERE 	rt.effective_date >= :fromDate AND rt.effective_date < :toDate \n" +
				" AND 		rt.register_transaction_type_code = :workPayment " +
				" AND 		rt.pending_flag = 'N' \n" +
				" AND 		rt.amount > 0 " +
				" AND     	wrt.account_service_type_code IN (" + StringUtils.join(StringUtilities.surround(accountServiceType, "'"), ",") + " )" +
				" AND     	account_register.company_id = :companyId " +
				" AND     	work.company_id = :buyerCompanyId";

		MapSqlParameterSource params = new MapSqlParameterSource();
		params.addValue("fromDate", dateRange.getFrom())
				.addValue("toDate", dateRange.getThrough())
				.addValue("workPayment", RegisterTransactionType.RESOURCE_WORK_PAYMENT)
				.addValue("companyId", resourceCompanyId)
				.addValue("buyerCompanyId", buyerCompanyId);

		return this.jdbcTemplate.queryForObject(sql, params, BigDecimal.class);
	}

	@Override
	public List<Long> findAllApplicantsPendingApproval(long workId) {
		String sql =
				"SELECT wn.requestor_id " +
						"FROM work w " +
						"INNER JOIN work_negotiation wn " +
						"ON w.id = wn.work_id " +
						"LEFT OUTER JOIN blocked_user_association bua " +
						"ON w.company_id = bua.blocking_company_id " +
						"AND wn.requestor_id = bua.blocked_user_id " +
						"AND bua.deleted = 0 " +
						"WHERE w.id = :workId " +
						"AND bua.blocked_user_id IS NULL " +
						"AND wn.type = :apply " +
						"AND wn.requestor_is_resource = TRUE " +
						"AND (wn.expires_on IS NULL OR wn.expires_on >= now()) " +
						"AND wn.approval_status = 0;";

		MapSqlParameterSource params = new MapSqlParameterSource();
		params.addValue("workId", workId);
		params.addValue("apply", WorkNegotiation.APPLY);

		return this.jdbcTemplate.queryForList(sql, params, Long.class);
	}

	@Override
	public void applySorts(Pagination<AbstractWorkNegotiation> pagination, Criteria query, Criteria count) {
		query.addOrder(Order.desc("requestedOn"));
	}

	@Override
	public void applyFilters(Pagination<AbstractWorkNegotiation> pagination, Criteria query, Criteria count) {
		if (pagination.hasFilter(WorkNegotiationPagination.FILTER_KEYS.APPROVAL_STATUS)) {
			String approvalStatus = pagination.getFilter(WorkNegotiationPagination.FILTER_KEYS.APPROVAL_STATUS);
			query.add(Restrictions.eq("approvalStatus", ApprovalStatus.valueOf(approvalStatus)));
			count.add(Restrictions.eq("approvalStatus", ApprovalStatus.valueOf(approvalStatus)));
		}

		if (pagination.hasFilter(WorkNegotiationPagination.FILTER_KEYS.EXPIRED)) {
			Boolean expired = Boolean.parseBoolean(pagination.getFilter(WorkNegotiationPagination.FILTER_KEYS.EXPIRED));

			if (expired) {
				query.add(Restrictions.le("expiresOn", DateUtilities.getCalendarNow()));
				count.add(Restrictions.le("expiresOn", DateUtilities.getCalendarNow()));
			} else {
				query.add(Restrictions.or(
						Restrictions.isNull("expiresOn"),
						Restrictions.gt("expiresOn", DateUtilities.getCalendarNow())
				));
				count.add(Restrictions.or(
						Restrictions.isNull("expiresOn"),
						Restrictions.gt("expiresOn", DateUtilities.getCalendarNow())
				));
			}
		}
	}

	@Override
	public void buildWhereClause(Criteria query, Criteria count, Map<String, Object> params) {
		for (String key : params.keySet()) {
			query.add(Restrictions.eq(key, params.get(key)));
			count.add(Restrictions.eq(key, params.get(key)));
		}
	}

	@SuppressWarnings("unchecked")
	private WorkNegotiation findLatestNegotiationForWorkByUserAndStatus(Long workId, Long userId, ApprovalStatus status) {
		Criteria criteria = getFactory().getCurrentSession().createCriteria(WorkNegotiation.class)
				.add(Restrictions.eq("work.id", workId))
				.add(Restrictions.ge("deleted", Boolean.FALSE))
				.addOrder(Order.desc("id"))
				.setMaxResults(1);
		if (userId != null) criteria.add(Restrictions.eq("requestedBy.id", userId));
		if (status != null) criteria.add(Restrictions.eq("approvalStatus", status));

		return (WorkNegotiation) criteria.uniqueResult();
	}

	@SuppressWarnings("unchecked")
	private WorkNegotiation findLatestNegotiationForWorkByCompanyAndStatus(Long workId, Long companyId, ApprovalStatus status) {
		Criteria criteria = getFactory().getCurrentSession().createCriteria(WorkNegotiation.class)
			.createAlias("requestedBy", "rb")
			.add(Restrictions.eq("work.id", workId))
			.add(Restrictions.ge("deleted", Boolean.FALSE))
			.addOrder(Order.desc("id"))
			.setMaxResults(1);
		if (companyId != null) criteria.add(Restrictions.eq("rb.company.id", companyId));
		if (status != null) criteria.add(Restrictions.eq("approvalStatus", status));

		return (WorkNegotiation) criteria.uniqueResult();
	}

	@SuppressWarnings("unchecked")
	private <T extends AbstractWorkNegotiation> T findLatestNegotiationByUserForWorkWithStatus(Class<T> clazz, Long userId, Long workId, ApprovalStatus status) {
		Criteria criteria = getFactory().getCurrentSession().createCriteria(clazz)
				.add(Restrictions.eq("work.id", workId))
				.add(Restrictions.ge("deleted", Boolean.FALSE))
				.addOrder(Order.desc("id"))
				.setMaxResults(1);
		if (status != null) criteria.add(Restrictions.eq("approvalStatus", status));
		if (userId != null) criteria.add(Restrictions.eq("requestedBy.id", userId));

		return (T) criteria.uniqueResult();
	}

	@SuppressWarnings("unchecked")
	private <T extends AbstractWorkNegotiation> T findLatestNegotiationByCompanyForWorkWithStatus(Class<T> clazz, Long companyId, Long workId, ApprovalStatus status) {
		Criteria criteria = getFactory().getCurrentSession().createCriteria(clazz)
			.createAlias("requestedBy", "rb")
			.add(Restrictions.eq("work.id", workId))
			.add(Restrictions.ge("deleted", Boolean.FALSE))
			.addOrder(Order.desc("id"))
			.setMaxResults(1);
		if (status != null) criteria.add(Restrictions.eq("approvalStatus", status));
		if (companyId != null) criteria.add(Restrictions.eq("rb.company.id", companyId));

		return (T) criteria.uniqueResult();
	}

	@SuppressWarnings("unchecked")
	private <T extends AbstractWorkNegotiation> List<T> findAllNegotiationsByUserForWorkWithStatus(Class<T> clazz, Long userId, Long workId, ApprovalStatus status) {
		Criteria criteria = getFactory().getCurrentSession().createCriteria(clazz)
			.add(Restrictions.eq("work.id", workId))
			.add(Restrictions.ge("deleted", Boolean.FALSE))
			.addOrder(Order.desc("id"));
		if (status != null) criteria.add(Restrictions.eq("approvalStatus", status));
		if (userId != null) criteria.add(Restrictions.eq("requestedBy.id", userId));

		return (List<T>) criteria.list();
	}

	@SuppressWarnings("unchecked")
	private <T extends AbstractWorkNegotiation> List<T> findAllNegotiationsByCompanyForWorkWithStatus(Class<T> clazz, Long companyId, Long workId, ApprovalStatus status) {
		Criteria criteria = getFactory().getCurrentSession().createCriteria(clazz)
			.createAlias("requestedBy", "rb")
			.add(Restrictions.eq("work.id", workId))
			.add(Restrictions.ge("deleted", Boolean.FALSE))
			.addOrder(Order.desc("id"));
		if (status != null) criteria.add(Restrictions.eq("approvalStatus", status));
		if (companyId != null) criteria.add(Restrictions.eq("rb.company.id", companyId));

		return (List<T>) criteria.list();
	}
}
