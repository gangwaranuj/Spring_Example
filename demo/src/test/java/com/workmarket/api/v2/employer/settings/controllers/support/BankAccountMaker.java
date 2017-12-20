package com.workmarket.api.v2.employer.settings.controllers.support;


import com.natpryce.makeiteasy.Instantiator;
import com.natpryce.makeiteasy.Property;
import com.natpryce.makeiteasy.PropertyLookup;
import com.workmarket.api.v2.employer.settings.models.ACHBankAccountDTO;
import com.workmarket.domains.model.banking.AbstractBankAccount;
import com.workmarket.domains.model.banking.BankAccountType;
import com.workmarket.domains.model.postalcode.Country;

import static com.natpryce.makeiteasy.Property.newProperty;

public class BankAccountMaker {

	public static final Property<ACHBankAccountDTO, String> bankName = newProperty();
	public static final Property<ACHBankAccountDTO, String> nameOnAccount = newProperty();
	public static final Property<ACHBankAccountDTO, String> routingNumber = newProperty();
	public static final Property<ACHBankAccountDTO, String> accountNumber = newProperty();
	public static final Property<ACHBankAccountDTO, String> accountNumberConfirm = newProperty();
	public static final Property<ACHBankAccountDTO, String> bankAccountTypeCode = newProperty();
	public static final Property<ACHBankAccountDTO, String> type = newProperty();

	public static final Instantiator<ACHBankAccountDTO> ACHBankAccountDTO = new Instantiator<ACHBankAccountDTO>() {
		@Override
		public ACHBankAccountDTO instantiate(PropertyLookup<ACHBankAccountDTO> lookup) {

			return new ACHBankAccountDTO.Builder()
				.setBankName(lookup.valueOf(bankName, "Bank of America"))
				.setNameOnAccount(lookup.valueOf(nameOnAccount, "ABC"))
				.setRoutingNumber(lookup.valueOf(routingNumber, "011000138"))
				.setAccountNumber(lookup.valueOf(accountNumber, "0123456789"))
				.setAccountNumberConfirm(lookup.valueOf(accountNumberConfirm, "0123456789"))
				.setBankAccountTypeCode(lookup.valueOf(bankAccountTypeCode, BankAccountType.CHECKING))
				.setType(AbstractBankAccount.ACH)
				.setCountry(Country.USA_COUNTRY.getId())
				.build();
		}
	};
}
