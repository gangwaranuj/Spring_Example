package com.workmarket.domains.model.fulfillment;

import java.io.Serializable;
import java.math.BigDecimal;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Column;
import javax.persistence.Embeddable;

@Access(AccessType.PROPERTY)
@Embeddable
public class FulfillmentStrategy implements Serializable {

	private static final long serialVersionUID = -8710079965857458451L;

	private BigDecimal buyerFee;
	private BigDecimal workPrice;
	private BigDecimal amountEarned;
	private BigDecimal refundedAmount;
	private BigDecimal voidedAmount;	
	private BigDecimal workPricePriorComplete; 
	private BigDecimal buyerTotalCost;

	public FulfillmentStrategy() {
		buyerFee = BigDecimal.ZERO;
		workPrice = BigDecimal.ZERO;
		amountEarned = BigDecimal.ZERO;
		refundedAmount = BigDecimal.ZERO;
		voidedAmount = BigDecimal.ZERO;
		workPricePriorComplete = BigDecimal.ZERO;
		buyerTotalCost = BigDecimal.ZERO;
	}

	/**
	 * Fee charged by Work Market for this work performed by the contractor based on the buyer's company fee schedule. This fee should be frozen at the moment of assignment
	 * 
	 * @return fee charged by Work Market for work performed by "user" contractor
	 */
	@Column(name = "buyer_fee")
	public BigDecimal getBuyerFee() {
		return buyerFee;
	}

	public void setBuyerFee(BigDecimal buyerFee) {
		this.buyerFee = buyerFee;
	}

	@Column(name = "work_price", nullable = true)
	public BigDecimal getWorkPrice() {
		return workPrice;
	}

	public void setWorkPrice(BigDecimal workPrice) {
		this.workPrice = workPrice;
	}

	@Column(name = "amount_earned", nullable = true)
	public BigDecimal getAmountEarned() {
		return amountEarned;
	}

	public void setAmountEarned(BigDecimal amountEarned) {
		this.amountEarned = amountEarned;
	}

	@Column(name = "refunded_amount", nullable = true)
	public BigDecimal getRefundedAmount() {
		return refundedAmount;
	}

	public void setRefundedAmount(BigDecimal refundedAmount) {
		this.refundedAmount = refundedAmount;
	}

	@Column(name = "voided_amount_earned", nullable = true)
	public BigDecimal getVoidedAmount() {
		return voidedAmount;
	}

	public void setVoidedAmount(BigDecimal voidedAmount) {
		this.voidedAmount = voidedAmount;
	}

	@Column(name = "buyer_total_cost")
	public BigDecimal getBuyerTotalCost() {
		return buyerTotalCost;
	}
	
	public void setBuyerTotalCost(BigDecimal buyerTotalCost) {
		this.buyerTotalCost = buyerTotalCost;
	}

	@Column(name = "work_price_prior_complete")
	public BigDecimal getWorkPricePriorComplete() {
		return workPricePriorComplete;
	}
	
	public void setWorkPricePriorComplete(BigDecimal workPricePriorComplete) {
		this.workPricePriorComplete = workPricePriorComplete;
	}
}