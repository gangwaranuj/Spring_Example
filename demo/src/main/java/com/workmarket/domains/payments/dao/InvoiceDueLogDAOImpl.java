package com.workmarket.domains.payments.dao;
import com.workmarket.dao.AbstractDAO;
import com.workmarket.domains.model.invoice.InvoiceDueLog;

import org.springframework.stereotype.Repository;

@Repository
public class InvoiceDueLogDAOImpl extends AbstractDAO<InvoiceDueLog> implements InvoiceDueLogDAO {

	protected Class<InvoiceDueLog> getEntityClass() {
        return InvoiceDueLog.class;
    }

}
