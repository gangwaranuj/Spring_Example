/**
 *
 */
package com.workmarket.service.business.accountregister;

import com.workmarket.domains.model.User;
import com.workmarket.domains.model.account.AccountRegister;
import com.workmarket.domains.model.banking.AbstractBankAccount;
import com.workmarket.domains.model.banking.BankAccount;
import com.workmarket.service.exception.account.AccountRegisterConcurrentException;
import com.workmarket.service.exception.account.InvalidAccountRegisterException;
import com.workmarket.service.exception.account.InvalidBankAccountException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component(value = "payPalAccountTransaction")
@Scope(value = "prototype")
public class PayPalAccountTransaction extends BankTransaction {

	private static final Log logger = LogFactory.getLog(PayPalAccountTransaction.class);

	public PayPalAccountTransaction() {
		super();
	}

	@Override
	public com.workmarket.domains.model.account.BankAccountTransaction executeAddFundsToRegisterFromAch(User user, AbstractBankAccount bankAccount, String amount, AccountRegister accountRegister) throws AccountRegisterConcurrentException, InvalidAccountRegisterException, InvalidBankAccountException {

		throw new InvalidBankAccountException("Can't add funds from Pay Pal");

	}

	@Override
	public com.workmarket.domains.model.account.BankAccountTransaction executeAddAchVerify(BankAccount bankAccount, AccountRegister accountRegister, BigDecimal achVerifyAmount) throws AccountRegisterConcurrentException {

		throw new UnsupportedOperationException();
	}
}
