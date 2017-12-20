package com.workmarket.service.business;

import com.workmarket.service.business.dto.PaymentDTO;
import com.workmarket.service.business.dto.PaymentResponseDTO;
import com.workmarket.service.infra.business.PaymentService;
import com.workmarket.test.BrokenTest;
import com.workmarket.utility.RandomUtilities;
import org.junit.Assert;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

@RunWith(SpringJUnit4ClassRunner.class)
@Category(BrokenTest.class)
@Ignore
public class PaymentServiceIT extends BaseServiceIT {

	@Autowired private PaymentService paymentService;

	@Before
	public void before() {
	}

	@After
	public void after() {
	}

	@Test
	@Transactional
	public void test_doSuccessfulCardPayment() throws Exception {

		PaymentDTO paymentDTO = new PaymentDTO();
		paymentDTO.setAmount("1.00");
		paymentDTO.setCardType("Visa");
		paymentDTO.setCardNumber(VALID_VISA_CARD);
		paymentDTO.setCardExpirationDateString("122016");
		paymentDTO.setCardSecurityCode(RandomUtilities.generateNumericString(3));
		paymentDTO.setFirstName("Roger" + RandomUtilities.generateAlphaString(8));
		paymentDTO.setLastName("Mustard" + RandomUtilities.generateAlphaString(8));
		paymentDTO.setAddress1("20 West 20th Street");
		paymentDTO.setAddress2("");
		paymentDTO.setCity("New York");
		paymentDTO.setState("NY");
		paymentDTO.setPostalCode("10001");
		paymentDTO.setCountry("US");

		PaymentResponseDTO response = paymentService.doCardPayment(paymentDTO);

		Assert.assertNotNull(response);
		Assert.assertFalse(response.isApproved()); // should fail due to 0.00 sale tx to confirm cvv2

	}


	/**
	 * Customers were complaining about discover card transactions not going through.
	 * Best we can do is test with a fake card
	 *
	 * @throws Exception
	 */
	@Test
	@Transactional
	public void test_doSuccessfulDiscoverCardPayment() throws Exception {

		PaymentDTO paymentDTO = new PaymentDTO();
		paymentDTO.setAmount("1.00");
		paymentDTO.setCardType("Discover");
		paymentDTO.setCardNumber(VALID_DISCOVER_CARD);
		paymentDTO.setCardExpirationDateString("122012");
		paymentDTO.setCardSecurityCode(RandomUtilities.generateNumericString(3));
		paymentDTO.setFirstName("Roger" + RandomUtilities.generateAlphaString(8));
		paymentDTO.setLastName("Mustard" + RandomUtilities.generateAlphaString(8));
		paymentDTO.setAddress1("20 West 20th Street");
		paymentDTO.setAddress2("");
		paymentDTO.setCity("New York");
		paymentDTO.setState("NY");
		paymentDTO.setPostalCode("10001");
		paymentDTO.setCountry("US");

		PaymentResponseDTO response = paymentService.doCardPaymentSkipAuth(paymentDTO);

		Assert.assertNotNull(response);
		Assert.assertTrue(response.isApproved());
	}

	@Test
	@Transactional
	public void test_doFailedCardPayment() throws Exception {
		PaymentDTO paymentDTO = new PaymentDTO();
		paymentDTO.setAmount("1.00");
		paymentDTO.setCardType("Visa");
		paymentDTO.setCardNumber("4987570526179222");
		paymentDTO.setCardExpirationDateString("012010"); // Expired date...
		paymentDTO.setCardSecurityCode("000");
		paymentDTO.setFirstName("Roger");
		paymentDTO.setLastName("Mustard");
		paymentDTO.setAddress1("20 West 20th Street");
		paymentDTO.setAddress2("");
		paymentDTO.setCity("New York");
		paymentDTO.setState("NY");
		paymentDTO.setPostalCode("10001");
		paymentDTO.setCountry("US");

		PaymentResponseDTO response = paymentService.doCardPayment(paymentDTO);

		Assert.assertNotNull(response);
		Assert.assertFalse(response.isApproved());
	}

	@Test(expected = IllegalStateException.class)
	@Transactional
	public void test_validateEmptyFields() throws Exception {
		PaymentDTO paymentDTO = new PaymentDTO();
		paymentDTO.setAmount("1.00");
		paymentDTO.setCardType("Visa");
		paymentDTO.setCardExpirationDateString("012010"); // Expired date...
		paymentDTO.setFirstName("Roger");
		paymentDTO.setLastName("Mustard");
		paymentDTO.setAddress1("20 West 20th Street");
		paymentDTO.setAddress2("");
		paymentDTO.setCity("New York");
		paymentDTO.setState("NY");
		paymentDTO.setPostalCode("10001");
		paymentDTO.setCountry("US");

		paymentService.doCardPayment(paymentDTO);

	}

	public PaymentService getPaymentService() {
		return paymentService;
	}

	public void setPaymentService(PaymentService paymentService) {
		this.paymentService = paymentService;
	}
}
