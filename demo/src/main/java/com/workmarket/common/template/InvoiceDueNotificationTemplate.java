package com.workmarket.common.template;

import com.google.common.collect.Maps;
import com.workmarket.domains.model.invoice.Invoice;
import com.workmarket.domains.model.notification.NotificationType;
import com.workmarket.domains.work.model.WorkDue;
import com.workmarket.configuration.Constants;
import com.workmarket.service.infra.communication.ReplyToType;
import com.workmarket.utility.DateUtilities;

import java.util.List;
import java.util.Map;

public class InvoiceDueNotificationTemplate extends NotificationTemplate {

	private static final long serialVersionUID = -2762969380456886719L;
	private List<Invoice> invoices;
	private final Map<Long, WorkDue> assignments;
	private boolean singleInvoice;

	private Map<Long, String> formattedDates = Maps.newHashMap();

	public InvoiceDueNotificationTemplate(Long toId, List<Invoice> invoices, Map<Long, WorkDue> invoiceAssignments, NotificationType notificationType, boolean singleInvoice) {
		super(Constants.EMAIL_USER_ID_TRANSACTIONAL, toId, notificationType, ReplyToType.TRANSACTIONAL);
		this.invoices = invoices;
		this.assignments = invoiceAssignments;
		this.singleInvoice = singleInvoice;
		updateDates();
	}

	public List<Invoice> getInvoices() {
		return invoices;
	}

	public Map<Long, WorkDue> getAssignments() {
		return assignments;
	}

	public boolean isSingleInvoice() {
		return singleInvoice;
	}

	public void setSingleInvoice(boolean singleInvoice) {
		this.singleInvoice = singleInvoice;
	}

	public Map<Long, String> getFormattedDates() {
		return formattedDates;
	}

	@Override
	public void setTimeZoneId(String timeZoneId) {
		this.timeZoneId = timeZoneId;
		updateDates();
	}

	private void updateDates() {
		for (Invoice invoice : invoices) {
			formattedDates.put(invoice.getId(), DateUtilities.formatDateForEmailNoTime(invoice.getDueDate(), getTimeZoneId()));
		}
	}
}
