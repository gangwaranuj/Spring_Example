package com.workmarket.dao.account.pricing.subscription;


import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.workmarket.dao.PaginationAbstractDAO;
import com.workmarket.domains.model.ApprovalStatus;
import com.workmarket.domains.model.Pagination;
import com.workmarket.domains.model.VerificationStatus;
import com.workmarket.domains.model.account.pricing.AccountPricingType;
import com.workmarket.domains.model.account.pricing.subscription.SubscriptionConfiguration;
import com.workmarket.domains.model.account.pricing.subscription.SubscriptionConfigurationPagination;
import com.workmarket.domains.model.account.pricing.subscription.SubscriptionStatusType;
import com.workmarket.utility.DateUtilities;
import com.workmarket.domains.model.account.pricing.subscription.SubscriptionUtilities;
import org.hibernate.Criteria;
import org.hibernate.FetchMode;
import org.hibernate.Hibernate;
import org.hibernate.SQLQuery;
import org.hibernate.criterion.CriteriaSpecification;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.transform.ResultTransformer;
import org.springframework.stereotype.Repository;
import org.springframework.util.Assert;

import java.math.BigInteger;
import java.util.Calendar;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.apache.commons.lang3.StringUtils.isNotEmpty;

/**
 * Author: rocio
 */
@Repository
public class SubscriptionConfigurationDAOImpl extends PaginationAbstractDAO<SubscriptionConfiguration> implements SubscriptionConfigurationDAO {

	protected Class<SubscriptionConfiguration> getEntityClass() {
		return SubscriptionConfiguration.class;
	}

	@Override
	public SubscriptionConfiguration findActiveSubscriptionConfigurationByCompanyId(long companyId) {
		Criteria criteria = getFactory().getCurrentSession().createCriteria(getEntityClass())
				.add(Restrictions.eq("company.id", companyId))
				.add(Restrictions.eq("subscriptionStatusType.code", SubscriptionStatusType.ACTIVE))
				.add(Restrictions.eq("approvalStatus", ApprovalStatus.APPROVED))
				.add(Restrictions.eq("verificationStatus", VerificationStatus.VERIFIED))
				.add(Restrictions.eq("deleted", false));
		return (SubscriptionConfiguration) criteria.uniqueResult();
	}

	@Override
	public SubscriptionConfigurationPagination findAllSubscriptionConfigurations(SubscriptionConfigurationPagination pagination) {
		Assert.notNull(pagination);

		Criteria query = getFactory().getCurrentSession().createCriteria(getEntityClass())
				.add(Restrictions.eq("deleted", false))
				.setResultTransformer(CriteriaSpecification.DISTINCT_ROOT_ENTITY)
				.setFirstResult(pagination.getStartRow())
				.setMaxResults(pagination.getResultsLimit());

		Criteria count = getFactory().getCurrentSession().createCriteria(getEntityClass())
				.add(Restrictions.eq("deleted", false))
				.setResultTransformer(CriteriaSpecification.DISTINCT_ROOT_ENTITY)
				.setProjection(Projections.rowCount());

		applyFilters(pagination, query, count);
		applySorts(pagination, query, count);

		pagination.setResults(query.list());
		if (count.list().size() > 0)
			pagination.setRowCount(((Long) count.list().get(0)).intValue());
		else
			pagination.setRowCount(0);

		return pagination;
	}

