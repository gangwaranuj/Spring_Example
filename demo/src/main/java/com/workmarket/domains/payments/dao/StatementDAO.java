package com.workmarket.domains.payments.dao;

import com.workmarket.domains.model.invoice.Statement;
import com.workmarket.domains.model.invoice.StatementPagination;

public interface StatementDAO extends AbstractInvoiceDAO<Statement> {

	StatementPagination findAllStatements(Long companyId, StatementPagination pagination);
}
