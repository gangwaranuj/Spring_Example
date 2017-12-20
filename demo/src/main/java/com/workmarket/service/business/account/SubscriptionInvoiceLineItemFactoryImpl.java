package com.workmarket.service.business.account;

import com.google.common.collect.Lists;
import com.workmarket.domains.model.account.RegisterTransactionType;
import com.workmarket.domains.model.account.ServiceTransaction;
import com.workmarket.domains.model.invoice.item.*;
import com.workmarket.service.business.dto.account.pricing.subscription.SubscriptionPaymentDTO;
import com.workmarket.service.business.dto.invoice.InvoiceLineItemDTO;
import com.workmarket.utility.BeanUtilities;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.util.Calendar;
import java.util.Collections;
import java.util.List;

/**
 * Author: rocio
 */
@Service
public class SubscriptionInvoiceLineItemFactoryImpl implements InvoiceLineItemFactory {

	@Override
	public InvoiceLineItem newInvoiceLineItem(InvoiceLineItemDTO invoiceLineItemDTO) {
		Assert.notNull(invoiceLineItemDTO);
		Assert.notNull(invoiceLineItemDTO.getInvoiceLineItemType());
		InvoiceLineItem invoiceLineItem;

		switch (invoiceLineItemDTO.getInvoiceLineItemType()) {
			case SUBSCRIPTION_SOFTWARE_FEE:
				invoiceLineItem = new SubscriptionSoftwareFeeLineItem();
				break;
			case SUBSCRIPTION_SOFTWARE_FEE_VOR:
				invoiceLineItem = new SubscriptionVorSoftwareFeeLineItem();
				break;
			case SUBSCRIPTION_ADD_ON:
				invoiceLineItem = new SubscriptionAddOnLineItem();
				break;
			case SUBSCRIPTION_VOR:
				invoiceLineItem = new SubscriptionVORLineItem();
				break;
			case SUBSCRIPTION_SETUP_FEE:
				invoiceLineItem = new SubscriptionSetupFeeLineItem();
				break;
			case SUBSCRIPTION_DISCOUNT:
				invoiceLineItem = new SubscriptionDiscountLineItem();
				break;
			case DEPOSIT_RETURN_FEE:
				invoiceLineItem = new DepositReturnFeeInvoiceLineItem();
				break;
			case LATE_PAYMENT_FEE:
				invoiceLineItem = new LatePaymentFeeInvoiceLineItem();
				break;
			case MISC_FEE:
				invoiceLineItem = new MiscFeeInvoiceLineItem();
				break;
			case WITHDRAWAL_RETURN_FEE:
				invoiceLineItem = new WithdrawalReturnFeeInvoiceLineItem();
				break;
			default://BASE
				invoiceLineItem = new InvoiceLineItem();
		}

		Assert.notNull(invoiceLineItemDTO.getAmount());
		BeanUtilities.copyProperties(invoiceLineItem, invoiceLineItemDTO);
		if (StringUtils.isBlank(invoiceLineItemDTO.getDescription())) {
			invoiceLineItem.setDescription(invoiceLineItemDTO.getInvoiceLineItemType().getDescription());
		}
		return invoiceLineItem;
	}

