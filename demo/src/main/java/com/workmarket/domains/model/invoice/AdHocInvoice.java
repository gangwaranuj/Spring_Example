package com.workmarket.domains.model.invoice;

import com.workmarket.domains.model.Company;
import com.workmarket.domains.model.audit.AuditChanges;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.Transient;

@Entity(name = "adHocInvoice")
@DiscriminatorValue(AdHocInvoice.AD_HOC_INVOICE_TYPE)
@AuditChanges
public class AdHocInvoice extends AbstractServiceInvoice {

	private static final long serialVersionUID = 1L;
	public static final String AD_HOC_INVOICE_TYPE = "adHoc";

	public AdHocInvoice() {
		super();
		setDescription("Work Market Invoice");
	}

	public AdHocInvoice(Company company) {
		super(company);
		setDescription("Work Market Invoice");
	}

	@Override
	@Transient
	public String getType() {
		return AD_HOC_INVOICE_TYPE;
	}

}
