package com.workmarket.common.template;

import com.google.common.collect.Lists;
import com.workmarket.common.template.email.EmailTemplate;
import com.workmarket.service.business.dto.FileDTO;
import com.workmarket.configuration.Constants;

/**
 * User: KhalidRich
 * Date: 8/23/13
 */
public class SearchCSVGeneratedTemplate extends EmailTemplate {

	private static final long serialVersionUID = -8732907212118314979L;

	public SearchCSVGeneratedTemplate(String recipient, FileDTO file) {
		super(Constants.EMAIL_USER_ID_TRANSACTIONAL, "");
		this.toEmail = recipient;
		this.attachments = Lists.newArrayList(file);
	}

}