	@Override
	public SubscriptionConfigurationPagination findAllPendingSubscriptionConfigurations(SubscriptionConfigurationPagination pagination) {
		Assert.notNull(pagination);

		String pendingApprovalSubscriptions =
				" SELECT 0 as approvalType, this.id, this.company_id, this.subscription_status_type_code, this.effective_date, this.subscription_period,this.number_of_periods, this.signed_date, "
						+ " this.end_date,this.discounted_periods,this.discounted_amount_per_period,this.set_up_fee,this.payment_terms_days,"
						+ " this.cancellation_option, this.client_ref_id, this.approval_status,this.verification_status,this.approved_on,this.approved_by,this.deleted,this.created_on,this.modified_on, "
						+ " this.creator_id,this.modifier_id,this.last_throughput_upper_bound_reached_on,this.past_payment_periods,this.next_payment_period_start_date,"
						+ " this.number_of_renewals, this.parent_subscription_id, this.effective_date as effectiveDate, this.next_throughput_reset_date"
						+ " FROM subscription_configuration this"
						+ " WHERE this.approval_status = :pendingApprovalStatus"
						+ " AND this.subscription_status_type_code = :pendingSubscriptionStatus"
						+ " AND this.deleted = false";

		String pendingApprovalFeeConfigurations =
				" SELECT 3 as approvalType, this.id, this.company_id, this.subscription_status_type_code, feeConfiguration.effective_date ,this.subscription_period,this.number_of_periods, this.signed_date, "
						+ " this.end_date,this.discounted_periods,this.discounted_amount_per_period,this.set_up_fee,this.payment_terms_days,"
						+ " this.cancellation_option, this.client_ref_id, this.approval_status,this.verification_status,this.approved_on,this.approved_by,this.deleted,this.created_on,this.modified_on,"
						+ " this.creator_id,this.modifier_id,this.last_throughput_upper_bound_reached_on,this.past_payment_periods,this.next_payment_period_start_date,"
						+ " this.number_of_renewals, this.parent_subscription_id, feeConfiguration.effective_date as effectiveDate, this.next_throughput_reset_date"
						+ " FROM subscription_configuration this"
						+ " INNER JOIN subscription_fee_configuration feeConfiguration ON (feeConfiguration.subscription_configuration_id = this.id)"
						+ " WHERE this.approval_status = :approvedApprovalStatus"
						+ " AND this.subscription_status_type_code = :activeSubscriptionStatus"
						+ " AND feeConfiguration.approval_status = :pendingApprovalStatus"
						+ " AND this.deleted = false"
						+ " AND feeConfiguration.deleted = false"
						+ " AND feeConfiguration.active = false";

		String pendingApprovalCancellations =
				" SELECT 2 as approvalType, this.id, this.company_id, this.subscription_status_type_code, cancellation.effective_date  ,this.subscription_period,this.number_of_periods, this.signed_date, "
						+ " this.end_date,this.discounted_periods,this.discounted_amount_per_period,cancellation.cancellation_fee AS set_up_fee,this.payment_terms_days,"
						+ " this.cancellation_option, this.client_ref_id, this.approval_status,this.verification_status,this.approved_on,this.approved_by,this.deleted,this.created_on,this.modified_on,"
						+ " this.creator_id,this.modifier_id,this.last_throughput_upper_bound_reached_on,this.past_payment_periods,this.next_payment_period_start_date,"
						+ " this.number_of_renewals, this.parent_subscription_id, cancellation.effective_date as effectiveDate, this.next_throughput_reset_date"
						+ " FROM subscription_configuration this"
						+ " INNER JOIN subscription_cancellation cancellation ON (cancellation.subscription_configuration_id = this.id)"
						+ " WHERE this.approval_status = :approvedApprovalStatus"
						+ " AND this.subscription_status_type_code = :activeSubscriptionStatus"
						+ " AND cancellation.approval_status = :pendingApprovalStatus"
						+ " AND this.deleted = false"
						+ " AND cancellation.deleted = false";

		String pendingApprovalAddOnAssociations =
				" SELECT 4 as approvalType, this.id, this.company_id, this.subscription_status_type_code, association.effective_date,this.subscription_period,this.number_of_periods, this.signed_date, "
						+ " this.end_date,this.discounted_periods,this.discounted_amount_per_period,this.set_up_fee,this.payment_terms_days,"
						+ " this.cancellation_option, this.client_ref_id, this.approval_status,this.verification_status,this.approved_on,this.approved_by,this.deleted,this.created_on,this.modified_on,"
						+ " this.creator_id,this.modifier_id,this.last_throughput_upper_bound_reached_on,this.past_payment_periods,this.next_payment_period_start_date,"
						+ " this.number_of_renewals, this.parent_subscription_id, association.effective_date as effectiveDate, this.next_throughput_reset_date"
						+ " FROM subscription_configuration this"
						+ " INNER JOIN subscription_add_on_type_association association ON (association.subscription_configuration_id = this.id)"
						+ " WHERE this.approval_status = :approvedApprovalStatus"
						+ " AND this.subscription_status_type_code = :activeSubscriptionStatus"
						+ " AND association.approval_status = :pendingApprovalStatus"
						+ " AND this.deleted = false"
						+ " AND association.deleted = false";

		String pendingApprovalRenewalRequests =
				" SELECT 1 as approvalType, this.id, this.company_id, this.subscription_status_type_code, this.end_date,this.subscription_period,this.number_of_periods, this.signed_date, "
						+ " this.end_date,this.discounted_periods,this.discounted_amount_per_period,this.set_up_fee,this.payment_terms_days,"
						+ " this.cancellation_option, this.client_ref_id, this.approval_status,this.verification_status,this.approved_on,this.approved_by,this.deleted,this.created_on,this.modified_on,"
						+ " this.creator_id,this.modifier_id,this.last_throughput_upper_bound_reached_on,this.past_payment_periods,this.next_payment_period_start_date, "
						+ " this.number_of_renewals, this.parent_subscription_id, this.end_date as effectiveDate, this.next_throughput_reset_date"
						+ " FROM subscription_configuration this"
						+ " INNER JOIN subscription_renewal_request renewalRequest ON (renewalRequest.parent_subscription_id = this.id)"
						+ " WHERE this.approval_status = :approvedApprovalStatus"
						+ " AND this.subscription_status_type_code = :activeSubscriptionStatus"
						+ " AND renewalRequest.approval_status = :pendingApprovalStatus"
						+ " AND this.deleted = false"
						+ " AND renewalRequest.deleted = false";

		String sqlQuery = pendingApprovalSubscriptions + " UNION " + pendingApprovalFeeConfigurations + " UNION " + pendingApprovalCancellations + " UNION " + pendingApprovalAddOnAssociations
				+ " UNION " + pendingApprovalRenewalRequests;

		// Add a scalar type to represent the type of approval in the approval queue
		SQLQuery query = getFactory().getCurrentSession()
				.createSQLQuery(sqlQuery + " ORDER BY effectiveDate DESC LIMIT " + pagination.getStartRow() + ", " + pagination.getResultsLimit())
				.addEntity(getEntityClass())
				.addScalar("approvalType");
		query.setParameter("approvedApprovalStatus", ApprovalStatus.APPROVED.ordinal());
		query.setParameter("pendingApprovalStatus", ApprovalStatus.PENDING.ordinal());
		query.setParameter("pendingSubscriptionStatus", SubscriptionStatusType.PENDING);
		query.setParameter("activeSubscriptionStatus", SubscriptionStatusType.ACTIVE);

		query.setResultTransformer(new ResultTransformer() {
			private static final long serialVersionUID = 1L;

			@Override
			public Object transformTuple(Object[] tuple, String[] aliases) {
				SubscriptionConfiguration subscription = (SubscriptionConfiguration) tuple[0];
				// See SubscriptionsUtilities.APPROVAL_TYPE for the lookup codes
				Integer approvalTypeCode = ((BigInteger) tuple[1]).intValue();
				subscription.setApprovalType(SubscriptionUtilities.APPROVAL_TYPE.lookupByCode(approvalTypeCode));
				// XXX : Hack to initialize the creator. There's probably a better solution.
				Hibernate.initialize(subscription.getSubscriptionAddOns());
				return subscription;
			}

			@Override
			public List transformList(List collection) {
				List<SubscriptionConfiguration> assignments = Lists.newArrayList();
				for (Object o : collection) {
					assignments.add((SubscriptionConfiguration) o);
				}
				return assignments;
			}
		});

		SQLQuery count = getFactory().getCurrentSession()
				.createSQLQuery("SELECT COUNT(*) FROM (" + sqlQuery + ") as count");
		count.setParameter("approvedApprovalStatus", ApprovalStatus.APPROVED.ordinal());
		count.setParameter("pendingApprovalStatus", ApprovalStatus.PENDING.ordinal());
		count.setParameter("pendingSubscriptionStatus", SubscriptionStatusType.PENDING);
		count.setParameter("activeSubscriptionStatus", SubscriptionStatusType.ACTIVE);

		pagination.setResults(query.list());
		if (count.list().size() > 0)
			pagination.setRowCount(((BigInteger) count.list().get(0)).intValue());
		else
			pagination.setRowCount(0);

		return pagination;
	}

