package com.workmarket.api.internal.dao;

import com.workmarket.api.internal.model.PlutusInvoiceInvoice;
import com.workmarket.dao.DAOInterface;
import com.workmarket.domains.model.invoice.Invoice;

import java.util.Collection;

public interface PlutusInvoiceInvoiceDAO extends DAOInterface<PlutusInvoiceInvoice> {
  Collection<Invoice> findAllInvoicesByPlutusInvoiceUuid(final String plutusInvoiceUuid);
}
