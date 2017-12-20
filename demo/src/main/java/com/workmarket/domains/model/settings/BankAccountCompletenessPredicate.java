package com.workmarket.domains.model.settings;

import com.workmarket.domains.model.banking.AbstractBankAccount;
import com.workmarket.domains.model.banking.BankAccount;
import com.workmarket.utility.CollectionUtilities;

import java.util.List;

public class BankAccountCompletenessPredicate implements CompletenessPredicate<List<? extends AbstractBankAccount>> {

	@Override
	public boolean test(final List<? extends AbstractBankAccount> accounts) {
		Boolean hasActiveBankAccount = Boolean.FALSE;
		if (accounts != null && !CollectionUtilities.isEmpty(accounts)) {
			for (AbstractBankAccount account : accounts) {
				if (account instanceof BankAccount) {
					if (account.getActiveFlag()) {
						hasActiveBankAccount = Boolean.TRUE;
					}
				}
			}
		}
		return hasActiveBankAccount;
	}
}
