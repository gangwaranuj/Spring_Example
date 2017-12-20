package com.workmarket.service.infra.payment;

import com.workmarket.service.business.dto.PaymentDTO;
import com.workmarket.service.business.dto.PaymentResponseDTO;
import net.linkpt.secure.fdggwsapi.schemas_us.fdggwsapi.FDGGWSApiOrderRequest;
import net.linkpt.secure.fdggwsapi.schemas_us.fdggwsapi.FDGGWSApiOrderResponse;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Calendar;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.internal.verification.VerificationModeFactory.times;

/**
 * author: rocio
 */
@RunWith(MockitoJUnitRunner.class)
public class FirstDataPaymentAdapterImplUniTest {

	@Mock private org.springframework.ws.client.core.WebServiceTemplate webServiceTemplate;
	@InjectMocks FirstDataPaymentAdapterImpl firstDataPaymentAdapter;
	private PaymentDTO paymentDTO;
	private PaymentDTO validPaymentDTO;
	//private FDGGWSApiOrderResponse orderResponse;

	@Before
	public void setup() {
		paymentDTO = new PaymentDTO();
		validPaymentDTO = new PaymentDTO();
		validPaymentDTO.setCardNumber("235635631632317");
		validPaymentDTO.setCardExpirationMonth("04");
		validPaymentDTO.setCardExpirationYear("2056");
		validPaymentDTO.setCardSecurityCode("3444");
		validPaymentDTO.setAmount("234");

	//	orderResponse = mock(FDGGWSApiOrderResponse.class);
		//when(orderResponse.getTransactionResult()).thenReturn("APPROVED");
	//	when(webServiceTemplate.marshalSendAndReceive(any(FDGGWSApiOrderRequest.class))).thenReturn(orderResponse);
	}

	@Test(expected = IllegalStateException.class)
	public void doCardPayment_withEmptyCardNumber_fail() {
		firstDataPaymentAdapter.doCardPayment(paymentDTO);
	}

	@Test(expected = IllegalStateException.class)
	public void doCardPayment_withEmptyExpiration_fail() {
		paymentDTO.setCardNumber("235635631632317");
		firstDataPaymentAdapter.doCardPayment(paymentDTO);
	}

	@Test(expected = IllegalStateException.class)
	public void doCardPayment_withEmptySecurityCode_fail() {
		paymentDTO.setCardNumber("235635631632317");
		paymentDTO.setCardExpirationMonth("04");
		paymentDTO.setCardExpirationYear("2056");
		firstDataPaymentAdapter.doCardPayment(paymentDTO);
	}

	@Test(expected = IllegalStateException.class)
	public void doCardPayment_withEmptyAmount_fail() {
		paymentDTO.setCardNumber("235635631632317");
		paymentDTO.setCardExpirationMonth("04");
		paymentDTO.setCardExpirationYear("2056");
		paymentDTO.setCardSecurityCode("3444");
		firstDataPaymentAdapter.doCardPayment(paymentDTO);
	}

	@Test
	@Ignore
	public void doCardPayment_verifyWebserviceCall() {
		validPaymentDTO.setCardType("amex");
		firstDataPaymentAdapter.doCardPayment(validPaymentDTO);
		//verify(webServiceTemplate, times(1)).marshalSendAndReceive(any(FDGGWSApiOrderRequest.class));
	}

	@Test
	@Ignore
	public void doCardPayment_withAMEX_success() {
		validPaymentDTO.setCardType("amex");
		PaymentResponseDTO responseDTO = firstDataPaymentAdapter.doCardPayment(validPaymentDTO);
		assertNotNull(responseDTO);
		assertTrue(responseDTO.isApproved());
		assertNotNull(responseDTO.getAuthorizationAudit());
	}

	/*@Test
	public void doCardPayment_withVisa_success() {
		validPaymentDTO.setCardType("visa");
		when(orderResponse.getApprovalCode()).thenReturn("2438861242833068:YYYM:");
		PaymentResponseDTO responseDTO = firstDataPaymentAdapter.doCardPayment(validPaymentDTO);
		assertNotNull(responseDTO);
		assertTrue(responseDTO.isApproved());
		assertNotNull(responseDTO.getAuthorizationAudit());
	}

	@Test
	public void doCardPayment_approved_completeAuthorizationAudit() {
		validPaymentDTO.setCardType("visa");
		when(orderResponse.getApprovalCode()).thenReturn("2438861242833068:YYYM:");
		when(orderResponse.getTDate()).thenReturn("1372890493");

		PaymentResponseDTO responseDTO = firstDataPaymentAdapter.doCardPayment(validPaymentDTO);
		assertNotNull(responseDTO);
		assertTrue(responseDTO.isApproved());
		assertNotNull(responseDTO.getAuthorizationAudit());
		assertNotNull(responseDTO.getAuthorizationAudit().getProviderName());

		Calendar date = Calendar.getInstance();
		date.setTimeInMillis(Long.valueOf("1372890493"));
		assertEquals(responseDTO.getAuthorizationAudit().getTransactionDate(), date);
	} */
}
