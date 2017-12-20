package com.workmarket.common.template;

import com.workmarket.domains.model.invoice.Statement;
import com.workmarket.domains.model.notification.NotificationType;
import com.workmarket.configuration.Constants;
import com.workmarket.service.infra.communication.ReplyToType;

public class StatementReminderNotificationTemplate extends NotificationTemplate {

	private static final long serialVersionUID = 4421396916716642376L;
	private Statement statement;
	private Integer numberOfInvoices;

	public StatementReminderNotificationTemplate(Long toId, Statement statement) {
		super(Constants.EMAIL_USER_ID_TRANSACTIONAL, toId, new NotificationType(NotificationType.STATEMENT_REMINDER), ReplyToType.TRANSACTIONAL);
		this.statement = statement;
		this.numberOfInvoices = statement.getInvoices().size();
	}

	public Statement getStatement() {
		return statement;
	}

	public Integer getNumberOfInvoices() {
		return numberOfInvoices;
	}
}
