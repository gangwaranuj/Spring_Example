package com.workmarket.service.business.subscriptions;

import com.workmarket.domains.model.invoice.item.InvoiceLineItem;
import com.workmarket.domains.model.invoice.item.InvoiceLineItemType;
import com.workmarket.service.business.account.SubscriptionInvoiceLineItemFactoryImpl;
import com.workmarket.service.business.dto.invoice.InvoiceLineItemDTO;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.runners.MockitoJUnitRunner;

import java.math.BigDecimal;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * Author: rocio
 */
@RunWith(MockitoJUnitRunner.class)
public class SubscriptionInvoiceLineItemFactoryImplTest {


	@InjectMocks SubscriptionInvoiceLineItemFactoryImpl subscriptionInvoiceLineItemFactory;

	@Before
	public void setUp() throws Exception {

	}

	@Test(expected = IllegalArgumentException.class)
	public void newInvoiceLineItem_withNullInvoiceLineItemDTO_fails() {
		subscriptionInvoiceLineItemFactory.newInvoiceLineItem(null);
	}

	@Test(expected = IllegalArgumentException.class)
	public void newInvoiceLineItem_withEmptyInvoiceLineItemDTO_fails() {
		subscriptionInvoiceLineItemFactory.newInvoiceLineItem(new InvoiceLineItemDTO());
	}

	@Test
	public void newInvoiceLineItem_success() {
		InvoiceLineItem invoiceLineItem = subscriptionInvoiceLineItemFactory.newInvoiceLineItem(new InvoiceLineItemDTO().setInvoiceLineItemType(InvoiceLineItemType.DEPOSIT_RETURN_FEE));
		assertNotNull(invoiceLineItem);
		assertEquals(invoiceLineItem.getAmount(), BigDecimal.ZERO);
	}
	@Test
	public void newInvoiceLineItem_withAmount_success() {
		InvoiceLineItem invoiceLineItem = subscriptionInvoiceLineItemFactory.newInvoiceLineItem(new InvoiceLineItemDTO().setAmount(BigDecimal.TEN).setInvoiceLineItemType(InvoiceLineItemType.DEPOSIT_RETURN_FEE));
		assertNotNull(invoiceLineItem);
		assertEquals(invoiceLineItem.getAmount(), BigDecimal.TEN);
	}
}