	@Override
	public Set<SubscriptionConfiguration> findApprovedSubscriptionConfigurationsWithTransactionalPricingByEffectiveDate(Calendar effectiveDate) {
		Assert.notNull(effectiveDate);
		Criteria criteria = getFactory().getCurrentSession()
				.createCriteria(getEntityClass())
				.createAlias("company", "company")
				.createAlias("company.paymentConfiguration", "paymentConfiguration")
				.createAlias("paymentConfiguration.accountPricingType", "accountPricingType")
				.createAlias("subscriptionCancellation", "subscriptionCancellation", Criteria.LEFT_JOIN)
				.add(Restrictions.eq("accountPricingType.code", AccountPricingType.TRANSACTIONAL_PRICING_TYPE))
				.add(Restrictions.eq("subscriptionStatusType.code", SubscriptionStatusType.ACTIVE))
				.add(Restrictions.le("effectiveDate", effectiveDate))
				.add(Restrictions.gt("endDate", effectiveDate))
				.add(Restrictions.disjunction()
						.add(Restrictions.gt("subscriptionCancellation.approvedOn", effectiveDate))
						.add(Restrictions.isNull("subscriptionCancellation.approvedOn")))
				.add(Restrictions.eq("deleted", false));
		return Sets.newHashSet(criteria.list());
	}

