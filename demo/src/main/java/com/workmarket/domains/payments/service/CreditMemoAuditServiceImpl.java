package com.workmarket.domains.payments.service;

import com.workmarket.domains.model.invoice.CreditMemoAudit;
import com.workmarket.domains.payments.dao.CreditMemoAuditDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CreditMemoAuditServiceImpl implements CreditMemoAuditService {

	@Autowired
	private CreditMemoAuditDAO creditMemoAuditDAO;


	@Override
	public void saveOrUpdate(CreditMemoAudit creditMemoAudit) {
		creditMemoAuditDAO.saveOrUpdate(creditMemoAudit);
	}

	@Override
	public CreditMemoAudit findByInvoiceId(Long id) {
		return creditMemoAuditDAO.findByInvoiceId(id);
	}

	@Override
	public CreditMemoAudit findByOriginalInvoiceId(Long id) {
		return creditMemoAuditDAO.findByOriginalInvoiceId(id);
	}

	@Override
	public boolean creditMemoAlreadyExisted(Long id){ return creditMemoAuditDAO.creditMemoAlreadyExisted(id);}

}
