package com.workmarket.service.business.pay;

import com.workmarket.domains.work.model.AbstractWork;
import com.workmarket.service.business.dto.PaymentSummaryDTO;

/**
 * Author: rocio
 */
public interface PaymentSummaryService {

	PaymentSummaryDTO generatePaymentSummaryForNegotiation(long negotiationId) throws Exception;

	PaymentSummaryDTO generatePaymentSummaryForWork(AbstractWork work);

	PaymentSummaryDTO generatePaymentSummaryForWork(long workId);
}
