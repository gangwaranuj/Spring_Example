package com.workmarket.service.business.dto;

import java.io.Serializable;
import java.math.BigDecimal;

public class WorkCostDTO implements Serializable {

	private static final long serialVersionUID = -6456878066809550945L;

	public WorkCostDTO() {}

	public WorkCostDTO(BigDecimal totalResourceCost, BigDecimal buyerFee, BigDecimal totalBuyerCost) {
		setTotalResourceCost(totalResourceCost);
		setBuyerFee(buyerFee);
		setTotalBuyerCost(totalBuyerCost);
	}

	/**
	 * Instance variables and constants
	 */
	private BigDecimal totalResourceCost;
	private BigDecimal buyerFee;
	private BigDecimal totalBuyerCost;

	public BigDecimal getTotalResourceCost() {
		return totalResourceCost;
	}

	public WorkCostDTO setTotalResourceCost(BigDecimal totalResourceCost) {
		this.totalResourceCost = totalResourceCost;
		return this;
	}

	public BigDecimal getBuyerFee() {
		return buyerFee;
	}

	public WorkCostDTO setBuyerFee(BigDecimal buyerFee) {
		this.buyerFee = buyerFee;
		return this;
	}

	public BigDecimal getTotalBuyerCost() {
		return totalBuyerCost;
	}

	public WorkCostDTO setTotalBuyerCost(BigDecimal totalBuyerCost) {
		this.totalBuyerCost = totalBuyerCost;
		return this;
	}

	public String toString() {
		StringBuilder sb = new StringBuilder("AccountRegisterMoniesDAO[");
		sb.append("totalResourceCost:" + getTotalResourceCost());
		sb.append(", buyerFee:" + getBuyerFee());
		sb.append(", totalBuyerCost:" + getTotalBuyerCost());
		sb.append("]");
		return sb.toString();
	}

}
