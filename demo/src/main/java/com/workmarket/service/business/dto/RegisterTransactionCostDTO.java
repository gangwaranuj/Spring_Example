package com.workmarket.service.business.dto;

import com.workmarket.domains.model.account.RegisterTransactionCost;
import com.workmarket.utility.BeanUtilities;

import java.io.Serializable;

/**
 * Created by nick on 5/13/13 3:05 PM
 */
public class RegisterTransactionCostDTO implements Serializable {
	private static final long serialVersionUID = -3254006804110538107L;
	private String registerTransactionTypeCode;
	private Double percentageAmount;
	private Double fixedAmount;
	private Double percentageBasedAmountLimit;

	public static RegisterTransactionCostDTO newDTO(RegisterTransactionCost registerTransactionCost) {
		RegisterTransactionCostDTO dto = BeanUtilities.newBean(RegisterTransactionCostDTO.class, registerTransactionCost);
		dto.setRegisterTransactionTypeCode(registerTransactionCost.getRegisterTransactionType().getCode());
		return dto;
	}

	public String getRegisterTransactionTypeCode() {
		return registerTransactionTypeCode;
	}

	public void setRegisterTransactionTypeCode(String registerTransactionTypeCode) {
		this.registerTransactionTypeCode = registerTransactionTypeCode;
	}

	public Double getPercentageAmount() {
		return percentageAmount;
	}

	public void setPercentageAmount(Double percentageAmount) {
		this.percentageAmount = percentageAmount;
	}

	public Double getFixedAmount() {
		return fixedAmount;
	}

	public void setFixedAmount(Double fixedAmount) {
		this.fixedAmount = fixedAmount;
	}

	public Double getPercentageBasedAmountLimit() {
		return percentageBasedAmountLimit;
	}

	public void setPercentageBasedAmountLimit(Double percentageBasedAmountLimit) {
		this.percentageBasedAmountLimit = percentageBasedAmountLimit;
	}
}