	@Override
	public Set<SubscriptionConfiguration> findAllSubscriptionConfigurationsWithNextThroughputReset(Calendar updateDate) {
		String sqlQuery =
				" SELECT this.*"
						+ " FROM subscription_configuration this"
						+ " WHERE this.approval_status = 1"
						+ " AND this.subscription_status_type_code = :activeStatus "
						+ " AND this.next_throughput_reset_date <= :updateDate "
						+ " AND this.deleted = false";

		SQLQuery query = getFactory().getCurrentSession()
				.createSQLQuery(sqlQuery)
				.addEntity(SubscriptionConfiguration.class);
		query.setParameter("updateDate", DateUtilities.formatCalendarForSQL(updateDate));
		query.setParameter("activeStatus", SubscriptionStatusType.ACTIVE);
		query.setResultTransformer(CriteriaSpecification.DISTINCT_ROOT_ENTITY);

		return Sets.newHashSet(query.list());
	}


	@Override
	public Set<SubscriptionConfiguration> findAllUpdatableSubscriptionConfigurationsByUpdateDate(Calendar updateDate) {
		Assert.notNull(updateDate);

		String expirableSubscriptions =
				" SELECT this.*, this.end_date as updateDate "
						+ " FROM subscription_configuration this"
						+ " WHERE this.approval_status = 1"
						+ " AND this.end_date <= :updateDate "
						+ " AND this.subscription_status_type_code = :activeStatus "
						+ " AND this.deleted = false";

		String updatableFeeConfiguration =
				" SELECT this.*, feeConfiguration.effective_date as updateDate"
						+ " FROM subscription_configuration this"
						+ " INNER JOIN subscription_fee_configuration feeConfiguration ON (feeConfiguration.subscription_configuration_id = this.id)"
						+ " WHERE this.approval_status = 1"
						+ " AND this.subscription_status_type_code = :activeStatus "
						+ " AND feeConfiguration.approval_status = 1"
						+ " AND this.deleted = false"
						+ " AND feeConfiguration.deleted = false"
						+ " AND feeConfiguration.active = false"
						+ " AND feeConfiguration.effective_date <= :updateDate ";

		String cancellableSubscriptions =
				" SELECT this.*, cancellation.effective_date as updateDate"
						+ " FROM subscription_configuration this"
						+ " INNER JOIN subscription_cancellation cancellation ON (cancellation.subscription_configuration_id = this.id)"
						+ " WHERE this.approval_status = 1"
						+ " AND this.subscription_status_type_code in ('active', 'renewal') "
						+ " AND cancellation.approval_status = 1"
						+ " AND cancellation.effective_date <= :updateDate "
						+ " AND this.deleted = false"
						+ " AND cancellation.deleted = false";

		String updatableAddOnAssociations =
				" SELECT this.*, association.effective_date as updateDate"
						+ " FROM subscription_configuration this"
						+ " INNER JOIN subscription_add_on_type_association association ON (association.subscription_configuration_id = this.id)"
						+ " WHERE this.approval_status = 1"
						+ " AND this.subscription_status_type_code = :activeStatus "
						+ " AND association.approval_status = 1"
						+ " AND association.active = false"
						+ " AND association.effective_date <= :updateDate "
						+ " AND this.deleted = false"
						+ " AND association.deleted = false"
						+ " ORDER BY updateDate DESC";

		String sqlQuery = expirableSubscriptions
				+ " UNION"
				+ updatableFeeConfiguration
				+ " UNION"
				+ cancellableSubscriptions
				+ " UNION"
				+ updatableAddOnAssociations;

		SQLQuery query = getFactory().getCurrentSession()
				.createSQLQuery(sqlQuery)
				.addEntity(SubscriptionConfiguration.class);
		query.setParameter("updateDate", DateUtilities.formatCalendarForSQL(updateDate));
		query.setParameter("activeStatus", SubscriptionStatusType.ACTIVE);
		query.setResultTransformer(CriteriaSpecification.DISTINCT_ROOT_ENTITY);

		return Sets.newHashSet(query.list());
	}

