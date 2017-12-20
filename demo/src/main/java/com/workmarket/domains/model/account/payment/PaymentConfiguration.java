package com.workmarket.domains.model.account.payment;

import com.google.common.collect.Lists;
import com.workmarket.domains.model.account.pricing.AccountPricingType;
import com.workmarket.domains.model.account.pricing.AccountServiceType;
import com.workmarket.domains.model.account.pricing.AccountServiceTypeConfiguration;
import com.workmarket.domains.model.audit.AuditedEntity;
import com.workmarket.domains.model.postalcode.Country;
import com.workmarket.domains.model.audit.AuditChanges;

import com.workmarket.domains.model.tax.AbstractTaxEntity;
import com.workmarket.domains.model.tax.UsaTaxEntity;
import org.apache.commons.collections.CollectionUtils;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import javax.persistence.*;

import java.util.Calendar;
import java.util.List;

@Entity(name = "paymentConfiguration")
@Table(name = "payment_configuration")
@AuditChanges
public class PaymentConfiguration extends AuditedEntity {

	private static final long serialVersionUID = -5569890916961801774L;

	private Integer paymentCycleDays = 0;
	private Integer accountingProcessDays = 0;
	private Integer preferredDayOfWeek = Calendar.MONDAY;
	private Integer preferredDayOfMonth = 1;
	private boolean checkPaymentMethodEnabled = false;
	private boolean wireTransferPaymentMethodEnabled = true;
	private boolean achPaymentMethodEnabled = false;
	private boolean creditCardPaymentMethodEnabled = false;
	private boolean prefundPaymentMethodEnabled = false;
	private Calendar startDatePaymentCycle;
	private Calendar nextStatementDate;
	private Calendar lastStatementSentOn;
	private boolean biweeklyPaymentOnSpecificDayOfMonth = false;
	private Integer preferredDayOfMonthBiweeklyFirstPayment = 1;
	private Integer preferredDayOfMonthBiweeklySecondPayment = 15;
	private AccountPricingType accountPricingType = new AccountPricingType(AccountPricingType.TRANSACTIONAL_PRICING_TYPE);
	private List<AccountServiceTypeConfiguration> accountServiceTypeConfigurations = Lists.newArrayList();
	private Calendar accountPricingModifiedOn;
	private Calendar accountServiceTypeModifiedOn;
	private Integer paymentCalculatorType=0;

	public PaymentConfiguration() {
		for (String countryId : Country.WM_SUPPORTED_COUNTRIES) {
			AccountServiceTypeConfiguration accountServiceTypeConfiguration = new AccountServiceTypeConfiguration(new AccountServiceType(AccountServiceType.NONE), new Country(countryId));
			accountServiceTypeConfiguration.setPaymentConfiguration(this);
			this.accountServiceTypeConfigurations.add(accountServiceTypeConfiguration);
		}
	}

	@Column(name = "payment_cycle_days", nullable = false)
	public Integer getPaymentCycleDays() {
		return paymentCycleDays;
	}

	public void setPaymentCycleDays(Integer paymentCycleDays) {
		this.paymentCycleDays = paymentCycleDays;
	}

	@Column(name = "accounting_process_days", nullable = false)
	public Integer getAccountingProcessDays() {
		return accountingProcessDays;
	}

	public void setAccountingProcessDays(Integer accountingProcessDays) {
		this.accountingProcessDays = accountingProcessDays;
	}

	@Column(name = "preferred_day_of_week", nullable = false)
	public Integer getPreferredDayOfWeek() {
		return preferredDayOfWeek;
	}

	public void setPreferredDayOfWeek(Integer preferredDayOfWeek) {
		this.preferredDayOfWeek = preferredDayOfWeek;
	}

	@Column(name = "preferred_day_of_month", nullable = false)
	public Integer getPreferredDayOfMonth() {
		return preferredDayOfMonth;
	}

	public void setPreferredDayOfMonth(Integer preferredDayOfMonth) {
		this.preferredDayOfMonth = preferredDayOfMonth;
	}

	@Column(name = "check_payment_method_enabled", nullable = false)
	public boolean isCheckPaymentMethodEnabled() {
		return checkPaymentMethodEnabled;
	}

	public void setCheckPaymentMethodEnabled(boolean checkPaymentMethodEnabled) {
		this.checkPaymentMethodEnabled = checkPaymentMethodEnabled;
	}

	@Column(name = "wire_transfer_payment_method_enabled", nullable = false)
	public boolean isWireTransferPaymentMethodEnabled() {
		return wireTransferPaymentMethodEnabled;
	}

