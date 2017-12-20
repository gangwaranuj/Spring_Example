package com.workmarket.domains.model.account;

import com.workmarket.domains.model.Company;
import com.workmarket.domains.model.audit.AuditedEntity;
import com.workmarket.configuration.Constants;
import com.workmarket.domains.model.audit.AuditChanges;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.Version;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity(name = "accountRegister")
@Table(name = "account_register")
@NamedQueries({
		//Find by companyId
		@NamedQuery(name = "accountregister.byCompanyId", query = "from accountRegister ar where ar.company.id = :companyId"),
		@NamedQuery(name = "accountregister.select_all_ids", query = "select id from accountRegister ar"),
		@NamedQuery(name = "accountregister.select_all", query = "from accountRegister ar")
})
@org.hibernate.annotations.Entity(dynamicUpdate = true)
@AuditChanges
public class AccountRegister extends AuditedEntity {

	/*
	 * Instance variables and constants
	 */
	private Company company;
	private BigDecimal credit;
	private Set<RegisterTransactionCost> registerTransactionCost;
	private Integer workCreditBalanceLane2;
	private Integer workCreditBalanceLane3;
	@Deprecated
	private BigDecimal paymentSummation = BigDecimal.ZERO;
	private BigDecimal currentWorkFeePercentage;
	private List<WorkFeeHistory> workFeeHistories;
	private List<WorkFeeConfiguration> workFeeConfigurations;
	private AccountRegisterSummaryFields accountRegisterSummaryFields;
	private Boolean initSummaries = Boolean.TRUE;
	private Integer workFeeLevel;
	private BigDecimal apLimit = BigDecimal.ZERO;
	private Long optimisticLockVersion;

	private static final long serialVersionUID = 31358315897231L;

	public AccountRegister() {
		super();

		workCreditBalanceLane2 = 0;
		workCreditBalanceLane3 = 0;
		registerTransactionCost = new HashSet<>();
		registerTransactionCost.add(new RegisterTransactionCost(RegisterTransactionCost.LANE2_NEW_WORK_FEE_DEFAULT_COST_ID));
		registerTransactionCost.add(new RegisterTransactionCost(RegisterTransactionCost.LANE3_NEW_WORK_FEE_DEFAULT_COST_ID));
		registerTransactionCost.add(new RegisterTransactionCost(RegisterTransactionCost.LANE2_FINISHED_WORK_FEE_DEFAULT_COST_ID));
		registerTransactionCost.add(new RegisterTransactionCost(RegisterTransactionCost.LANE3_FINISHED_WORK_FEE_DEFAULT_COST_ID));
		registerTransactionCost.add(new RegisterTransactionCost(RegisterTransactionCost.CREDIT_CARD_FEE_DEFAULT_COST_ID));
		registerTransactionCost.add(new RegisterTransactionCost(RegisterTransactionCost.BACKGROUND_CHECK_FEE_DEFAULT_COST_ID));
		registerTransactionCost.add(new RegisterTransactionCost(RegisterTransactionCost.DRUG_TEST_FEE_DEFAULT_COST_ID));
		registerTransactionCost.add(new RegisterTransactionCost(RegisterTransactionCost.LANE1_NEW_WORK_FEE_DEFAULT_COST_ID));
		registerTransactionCost.add(new RegisterTransactionCost(RegisterTransactionCost.LANE1_FINISHED_WORK_FEE_DEFAULT_COST_ID));
		registerTransactionCost.add(new RegisterTransactionCost(RegisterTransactionCost.BACKGROUND_CHECK_CANADA_FEE_DEFAULT_COST_ID));
		registerTransactionCost.add(new RegisterTransactionCost(RegisterTransactionCost.AMEX_CREDIT_CARD_FEE_DEFAULT_COST_ID));
		registerTransactionCost.add(new RegisterTransactionCost(RegisterTransactionCost.PAY_PAL_FEE_DEFAULT_COST_ID));
		registerTransactionCost.add(new RegisterTransactionCost(RegisterTransactionCost.PAY_PAL_FEE_CANADA_DEFAULT_COST_ID));
		registerTransactionCost.add(new RegisterTransactionCost(RegisterTransactionCost.PAY_PAL_FEE_INTL_DEFAULT_COST_ID));
		registerTransactionCost.add(new RegisterTransactionCost(RegisterTransactionCost.WM_PAY_PAL_FEE_DEFAULT_COST_ID));
		registerTransactionCost.add(new RegisterTransactionCost(RegisterTransactionCost.WM_PAY_PAL_FEE_CANADA_DEFAULT_COST_ID));
		registerTransactionCost.add(new RegisterTransactionCost(RegisterTransactionCost.WM_PAY_PAL_FEE_INTL_DEFAULT_COST_ID));
		registerTransactionCost.add(new RegisterTransactionCost(RegisterTransactionCost.BACKGROUND_CHECK_INTERNATIONAL_FEE_DEFAULT_COST_ID));

		workFeeLevel = 1;

		// net money fees
		BigDecimal defaultFee = Constants.DEFAULT_WORK_FEE_PERCENTAGE;
		currentWorkFeePercentage = defaultFee;

		WorkFeeConfiguration workFeeConfiguration = new WorkFeeConfiguration();
		List<WorkFeeBand> workFeeBands = new ArrayList<>();

		// For new companies, set one fee band with default fee
		WorkFeeBand workFeeBand = new WorkFeeBand();
		workFeeBand.setMinimum(BigDecimal.ZERO);
		workFeeBand.setMaximum(WorkFeeBand.MAXIMUM);
		workFeeBand.setPercentage(defaultFee);
		workFeeBand.setWorkFeeConfiguration(workFeeConfiguration);
		workFeeBands.add(workFeeBand);

		workFeeConfiguration.setWorkFeeBands(workFeeBands);
		workFeeConfiguration.setAccountRegister(this);
		workFeeConfiguration.setActive(true);
		workFeeConfiguration.setActiveDate(GregorianCalendar.getInstance());
		workFeeConfigurations = new ArrayList<>();
		workFeeConfigurations.add(workFeeConfiguration);

		workFeeHistories = new ArrayList<>();
		WorkFeeHistory workFeeHistory = new WorkFeeHistory();
		workFeeHistory.setActive(true);
		workFeeHistory.setPercentage(defaultFee);
		workFeeHistory.setWorkFeeConfiguration(workFeeConfiguration);
		workFeeHistory.setAccountRegister(this);
		workFeeHistory.setActiveDate(GregorianCalendar.getInstance());
		workFeeHistories.add(workFeeHistory);

	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "company_id", referencedColumnName = "id", nullable = false, insertable = false, updatable = false)
	public Company getCompany() {
		return company;
	}

