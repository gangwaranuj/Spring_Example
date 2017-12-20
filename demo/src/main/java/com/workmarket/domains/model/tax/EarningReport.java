package com.workmarket.domains.model.tax;

import com.workmarket.domains.model.audit.AuditChanges;
import com.workmarket.utility.StringUtilities;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;
import java.math.BigDecimal;

@Entity(name = "earningReport")
@Table(name = "earning_report")
@AuditChanges
public class EarningReport extends AbstractTaxReport {

	private static final long serialVersionUID = 1L;

	private EarningReportSet earningReportSet;
	private BigDecimal earnings = BigDecimal.ZERO;
	private BigDecimal workPayments = BigDecimal.ZERO;
	private BigDecimal paymentReversals = BigDecimal.ZERO;
	private BigDecimal marketingPayments = BigDecimal.ZERO;
	private BigDecimal expenses = BigDecimal.ZERO;
	private BigDecimal vorEarnings = BigDecimal.ZERO;
	private BigDecimal nonVorEarnings = BigDecimal.ZERO;
	private BigDecimal vorExpenses = BigDecimal.ZERO;
	private BigDecimal nonVorExpenses = BigDecimal.ZERO;
	private String businessName;
	private boolean businessNameFlag = false;

	@ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	@JoinColumn(name = "earning_report_set_id", referencedColumnName = "id")
	public EarningReportSet getEarningReportSet() {
		return earningReportSet;
	}

	public void setEarningReportSet(EarningReportSet earningReportSet) {
		this.earningReportSet = earningReportSet;
	}

	@Column(name = "earnings", nullable = false)
	public BigDecimal getEarnings() {
		return earnings;
	}

	public void setEarnings(BigDecimal earnings) {
		this.earnings = earnings;
	}

	@Column(name = "marketing_payments_amount", nullable = false)
	public BigDecimal getMarketingPayments() {
		return marketingPayments;
	}

	public void setMarketingPayments(BigDecimal marketingPayments) {
		this.marketingPayments = marketingPayments;
	}

	@Column(name = "payment_reversal_amount", nullable = false)
	public BigDecimal getPaymentReversals() {
		return paymentReversals;
	}

	public void setPaymentReversals(BigDecimal paymentReversals) {
		this.paymentReversals = paymentReversals;
	}

	@Column(name = "work_earned_amount", nullable = false)
	public BigDecimal getWorkPayments() {
		return workPayments;
	}

	public void setWorkPayments(BigDecimal workPayments) {
		this.workPayments = workPayments;
	}

	@Column(name = "expenses", nullable = false)
	public BigDecimal getExpenses() {
		return expenses;
	}

	public void setExpenses(BigDecimal expenses) {
		this.expenses = expenses;
	}

	@Column(name = "non_vor_earnings", nullable = false)
	public BigDecimal getNonVorEarnings() {
		return nonVorEarnings;
	}

	public void setNonVorEarnings(BigDecimal nonVorEarnings) {
		this.nonVorEarnings = nonVorEarnings;
	}

	@Column(name = "vor_earnings", nullable = false)
	public BigDecimal getVorEarnings() {
		return vorEarnings;
	}

	public void setVorEarnings(BigDecimal vorEarnings) {
		this.vorEarnings = vorEarnings;
	}

	@Column(name = "non_vor_expenses", nullable = false)
	public BigDecimal getNonVorExpenses() {
		return nonVorExpenses;
	}

	public void setNonVorExpenses(BigDecimal nonVorExpenses) {
		this.nonVorExpenses = nonVorExpenses;
	}

	@Column(name = "vor_expenses", nullable = false)
	public BigDecimal getVorExpenses() {
		return vorExpenses;
	}

	public void setVorExpenses(BigDecimal vorExpenses) {
		this.vorExpenses = vorExpenses;
	}

	@Column(name = "business_name", nullable = false)
	public String getBusinessName() {
		return businessName;
	}

	public void setBusinessName(String businessName) {
		this.businessName = businessName;
	}

	@Column(name = "business_name_flag", nullable = false)
	public boolean isBusinessNameFlag() {
		return businessNameFlag;
	}

	public void setBusinessNameFlag(boolean businessNameFlag) {
		this.businessNameFlag = businessNameFlag;
	}

	@Transient
	public String getFormattedWorkPayments() {
		return StringUtilities.formatMoneyForAccounting(workPayments);
	}

	@Transient
	public String getFormattedPaymentReversals() {
		return StringUtilities.formatMoneyForAccounting(paymentReversals);
	}

	@Transient
	public String getFormattedMarketingPayments() {
		return StringUtilities.formatMoneyForAccounting(marketingPayments);
	}

	@Transient
	public String getFormattedExpenses() {
		return StringUtilities.formatMoneyForAccounting(expenses);
	}

	@Transient
	public String getFormattedNetEarnings() {
		BigDecimal netEarnings = workPayments.add(paymentReversals).add(marketingPayments).add(expenses);
		return StringUtilities.formatMoneyForAccounting(netEarnings);
	}

	@Transient
	public String getFormattedVorEarning() {
		return StringUtilities.formatMoneyForAccounting(vorEarnings);
	}

	@Transient
	public String getFormattedTotalVorEarningPaymentReversalAndMarketing() {
		return StringUtilities.formatMoneyForAccounting(vorEarnings.add(paymentReversals).add(marketingPayments));
	}

	@Transient
	public String getFormattedNonVorEarning() {
		return StringUtilities.formatMoneyForAccounting(nonVorEarnings);
	}

	@Transient
	public String getFormattedNonVorExpenses() {
		return StringUtilities.formatMoneyForAccounting(nonVorExpenses);
	}

	@Transient
	public String getFormattedVorExpenses() {
		return StringUtilities.formatMoneyForAccounting(vorExpenses);
	}

	@Transient
	public String getFormattedNonVorNetEarnings() {
		BigDecimal netEarnings = nonVorEarnings.add(nonVorExpenses);
		return StringUtilities.formatMoneyForAccounting(netEarnings);
	}

	@Transient
	public String getFormattedVorNetEarnings() {
		BigDecimal netEarnings = vorEarnings.add(paymentReversals).add(marketingPayments).add(vorExpenses);
		return StringUtilities.formatMoneyForAccounting(netEarnings);
	}
}