	public void setWireTransferPaymentMethodEnabled(boolean wireTransferPaymentMethodEnabled) {
		this.wireTransferPaymentMethodEnabled = wireTransferPaymentMethodEnabled;
	}

	@Column(name = "ach_payment_method_enabled", nullable = false)
	public boolean isAchPaymentMethodEnabled() {
		return achPaymentMethodEnabled;
	}

	public void setAchPaymentMethodEnabled(boolean achPaymentMethodEnabled) {
		this.achPaymentMethodEnabled = achPaymentMethodEnabled;
	}

	@Column(name = "credit_card_payment_method_enabled", nullable = false)
	public boolean isCreditCardPaymentMethodEnabled() {
		return creditCardPaymentMethodEnabled;
	}

	public void setCreditCardPaymentMethodEnabled(boolean creditCardPaymentMethodEnabled) {
		this.creditCardPaymentMethodEnabled = creditCardPaymentMethodEnabled;
	}

	@Column(name = "start_date_payment_cycle", nullable = true)
	public Calendar getStartDatePaymentCycle() {
		return startDatePaymentCycle;
	}

	public void setStartDatePaymentCycle(Calendar startDatePaymentCycle) {
		this.startDatePaymentCycle = startDatePaymentCycle;
	}

	@Column(name = "next_statement_date", nullable = true)
	public Calendar getNextStatementDate() {
		return nextStatementDate;
	}

	public void setNextStatementDate(Calendar nextStatementDate) {
		this.nextStatementDate = nextStatementDate;
	}

	@Column(name = "last_statement_sent_on", nullable = true)
	public Calendar getLastStatementSentOn() {
		return lastStatementSentOn;
	}

	public void setLastStatementSentOn(Calendar lastStatementSentOn) {
		this.lastStatementSentOn = lastStatementSentOn;
	}

	@Column(name = "biweekly_payment_day_of_month", nullable = false)
	public boolean isBiweeklyPaymentOnSpecificDayOfMonth() {
		return biweeklyPaymentOnSpecificDayOfMonth;
	}

	public void setBiweeklyPaymentOnSpecificDayOfMonth(boolean biweeklyPaymentOnSpecificDayOfMonth) {
		this.biweeklyPaymentOnSpecificDayOfMonth = biweeklyPaymentOnSpecificDayOfMonth;
	}

	@Column(name = "preferred_day_of_month_first_payment", nullable = false)
	public Integer getPreferredDayOfMonthBiweeklyFirstPayment() {
		return preferredDayOfMonthBiweeklyFirstPayment;
	}

	public void setPreferredDayOfMonthBiweeklyFirstPayment(Integer preferredDayOfMonthBiweeklyFirstPayment) {
		this.preferredDayOfMonthBiweeklyFirstPayment = preferredDayOfMonthBiweeklyFirstPayment;
	}

	@Column(name = "preferred_day_of_month_second_payment", nullable = false)
	public Integer getPreferredDayOfMonthBiweeklySecondPayment() {
		return preferredDayOfMonthBiweeklySecondPayment;
	}

	public void setPreferredDayOfMonthBiweeklySecondPayment(Integer preferredDayOfMonthBiweeklySecondPayment) {
		this.preferredDayOfMonthBiweeklySecondPayment = preferredDayOfMonthBiweeklySecondPayment;
	}

	@Fetch(FetchMode.JOIN)
	@ManyToOne
	@JoinColumn(name = "account_pricing_type_code", referencedColumnName = "code")
	public AccountPricingType getAccountPricingType() {
		return accountPricingType;
	}

	public void setAccountPricingType(AccountPricingType accountPricingType) {
		this.accountPricingType = accountPricingType;
	}

	@Fetch(FetchMode.JOIN)
	@OneToMany(cascade = CascadeType.ALL)
	@JoinColumn(name = "payment_configuration_id", referencedColumnName = "id", nullable = false)
	public List<AccountServiceTypeConfiguration> getAccountServiceTypeConfigurations() {
		return accountServiceTypeConfigurations;
	}

	public void setAccountServiceTypeConfigurations(List<AccountServiceTypeConfiguration> accountServiceTypeConfigurations) {
		this.accountServiceTypeConfigurations = accountServiceTypeConfigurations;
	}

	@Column(name = "account_pricing_type_modified_on")
	public Calendar getAccountPricingModifiedOn() {
		return accountPricingModifiedOn;
	}

	public void setAccountPricingModifiedOn(Calendar accountPricingModifiedOn) {
		this.accountPricingModifiedOn = accountPricingModifiedOn;
	}

	@Column(name = "account_service_type_modified_on")
	public Calendar getAccountServiceTypeModifiedOn() {
		return accountServiceTypeModifiedOn;
	}

