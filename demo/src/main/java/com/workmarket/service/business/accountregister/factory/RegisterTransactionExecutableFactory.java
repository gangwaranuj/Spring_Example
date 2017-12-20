package com.workmarket.service.business.accountregister.factory;

import com.workmarket.domains.model.invoice.AbstractInvoice;
import com.workmarket.domains.model.lane.LaneType;
import com.workmarket.service.business.accountregister.RegisterTransactionExecutor;

/**
 * Author: rocio
 */
public interface RegisterTransactionExecutableFactory {

	<T extends RegisterTransactionExecutor> T newInstance(String transactionType);

	<T extends RegisterTransactionExecutor> T newInstance(LaneType laneType);

	<T extends RegisterTransactionExecutor> T newInvoicePaymentRegisterTransaction(AbstractInvoice invoice);
}
