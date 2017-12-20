package com.workmarket.domains.model.reporting.subscriptions;

import com.google.common.collect.Lists;
import com.workmarket.domains.model.account.pricing.subscription.SubscriptionPeriod;
import com.workmarket.configuration.Constants;
import com.workmarket.utility.DateUtilities;
import com.workmarket.utility.NumberUtilities;
import com.workmarket.utility.StringUtilities;
import org.apache.commons.lang.StringUtils;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.Calendar;
import java.util.List;

public class SubscriptionReportRow {

	private long companyId;
	private long subscriptionConfigurationId;

	private String companyName;
	private String companyAccountManager;
	private String paymentPeriod;

	private int termsInMonths;
	private int numberOfRenewals;

	private boolean vendorOfRecord;

	private Calendar effectiveDate;
	private Calendar signedDate;
	private Calendar renewalDate;

	/**
	 * Total dollar amount of all assignments that are routed on terms
	 */
	private BigDecimal termsUsed;
	private BigDecimal currentAnnualThroughput;

	private BigDecimal currentTierLowerBoundThroughput;
	private BigDecimal currentTierUpperBoundThroughput;

	private BigDecimal currentTierVORAmount;
	private BigDecimal currentTierPaymentAmount;
	private BigDecimal nextTierVORAmount;
	private BigDecimal nextTierPaymentAmount;
	private BigDecimal onTimePaymentPercentage;

	//For usage report only
	private BigDecimal invoicesComingDue;
	private BigDecimal invoicePastDue;

	public SubscriptionReportRow() {
	}

	public long getCompanyId() {
		return companyId;
	}

	public void setCompanyId(long companyId) {
		this.companyId = companyId;
	}

	public long getSubscriptionConfigurationId() {
		return subscriptionConfigurationId;
	}

	public void setSubscriptionConfigurationId(long subscriptionConfigurationId) {
		this.subscriptionConfigurationId = subscriptionConfigurationId;
	}

	public String getCompanyName() {
		return companyName;
	}

	public void setCompanyName(String companyName) {
		this.companyName = companyName;
	}

	public String getCompanyAccountManager() {
		return companyAccountManager;
	}

	public void setCompanyAccountManager(String companyAccountManager) {
		this.companyAccountManager = companyAccountManager;
	}

	public String getPaymentPeriod() {
		return paymentPeriod;
	}

	public void setPaymentPeriod(String paymentPeriod) {
		this.paymentPeriod = paymentPeriod;
	}

	public int getTermsInMonths() {
		return termsInMonths;
	}

	public void setTermsInMonths(int termsInMonths) {
		this.termsInMonths = termsInMonths;
	}

	public int getNumberOfRenewals() {
		return numberOfRenewals;
	}

	public void setNumberOfRenewals(int numberOfRenewals) {
		this.numberOfRenewals = numberOfRenewals;
	}

	public boolean isVendorOfRecord() {
		return vendorOfRecord;
	}

	public void setVendorOfRecord(boolean vendorOfRecord) {
		this.vendorOfRecord = vendorOfRecord;
	}

	public Calendar getRenewalDate() {
		return renewalDate;
	}

	public void setRenewalDate(Calendar renewalDate) {
		this.renewalDate = renewalDate;
	}

	public BigDecimal getTermsUsed() {
		return termsUsed;
	}

	public void setTermsUsed(BigDecimal termsUsed) {
		this.termsUsed = termsUsed;
	}

	public BigDecimal getCurrentAnnualThroughput() {
		return currentAnnualThroughput;
	}

	public void setCurrentAnnualThroughput(BigDecimal currentAnnualThroughput) {
		this.currentAnnualThroughput = currentAnnualThroughput;
	}

	public BigDecimal getMonthlyRecurringRevenue() {
		BigDecimal mrr = BigDecimal.ZERO;
		if (currentTierPaymentAmount != null) {
			SubscriptionPeriod period = SubscriptionPeriod.valueOf(getPaymentPeriod());
			if (period != null) {
				mrr = mrr.add(currentTierPaymentAmount).add((currentTierVORAmount != null && vendorOfRecord) ? currentTierVORAmount : BigDecimal.ZERO);
				mrr = mrr.divide(BigDecimal.valueOf(period.getMonths()), MathContext.DECIMAL32);
			}
		}
		return mrr;
	}

	public BigDecimal getAnnualRecurringRevenue() {
		BigDecimal mrr = getMonthlyRecurringRevenue();
		BigDecimal multiplier = BigDecimal.valueOf(12);
		if (termsInMonths <= 12) {
			multiplier = BigDecimal.valueOf(termsInMonths);
		}
		return mrr.multiply(multiplier);
	}

	public BigDecimal getCurrentTierLowerBoundThroughput() {
		return currentTierLowerBoundThroughput;
	}

	public void setCurrentTierLowerBoundThroughput(BigDecimal currentTierLowerBoundThroughput) {
		this.currentTierLowerBoundThroughput = currentTierLowerBoundThroughput;
	}

	public BigDecimal getCurrentTierUpperBoundThroughput() {
		return currentTierUpperBoundThroughput;
	}

	public void setCurrentTierUpperBoundThroughput(BigDecimal currentTierUpperBoundThroughput) {
		this.currentTierUpperBoundThroughput = currentTierUpperBoundThroughput;
	}

	public BigDecimal getCurrentTierVORAmount() {
		return currentTierVORAmount;
	}

	public void setCurrentTierVORAmount(BigDecimal currentTierVORAmount) {
		this.currentTierVORAmount = currentTierVORAmount;
	}

