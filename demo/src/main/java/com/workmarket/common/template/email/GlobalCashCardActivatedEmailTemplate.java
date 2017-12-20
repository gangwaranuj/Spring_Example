package com.workmarket.common.template.email;

import com.workmarket.configuration.Constants;

public class GlobalCashCardActivatedEmailTemplate extends EmailTemplate  {

	private static final long serialVersionUID = 3915850835689432363L;

	public GlobalCashCardActivatedEmailTemplate(Long toId){
		super(Constants.EMAIL_USER_ID_TRANSACTIONAL, toId, null);
	}

}
