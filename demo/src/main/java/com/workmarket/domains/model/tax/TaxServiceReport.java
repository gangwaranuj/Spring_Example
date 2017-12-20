package com.workmarket.domains.model.tax;

import com.workmarket.configuration.Constants;
import com.workmarket.domains.model.audit.AuditChanges;
import com.workmarket.utility.StringUtilities;
import org.apache.commons.lang3.BooleanUtils;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;
import java.math.BigDecimal;

/**
 * User: iloveopt
 * Date: 12/20/14
 */

@Entity(name = "taxServiceDetailReport")
@Table(name = "tax_service_detail_report")
@AuditChanges
public class TaxServiceReport extends AbstractTaxReport {

	private static final long serialVersionUID = 1L;

	private TaxServiceReportSet taxServiceReportSet;
	private BigDecimal earnings = BigDecimal.ZERO;
	private BigDecimal workPayments = BigDecimal.ZERO;
	private BigDecimal paymentReversals = BigDecimal.ZERO;
	private BigDecimal marketingPayments = BigDecimal.ZERO;
	private BigDecimal expenses = BigDecimal.ZERO;
	private Long buyerCompanyId;
	private Boolean useWMTaxEntity = false;


	@Fetch(FetchMode.JOIN)
	@ManyToOne(cascade = CascadeType.ALL)
	@JoinColumn(name = "tax_service_detail_report_set_id", referencedColumnName = "id")
	public TaxServiceReportSet getTaxServiceReportSet() {
		return taxServiceReportSet;
	}

	public void setTaxServiceReportSet(TaxServiceReportSet taxServiceReportSet) {
		this.taxServiceReportSet = taxServiceReportSet;
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

	@Column(name = "use_wm_tax_entity")
	public Boolean getUseWMTaxEntity() {
		return useWMTaxEntity;
	}

	public void setUseWMTaxEntity(Boolean useWMTaxEntity) {
		this.useWMTaxEntity = useWMTaxEntity;
	}

	@Transient
	public boolean isUseWMTaxEntity() {
		return BooleanUtils.isTrue(useWMTaxEntity);
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
	public String getTaxFormPDFName() {
		if (getTaxServiceReportSet() != null) {
			return String.format("%s%d%s", Constants.TAX_FORM_1099_PDF_FILENAME_PREFIX, getTaxServiceReportSet().getTaxYear(), Constants.PDF_EXTENSION);
		}
		return null;
	}

}