	@Override
	public Set<SubscriptionConfiguration> findSubscriptionsPendingInvoiceByPaymentPeriodStartDate(Calendar periodStartDate) {
		String sql = "SELECT this.* \n" +
				" FROM 	subscription_configuration this \n" +
				" INNER JOIN payment_period  ON payment_period.subscription_configuration_id = this.id \n" +
				" WHERE this.subscription_status_type_code = :activeStatus " +
				" AND 	this.approval_status = 1 \n" +
				" AND 	this.deleted = false \n" +
				" AND 	payment_period.deleted = false \n" +
				" AND 	payment_period.subscription_invoice_id IS NULL \n" +
				" AND 	payment_period.period_start_date = this.next_payment_period_start_date \n" +
				" AND 	(this.next_payment_period_start_date >= :periodStartDate OR payment_period.period_start_date <= :periodStartDate)" +
				" AND  	DATEDIFF(this.next_payment_period_start_date, :periodStartDate ) <= 35";

		SQLQuery query = getFactory().getCurrentSession()
				.createSQLQuery(sql)
				.addEntity(SubscriptionConfiguration.class);
		query.setParameter("periodStartDate", DateUtilities.formatCalendarForSQL(periodStartDate));
		query.setParameter("activeStatus", SubscriptionStatusType.ACTIVE);
		return Sets.newHashSet(query.list());
	}

	@Override
	public Set<SubscriptionConfiguration> findSubscriptionRenewalsPendingInvoiceByPaymentPeriodStartDate(Calendar periodStartDate) {
		String sql = "SELECT this.* \n" +
				" FROM 	subscription_configuration this \n" +
				" INNER JOIN payment_period  ON payment_period.subscription_configuration_id = this.id \n" +
				" WHERE this.subscription_status_type_code = :renewal " +
				" AND 	this.approval_status = 1 \n" +
				" AND 	this.deleted = false \n" +
				" AND 	payment_period.deleted = false \n" +
				" AND 	payment_period.subscription_invoice_id IS NULL \n" +
				" AND 	payment_period.period_start_date = this.next_payment_period_start_date \n" +
				" AND 	this.next_payment_period_start_date <= :periodStartDate \n" +
				// check for the parent
				" AND  	EXISTS(SELECT id FROM subscription_configuration \n" +
				"		WHERE company_id = this.company_id \n" +
				"		AND id = this.parent_subscription_id \n" +
				"		AND subscription_status_type_code = 'active' AND approval_status = 1 \n" +
				" 		AND deleted = false)";

		SQLQuery query = getFactory().getCurrentSession()
				.createSQLQuery(sql)
				.addEntity(SubscriptionConfiguration.class);
		query.setParameter("periodStartDate", DateUtilities.formatCalendarForSQL(periodStartDate));
		query.setParameter("renewal", SubscriptionStatusType.PENDING_RENEWAL);
		return Sets.newHashSet(query.list());
	}

	@Override
	public void applySorts(Pagination<SubscriptionConfiguration> pagination, Criteria query, Criteria count) {
		if (pagination.hasSortColumn()) {
			String sort = pagination.getSortColumn();
			if (pagination.getSortDirection().equals(Pagination.SORT_DIRECTION.DESC)) {
				query.addOrder(Order.desc(sort));
			} else {
				query.addOrder(Order.asc(sort));
			}
		}
	}

