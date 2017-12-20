package com.workmarket.domains.payments.dao;

import com.workmarket.dao.DAOInterface;
import com.workmarket.domains.model.invoice.CreditMemoAudit;

public interface CreditMemoAuditDAO extends DAOInterface<CreditMemoAudit> {

	public CreditMemoAudit findByInvoiceId(Long id);
	public CreditMemoAudit findByOriginalInvoiceId(Long id);
	public boolean creditMemoAlreadyExisted(Long id);
}
