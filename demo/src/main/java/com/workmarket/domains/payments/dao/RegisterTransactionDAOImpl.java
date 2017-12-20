package com.workmarket.domains.payments.dao;

import com.google.common.base.MoreObjects;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

import com.workmarket.dao.PaginationAbstractDAO;
import com.workmarket.domains.model.DateFilter;
import com.workmarket.domains.model.Pagination;
import com.workmarket.domains.model.account.BankAccountTransaction;
import com.workmarket.domains.model.account.BankAccountTransactionStatus;
import com.workmarket.domains.model.account.CreditCardTransaction;
import com.workmarket.domains.model.account.InvoicePaymentTransaction;
import com.workmarket.domains.model.account.RegisterTransaction;
import com.workmarket.domains.model.account.RegisterTransactionPagination;
import com.workmarket.domains.model.account.RegisterTransactionType;
import com.workmarket.domains.model.account.ServiceTransaction;
import com.workmarket.domains.model.account.WorkBundleTransaction;
import com.workmarket.domains.model.account.WorkResourceTransaction;
import com.workmarket.domains.model.invoice.AbstractInvoice;
import com.workmarket.domains.model.postalcode.Country;
import com.workmarket.utility.CollectionUtilities;
import com.workmarket.utility.DateUtilities;
import com.workmarket.utility.StringUtilities;
import com.workmarket.utility.sql.SQLBuilder;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.FetchMode;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.util.Assert;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.apache.commons.collections.CollectionUtils.isNotEmpty;

@Repository
@SuppressWarnings({"unchecked", "JpaQueryApiInspection"})
public class RegisterTransactionDAOImpl extends PaginationAbstractDAO<RegisterTransaction> implements RegisterTransactionDAO {

	protected Class<RegisterTransaction> getEntityClass() {
		return RegisterTransaction.class;
	}

	@Qualifier("jdbcTemplate")
	@Autowired
	private NamedParameterJdbcTemplate jdbcTemplate;

	private static final String SUBSCRIPTION_THROUGHPUT_SQL = " SELECT COALESCE(SUM(rt.amount),0) AS amount " +
			" FROM 	register_transaction rt " +
			" INNER JOIN work_resource_transaction wr ON wr.id = rt.id " +
			" WHERE rt.account_register_id = :accountRegisterId " +
			" AND 	rt.pending_flag = 'N' " +
			" AND 	rt.register_transaction_type_code = :payment " +
			" AND 	wr.account_pricing_type_code = 'subscription' " +
			" AND 	rt.effective_date >= :fromDate";

	private static final String INCREMENTAL_UNINVOICED_SUBSCRIPTION_TRANSACTION_AMOUNT = "select COALESCE(abs(SUM(r.amount)), 0.00)\n" +
		"from register_transaction r,\n" +
		"account_register ar,\n" +
		"service_transaction st\n" +
		"where st.invoiced = 0\n" +
		"and st.subscription_incremental_transaction = 1\n" +
		"and st.subscription_payment_period_id = :paymentPeriod\n" +
		"and st.id = r.id\n" +
		"and r.register_transaction_type_code = :registerTransactionTypeCode\n" +
		"and r.account_register_id = ar.id\n" +
		"and ar.company_id = :companyId";


	@Override
	public BigDecimal calculateAvailableCashByAccountRegister(Long accountRegisterId) {
		String sql = " SELECT COALESCE(SUM(rt.amount),0) AS amount " +
				" FROM 	register_transaction rt " +
				" WHERE rt.account_register_id = :accountRegisterId " +
				" AND 	rt.pending_flag = 'N' " +
				" AND 	rt.register_transaction_type_code IN (" + StringUtils.join(StringUtilities.surround(RegisterTransactionType.AVAILABLE_CASH_INCREASE_REGISTER_TRANSACTION_TYPES, "'"), ",") + ")";


		MapSqlParameterSource params = new MapSqlParameterSource();
		params.addValue("accountRegisterId", accountRegisterId);
		return jdbcTemplate.queryForObject(sql, params, BigDecimal.class);
	}

