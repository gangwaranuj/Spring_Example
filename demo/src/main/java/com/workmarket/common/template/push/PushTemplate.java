package com.workmarket.common.template.push;

import com.workmarket.common.template.TwoWayTypedTemplate;
import com.workmarket.domains.model.notification.NotificationType;

public class PushTemplate extends TwoWayTypedTemplate {

	private static final long serialVersionUID = 1L;


	protected static final String PUSH_HEADER_TEMPLATE = "HeaderPushTemplate";
	protected static final String PUSH_FOOTER_TEMPLATE = "FooterPushTemplate";
	protected static final String PUSH_TEMPLATE_DIRECTORY_PATH = "/template/push";
	protected static final String PUSH_TEMPLATE_EXTENSION = ".vm";


	private String message;
	private String action;

	public PushTemplate() {

	}

	public PushTemplate(Long fromId, Long toId) {
		super(fromId, toId);
	}

	public PushTemplate(Long fromId, Long toId, String message) {
		super(fromId, toId);
		this.message = message;
	}

	public PushTemplate(Long fromId, Long toId, NotificationType notificationType) {
		super(fromId, toId, notificationType);
	}


	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getAction() {
		return action;
	}

	public void setAction(String action) {
		this.action = action;
	}

	@Override
	public String getPath() {
		return "/template/push/";
	}
}
