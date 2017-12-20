package com.workmarket.common.template.email;

import com.workmarket.configuration.Constants;

public class RegistrationRemindUserEmailTemplate extends EmailTemplate {

	private static final long serialVersionUID = -9207497758291344489L;

	public RegistrationRemindUserEmailTemplate(Long toId){
		super(Constants.EMAIL_USER_ID_TRANSACTIONAL, toId, "Please confirm your registration with workmarket");
	}
}
