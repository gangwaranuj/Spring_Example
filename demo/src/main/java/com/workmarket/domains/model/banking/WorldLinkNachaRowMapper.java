package com.workmarket.domains.model.banking;

import com.workmarket.domains.model.account.BankAccountTransaction;
import com.workmarket.domains.model.account.RegisterTransactionType;
import com.workmarket.factory.BankingIntegrationGenerationRequestRowMapper;
import com.workmarket.utility.NumberUtilities;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.util.Assert;

import java.math.BigDecimal;

import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

public class WorldLinkNachaRowMapper implements BankingIntegrationGenerationRequestRowMapper {

	private static final Log logger = LogFactory.getLog(WorldLinkNachaRowMapper.class);
	private static final String COMPANY_NAME_DEBIT_ACCOUNT = "WORK MARKET INC";
	private static final String[] PROHIBITED_CHARACTERS = new String[]{"!", "“", "#", "$", "%", "&",
		"*", ";", "<", "=", ">", "@", "[", "\"", "]", "^", "_", "`", "{", "}", "|", "~", "£"};

	private String debitAccount;

	public WorldLinkNachaRowMapper(String debitAccount) {
		this.debitAccount = debitAccount;
	}

	@Override
	public String[] mapRow(BankAccountTransaction transaction, String bankAccountNumber) {
		Assert.notNull(transaction);

		if (!isValidTransaction(transaction, bankAccountNumber)) {
			return new String[0];
		}

		String[] response = new String[91];

 		//Country code - Must contain "WL"
		response[1] = "WL";
		//Payment method - Must contain "ACH"
		response[2] = "ACH";

		//3 character ISO code for currency
		response[8] = transaction.getBankAccount().getCountry().getCcy();

		/*
			Debit Account
			The account number from which the transaction will be funded.
			Length = 34
		 */
		//J3N will provide the number
		response[11] = debitAccount;

		/*
			Equivalent Amount
			Amount of the payment in funding currency terms.
			Must contain only numbers and/or the decimal point. No commas or negative amounts.
			Payment or Equivalent Amount must be populated but not both.

		 */
		BigDecimal amount = NumberUtilities.defaultValue(transaction.getAmount(), BigDecimal.ZERO);
		//If the transaction is a remove funds, it's stored as a negative value in the DB
		if (RegisterTransactionType.REMOVE_FUNDS.equals(transaction.getRegisterTransactionType().getCode())) {
			amount = amount.negate();
		}
		response[19] = amount.toString();

		/*
            Transaction Type
			Specifies the type of payment being made (i.e. salary, pension, vendor).
			Please refer to ACH user guide for list of appropriate transaction type codes for each country.

			450 – Miscellaneous Payment
		 */
		response[29] = "450";
		/*
			Company Name (Individual Company ID)
			The entity responsible for the transaction. (Company or Subsidiary)
			Length = 35
		 */
		response[31] = COMPANY_NAME_DEBIT_ACCOUNT;

		//Pre Note
		response[32] = "N";

		/*
			Beneficiary Account or Other ID.
			Beneficiary’s Bank Account Number.
			Length = 34
		 */
		response[43] = sanitizeAndTruncate(bankAccountNumber, 34);

		/*
			Beneficiary Name
			Length = 35
		 */

		response[44] = sanitizeAndTruncate((transaction.getBankAccount()).getNameOnAccount(), 35);

		/*
			Beneficiary Bank Routing Code
			Length = 12
		 */
		response[50] = sanitizeAndTruncate(((BankAccount) transaction.getBankAccount()).getRoutingNumber(), 12);
		/*
			Beneficiary Bank Name
			Length = 35
		 */
		response[54] = sanitizeAndTruncate((transaction.getBankAccount()).getBankName(), 35);
		return fillEmptyCellsWithDelimiter(response);
	}

	String[] fillEmptyCellsWithDelimiter(String[] line) {
		if (line != null) {
			for (int i = 0; i < line.length; i++) {
				if (isBlank(line[i])) {
					line[i] = StringUtils.EMPTY;
				}
			}
		}
		logger.info(line);
		return line;
	}

	String sanitizeAndTruncate(String content, int length) {
		if (isNotBlank(content)) {
			for (String character : PROHIBITED_CHARACTERS) {
				content = content.replace(character, "");
			}
			return StringUtils.left(content.trim(), length);
		}
		return "";
	}

	boolean isValidTransaction(BankAccountTransaction transaction, String bankAccountNumber) {
		return transaction.getBankAccount() != null &&
			transaction.getBankAccount().getCountry() != null &&
			isNotBlank(transaction.getBankAccount().getCountry().getCcy()) &&
			isNotBlank(transaction.getBankAccount().getNameOnAccount()) &&
			isNotBlank(bankAccountNumber) &&
			isNotBlank(transaction.getBankAccount().getBankName());
	}
}