	@Override
	public BigDecimal sumSpentAvailableCash(Long accountRegisterId) {
		String sql = "SELECT sum(amount) FROM ( " +
				" SELECT rt.amount,  bankT.bank_account_transaction_status_code" +
				" FROM  register_transaction rt" +
				" LEFT  OUTER JOIN  bank_account_transaction bankT ON rt.id = bankT.id" +
				" WHERE  rt.account_register_id = :accountRegisterId" +
				" AND  ( " +
				"(rt.pending_flag = 'N' and rt.register_transaction_type_code IN (" + StringUtils.join(StringUtilities.surround(RegisterTransactionType.AVAILABLE_CASH_DECREASE_REGISTER_TRANSACTION_TYPES, "'"), ",") + ")" +
				" ) OR   (rt.pending_flag = 'Y' and rt.register_transaction_type_code = 'commitment'))) as activity" +
				" WHERE  IFNULL(bank_account_transaction_status_code, 'empty') <> 'rejected'";

		SQLQuery sqlQuery = getFactory().getCurrentSession().createSQLQuery(sql);
		sqlQuery.setLong("accountRegisterId", accountRegisterId);

		BigDecimal result = (BigDecimal) sqlQuery.uniqueResult();
		if (result != null) {
			return result;
		}
		return BigDecimal.ZERO;
	}

	@Override
	public BigDecimal paymentsByAccountRegisterIdAndDate(Long accountRegisterId, Date fromDate) {
		Query query = getFactory().getCurrentSession().getNamedQuery("registertransaction.paymentsByAccountRegisterIdAndDate");
		query.setLong("accountRegisterId", accountRegisterId);
		query.setDate("fromDate", fromDate);
		BigDecimal result = (BigDecimal) query.uniqueResult();
		if (result == null)
			return new BigDecimal(0);

		return result;
	}

	@Override
	public BigDecimal findPaymentTermsCommitmentBalance(Long accountRegisterId) {
		return runQuery("registertransaction.paymentTermsCommitments", accountRegisterId);
	}

	@Override
	public WorkResourceTransaction findWorkResourcePendingCommitmentTransaction(Long workId) {
		Query query = getFactory().getCurrentSession().getNamedQuery("registertransaction.findworkresourcecommitment");
		query.setLong("workid", workId);
		return (WorkResourceTransaction) query.uniqueResult();
	}

	@Override
	public RegisterTransaction findPendingAuthorizationTransactionsByWorkId(Long workId) {
		Query query = getFactory().getCurrentSession().getNamedQuery("registertransaction.findByWorkIdAndPending");
		query.setLong("workId", workId);
		return (RegisterTransaction) query.uniqueResult();
	}

	@Override
	public WorkResourceTransaction findWorkResourcePendingPaymentTermsCommitmentTransaction(Long workId) {
		Query query = getFactory().getCurrentSession().getNamedQuery("registertransaction.findworkresourcepytrmscmmt");
		query.setLong("workid", workId);
		return (WorkResourceTransaction) query.uniqueResult();
	}

	@Override
	public WorkResourceTransaction findWorkResourcePendingPaymentTermsCommitmentReceivePayTransaction(Long workId) {
		Query query = getFactory().getCurrentSession().getNamedQuery("registertransaction.findworkresourcepytrmspyct");
		query.setLong("workid", workId);
		return (WorkResourceTransaction) query.uniqueResult();
	}

	@Override
	public List<WorkResourceTransaction> findWorkResourceTransactionWorkIdPending(Long workId) {
		Query query = getFactory().getCurrentSession().getNamedQuery("registertransaction.findworkresourcebyworkidpending");
		query.setLong("workid", workId);
		return query.list();
	}

