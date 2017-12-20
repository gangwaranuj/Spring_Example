package com.workmarket.common.template;

import com.workmarket.domains.model.account.RegisterTransaction;
import com.workmarket.domains.model.notification.NotificationType;
import com.workmarket.configuration.Constants;
import com.workmarket.service.infra.communication.ReplyToType;
import com.workmarket.utility.DateUtilities;

import java.math.BigDecimal;
import java.util.Calendar;

public class FundsPendingNotificationTemplate extends NotificationTemplate {
	private static final long serialVersionUID = 4724355431525970302L;

	private RegisterTransaction transaction;
	private Integer invoicesDueCount;
	private BigDecimal invoicesDueTotal;
	private Calendar invoicesDueDate;

	public FundsPendingNotificationTemplate(Long toId, RegisterTransaction transaction, Integer invoicesDueCount, BigDecimal invoicesDueTotal, Calendar invoicesDueDate) {
		super(Constants.EMAIL_USER_ID_TRANSACTIONAL, toId, new NotificationType(NotificationType.MONEY_DEPOSITED), ReplyToType.TRANSACTIONAL);
		this.transaction = transaction;
		this.invoicesDueCount = invoicesDueCount;
		this.invoicesDueDate = DateUtilities.cloneCalendar(invoicesDueDate);
		this.invoicesDueTotal = invoicesDueTotal;
	}

	public RegisterTransaction getTransaction() {
		return transaction;
	}

	public Integer getInvoicesDueCount() {
		return invoicesDueCount;
	}

	public BigDecimal getInvoicesDueTotal() {
		return invoicesDueTotal;
	}

	public String getInvoicesDueDate() {
		if (invoicesDueDate == null) return "";
		return DateUtilities.formatDateForEmail(invoicesDueDate, getTimeZoneId());
	}


}
