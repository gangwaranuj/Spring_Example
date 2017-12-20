package com.workmarket.common.template.email;

import com.workmarket.configuration.Constants;
import org.apache.commons.lang.StringUtils;

public class RegistrationConfirmEmailTemplate extends EmailTemplate {

	private static final long serialVersionUID = 1913840805589432361L;

	public RegistrationConfirmEmailTemplate(Long toId){
		super(Constants.EMAIL_USER_ID_TRANSACTIONAL, toId, null);
	}

	public RegistrationConfirmEmailTemplate(Long toId, String toEmail){
		super(Constants.EMAIL_USER_ID_TRANSACTIONAL, toId, toEmail, StringUtils.EMPTY);
	}

}
