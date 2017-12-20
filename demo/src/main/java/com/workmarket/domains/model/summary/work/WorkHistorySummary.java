package com.workmarket.domains.model.summary.work;

import com.workmarket.domains.model.summary.HistorySummaryEntity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.math.BigDecimal;

@Entity(name = "workHistorySummary")
@Table(name = "work_history_summary")
public class WorkHistorySummary extends HistorySummaryEntity {

	private static final long serialVersionUID = -8224608162655916627L;

	private Long workId;
	private Long companyId;
	private Long industryId;
	private BigDecimal workPrice;
	private BigDecimal buyerFee;
	private String workStatusTypeCode;
	private Boolean paymentTermsEnabled;
	private BigDecimal buyerTotalCost;
	private Long buyerUserId;
	private Long activeResourceUserId;
	private Long activeResourceCompanyId;
	private String accountPricingType;
	private String accountServiceType;

	@Column(name = "work_id", nullable = false, length = 11)
	public Long getWorkId() {
		return workId;
	}

	public void setWorkId(Long workId) {
		this.workId = workId;
	}

	@Column(name = "company_id", nullable = true, length = 11)
	public Long getCompanyId() {
		return companyId;
	}

	public void setCompanyId(Long companyId) {
		this.companyId = companyId;
	}

	@Column(name = "work_price", nullable = true, length = 11)
	public BigDecimal getWorkPrice() {
		return workPrice;
	}

	public void setWorkPrice(BigDecimal workPrice) {
		this.workPrice = workPrice;
	}

	@Column(name = "buyer_fee", nullable = true, length = 11)
	public BigDecimal getBuyerFee() {
		return buyerFee;
	}

	public void setBuyerFee(BigDecimal buyerFee) {
		this.buyerFee = buyerFee;
	}

	@Column(name = "work_status_type_code", nullable = true, length = 15)
	public String getWorkStatusTypeCode() {
		return workStatusTypeCode;
	}

	public void setWorkStatusTypeCode(String workStatusTypeCode) {
		this.workStatusTypeCode = workStatusTypeCode;
	}

	@Column(name = "payment_terms_enabled", nullable = true)
	public Boolean getPaymentTermsEnabled() {
		return paymentTermsEnabled;
	}

	public void setPaymentTermsEnabled(Boolean paymentTermsEnabled) {
		this.paymentTermsEnabled = paymentTermsEnabled;
	}

	@Column(name = "industry_id", nullable = true, length = 11)
	public Long getIndustryId() {
		return industryId;
	}

	public void setIndustryId(Long industryId) {
		this.industryId = industryId;
	}

	@Column(name = "buyer_total_cost")
	public BigDecimal getBuyerTotalCost() {
		return buyerTotalCost;
	}

	public void setBuyerTotalCost(BigDecimal buyerTotalCost) {
		this.buyerTotalCost = buyerTotalCost;
	}

	@Column(name = "buyer_user_id", nullable = false, length = 11)
	public Long getBuyerUserId() {
		return buyerUserId;
	}

	public void setBuyerUserId(Long buyerUserId) {
		this.buyerUserId = buyerUserId;
	}

	@Column(name = "active_resource_user_id", length = 11)
	public Long getActiveResourceUserId() {
		return activeResourceUserId;
	}

	public void setActiveResourceUserId(Long activeResourceUserId) {
		this.activeResourceUserId = activeResourceUserId;
	}

	@Column(name = "active_resource_company_id", length = 11)
	public Long getActiveResourceCompanyId() {
		return activeResourceCompanyId;
	}

	public void setActiveResourceCompanyId(Long activeResourceCompanyId) {
		this.activeResourceCompanyId = activeResourceCompanyId;
	}

	@Column(name = "account_pricing_type_code", length = 15, nullable = false)
	public String getAccountPricingType() {
		return accountPricingType;
	}

	public void setAccountPricingType(String accountPricingType) {
		this.accountPricingType = accountPricingType;
	}

	@Column(name = "account_service_type_code", length = 10, nullable = false)
	public String getAccountServiceType() {
		return accountServiceType;
	}

	public void setAccountServiceType(String accountServiceType) {
		this.accountServiceType = accountServiceType;
	}
}
