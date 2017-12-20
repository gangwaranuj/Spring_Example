package com.workmarket.common.template;

import com.google.common.collect.Iterables;
import com.workmarket.configuration.Constants;
import com.workmarket.domains.model.User;
import com.workmarket.domains.model.notification.NotificationType;
import com.workmarket.domains.work.model.Work;
import com.workmarket.service.business.wrapper.AcceptWorkResponse;
import com.workmarket.service.infra.communication.ReplyToType;
import org.apache.commons.lang.StringEscapeUtils;
import org.springframework.util.Assert;

public class BundleWorkAcceptFailedNotificationTemplate extends AbstractWorkNotificationTemplate {
	private static final long serialVersionUID = -8178449919886161830L;
	private User worker;
	private String message;
	private String title;

	public BundleWorkAcceptFailedNotificationTemplate(long toId, User worker, Work work, AcceptWorkResponse failure) {
		super(Constants.EMAIL_USER_ID_TRANSACTIONAL, toId, new NotificationType(NotificationType.BUNDLE_WORK_ACCEPT_FAILED), ReplyToType.TRANSACTIONAL, work);

		Assert.notNull(failure);
		Assert.notNull(worker);
		Assert.notNull(work);

		String message = Iterables.getFirst(failure.getMessages(), "");
		this.message = StringEscapeUtils.escapeHtml(message);
		this.title = failure.getWork() != null ? StringEscapeUtils.escapeHtml(failure.getWork().getTitle()) : "";
		this.worker = worker;
	}

	public String getTitle() {
		return title;
	}

	public String getMessage() {
		return message;
	}

	public User getWorker() {
		return worker;
	}
}
