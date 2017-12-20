package com.workmarket.service.infra.payment;

import com.workmarket.domains.model.account.authorization.CreditCardTransactionAuthorizationAudit;
import com.workmarket.domains.model.account.authorization.TransactionAuthorizationAudit;
import com.workmarket.service.business.dto.PaymentDTO;
import com.workmarket.service.business.dto.PaymentResponseDTO;
import net.linkpt.secure.fdggwsapi.schemas_us.fdggwsapi.FDGGWSApiOrderRequest;
import net.linkpt.secure.fdggwsapi.schemas_us.fdggwsapi.FDGGWSApiOrderResponse;
import net.linkpt.secure.fdggwsapi.schemas_us.v1.Billing;
import net.linkpt.secure.fdggwsapi.schemas_us.v1.CreditCardData;
import net.linkpt.secure.fdggwsapi.schemas_us.v1.CreditCardTxType;
import net.linkpt.secure.fdggwsapi.schemas_us.v1.Payment;
import net.linkpt.secure.fdggwsapi.schemas_us.v1.Transaction;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.util.Assert;
import org.springframework.ws.client.core.WebServiceTemplate;
import org.springframework.ws.soap.client.SoapFaultClientException;

import java.math.BigDecimal;
import java.util.Calendar;

public class FirstDataPaymentAdapterImpl implements PaymentAdapter {

	private static final Log logger = LogFactory.getLog(FirstDataPaymentAdapterImpl.class);
	private static final String AMERICAN_EXPRESS = "amex";
	private static final String FIRST_DATA = "First Data";
	private static final String APPROVED = "APPROVED";

	@Qualifier("webServiceTemplate")
	@Autowired private WebServiceTemplate webServiceTemplate;

	/*
	 * The FDGG accepts $0.00 transactions for processing from AMEX, Visa, MasterCard, JCB, and
	 * Discover credit cards. The primary purpose for submitting a $0.00 charge total amount with a
	 * card billing address is for credit card verification (not lost or stolen), or AVS (address
	 * match/mismatch). A $0.00 Authorization does not Auth funds on the customer's account,
	 * cannot be submitted for settlement, nor have a Return transaction processed against it
	 */
	public PaymentResponseDTO doCardPayment(PaymentDTO paymentDTO) {
		Assert.state(StringUtils.isNotEmpty(paymentDTO.getCardNumber()), "Card Number is Missing");
		Assert.state(StringUtils.isNotEmpty(paymentDTO.getCardExpirationDateString()), "Card Expiration Date is Missing");
		Assert.state(StringUtils.isNotEmpty(paymentDTO.getCardSecurityCode()), "Card Security code is Missing");
		Assert.state(StringUtils.isNotEmpty(paymentDTO.getAmount()), "Amount is Missing");

		BigDecimal paymentAmount = new BigDecimal(paymentDTO.getAmount());
		// don't attempt 0 amount preauth on amex
		if (AMERICAN_EXPRESS.equals(paymentDTO.getCardType())) {
			return requestTransaction(paymentDTO, paymentAmount);
		}

		if (requestTransaction(paymentDTO, BigDecimal.ZERO).isApproved()) {
			return requestTransaction(paymentDTO, paymentAmount);
		}

		PaymentResponseDTO response = new PaymentResponseDTO();
		response.setResponseMessage("");
		response.setApproved(false);
		return response;

	}

	/**
	 * This should only be used for testing. We could protect this method if the unit test was in the
	 * com.workmarket.service.infra.payment package
	 *
	 * @param  paymentDTO
	 * @return PaymentResponseDTO
	 */
	public PaymentResponseDTO doCardPaymentSkipAuth(PaymentDTO paymentDTO) {
		Assert.state(StringUtils.isNotEmpty(paymentDTO.getCardNumber()), "Card Number is Missing");
		Assert.state(StringUtils.isNotEmpty(paymentDTO.getCardExpirationDateString()), "Card Expiration Date is Missing");
		Assert.state(StringUtils.isNotEmpty(paymentDTO.getCardSecurityCode()), "Card Security code is Missing");

		return requestTransaction(paymentDTO, new BigDecimal(paymentDTO.getAmount()));
	}

	@Override
	public PaymentResponseDTO auditCreditCardTransaction(PaymentResponseDTO paymentResponseDTO, TransactionAuthorizationAudit authorizationAudit) {
		logger.debug(String.format("[firstdata] result: %s", authorizationAudit.getCreditCardType()));
		logger.debug(String.format("[firstdata] result: %s", authorizationAudit.getTransactionResponse()));
		logger.debug(String.format("[firstdata] order id: %s", authorizationAudit.getTransactionOrderId()));
		logger.debug(String.format("[firstdata] tdate: %s", authorizationAudit.getTransactionDate()));
		logger.debug(String.format("[firstdata] error: %s", authorizationAudit.getTransactionErrorMessage()));
		logger.debug(String.format("[firstdata] approval code: %s", authorizationAudit.getTransactionApprovalCode()));

		if (paymentResponseDTO != null) {
			paymentResponseDTO.setAuthorizationAudit(authorizationAudit);
		}
		return paymentResponseDTO;
	}

