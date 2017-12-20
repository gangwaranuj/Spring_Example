package com.workmarket.domains.model;

import com.google.common.collect.Sets;
import com.workmarket.data.annotation.TrackChanges;
import com.workmarket.domains.model.account.AccountRegister;
import com.workmarket.domains.model.account.AccountRegisterSummaryFields;
import com.workmarket.domains.model.account.payment.PaymentConfiguration;
import com.workmarket.domains.model.account.payment.PaymentTermsDurationCompanyAssociation;
import com.workmarket.domains.model.account.pricing.AccountPricingType;
import com.workmarket.domains.model.asset.Asset;
import com.workmarket.domains.model.audit.AuditChanges;
import com.workmarket.domains.model.audit.AuditedEntity;
import com.workmarket.domains.model.banking.AbstractBankAccount;
import com.workmarket.domains.model.company.CompanyStatusType;
import com.workmarket.domains.model.contract.Contract;
import com.workmarket.domains.model.directory.Email;
import com.workmarket.domains.model.tax.AbstractTaxEntity;
import com.workmarket.configuration.Constants;
import com.workmarket.utility.CompanyUtilities;
import com.workmarket.utility.DateUtilities;
import com.workmarket.utility.EncryptionUtilities;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.hibernate.annotations.*;

import javax.persistence.*;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.Table;

import java.math.BigDecimal;
import java.util.*;

@Entity(name = "company")
@org.hibernate.annotations.Entity(dynamicInsert = true, dynamicUpdate = true)
@Table(name = "company")
@AuditChanges
public class Company extends AuditedEntity {

	private static final long serialVersionUID = 1L;
	public static final int
		UNLOCK_HOURS_NO_LIMIT = 876581, // Arbitrarily set to 100 years into the future
		COMPANY_NAME_MAX_LENGTH = 255,
		COMPANY_OVERVIEW_MAX_LENGTH = 1000,
		COMPANY_WEBSITE_MAX_LENGTH = 255;

	private String uuid;
	private String name;
	private String website;
	private String overview;
	private Integer employees;
	private Integer yearFounded;
	private Integer employedProfessionals;
	private Address address;
	private List<? extends AbstractTaxEntity> taxEntities = new ArrayList<>();
	private List<? extends AbstractBankAccount> bankAccounts = new ArrayList<>();
	private Set<Contract> contracts = Sets.newLinkedHashSet();
	private Set<AccountRegister> accountRegisters = Sets.newLinkedHashSet();
	private Set<User> users = Sets.newLinkedHashSet();

	private CompanyStatusType companyStatusType = new CompanyStatusType(CompanyStatusType.ACTIVE);
	private String nameOldValue;
	private String overviewOldValue;
	private String websiteOldValue;
	private Integer lowBalancePercentage = Constants.LOW_BALANCE_PERCENTAGE;
	private BigDecimal lowBalanceAmount;
	private Boolean customLowBalanceFlag = Boolean.FALSE;
	private ManageMyWorkMarket manageMyWorkMarket = new ManageMyWorkMarket();
	private Boolean operatingAsIndividualFlag = Boolean.FALSE;
	private boolean vipFlag = false;
	private Calendar vipSetOn;
	private Long vipSetBy;

	// a.k.a First Employee
	private User createdBy;
	private String effectiveName;

	private Calendar lockedOn;
	private Calendar unlockedOn;
	private User unlockedBy;
	@Deprecated	private Integer lockPeriodGraceHrs = 0;

	private String companyNumber;
	private Integer lastInvoiceId = 0;
	private String invoiceSentToEmail;
	private Calendar lockAccountWarningSentOn;
	private Calendar overdueAccountWarningSentOn;

	private String customSignatureLine;
	private Integer lastInvoiceSummaryId = 0;
	private Integer lastStatementId = 0;
	private Calendar firstPaidAssignmentOn;
	private Calendar firstCreatedAssignmentOn;
	private Integer optimisticLockVersion;
	private Set<Email> agingAlertEmails = Sets.newLinkedHashSet();
	private PaymentConfiguration paymentConfiguration;
	private Set<Email> subscriptionInvoiceEmails = Sets.newLinkedHashSet();
	private boolean authorizeByInetAddress = Boolean.FALSE;
	private String customerType = "unknown";
	private CompanyPreference companyPreference;
	private CompanyEmployeeCountRangeEnum companyEmployeeCountRangeEnum;