	@Override
	public void applyFilters(Pagination<SubscriptionConfiguration> pagination, Criteria query, Criteria count) {

		if (pagination.hasFilters()) {
			if (pagination.hasFilter(SubscriptionConfigurationPagination.FILTER_KEYS.COMPANY_ID)) {
				long companyId = Long.valueOf(pagination.getFilter(SubscriptionConfigurationPagination.FILTER_KEYS.COMPANY_ID));
				query.add(Restrictions.eq("company.id", companyId));
				count.add(Restrictions.eq("company.id", companyId));
			}
			if (pagination.hasFilter(SubscriptionConfigurationPagination.FILTER_KEYS.EFFECTIVE_DATE_FROM)) {
				Calendar date = DateUtilities.getCalendarFromISO8601(pagination.getFilter(SubscriptionConfigurationPagination.FILTER_KEYS.EFFECTIVE_DATE_FROM));
				query.add(Restrictions.ge("effectiveDate", date));
				count.add(Restrictions.ge("effectiveDate", date));
			}
			if (pagination.hasFilter(SubscriptionConfigurationPagination.FILTER_KEYS.EFFECTIVE_DATE_TO)) {
				Calendar date = DateUtilities.getCalendarFromISO8601(pagination.getFilter(SubscriptionConfigurationPagination.FILTER_KEYS.EFFECTIVE_DATE_TO));
				query.add(Restrictions.le("effectiveDate", date));
				count.add(Restrictions.le("effectiveDate", date));
			}
			if (pagination.hasFilter(SubscriptionConfigurationPagination.FILTER_KEYS.APPROVAL_STATUS)) {
				ApprovalStatus status = ApprovalStatus.valueOf(pagination.getFilter(SubscriptionConfigurationPagination.FILTER_KEYS.APPROVAL_STATUS));
				if (status != null) {
					query.add(Restrictions.eq("approvalStatus", status));
					count.add(Restrictions.eq("approvalStatus", status));
				}
			}
			if (pagination.hasFilter(SubscriptionConfigurationPagination.FILTER_KEYS.SUBSCRIPTION_STATUS)) {
				String statusCode = pagination.getFilter(SubscriptionConfigurationPagination.FILTER_KEYS.SUBSCRIPTION_STATUS);
				if (isNotEmpty(statusCode)) {
					query.add(Restrictions.eq("subscriptionStatusType.code", statusCode));
					count.add(Restrictions.eq("subscriptionStatusType.code", statusCode));
				}

			}
		}
	}

	@Override
	public void buildWhereClause(Criteria query, Criteria count, Map<String, Object> params) {
		query.add(Restrictions.eq("deleted", false));
		count.add(Restrictions.eq("deleted", false));
	}

	@Override
	public SubscriptionConfiguration findLatestPendingApprovalSubscriptionConfigurationByCompanyId(long companyId) {
		Criteria criteria = getFactory().getCurrentSession()
				.createCriteria(getEntityClass())
				.add(Restrictions.eq("company.id", companyId))
				.add(Restrictions.eq("subscriptionStatusType.code", SubscriptionStatusType.PENDING))
				.add(Restrictions.eq("approvalStatus", ApprovalStatus.PENDING))
				.add(Restrictions.eq("deleted", false))
				.addOrder(Order.desc("modifiedOn"));
		return (SubscriptionConfiguration) criteria.uniqueResult();
	}

	@Override
	public SubscriptionConfiguration findLatestNotReadySubscriptionConfigurationByCompanyId(long companyId) {
		Criteria criteria = getFactory().getCurrentSession()
				.createCriteria(getEntityClass())
				.add(Restrictions.eq("company.id", companyId))
				.add(Restrictions.eq("subscriptionStatusType.code", SubscriptionStatusType.PENDING))
				.add(Restrictions.eq("approvalStatus", ApprovalStatus.NOT_READY))
				.add(Restrictions.eq("deleted", false))
				.addOrder(Order.desc("modifiedOn"));
		return (SubscriptionConfiguration) criteria.uniqueResult();
	}

	@Override
	public Set<SubscriptionConfiguration> findPreviousSubscriptionConfigurationsByCompanyId(long companyId) {
		Criteria criteria = getFactory().getCurrentSession()
				.createCriteria(getEntityClass())
				.setFetchMode("notes", FetchMode.JOIN)
				.setFetchMode("activeSubscriptionAddOns", FetchMode.JOIN)
				.add(Restrictions.eq("company.id", companyId))
				.add(Restrictions.disjunction()
						.add(Restrictions.eq("subscriptionStatusType.code", SubscriptionStatusType.EXPIRED))
						.add(Restrictions.eq("subscriptionStatusType.code", SubscriptionStatusType.CANCELLED)))
				.add(Restrictions.eq("deleted", false))
				.addOrder(Order.desc("effectiveDate"));
		return Sets.newHashSet(criteria.list());
	}

	@Override
	public SubscriptionConfiguration findRenewSubscriptionConfiguration(long subscriptionId) {
		Criteria criteria = getFactory().getCurrentSession()
				.createCriteria(getEntityClass())
				.add(Restrictions.eq("parentSubscription.id", subscriptionId))
				.add(Restrictions.eq("deleted", false));
		return (SubscriptionConfiguration) criteria.uniqueResult();
	}


}