	public BigDecimal getCurrentTierPaymentAmount() {
		return currentTierPaymentAmount;
	}

	public void setCurrentTierPaymentAmount(BigDecimal currentTierPaymentAmount) {
		this.currentTierPaymentAmount = currentTierPaymentAmount;
	}

	public BigDecimal getNextTierVORAmount() {
		return nextTierVORAmount;
	}

	public void setNextTierVORAmount(BigDecimal nextTierVORAmount) {
		this.nextTierVORAmount = nextTierVORAmount;
	}

	public BigDecimal getNextTierPaymentAmount() {
		return nextTierPaymentAmount;
	}

	public void setNextTierPaymentAmount(BigDecimal nextTierPaymentAmount) {
		this.nextTierPaymentAmount = nextTierPaymentAmount;
	}

	public BigDecimal getTierThroughputUsage() {
		if (effectiveDate != null && effectiveDate.after(Calendar.getInstance())) {
			return BigDecimal.ZERO;
		}
		if (currentTierUpperBoundThroughput == null || currentAnnualThroughput == null) {
			return BigDecimal.ZERO;
		}
		return NumberUtilities.rate(currentAnnualThroughput, BigDecimal.valueOf(100), currentTierUpperBoundThroughput).setScale(2, RoundingMode.HALF_UP);
	}

	public Calendar getEffectiveDate() {
		return effectiveDate;
	}

	public void setEffectiveDate(Calendar effectiveDate) {
		this.effectiveDate = effectiveDate;
	}

	public Calendar getSignedDate() {
		return signedDate;
	}

	public void setSignedDate(Calendar signedDate) {
		this.signedDate = signedDate;
	}

	public BigDecimal getInvoicePastDue() {
		return invoicePastDue;
	}

	public void setInvoicePastDue(BigDecimal invoicePastDue) {
		this.invoicePastDue = invoicePastDue;
	}

	public BigDecimal getInvoicesComingDue() {
		return invoicesComingDue;
	}

	public void setInvoicesComingDue(BigDecimal invoicesComingDue) {
		this.invoicesComingDue = invoicesComingDue;
	}

	public BigDecimal getOnTimePaymentPercentage() {
		return onTimePaymentPercentage;
	}

	public void setOnTimePaymentPercentage(BigDecimal onTimePaymentPercentage) {
		this.onTimePaymentPercentage = onTimePaymentPercentage;
	}

	public int getDurationInMonths() {
		return Math.abs(DateUtilities.getMonthsBetweenNow(getEffectiveDate()));
	}

	public String getFormattedRenewalDate() {
		if (renewalDate == null) {
			return StringUtils.EMPTY;
		}
		return DateUtilities.format("MM/dd/yyyy", renewalDate, Constants.WM_TIME_ZONE);
	}

	public String getFormattedEffectiveDate() {
		if (effectiveDate == null) {
			return StringUtils.EMPTY;
		}
		return DateUtilities.format("MM/dd/yyyy", effectiveDate);
	}

	public String getFormattedSignedDate() {
		if (signedDate == null) {
			return StringUtils.EMPTY;
		}
		return DateUtilities.format("MM/dd/yyyy", signedDate, Constants.WM_TIME_ZONE);
	}

	public List<String> toStandardReportStringList() {
		List<String> row = Lists.newArrayList(
				//Company Name
				getCompanyName(),
				//Effective date
				getFormattedEffectiveDate(),
				//Signed date
			    getFormattedSignedDate(),
				//Account Manager
				getCompanyAccountManager(),
				//Terms Used
				String.valueOf(getTermsUsed()),
				//Subscription Term (mo)
				String.valueOf(getTermsInMonths()),
				//VOR
				StringUtilities.toYesNo(isVendorOfRecord()),
				//Auto Renewal
				String.valueOf(getNumberOfRenewals()),
				//Renewal Date
				getFormattedRenewalDate(),
				//Duration (mo)
				String.valueOf(getDurationInMonths()),
				//Current Tier Range
				String.valueOf(getCurrentTierUpperBoundThroughput()),
				//Payment Period
				getPaymentPeriod(),
				//MRR
				String.valueOf(getMonthlyRecurringRevenue()),
				//ARR
				String.valueOf(getAnnualRecurringRevenue())
		);
		return row;
	}

	public List<String> toUsageReportStringList() {
		List<String> row = Lists.newArrayList(
				//Company Name
				getCompanyName(),
				//Effective date
				getFormattedEffectiveDate(),
				//Existing Payable - Current
				String.valueOf(getInvoicesComingDue()),
				//Existing Payable - Past Due
				String.valueOf(getInvoicePastDue()),
				//% On time Payment
				String.valueOf(NumberUtilities.defaultValue(getOnTimePaymentPercentage())),
				//Current Annual Throughput
				String.valueOf(getCurrentAnnualThroughput()),
				//Tier Throughput Usage (%)
				String.valueOf(getTierThroughputUsage()),
				//Current Payment Amount
				String.valueOf(getCurrentTierPaymentAmount()),
				//Current VOR Amount
				String.valueOf(getCurrentTierVORAmount()),
				//Next Tier's Payment Amount
				String.valueOf(getNextTierPaymentAmount() != null ? getNextTierPaymentAmount() : StringUtils.EMPTY),
				//Next Tier's VOR Amount
				String.valueOf(getNextTierVORAmount() != null ? getNextTierVORAmount() : StringUtils.EMPTY)
		);
		return row;
	}
}
