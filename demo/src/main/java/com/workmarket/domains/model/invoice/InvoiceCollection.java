package com.workmarket.domains.model.invoice;

import com.google.common.collect.Sets;
import com.workmarket.domains.model.audit.AuditChanges;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.Transient;
import java.util.Set;

/**
 * Invoice Collection is a group of invoices and it extends from {@link Invoice Invoice}
 * because conceptually it is one when displaying all the invoices on a particular account.
 * In Hibernate if we say "Select * from invoice" we'll get the two types.
 *
 * @author rocio
 *
 */
@Entity(name = "invoiceCollection")
@DiscriminatorValue(InvoiceCollection.INVOICE_COLLECTION_TYPE)
@AuditChanges
public class InvoiceCollection extends InvoiceSummary {


	private Set<Invoice> invoices = Sets.newLinkedHashSet();
	public static final String INVOICE_COLLECTION_TYPE = "collection";

	public InvoiceCollection() {
		super();
	}

	@Override
	@Transient
	public String getType() {
		return INVOICE_COLLECTION_TYPE;
	}
}
