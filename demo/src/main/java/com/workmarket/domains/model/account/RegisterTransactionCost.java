package com.workmarket.domains.model.account;

import com.workmarket.domains.model.AbstractEntity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;
import java.math.BigDecimal;


@Entity(name = "register_transaction_cost")
@Table(name = "register_transaction_cost")
public class RegisterTransactionCost extends AbstractEntity {

	private static final long serialVersionUID = 1L;

	public static final Long LANE2_NEW_WORK_FEE_DEFAULT_COST_ID = 1L;
	public static final Long LANE3_NEW_WORK_FEE_DEFAULT_COST_ID = 2L;
	public static final Long LANE2_FINISHED_WORK_FEE_DEFAULT_COST_ID = 3L;
	public static final Long LANE3_FINISHED_WORK_FEE_DEFAULT_COST_ID = 4L;
	public static final Long CREDIT_CARD_FEE_DEFAULT_COST_ID = 5L;
	public static final Long BACKGROUND_CHECK_FEE_DEFAULT_COST_ID = 6L;
	public static final Long DRUG_TEST_FEE_DEFAULT_COST_ID = 7L;
	public static final Long LANE1_NEW_WORK_FEE_DEFAULT_COST_ID = 8L;
	public static final Long LANE1_FINISHED_WORK_FEE_DEFAULT_COST_ID = 9L;
	public static final Long BACKGROUND_CHECK_CANADA_FEE_DEFAULT_COST_ID = 10L;
	public static final Long AMEX_CREDIT_CARD_FEE_DEFAULT_COST_ID = 11L;
	public static final Long PAY_PAL_FEE_DEFAULT_COST_ID = 12L;
	public static final Long PAY_PAL_FEE_CANADA_DEFAULT_COST_ID = 13L;
	public static final Long WM_PAY_PAL_FEE_DEFAULT_COST_ID = 14L;
	public static final Long WM_PAY_PAL_FEE_CANADA_DEFAULT_COST_ID = 15L;
	public static final Long BACKGROUND_CHECK_INTERNATIONAL_FEE_DEFAULT_COST_ID = 16L;
	public static final Long PAY_PAL_FEE_INTL_DEFAULT_COST_ID = 17L;
	public static final Long WM_PAY_PAL_FEE_INTL_DEFAULT_COST_ID = 18L;

	private RegisterTransactionType registerTransactionType;
	private BigDecimal percentageAmount;
	private BigDecimal fixedAmount;
	private BigDecimal percentageBasedAmountLimit = BigDecimal.ZERO;

	public RegisterTransactionCost() {
		super();
	}

	public RegisterTransactionCost(Long id) {
		super();
		super.setId(id);
	}

	@Column(name = "percentage_amount")
	public BigDecimal getPercentageAmount() {
		return percentageAmount;
	}

	public void setPercentageAmount(BigDecimal percentageAmount) {
		this.percentageAmount = percentageAmount;
	}

	@Column(name = "fixed_amount")
	public BigDecimal getFixedAmount() {
		return fixedAmount;
	}

	public void setFixedAmount(BigDecimal fixedAmount) {
		this.fixedAmount = fixedAmount;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "register_transaction_type_code", referencedColumnName = "code", nullable = false)
	public RegisterTransactionType getRegisterTransactionType() {
		return registerTransactionType;
	}

	public void setRegisterTransactionType(RegisterTransactionType registerTransactionType) {
		this.registerTransactionType = registerTransactionType;
	}

	@Column(name = "percentage_based_amount_limit", nullable = false)
	public BigDecimal getPercentageBasedAmountLimit() {
		return percentageBasedAmountLimit;
	}

	public void setPercentageBasedAmountLimit(BigDecimal percentageBasedAmountLimit) {
		this.percentageBasedAmountLimit = percentageBasedAmountLimit;
	}

	@Transient
	public BigDecimal getPercentageAmountMultiplier() {
		BigDecimal percentageAmountMultiplier = BigDecimal.ONE;
		percentageAmountMultiplier = percentageAmountMultiplier.add(percentageAmount);
		return percentageAmountMultiplier;
	}

	@Transient
	public boolean hasPercentageBasedAmountLimit() {
		return BigDecimal.ZERO.compareTo(percentageBasedAmountLimit) < 0;
	}

	@Transient
	public boolean isFixed() {
		return fixedAmount != null && BigDecimal.ZERO.compareTo(fixedAmount) < 0;
	}

}
