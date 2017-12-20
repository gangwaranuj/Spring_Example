package com.workmarket.common.template.email;

import com.workmarket.configuration.Constants;

public class GlobalCashCardCreatedEmailTemplate extends EmailTemplate {

	private static final long serialVersionUID = 1914840815689432362L;

	public GlobalCashCardCreatedEmailTemplate(Long toId) {
		super(Constants.EMAIL_USER_ID_TRANSACTIONAL, toId, null);
	}
}