	@Override
	public InvoiceLineItem newSubscriptionInvoiceLineItem(ServiceTransaction serviceTransaction) {
		Assert.notNull(serviceTransaction);
		Assert.notNull(serviceTransaction.getRegisterTransactionType());

		InvoiceLineItem invoiceLineItem;
		String registerTransactionCode = serviceTransaction.getRegisterTransactionType().getCode();
		if (RegisterTransactionType.SUBSCRIPTION_SOFTWARE_FEE_PAYMENT.equals(registerTransactionCode)) {
			if (serviceTransaction.isSubscriptionVendorOfRecord()) {
				invoiceLineItem = new SubscriptionVorSoftwareFeeLineItem();
			} else {
				invoiceLineItem = new SubscriptionSoftwareFeeLineItem();
			}
		} else if (RegisterTransactionType.SUBSCRIPTION_VENDOR_OF_RECORD_PAYMENT.equals(registerTransactionCode)) {
			invoiceLineItem = new SubscriptionVORLineItem();
		} else if (RegisterTransactionType.SUBSCRIPTION_SETUP_FEE_PAYMENT.equals(registerTransactionCode)) {
			invoiceLineItem = new SubscriptionSetupFeeLineItem();
		} else if (RegisterTransactionType.SUBSCRIPTION_DISCOUNT.equals(registerTransactionCode)) {
			invoiceLineItem = new SubscriptionDiscountLineItem();
		} else if (RegisterTransactionType.SUBSCRIPTION_ADD_ON_PAYMENT.equals(registerTransactionCode)) {
			invoiceLineItem = new SubscriptionAddOnLineItem();
		} else {
			throw new UnsupportedOperationException("Invalid SubscriptionTransaction " + serviceTransaction.getId());
		}
		invoiceLineItem.setAmount(serviceTransaction.getAmount().negate());
		invoiceLineItem.setDescription(serviceTransaction.getRegisterTransactionType().getDescription());
		invoiceLineItem.setRegisterTransaction(serviceTransaction);
		invoiceLineItem.setTransactionDate((Calendar) serviceTransaction.getTransactionDate().clone());
		return invoiceLineItem;
	}

	@Override
	public List<InvoiceLineItemDTO> newSubscriptionInvoiceLineItemDTOList(SubscriptionPaymentDTO subscriptionPaymentDTO) {
		Assert.notNull(subscriptionPaymentDTO);
		if (!subscriptionPaymentDTO.hasValue()) {
			return Collections.emptyList();
		}

		List<InvoiceLineItemDTO> lineItemDTOList = Lists.newArrayList();
		if (subscriptionPaymentDTO.hasDiscount()) {
			lineItemDTOList.add(new InvoiceLineItemDTO(InvoiceLineItemType.SUBSCRIPTION_DISCOUNT)
					.setTransactionDate(subscriptionPaymentDTO.getDueDate())
					.setAmount(subscriptionPaymentDTO.getDiscount()));
		}
		if (subscriptionPaymentDTO.hasAddOnsFee()) {
			lineItemDTOList.add(new InvoiceLineItemDTO(InvoiceLineItemType.SUBSCRIPTION_ADD_ON)
					.setTransactionDate(subscriptionPaymentDTO.getDueDate())
					.setAmount(subscriptionPaymentDTO.getAddOnsAmount()));
		}
		if (subscriptionPaymentDTO.hasSetupFee()) {
			lineItemDTOList.add(new InvoiceLineItemDTO(InvoiceLineItemType.SUBSCRIPTION_SETUP_FEE)
					.setTransactionDate(subscriptionPaymentDTO.getDueDate())
					.setAmount(subscriptionPaymentDTO.getSetupFee()));
		}
		if (subscriptionPaymentDTO.hasVorFee()) {
			lineItemDTOList.add(new InvoiceLineItemDTO(InvoiceLineItemType.SUBSCRIPTION_VOR)
					.setTransactionDate(subscriptionPaymentDTO.getDueDate())
					.setAmount(subscriptionPaymentDTO.getVorFeeAmount()));
		}
		if (subscriptionPaymentDTO.hasSoftwareFee()) {
			if (subscriptionPaymentDTO.isVendorOfRecord()) {
				lineItemDTOList.add(new InvoiceLineItemDTO(InvoiceLineItemType.SUBSCRIPTION_SOFTWARE_FEE_VOR)
						.setTransactionDate(subscriptionPaymentDTO.getDueDate())
						.setAmount(subscriptionPaymentDTO.getSoftwareFeeAmount()));
			} else {
				lineItemDTOList.add(new InvoiceLineItemDTO(InvoiceLineItemType.SUBSCRIPTION_SOFTWARE_FEE)
					.setTransactionDate(subscriptionPaymentDTO.getDueDate())
					.setAmount(subscriptionPaymentDTO.getSoftwareFeeAmount()));
			}
		}
		return lineItemDTOList;
	}
}
