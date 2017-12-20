package com.workmarket.common.template.email;

import com.workmarket.configuration.Constants;

public class EvidenceReportCSVTemplate extends EmailTemplate {

	private static final long serialVersionUID = -3875083150392663252L;

	public EvidenceReportCSVTemplate(String toEmail) {
		super(Constants.EMAIL_USER_ID_TRANSACTIONAL,toEmail);
	}

}
