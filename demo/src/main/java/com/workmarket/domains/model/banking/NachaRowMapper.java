package com.workmarket.domains.model.banking;

import com.workmarket.factory.BankingIntegrationGenerationRequestRowMapper;
import com.workmarket.domains.model.account.BankAccountTransaction;
import com.workmarket.domains.model.account.RegisterTransactionType;
import com.workmarket.utility.NumberUtilities;
import com.workmarket.utility.StringUtilities;

import java.math.BigDecimal;

public class NachaRowMapper implements BankingIntegrationGenerationRequestRowMapper {

	@Override
	public String[] mapRow(BankAccountTransaction tx, String bankAccountNumber) {
		String[] row;

		if (!tx.getRegisterTransactionType().getCode().equals(RegisterTransactionType.REMOVE_FUNDS)) {
			row = new String[7];
		} else {
			row = new String[6];
		}

		BigDecimal amount = NumberUtilities.defaultValue(tx.getAmount(), BigDecimal.ZERO);
		//If the transaction is a remove funds, it's stored as a negative value in the DB
		if (tx.getRegisterTransactionType().getCode().equals(RegisterTransactionType.REMOVE_FUNDS)) {
			amount = amount.negate();
		}
		row[0] = amount.toString();

		row[1] = StringUtilities.truncate(tx.getBankAccount().getNameOnAccount(), 22);
		row[2] = tx.getAccountRegister().getCompany().getId().toString();
		row[3] = padRoutingNumber(((BankAccount) tx.getBankAccount()).getRoutingNumber());
		row[4] = bankAccountNumber;

		if (tx.getBankAccount().getBankAccountType().isCheckingAccount()) {
			row[5] = "C";
		} else {
			row[5] = "S";
		}

		// if the transaction is a withdrawal, it won't have a description field
		if (!tx.getRegisterTransactionType().getCode().equals(RegisterTransactionType.REMOVE_FUNDS)) {

			if (tx.getRegisterTransactionType().getCode().equals(RegisterTransactionType.ACH_VERIFY)) {
				row[6] = "WM Acct Verify";
			} else {
				// must be add fund tx
				row[6] = "WM Payments";
			}
		}
		return row;
	}

	private String padRoutingNumber(String routingNumber) {

		if (routingNumber.length() < 9) {
			StringBuffer buf = new StringBuffer();
			for (int i = 0; i < 9; i++) {
				buf.append("0");
			}
			return buf.append(routingNumber).toString();
		}

		return routingNumber;
	}
}