	public static final String MANAGED_CUSTOMER_TYPE = "managed";
	public static final String BUYER_CUSTOMER_TYPE = "buyer";
	public static final String RESOURCE_CUSTOMER_TYPE = "resource";
	public static final String UKNOWN_CUSTOMER_TYPE = "uknown";
	public static final String TEST_CUSTOMER_TYPE = "test";
	private Set<PaymentTermsDurationCompanyAssociation> paymentTermsDurationsCompanyAssociations;
	private boolean hidePricing;
	private boolean inVendorSearch;

	public Company() {
		AccountRegister accountRegister;
		accountRegister = new AccountRegister();
		AccountRegisterSummaryFields accountRegisterSummaryFields = new AccountRegisterSummaryFields();
		accountRegister.setAccountRegisterSummaryFields(accountRegisterSummaryFields);
		accountRegister.setCompany(this);
		accountRegister.setCredit(new BigDecimal(0));
		this.accountRegisters = new HashSet<>();
		this.accountRegisters.add(accountRegister);
		this.companyPreference = new CompanyPreference(this);
	}

	@Column(name = "name", nullable = false, length = COMPANY_NAME_MAX_LENGTH)
	public String getName() {
		return name;
	}

	@Column(name = "name_old_value", nullable = true, length = COMPANY_NAME_MAX_LENGTH)
	public String getNameOldValue() {
		return nameOldValue;
	}

	@Column(name = "website", nullable = true, length = COMPANY_WEBSITE_MAX_LENGTH)
	public String getWebsite() {
		return website;
	}

	@Column(name = "overview", nullable = true, length = COMPANY_OVERVIEW_MAX_LENGTH)
	public String getOverview() {
		return overview;
	}

	@Column(name = "overview_old_value", nullable = true, length = COMPANY_OVERVIEW_MAX_LENGTH)
	public String getOverviewOldValue() {
		return overviewOldValue;
	}

	@Column(name = "emloyees", nullable = true, length = 11)
	public Integer getEmployees() {
		return employees;
	}

	@Enumerated(EnumType.STRING)
	@Column(name = "employee_count_range", nullable = true)
	public CompanyEmployeeCountRangeEnum getCompanyEmployeeCountRangeEnum() { return companyEmployeeCountRangeEnum; }

	public void setCompanyEmployeeCountRangeEnum(CompanyEmployeeCountRangeEnum companyEmployeeCountRangeEnum) { this.companyEmployeeCountRangeEnum = companyEmployeeCountRangeEnum; }

	@Column(name = "year_founded", nullable = true, length = 11)
	public Integer getYearFounded() {
		return yearFounded;
	}

	@Column(name = "employed_professionals", nullable = true, length = 11)
	public Integer getEmployedProfessionals() {
		return employedProfessionals;
	}

	@Fetch(FetchMode.JOIN)
	@ManyToOne(cascade = CascadeType.ALL)
	@JoinColumn(name = "address_id", referencedColumnName = "id", nullable = true)
	public Address getAddress() {
		return address;
	}