	@Override
	public BigDecimal findTotalWithdrawalsForTodayBalance(Long accountRegisterId) {
		Calendar start = DateUtilities.getMidnightToday();
		Calendar end = DateUtilities.getMidnightTomorrow();

		Query query = getFactory().getCurrentSession().getNamedQuery("registertransaction.totalwithdrawalsfortoday");
		query.setParameter("registerid", accountRegisterId);
		query.setParameter("start", start);
		query.setParameter("end", end);

		return (BigDecimal) MoreObjects.firstNonNull(query.uniqueResult(), BigDecimal.ZERO);
	}

	@Override
	public List<WorkResourceTransaction> findWorkResourceTransactionPaymentTermsCommitmentReceivePay(Long workResourceId) {

		Query query = getFactory().getCurrentSession().getNamedQuery("registertransaction.findworkresourcetransactionpytrmspyct");
		query.setLong("workresourceid", workResourceId);
		return query.list();
	}

	@Override
	public List<BankAccountTransaction> findACHVerificationTransactions(
			Long bankAccountId) {
		Query query = getFactory().getCurrentSession().getNamedQuery("registertransaction.findachverificationsforaccount")
			.setLong("bankaccountid", bankAccountId);
		return query.list();
	}

	@Override public List<BankAccountTransaction> findBankAccountTransactions(String type, String status) {
		Query query = getFactory().getCurrentSession().getNamedQuery("bankaccounttransaction.byTypeAndStatus")
			.setString("type", type)
			.setString("status", status);

		return query.list();
	}

	@Override
	public List<BankAccountTransaction> findBankAccountTransactions(String type, String status, Country country) {
		if (country == null) {
			return findBankAccountTransactions(type, status);
		}
		Query query = getFactory().getCurrentSession().getNamedQuery("bankaccounttransaction.byTypeAndStatusAndCountry")
				.setString("type", type)
				.setString("status", status)
				.setString("countryId", country.getId());

		return query.list();
	}

	@Override
	public List<RegisterTransaction> findFundingTransactionsByDate(DateFilter datefilter) {

		if (datefilter == null || datefilter.getFromDate() == null || datefilter.getToDate() == null) {
			return Collections.EMPTY_LIST;
		}

		Query query = getFactory().getCurrentSession().getNamedQuery("registertransaction.fundingtransactionsbydate");
		query.setDate("fromDate", datefilter.getFromDate().getTime());
		query.setDate("toDate", datefilter.getToDate().getTime());
		return query.list();
	}

	@Override
	public List<RegisterTransaction> findAllWorkResourceTransactionsPending(Long workId) {
		Query query = getFactory().getCurrentSession().getNamedQuery("registertransaction.findpaymenttermsworkcommitmentsByWork")
				.setLong("workId", workId);
		return query.list();
	}

	@Override
	public Collection<RegisterTransaction> findAllRegisterTransactions(Long accountRegisterId) {
		Query query = getFactory().getCurrentSession().getNamedQuery("registertransaction.all")
				.setLong("registerid", accountRegisterId);
		return query.list();
	}

	@Override
	public List<InvoicePaymentTransaction> findAllInvoicePaymentTransactionsByFulfillmentStatus(String paymentFulfillmentStatusTypeCode) {
		Criteria criteria = getFactory().getCurrentSession().createCriteria(InvoicePaymentTransaction.class)
				.setFetchMode("invoice", FetchMode.JOIN)
				.createAlias("invoice", "invoice")
				.add(Restrictions.eq("invoice.paymentFulfillmentStatusType.code", paymentFulfillmentStatusTypeCode))
				.addOrder(Order.asc("transactionDate"));

		return criteria.list();
	}

	public void applySorts(Pagination<RegisterTransaction> pagination, Criteria query, Criteria count) {
		query.addOrder(Order.desc("transactionDate"));
	}

