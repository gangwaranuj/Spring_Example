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

/**
 * User: iloveopt
 * Date: 1/3/14
 */

@Entity(name = "earningDetailReport")
@Table(name = "earning_detail_report")
@AuditChanges
public class EarningDetailReport extends AbstractTaxReport {

	private static final long serialVersionUID = 1L;

	private EarningDetailReportSet earningDetailReportSet;
	private BigDecimal earnings = BigDecimal.ZERO;
	private BigDecimal workPayments = BigDecimal.ZERO;
	private BigDecimal paymentReversals = BigDecimal.ZERO;
	private BigDecimal marketingPayments = BigDecimal.ZERO;
	private BigDecimal expenses = BigDecimal.ZERO;
	private Long buyerCompanyId;


	@ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	@JoinColumn(name = "earning_detail_report_set_id", referencedColumnName = "id")
	public EarningDetailReportSet getEarningDetailReportSet() {
		return earningDetailReportSet;
	}

	public void setEarningDetailReportSet(EarningDetailReportSet earningDetailReportSet) {
		this.earningDetailReportSet = earningDetailReportSet;
	}

	@Column(name = "buyer_company_id", nullable = false)
	public Long getBuyerCompanyId() {
		return buyerCompanyId;
	}

	public void setBuyerCompanyId(Long buyerCompanyId) {
		this.buyerCompanyId = buyerCompanyId;
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

}
