package com.workmarket.domains.model.account;

import java.math.BigDecimal;
import java.util.Calendar;

import javax.persistence.*;

import org.hibernate.annotations.Type;

import com.workmarket.domains.model.audit.AuditedEntity;
import com.workmarket.domains.work.model.Work;
import com.workmarket.domains.model.audit.AuditChanges;

@Entity(name = "register_transaction")
@Table(name = "register_transaction")
@Inheritance(strategy = InheritanceType.JOINED)
@NamedQueries({
		@NamedQuery(name = "registertransaction.all", query = "from register_transaction rt join fetch rt.registerTransactionType join fetch rt.accountRegister join fetch rt.accountRegister.company where rt.accountRegister.id = :registerid"),

		// Sum
		@NamedQuery(name = "registertransaction.paymentsByAccountRegisterIdAndDate", query = "select sum(rt.amount) from register_transaction rt where rt.accountRegister.id = :accountRegisterId and rt.registerTransactionType = 'payment' and rt.transactionDate > :fromDate"),

		@NamedQuery(name = "registertransaction.commitments", query = "select sum(rt.amount) from register_transaction rt where rt.accountRegister.id = :registerid and rt.pendingFlag = 'Y' and rt.registerTransactionType = 'commitment'"),
		@NamedQuery(name = "registertransaction.paymentTermsCommitments", query = "select sum(rt.amount) from register_transaction rt where rt.accountRegister.id = :registerid and rt.pendingFlag = 'Y' and rt.registerTransactionType = 'pytrmscmmt'"),

		@NamedQuery(name = "registertransaction.paymenttermsresourcecommitments", query = "select sum(rt.amount) from register_transaction rt where rt.accountRegister.id = :registerid and rt.pendingFlag = 'Y' and rt.registerTransactionType = 'pytrmspyct'"),

		@NamedQuery(name = "registertransaction.withdrawables", query = "select sum(rt.amount) from register_transaction rt where rt.accountRegister.id = :registerid and (rt.registerTransactionType.code in('removefund','removefpp','wrkpayment','cancelpay','finwork3','finwork2','lane2work','lane3work'))"),
		@NamedQuery(name = "registertransaction.totalwithdrawalsfortoday", query = "select sum(rt.amount) from register_transaction rt where rt.accountRegister.id = :registerid and rt.registerTransactionType.code in ('removefund', 'removefpp') and  rt.transactionDate > :start and rt.transactionDate < :end"),

		@NamedQuery(name = "registertransaction.allfundingtransactions", query = "from register_transaction rt join fetch rt.registerTransactionType join fetch rt.accountRegister join fetch rt.accountRegister.company where rt.registerTransactionType.code in ('addfunds', 'directDep', 'checkDep') and amount > 100.00 order by rt.createdOn desc "),
		@NamedQuery(name = "registertransaction.fundingtransactionsbydate", query = "from register_transaction rt join fetch rt.registerTransactionType join fetch rt.accountRegister join fetch rt.accountRegister.company where rt.registerTransactionType.code in ('addfunds', 'directDep', 'checkDep') and amount > 100.00 and rt.transactionDate > :fromDate and rt.transactionDate <= :toDate order by rt.createdOn desc "),

		// reporting queries for client services/banking integration
		@NamedQuery(name = "bankaccounttransaction.byTypeAndStatus", query = "from bank_account_transaction rt join fetch rt.bankAccount join fetch rt.bankAccount.company " +
				" where rt.registerTransactionType.code = :type " +
				" and 	rt.bankAccountTransactionStatus.code = :status "),

		@NamedQuery(name = "bankaccounttransaction.byTypeAndStatusAndCountry", query = "from bank_account_transaction rt join fetch rt.bankAccount join fetch rt.bankAccount.company " +
				" where rt.registerTransactionType.code = :type " +
				" and 	rt.bankAccountTransactionStatus.code = :status " +
				" and 	rt.bankAccount.country.id = :countryId"),

		// queries to select transactions for a piece of work
		@NamedQuery(name = "registertransaction.findworkcommitments", query = "from work_resource_transaction rt where rt.workResource.id = :workresourceid  and rt.registerTransactionType.code in ('paycommit', 'commitment', 'pytrmspyct', 'pytrmscmmt')"),
		@NamedQuery(name = "registertransaction.findpaymenttermsworkcommitmentsByWork", query = "from work_resource_transaction rt where rt.work.id = :workId and rt.registerTransactionType.code in ('pytrmspyct', 'pytrmscmmt', 'paycommit', 'commitment') AND rt.pendingFlag = 'Y'"),

		@NamedQuery(name = "registertransaction.findByWorkId", query = "from register_transaction rt where rt.work.id = :workId"),
		@NamedQuery(name = "registertransaction.findByWorkIdAndPending", query = "from register_transaction rt where rt.work.id = :workId and rt.pendingFlag = 'Y' and rt.registerTransactionType.code in ('pytrmscmmt', 'commitment')"),

		@NamedQuery(name = "registertransaction.findworkresourcecommitment", query = "from work_resource_transaction rt where rt.work.id = :workid and rt.pendingFlag = 'Y' and rt.registerTransactionType = 'commitment'"),
		@NamedQuery(name = "registertransaction.findworkresourcepytrmscmmt", query = "from work_resource_transaction rt where rt.work.id = :workid and rt.pendingFlag = 'Y' and rt.registerTransactionType = 'pytrmscmmt'"),
		@NamedQuery(name = "registertransaction.findworkresourcepytrmspyct", query = "from work_resource_transaction rt where rt.work.id = :workid and rt.pendingFlag = 'Y' and rt.registerTransactionType = 'pytrmspyct'"),

		@NamedQuery(name = "registertransaction.findworkresourcebyworkidpending", query = "from work_resource_transaction rt where rt.work.id = :workid and rt.pendingFlag = 'Y' and rt.registerTransactionType != 'addfunds'"),
		@NamedQuery(name = "registertransaction.findworkresourcetransactionpayment", query = "from work_resource_transaction rt where rt.workResource.id = :workresourceid  and rt.registerTransactionType.code = 'payment'"),
		@NamedQuery(name = "registertransaction.findworkresourcetransactionpytrmspyct", query = "from work_resource_transaction rt where rt.workResource.id = :workresourceid  and rt.registerTransactionType.code = 'pytrmspyct' order by id desc"),

		// queries for ach and banking related transactions
		@NamedQuery(name = "registertransaction.findachverificationsforaccount", query = "from bank_account_transaction rt where rt.registerTransactionType.code = 'achverify' and rt.bankAccount.id = :bankaccountid "),
		@NamedQuery(name = "registertransaction.findachverificationsbystatus", query = "from bank_account_transaction rt where rt.registerTransactionType.code = 'achverify' and rt.bankAccountTransactionStatus.code = :status"),
		@NamedQuery(name = "registertransaction.findtransactionforbankingintegrationrequest", query = "from bank_account_transaction rt where rt.registerTransactionType.code = 'achverify' and rt.bankAccountTransactionStatus.code = :status"),

		// find the last deposit amount
		@NamedQuery(name = "registertransaction.findlastdepositamount", query = "select amount from register_transaction rt where rt.accountRegister.id = :registerid and rt.registerTransactionType.code in ('addfunds', 'directDep', 'checkDep') order by rt.transactionDate DESC limit 1"),

		// find a credit card transaction
		@NamedQuery(name = "registertransaction.findCreditCardTransactionById", query = "from credit_card_transaction cct where cct.id = :transactionId and cct.accountRegister.company.id = :companyId and cct.registerTransactionType.code = 'addfunds'"),
		@NamedQuery(name = "registertransaction.findBankAccountTransactionById", query = "from bank_account_transaction bat where bat.id = :transactionId and bat.accountRegister.company.id = :companyId and bat.registerTransactionType.code = 'addfunds'"),
		@NamedQuery(name = "registertransaction.findWireOrCheckTransactionById", query = "from register_transaction rt where rt.id = :transactionId and rt.accountRegister.company.id = :companyId and rt.registerTransactionType.code in ('directDep', 'checkDep')"),

		//Work bundle auth
		@NamedQuery(name = "registerTransaction.findWorkBundlePendingAuthorization", query = "FROM workBundleTransaction wbt WHERE wbt.work.id = :workId and wbt.pendingFlag = 'Y' AND wbt.registerTransactionType.code IN ('bundleAuth', 'bundlePPAu') "),
		@NamedQuery(name = "registerTransaction.findWorkBundleAuthorization", query = "FROM workBundleTransaction wbt WHERE wbt.work.id = :workId AND wbt.registerTransactionType.code IN ('bundleAuth', 'bundlePPAu') ")

})
@AuditChanges
public class RegisterTransaction extends AuditedEntity implements Cloneable {