	@Column(name = "credit")
	public BigDecimal getCredit() {
		return credit;
	}


	@ManyToMany
	@JoinTable(name = "account_register_transaction_cost",
			joinColumns = {@JoinColumn(name = "account_register_id")},
			inverseJoinColumns = {@JoinColumn(name = "register_transaction_cost_id")})
	public Set<RegisterTransactionCost> getRegisterTransactionCost() {
		return registerTransactionCost;
	}


	public void setCompany(Company company) {
		this.company = company;
	}

	public void setCredit(BigDecimal credit) {
		this.credit = credit;
	}


	public void setRegisterTransactionCost(
			Set<RegisterTransactionCost> registerTransactionCost) {
		this.registerTransactionCost = registerTransactionCost;
	}

	@Column(name = "work_credit_balance_lane2")
	public Integer getWorkCreditBalanceLane2() {
		return workCreditBalanceLane2;
	}

	@Column(name = "work_credit_balance_lane3")
	public Integer getWorkCreditBalanceLane3() {
		return workCreditBalanceLane3;
	}

	public void setWorkCreditBalanceLane2(Integer workCreditBalanceLane2) {
		this.workCreditBalanceLane2 = workCreditBalanceLane2;
	}

	public void setWorkCreditBalanceLane3(Integer workCreditBalanceLane3) {
		this.workCreditBalanceLane3 = workCreditBalanceLane3;
	}

	/**
	 * @return the paymentSummation
	 */
	@Column(name = "payment_summation", nullable = false)
	public BigDecimal getPaymentSummation() {
		return paymentSummation;
	}

	/**
	 * @param paymentSummation the paymentSummation to set
	 */
	public void setPaymentSummation(BigDecimal paymentSummation) {
		this.paymentSummation = paymentSummation;
	}


	@Column(name = "current_work_fee_percentage")
	public BigDecimal getCurrentWorkFeePercentage() {
		return currentWorkFeePercentage;
	}

	public void setCurrentWorkFeePercentage(BigDecimal currentWorkFeePercentage) {
		this.currentWorkFeePercentage = currentWorkFeePercentage;
	}

	@OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	@JoinColumn(name = "account_register_id", referencedColumnName = "id")
	public List<WorkFeeHistory> getWorkFeeHistories() {
		return workFeeHistories;
	}


	public void setWorkFeeHistories(List<WorkFeeHistory> workFeeHistories) {
		this.workFeeHistories = workFeeHistories;
	}

	@OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	@JoinColumn(name = "account_register_id", referencedColumnName = "id")
	public List<WorkFeeConfiguration> getWorkFeeConfigurations() {
		return workFeeConfigurations;
	}

	public void setWorkFeeConfigurations(List<WorkFeeConfiguration> workFeeConfigurations) {
		this.workFeeConfigurations = workFeeConfigurations;
	}

	@Embedded
	public AccountRegisterSummaryFields getAccountRegisterSummaryFields() {
		return accountRegisterSummaryFields;
	}

	public void setAccountRegisterSummaryFields(AccountRegisterSummaryFields accountRegisterSummaryFields) {
		this.accountRegisterSummaryFields = accountRegisterSummaryFields;
	}

	@Column(name = "init_summaries", nullable = true)
	public Boolean getInitSummaries() {
		return initSummaries;
	}

	public void setInitSummaries(Boolean initSummaries) {
		if (initSummaries != null)
			this.initSummaries = initSummaries;
	}

	@Column(name = "work_fee_level", nullable = false)
	public Integer getWorkFeeLevel() {
		return workFeeLevel;
	}

	public void setWorkFeeLevel(Integer workFeeLevel) {
		this.workFeeLevel = workFeeLevel;
	}

	@Column(name = "ap_limit", nullable = false)
	public BigDecimal getApLimit() {
		return apLimit;
	}

	public void setApLimit(BigDecimal apLimit) {
		this.apLimit = apLimit;
	}


	/**
	 * @return the optimisticLockVersion
	 */
	@Version
	@Column(name = "optimistic_lock_version", nullable = false, length = 11)
	public Long getOptimisticLockVersion() {
		return optimisticLockVersion;
	}

	/**
	 * @param optimisticLockVersion the optimisticLockVersion to set
	 */
	public void setOptimisticLockVersion(Long optimisticLockVersion) {
		this.optimisticLockVersion = optimisticLockVersion;
	}

	@Override
	public String toString() {
		return "AccountRegister [apLimit=" + apLimit + ", optimisticLockVersion=" + optimisticLockVersion + ", getId()=" + getId() + "]";
	}

	@Transient
	public BigDecimal getAvailableCash() {
		if (accountRegisterSummaryFields != null) {
			return accountRegisterSummaryFields.getAvailableCash();
		}
		return BigDecimal.ZERO;
	}

}