	public void setAccountServiceTypeModifiedOn(Calendar accountServiceTypeModifiedOn) {
		this.accountServiceTypeModifiedOn = accountServiceTypeModifiedOn;
	}

	@Column(name = "prefund_payment_method_enabled")
	public boolean isPrefundPaymentMethodEnabled() {
		return prefundPaymentMethodEnabled;
	}

	public void setPrefundPaymentMethodEnabled(boolean prefundPaymentMethodEnabled) {
		this.prefundPaymentMethodEnabled = prefundPaymentMethodEnabled;
	}

	@Column(name = "payment_calculator_type")
	public Integer getPaymentCalculatorType() {
		return paymentCalculatorType;
	}

	public void setPaymentCalculatorType(Integer paymentCalculatorType) {
		this.paymentCalculatorType = paymentCalculatorType;
	}

	@Transient
	public PaymentCycle getPaymentCycle() {
		return PaymentCycle.getPaymentCycle(getPaymentCycleDays());
	}

	@Transient
	public AccountingProcessTime getAccountingProcessTime() {
		return AccountingProcessTime.getAccountingProcessTime(getAccountingProcessDays());
	}

	@Transient
	public BiweeklyPaymentDays getBiweeklyPaymentDays() {
		if (getPaymentCycle().equals(PaymentCycle.BIWEEKLY) && isBiweeklyPaymentOnSpecificDayOfMonth()) {
			return BiweeklyPaymentDays.getPaymentCycle(getPreferredDayOfMonthBiweeklyFirstPayment());
		}
		return null;
	}

	@Transient
	public boolean isSubscriptionPricing() {
		return accountPricingType.isSubscriptionPricing();
	}

	@Transient
	public boolean isTransactionalPricing() {
		return accountPricingType.isTransactionalPricing();
	}

	@Transient
	public boolean isVendorOfRecord() {
		if (CollectionUtils.isNotEmpty(accountServiceTypeConfigurations)) {
			for (String supportedCountryId : Country.WM_SUPPORTED_COUNTRIES) {
				if (this.findAccountServiceTypeForCountry(supportedCountryId).isVendorOfRecord()) {
					return true;
				}
			}
		}
		return false;
	}

	@Transient
	public AccountServiceType findAccountServiceTypeForCountry(String countryId) {
		AccountServiceTypeConfiguration accountServiceTypeConfiguration = findAccountServiceTypeConfigurationForCountry(countryId);
		if(accountServiceTypeConfiguration == null) {
			return new AccountServiceType(AccountServiceType.NONE);
		}
		else {
			return accountServiceTypeConfiguration.getAccountServiceType();
		}
	}

	@Transient
	public AccountServiceTypeConfiguration findAccountServiceTypeConfigurationForCountry(String countryId) {
		if(AbstractTaxEntity.COUNTRY_USA.equals(UsaTaxEntity.getCountryFromCountryId(countryId))) {
			countryId = Country.USA;
		}
		if (CollectionUtils.isNotEmpty(accountServiceTypeConfigurations)) {
			for (AccountServiceTypeConfiguration c : accountServiceTypeConfigurations) {
				if (c.getCountry().getId().equals(countryId)) {
					return c;
				}
			}
		}
		return null;
	}

	@Override
	public String toString() {
		return "PaymentConfiguration{" +
				"accountingProcessDays=" + accountingProcessDays +
				", paymentCycleDays=" + paymentCycleDays +
				", preferredDayOfWeek=" + preferredDayOfWeek +
				", preferredDayOfMonth=" + preferredDayOfMonth +
				", checkPaymentMethodEnabled=" + checkPaymentMethodEnabled +
				", wireTransferPaymentMethodEnabled=" + wireTransferPaymentMethodEnabled +
				", achPaymentMethodEnabled=" + achPaymentMethodEnabled +
				", creditCardPaymentMethodEnabled=" + creditCardPaymentMethodEnabled +
				", startDatePaymentCycle=" + startDatePaymentCycle +
				", nextStatementDate=" + nextStatementDate +
				", lastStatementSentOn=" + lastStatementSentOn +
				", biweeklyPaymentOnSpecificDayOfMonth=" + biweeklyPaymentOnSpecificDayOfMonth +
				", preferredDayOfMonthBiweeklyFirstPayment=" + preferredDayOfMonthBiweeklyFirstPayment +
				", preferredDayOfMonthBiweeklySecondPayment=" + preferredDayOfMonthBiweeklySecondPayment +
				", accountPricingType=" + accountPricingType.getCode() +
				'}';
	}
}