	private static final long serialVersionUID = 1L;

	private Calendar transactionDate;
	private Calendar effectiveDate;
	private BigDecimal amount;
	private RegisterTransactionType registerTransactionType;
	private AccountRegister accountRegister;
	private Boolean pendingFlag;
	private Work work;
	private AccountRegisterSummaryFields accountRegisterSummaryFields;

	@Column(name = "transaction_date", nullable = false)
	public Calendar getTransactionDate() {
		return transactionDate;
	}

	@Column(name = "amount")
	public BigDecimal getAmount() {
		return amount;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "register_transaction_type_code", referencedColumnName = "code")
	public RegisterTransactionType getRegisterTransactionType() {
		return registerTransactionType;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "account_register_id", referencedColumnName = "id", nullable = false, updatable = false)
	public AccountRegister getAccountRegister() {
		return accountRegister;
	}

	@Column(name = "pending_flag")
	@Type(type = "yes_no")
	public Boolean getPendingFlag() {
		return pendingFlag;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "work_id", referencedColumnName = "id", updatable = false)
	public Work getWork() {
		return work;
	}

	public RegisterTransaction setAmount(BigDecimal amount) {
		this.amount = amount;
		return this;
	}

	public RegisterTransaction setTransactionDate(Calendar transactionDate) {
		this.transactionDate = transactionDate;
		return this;
	}

