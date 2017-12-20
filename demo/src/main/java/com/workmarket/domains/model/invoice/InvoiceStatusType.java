package com.workmarket.domains.model.invoice;

import javax.persistence.Entity;
import javax.persistence.Table;

import com.workmarket.domains.model.LookupEntity;

@Entity(name="invoiceStatusType")
@Table(name="invoice_status_type")
public class InvoiceStatusType extends LookupEntity {

	private static final long serialVersionUID = -8226184200591432371L;
	
	public static final String PAYMENT_PENDING = "pending";
	public static final String PAID = "paid";
	public static final String PAID_OFFLINE = "paidoff";
	public static final String VOID = "void";

	public InvoiceStatusType() {
		super();
	}

	public InvoiceStatusType(String code) {
		super(code);
	}
}
