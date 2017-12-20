package com.workmarket.domains.payments.service;

import com.workmarket.domains.payments.model.BankAccountDTO;

import java.util.List;

/**
 * Created by ianha on 10/10/14
 */
public interface BankAccountDTOValidator {
	List<String> validate(BankAccountDTO bankAccountDTO);
}
