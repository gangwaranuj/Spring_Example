package com.workmarket.dao.account.payment;

import com.workmarket.dao.DAOInterface;
import com.workmarket.domains.model.account.payment.PaymentTermsDuration;

import java.util.List;

/**
 * Created by nick on 9/17/12 7:03 PM
 */
public interface PaymentTermsDurationDAO extends DAOInterface<PaymentTermsDuration> {
	List<PaymentTermsDuration> findDefaultPaymentTermsDurations();
}
