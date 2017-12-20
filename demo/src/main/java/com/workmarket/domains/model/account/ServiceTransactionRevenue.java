package com.workmarket.domains.model.account;

import com.workmarket.domains.model.AbstractEntity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import java.math.BigDecimal;
import java.util.Calendar;

@Entity(name = "serviceTransactionRevenue")
@Table(name = "service_transaction_revenue")
public class ServiceTransactionRevenue extends AbstractEntity {

	private static final long serialVersionUID = 3292910262466680246L;
	private ServiceTransaction serviceTransaction;
	private Calendar revenueEffectiveDate;
	private BigDecimal revenueAmount;
	private boolean deferredRevenue = true;

	public ServiceTransactionRevenue() {
	}

	public ServiceTransactionRevenue(ServiceTransaction serviceTransaction) {
		this.serviceTransaction = serviceTransaction;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "service_transaction_id", referencedColumnName = "id", updatable = false)
	public ServiceTransaction getServiceTransaction() {
		return serviceTransaction;
	}

	public void setServiceTransaction(ServiceTransaction serviceTransaction) {
		this.serviceTransaction = serviceTransaction;
	}

	@Column(name = "revenue_effective_date", nullable = false)
	public Calendar getRevenueEffectiveDate() {
		return revenueEffectiveDate;
	}

	public void setRevenueEffectiveDate(Calendar revenueEffectiveDate) {
		this.revenueEffectiveDate = revenueEffectiveDate;
	}

	@Column(name = "revenue_amount", nullable = false)
	public BigDecimal getRevenueAmount() {
		return revenueAmount;
	}

	public void setRevenueAmount(BigDecimal revenueAmount) {
		this.revenueAmount = revenueAmount;
	}

	@Column(name = "deferred_revenue", nullable = false)
	public boolean isDeferredRevenue() {
		return deferredRevenue;
	}

	public void setDeferredRevenue(boolean deferredRevenue) {
		this.deferredRevenue = deferredRevenue;
	}
}