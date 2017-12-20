package com.workmarket.common.template.pdf;

import com.google.common.collect.Lists;
import com.workmarket.domains.model.account.CreditCardTransaction;
import com.workmarket.domains.model.account.RegisterTransaction;
import com.workmarket.utility.CollectionUtilities;
import com.workmarket.utility.DateUtilities;
import com.workmarket.utility.NumberUtilities;
import com.workmarket.utility.StringUtilities;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public class CreditCardReceiptPDFTemplate extends PDFTemplate {
	private static final long serialVersionUID = 1L;

	private CreditCardTransaction creditCardTransaction;

	public CreditCardReceiptPDFTemplate(CreditCardTransaction creditCardTransaction) {
		super();
		this.creditCardTransaction = creditCardTransaction;
		if (creditCardTransaction != null) {
			setOutputFileName("receipt_" + creditCardTransaction.getId());
		}
	}
	
	public CreditCardTransaction getCreditCardTransaction() {
		return creditCardTransaction;
	}

	public String getDate() {
		return formatDate(creditCardTransaction);
	}

	public String getFullName() {
		return StringUtilities.fullName(creditCardTransaction.getFirstName(), creditCardTransaction.getLastName());
	}

	public List<Map<String, Object>> getRows() {
		return formatRows(creditCardTransaction);
	}

	public String getTotal() {
		return NumberUtilities.currency(creditCardTransaction.getAmount());
	}

	/*
		Static Methods
	 */
	public static String formatDate(CreditCardTransaction creditCardTransaction) {
		return DateUtilities.format("MM/dd/YY", creditCardTransaction.getTransactionDate());
	}

	public static List<Map<String, Object>> formatRows(CreditCardTransaction creditCardTransaction) {
		RegisterTransaction feeTransaction = creditCardTransaction.getFeeTransaction();

		BigDecimal totalCharged = creditCardTransaction.getAmount();
		BigDecimal fee = (feeTransaction != null) ? feeTransaction.getAmount().abs() : BigDecimal.ZERO;
		BigDecimal moneyAdded = totalCharged.subtract(fee);

		List<Map<String, Object>> rows = Lists.newArrayList(
				// amount added to account
				CollectionUtilities.newObjectMap(
						"description", "Funds added to Work Market account",
						"amount", NumberUtilities.currency(moneyAdded)
				)
		);

		if (feeTransaction != null) {
			rows.add(
				// fee charged
				CollectionUtilities.newObjectMap(
					"description", "Merchant fee",
					"amount", NumberUtilities.currency(fee)
				)
			);
		}

		return rows;
	}
}
