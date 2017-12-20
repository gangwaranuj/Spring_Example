package com.workmarket.service.infra.business;

import com.workmarket.dao.account.authorization.TransactionAuthorizationAuditDAO;
import com.workmarket.service.business.dto.PaymentDTO;
import com.workmarket.service.business.dto.PaymentResponseDTO;
import com.workmarket.service.infra.payment.PaymentAdapter;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PaymentServiceImpl implements PaymentService {

	@Autowired private PaymentAdapter paymentAdapter;
	@Autowired private TransactionAuthorizationAuditDAO transactionAuthorizationAuditDAO;

	@Override
	public PaymentResponseDTO doCardPayment(PaymentDTO paymentDTO) {
		return auditPaymentTransaction(paymentAdapter.doCardPayment(paymentDTO));
	}

	@Override
	public PaymentResponseDTO doCardPaymentSkipAuth(PaymentDTO paymentDTO) {
		return auditPaymentTransaction(paymentAdapter.doCardPaymentSkipAuth(paymentDTO));
	}

	@Override
	public PaymentResponseDTO auditPaymentTransaction(PaymentResponseDTO paymentResponseDTO) {
		if (paymentResponseDTO.getAuthorizationAudit() != null &&
				StringUtils.isNotBlank(paymentResponseDTO.getAuthorizationAudit().getProviderName())) {
			transactionAuthorizationAuditDAO.saveOrUpdate(paymentResponseDTO.getAuthorizationAudit());
		}
		return paymentResponseDTO;
	}

}