	public RegisterTransaction setRegisterTransactionType(
			RegisterTransactionType registerTransactionType) {
		this.registerTransactionType = registerTransactionType;
		return this;
	}

	public RegisterTransaction setAccountRegister(AccountRegister accountRegister) {
		this.accountRegister = accountRegister;
		return this;
	}

	public RegisterTransaction setPendingFlag(Boolean pendingFlag) {
		this.pendingFlag = pendingFlag;
		return this;
	}

	public void setWork(Work work) {
		this.work = work;
	}

	@Embedded
	public AccountRegisterSummaryFields getAccountRegisterSummaryFields() {
		return accountRegisterSummaryFields;
	}

	public void setAccountRegisterSummaryFields(AccountRegisterSummaryFields accountRegisterSummaryFields) {
		this.accountRegisterSummaryFields = accountRegisterSummaryFields;
	}

	public RegisterTransaction clone() throws CloneNotSupportedException {
		RegisterTransaction rt = (RegisterTransaction) super.clone();
		rt.setId(null);
		rt.setTransactionDate(this.getTransactionDate());
		rt.setEffectiveDate(this.getEffectiveDate());
		return rt;
	}

	@Transient
	public void copyStatus(RegisterTransaction tx) {
		setPendingFlag(tx.getPendingFlag());
		if (!tx.getPendingFlag()) {
			setEffectiveDate(Calendar.getInstance());
		}
	}

	@Column(name = "effective_date", nullable = false)
	public Calendar getEffectiveDate() {
		return effectiveDate;
	}

	public RegisterTransaction setEffectiveDate(Calendar effectiveDate) {
		this.effectiveDate = effectiveDate;
		return this;
	}

	@Transient
	public boolean isPending() {
		return getPendingFlag();
	}

	@Override public String toString() {
		return "RegisterTransaction{" +
				"accountRegister=" + accountRegister +
				", transactionDate=" + transactionDate +
				", effectiveDate=" + effectiveDate +
				", amount=" + amount +
				", registerTransactionType=" + registerTransactionType +
				", pendingFlag=" + pendingFlag +
				'}';
	}
}
