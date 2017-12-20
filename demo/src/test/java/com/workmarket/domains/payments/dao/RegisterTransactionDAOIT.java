package com.workmarket.domains.payments.dao;

import static org.junit.Assert.assertEquals;

import com.workmarket.domains.model.Company;
import com.workmarket.domains.model.account.RegisterTransactionType;
import com.workmarket.service.business.BaseServiceIT;
import com.workmarket.test.IntegrationTest;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@RunWith(SpringJUnit4ClassRunner.class)
@Category(IntegrationTest.class)
@TransactionConfiguration
@Transactional
public class RegisterTransactionDAOIT extends BaseServiceIT {

	@Autowired
	RegisterTransactionDAO registerTransactionDAO;

	@Qualifier("jdbcTemplate")
	@Autowired
	private NamedParameterJdbcTemplate jdbcTemplate;

	/**
	 * This test is used to just check the validity of the SQL, unfortunately there is no fixed
	 * data I can use to test this and the level of effort to create a test baseline would
	 * probably take a day to complete (there are simply too many foreign keys)
	 */
	@Test
	public void findIncrementalSubscriptionTransactionPendingInvoiceForPaymentPeriod_no_pending_returns_0() {
		BigDecimal softwareFees = registerTransactionDAO.calculateIncrementalSubscriptionTransactionPendingInvoiceForPaymentPeriod(1l, 1l, RegisterTransactionType.SUBSCRIPTION_SOFTWARE_FEE_PAYMENT);
		BigDecimal vorFees = registerTransactionDAO.calculateIncrementalSubscriptionTransactionPendingInvoiceForPaymentPeriod(1l, 1l, RegisterTransactionType.SUBSCRIPTION_VENDOR_OF_RECORD_PAYMENT);

		BigDecimal zero = BigDecimal.ZERO.setScale(2);
		assertEquals(zero, softwareFees);
		assertEquals(zero, vorFees);
	}

}
