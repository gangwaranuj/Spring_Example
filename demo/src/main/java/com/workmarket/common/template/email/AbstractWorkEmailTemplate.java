package com.workmarket.common.template.email;

import com.workmarket.configuration.Constants;
import com.workmarket.domains.model.notification.NotificationType;

public abstract class AbstractWorkEmailTemplate extends EmailTemplate {
	private static final long serialVersionUID = 8058826400990367353L;

	private String message;


	private Long workId;
	private String workTitle;
	private String workShortUrl;
	private String workRelativeURI;

	protected AbstractWorkEmailTemplate(String subject, String message, Long toId,
		Long workId, String workTitle, String workShortUrl, String workRelativeURI) {
		super(Constants.EMAIL_USER_ID_TRANSACTIONAL, toId, subject);
		this.message = message;
		this.workId = workId;
		this.workTitle = workTitle;
		this.workShortUrl = workShortUrl;
		this.workRelativeURI = workRelativeURI;
	}

	protected AbstractWorkEmailTemplate(String subject, String message, Long toId, NotificationType notificationType,
		Long workId, String workTitle, String workShortUrl, String workRelativeURI) {
		super(Constants.EMAIL_USER_ID_TRANSACTIONAL, toId, subject, notificationType);
		this.message = message;
		this.workId = workId;
		this.workTitle = workTitle;
		this.workShortUrl = workShortUrl;
		this.workRelativeURI = workRelativeURI;
	}

	public String getMessage() {
		return message;
	}

	public Long getWorkId() {
		return workId;
	}

	public String getWorkTitle() {
		return workTitle;
	}

	public String getWorkShortUrl() {
		return workShortUrl;
	}

	public String getWorkRelativeURI() {
		return workRelativeURI;
	}
}