	private TransactionAuthorizationAudit auditCreditCardTransaction(PaymentDTO paymentDTO, FDGGWSApiOrderResponse orderResponse) {
		CreditCardTransactionAuthorizationAudit authorizationAudit = new CreditCardTransactionAuthorizationAudit();
		authorizationAudit.setFirstName(paymentDTO.getFirstName());
		authorizationAudit.setLastName(paymentDTO.getLastName());
		authorizationAudit.setProviderName(FIRST_DATA);
		authorizationAudit.setCreditCardType(paymentDTO.getCardType());
		authorizationAudit.setTransactionResponse(orderResponse.getTransactionResult());
		authorizationAudit.setTransactionOrderId(orderResponse.getOrderId());
		authorizationAudit.setTransactionErrorMessage(orderResponse.getErrorMessage());
		authorizationAudit.setTransactionApprovalCode(orderResponse.getApprovalCode());

		Calendar now = Calendar.getInstance();
		if (StringUtils.isNotBlank(orderResponse.getTDate())) {
			now.setTimeInMillis(Long.valueOf(orderResponse.getTDate()));
		}

		authorizationAudit.setTransactionDate(now);
		return authorizationAudit;
	}

	private PaymentResponseDTO requestTransaction(PaymentDTO paymentDTO, BigDecimal paymentAmount) {

		PaymentResponseDTO response = new PaymentResponseDTO();

		try {
			net.linkpt.secure.fdggwsapi.schemas_us.fdggwsapi.ObjectFactory fdggwsapiObjectFactory = new net.linkpt.secure.fdggwsapi.schemas_us.fdggwsapi.ObjectFactory();
			net.linkpt.secure.fdggwsapi.schemas_us.v1.ObjectFactory v1ObjectFactory = new net.linkpt.secure.fdggwsapi.schemas_us.v1.ObjectFactory();

			CreditCardTxType ccTxType = v1ObjectFactory.createCreditCardTxType();
			ccTxType.setType("sale");

			CreditCardData ccData = v1ObjectFactory.createCreditCardData();
			fillCreditCardData(ccData, paymentDTO);

			Billing billing = v1ObjectFactory.createBilling();
			fillBillingInformation(billing, paymentDTO);

			Payment payment = v1ObjectFactory.createPayment();
			payment.setChargeTotal(paymentAmount);

			Transaction tran = v1ObjectFactory.createTransaction();
			tran.setCreditCardTxType(ccTxType);
			tran.setCreditCardData(ccData);
			tran.setBilling(billing);
			tran.setPayment(payment);

			FDGGWSApiOrderRequest orderRequest = fdggwsapiObjectFactory.createFDGGWSApiOrderRequest();
			orderRequest.setTransaction(tran);
			FDGGWSApiOrderResponse orderResponse = (FDGGWSApiOrderResponse) webServiceTemplate.marshalSendAndReceive(orderRequest);

			TransactionAuthorizationAudit transactionAuthorizationAudit = auditCreditCardTransaction(paymentDTO, orderResponse);
			auditCreditCardTransaction(response, transactionAuthorizationAudit);

			logger.debug("[firstData] response " + orderResponse.toString());

			boolean isResponseApproved = APPROVED.equals(orderResponse.getTransactionResult());

			// if Amex, ignore cvv2 issues until we get first data to turn that on
			if (AMERICAN_EXPRESS.equals(paymentDTO.getCardType())) {
				response.setApproved(isResponseApproved);
				return response;
			}

			isResponseApproved = isResponseApproved && isValidCode(orderResponse.getApprovalCode());
			response.setApproved(isResponseApproved);
			if (!isResponseApproved) {
				response.setResponseMessage(orderResponse.getErrorMessage());
			}
			return response;

		} catch (SoapFaultClientException e) {
			logger.error(e);
			response.setApproved(false);
			response.setResponseMessage("An error occured with processing payment");
			return response;
		}
	}

	private void fillCreditCardData(CreditCardData ccData, PaymentDTO paymentDTO) {
		ccData.setCardNumber(paymentDTO.getCardNumber());
		ccData.setExpMonth(paymentDTO.getCardExpirationDateString().substring(0, 2));
		ccData.setExpYear(paymentDTO.getCardExpirationDateString().substring(4, 6));
		ccData.setCardCodeValue(paymentDTO.getCardSecurityCode());
		ccData.setCardCodeIndicator("PROVIDED");
	}

	private void fillBillingInformation(Billing billing, PaymentDTO paymentDTO) {
		billing.setName(paymentDTO.getNameOnCard());
		billing.setAddress1(paymentDTO.getAddress1());
		billing.setAddress2(paymentDTO.getAddress2());
		billing.setCity(paymentDTO.getCity());
		billing.setState(paymentDTO.getState());
		billing.setZip(paymentDTO.getPostalCode());
		billing.setCountry(paymentDTO.getCountry());
	}

	/*
	 * The last alphabetic character in the middle (M) 
	 * is a code indicating whether the card code matched the card-issuing bank's code.		
	 * Format: 0097820000019564:YNAM:12345678901234567890123:
	 */
	private boolean isValidCode(String approvalCode) {
		char ccv2ApprovalCode;
		/*
		* M -> Card code matches.
		* N -> Card code does not match.
		* P -> Not processed
		* S -> Merchant has indicated that the card code is not present on the card.
		* U -> Issuer is not certified and/or has not provided encryption keys.
		* X -> No response from the credit card association was received.
		*/
		if (StringUtils.isNotBlank(approvalCode)) {
			ccv2ApprovalCode = approvalCode.substring(approvalCode.indexOf(":")).charAt(4);
			return (ccv2ApprovalCode == 'M');
		}

		return false;
	}

}