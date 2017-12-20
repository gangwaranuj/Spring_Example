package com.workmarket.domains.payments.service;

import com.google.api.client.util.Lists;
import com.workmarket.domains.model.postalcode.Country;
import com.workmarket.domains.payments.model.BankAccountDTO;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by ianha on 10/10/14
 */
@Service
public class BankAccountDTOValidatorImpl implements BankAccountDTOValidator {
	@Override
	public List<String> validate(BankAccountDTO bankAccountDTO) {
		List<String> errorsPropertyCodes = Lists.newArrayList();

		if (Country.USA.equals(bankAccountDTO.getCountry())) {
			if ("NONUSTAXID".equals(bankAccountDTO.getGovIdType())) {
				errorsPropertyCodes.add("funds.accounts.create.gcc.invalid_gov_id_type");
			}
		} else if (!"NONUSTAXID".equals(bankAccountDTO.getGovIdType())) {
			errorsPropertyCodes.add("funds.accounts.create.gcc.invalid_gov_id_type");
		}

		return errorsPropertyCodes;
	}
}
