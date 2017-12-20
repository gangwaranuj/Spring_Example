package com.workmarket.common.template;

import java.math.BigDecimal;

import com.workmarket.common.template.email.EmailTemplate;
import com.workmarket.configuration.Constants;

public class LowBalanceEmailTemplate extends EmailTemplate {

	private static final long serialVersionUID = 6964015287643973495L;
	private static final String SUBJECT = "Work Market Low Balance Alert";

	private BigDecimal spendLimit = BigDecimal.ZERO;

	public LowBalanceEmailTemplate(Long toId, String toEmail, BigDecimal spendLimit) {
		super(Constants.EMAIL_USER_ID_TRANSACTIONAL, toId, toEmail, SUBJECT);
		this.spendLimit = spendLimit;
	}

	public BigDecimal getSpendLimit() {
		return spendLimit;
	}

}