	@OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, targetEntity = AbstractTaxEntity.class)
	@JoinColumn(name = "company_id")
	public List<? extends AbstractTaxEntity> getTaxEntities() {
		return taxEntities;
	}

	@OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, targetEntity = AbstractBankAccount.class)
	@JoinColumn(name = "company_id")
	public List<? extends AbstractBankAccount> getBankAccounts() {
		return bankAccounts;
	}

	@OneToMany(fetch = FetchType.LAZY, cascade = {})
	@JoinColumn(name = "company_id", referencedColumnName = "id")
	public Set<Contract> getContracts() {
		return contracts;
	}

	@OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	@JoinColumn(name = "company_id", referencedColumnName = "id", nullable = false)
	public Set<AccountRegister> getAccountRegisters() {
		return accountRegisters;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setNameOldValue(String nameOldValue) {
		this.nameOldValue = nameOldValue;
	}

	@TrackChanges
	public void setOverview(String overview) {
		this.overview = overview;
	}

	public void setOverviewOldValue(String overviewOldValue) {
		this.overviewOldValue = overviewOldValue;
	}

	public void setWebsite(String website) {
		this.website = website;
	}

	public void setEmployees(Integer employees) {
		this.employees = employees;
	}

	public void setYearFounded(Integer yearFounded) {
		this.yearFounded = yearFounded;
	}

	public void setEmployedProfessionals(Integer employedProfessionals) {
		this.employedProfessionals = employedProfessionals;
	}

	public void setAddress(Address address) {
		this.address = address;
	}

	public void setTaxEntities(List<? extends AbstractTaxEntity> taxEntities) {
		this.taxEntities = taxEntities;
	}

	public void setBankAccounts(List<? extends AbstractBankAccount> bankAccounts) {
		this.bankAccounts = bankAccounts;
	}

	public void setContracts(Set<Contract> contracts) {
		this.contracts = contracts;
	}

	@OneToMany(mappedBy = "company", fetch = FetchType.LAZY, cascade = {})
	public Set<User> getUsers() {
		return users;
	}

	public void setUsers(Set<User> users) {
		this.users = users;
	}


	@OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	@JoinColumn(name = "company_id", referencedColumnName = "id")
	public Set<PaymentTermsDurationCompanyAssociation> getPaymentTermsDurationCompanyAssociations() {
		return paymentTermsDurationsCompanyAssociations;
	}

	public void setPaymentTermsDurationCompanyAssociations(Set<PaymentTermsDurationCompanyAssociation> paymentTermsDurationsCompanyAssociations) {
		this.paymentTermsDurationsCompanyAssociations = paymentTermsDurationsCompanyAssociations;
	}

	@Column(name = "operating_as_individual_flag", nullable = false)
	public Boolean getOperatingAsIndividualFlag() {
		return operatingAsIndividualFlag;
	}

	// ----- Transient
	@Transient
	public boolean isActive() {
		return getCompanyStatusType().getCode().equals(CompanyStatusType.ACTIVE);
	}

	@Transient
	public boolean isSuspended() {
		return getCompanyStatusType().getCode().equals(CompanyStatusType.SUSPENDED);
	}

	@Transient
	public boolean isLocked() {
		return getCompanyStatusType().getCode().equals(CompanyStatusType.LOCKED);
	}

	@Transient
	public String getIdHash() {
		return EncryptionUtilities.getMD5Digest(getId());
	}

	@Transient
	public boolean isNameUpdated() {
		return (!this.name.equals((this.nameOldValue != null ? this.nameOldValue : this.name)));
	}

	@Transient
	public boolean isOverviewUpdated() {
		return (!this.overview.equals((this.overviewOldValue != null ? this.overviewOldValue : this.overview)));
	}

	public void setAccountRegisters(Set<AccountRegister> accountRegisters) {
		this.accountRegisters = accountRegisters;
	}

	@Override
	public String toString() {
		return new ToStringBuilder(this, ToStringStyle.SIMPLE_STYLE).append("id", getId()).append("name", getName()).toString();
	}

	@Column(name = "low_balance_percentage", nullable = false)
	public Integer getLowBalancePercentage() {
		return lowBalancePercentage;
	}

	public void setLowBalancePercentage(Integer lowBalancePercentage) {
		this.lowBalancePercentage = lowBalancePercentage;
	}

	@Column(name = "low_balance_amount")
	public BigDecimal getLowBalanceAmount() {
		return lowBalanceAmount;
	}

	public void setLowBalanceAmount(BigDecimal lowBalanceAmount) {
		this.lowBalanceAmount = lowBalanceAmount;
	}

	@Column(name = "custom_low_balance_flag", nullable = false)
	public Boolean getCustomLowBalanceFlag() {
		return customLowBalanceFlag;
	}

	public void setCustomLowBalanceFlag(Boolean customLowBalanceFlag) {
		this.customLowBalanceFlag = customLowBalanceFlag;
	}

	public void setVipFlag(boolean vipFlag) {
		this.vipFlag = vipFlag;
	}

	@Column(name = "vip_flag", nullable = false)
	public boolean isVipFlag() {
		return vipFlag;
	}

	@Column(name = "hide_pricing")
	public boolean isHidePricing() {
		return hidePricing;
	}

	public void setHidePricing(boolean hidePricing) {
		this.hidePricing = hidePricing;
	}

	@Column(name = "in_vendor_search")
	public boolean isInVendorSearch() {
		return inVendorSearch;
	}

	@TrackChanges
	public void setInVendorSearch(boolean inVendorSearch) {
		this.inVendorSearch = inVendorSearch;
	}

	@Embedded
	public ManageMyWorkMarket getManageMyWorkMarket() {
		return manageMyWorkMarket;
	}

	public void setManageMyWorkMarket(ManageMyWorkMarket manageMyWorkMarket) {
		this.manageMyWorkMarket = manageMyWorkMarket;
	}

	@Transient
	public boolean isPaymentTermsEnabled() {
		return manageMyWorkMarket.getPaymentTermsEnabled();
	}

	@Transient
	public Integer getPaymentTermsDays() {
		return manageMyWorkMarket.getPaymentTermsDays();
	}

	public void setOperatingAsIndividualFlag(Boolean operatingAsIndividualFlag) {
		this.operatingAsIndividualFlag = operatingAsIndividualFlag;
	}

	public void setWebsiteOldValue(String websiteOldValue) {
		this.websiteOldValue = websiteOldValue;
	}

	@Column(name = "website_old_value", length = 255)
	public String getWebsiteOldValue() {
		return websiteOldValue;
	}

	@ManyToOne(cascade = {})
	@JoinColumn(name = "created_by")
	public User getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(User createdBy) {
		this.createdBy = createdBy;
	}

	@Fetch(FetchMode.JOIN)
	@ManyToOne
	@JoinColumn(name = "company_status_type_code", referencedColumnName = "code")
	public CompanyStatusType getCompanyStatusType() {
		return companyStatusType;
	}

	@TrackChanges
	public void setCompanyStatusType(CompanyStatusType companyStatusType) {
		this.companyStatusType = companyStatusType;
	}

	@Column(name = "locked_on")
	public Calendar getLockedOn() {
		return lockedOn;
	}

	public void setLockedOn(Calendar lockedOn) {
		this.lockedOn = lockedOn;
	}

	@Column(name = "vip_set_on")
	public Calendar getVipSetOn() {
		return vipSetOn;
	}

	public void setVipSetOn(Calendar vipSetOn) {
		this.vipSetOn = vipSetOn;
	}

	@Column(name = "unlocked_on")
	public Calendar getUnlockedOn() {
		return unlockedOn;
	}

	public void setUnlockedOn(Calendar unlockedOn) {
		this.unlockedOn = unlockedOn;
	}

	@ManyToOne(cascade = {}, fetch = FetchType.LAZY)
	@JoinColumn(name = "unlocked_by")
	public User getUnlockedBy() {
		return unlockedBy;
	}

	public void setUnlockedBy(User unlockedBy) {
		this.unlockedBy = unlockedBy;
	}

	@Column(name = "vip_set_by")
	public Long getVipSetBy() {
		return vipSetBy;
	}

	public void setVipSetBy(Long vipSetBy) {
		this.vipSetBy = vipSetBy;
	}

	/** TODO(RemoveGraceHrs) */
	@Deprecated
	@Column(name = "lock_grace_hrs")
	public Integer getLockPeriodGraceHrs() {
		return lockPeriodGraceHrs;
	}

	/** TODO(RemoveGraceHrs)s */
	@Deprecated
	public void setLockPeriodGraceHrs(Integer lockPeriodGraceHrs) {
		this.lockPeriodGraceHrs = lockPeriodGraceHrs;
	}

	@Column(name = "effective_name", nullable = false)
	public String getEffectiveName() {
		return effectiveName;
	}

	public void setEffectiveName(String effectiveName) {
		this.effectiveName = effectiveName;
	}

	@Column(name = "company_number", unique = true, nullable = false, length = 10)
	public String getCompanyNumber() {
		return companyNumber;
	}

	public void setCompanyNumber(String companyNumber) {
		this.companyNumber = companyNumber;
	}

	@Column(name = "last_invoice_id", nullable = false)
	public Integer getLastInvoiceId() {
		return lastInvoiceId;
	}

	public void setLastInvoiceId(Integer lastInvoiceId) {
		this.lastInvoiceId = lastInvoiceId;
	}

	@Column(name = "invoice_sent_to_email", length = 255)
	public String getInvoiceSentToEmail() {
		return invoiceSentToEmail;
	}

	public void setInvoiceSentToEmail(String invoiceSentToEmail) {
		this.invoiceSentToEmail = invoiceSentToEmail;
	}

	@Column(name = "lock_account_warning_sent_on")
	public Calendar getLockAccountWarningSentOn() {
		return lockAccountWarningSentOn;
	}

	public void setLockAccountWarningSentOn(Calendar lockAccountWarningSentOn) {
		this.lockAccountWarningSentOn = lockAccountWarningSentOn;
	}

	/** TODO(RemoveGraceHrs) */
	@Transient
	@Deprecated
	public Calendar getNextLockDate() {
		Calendar nextLock = DateUtilities.getCalendarNow();
		if (getLockedOn() != null) {
			nextLock = (Calendar) getLockedOn().clone();
			nextLock.add(Calendar.HOUR_OF_DAY, getLockPeriodGraceHrs());
		}
		return nextLock;
	}

	@Transient
	public boolean hasUsers() {
		return !users.isEmpty();
	}

	@Transient
	public boolean hasLockWarning() {
		return CompanyUtilities.hasLockWarning(getLockAccountWarningSentOn());
	}

	public static boolean hasLockWarning(Calendar lockAccountWarningSentOn) {
		return lockAccountWarningSentOn != null &&
			DateUtilities.getHoursBetweenFromNow(lockAccountWarningSentOn) <= 24;
	}

	@Transient
	public boolean hasOverdueWarning() {
		return CompanyUtilities.hasOverdueWarning(getOverdueWarningDaysBetweenFromNow());
	}

	public static boolean hasOverdueWarning(int overdueWarningDaysBetweenFromNow) {
		return overdueWarningDaysBetweenFromNow >= 0;
	}

	@Transient
	public int getOverdueWarningDaysBetweenFromNow() {
		return CompanyUtilities.getOverdueWarningDaysBetweenFromNow(getOverdueAccountWarningSentOn());
	}

	@Transient
	public Asset getAvatarSmall() {
		return null;
	}

	@Column(name = "custom_signature_line")
	public String getCustomSignatureLine() {
		return customSignatureLine;
	}

	public void setCustomSignatureLine(String customSignatureLine) {
		this.customSignatureLine = customSignatureLine;
	}

	@Transient
	public boolean hasStatementsEnabled() {
		return getManageMyWorkMarket().getStatementsEnabled();
	}

	@Column(name = "last_invoice_summary_id", nullable = false)
	public Integer getLastInvoiceSummaryId() {
		return lastInvoiceSummaryId;
	}

	public void setLastInvoiceSummaryId(Integer lastInvoiceSummaryId) {
		this.lastInvoiceSummaryId = lastInvoiceSummaryId;
	}

	@Column(name = "last_statement_id", nullable = false)
	public Integer getLastStatementId() {
		return lastStatementId;
	}

	public void setLastStatementId(Integer lastStatementId) {
		this.lastStatementId = lastStatementId;
	}

	@Column(name = "first_paid_assignment_on")
	public Calendar getFirstPaidAssignmentOn() {
		return firstPaidAssignmentOn;
	}

	public void setFirstPaidAssignmentOn(Calendar firstPaidAssignmentOn) {
		this.firstPaidAssignmentOn = firstPaidAssignmentOn;
	}

	@Column(name = "first_created_assignment_on")
	public Calendar getFirstCreatedAssignmentOn() {
		return firstCreatedAssignmentOn;
	}

	public void setFirstCreatedAssignmentOn(Calendar firstCreatedAssignmentOn) {
		this.firstCreatedAssignmentOn = firstCreatedAssignmentOn;
	}

	@Version
	@Column(name = "optimistic_lock_version")
	public Integer getOptimisticLockVersion() {
		return optimisticLockVersion;
	}

	public void setOptimisticLockVersion(Integer optimisticLockVersion) {
		this.optimisticLockVersion = optimisticLockVersion;
	}

	@ManyToMany
	@JoinTable(name = "company_alert_email",
		joinColumns = @JoinColumn(name = "company_id"),
		inverseJoinColumns = @JoinColumn(name = "email_id"))
	public Set<Email> getAgingAlertEmails() {
		return agingAlertEmails;
	}

	public void setAgingAlertEmails(Set<Email> agingAlertEmails) {
		this.agingAlertEmails = agingAlertEmails;
	}

	@Column(name = "authorize_by_ip")
	public boolean isAuthorizeByInetAddress() {
		return authorizeByInetAddress;
	}

	public void setAuthorizeByInetAddress(boolean authorizeByInetAddress) {
		this.authorizeByInetAddress = authorizeByInetAddress;
	}

	@Fetch(FetchMode.JOIN)
	@OneToOne(optional = false)
	@JoinColumn(name = "payment_configuration_id", referencedColumnName = "id", updatable = false)
	public PaymentConfiguration getPaymentConfiguration() {
		return paymentConfiguration;
	}

	public void setPaymentConfiguration(PaymentConfiguration paymentConfiguration) {
		this.paymentConfiguration = paymentConfiguration;
	}

	@Transient
	public boolean hasBankAccounts() {
		return (bankAccounts != null && !bankAccounts.isEmpty());

	}

	@Column(name = "overdue_account_warning_sent_on" )
	public Calendar getOverdueAccountWarningSentOn() {
		return overdueAccountWarningSentOn;
	}

	public void setOverdueAccountWarningSentOn(Calendar overdueAccountWarningSentOn) {
		this.overdueAccountWarningSentOn = overdueAccountWarningSentOn;
	}

	@Column(name="customer_type")
	public String getCustomerType() {
		return customerType;
	}

	@TrackChanges
	public void setCustomerType(String customerType) {
		this.customerType = customerType;
	}

	@ManyToMany
	@JoinTable(name = "company_subscription_invoice_email",
		joinColumns = @JoinColumn(name = "company_id"),
		inverseJoinColumns = @JoinColumn(name = "email_id"))
	public Set<Email> getSubscriptionInvoiceEmails() {
		return subscriptionInvoiceEmails;
	}

	public void setSubscriptionInvoiceEmails(Set<Email> subscriptionInvoiceEmails) {
		this.subscriptionInvoiceEmails = subscriptionInvoiceEmails;
	}

	@Transient
	public boolean hasDefaultAddressForSubscriptionInvoices() {
		return CollectionUtils.isNotEmpty(subscriptionInvoiceEmails);
	}

	@Transient
	public AccountPricingType getAccountPricingType() {
		return paymentConfiguration.getAccountPricingType();
	}

	@Transient
	public boolean isVendorOfRecord(){
		return paymentConfiguration.isVendorOfRecord();
	}

	@OneToOne(mappedBy = "company")
	@Cascade({ org.hibernate.annotations.CascadeType.SAVE_UPDATE, org.hibernate.annotations.CascadeType.DELETE })
	public CompanyPreference getCompanyPreference() {
		return this.companyPreference;
	}

	public void setCompanyPreference(CompanyPreference companyPreference) {
		this.companyPreference = companyPreference;
	}

	/**
	 * Lock this company.
	 *
	 * @return True if the company was actively locked; false if no action was taken or company is already locked
	 */
	public boolean lock() {
		if (!isLocked() && isActive() && !isVipFlag()) { // VIP companies can not be locked
			setCompanyStatusType(new CompanyStatusType(CompanyStatusType.LOCKED));
			setLockedOn(DateUtilities.getCalendarNow());
			return true;
		}

		return false;
	}

	/**
	 * Unlock this company.
	 *
	 * @param unlockedPeriodGraceHours Period in hours company is to remain unlocked before they are locked again
	 * @return True if the company was actively unlocked; false if no action was taken or the company is already unlocked
	 */
	public boolean unlock(int unlockedPeriodGraceHours) {
		if (isLocked()) {
			setCompanyStatusType(new CompanyStatusType(CompanyStatusType.ACTIVE));
			setUnlockedOn(DateUtilities.getCalendarNow());
			setLockPeriodGraceHrs(unlockedPeriodGraceHours);
			return true;
		}

		return false;
	}

	/**
	 * Unlock this company.
	 *
	 * @return True if the company was actively unlocked; false if no action was taken or the company is already unlocked
	 */
	public boolean unlock() {
		return unlock(UNLOCK_HOURS_NO_LIMIT);
	}

	/**
	 * Mark this company as VIP.
	 *
	 * @return True if the user was actively marked VIP; false if user is already a VIP an no action was therefore taken
	 */
	public boolean markAsVip() {
		return markAsVip(true);
	}

	/**
	 * Toggle VIP marking.
	 *
	 * @param vip True to set this company as VIP; false otherwise
	 * @return True if the user has changed; false otherwise
	 */
	public boolean markAsVip(boolean vip) {
		if (vip && !isVipFlag()) {
			this.vipFlag = true;
			unlock(UNLOCK_HOURS_NO_LIMIT); // VIPs by default should always be unlocked
			return true;
		} else if (!vip && isVipFlag()) {
			this.vipFlag = false; // Just toggle off and don't do any locking of any kind
			return true;
		}

		return false;
	}

	@Transient
	public boolean isResourceAccount() {
		return RESOURCE_CUSTOMER_TYPE.equals(customerType);
	}

	@Transient
	public boolean isBuyerAccount() {
		return BUYER_CUSTOMER_TYPE.equals(customerType);
	}

	@Transient
	public boolean isManagedAccount() {
		return MANAGED_CUSTOMER_TYPE.equals(customerType);
	}

	@Column(name = "uuid", updatable = false)
	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}
}