	public void applyFilters(Pagination<RegisterTransaction> pagination, Criteria criteria, Criteria count) {

		DateFormat df = new SimpleDateFormat("MM/dd/yy");
		if (pagination.getFilters() != null) {
			if (pagination.getFilters().get(RegisterTransactionPagination.FILTER_KEYS.BEFORE) != null) {

				String beforeDate = pagination.getFilters().get(RegisterTransactionPagination.FILTER_KEYS.BEFORE);
				Date before;
				try {
					before = df.parse(beforeDate);
				} catch (ParseException e) {
					throw new IllegalArgumentException("Cannot parse date in pagination for before: " + beforeDate, e);
				}
				Calendar beforeCal = Calendar.getInstance();
				beforeCal.setTime(before);
				beforeCal.add(Calendar.DAY_OF_YEAR, 1);
				criteria.add(Restrictions.le("transactionDate", beforeCal));
				count.add(Restrictions.le("transactionDate", beforeCal));

			}

			if (pagination.getFilters().get(RegisterTransactionPagination.FILTER_KEYS.SINCE) != null) {

				String sinceDateString = pagination.getFilters().get(RegisterTransactionPagination.FILTER_KEYS.SINCE);
				Date since;
				try {
					since = df.parse(sinceDateString);
				} catch (ParseException e) {
					throw new IllegalArgumentException("Cannot parse since date string: " + sinceDateString, e);
				}
				Calendar sinceCal = Calendar.getInstance();
				sinceCal.setTime(since);
				criteria.add(Restrictions.ge("transactionDate", sinceCal));
				count.add(Restrictions.ge("transactionDate", sinceCal));

			}
		}
	}

	public void buildWhereClause(Criteria query, Criteria count, Map<String, Object> params) {

		query.add(Restrictions.eq("accountRegister.id", params.get("accountRegisterId")));
		count.add(Restrictions.eq("accountRegister.id", params.get("accountRegisterId")));

		query.add(Restrictions.or(

				Restrictions.and
						(Restrictions.eq("registerTransactionType.code", RegisterTransactionType.BUYER_COMMITMENT_TO_PAY),
								Restrictions.eq("pendingFlag", Boolean.TRUE)),

				Restrictions.and(
						Restrictions.and
								(Restrictions.ne("registerTransactionType.code", RegisterTransactionType.BUYER_COMMITMENT_TO_PAY),
										Restrictions.ne("registerTransactionType.code", RegisterTransactionType.ACH_VERIFY)),
						Restrictions.ne("registerTransactionType.code", RegisterTransactionType.BUYER_PAYMENT_TERMS_COMMITMENT))
		));

		count.add(Restrictions.or(

				Restrictions.and
						(Restrictions.eq("registerTransactionType.code", RegisterTransactionType.BUYER_COMMITMENT_TO_PAY),
								Restrictions.eq("pendingFlag", Boolean.TRUE)),

				Restrictions.and(
						Restrictions.and
								(Restrictions.ne("registerTransactionType.code", RegisterTransactionType.BUYER_COMMITMENT_TO_PAY),
										Restrictions.ne("registerTransactionType.code", RegisterTransactionType.ACH_VERIFY)),
						Restrictions.ne("registerTransactionType.code", RegisterTransactionType.BUYER_PAYMENT_TERMS_COMMITMENT))
		));

		query.setFetchMode("work", FetchMode.JOIN);
		query.setFetchMode("registerTransactionType", FetchMode.JOIN);
		query.createAlias("registerTransactionType", "registerTransactionType");
		query.setFetchMode("registerTransactionType.transactionType", FetchMode.JOIN);

	}

	private BigDecimal runQuery(String name, Long accountRegisterId) {
		Query query = getFactory().getCurrentSession().getNamedQuery(name);
		query.setLong("registerid", accountRegisterId);
		BigDecimal result = (BigDecimal) query.uniqueResult();
		if (result == null) {
			return new BigDecimal(0);
		}
		return result;
	}

