package com.workmarket.domains.payments.dao;

import com.workmarket.domains.model.invoice.CreditMemoAudit;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertNotNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


@RunWith(MockitoJUnitRunner.class)
public class CreditMemoAuditDAOTest {

	@Mock
	CreditMemoAuditDAOImpl creditMemoAuditDAO;

	@Test
	public void testFindByInvoicdId_noEntryFound() {
		CreditMemoAudit expected = null;
		when(creditMemoAuditDAO.findByInvoiceId(1L)).thenReturn(null);
		assertEquals(creditMemoAuditDAO.findByInvoiceId(1L), expected);
	}

	@Test
	public void testFindByInvoicdId() {
		CreditMemoAudit creditMemoAudit = new CreditMemoAudit();
		when(creditMemoAuditDAO.findByInvoiceId(1L)).thenReturn(creditMemoAudit);
		assertNotNull(creditMemoAuditDAO.findByInvoiceId(1L));
	}

	@Test
	public void testFindByOriginalInvoicdId_noEntryFound() {
		CreditMemoAudit expected = null;
		when(creditMemoAuditDAO.findByOriginalInvoiceId(1L)).thenReturn(null);
		assertEquals(creditMemoAuditDAO.findByInvoiceId(1L), expected);
	}

	@Test
	public void testFindByOriginalInvoiceId() {
		CreditMemoAudit creditMemoAudit = new CreditMemoAudit();
		when(creditMemoAuditDAO.findByOriginalInvoiceId(1L)).thenReturn(creditMemoAudit);
		assertNotNull(creditMemoAuditDAO.findByOriginalInvoiceId(1L));
	}

	@Test
	public void testCreditMemoAlreadyExisted(){
		when(creditMemoAuditDAO.creditMemoAlreadyExisted(1L)).thenReturn(true);
		CreditMemoAudit creditMemoAudit = creditMemoAuditDAO.findByOriginalInvoiceId(1L);
		verify(creditMemoAuditDAO).findByOriginalInvoiceId(1L);
	}
}
