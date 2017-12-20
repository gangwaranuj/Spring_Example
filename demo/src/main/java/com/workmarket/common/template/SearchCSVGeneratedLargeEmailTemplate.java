package com.workmarket.common.template;

import com.workmarket.common.template.email.EmailTemplate;
import com.workmarket.configuration.Constants;

/**
 * Author: rocio
 */
public class SearchCSVGeneratedLargeEmailTemplate extends EmailTemplate {

	private static final long serialVersionUID = -8732907212718314979L;
	private String downloadUri;

	public SearchCSVGeneratedLargeEmailTemplate(String downloadUri, String recipient) {
		super(Constants.EMAIL_USER_ID_TRANSACTIONAL, "");
		this.toEmail = recipient;
		this.downloadUri = downloadUri;
	}

	public String getDownloadUri() {
		return downloadUri;
	}
}