	@Override
	public List<RegisterTransaction> findChildTransactions(Long txId) {
		SQLBuilder bankSql = new SQLBuilder()
				.addColumn("id")
				.addTable("bank_account_transaction")
				.addWhereClause("parent_transaction_id = :id")
				.addParam("id", txId);
		SQLBuilder secretSql = new SQLBuilder()
				.addColumn("id")
				.addTable("secret_transaction")
				.addWhereClause("parent_transaction_id = :id");

		Set<Long> childIds = Sets.newHashSet(jdbcTemplate.queryForList(String.format("%s UNION %s", bankSql.build(), secretSql.build()), bankSql.getParams(), Long.class));
		if (CollectionUtils.isEmpty(childIds))
			return Collections.emptyList();
		return get(childIds);
	}

	@Override
	public List<RegisterTransaction> findProjectChildTransactions(Long txId) {
		SQLBuilder bankSql = new SQLBuilder()
				.addColumn("id")
				.addTable("project_transaction")
				.addWhereClause("parent_transaction_id = :id")
				.addParam("id", txId);

		Set<Long> childIds = Sets.newHashSet(jdbcTemplate.queryForList(String.format("%s", bankSql.build()), bankSql.getParams(), Long.class));
		if (CollectionUtils.isEmpty(childIds))
			return Collections.emptyList();
		return get(childIds);
	}

	@Override
	public List<RegisterTransaction> findGeneralChildTransactions(Long txId) {
		SQLBuilder bankSql = new SQLBuilder()
				.addColumn("id")
				.addTable("general_transaction")
				.addWhereClause("parent_transaction_id = :id")
				.addParam("id", txId);

		Set<Long> childIds = Sets.newHashSet(jdbcTemplate.queryForList(String.format("%s", bankSql.build()), bankSql.getParams(), Long.class));
		if (CollectionUtils.isEmpty(childIds))
			return Collections.emptyList();
		return get(childIds);
	}

	@Override
	public Map<Long, List<Long>> findAllSubscriptionTransactionPendingInvoice() {
		Criteria criteria = getFactory().getCurrentSession().createCriteria(ServiceTransaction.class)
				.add(Restrictions.eq("invoiced", false))
				.addOrder(Order.asc("accountRegister.id"))
				.addOrder(Order.asc("transactionDate"));

		List<ServiceTransaction> transactions = criteria.list();
		if (transactions.isEmpty()) {
			return Collections.EMPTY_MAP;
		}

		Map<Long, List<Long>> map = Maps.newLinkedHashMap();
		for (ServiceTransaction transaction : transactions) {
			if (!map.containsKey(transaction.getAccountRegister().getId())) {
				map.put(transaction.getAccountRegister().getId(), new ArrayList<Long>());
			}
			map.get(transaction.getAccountRegister().getId()).add(transaction.getId());
		}
		return map;
	}

	@Override
	public BigDecimal calculateIncrementalSubscriptionTransactionPendingInvoiceForPaymentPeriod(Long companyId, Long subscriptionPaymentPeriodId, String registerTransactionType) {
		MapSqlParameterSource params = new MapSqlParameterSource();
		params.addValue("companyId", companyId);
		params.addValue("paymentPeriod", subscriptionPaymentPeriodId);
		params.addValue("registerTransactionTypeCode", registerTransactionType);

		return jdbcTemplate.queryForObject(INCREMENTAL_UNINVOICED_SUBSCRIPTION_TRANSACTION_AMOUNT, params, BigDecimal.class);
	}

	@Override
	public CreditCardTransaction findCreditCardTransaction(Long transactionId, Long companyId) {
		Query query = getFactory().getCurrentSession().getNamedQuery("registertransaction.findCreditCardTransactionById");
		query.setLong("transactionId", transactionId);
		query.setLong("companyId", companyId);
		return (CreditCardTransaction) query.uniqueResult();
	}

