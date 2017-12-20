package com.workmarket.domains.payments.service;

import com.workmarket.domains.model.invoice.CreditMemoAudit;

public interface CreditMemoAuditService {

	public void saveOrUpdate(CreditMemoAudit creditMemoAudit);
	public CreditMemoAudit findByInvoiceId(Long id);
	public CreditMemoAudit findByOriginalInvoiceId(Long id);
	public boolean creditMemoAlreadyExisted(Long id);

}
