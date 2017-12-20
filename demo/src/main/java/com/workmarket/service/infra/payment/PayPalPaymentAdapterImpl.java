package com.workmarket.service.infra.payment;

import com.paypal.sdk.core.nvp.NVPDecoder;
import com.paypal.sdk.core.nvp.NVPEncoder;
import com.paypal.sdk.profiles.APIProfile;
import com.paypal.sdk.profiles.ProfileFactory;
import com.paypal.sdk.services.NVPCallerServices;
import com.workmarket.domains.model.account.authorization.CreditCardTransactionAuthorizationAudit;
import com.workmarket.domains.model.account.authorization.TransactionAuthorizationAudit;
import com.workmarket.service.business.dto.PaymentDTO;
import com.workmarket.service.business.dto.PaymentResponseDTO;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Value;

import java.util.Calendar;

public class PayPalPaymentAdapterImpl implements PaymentAdapter {

	private static final Log logger = LogFactory.getLog(PayPalPaymentAdapterImpl.class);

	@Value("${payment.adapter.paypal.apiUsername}")
	private String payPalAPIUsername;

	@Value("${payment.adapter.paypal.apiPassword}")
	private String payPalAPIPassword;

	@Value("${payment.adapter.paypal.signature}")
	private String payPalSignature;

	@Value("${payment.adapter.paypal.environment}")
	private String payPalEnvironment;

	@Value("${payment.adapter.paypal.subject}")
	private String payPalSubject;

	private static final String PAY_PAL = "PayPal";
	private static final String SUCCESS = "Success";

	public PaymentResponseDTO doCardPaymentSkipAuth(PaymentDTO paymentDTO) {

		//TODO - not sure we will ever use paypal
		// but we should figure out how to enforce cvv2 checking

		return doCardPayment(paymentDTO);

	}

	public PaymentResponseDTO doCardPayment(PaymentDTO paymentDTO) {
		// @see https://www.x.com/docs/DOC-1174 (DoDirectPayment NVP API Reference)
		// @see https://cms.paypal.com/cms_content/US/en_US/files/developer/nvp_DoDirectPayment_java.txt (Sample Code)

		NVPEncoder encoder = new NVPEncoder();
		NVPDecoder decoder = new NVPDecoder();
		try {
			NVPCallerServices caller = new NVPCallerServices();

			APIProfile profile = ProfileFactory.createSignatureAPIProfile();
			profile.setAPIUsername(payPalAPIUsername);
			profile.setAPIPassword(payPalAPIPassword);
			profile.setSignature(payPalSignature);
			profile.setEnvironment(payPalEnvironment);
			profile.setSubject(payPalSubject);
			caller.setAPIProfile(profile);

			// If we want to pass dates around in ISO8601 exclusively
			// String dateString = DateUtilities.format("MMyyyy", paymentDTO.getCardExpirationDateString());
			String dateString = paymentDTO.getCardExpirationDateString();

			encoder.add("VERSION", "51.0");
			encoder.add("METHOD", "DoDirectPayment");

			encoder.add("IPADDRESS", "");

			encoder.add("PAYMENTACTION", "Sale");
			encoder.add("AMT", paymentDTO.getAmount());
			encoder.add("CREDITCARDTYPE", paymentDTO.getCardType());
			encoder.add("ACCT", paymentDTO.getCardNumber());
			encoder.add("EXPDATE", dateString);
			encoder.add("CVV2", paymentDTO.getCardSecurityCode());
			encoder.add("FIRSTNAME", paymentDTO.getFirstName());
			encoder.add("LASTNAME", paymentDTO.getLastName());
			encoder.add("STREET", paymentDTO.getAddress1());
			encoder.add("STREET2", paymentDTO.getAddress2());
			encoder.add("CITY", paymentDTO.getCity());
			encoder.add("STATE", paymentDTO.getState());
			encoder.add("ZIP", paymentDTO.getPostalCode());
			encoder.add("COUNTRYCODE", paymentDTO.getCountry());

			String nvpRequest = encoder.encode();
			String nvpResponse = caller.call(nvpRequest);
			decoder.decode(nvpResponse);

		} catch (Exception e) {
			logger.error(e);
		}

		PaymentResponseDTO response = new PaymentResponseDTO();
		String responseMessage = decoder.get("ACK");
		response.setApproved(SUCCESS.equals(responseMessage));

		CreditCardTransactionAuthorizationAudit authorizationAudit = new CreditCardTransactionAuthorizationAudit();
		authorizationAudit.setProviderName(PAY_PAL);
		authorizationAudit.setFirstName(paymentDTO.getFirstName());
		authorizationAudit.setLastName(paymentDTO.getLastName());
		authorizationAudit.setTransactionDate(Calendar.getInstance());
		authorizationAudit.setTransactionResponse(responseMessage);

		return  auditCreditCardTransaction(response, authorizationAudit);
	}

	public void setPayPalAPIUsername(String payPalAPIUsername) {
		this.payPalAPIUsername = payPalAPIUsername;
	}

	public String getPayPalAPIUsername() {
		return payPalAPIUsername;
	}

	public void setPayPalAPIPassword(String payPalAPIPassword) {
		this.payPalAPIPassword = payPalAPIPassword;
	}

	public String getPayPalAPIPassword() {
		return payPalAPIPassword;
	}

	public void setPayPalSignature(String payPalSignature) {
		this.payPalSignature = payPalSignature;
	}

	public String getPayPalSignature() {
		return payPalSignature;
	}

	public void setPayPalEnvironment(String payPalEnvironment) {
		this.payPalEnvironment = payPalEnvironment;
	}

	public String getPayPalEnvironment() {
		return payPalEnvironment;
	}

	public void setPayPalSubject(String payPalSubject) {
		this.payPalSubject = payPalSubject;
	}

	public String getPayPalSubject() {
		return payPalSubject;
	}

	@Override
	public PaymentResponseDTO auditCreditCardTransaction(PaymentResponseDTO paymentResponseDTO, TransactionAuthorizationAudit authorizationAudit) {
		if (paymentResponseDTO != null) {
			paymentResponseDTO.setAuthorizationAudit(authorizationAudit);
		}
		return paymentResponseDTO;
	}
}