	public BankAccountTransaction findBankAccountTransaction(Long transactionId, Long companyId) {
		Query query = getFactory().getCurrentSession().getNamedQuery("registertransaction.findBankAccountTransactionById");
		query.setLong("transactionId", transactionId);
		query.setLong("companyId", companyId);
		return (BankAccountTransaction) query.uniqueResult();
	}

	public RegisterTransaction findWireOrCheckTransaction(Long transactionId, Long companyId) {
		Query query = getFactory().getCurrentSession().getNamedQuery("registertransaction.findWireOrCheckTransactionById");
		query.setLong("transactionId", transactionId);
		query.setLong("companyId", companyId);
		return (RegisterTransaction) query.uniqueResult();
	}

	@Override
	public WorkBundleTransaction findWorkBundlePendingAuthorizationTransaction(long workBundleId) {
		Query query = getFactory().getCurrentSession().getNamedQuery("registerTransaction.findWorkBundlePendingAuthorization");
		query.setLong("workId", workBundleId);
		List<WorkBundleTransaction> bundleTransactions = query.list();
		if (isNotEmpty(bundleTransactions)) {
			return CollectionUtilities.first(bundleTransactions);
		}
		return null;
	}

	@Override
	public WorkBundleTransaction findWorkBundleAuthorizationTransaction(long workBundleId) {
		Query query = getFactory().getCurrentSession().getNamedQuery("registerTransaction.findWorkBundleAuthorization");
		query.setLong("workId", workBundleId);
		List<WorkBundleTransaction> bundleTransactions = query.list();
		if (isNotEmpty(bundleTransactions)) {
			return CollectionUtilities.first(bundleTransactions);
		}
		return null;
	}

	@Override
	public void markBankAccountTransactionProcessing(List<Long> transactionIds) {
		final String sql =
				" UPDATE bank_account_transaction "
						+ " SET bank_account_transaction_status_code = :status "
						+ " WHERE id in ( :transactionIds ) ";

		MapSqlParameterSource params = new MapSqlParameterSource();
		params.addValue("transactionIds", transactionIds);
		params.addValue("status", BankAccountTransactionStatus.PROCESSING);

		jdbcTemplate.update(sql, params);
	}

	@Override
	public BigDecimal calculateSubscriptionAssignmentThroughput(long accountRegisterId, Calendar fromDate) {
		Assert.notNull(fromDate);

		MapSqlParameterSource params = new MapSqlParameterSource()
				.addValue("accountRegisterId", accountRegisterId)
				.addValue("fromDate", fromDate)
				.addValue("payment", RegisterTransactionType.BUYER_WORK_PAYMENT);
		return jdbcTemplate.queryForObject(SUBSCRIPTION_THROUGHPUT_SQL, params, BigDecimal.class).abs();
	}

	@Override
	public BigDecimal calculateSubscriptionVORAssignmentThroughput(long accountRegisterId, Calendar fromDate) {
		Assert.notNull(fromDate);
		String sql = SUBSCRIPTION_THROUGHPUT_SQL + " AND wr.account_service_type_code = 'vor'";

		MapSqlParameterSource params = new MapSqlParameterSource()
				.addValue("accountRegisterId", accountRegisterId)
				.addValue("fromDate", fromDate)
				.addValue("payment", RegisterTransactionType.BUYER_WORK_PAYMENT);
		return jdbcTemplate.queryForObject(sql, params, BigDecimal.class).abs();
	}

	@Override
	public InvoicePaymentTransaction findInvoicePaymentTransactionByInvoice(AbstractInvoice invoice) {

		Criteria criteria = getFactory().getCurrentSession().createCriteria(InvoicePaymentTransaction.class);
		criteria.add(Restrictions.eq("invoice", invoice));
		if(criteria.uniqueResult() != null)
			return (InvoicePaymentTransaction)criteria.uniqueResult();
		else
			return null;
	}
}
